/*
 * DefaultSearchableDatabase.java
 *
 * Created on February 9, 2005, 2:31 PM
 */

package servlets.advancedSearch;

/**
 *
 * @author khoran
 */

import java.util.*;
import servlets.Common;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import org.apache.log4j.Logger;
import javax.servlet.http.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import java.io.*;
import servlets.dataViews.*;
import servlets.advancedSearch.queryTree.*;
import servlets.advancedSearch.visitors.*;

public class DefaultSearchableDatabase implements SearchableDatabase
{
    final static int rpp=25;
    
    /////  These values should be defined in the defineOptions method
    Field[] fields;
    String[] operators;
    String[] booleans; 
    
    String rootTableName,primaryKey,defaultColumn; 
    int unaryBoundry; //seperates unary and binary ops in operators array        
    ///////////////////////////
    
    static DbConnection dbc=null;  //need a connection to a different database
    static Logger log=Logger.getLogger(DefaultSearchableDatabase.class);
    static SearchTreeManager stm;
    
    private int sp,ep,index;
    
    /** Creates a new instance of DefaultSearchableDatabase */
    public DefaultSearchableDatabase(DbConnection dbc,String filename)
    {
        log.debug("createing new DefauleSearchableDatabase");
        this.dbc=dbc;
        stm=new SearchTreeManager(filename);
        defineOptions();
    }
    
    public servlets.advancedSearch.queryTree.Query buildQueryTree(SearchState state)
    {
        log.debug("building query tree");
        
        if(state.getSelectedFields().size()==0)
            return buildInitialTree();
        
        List fields=new LinkedList();
        Set tables=new HashSet();
        Integer limit;
        DbField orderField;
        Order order;
        Expression condition;
        Query query;

                 //make sure we have a valid sort field
        if(getFields()[state.getSortField()].dbName.length()==0)
            state.setSortField(0);
        
        limit=Integer.valueOf(state.getLimit());
        
        orderField=new DbField(getFields()[state.getSortField()].dbName, String.class);
        order=new Order(orderField,"ASC");
        
        fields.add(rootTableName+"."+primaryKey);
        fields.add(orderField.getName());
        
        //add tables to from clause and create condition
        condition=buildCondition(state,tables);
        
        query=new Query(condition,fields,new LinkedList(tables),order,limit);
        log.debug("query="+query);
        return query;
    }

    public void displayResults(SearchState state, javax.servlet.ServletContext context, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
    {
        List results;
        try{
            Query queryTree=buildQueryTree(state);
            SqlVisitor sv=new SqlVisitor();            
            String sql=sv.getSql(queryTree);
            log.debug("sql="+sql);
            results=dbc.sendQuery(sql);
       }catch(Exception e){
            log.error("could not send query: "+e.getMessage());            
            e.printStackTrace();
            return;
        }        
        ServletRequest sr=getNewRequest(state,request,results);        
        
        try{            
            context.getRequestDispatcher("/QueryPageServlet").forward(sr, response);            
        }catch(Exception e){
            log.error("could not forward to QueryPageServlet: "+e.getMessage());
            e.printStackTrace();
        }
    }
    protected ServletRequest getNewRequest(SearchState state,HttpServletRequest request,List results)
    { //this can be overridden by sub classes to send different parameters
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);  
        mRequest.getParameterMap().put("rpp",new Integer(rpp).toString());
                
        mRequest.getParameterMap().put("displayType","unknowns2View");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        return mRequest;
    }

    
    
    ////////////////////////  Accessors  ///////////////////////////////////////
    public String[] getBooleans()
    {
        return booleans;
    }
    public String getBoolean(int i)
    {
        return booleans[i];
    }
    public void setBoolean(int i,String s)
    {
        booleans[i]=s;
    }

    public Field[] getFields()
    {
        return fields;
    }
    public Field getField(int i)
    {
        return fields[i];
    }
    public void setField(int i,Field f)
    {
        fields[i]=f;
    }

    public String[] getOperators()
    {
        return operators;
    }
    public String getOperator(int i)
    {
        return operators[i];
    }
    public void setOperator(int i,String op)
    {
        operators[i]=op;
    }

