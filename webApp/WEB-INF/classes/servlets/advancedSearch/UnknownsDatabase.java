/*
 * UnknownsDatabase.java
 *
 * Created on September 7, 2004, 3:58 PM
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
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import java.io.*;
import servlets.dataViews.*;

public class UnknownsDatabase extends DefaultSearchableDatabase
{
//    public Field[] fields;
//    public String[] operators;
//    public String[] booleans; 
//    
//    private int unaryBoundry; //seperates unary and binary ops in operators array
//    private static DbConnection dbc=null;  //need a connection to a different database
//    private static Logger log=Logger.getLogger(UnknownsDatabase.class);
//    private static SearchStateManager ssm=new SearchStateManager("UnknownDatabase.sss");
    
    /** Creates a new instance of UnknownsDatabase */
    public UnknownsDatabase() 
    {
        super(null,"UnknownDatabase.properties");
        dbc=DbConnectionManager.getConnection("unknowns");
        if(dbc==null)
            try{
                Class.forName("org.gjt.mm.mysql.Driver").newInstance();
                dbc=new DbConnection("jdbc:mysql://138.23.191.152/unknowns","servlet","512256");
                DbConnectionManager.setConnection("unknowns",dbc); 
            }catch(Exception e){
                log.error("could not connect to database: "+e.getMessage());
            }
        //defineOptions();
    }
    
