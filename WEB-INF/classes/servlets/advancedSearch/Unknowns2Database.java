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
import servlets.advancedSearch.fields.* ;
import servlets.advancedSearch.fields.Field;


public class Unknowns2Database extends DefaultSearchableDatabase
{

    private static SearchTreeManager stm=new SearchTreeManager("Unknown2Database.properties");
    
    /** Creates a new instance of Unknowns2Database */
   
    public Unknowns2Database()
    {        
        super(DbConnectionManager.getConnection("khoran"),stm);
                
        if(dbc==null)
            log.error("could not connect to database, 'khoran' not set");

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
            new StringField("At key",db+"unknown_keys.key",true).setSortable(true),
            new StringField("Description",db+"unknown_keys.description").setSortable(true),
            new IntField("Number of ests",db+"unknown_keys.est_count").setSortable(true),
                        
            new StringField("Blast/Pfam Searches (best per db)",""),                        
            new ListField(space+"database",db+"blast_summary_view.db_name",             
                        new String[]{"swp","pfam","rice","yeast","human/rat/mouse"}),
            new ListField(space+"method",db+"blast_summary_view.method",
                        new String[]{"BLASTP","hmmPfam"}),
            new StringField(space+"Blast target accession",db+"blast_summary_view.target_accession"),
            new StringField(space+"Blast target description",db+"blast_summary_view.target_description"),    
            new FloatField(space+"best e_value",db+"blast_summary_view.e_value"),       
            new StringField(space+"score",db+"blast_summary_view.score"),
            new StringField(space+"identities",db+"blast_summary_view.identities"),            
            
            new StringField("GO",""),
            new StringField(space+"number",db+"go_view.go_number",true),
            new StringField(space+"description",db+"go_view.text"),
            new ListField(space+"function",db+"go_view.function",
                        new String[]{"process","component","function"}),
            new BooleanField(space+"Molecular function unknown?",db+"unknown_keys.mfu").setSortable(true),  //15
            new BooleanField(space+"Cellular component unknown?",db+"unknown_keys.ccu").setSortable(true),
            new BooleanField(space+"Biological process unknown?",db+"unknown_keys.bpu").setSortable(true),
                    
                        
            new StringField("Clusters",""),
            new ListField(space+"Score Threshold",db+"cluster_info_and_counts_view.cutoff",
                        new String[]{"35","50","70"}).setElementType(Integer.class),
            new IntField(space+"Size",db+"cluster_info_and_counts_view.size"),
            
            new StringField("Proteomic Stats",""),   //21         
            new StringField(space+"Molecular Weight",db+"proteomics_stats.mol_weight"),
            new StringField(space+"Isoelectric Point",db+"proteomics_stats.ip"),
            new StringField(space+"Charge",db+"proteomics_stats.charge"),
            new StringField(space+"Probability of expression in inclusion bodies",db+"proteomics_stats.prob_in_body"),
            new StringField(space+"Probability is negative",db+"proteomics_stats.prob_is_neg"),
                        
            new StringField("External Sources",""),  //27
            new ListField(space+"Source",db+"external_unknowns.source",new String[]{"tigr","citosky"}),
            new BooleanField(space+"is unknown?",db+"external_unknowns.is_unknown")
        };
        
        booleans=new String[]{"and","or"};                
    }
}
