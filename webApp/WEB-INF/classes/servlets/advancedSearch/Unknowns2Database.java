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

public class Unknowns2Database implements SearchableDatabase
{
    public Field[] fields;
    public String[] operators;
    public String[] booleans; 
   
    
    private int unaryBoundry; //seperates unary and binary ops in operators array
    private static DbConnection dbc=null;  //need a connection to a different database
    private static Logger log=Logger.getLogger(Unknowns2Database.class);
    private static SearchStateManager ssm=new SearchStateManager("Unknown2Database.sss");
    
    /** Creates a new instance of Unknowns2Database */
    public Unknowns2Database()
    {
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            try{
                Class.forName("org.postgresql.Driver").newInstance();
                dbc=new DbConnection("jdbc:postgresql://bioinfo.ucr.edu/khoran","servlet","512256");
                DbConnectionManager.setConnection("khoran",dbc); 
            }catch(Exception e){
                log.error("could not connect to database: "+e.getMessage());
            }
        defineOptions();
    }
    
   
    ////////////  SearchableDatabase Interface methods
    ////////////////////////////////////////////////
    public void displayResults(SearchState state, javax.servlet.ServletContext context, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
    {
        List results;
        try{
            results=dbc.sendQuery(buildQuery(state));
        }catch(Exception e){
            log.error("could not send query: "+e.getMessage());
            //e.printStackTrace();
            return;
        }
        
        //then figure out how to pass this info to QueryPageServlet via post.
        //set the parameters needed by QueryPageServlet
        
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);         
                
        mRequest.getParameterMap().put("displayType","unknowns2View");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        try{            
            context.getRequestDispatcher("/QueryPageServlet").forward(mRequest, response);    
        }catch(Exception e){
            log.error("could not forward to QueryPageServlet: "+e.getMessage());
            e.printStackTrace();
        }
    }
          
    public String[] getBooleans() 
    {
        return booleans;
    }
    
    public Field[] getFields() 
    {
        return fields;
    }
    
    public String[] getOperators() 
    {
        return operators;
    }
   
