/*
 * TreatmentDatabase.java
 *
 * Created on March 22, 2006, 1:02 PM
 *
 */

package servlets.advancedSearch;


import java.util.*;
import servlets.Common;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import servlets.advancedSearch.fields.*;
import servlets.advancedSearch.fields.Field;
import servlets.advancedSearch.queryTree.*;

import javax.servlet.http.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
/**
 * This database is for searching through different experiments. 
 * It outputs a set of probe_set_key_id-comparison_id pairs.  Most of the
 * search feilds are also present in the Unknowns2 database, but here
 * we want the probe_set  and comparison of the match, not the accession key.
 * @author khoran
 */
public class TreatmentDatabase extends DefaultSearchableDatabase
{
    private static SearchTreeManager stm=new SearchTreeManager("TreatmentDatabase.properties");
   
    private String userName;
    
    /**
     * Creates a new TreatmentDatabase for the given user.
     * We need the user name here since we use queries to provide a list
     * of options for some things. Inparticular, the experiment set list
     * needs to know which experiments the user is allowed to see.
     *   
     * If the user name is null, the name 'public' is used.
     * @param userName Name of the user currently logged in, or null if no user is logged in.
     */
    public TreatmentDatabase(String userName)
    {
        // this will not call defineOptions like usual, so we call it later.
         super(DbConnectionManager.getConnection("khoran"),stm,false);
                
        if(dbc==null)
            try{
                Class.forName("org.postgresql.Driver").newInstance();
                dbc=new DbConnection("jdbc:postgresql://bioinfo.ucr.edu/khoran","servlet","512256");
                DbConnectionManager.setConnection("khoran",dbc); 
            }catch(Exception e){
                log.error("could not connect to database: "+e.getMessage());
            }
        if(userName==null){
             log.debug("setting user name to public");
             this.userName="public";
        }            
        else
            this.userName=userName;
         
        defineOptions();
        log.debug("fields.length="+fields.length);
    }
    