//    public String buildQuery(SearchState state) 
//    {
//        StringBuffer query=new StringBuffer();
//        
//        query.append("SELECT DISTINCT unknowns.unknown_id " +
//                     "FROM unknowns LEFT JOIN treats USING(unknown_id) " +
//                     "WHERE (");
//        
//        
//        int sp=0,ep=0;
//        int fid,oid;
//        for(int i=0;i<state.getSelectedFields().size();i++)
//        {
//            if(sp < state.getStartParinths().size() && state.getStartParinth(sp).intValue()==i){
//                sp++;
//                query.append("(");
//            }
//            fid=state.getSelectedField(i).intValue();
//            oid=state.getSelectedOp(i).intValue();                        
//            
//            query.append(fields[fid].dbName+" "+operators[oid]+" ");
//
//            if(!isUnaryOp(oid) && fields[fid].type.equals(String.class) || fields[fid].type.equals(List.class))
//                query.append("'"+state.getValue(i)+"'");            
//            else if(!isUnaryOp(oid))
//                query.append(state.getValue(i));                
//            
//            query.append(" ");
//            
//            if(ep < state.getEndParinths().size() && state.getEndParinth(ep).intValue()==i){
//                ep++;
//                query.append(")");
//            }
//
//            if(i+1 < state.getSelectedFields().size())
//                query.append(booleans[state.getSelectedBool(i).intValue()]+" ");                        
//        }
//        
//        
//        query.append(") ORDER BY "+fields[state.getSortField()].dbName);
//        query.append(" LIMIT "+state.getLimit());
//        return query.toString();
//    }
//    public servlets.advancedSearch.queryTree.Query buildQueryTree(SearchState state)
//    {
//        return null;
//    }
//
//    public String[] getBooleans() 
//    {
//        return booleans;
//    }
//    
//    public Field[] getFields() 
//    {
//        return fields;
//    }
//    
//    public String[] getOperators() 
//    {
//        return operators;
//    }
//    private boolean isUnaryOp(int opId)
//    {
//        return opId >= unaryBoundry;
//    }
    void defineOptions()
    {                
        rootTableName="unknowns";
        primaryKey="unknown_id";
        defaultColumn="At_key";
                
        String[] printNames=new String[]{
            "At Key" ,
            "Description" ,
            "Unknown Method TIGR" ,
            "Unknown Method SWP_BLAST" ,
            "Unknown Method GO: MFU OR CCU OR BPU" ,
            "Unknown Method GO: MFU" ,
            "Unknown Method InterPro" ,
            "Unknown Method Pfam" ,
            "Citosky Small List" ,
            "SALK tDNA-Insertion" ,
            "EST avail" ,
            "avail" ,
            "flcDNA TIGR (XML) avail" ,
            "Nottingham Chips: 3x >90" ,
            "Rice Orth E-value" ,
            "HumanRatMouse Orth E-value" ,
            "S. cerevisiae E-value" ,
            "Gene Family Size 35%_50%_70% ident" ,
            "Pet Gene from" ,
            "Targeting Ipsort" ,
            "Targeting Predotar" ,
            "Targeting Targetp" ,
            "Membr dom Hmmtop" ,
            "Membr dom Thumbup" ,
            "Membr dom TMHMM" ,
            "Focus list of grant" ,
            "Selected by" ,
            "Multiple selects" ,
            "Occurrence in treaments",        
            "Treatments"
        };
        String[] dbNames=new String[]{
            rootTableName+".At_Key",
            rootTableName+".Description",
            rootTableName+".Unknown_Method_TIGR",
            rootTableName+".Unknown_Method_SWP_BLAST",
            rootTableName+".Unknown_Method_GO_MFU_OR_CCU_OR_BPU",
            rootTableName+".Unknown_Method_GO_MFU",
            rootTableName+".Unknown_Method_InterPro",
            rootTableName+".Unknown_Method_Pfam",
            rootTableName+".Citosky_Small_List",
            rootTableName+".SALK_tDNA_Insertion",
            rootTableName+".EST_avail",
            rootTableName+".avail",
            rootTableName+".flcDNA_TIGR_XML_avail",
            rootTableName+".Nottingham_Chips_3x_90",
            rootTableName+".Rice_Orth_Evalue",
            rootTableName+".HumanRatMouse_Orth_Evalue",
            rootTableName+".S_cerevisiae_Evalue",
            rootTableName+".Gene_Family_Size_35_50_70_perc_ident",
            rootTableName+".Pet_Gene_from",
            rootTableName+".Targeting_Ipsort",
            rootTableName+".Targeting_Predotar",
            rootTableName+".Targeting_Targetp",
            rootTableName+".Membr_dom_Hmmtop",
            rootTableName+".Membr_dom_Thumbup",
            rootTableName+".Membr_dom_TMHMM",
            rootTableName+".Focus_list_of_grant",
            rootTableName+".Selected_by",
            rootTableName+".Multiple_selects",
            "treats.Occurrence_in_treaments",
            "treats.treat"
        };
        fields=new Field[dbNames.length];
        for(int i=0;i<fields.length;i++)
            fields[i]=new Field(printNames[i],dbNames[i]);
        
        operators=new String[]{"=","!=","<",">","<=",">=",
                "LIKE","NOT LIKE","is NULL","is not NULL"};
        unaryBoundry=9;
        booleans=new String[]{"and","or"};        
    }
     protected ServletRequest getNewRequest(SearchState state,HttpServletRequest request,List results)
     {
         NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);         
                
        mRequest.getParameterMap().put("displayType","unknownsView");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        return mRequest;
     }
//    public String getDestination() {
//        return "UnknownResultsServlet";
//    }
//    
//    public List sendQuery(String query) {
//        try{
//            return dbc.sendQuery(query);
//        }catch(Exception e){
//            log.error("could not send query: "+e.getMessage());
//        }
//        return null;
//    }       
//    
//    public void displayResults(SearchState state, ServletContext context, HttpServletRequest request, HttpServletResponse response) 
//    {
//        List results=sendQuery(buildQuery(state));
//        
//        //then figure out how to pass this info to QueryPageServlet via post.
//        //set the parameters needed by QueryPageServlet
//        
//        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
//                    (HttpServletRequest)request,new HashMap(),false,"POST");
//        
//        mRequest.getParameterMap().put("searchType","seq_id");
//        mRequest.getParameterMap().put("limit", state.getLimit());
//        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);         
//                
//        mRequest.getParameterMap().put("displayType","unknownsView");
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
//        
//    }
//    
//    public SearchStateManager getSearchManager() 
//    {
//        return ssm;
//    }
    
}
