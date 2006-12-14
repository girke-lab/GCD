/*
 * Version2DataViewQuerySet.java
 *
 * Created on March 25, 2005, 3:18 PM
 */

package servlets.querySets;

import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.KeyTypeUser;
import servlets.advancedSearch.*;
import servlets.dataViews.AffyKey;
import static servlets.KeyTypeUser.*;

/**
 * This class implements all the different QuerySet objects to
 * provide an implementation for version 2 of the databse (now the current db).
 * @author khoran
 */



public class V2QuerySets implements DataViewQuerySet , RecordQuerySet , DatabaseQuerySet, ScriptQuerySet, SearchQuerySet
{
    private static Logger log=Logger.getLogger(V2QuerySets.class);
    
    /** Creates a new instance of Version2DataViewQuerySet */
    public V2QuerySets()
    {
    }
    private void logQuery(String q)
    {

//        log.info("query from "+source+" is: "+q); // use reflection to get calling method here
    }
    
    // <editor-fold defaultstate="collapsed" desc=" DataView queries ">
    public String getBlastDataViewQuery(java.util.Collection ids, String sortCol, String sortDir, KeyType keyType)
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
    public String getClusterDataViewQuery(java.util.Collection ids, String order, int[] DBs, KeyType keyType)
    {
        order=order.replaceAll("(general\\.)?cluster_sizes_by_model_mv", "csm");
        String query="SELECT DISTINCT clusters.key, clusters.name,csm.arab_count," +
                "           csm.rice_count,csm.size,clusters.method " +
            " FROM general.clusters JOIN general.cluster_sizes_by_model_mv as csm USING(cluster_id) " +
            " WHERE "+Common.buildIdListCondition("clusters.cluster_id",ids)+
            " ORDER BY "+order;        
        logQuery(query);
        return query;   
    }
    public String getUnknownsDataViewQuery(java.util.Collection ids, String sortCol, String sortDir, KeyType keyType)
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

    public String getModelDataViewQuery(java.util.Collection ids, String fields, KeyType keyType)
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
   
    public String getSeqDataViewQuery(java.util.Collection ids, String order, int[] DBs, KeyType keyType)
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
                "        LEFT JOIN general.cluster_sizes_by_model_mv as csm ON(clusters.cluster_id=csm.cluster_id)" +
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
    public String getPfamOptionsQuery(String clusterKey)
    {
        String query=" SELECT DISTINCT ON(d.key) d.key,d.description " +
                "FROM general.clusters as c JOIN general.cluster_domains as cd USING(cluster_id)" +
                "       JOIN general.domains as d USING(domain_id) "+
                "WHERE c.key='"+clusterKey+"'" +
                "ORDER BY d.key";
        logQuery(query);
        return query;
    }
    public String getDiffTrackingDataViewQuery()
    {
        String query=
            "SELECT" +
            "   vi.version," +
            "   q.queries_id, q.name,q.purpose," +
            "   q.description, q.link," +
            "   qc.count," +
            "   csv.version_a,vi.updated_on,csv.added,csv.removed,csv.unchanged,csv.comp_id, " +
            "   gd.db_name, gd.genome_db_id "+
            " FROM    updates.queries as q" +
            "   JOIN updates.query_counts as qc USING(queries_id)" +
            "   JOIN unknowns.version_info as vi USING(version)" +
            "   JOIN general.genome_databases as gd USING(genome_db_id) "+
            "   LEFT JOIN updates.comparison_summary_view as csv" +
            "       ON(qc.version=csv.version_b AND qc.genome_db_id=csv.genome_db_id_b)" +
            " WHERE   (csv.queries_id_a is null OR qc.queries_id=csv.queries_id_a )" +
            " ORDER BY qc.genome_db_id,vi.version desc,q.queries_id ";
        logQuery(query);
        return query;
    }
    public String getDiffStatsQuery()
    {
        String query="SELECT * FROM updates.unknowns_stats_mv " +
                " WHERE db_name!='arab rice'  "+
                " ORDER BY length(name), db_name";
        logQuery(query);
        return query;
    }
    
    public String[][] getSortableAffyColumns()
    {
        return new String[][] {
                //experiment_set_summary_mv fields used
                {"probe_set_key","experiment_set_key","name","up2x","down2x","up4x",
                         "down4x","pma_on","pma_off","control_average","control_stddev",
                         "treatment_average","treatment_stddev"},
                //experiment_group_summary_mv fields used
                {"comparison","control_mean","treatment_mean","control_pma",
                         "treatment_pma","t_c_ratio_lg",
                         "contrast","p_value","adj_p_value","pfp_up","pfp_down"},
                // detail_view fields used
                {"type_name","file_name","intensity","pma"}
            };
    }
    public String[] getSortableCorrelationColumns()
    {
        return new String[] {
            "psk2_key","pearson", "spearman"
        };
    }
    