    public SearchTreeManager getSearchManager()
    {
        return stm;
    }
/////////////////////////////////////////////////////////////////////////
//              Tree building methods    
/////////////////////////////////////////////////////////////////////////
    
    /**method for building trees. 
     * Tables should be an empty set to which the from tables are added.
     * 
     */
    private Expression buildCondition(SearchState state,Set tables)
    {
        ExpressionSet expSet=null;
        Expression condition;
        
        sp=0;  
        ep=0;
        index=0;
        expSet=buildExpression(state,tables);
        
        //make sure we add the table we're sorting by.
        Expression j=expSet.getJoins();
        j=updateJoin(j,getTableName(getFields()[state.getSortField()].dbName),tables);                    
        
        //add any user joins.
        for(Iterator i=additionalJoins().iterator();i.hasNext();)
            j=updateJoin(j,(String)i.next(),tables);
        expSet.setJoins(j);    
        
        if(expSet.getJoins()==null)
            condition= expSet.getRestrictedValues();
        else if(expSet.getRestrictedValues()==null) //this should never happen
        {
            log.warn("no restrictedValue conditions");
            condition=expSet.getJoins();
        }
        else
            condition=new Operation("and",expSet.getJoins(),expSet.getRestrictedValues());        
        
        if(condition==null)
            condition=new Operation("=",new DbField(rootTableName+"."+defaultColumn,String.class),
                    new StringLiteralValue(""));
        return condition;
    }
    /**
      this method exists for subclasses to override so they can
      easily add additional tables to select from.
    */
    protected List additionalJoins()
    { 
        return new ArrayList(0);
    }
    private ExpressionSet buildExpression(SearchState state,Set tables)
    {
        int fieldCount=state.getSelectedFields().size();
        int fid,oid,bid; //field,operator, and boolean ids        
        String value,tableName;
        Expression joins=null,restrictedValues=null;
        
        log.debug("starting buildExpression, sp="+sp+", ep="+ep+", index="+index);
        for(;index<fieldCount;index++)
        {
            fid=state.getSelectedField(index).intValue();
            oid=state.getSelectedOp(index).intValue();
            value=state.getValue(index);
            if(index>0)
                bid=state.getSelectedBool(index-1).intValue();
            else 
                bid=-1; //should not be used on first iteration anyway.            
            if(oid >= getFields()[fid].getValidOps().length)
                oid=0;
            
            //log.debug("fid="+fid+", oid="+oid+", value="+value+"\n\tbid="+bid);            
            tableName=getTableName(getFields()[fid].dbName);
            if(tableName.equals(""))
            {
                log.debug("reseting title field to first field");                
                state.getSelectedFields().set(index,new Integer(0));
                fid=0;
                tableName=getTableName(getFields()[fid].dbName);
            }
            
            //add join conditions
            joins=updateJoin(joins,tableName,tables);            
                        
            if(sp < state.getStartParinths().size() && state.getStartParinth(sp).intValue()==index){
                log.debug("found open parinth");
                sp++;
                //start building expression from i
                log.debug("starting sub espression, index="+index);
                ExpressionSet es=buildExpression(state,tables);
                log.debug("back from sub expression, index="+index);
                                
                if(es.getJoins()!=null)
                {//update joins
                    if(joins==null)
                        joins=es.getJoins();
                    else
                        joins=new Operation("and",es.getJoins(),joins);
                }
                if(es.getRestrictedValues()!=null)
                {//update restricted values
                    if(restrictedValues==null)
                        restrictedValues=es.getRestrictedValues();
                    else    
                        restrictedValues=new Operation(getBooleans()[bid],restrictedValues,es.getRestrictedValues());
                }                
                continue;               
            }            
            
            //add regular condition
            Operation op;
            DbField field=new DbField(getFields()[fid].dbName,getFields()[fid].type);
            
//            if(isUnaryOp(oid))
//                op=new Operation(getOperators()[oid],field,Operation.LEFT);
//            else
//            {
                LiteralValue lv=getLiteralValue(getFields()[fid],value);
                op=new Operation(getFields()[fid].getValidOps()[oid],field,lv);
  //              op=new Operation(getOperators()[oid],field,lv);
//            }                        
            
            //log.debug("new expression is :"+op);
            if(restrictedValues==null)
                restrictedValues=op;
            else
                restrictedValues=new Operation(getBooleans()[bid],restrictedValues,op);            
            
            if(ep < state.getEndParinths().size() && state.getEndParinth(ep).intValue()==index){
                ep++;
                log.debug("found end parinth, index="+index);
                //break loop and return current expression
                break;                
            }
        }
        return new ExpressionSet(joins,restrictedValues);
    }
    private Expression updateJoin(Expression currentJoins,String tableName,Set tables)
    {
        log.debug("updateding join, tablename="+tableName+", existing tables:"+tables);
        DbField root_key=new DbField(rootTableName+"."+primaryKey,Integer.class);
        Expression newJoins=currentJoins;
        //see if we have already added this table
        if(!tables.contains(tableName))
        { //then add table to set, and add join condition
            tables.add(tableName);
            if(!tableName.equals(rootTableName))
            {//no need to join root table to itself.
                Operation op;
                DbField table_key=new DbField(tableName+"."+primaryKey,Integer.class);                

                op=new Operation("=",root_key,table_key);
                if(currentJoins==null)
                    newJoins=op;
                else
                    newJoins=new Operation("and",op,currentJoins);
            }
        }
        return newJoins;
    }
    private LiteralValue getLiteralValue(Field f,String v)
    {
        if(f.type==String.class)
            return new StringLiteralValue(v);
        else if(f.type==Integer.class)
            try{
                return new IntLiteralValue(Integer.valueOf(v));
            }catch(Exception e){
                return new IntLiteralValue(0);
            }            
        else if(f.type==Float.class)
            try{
                return new FloatLiteralValue(Float.valueOf(v));
            }catch(Exception e){
                return new FloatLiteralValue(0);
            }
        else if(f.type==Boolean.class)                  
            return new BooleanLiteralValue(Common.getBoolean(v));
        else if(f.type==List.class)
        {
            List l=new LinkedList();
            StringTokenizer tok=new StringTokenizer(v);
            while(tok.hasMoreTokens())
                l.add(tok.nextToken()); 
            return new ListLiteralValue(l);
        }            
        else
            log.error("unknown type: "+f.type.getName());
        return null;
    }
    private String getTableName(String str)
    {//str should be in form 'schema.table.column', so this function
        //cuts off the column to get the schema qualified table name.
        log.debug("getting table name from "+str);
        int i=str.lastIndexOf('.');
        if(i==-1)
            return str;
        return str.substring(0,i);
    }
    