    /**
     * Calls the parents buildQueryTree  and then adds on some distinct fields.
     * We need to make the result set distinct on comparison_id and probe_set_key_id
     * so that we don't get dups in the dataview. This requres adding these fields
     * to te order by clause also.
     * @param state 
     * @return 
     */
     public Query buildQueryTree(SearchState state)
    {          
        Query q=super.buildQueryTree(state);
        
        q.setDistinct(true);
        q.addDistinctField("experiment_group_summary_view.comparison_id");
        q.addDistinctField("experiment_group_summary_view.probe_set_key_id");
        
        // all distinct fields must appear first in order
        Order o=q.getOrder();
        
        if(o.getOrder() instanceof DbField)
        {
            String oldOrder=((DbField)o.getOrder()).getName();
            o=new Order(new DbField("experiment_group_summary_view.comparison_id, "+
                                    "experiment_group_summary_view.probe_set_key_id, " +
                                    oldOrder, String.class), "asc");
        }
        q.setOrder(o);
        
        q.getFields().add("experiment_group_summary_view.comparison_id");
        
        return q;
    }        
//    protected List additionalJoins()
//    {
//        List joins=new LinkedList();
//        
//        joins.add("affy.experiment_group_summary_view");
//        
//        return joins;
//    }
    /**
     * the original sql will make sure rows are distinct on
     * columns 1 and 3, but we want to sort now on the user specified
     * column, which is always column 2. ( we must sort first by 3
     * to keep comparisons together).
     * @param sql 
     * @return 
     */
     protected String modifySql(String sql)
     {
         
         return "SELECT * FROM ("+sql+") as t ORDER BY 3,2";
     }
     
     
    void defineOptions()
    {   
        log.debug("defining options"); 

        rootTableName="affy.experiment_group_summary_view";
        primaryKey="probe_set_key_id";
        defaultColumn="experiment_set_key";
        
        String space=" &nbsp&nbsp ";
        
        //as long as we only use fields from tables that have an 'accession_id' column,
        //we don't need any special cases in the query building code.
        
        String catagoryQuery="SELECT catagory FROM affy.catagory_list ORDER BY catagory";
        String analysisQuery="SELECT type_name FROM affy.cel_analysis_type_list ORDER BY type_name";
        String expSetQuery="SELECT DISTINCT key FROM affy.experiments_view" +
                                " JOIN affy.es_valid_users USING(experiment_set_id)" +
                                " WHERE user_name='"+userName+"' ORDER BY key";
        
        
        fields=new Field[]{                                    
            new StringField(space+"Probe Set Key",
                                "affy.experiment_group_summary_view.probe_set_key",true).setSortable(true),
            new ListField(space+"Experiment Set Key",
                                "affy.experiment_group_summary_view.experiment_set_key",expSetQuery).setSortable(true),            

            new StringField("Affy Experiment Sets",""), 
            new ListField(space+"Catagory","affy.experiment_set_summary_view.catagory",catagoryQuery),                    
            new ListField(space+"Intensity type","affy.experiment_group_summary_view.data_type",analysisQuery),                                
            new IntField(space+">4 fold change up","affy.experiment_set_summary_view.up4x"),                                
            new IntField(space+">4 fold change down","affy.experiment_set_summary_view.down4x"),
            new IntField(space+">2 fold change up","affy.experiment_set_summary_view.up2x"),                                
            new IntField(space+">2 fold change down","affy.experiment_set_summary_view.down2x"),
            new IntField(space+"PMA on","affy.experiment_set_summary_view.pma_on"),
            new IntField(space+"PMA off","affy.experiment_set_summary_view.pma_off"),         
            new FloatField(space+"Control average","affy.experiment_set_summary_view.control_average"),
            new FloatField(space+"Treatment average","affy.experiment_set_summary_view.treatement_average"),
            new FloatField(space+"Control std deviation","affy.experiment_set_summary_view.control_stddev"),
            new FloatField(space+"Treatment std deviation","affy.experiment_set_summary_view.treatment_stddev"),
                        
            new StringField("Affy Experiment Comparisions",""), 
            new StringField(space+"Description","affy.experiment_group_summary_view.description"),
            new IntField(space+"Comparison","affy.experiment_group_summary_view.comparison"),     //TODO: query here to get list of valid comparisons
            new FloatField(space+"Control mean","affy.experiment_group_summary_view.control_mean").setSortable(true),
            new StringField(space+"Control PMA","affy.experiment_group_summary_view.control_pma").setSortable(true),
            new FloatField(space+"Treatment mean","affy.experiment_group_summary_view.treatment_mean").setSortable(true),
            new StringField(space+"Treatment PMA","affy.experiment_group_summary_view.treatment_pma").setSortable(true),
            new FloatField(space+"Ratio (log_2(treat_mean/control_mean))",
                    "affy.experiment_group_summary_view.t_c_ratio_lg").setSortable(true),            
            new FloatField(space+"Contrast","affy.experiment_group_summary_view.contrast").setSortable(true),
            new FloatField(space+"P-value","affy.experiment_group_summary_view.p_value").setSortable(true),
            new FloatField(space+"Adjusted P-value","affy.experiment_group_summary_view.adj_p_value").setSortable(true),
            new FloatField(space+"pfp up","affy.experiment_group_summary_view.pfp_up").setSortable(true),
            new FloatField(space+"pfp down","affy.experiment_group_summary_view.pfp_down").setSortable(true),
                                                    
            new StringField("Correlations",""), 
            new FloatField(space+"Correlation","affy.correlation_view.correlation"),
            new FloatField(space+"P Value","affy.correlation_view.p_value")
        };
               
        booleans=new String[]{"and","or"};                
    }
 
    /**
     * Sends results to the probeSetView dataview.
     * The keys are probe_set_key_id and comparison_id appended with
     * an underscore.
     */
    protected ServletRequest getNewRequest(SearchState state,HttpServletRequest request,List results)
    { //this can be overridden by sub classes to send different parameters
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol","psk_"+getFields()[state.getSortField()].dbName);  
        mRequest.getParameterMap().put("rpp",new Integer(rpp).toString());
                
        mRequest.getParameterMap().put("displayType","probeSetView");
        mRequest.getParameterMap().put("origin_page","unknownsSearch.jsp");
        
        StringBuilder inputStr=new StringBuilder();      
        List row;
        
        for(Iterator i=results.iterator();i.hasNext();)
        {
            row=(List)i.next();
            inputStr.append(row.get(0)+"_"+row.get(2)+" ");
        }

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        return mRequest;
    }

    
}
