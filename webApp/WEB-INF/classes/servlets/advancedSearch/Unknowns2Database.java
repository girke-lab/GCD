/*
 * Unknowns2Database.java
 *
 * Created on October 7, 2004, 1:44 PM
 */

package servlets.advancedSearch;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import org.apache.log4j.Logger;
import javax.servlet.http.*;
import javax.servlet.ServletContext;
import java.io.*;
import servlets.dataViews.*;
import servlets.advancedSearch.queryTree.*;
import servlets.advancedSearch.visitors.*;

public class Unknowns2Database extends DefaultSearchableDatabase
{
//    public Field[] fields;
//    public String[] operators;
//    public String[] booleans; 
    
//    private final static int rpp=25;
    
//    private int unaryBoundry; //seperates unary and binary ops in operators array
//    private String rootTableName;
//    private int sp,ep,index;
//    private static DbConnection dbc=null;  //need a connection to a different database
//    private static Logger log=Logger.getLogger(Unknowns2Database.class);
//    private static SearchStateManager ssm=new SearchStateManager("Unknown2Database.sss");
//    private static SearchTreeManager stm=new SearchTreeManager("Unknown2Database.properties");
    
    /** Creates a new instance of Unknowns2Database */
   
    public Unknowns2Database()
    {        
        super(null,"Unknown2Database.properties");
        log.debug("back from super constructor");
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            try{
                Class.forName("org.postgresql.Driver").newInstance();
                dbc=new DbConnection("jdbc:postgresql://bioinfo.ucr.edu/khoran","servlet","512256");
                DbConnectionManager.setConnection("khoran",dbc); 
            }catch(Exception e){
                log.error("could not connect to database: "+e.getMessage());
            }
//        defineOptions();
        log.debug("fields.length="+fields.length);
    }
    
