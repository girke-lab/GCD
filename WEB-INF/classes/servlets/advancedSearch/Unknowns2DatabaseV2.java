/*
 * Unknowns2DatabaseV2.java
 *
 * Created on March 30, 2005, 9:12 AM
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
import servlets.advancedSearch.fields.*;
import servlets.advancedSearch.fields.Field;
import servlets.advancedSearch.queryTree.*;

import javax.servlet.http.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;

/**
 * This database is for searching for accessions.
 */
public class Unknowns2DatabaseV2 extends DefaultSearchableDatabase
{       
 
    private static SearchTreeManager stm=new SearchTreeManager("Unknown2DatabaseV2.properties");
    
    /** Creates a new instance of Unknowns2Database */
   
    public Unknowns2DatabaseV2()
    {        
        super(DbConnectionManager.getConnection("khoran"),stm);
                
        if(dbc==null)
            try{
                Class.forName("org.postgresql.Driver").newInstance();
                dbc=new DbConnection("jdbc:postgresql://bioinfo.ucr.edu/khoran","servlet","512256");
                DbConnectionManager.setConnection("khoran",dbc); 
            }catch(Exception e){
                log.error("could not connect to database: "+e.getMessage());
            }

        log.debug("fields.length="+fields.length);
    }
    /**
     * Makes the query distinct on accession_id
     * @param state 
     * @return 
     */
    public Query buildQueryTree(SearchState state)
    {          
        Query q=super.buildQueryTree(state);
        
        q.setDistinct(true);
        q.addDistinctField("general.accessions.accession_id");
        
        // all distinct fields must appear first in order
        Order o=q.getOrder();
        
        if(o.getOrder() instanceof DbField)
        {
            String oldOrder=((DbField)o.getOrder()).getName();
            o=new Order(new DbField("accessions.accession_id, "+oldOrder, String.class), "asc");
        }
        q.setOrder(o);
        return q;
    }        
    /**
     * adds the accessions table to every query.
     * TODO: this seems pointless, this table is already the root table.
     * @return 
     */
    protected List additionalJoins()
    {
        List j=new ArrayList(1);    
        j.add("general.accessions");
        return j;
    }
    void defineOptions()
    {   
        log.debug("defining options"); 
        rootTableName="general.accessions";
//        primaryKey="accession_id";
//        defaultColumn="accession";
        
//        rootTableName="unknowns.arab_accessions";
        primaryKey="accession_id";
        defaultColumn="accession";
        
        String db="unknowns.";        
        String space=" &nbsp&nbsp ";
        //as long as we only use fields from tables that have an 'accession_id' column,
        //we don't need any special cases in the query building code.
        
        String catagoryQuery="SELECT catagory FROM affy.catagory_list ORDER BY catagory";
        String databaseQuery="SELECT db_name FROM general.genome_database_list ORDER BY db_name";        
        String analysisQuery="SELECT type_name FROM affy.cel_analysis_type_list ORDER BY type_name";
        
        fields=new Field[]{
            new StringField("At key",db+"other_accessions_view.other_accession",true).setSortable(true),
            new StringField("Description","general.accessions.description").setSortable(true),
            new BooleanField("is model?","general.accessions.is_model"),
            new ListField("Genome","general.genome_databases_view.db_name",new String[]{"arab","rice"}).setSortable(true),
            new IntField("Number of ests",db+"unknown_data.est_count"),
                        
            new StringField("Blast/Pfam Searches (best per db)",""),                        
            new ListField(space+"database","general.blast_summary_mv.db_name",databaseQuery),                                     
            new ListField(space+"method","general.blast_summary_mv.method", new String[]{"BLASTP","hmmPfam"}),
            new StringField(space+"Blast target accession","general.blast_summary_mv.accession"),
            new StringField(space+"Blast target description","general.blast_summary_mv.description"),    
            new FloatField(space+"best e_value","general.blast_summary_mv.e_value"),       
            new StringField(space+"score","general.blast_summary_mv.score"),
            new StringField(space+"identities","general.blast_summary_mv.identities"),            
            
            new StringField("GO",""),
            new StringField(space+"number",db+"go_view.go_number",true),
            new StringField(space+"description",db+"go_view.text"),
            new ListField(space+"function",db+"go_view.function", new String[]{"process","component","function"}),
            new BooleanField(space+"Molecular function unknown?",db+"unknown_data.mfu").setSortable(true),
            new BooleanField(space+"Cellular component unknown?",db+"unknown_data.ccu").setSortable(true),
            new BooleanField(space+"Biological process unknown?",db+"unknown_data.bpu").setSortable(true),
                                            
            new StringField("Clusters",""),
            new ListField(space+"Method","general.clusters_and_info.method",
                        new String[]{"BLASTCLUST_35","BLASTCLUST_50","BLASTCLUST_70","Domain Composition"}),
            new IntField(space+"Size","general.clusters_and_info.size"),
            
            new StringField("Proteomic Stats",""),
            new StringField(space+"Molecular Weight",db+"proteomics_stats.mol_weight"),
            new StringField(space+"Isoelectric Point",db+"proteomics_stats.ip"),
            new StringField(space+"Charge",db+"proteomics_stats.charge"),
            new StringField(space+"Probability of expression in inclusion bodies",db+"proteomics_stats.prob_in_body"),
            new BooleanField(space+"Probability is negative",db+"proteomics_stats.prob_is_neg"),
                        
            new StringField("External Sources",""),
            new ListField(space+"Source",db+"external_unknowns.source",new String[]{"tigr","citosky"}),
            new BooleanField(space+"is unknown?",db+"external_unknowns.is_unknown"),
            
            new StringField("Affy Experiment Sets",""),
            new StringField(space+"Probe Set Key","affy.experiment_set_summary_view.probe_set_key",true),
            new StringField(space+"Experiment Set Key","affy.experiment_set_summary_view.experiment_set_key",true),            
            new ListField(space+"Catagory","affy.experiment_set_summary_view.catagory",catagoryQuery),
            new ListField(space+"Intensity type","affy.experiment_set_summary_view.data_type",analysisQuery),                                
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
            new IntField(space+"Comparison","affy.experiment_group_summary_view.comparison"),
            new FloatField(space+"Control mean","affy.experiment_group_summary_view.control_mean"),
            new IntField(space+"Control PMA","affy.experiment_group_summary_view.control_pma"),
            new FloatField(space+"Treatment mean","affy.experiment_group_summary_view.treatment_mean"),
            new IntField(space+"Treatment PMA","affy.experiment_group_summary_view.treatement_pma"),
            new FloatField(space+"Ratio (log_2(treat_mean/control_mean))","affy.experiment_group_summary_view.t_c_ratio_lg"),
            new FloatField(space+"Contrast","affy.experiment_group_summary_view.contrast"),
            new FloatField(space+"P-value","affy.experiment_group_summary_view.p_value"),
            new FloatField(space+"Adjusted P-value","affy.experiment_group_summary_view.adj_p_value"),
            new FloatField(space+"pfp up","affy.experiment_group_summary_view.pfp_up"),
            new FloatField(space+"pfp down","affy.experiment_group_summary_view.pfp_down"),
                                        
            new StringField("Correlations",""),
            new FloatField(space+"Correlation","affy.correlation_view.correlation"),
            new FloatField(space+"P Value","affy.correlation_view.p_value")
        };
        
        booleans=new String[]{"and","or"};                
    }
 
    /**
     * Sends a list of accession_ids to the unknowns2View dataview
     */
    protected ServletRequest getNewRequest(SearchState state,HttpServletRequest request,List results)
    { //this can be overridden by sub classes to send different parameters
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);  
        mRequest.getParameterMap().put("rpp",new Integer(rpp).toString());
                
        mRequest.getParameterMap().put("displayType","unknowns2View");
        mRequest.getParameterMap().put("origin_page","unknownsSearch.jsp");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        return mRequest;
    }

}