    public SearchStateManager getSearchManager()
    {
        return ssm;
    }
    //////////////////////////////////////////////////
    
    
    private String buildQuery(SearchState state)
    {
        String joinConditions,userConditions,fieldList,order;
        StringBuffer join=new StringBuffer();       
        StringBuffer query=new StringBuffer();
                
        fieldList="unknowns.unknown_keys.key_id ";
        order=getFields()[state.getSortField()].dbName;
        //build join
        int fid;
        Set tables=new HashSet();
        
        tables.add("unknowns.unknown_keys"); //make sure root table is always added
        //sort field must be added to keep postgres from adding an unconstrained join
        tables.add(order.substring(0,order.lastIndexOf('.'))); 
        
        boolean seq_gosAdded=false;
        for(Iterator i=state.getSelectedFields().iterator();i.hasNext();)
        {
            fid=((Integer)i.next()).intValue();
            if(getFields()[fid].dbName.equals("")) //this is just a title field
                continue;
            if(getFields()[fid].dbName.startsWith("go.") && !seq_gosAdded)
            {
                tables.add("go.seq_gos");
                seq_gosAdded=true;
            }            
            tables.add(getFields()[fid].dbName.substring(0,getFields()[fid].dbName.lastIndexOf('.')));
        }
        for(Iterator i=tables.iterator();i.hasNext();)
        {
            join.append(i.next());
            if(i.hasNext())
                join.append(",");
        }
        joinConditions=buildJoinConditions(tables);
        //build condition
        userConditions=buildConditions(state);
        
        //assemble query
        query.append("SELECT DISTINCT "+fieldList+","+order);
        query.append(" FROM "+join);
        if(joinConditions.length()!=0 || userConditions.length()!=0)
        {
            query.append(" WHERE "+joinConditions);
            if(userConditions.length()!=0){
                if(joinConditions.length()!=0)
                    query.append(" AND ");
                query.append("("+userConditions+")");
            }                            
        }
        query.append(" ORDER BY "+order);
        query.append(" LIMIT "+state.getLimit());
        
//        query="SELECT DISTINCT "+fieldList+","+order+
//              " FROM "+join+
//              " WHERE "+joinConditions+
//                (joinConditions.equals("") || userConditions.equals("")? "":" AND") +
//                (userConditions.equals("")? "":" ("+userConditions+")")+
//              " ORDER BY "+order+
//              " LIMIT "+state.getLimit();
        log.info("unknowns2 query: "+query);
        return query.toString();
    }    
    private String buildJoinConditions(Set tables)
    {
        StringBuffer conditions=new StringBuffer();
        String rootTable="unknowns.unknown_keys",table;        
        String goRootTable="go.seq_gos";
        boolean goRootAdded=false;
        
        tables.remove(rootTable);
        for(Iterator i=tables.iterator();i.hasNext();)
        {
            table=(String)i.next();
                        
            if(table.startsWith("unknowns."))
                conditions.append(rootTable+".key_id="+table+".key_id");
            else if(table.equals(goRootTable))
                conditions.append(rootTable+".key=go.seq_gos.accession");
            else if(table.startsWith("go."))
                conditions.append(goRootTable+".go_id="+table+".go_id");            
            
            if(i.hasNext())
                conditions.append(" AND ");            
        }
        return conditions.toString();
    }
    private String buildConditions(SearchState state)
    {
        StringBuffer conditions=new StringBuffer();
        
        int sp=0,ep=0;
        int fid,oid;
        for(int i=0;i<state.getSelectedFields().size();i++)
        {
            fid=state.getSelectedField(i).intValue();
            oid=state.getSelectedOp(i).intValue();

            if(getFields()[fid].dbName.equals(""))
                continue;
            
            if(sp < state.getStartParinths().size() && state.getStartParinth(sp).intValue()==i){
                sp++;
                conditions.append("(");
            }
                        
            conditions.append(fields[fid].dbName+" "+operators[oid]+" ");

            if(fields[fid].type.equals(String.class))
                conditions.append("'"+state.getValue(i)+"'");            
            else
                conditions.append(state.getValue(i));    
                    
            conditions.append(" ");
            
            if(ep < state.getEndParinths().size() && state.getEndParinth(ep).intValue()==i){
                ep++;
                conditions.append(")");
            }
            // tack on operator
            if(i+1 < state.getSelectedFields().size())
                conditions.append(booleans[state.getSelectedBool(i).intValue()]+" ");                        
        }        
        
        return conditions.toString();
    }
    private boolean isUnaryOp(int opId)
    {
        return opId >= unaryBoundry;
    }
    private void defineOptions()
    {                

        String db="unknowns.";
        String space=" &nbsp&nbsp ";
        fields=new Field[]{
            new Field("At key",db+"unknown_keys.key"),
            new Field("Description",db+"unknown_keys.description"),
            new Field("Number of ests",db+"unknown_keys.est_count"),
            new Field("Molecular function unknown?",db+"unknown_keys.mfu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field("Cellular component unknown?",db+"unknown_keys.ccu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field("Biological process unknown?",db+"unknown_keys.bpu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field("Blast data",""),            
            new Field(space+"blast database",db+"blast_results.blast_type",  
                        new String[]{"uniprot_sprot.fasta"}),
            new Field(space+"Blast target accession",db+"blast_results.target_accession"),
            new Field(space+"e-value",db+"blast_results.e_value",Float.class),
            new Field(space+"score",db+"blast_results.score"),
            new Field(space+"identities",db+"blast_results.identities"),
            //new Field("length",db+"blast_results.length",Integer.class),
            //new Field("positives",db+"blast_results.positives"),
            //new Field("gaps",db+"blast_results.gaps"),
            new Field("GO",""),
            new Field(space+"GO number","go.go_numbers.go_number"),
            new Field(space+"GO description","go.go_numbers.function"),
            new Field(space+"GO function","go.go_numbers.text",
                        new String[]{"process","component","function"})
        };
        
               
        operators=new String[]{"=","!=","<",">","<=",">=",
                "ILIKE","NOT ILIKE","is NULL","is not NULL"};
        unaryBoundry=9;
        booleans=new String[]{"and","or"};        
        
    }
}
