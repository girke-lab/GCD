/*
 * Version2DataViewQuerySet.java
 *
 * Created on March 25, 2005, 3:18 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.advancedSearch.*;

public class DataViewQuerySetV2 implements DataViewQuerySet , RecordQuerySet , DatabaseQuerySet
{
    private static Logger log=Logger.getLogger(DataViewQuerySetV2.class);
    
    /** Creates a new instance of Version2DataViewQuerySet */
    public DataViewQuerySetV2()
    {
    }
    private void logQuery(String q)
    {
        log.info("query from is: "+q); // use reflection to get calling method here
    }
    
    
    public String getBlastDataViewQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        String query=
            "SELECT query.accession,target.accession,gd.link,target.description, " +
            "   o.name,br.e_value,br.score,br.identities,br.length " +
            "FROM general.blast_results as br, general.accessions as query, " +
            "   general.accessions as target LEFT JOIN general.organisms as o USING(organism_id), " +
            "   general.genome_databases as gd " +
            "WHERE br.query_accession_id=query.accession_id AND " +
            "   br.target_accession_id=target.accession_id AND " +
            "   target.genome_db_id=gd.genome_db_id AND " +
                Common.buildIdListCondition("br.blast_id",ids)+
            "ORDER BY query.accession, "+sortCol+" "+sortDir;
        logQuery(query);
        return query;
    }
    public String[] getSortableBlastColumns()
    {
        return new String[] { "target.accession","target.description",
                            "o.name","br.e_value","br.score","br.identities","br.length"};
    }
    public String getClusterDataViewQuery(java.util.Collection ids, String order, int[] DBs)
    {
        order=order.replaceAll("(general\\.)?cluster_sizes_by_model", "csm");
        String query="SELECT DISTINCT clusters.key, clusters.name,csm.arab_count," +
                "           csm.rice_count,csm.size,clusters.method " +
            " FROM general.clusters JOIN general.cluster_sizes_by_model as csm USING(cluster_id) " +
            " WHERE "+Common.buildIdListCondition("clusters.cluster_id",ids)+
            " ORDER BY "+order;        
        logQuery(query);
        return query;   
    }
    public String getUnknownsDataViewQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        String query="SELECT unknowns.*,treats.treat " +
            " FROM old_unknowns.unknowns LEFT JOIN old_unknowns.treats USING(unknown_id) " +
            " WHERE "+Common.buildIdListCondition("unknowns.unknown_id",ids)+
            " ORDER BY "+sortCol+" "+sortDir+",unknowns.unknown_id ";
        
        logQuery(query);
        return query;
    }
    public String[] getSortableUnknownsColumns()
    {
        return new String[]{
            "unknown_id",                   "At_Key ",
            "Description ",                 "Unknown_Method_TIGR ",
            "Unknown_Method_SWP_BLAST ",    "Unknown_Method_GO_MFU_OR_CCU_OR_BPU ",
            "Unknown_Method_GO_MFU ",       "Unknown_Method_InterPro ",
            "Unknown_Method_Pfam ",         "Citosky_Small_List ",
            "SALK_tDNA_Insertion ",         "EST_avail ",
            "avail ",                       "flcDNA_TIGR_XML_avail ",
            "Nottingham_Chips_3x_90 ",      "Rice_Orth_Evalue ",
            "HumanRatMouse_Orth_Evalue ",   "S_cerevisiae_Evalue ",
            "Gene_Family_Size_35_50_70_perc_ident ",    "Pet_Gene_from ",
            "Targeting_Ipsort ",            "Targeting_Predotar ",
            "Targeting_Targetp ",           "Membr_dom_Hmmtop ",
            "Membr_dom_Thumbup ",           "Membr_dom_TMHMM ",
            "Focus_list_of_grant ",         "Selected_by ",
            "Multiple_selects ",            "Occurrence_in_treaments",
            "treat"        
        };  
    }

    public String getModelDataViewQuery(java.util.Collection ids, String fields)
    {        
        String query="SELECT "+fields+
                " FROM general.genome_databases " +
                "   LEFT JOIN general.accessions USING(genome_db_id) " +
                "   LEFT JOIN common.sequence_data USING(accession_id) " +
                "   LEFT JOIN common.model_data ON(sequence_data.accession_id=model_data.sequence_accession_id) " +
                "   LEFT JOIN general.accessions as models ON(model_data.accession_id=models.accession_id) " +
                " WHERE "+Common.buildIdListCondition("accessions.accession_id",ids)+
                " ORDER BY genome_databases.db_name, "+getModelColumns()[0]+","+getModelColumns()[1];
        
        logQuery(query);
        return query;
    }
    public String[] getModelColumns()
    {
         return new String[]{        
            "accessions.accession",     "models.accession",
            "accessions.description",   "model_data.tu",
            "sequence_data.intergenic", "model_data.utr3",
            "sequence_data.intergenic", "model_data.cds",
            "model_data.utr5",          "model_data.protein",
            "genome_databases.db_name"};
    }
   
    public String getSeqDataViewQuery(java.util.Collection ids, String order, int[] DBs)
    {
        StringBuffer query=new StringBuffer();
        //account for local aliases
        order=order.replaceAll("general.accessions.accession","amg.accession");
        order=order.replaceAll("general.accessions.description","amg.description");
        
        query.append(
                " SELECT amg.db_name, amg.accession, amg.description, " +
                "       amg.model_accession, amg.go_number, clusters.key, csm.size, clusters.name, " +
                "       csm.arab_count, csm.rice_count, clusters.method, NULL as v3Key "+
                " FROM" +
                "        (SELECT a.accession_id, a.accession,a.description, a.is_model, models.accession as model_accession," +
                "                gd.db_name, go_numbers.go_number, models.accession_id as model_accession_id" +
                "        FROM" +
                "                general.genome_databases as gd JOIN general.accessions as a USING(genome_db_id)" +
                "                LEFT JOIN common.model_data as md ON (a.accession_id=md.sequence_accession_id)" +
                "                LEFT JOIN general.accessions as models ON(md.accession_id=models.accession_id)" +
                "                LEFT JOIN general.accession_gos as ag ON(ag.accession_id=a.accession_id)" +
                "                LEFT JOIN general.go_numbers ON(ag.go_id=go_numbers.go_id)" +
                "        ) as amg" +
                "        LEFT JOIN general.cluster_members as cm ON(amg.model_accession_id=cm.accession_id)" +
                "        LEFT JOIN general.clusters ON (cm.cluster_id=clusters.cluster_id)" +
                "        LEFT JOIN general.cluster_sizes_by_model as csm ON(clusters.cluster_id=csm.cluster_id)" +
                " WHERE   " 
                );                
                
        query.append("  (");
        for(int i=0;i<DBs.length;i++)
        {
            query.append("amg.db_name='"+Common.dbRealNames[DBs[i]]+"' ");
            if(i+1 < DBs.length)
                query.append(" or ");
        }
        
        query.append(") AND ( "+Common.buildIdListCondition("amg.accession_id",ids)+" ) ");
        query.append("ORDER BY amg.db_name,");
        if(order!=null && !order.equals(""))
            query.append(order+", ");
        //query.append(" sequence_view.primary_key,clusters.model_id, go.go,cluster_info.filename ");        
        query.append(" amg.accession,clusters.key, amg.go_number ");        
        logQuery(query.toString());
        return query.toString();
    }

 

    
    ///////////////////////////////
    //////// RecordQuerySet methods
    ///////////////////////////////
    String uSchema="unknowns";
    public String getUnknownRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="accession";
         String query="SELECT 1, accessions.accession_id, accessions.accession, accessions.description, " +
                 " unknown_data.est_count, unknown_data.mfu, unknown_data.ccu, unknown_data.bpu, " +
                 " unknown_data.version" +
        "   FROM general.accessions JOIN "+uSchema+".unknown_data USING(accession_id) " +
        "   WHERE "+Common.buildIdListCondition("accessions.accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
                
        logQuery(query);
        return query;
    }

    public String getBlastRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
         if(sortCol==null)
            sortCol="purpose";
        String query="SELECT * " +
        "   FROM "+uSchema+".blast_summary_view " +
        "   WHERE "+Common.buildIdListCondition("accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }

    public String getClusterRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="name";
        String query="SELECT accession_id, name, size, 'cutoff', cluster_id "+                 
        "   FROM "+uSchema+".cluster_info_and_counts_view " +        
        "   WHERE "+Common.buildIdListCondition("accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
        logQuery(query);
        return query;
    }

    public String getExternlUnknwownsRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="source";
        String query="SELECT accession_id,is_unknown,source "+
        "   FROM "+uSchema+".external_unknowns " +
        "   WHERE "+Common.buildIdListCondition("accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }

    public String getGoRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="go_numbers.go_number";
        
//        select seq.accession_id, seq.accession,gn.go_number 
//        from common.model_data as md JOIN general.accessions as seq ON(md.sequence_accession_id=seq.accession_id) JOIN 
//                general.accession_gos as ag ON(seq.accession_id=ag.accession_id) JOIN general.go_numbers as gn USING(go_id) 
//                where md.accession_id=236;

        String query="SELECT accession_gos.accession_id,go_numbers.go_number, go_numbers.function,go_numbers.text " +
                " FROM common.model_data as md JOIN general.accessions as seq ON(md.sequence_accession_id=seq.accession_id) " +
                "   JOIN general.accession_gos ON(seq.accession_id=accession_gos.accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                " WHERE "+Common.buildIdListCondition("md.accession_id",ids)+
                " ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }

    public String getProteomicsRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        String query="SELECT * "+
        "   FROM "+uSchema+".proteomics_stats " +
        "   WHERE "+Common.buildIdListCondition("accession_id",ids);

        logQuery(query);
        return query;
    }
   
    /////////////////////////////////
    //////// DatabaseQuerySet methods
    /////////////////////////////////
    public servlets.advancedSearch.SearchableDatabase getCommonDatabase()
    {
        return new CommonDatabaseV2();
    }
    public servlets.advancedSearch.SearchableDatabase getUnknowns2Database()
    {
        return new Unknowns2DatabaseV2();
    }
    public servlets.advancedSearch.SearchableDatabase getUnknownsDatabase()
    {
        return new UnknownsDatabase();
    }    
}
