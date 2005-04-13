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
     /////////////////////////////////
    //////// SearchQuerySet methods
    /////////////////////////////////
    public String getBlastSearchQuery(String blastDb, java.util.Collection keys)
    {
        String query=
            "SELECT br.blast_id " +
            "FROM general.blast_results as br, general.accessions as query, " +
            "   general.accessions as target, general.genome_databases as gd " +
            "WHERE gd.db_name='"+blastDb+"' and gd.genome_db_id=target.genome_db_id and " +
            "   query.accession_id=br.query_accession_id AND target.accession_id=br.target_accession_id AND "+
                Common.buildIdListCondition("query.accession",keys,true);
        logQuery(query);
        return query;
    }

    public String getClusterIDSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        String q="SELECT distinct accessions.accession_id,clusters.key, genome_databases.db_name " +
                " FROM    general.clusters" +
                "        JOIN general.cluster_members USING(cluster_id)" +
                "        JOIN common.model_data USING(accession_id)" +
                "        JOIN general.accessions ON(accessions.accession_id=model_data.sequence_accession_id)" +
                "        JOIN general.genome_databases USING(genome_db_id)" +
                " WHERE  NOT accessions.is_model AND (";                       
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondtion("clusters.key",input,limit)+")";
        q+=" order by genome_databases.db_name ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getClusterNameSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        String q="SELECT distinct accessions.accession_id,clusters.name, genome_databases.db_name " +
                "FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                "   JOIN general.clusters USING(cluster_id) JOIN general.genome_databases USING(genome_db_id) " +
                "WHERE accessions.is_model=FALSE AND (";                       
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondtion("clusters.name",input,limit)+")";
        q+=" order by genome_databases.db_name ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getDescriptionSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {        
        
        String id="SELECT DISTINCT accessions.accession_id, genome_databases.db_name " +
                "FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "WHERE accessions.is_model=FALSE AND (";
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

    public String getGoSearchQuery(java.util.Collection input, int limit)
    {
        String query="SELECT DISTINCT accessions.accession_id, go_numbers.go_number, " +
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

    public String getGoTextSearchQuery(java.util.Collection input, int limit)
    {
        String query="SELECT DISTINCT accessions.accession_id,  " +
                "       genome_databases.db_name" +
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "   JOIN general.accession_gos USING(accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                " WHERE "+Common.buildDescriptionCondition("go_numbers.text",input)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        logQuery(query);
        return query;
    }

    public String getIdSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {        
        String id="SELECT DISTINCT accessions.accession_id, other_accessions.other_accession, genome_databases.db_name "+
                " FROM general.genome_databases " +
                "       JOIN general.accessions USING(genome_db_id) " +
                "       JOIN general.other_accessions USING(accession_id) "+
                " WHERE NOT accessions.is_model AND (";
                
//                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
//                " WHERE accessions.is_model=FALSE AND (";
        for(int i=0;i<DBs.length;i++)
        {
            id+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        String condition;
        if(input.size()!=0 && ((String)input.iterator().next()).equals("exact") )
        {//if first element is 'exact', then use an exact match condition, which is a bit faster.
            Iterator i=input.iterator();
            i.next();
            i.remove(); //remove first elemtent
            condition=Common.buildIdListCondition("other_accessions.other_accession",input,true,limit);
        }
        else
            condition=Common.buildLikeCondtion("other_accessions.other_accession",input,limit);
        
        
        id+=") and ("+condition+")";
        id+=" order by genome_databases.db_name";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getQueryCompSearchQuery(String comp_id, String status)
    {
        String query="SELECT key_id FROM updates.diffs " +
                     "WHERE comp_id="+comp_id+" AND difference='"+status+"'";
        logQuery(query);
        return query;
    }

    public String getQuerySearchQuery(String queries_id)
    {
        String query="select sql from updates.queries where queries_id="+queries_id;
        logQuery(query);
        return query;
    }

    public String getSeqModelSearchQuery(java.util.Collection model_ids)
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
        String condition=Common.buildIdListCondition("md.sequence_accession_id",data);
        String query=
                "SELECT 'models', count(distinct md.accession_id) "+
                " FROM common.model_data as md" +
                " WHERE "+condition+
            " UNION "+
                "SELECT c.method, count(distinct cm.cluster_id) " +
                " FROM common.model_data md JOIN general.accessions as a ON(md.accession_id=a.accession_id)" +
                "   JOIN general.cluster_members as cm ON(a.accession_id=cm.accession_id) JOIN general.clusters as c USING(cluster_id) " +
                " WHERE "+condition+
                " GROUP BY c.method";
                
//select c.method, count(distinct cm.cluster_id) from common.model_data as md JOIN general.accessions as a ON(md.accession_id=a.accession_id) JOIN general.cluster_members as cm USING(accession_id) JOIN general.clusters as c USING(cluster_id)  where md.sequence_accession_id in  (683939,683217) group by c.method;
        logQuery(query);
        return query;
                
    }

    public String getStatsByQuery(String idQuery)
    {
        String condition="md.sequence_accession_id in ("+idQuery+")";
        String query=
                "SELECT 'models', count(distinct md.accession_id) "+
                " FROM common.model_data as md" +
                " WHERE "+condition+
            " UNION "+
                "SELECT c.method, count(distinct cm.cluster_id) " +
                " FROM common.model_data md JOIN general.accessions as a ON(md.accession_id=a.accession_id)" +
                "   JOIN general.cluster_members as cm ON(a.accession_id=cm.accession_id) JOIN general.clusters as c USING(cluster_id) " +
                " WHERE "+condition+
                " GROUP BY c.method";
        logQuery(query);
        return query;
    }

    public String getUnknownClusterIdSearchQuery(int cluster_id)
    {
        //TODO: should not be used for version 2.
        String query="SELECT DISTINCT accessions.accession" +
                " FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                " WHERE cluster_member.cluster_id="+cluster_id;
        logQuery(query);
        return query;
    }
}