    void defineOptions()
    {   
        log.debug("defining options"); 
        rootTableName="unknowns.unknown_keys";
        primaryKey="key_id";
        defaultColumn="key";
        
        String db="unknowns.";        
        String space=" &nbsp&nbsp ";
        //as long as we only use fields from tables that have a 'key_id' column,
        //we don't need any special cases in the query building code.
        
        fields=new Field[]{
            new Field("At key",db+"unknown_keys.key",List.class),
            new Field("Description",db+"unknown_keys.description"),
            new Field("Number of ests",db+"unknown_keys.est_count",Integer.class),
                        
            new Field("Similarity Searches (best per db)",""),                        
            new Field(space+"database",db+"blast_summary_view.db_name",             
                        new String[]{"swp","pfam"}),
            new Field(space+"method",db+"blast_summary_view.method",
                        new String[]{"BLASTP","hmmPfam"}),
            new Field(space+"Blast target accession",db+"blast_summary_view.target_accessicd" +
            "cdon"),
            new Field(space+"Blast target description",db+"blast_summary_view.target_description"),    
            new Field(space+"best e_value",db+"blast_summary_view.e_value",Float.class),       
            new Field(space+"score",db+"blast_summary_view.score"),
            new Field(space+"identities",db+"blast_summary_view.identities"),            
            
            new Field("GO",""),
            new Field(space+"number",db+"go_view.go_number",List.class),
            new Field(space+"description",db+"go_view.text"),
            new Field(space+"function",db+"go_view.function",
                        new String[]{"process","component","function"}),
            new Field(space+"Molecular function unknown?",db+"unknown_keys.mfu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field(space+"Cellular component unknown?",db+"unknown_keys.ccu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field(space+"Biological process unknown?",db+"unknown_keys.bpu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
                    
                        
            new Field("Clusters",""),
            new Field(space+"Score Threshold",db+"cluster_info_and_counts_view.cutoff",Integer.class,
                        new String[]{"35","50","70"}),
            new Field(space+"Size",db+"cluster_info_and_counts_view.size",Integer.class),
            
            new Field("Proteomic Stats",""),            
            new Field(space+"Molecular Weight",db+"proteomics_stats.mol_weight"),
            new Field(space+"Isoelectric Point",db+"proteomics_stats.ip"),
            new Field(space+"Charge",db+"proteomics_stats.charge"),
            new Field(space+"Probability of expression in inclusion bodies",db+"proteomics_stats.prob_in_body"),
            new Field(space+"Probability is negative",db+"proteomics_stats.prob_is_neg",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
                        
            new Field("External Sources",""),
            new Field(space+"Source",db+"external_unknowns.source",new String[]{"tigr","citosky"}),
            new Field(space+"is unknown?",db+"external_unknowns.is_unknown",Boolean.class,
                        new String[]{"TRUE","FALSE"})
        };
//new Field(space+"",""),
        operators=new String[]{"=","!=","<",">","<=",">=",
                "ILIKE","NOT ILIKE","IS NULL","IS NOT NULL"};
        unaryBoundry=8; //index of first unary op.
        booleans=new String[]{"and","or"};                
    }
    ////////////  SearchableDatabase Interface methods
    ////////////////////////////////////////////////
//    public void displayResults(SearchState state, javax.servlet.ServletContext context, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
//    {
//        List results;
//        try{
//            Query queryTree=buildQueryTree(state);
//            SqlVisitor sv=new SqlVisitor();            
//            String sql=sv.getSql(queryTree);
//            log.debug("sql="+sql);
////            if(true)
////                return;
//            results=dbc.sendQuery(sql);
//            //results=dbc.sendQuery(buildQuery(state));
//        }catch(Exception e){
//            log.error("could not send query: "+e.getMessage());            
//            e.printStackTrace();
//            return;
//        }        
//        //then figure out how to pass this info to QueryPageServlet via post.
//        //set the parameters needed by QueryPageServlet
//        
//        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
//                    (HttpServletRequest)request,new HashMap(),false,"POST");
//        
//        mRequest.getParameterMap().put("searchType","seq_id");
//        mRequest.getParameterMap().put("limit", state.getLimit());
//        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);  
//        mRequest.getParameterMap().put("rpp",new Integer(rpp).toString());
//                
//        mRequest.getParameterMap().put("displayType","unknowns2View");
//        
//        StringBuffer inputStr=new StringBuffer();      
//        for(Iterator i=results.iterator();i.hasNext();)
//            inputStr.append(((List)i.next()).get(0)+" ");       
//
//        mRequest.getParameterMap().put("inputKey",inputStr.toString());
//        
//        try{            
//            context.getRequestDispatcher("/QueryPageServlet").forward(mRequest, response);            
//        }catch(Exception e){
//            log.error("could not forward to QueryPageServlet: "+e.getMessage());
//            e.printStackTrace();
//        }
//    }
//    public Query buildQueryTree(SearchState state)
//    {
//        log.debug("building query tree");
//        
//        if(state.getSelectedFields().size()==0)
//            return buildInitialTree();
//        
//        List fields=new LinkedList();
//        Set tables=new HashSet();
//        Integer limit;
//        DbField orderField;
//        Order order;
//        Expression condition;
//        Query query;
//
//                 //make sure we have a valid sort field
//        if(getFields()[state.getSortField()].dbName.length()==0)
//            state.setSortField(0);
//        
//        limit=Integer.decode(state.getLimit());
//        
//        orderField=new DbField(getFields()[state.getSortField()].dbName, String.class);
//        order=new Order(orderField,"ASC");
//        
//        fields.add("unknowns.unknown_keys.key_id");
//        fields.add(orderField.getName());
//        
//        //add tables to from clause and create condition
//        condition=buildCondition(state,tables);
//        
//        query=new Query(condition,fields,new LinkedList(tables),order,limit);
//        log.debug("query="+query);
//        return query;
//    }
//    public String[] getBooleans() 
//    {
//        return booleans;
//    }    
//    public Field[] getFields() 
//    {
//        return fields;
//    }    
//    public String[] getOperators() 
//    {
//        return operators;
//    }   
//    public SearchTreeManager getSearchManager()
//    {
//        return stm;
//    }
//    //////////////////////////////////////////////////
//    
//    /**method for building trees. 
//     * Tables should be an empty set to which the from tables are added.
//     * 
//     */
//    private Expression buildCondition(SearchState state,Set tables)
//    {
//        ExpressionSet expSet=null;
//        Expression condition;
//        
//        sp=0;  
//        ep=0;
//        index=0;
//        expSet=buildExpression(state,tables);
//        
//        //make sure we add the table we're sorting by.
//        expSet.setJoins(updateJoin(expSet.getJoins(),getTableName(getFields()[state.getSortField()].dbName),tables));
//                
//        if(expSet.getJoins()==null)
//            condition= expSet.getRestrictedValues();
//        else if(expSet.getRestrictedValues()==null) //this should never happen
//        {
//            log.warn("no restrictedValue conditions");
//            condition=expSet.getJoins();
//        }
//        else
//            condition=new Operation("and",expSet.getJoins(),expSet.getRestrictedValues());        
//        
//        if(condition==null)
//            condition=new Operation("=",new DbField(rootTableName+".key",String.class),
//                    new StringLiteralValue(""));
//        return condition;
//    }
//    private ExpressionSet buildExpression(SearchState state,Set tables)
//    {
//        int fieldCount=state.getSelectedFields().size();
//        int fid,oid,bid; //field,operator, and boolean ids        
//        String value,tableName;
//        Expression joins=null,restrictedValues=null;
//        
//        log.debug("starting buildExpression, sp="+sp+", ep="+ep+", index="+index);
//        for(;index<fieldCount;index++)
//        {
//            fid=state.getSelectedField(index).intValue();
//            oid=state.getSelectedOp(index).intValue();
//            value=state.getValue(index);
//            if(index>0)
//                bid=state.getSelectedBool(index-1).intValue();
//            else 
//                bid=-1; //should not be used on first iteration anyway.            
//            
//            //log.debug("fid="+fid+", oid="+oid+", value="+value+"\n\tbid="+bid);            
//            tableName=getTableName(getFields()[fid].dbName);
//            if(tableName.equals(""))
//            {
//                log.debug("reseting title field to first field");                
//                state.getSelectedFields().set(index,new Integer(0));
//                fid=0;
//                tableName=getTableName(getFields()[fid].dbName);
//            }
//            
//            //add join conditions
//            joins=updateJoin(joins,tableName,tables);            
//                        
//            if(sp < state.getStartParinths().size() && state.getStartParinth(sp).intValue()==index){
//                log.debug("found open parinth");
//                sp++;
//                //start building expression from i
//                log.debug("starting sub espression, index="+index);
//                ExpressionSet es=buildExpression(state,tables);
//                log.debug("back from sub expression, index="+index);
//                                
//                if(es.getJoins()!=null)
//                {//update joins
//                    if(joins==null)
//                        joins=es.getJoins();
//                    else
//                        joins=new Operation("and",es.getJoins(),joins);
//                }
//                if(es.getRestrictedValues()!=null)
//                {//update restricted values
//                    if(restrictedValues==null)
//                        restrictedValues=es.getRestrictedValues();
//                    else    
//                        restrictedValues=new Operation(getBooleans()[bid],restrictedValues,es.getRestrictedValues());
//                }                
//                continue;               
//            }            
//            
//            //add regular condition
//            Operation op;
//            DbField field=new DbField(getFields()[fid].dbName,getFields()[fid].type);
//            
//            if(isUnaryOp(oid))
//                op=new Operation(getOperators()[oid],field,Operation.LEFT);
//            else
//            {
//                LiteralValue lv=getLiteralValue(getFields()[fid],value);
//                op=new Operation(getOperators()[oid],field,lv);
//            }                        
//            
//            //log.debug("new expression is :"+op);
//            if(restrictedValues==null)
//                restrictedValues=op;
//            else
//                restrictedValues=new Operation(getBooleans()[bid],restrictedValues,op);            
//            
//            if(ep < state.getEndParinths().size() && state.getEndParinth(ep).intValue()==index){
//                ep++;
//                log.debug("found end parinth, index="+index);
//                //break loop and return current expression
//                break;                
//            }
//        }
//        return new ExpressionSet(joins,restrictedValues);
//    }
//    private Expression updateJoin(Expression currentJoins,String tableName,Set tables)
//    {
//        DbField root_key=new DbField(rootTableName+".key_id",Integer.class);
//        Expression newJoins=currentJoins;
//        //see if we have already added this table
//        if(!tables.contains(tableName))
//        { //then add table to set, and add join condition
//            tables.add(tableName);
//            if(!tableName.equals(rootTableName))
//            {//no need to join root table to itself.
//                Operation op;
//                DbField table_key=new DbField(tableName+".key_id",Integer.class);                
//
//                op=new Operation("=",root_key,table_key);
//                if(currentJoins==null)
//                    newJoins=op;
//                else
//                    newJoins=new Operation("and",op,currentJoins);
//            }
//        }
//        return newJoins;
//    }
//    private LiteralValue getLiteralValue(Field f,String v)
//    {
//        if(f.type==String.class)
//            return new StringLiteralValue(v);
//        else if(f.type==Integer.class)
//            return new IntLiteralValue(Integer.valueOf(v));
//        else if(f.type==Float.class)
//            return new FloatLiteralValue(Float.valueOf(v));
//        else if(f.type==Boolean.class)                  
//            return new BooleanLiteralValue(Common.getBoolean(v));
//        else if(f.type==List.class)
//        {
//            List l=new LinkedList();
//            StringTokenizer tok=new StringTokenizer(v);
//            while(tok.hasMoreTokens())
//                l.add(tok.nextToken()); 
//            return new ListLiteralValue(l);
//        }            
//        else
//            log.error("unknown type: "+f.type.getName());
//        return null;
//    }
//    private String getTableName(String str)
//    {//str should be in form 'schema.table.column', so this function
//        //cuts off the column to get the schema qualified table name.
//        int i=str.lastIndexOf('.');
//        if(i==-1)
//            return str;
//        return str.substring(0,i);
//    }
//    
//    private Query buildInitialTree()
//    {
//        log.debug("building initial tree");
//        Query query;
//        Order order;
//        List fields=new LinkedList(),tables=new LinkedList();
//        Expression conditions;
//        
//        order=new Order(new DbField(rootTableName+".key",String.class),"ASC");
//        
//        fields.add(rootTableName+".key_id");
//        fields.add(rootTableName+".key");
//        
//        tables.add(rootTableName);
//        
//        conditions=new Operation("=",
//                new DbField(rootTableName+".key",String.class),
//                new StringLiteralValue(""));
//        
//        query=new Query(conditions,fields,tables,order,
//                new Integer(Common.MAXKEYS));
//        return query;
//    }
//    
// /**   
//    
//    //////////////////////////////////////////////////
//    ///    old query building methods
//    ///////////////////////////////////////////////////
//    private String buildQuery(SearchState state)
//    {
//        String joinConditions,userConditions,fieldList,order;
//        StringBuffer join=new StringBuffer();       
//        StringBuffer query=new StringBuffer();
//        
//        //make sure we have a valid sort field
//        if(getFields()[state.getSortField()].dbName.length()==0)
//            state.setSortField(0);
//        
//        order=getFields()[state.getSortField()].dbName;
//                
//        fieldList="unknowns.unknown_keys.key_id, "+order;
//        
//        //build from clause        
//        Set tables=buildTableSet(state,order);        
//        for(Iterator i=tables.iterator();i.hasNext();)
//        {
//            join.append(i.next());
//            if(i.hasNext())
//                join.append(",");
//        }
//        //build conditions related to the join
//        joinConditions=buildJoinConditions(tables);
//        //build condition
//        userConditions=buildConditions(state);
//        
//        //assemble query
//        query.append("SELECT DISTINCT "+fieldList);
//        query.append(" FROM "+join);
//        if(joinConditions.length()!=0 || userConditions.length()!=0)
//        {
//            query.append(" WHERE "+joinConditions);
//            if(userConditions.length()!=0){
//                if(joinConditions.length()!=0)
//                    query.append(" AND ");
//                query.append("("+userConditions+")");
//            }                            
//        }                
//        query.append(" ORDER BY "+order);
//        query.append(" LIMIT "+state.getLimit());
//        
//        //log.info("unknowns2 query: "+query);
//        return query.toString();
//    }    
//    private Set buildTableSet(SearchState state,String order)
//    {
//        Set tables=new HashSet();
//        
//        tables.add("unknowns.unknown_keys"); //make sure root table is always added        
//        //sort field must be added to keep postgres from adding an unconstrained join
//        int j=order.lastIndexOf('.');
//        tables.add(order.substring(0,j==-1?order.length():j)); 
//            
//        int fid;
//        boolean seq_gosAdded=false,blast_dbAdded=false;
//        for(Iterator i=state.getSelectedFields().iterator();i.hasNext();)
//        {
//            fid=((Integer)i.next()).intValue();
//            if(getFields()[fid].dbName.equals("")) //this is just a title field
//                continue;
//            if(getFields()[fid].dbName.startsWith("go.") && !seq_gosAdded)
//            {
//                tables.add("go.seq_gos");
//                seq_gosAdded=true;
//            }            
//            else if(getFields()[fid].dbName.startsWith("unknowns.blast_databases") && !blast_dbAdded)
//            {
//                tables.add("unknowns.blast_results");
//                blast_dbAdded=true;
//            }
//            tables.add(getFields()[fid].dbName.substring(0,getFields()[fid].dbName.lastIndexOf('.')));
//        }
//        return tables;
//    }
//    private String buildJoinConditions(Set tables)
//    {
//        StringBuffer conditions=new StringBuffer();
//        String rootTable="unknowns.unknown_keys",table;        
//        String goRootTable="go.seq_gos";
//        boolean goRootAdded=false,addGoRoot=false;
//        
//        tables.remove(rootTable);
//        for(Iterator i=tables.iterator();i.hasNext();)
//        {
//            table=(String)i.next();
//                        
//            if(table.equals("unknowns.blast_databases"))
//                conditions.append("unknowns.blast_results.blast_db_id=unknowns.blast_databases.blast_db_id");
//            else if(table.startsWith("unknowns."))
//                conditions.append(rootTable+".key_id="+table+".key_id");
//            else if(!goRootAdded && table.equals(goRootTable))
//            {                
//                goRootAdded=true;
//                conditions.append("substring("+rootTable+".key from 1 for 9)=go.seq_gos.accession");
//            }                
//            else if(table.startsWith("go."))
//            { //make sure the go root is added if any go tables are used                
//                if(!goRootAdded)                    
//                {
//                    conditions.append("substring("+rootTable+".key from 1 for 9)=go.seq_gos.accession AND ");
//                    addGoRoot=true;
//                }                    
//                conditions.append(goRootTable+".go_id="+table+".go_id");            
//            }
//            
//            if(i.hasNext())
//                conditions.append(" AND ");            
//        }
//        return conditions.toString();
//    }
//    private String buildConditions(SearchState state)
//    {
//        StringBuffer conditions=new StringBuffer();
//        
//        int sp=0,ep=0;
//        int fid,oid;
//        for(int i=0;i<state.getSelectedFields().size();i++)
//        {
//            fid=state.getSelectedField(i).intValue();
//            oid=state.getSelectedOp(i).intValue();
//
//            if(getFields()[fid].dbName.equals(""))
//                continue;
//            
//            if(sp < state.getStartParinths().size() && state.getStartParinth(sp).intValue()==i){
//                sp++;
//                conditions.append("(");
//            }
//                        
//            conditions.append(fields[fid].dbName+" "+operators[oid]+" ");
//
//            if(!isUnaryOp(oid) && fields[fid].type.equals(String.class))
//                conditions.append("'"+state.getValue(i)+"'");            
//            else if(!isUnaryOp(oid))
//                conditions.append(state.getValue(i));    
//                    
//            conditions.append(" ");
//            
//            if(ep < state.getEndParinths().size() && state.getEndParinth(ep).intValue()==i){
//                ep++;
//                conditions.append(")");
//            }
//            // tack on operator
//            if(i+1 < state.getSelectedFields().size())
//                conditions.append(booleans[state.getSelectedBool(i).intValue()]+" ");                        
//        }        
//        
//        return conditions.toString();
//    }
//    /////////////////////////////////////////////////////////
//    
// */   
//    
//    private boolean isUnaryOp(int opId)
//    {
//        return opId >= unaryBoundry;
//    }
   

 
}
//class ExpressionSet
//{
//    private Expression joins,restrictedValues;
//    
//    public ExpressionSet(Expression j,Expression rv)
//    {
//        joins=j;
//        restrictedValues=rv;
//    }
//    public Expression getJoins()
//    {
//        return joins;
//    }
//    public Expression getRestrictedValues()
//    {
//        return restrictedValues;
//    }
//    public void setJoins(Expression joins)
//    {
//        this.joins = joins;
//    }
//    public void setRestrictedValues(Expression restrictedValues)
//    {
//        this.restrictedValues = restrictedValues;
//    }    
//    public String toString()
//    {
//        return "joins: \n"+joins.toString("    ")+"\n"+
//               "restricted values: \n"+restrictedValues.toString("    ");
//    }
//}