    public String[][] getSortableTreatmentColoumns()
    {
        return new String[][]{
            {"exp_set_key","comparison","control_desc","treatement_desc"},
            {"probe_set_key","accessions","control_mean",
                "treatment_mean","control_pma","treatment_pma","t_c_ratio_lg",
                "contrast","p_value","adj_p_value","pfp_up","pfp_down",
                "cluster_names","acc_descriptions"}            
        };
    }
    public String getCompCountDataViewQuery(String userName)
    {
        if(userName==null || userName.equals(""))
            userName="public";
        String query="SELECT * FROM affy.comparison_counts " +
                " WHERE user_name='"+userName+"'";
        logQuery(query);
        return query;
    }
    
    //   </editor-fold> 

    // <editor-fold defaultstate="collapsed" desc=" Record queries "> 
    ///////////////////////////////
    //////// RecordQuerySet methods
    ///////////////////////////////
    String uSchema="unknowns";
    public String getUnknownRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        log.debug("sort col for unknown data query:"+sortCol+":");
        if(sortCol==null || sortCol.equals(""))
            sortCol="accession";
        else if(sortCol.equals("unknowns.other_accessions_view.other_accession"))
            sortCol="accessions.accession";
        else if(sortCol.equals("unknowns.arab_accessions.description"))
            sortCol="accessions.description";
        
        String query="SELECT DISTINCT  " +
                 " ma.model_accession_id, accessions.accession, accessions.description, " +
                 " unknown_data.est_count, unknown_data.mfu, unknown_data.ccu, unknown_data.bpu, " +
                 //" unknown_data.version" +
                 " cbp.probe_set_key_id, cbp.cluster_ids, cbp.cluster_names, cbp.methods, cbp.sizes "+
        "   FROM general.accessions " +        
        "       JOIN general.to_model_accessions as ma ON(accessions.accession_id=ma.model_accession_id) " +
        "       JOIN "+uSchema+".unknown_data ON(ma.model_accession_id=unknown_data.accession_id) " +
                
        "       JOIN general.to_sequence_accessions as sa ON(ma.accession_id=sa.accession_id) "+
        "       LEFT JOIN affy.psk_to_accession_view as pska ON(sa.sequence_accession_id=pska.accession_id) "+
        "       LEFT JOIN affy.clusters_by_psk as cbp USING(probe_set_key_id) "+
        "   WHERE "+Common.buildIdListCondition("ma.accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
                
        logQuery(query);
        return query;
    }

    public String getBlastRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
         if(sortCol==null)
            sortCol="purpose";
        String query="SELECT DISTINCT  blast_id, ma.model_accession_id, accession, description, " +
                " e_value, score, identities, length, positives, gaps, db_name, link, method, purpose" +
//                " CASE WHEN purpose is null THEN '' ELSE purpose END    "+                
        "   FROM general.blast_summary_mv " +
                "JOIN general.to_model_accessions as ma ON(blast_summary_mv.accession_id=ma.model_accession_id)" +
        "   WHERE purpose is not null AND "+Common.buildIdListCondition("ma.accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }

    public String getClusterRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="method";
        String query="SELECT DISTINCT ma.model_accession_id, name, size, method, cluster_id, key "+                 
        "   FROM general.clusters_and_info " +
                "JOIN general.to_model_accessions as ma ON(clusters_and_info.accession_id=ma.model_accession_id)" +        
        "   WHERE "+Common.buildIdListCondition("ma.accession_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
        logQuery(query);
        return query;
    }

    public String getExternlUnknwownsRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="source";
        String query="SELECT DISTINCT ma.model_accession_id,is_unknown,source,external_id "+
        "   FROM "+uSchema+".external_unknowns " +
                "JOIN general.to_model_accessions as ma ON(external_unknowns.accession_id=ma.model_accession_id)" +
        "   WHERE "+Common.buildIdListCondition("ma.accession_id",ids)+
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

        String query="SELECT DISTINCT ma.model_accession_id, go_numbers.go_number, go_numbers.function,go_numbers.text,go_numbers.go_id " +
                " FROM   general.to_sequence_accessions as sa "+
                "   JOIN general.accession_gos ON(sa.sequence_accession_id=accession_gos.accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                "   JOIN general.to_model_accessions as ma ON(sa.sequence_accession_id=ma.accession_id) "+
                " WHERE "+Common.buildIdListCondition("sa.accession_id",ids)+
                " ORDER BY go_numbers.function, "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }

    public String getProteomicsRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        String query="SELECT DISTINCT ma.model_accession_id, mol_weight, ip, charge, prob_in_body, prob_is_neg,prot_stats_id "+
        "   FROM "+uSchema+".proteomics_stats " +
                "JOIN general.to_model_accessions as ma ON(proteomics_stats.accession_id=ma.model_accession_id)" +
        "   WHERE "+Common.buildIdListCondition("ma.accession_id",ids);