    private Query buildInitialTree()
    {
        log.debug("building initial tree");
        Query query;
        Order order;
        List fields=new LinkedList(),tables=new LinkedList();
        Expression conditions;
        
        order=new Order(new DbField(rootTableName+"."+defaultColumn,String.class),"ASC");
        
        fields.add(rootTableName+"."+primaryKey);
        fields.add(rootTableName+"."+defaultColumn);
        
        tables.add(rootTableName);
        
        conditions=new Operation("=",
                new DbField(rootTableName+"."+defaultColumn,String.class),
                new StringLiteralValue(""));
        
        query=new Query(conditions,fields,tables,order,
                new Integer(Common.MAXKEYS));
        return query;
    }
////////////////////////////////////////////////////////////////////////    
    private boolean isUnaryOp(int opId)
    {
        return opId >= unaryBoundry;
    }
    
    void defineOptions()
    {   //this should be overridden to define the fields, booleans, and operator arrays.
        
    }
    
    class ExpressionSet
    {
        private Expression joins,restrictedValues;

        public ExpressionSet(Expression j,Expression rv)
        {
            joins=j;
            restrictedValues=rv;
        }
        public Expression getJoins()
        {
            return joins;
        }
        public Expression getRestrictedValues()
        {
            return restrictedValues;
        }
        public void setJoins(Expression joins)
        {
            this.joins = joins;
        }
        public void setRestrictedValues(Expression restrictedValues)
        {
            this.restrictedValues = restrictedValues;
        }    
        public String toString()
        {
            return "joins: \n"+joins.toString("    ")+"\n"+
                   "restricted values: \n"+restrictedValues.toString("    ");
        }
    }
    
}
