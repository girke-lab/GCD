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
    
    void defineOptions()
    {   
        log.debug("defining options"); 
        rootTableName="general.accessions";
        primaryKey="accession_id";
        defaultColumn="accession";
        
        String db="unknowns.";        
        String space=" &nbsp&nbsp ";
        //as long as we only use fields from tables that have a 'key_id' column,
        //we don't need any special cases in the query building code.
        
        fields=new Field[]{
            new Field("At key","general.accessions.accession_id",List.class),
            new Field("Description","general.accessions.description"),
            new Field("Number of ests",db+"unknown_data.est_count",Integer.class),
                        
            new Field("Blast/Pfam Searches (best per db)",""),                        
            new Field(space+"database",db+"blast_summary_view.db_name",             
                        new String[]{"swp","pfam","rice","yeast","human/rat/mouse"}),
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
            new Field(space+"Molecular function unknown?",db+"unknown_data.mfu",  //15
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field(space+"Cellular component unknown?",db+"unknown_data.ccu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
            new Field(space+"Biological process unknown?",db+"unknown_data.bpu",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
                    
                        
            new Field("Clusters",""),
            new Field(space+"Score Threshold",db+"cluster_info_and_counts_view.cutoff",Integer.class,
                        new String[]{"35","50","70"}),
            new Field(space+"Size",db+"cluster_info_and_counts_view.size",Integer.class),
            
            new Field("Proteomic Stats",""),   //21         
            new Field(space+"Molecular Weight",db+"proteomics_stats.mol_weight"),
            new Field(space+"Isoelectric Point",db+"proteomics_stats.ip"),
            new Field(space+"Charge",db+"proteomics_stats.charge"),
            new Field(space+"Probability of expression in inclusion bodies",db+"proteomics_stats.prob_in_body"),
            new Field(space+"Probability is negative",db+"proteomics_stats.prob_is_neg",
                        Boolean.class,new String[]{"TRUE","FALSE"}),
                        
            new Field("External Sources",""),  //27
            new Field(space+"Source",db+"external_unknowns.source",new String[]{"tigr","citosky"}),
            new Field(space+"is unknown?",db+"external_unknowns.is_unknown",Boolean.class,
                        new String[]{"TRUE","FALSE"})
        };
//new Field(space+"",""),
        
        int[] sortableFields=new int[]{0,1,2,11,15,16,17}; //,21,22,23,24,25,26,27,28,29};
        for(int i=0;i<sortableFields.length;i++)
            fields[sortableFields[i]].setSortable(true);
        
        operators=new String[]{"=","!=","<",">","<=",">=",
                "ILIKE","NOT ILIKE","IS NULL","IS NOT NULL"};
        unaryBoundry=8; //index of first unary op.
        booleans=new String[]{"and","or"};                
    }
    
}