        logQuery(query);
        return query;
    }
    public String getAffyDetailRecordQuery(Collection affyKeys, String dataType, boolean allGroups, String sortcol, String sortDir)
    {                        
        if(sortcol!=null && sortcol.startsWith("detail_"))
            sortcol=sortcol.replaceFirst("detail_", "");
        else
            sortcol="type_name";
        
        String query="SELECT * FROM " +
                " (SELECT DISTINCT ON (probe_set_key_id,experiment_set_id,group_no,type_name,file_name) * " +
                "   FROM affy.detail_view " +
                "   WHERE ("+AffyKey.buildIdSetCondition(affyKeys, !allGroups) +") "+
                "           AND data_type='"+dataType+"' "+                
                " ) as t " +
                "ORDER BY "+sortcol+" "+sortDir;
                
        
//        String query="SELECT DISTINCT ON (sequence_accession_id,file_name) * FROM affy.detail_view " +
//                " WHERE "+ AffyKey.buildIdSetCondition(affyKeys, !allGroups) +                    
//                " ORDER BY sequence_accession_id, file_name, "+
//                    sortCol+" "+sortDir;
        logQuery(query);
        return query;
    }
    public String getAffyCompRecordQuery(Collection affyKeys, String dataType, String sortcol, String sortDir)
    {
        if(sortcol!=null && sortcol.startsWith("comp_"))
            sortcol=sortcol.replaceFirst("comp_", "");
        else
            sortcol="comparison";
//        String feilds="probe_set_key_id, probe_set_key, " +
//                "experiment_set_id, experiment_set_key, description, comparison, " +
//                "control_mean, treatment_mean, control_pma, treatment_pma, t_c_ratio_lg";
//        String feilds="probe_set_key_id, probe_set_key, " +
//                "experiment_set_id, experiment_set_key, comparison, " +
//                "control_mean, treatment_mean, control_pma, treatment_pma, t_c_ratio_lg";

        String query="SELECT * FROM "+                
                "   (SELECT DISTINCT ON (probe_set_key_id, experiment_set_id, comparison)  * " +
                    "       FROM affy.experiment_group_summary_view " +
                "       WHERE ("+ AffyKey.buildIdSetCondition(affyKeys,false) +") "+
                "               AND data_type='"+dataType+"' "+
                "       ORDER BY probe_set_key_id, experiment_set_id, comparison  "+
                "   ) as t "+
                "ORDER BY "+ sortcol+" "+sortDir;    
        
//        String query=
//                "SELECT   * " +
//                    "       FROM affy.experiment_group_summary_view " +
//                "       WHERE ("+ AffyKey.buildIdSetCondition(affyKeys,false) +") "+
//                "               AND data_type='"+dataType+"' "+                
//                "ORDER BY "+ sortcol+" "+sortDir;    
        logQuery(query);
        return query;
    }            
    public String getAffyExpSetRecordQuery(Collection ids, String dataType, String sortcol, String sortDir, String userName)
    {
        log.debug("sortCol="+sortcol);        
        
        if(sortcol!=null && sortcol.startsWith("expset_"))
            sortcol=sortcol.replaceFirst("expset_", "");
        else 
            sortcol="catagory, probe_set_key_id"; 
        
//        String query="SELECT DISTINCT ess.* FROM " +
//                "       affy.experiment_set_summary_mv as ess JOIN " +
//                "       affy.es_valid_users as evu USING(experiment_set_id)"+
//                " WHERE ("+Common.buildIdListCondition("ess.accession_id",ids)+") "+
//                "               AND ess.data_type='"+dataType+"' "+
//                "               AND evu.user_name='"+userName+"' "+
//                " ORDER BY "+sortcol+" "+sortDir; 
        
        String query="SELECT ess.* FROM " +
                "       affy.experiment_set_summary_view as ess JOIN " +
                "       affy.es_valid_users as evu USING(experiment_set_id)"+
                " WHERE ("+Common.buildIdListCondition("ess.accession_id",ids)+") "+
                "               AND ess.data_type='"+dataType+"' "+
                "               AND evu.user_name='"+userName+"' "+
                " ORDER BY "+sortcol+" "+sortDir; 
        
        logQuery(query);
        return query;        
    }
    public String getProbeSetSummaryRecordQuery(Collection ids)
    {
        String query="SELECT * FROM affy.psk_by_model_mv " +                
                " WHERE "+Common.buildIdListCondition("accession_id",ids)+
                " ORDER BY key";
        logQuery(query);
        return query;
    }
    public String getCorrelationRecordQuery(Collection ids, String sortCol, String sortDir, String catagory)
    {
        String bottomValue="'infinity'";
        String order;
        
        if(sortCol!=null && sortCol.startsWith("corr_"))
            sortCol=sortCol.replaceFirst("corr_", "");
        else
            sortCol="pearson";
        
        if(catagory==null || catagory.equals(""))
            catagory="All";            
        if(sortDir!=null && sortDir.equals("desc"))
            bottomValue="'-infinity'";
        
        if(sortCol!=null && sortCol.equals("psk2_key"))
            order=sortCol+" "+sortDir;
        else
            order="CASE catagory='"+catagory+"' WHEN TRUE THEN "+sortCol+ 
                  "   ELSE "+bottomValue+" END "+sortDir; 
        
        String query="SELECT * FROM affy.correlation_view "+
                " WHERE "+Common.buildIdListCondition("correlation_id",ids)+
                " ORDER BY probe_set_key_id,  " +order;
                
                //"catagory, "+sortCol+" "+sortDir;        
        logQuery(query);
        return query;
    }
    public String getAffyExpDefRecordQuery(Collection ids)
    { //not currently used
        String query="SELECT * FROM affy.experiment_definitions " +
                "   WHERE "+Common.buildIdListCondition("experiment_name",ids,true);
        
        logQuery(query);
        return query;
    }
    public String getComparisonPskRecordQuery(Collection pskIds, Collection comparisonIds, 
            String sortCol, String sortDir, String userName,String dataType)
    {
        throw new UnsupportedOperationException("comparisonPsk record is deprecated");
        
//        if(sortCol!=null && sortCol.startsWith("treatment_"))
//            sortCol=sortCol.replaceFirst("treatment_", "");
//        else
//            sortCol="comparison";
//       
//        String query="SELECT * "+                
//                "       FROM affy.psk_summary_view " +
//                "       WHERE ("+ Common.buildIdListCondition("probe_set_key_id",pskIds)+
//                "           AND "+Common.buildIdListCondition("comparison_id",comparisonIds)+") "+                                                
//                "           AND data_type='"+dataType+"' "+
//                "ORDER BY "+ sortCol+" "+sortDir;           
//        logQuery(query);
//        return query;        
    }
    public String getComparisonRecordQuery(Collection comparisonIds, String sortCol, String sortDir, String userName, String dataType)
    {
        if(sortCol!=null && sortCol.startsWith("comparison_"))
            sortCol=sortCol.replaceFirst("comparison_", "");
        else
            sortCol="comparison_id";
        
        String query="SELECT comparison_view.* FROM affy.comparison_view " +    
                "       JOIN affy.es_valid_users USING(experiment_set_id) " +
                "   WHERE user_name='"+userName+"' AND "+
                        Common.buildIdListCondition("comparison_id",comparisonIds)+                    
                "   ORDER BY  "+sortCol+" "+sortDir; //comparison_id
        
        logQuery(query);
        return query;
    }

    public String getProbeSetKeyRecordQuery(Collection pskIds,Collection compIds, String sortCol, String sortDir, String dataType)
    {        
        log.debug("psk record sortCol: "+sortCol);
        if(sortCol!=null && sortCol.startsWith("psk_"))
        {
            sortCol=sortCol.replaceFirst("psk_", "");
            sortCol=getFieldName(sortCol);
        }
        else
            sortCol="comparison_id";
        StringBuilder condition=new StringBuilder(pskIds.size()*10); //rough guess of lenth size
        
        condition.append("(probe_set_key_id,comparison_id) IN ( ");
        
        Iterator pskItr=pskIds.iterator();
        Iterator compItr=compIds.iterator();
        while(pskItr.hasNext() && compItr.hasNext())        
            condition.append("("+pskItr.next()+","+compItr.next()+"),");
        
        condition.append("(-1,-1) )"); //just to make the last comma work.
        
        String query="SELECT * "+                
                "       FROM affy.psk_summary_view " +
                "       WHERE "+   condition+
//                Common.buildIdListCondition("probe_set_key_id",pskIds)+                
//                "           AND "+Common.buildIdListCondition("comparison_id",compIds)+
                "           AND data_type='"+dataType+"' "+
                " ORDER BY comparison_id, "+ sortCol+" "+sortDir;
                
        logQuery(query);
        return query;
    }

// </editor-fold>
   
    // <editor-fold defaultstate="collapsed" desc=" Database methods ">
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
    public SearchableDatabase getTreatmentDatabase(String userName)
    {
        return new TreatmentDatabase(userName);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Script queries ">
    /////////////////////////////////
    //////// ScriptQuerySet methods
    /////////////////////////////////
    public String getAlignToHmmQuery(Collection ids, int limit)
    {
        String query="SELECT a.accession, md.protein " +
                " FROM general.accessions as a JOIN common.model_data as md USING(accession_id) " +
                " WHERE "+Common.buildIdListCondition("md.sequence_accession_id",ids)+
                " ORDER BY a.accession LIMIT "+limit;
        logQuery(query);
        return query;
    }

    public String getChrPlotQuery(Collection ids)
    {
        String query="SELECT accession FROM general.accessions " +
                " WHERE "+Common.buildIdListCondition("accession_id",ids);
        logQuery(query);
        return query;
    }

    public String getDisplayKeysQuery(Collection ids)
    {
        String query="SELECT accession FROM general.accessions " +
                " WHERE "+Common.buildIdListCondition("accession_id",ids);
        logQuery(query);
        return query;
    }

    public String getGoSlimCountsQuery(Collection ids)
    {
        String query="SELECT a.accession, gn.go_number " +
                " FROM general.accessions as a JOIN common.sequence_data as sd USING(accession_id)" +
                "   JOIN general.accession_gos as ag ON(ag.accession_id=sd.accession_id) " +
                "   JOIN general.go_numbers as gn ON(gn.go_id=ag.go_id) " +
                " WHERE "+Common.buildIdListCondition("sd.accession_id",ids);
        logQuery(query);
        return query;
    }

    public String getMultigeneQuery(Collection ids, int limit)
    {
        String query="SELECT accession FROM general.accessions " +
                " WHERE "+Common.buildIdListCondition("accession_id",ids)+
                " LIMIT "+limit;
        logQuery(query);
        return query;
    }

    public String getTreeViewQuery(String clusterId)
    {
        String query="SELECT c.method, a.accession " +
                " FROM general.clusters as c JOIN general.cluster_members as cm USING(cluster_id) " +
                "   JOIN general.accessions as a USING(accession_id) " +
                " WHERE c.key='"+clusterId+"'";
        logQuery(query);
        return query;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Search queries ">
     /////////////////////////////////
    //////// SearchQuerySet methods
    /////////////////////////////////
    public String getBlastSearchQuery(Collection dbNames, java.util.Collection keys, KeyType keyType)
    {
        String query=
            "SELECT br.blast_id " +
            "FROM general.blast_results as br, general.accessions as query, " +
            "   general.accessions as target, general.genome_databases as gd " +
            "WHERE "+Common.buildIdListCondition("gd.db_name",dbNames,true)+" and gd.genome_db_id=target.genome_db_id and " +
            "   query.accession_id=br.query_accession_id AND target.accession_id=br.target_accession_id AND "+
                Common.buildIdListCondition("query.accession",keys,true);
        logQuery(query);
        return query;
    }

    public String getClusterIDSearchQuery(java.util.Collection input, int limit, int[] DBs, KeyType keyType)
    {
        String translateTable="sequence";
        if(keyType==KeyType.MODEL) 
            translateTable="model";
        String q="SELECT distinct "+translateTable+"_accession_id,clusters.key, genome_databases.db_name " +
                " FROM    general.clusters" +
                "        JOIN general.cluster_members USING(cluster_id)" +
                "        JOIN general.to_"+translateTable+"_accessions USING(accession_id)" +
                "        JOIN general.accessions ON(accessions.accession_id="+translateTable+"_accession_id)" +
                "        JOIN general.genome_databases USING(genome_db_id)" +
                " WHERE  (";                       
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondition("clusters.key",input,limit)+")";
        q+=" order by genome_databases.db_name ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getClusterNameSearchQuery(java.util.Collection input, int limit, int[] DBs, KeyType keyType)
    {
        String q="SELECT distinct accessions.accession_id, genome_databases.db_name " +
                " FROM  general.clusters " +
                "       JOIN general.cluster_members USING(cluster_id) " +
                "       JOIN general.to_sequence_accessions USING(accession_id) " +
                "       JOIN general.accessions ON(to_sequence_accessions.sequence_accession_id=accessions.accession_id) " +
                "       JOIN general.genome_databases USING(genome_db_id) " +
                " WHERE NOT accessions.is_model AND (";
                
//                "FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
//                "   JOIN general.clusters USING(cluster_id) JOIN general.genome_databases USING(genome_db_id) " +
//                "WHERE accessions.is_model=FALSE AND (";                       
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildDescriptionCondition("clusters.name",input)+")";
        q+=" order by genome_databases.db_name ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getDescriptionSearchQuery(java.util.Collection input, int limit, int[] DBs, KeyType keyType)
    {        
        String isModel="FALSE";
        if(keyType==KeyType.MODEL)
            isModel="TRUE";
        String id="SELECT DISTINCT accessions.accession_id, genome_databases.db_name " +
                "FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "WHERE accessions.is_model="+isModel+" AND (";
        for(int i=0;i<DBs.length;i++)
        {
            id+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+Common.buildDescriptionCondition("accessions.description",input)+")";
        id+=" order by genome_databases.db_name ";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getGoSearchQuery(java.util.Collection input, int limit, KeyType keyType)
    {
        String query;
        if(keyType==KeyType.MODEL)
            query="SELECT DISTINCT model_accession_id, go_numbers.go_number, " +
                "       genome_databases.db_name" +
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "   JOIN general.accession_gos USING(accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                "   JOIN general.to_model_accessions USING(accession_id) "+
                " WHERE "+Common.buildIdListCondition("go_numbers.go_number",input,true,limit)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        else        
            query="SELECT DISTINCT accessions.accession_id, go_numbers.go_number, " +
                "       genome_databases.db_name" +
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "   JOIN general.accession_gos USING(accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                " WHERE "+Common.buildIdListCondition("go_numbers.go_number",input,true,limit)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        logQuery(query);
        return query;
    }

    public String getGoTextSearchQuery(java.util.Collection input, int limit, KeyType keyType)
    {
        String query;
        if(keyType==KeyType.MODEL)
            query="SELECT DISTINCT to_model_accessions.model_accession_id, genome_databases.db_name" +
                " FROM general.genome_databases JOIN general.accessions USING(genome_db_id) "+
                "   JOIN general.accession_gos USING(accession_id)" +
                "   JOIN general.go_numbers USING(go_id) " +
                "   JOIN general.to_model_accessions USING(accession_id)"+
                " WHERE "+Common.buildDescriptionCondition("go_numbers.text",input)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        else         
            query="SELECT DISTINCT accessions.accession_id, genome_databases.db_name" +
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "   JOIN general.accession_gos USING(accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                " WHERE "+Common.buildDescriptionCondition("go_numbers.text",input)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        logQuery(query);
        return query;
    }

    public String getIdSearchQuery(java.util.Collection input, int limit, int[] DBs, KeyType keyType)
    {        
        if(keyType==KeyType.CORR)
        {
            String query="SELECT correlation_id, accession, probe_set_key " +
                    " FROM affy.accessions_to_psk_corr "+
                    " WHERE "+Common.buildLikeCondition("accession",input)+
                    " ORDER BY probe_set_key ASC, correlation DESC"+
                  " LIMIT "+limit;
            logQuery(query);
            return query;
        }
        
        
        String translateTable="sequence";        
        if(keyType==KeyType.MODEL)
            translateTable="model";
        String id="SELECT DISTINCT a.accession_id, oa.other_accession, gd.db_name "+
                " FROM  general.other_accessions as oa " +
                "       JOIN general.to_"+translateTable+"_accessions sa USING(accession_id) " +
                "       JOIN general.accessions as a ON(sa."+translateTable+"_accession_id=a.accession_id) " +
                "       JOIN general.genome_databases as gd USING(genome_db_id) " +
                " WHERE  ( ";
                
//                " FROM general.genome_databases " +
//                "       JOIN general.accessions USING(genome_db_id) " +
//                "       JOIN general.other_accessions USING(accession_id) "+
//                " WHERE NOT accessions.is_model AND (";
                
//                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
//                " WHERE accessions.is_model=FALSE AND (";
        for(int i=0;i<DBs.length;i++)
        {
            id+=" gd.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        String condition;
        if(input.size()!=0 && ((String)input.iterator().next()).equals("exact") )
        {//if first element is 'exact', then use an exact match condition, which is a bit faster.
            Iterator i=input.iterator();
            i.next();
            i.remove(); //remove first elemtent
            condition=Common.buildIdListCondition("oa.other_accession",input,true,limit);
        }
        else
            condition=Common.buildLikeCondition("oa.other_accession",input,limit);
        
        
        id+=") and ("+condition+")";
        id+=" order by gd.db_name";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getQueryCompSearchQuery(String comp_id, String status, KeyType keyType)
    {
        String query;
        if(keyType==KeyType.MODEL)
            query="SELECT model_accession_id " +
                  "FROM updates.diffs JOIN general.to_model_accessions USING(accession_id) " +
                  "WHERE comp_id="+comp_id+" AND difference='"+status+"'";
        else
            query="SELECT sequence_accession_id " +
                  "FROM updates.diffs JOIN general.to_sequence_accessions USING(accession_id) " +
                  "WHERE comp_id="+comp_id+" AND difference='"+status+"'";
            
        logQuery(query);
        return query;
    }

    public String getQuerySearchQuery(String queries_id, KeyType keyType)
    {
        String query="select sql from updates.queries where queries_id="+queries_id;
        logQuery(query);
        return query;
    }

    public String getSeqModelSearchQuery(java.util.Collection model_ids, KeyType keyType)
    {
       //TODO: needs testing.
        String query="SELECT clusters.method, count(distinct cluster_members.cluster_id) " +
                " FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                "       JOIN general.clusters USING(cluster_id) " +
                " WHERE "+Common.buildIdListCondition("accessions.accession_id",model_ids) +
                "       AND accessions.is_model=TRUE " +
                " GROUP BY clusters.method ";
        
        logQuery(query);
        return query;
    }

    public String getStatsById(java.util.Collection data)
    {
        return getStatsById(data, SearchQuerySet.STAT_CLUSTERS | SearchQuerySet.STAT_MODELS);
    }
    public String getStatsByQuery(String idQuery)
    {
        return getStatsByQuery(idQuery, SearchQuerySet.STAT_CLUSTERS | SearchQuerySet.STAT_MODELS);
    }
    public String getStatsById(java.util.Collection data, int stats)
    {        
        String query=buildStatsQuery(data, stats);
        logQuery(query);
        return query;                
    }
    public String getStatsByQuery(String idQuery, int stats)
    {
        String query=buildStatsQuery(idQuery, stats);        
        logQuery(query);
        return query;
    }
    
    
    private String buildStatsQuery(Object data,int stats)
    {
//        log.debug("clusters="+SearchQuerySet.STAT_CLUSTERS);
//        log.debug("models="+SearchQuerySet.STAT_MODELS);
//        log.debug(" or ="+(SearchQuerySet.STAT_CLUSTERS | SearchQuerySet.STAT_MODELS));
        log.debug("stats="+stats);
        StringBuffer query=new StringBuffer();
        
        if((stats & SearchQuerySet.STAT_MODELS) != 0)
        {
            log.debug("adding models");
            query.append(getModelCountQuery(data));
        }
        
        if((stats & SearchQuerySet.STAT_CLUSTERS) != 0)
        {
            log.debug("adding cluster counts");
            if(query.length() > 0)
                query.append(" UNION ");
            query.append(getClusterCountsQuery(data));
        }
        
        if((stats & SearchQuerySet.STAT_GENOMES) !=0 )
        {
            log.debug("adding genomes");
            if(query.length() > 0)
                query.append(" UNION ");
            query.append(getGenomeCountsQuery(data));
        }
        
        if((stats & SearchQuerySet.STAT_MODEL_CLUSTERS) !=0 )
        {
            log.debug("adding model clusters");
            if(query.length() > 0)
                query.append(" UNION ");
            query.append(getModelClusterCountsQuery(data));
        }

        return query.toString();
    }

    private String getModelCountQuery(Object data)
    {
        String condition=buildCondition("md.sequence_accession_id", data);
        return "SELECT 'models', count(distinct md.accession_id) "+
               " FROM common.model_data as md" +
               " WHERE "+condition;
    }
    private String getClusterCountsQuery(Object data)
    {
        String condition=buildCondition("md.sequence_accession_id", data);        
        return "SELECT c.method, count(distinct cm.cluster_id) " +
               " FROM common.model_data md JOIN general.cluster_members as cm USING(accession_id)" +
               "    JOIN general.clusters as c USING(cluster_id) " +
               " WHERE "+condition+
               " GROUP BY c.method";
    }
    private String getGenomeCountsQuery(Object data)
    {
        //select gd.db_name, count(distinct a.accession_id) 
        //from general.to_sequence_accessions as sa join general.accessions as a on(sa.sequence_accession_id=a.accession_id) 
        //  join general.genome_databases as gd using(genome_db_id) 
        //where gd.db_name in ('arab','rice') 
        //group by gd.db_name;
        
        String condition=buildCondition("sa.accession_id", data);
        return "SELECT gd.db_name, count(distinct a.accession_id) " +
               "FROM general.to_sequence_accessions as sa JOIN general.accessions as a ON(sa.sequence_accession_id=a.accession_id) " +
               "   JOIN general.genome_databases AS gd USING(genome_db_id) " +
               "WHERE gd.db_name IN ('arab','rice') AND " +condition+
               "GROUP BY gd.db_name";                                
    }
    private String getModelClusterCountsQuery(Object data)
    {
        String condition=buildCondition("accessions.accession_id",data);
        return  "SELECT clusters.method, count(distinct cluster_members.cluster_id) " +
                " FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                "       JOIN general.clusters USING(cluster_id) " +
                " WHERE "+condition+" AND accessions.is_model=TRUE " +
                " GROUP BY clusters.method ";
    }
    private String buildCondition(String field,Object data)
    {
        if(data instanceof Collection)
            return Common.buildIdListCondition(field,(Collection)data);
        else if(data instanceof String)
            return field+" in ( select accession_id from ("+data+") as t )";
        log.warn("invalid data type passed for field "+field+":"+data.getClass().getName());
        return "";
    }   
    
    
    public String getUnknownClusterIdSearchQuery(int cluster_id, KeyType keyType)
    {
        //TODO: should not be used for version 2.
        String query="SELECT DISTINCT accessions.accession" +
                " FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                " WHERE cluster_members.cluster_id="+cluster_id;
        logQuery(query);
        return query;
    }    

    public String getProbeSetSearchQuery(Collection input, int limit, KeyType keyType)
    {

        String query="";
        if(keyType==KeyType.MODEL)
            query="SELECT DISTINCT to_model_accessions.model_accession_id, genome_databases.db_name " +
                    " FROM general.genome_databases " +
                    "   JOIN general.accessions USING(genome_db_id) " +
                    "   JOIN affy.psk_to_accession_view as pska USING(accession_id) " +
                    "   JOIN general.to_model_accessions USING(accession_id) " +
                    " WHERE "+Common.buildLikeCondition("pska.key",input,limit)+
                    " ORDER BY genome_databases.db_name " +
                    " LIMIT "+limit;
            
        else if(keyType==KeyType.SEQ)
            query="SELECT DISTINCT accessions.accession_id, genome_databases.db_name " +
                    " FROM general.genome_databases " +
                    "   JOIN general.accessions USING(genome_db_id) " +
                    "   JOIN affy.psk_to_accession_view as pska USING(accession_id) " +                    
                    " WHERE "+Common.buildLikeCondition("pska.key",input,limit)+
                    " ORDER BY genome_databases.db_name " +
                    " LIMIT "+limit;
        else if(keyType==KeyType.CORR)
            query="SELECT correlation_id, psk1_key " +
                  " FROM affy.correlation_view"+
                  " WHERE "+Common.buildLikeCondition("psk1_key",input)+
                  " OERDER BY psk1_key ASC, pearson DESC"+
                  " LIMIT "+limit;
        else        
            log.error("invalid key type: "+keyType);
            

        logQuery(query);
        return query;
    }
   
    public String getProbeSetKeySearchQuery(Collection input, int limit, KeyType keyType)
    {                  
        String query="SELECT correlation_id, psk1_key FROM affy.correlation_view " +
                " WHERE "+Common.buildIdListCondition("probe_set_key_id",input)+
                " ORDER BY psk1_key ASC, pearson DESC";
        logQuery(query);
        return query;
    }
    
    public String getPskClusterSearchQuery(int cluster_id,KeyType keyType)
    {
        String query="";
        if(keyType==KeyType.PSK_COMP)               
            query="SELECT probe_set_key_id, cluster_id " +
                " FROM affy.cluster_summary_view " +
                " WHERE cluster_id="+cluster_id;                                
        
        logQuery(query);
        return query;
    }    
    
    public String getClusterCorrSearchQuery(int cluster_id, int psk_id, KeyTypeUser.KeyType keyType)
    {
        String query="SELECT corr.correlation_id, csv.cluster_id " +
                     " FROM affy.cluster_summary_view as csv " +
            "               JOIN affy.correlation_view as corr ON(corr.psk2_id=csv.probe_set_key_id) " +
                    "  WHERE corr.probe_set_key_id="+psk_id+" AND csv.cluster_id="+cluster_id+
                    " ORDER BY corr.pearson DESC";
        
        logQuery(query);
        return query;        
    }
    
    
    public String getQueryTestSearchQuery(String query_id, String version, String genome_id)
    {
        String query="SELECT accession_id " +
                "FROM updates.get_test_query_accessions("+query_id+","+version+","+genome_id+")";
        logQuery(query);
        return query;
    }
    public String getQueryStatsSearchQuery(java.util.List query_ids, java.util.List DBs)
    {
        String ids,dbs,query;
        boolean needOrthologs=false;
        
        ids=toArrayString(query_ids,false);
        
        //see if we need ortholog data or not.
        for(Object o : DBs)
            if(o!=null && ((String)o).indexOf("ortholog")!=-1)
                needOrthologs=true;
        if(needOrthologs)
        {
            String db=(String)DBs.get(0);
            String target_db;
            if(db!=null && db.indexOf("arab")!=-1){                        
                dbs="ARRAY['arab']";
                target_db="rice";
            }             
            else if(db!=null && db.indexOf("rice")!=-1){
                dbs="ARRAY['rice']";
                target_db="arab";
            }
            else
            {
                log.error("invalid database given: "+db);
                return "";
            }
            query="SELECT itq.accession_id " +
                "FROM updates.intersect_test_queries("+ids+","+dbs+") as itq " +
                    "   JOIN general.blast_summary_mv as bsm USING(accession_id) " +
                " WHERE bsm.e_value <= 1e-6 AND bsm.db_name='"+target_db+"'";
        }
        else
        {
            dbs=toArrayString(DBs,true);
            query="SELECT accession_id " +
                "FROM updates.intersect_test_queries("+ids+","+dbs+")";
        }
        
        logQuery(query);
        return query;
    }
    private String toArrayString(Collection c,boolean quoted)
    {
        StringBuffer a=new StringBuffer(c.size()*3+10);
        a.append("ARRAY[");
        for(Iterator i=c.iterator();i.hasNext();)
        {
            String s=(String)i.next();
            if(quoted)
                a.append("'"+s+"'");
            else
                a.append(s);            
            if(i.hasNext())
                a.append(",");                
        }
        a.append("]");
        return a.toString();
    }
    //</editor-fold>

   
    /** Get the field part of a fully qualified column name.
     *  The expected formt is: schema.table_name.column_name. 
     *  Any prefix can be missing.
     */
    private String getFieldName(String col)
    { 
        int i=col.lastIndexOf(".");
        if(i==-1) //no '.' found
            return col;
        else if(i+1 < col.length())
            return col.substring(i+1); //grab after '.' to end
        
        log.warn("malformed column name: "+col);
        return "";
    }

}
