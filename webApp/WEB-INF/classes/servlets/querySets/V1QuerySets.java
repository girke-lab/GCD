/*
 * OrigDataViewQuerySet.java
 *
 * Created on March 25, 2005, 3:17 PM
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

public class V1QuerySets implements DataViewQuerySet, RecordQuerySet, DatabaseQuerySet, ScriptQuerySet,SearchQuerySet
{
     private static Logger log=Logger.getLogger(V1QuerySets.class);
     //private Properties columnNames=null;
     
    /** Creates a new instance of OrigDataViewQuerySet */
    public V1QuerySets()
    {
//        columnNames=new Properties();
//        try{
//            columnNames.load(servlets.QueryPageServlet.class.getResourceAsStream("columnNames.properties"));
//            //log.debug("column names="+columnNames);
//        }catch(Exception e){
//            log.error("could not find or read from columnNames.properties: "+e);
//        }
    }
    private void logQuery(String q)
    {
        log.info("query from is: "+q); // use reflection to get calling method here
    }
    
    public String getBlastDataViewQuery(java.util.Collection ids, String sortCol, String sortDir, int keyType)
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
    
    public String getClusterDataViewQuery(java.util.Collection ids, String order, int[] DBs, int keyType)
    {
        StringBuffer query=new StringBuffer();        
  
        query.append("SELECT DISTINCT filename, name,arab_count,rice_count,size,method " +
            "FROM cluster_info " +
            "WHERE ");
        query.append(" ("+Common.buildIdListCondition("cluster_info.cluster_id",ids)+" )");
                
        query.append("ORDER BY "+order);        
        logQuery(query.toString());
        return query.toString();    
    }   

    public String getModelDataViewQuery(Collection ids, String fields, int keyType)
    {
        String query="SELECT "+fields+" FROM Sequences "+
                       " LEFT JOIN Models USING(Seq_id) WHERE "+Common.buildIdListCondition("sequences.seq_id",ids)+
                       " ORDER BY Genome, "+getModelColumns()[0]+","+getModelColumns()[1];
        logQuery(query);
        return query;
    }

    public String getSeqDataViewQuery(java.util.Collection ids, String order, int[] DBs, int keyType)
    {            
        StringBuffer query=new StringBuffer();
        
        query.append("SELECT sequence_view.genome, sequence_view.primary_key,sequence_view.description,models.model_accession," +
                    " go.go, cluster_info.filename,cluster_info.size,cluster_info.name,cluster_info.arab_count,cluster_info.rice_count,cluster_info.method,sequence_view.v3Key "+
                "FROM sequence_view LEFT JOIN models USING (seq_id) LEFT JOIN clusters USING (seq_id) LEFT JOIN cluster_info USING (cluster_id) LEFT JOIN go ON (sequence_view.seq_id=go.seq_id)"+
                //clusters, cluster_info, sequence_view LEFT JOIN go USING (seq_id) "+
                "WHERE  ");
        
                
        query.append("  (");
        for(int i=0;i<DBs.length;i++)
        {
            query.append("sequence_view.genome='"+Common.dbRealNames[DBs[i]]+"' ");
            if(i+1 < DBs.length)
                query.append(" or ");
        }
        
        query.append(") AND ( "+Common.buildIdListCondition("sequence_view.seq_id",ids)+" ) ");
        query.append("ORDER BY sequence_view.genome,");
        if(order!=null && !order.equals(""))
            query.append(order+", ");
        //query.append(" sequence_view.primary_key,clusters.model_id, go.go,cluster_info.filename ");        
        query.append(" sequence_view.primary_key,cluster_info.filename, go.go ");        
        logQuery(query.toString());
        return query.toString();
    
    }
    public String getUnknownsDataViewQuery(Collection ids, String sortCol, String sortDir, int keyType)
    {
         String query="SELECT unknowns.*,treats.treat " +
            " FROM unknowns LEFT JOIN treats USING(unknown_id) " +
            " WHERE "+Common.buildIdListCondition("unknowns.unknown_id",ids)+
            " ORDER BY "+sortCol+" "+sortDir+",unknowns.unknown_id ";
        
        logQuery(query);
        return query;
    }
    public String[] getSortableBlastColumns()
    {
        return new String[] { "target.accession","target.description",
                            "o.name","br.e_value","br.score","br.identities","br.length"};
    }
    public String[] getModelColumns()
    {
        return new String[]{        
            "Sequences.Primary_Key",    "Models.Model_accession",
            "Sequences.Description",    "Models.TU",
            "Sequences.Intergenic",     "Models.UTR3",
            "Sequences.Intergenic",     "Models.CDS",
            "Models.UTR5",              "Models.Protein",
            "Sequences.Genome"};
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

   
    
    
    ////////////////////////////////////////
    ///////////////// RecordQuerySet methods 
    ////////////////////////////////////////
    public String getUnknownRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="key";
         String query="SELECT 1, unknown_keys.* " +
        "   FROM unknowns.unknown_keys " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
                
        logQuery(query);
        return query;
    }
    
    public String getBlastRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="purpose";
        String query="SELECT * " +
        "   FROM unknowns.blast_summary_mv " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }
    public String getClusterRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="key_id";
        String query="SELECT * "+                 
        "   FROM unknowns.cluster_info_and_counts_mv " +        
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
        logQuery(query);
        return query;
    }

    public String getExternlUnknwownsRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="source";
        String query="SELECT key_id,is_unknown,source "+
        "   FROM unknowns.external_unknowns " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }

    public String getGoRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="go_number";
        String query="SELECT  key_id, go_number,function,text"+
        "   FROM unknowns.unknown_keys as uk, go.go_numbers as gn, go.seq_gos as sg " +
        "   WHERE substring(uk.key from 1 for 9)=sg.accession AND sg.go_id=gn.go_id \n" +
        "      AND "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;

        logQuery(query);
        return query;
    }
    public String getProteomicsRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        String query="SELECT key_id, mol_weight, ip, charge, prob_in_body, prob_is_neg "+
        "   FROM unknowns.proteomics_stats " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids);

        logQuery(query);
        return query;
    }

    //////////////////////////////////////////////////
    //////////////// DatabaseQuerySet methods
    //////////////////////////////////////////////////
    public servlets.advancedSearch.SearchableDatabase getCommonDatabase()
    {
        return new CommonDatabase();
    }
    public servlets.advancedSearch.SearchableDatabase getUnknowns2Database()
    {
        return new Unknowns2Database();
    }
    public servlets.advancedSearch.SearchableDatabase getUnknownsDatabase()
    {
        return new UnknownsDatabase();
    }
    
    //////////////////////////////////////////////////
    //////////////// ScriptQuerySet methods
    //////////////////////////////////////////////////    
    public String getAlignToHmmQuery(Collection ids, int limit)
    {
        String query="SELECT m.model_accession,m.protein " +
                " FROM sequences as s LEFT JOIN models as m USING(seq_id) " +
                " WHERE "+Common.buildIdListCondition("s.seq_id",ids)+
                " ORDER BY m.model_accession LIMIT "+limit;
        logQuery(query);
        return query;
    }

    public String getChrPlotQuery(Collection ids)
    {
        String query="SELECT s.primary_key FROM sequences as s " +
                " WHERE "+Common.buildIdListCondition("s.seq_id",ids);
        logQuery(query);
        return query;
    }

    public String getDisplayKeysQuery(Collection ids)
    {
        String query="SELECT s.primary_key FROM sequences as s " +
                " WHERE "+Common.buildIdListCondition("s.seq_id",ids);
        logQuery(query);
        return query;
    }

    public String getGoSlimCountsQuery(Collection ids)
    {
        String query="SELECT s.primary_key,go.go " +
                " FROM sequence_view as s LEFT JOIN go USING(seq_id) " +
                " WHERE "+Common.buildIdListCondition("s.seq_id",ids);
        logQuery(query);
        return query;
    }

    public String getMultigeneQuery(Collection ids, int limit)
    {
        String query="SELECT s.primary_key FROM sequence_view as s " +
                " WHERE "+Common.buildIdListCondition("s.seq_id",ids)+
                " LIMIT "+limit;
        logQuery(query);
        return query;
    }

    public String getTreeViewQuery(String clusterId)
    {
        String query="SELECT ci.method,m.model_accession "+
                     "FROM models as m, clusters as c, cluster_info as ci "+
                     "WHERE m.model_id=c.model_id AND c.cluster_id=ci.cluster_Id "+
                     "      AND ci.filename='"+clusterId+"'";  
        logQuery(query);
        return query;
    }
    
    //////////////////////////////////////////////////
    //////////////// SearchQuerySet methods
    //////////////////////////////////////////////////   
    public String getBlastSearchQuery(String blastDb, java.util.Collection keys, int keyType)
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

    public String getClusterIDSearchQuery(java.util.Collection input, int limit, int[] DBs, int keyType)
    {
        
        String q="SELECT distinct  Sequences.Seq_id, Cluster_Info.filename,sequences.genome "+
                 "FROM Sequences, Cluster_Info, Clusters "+
                 "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND Sequences.seq_id=Clusters.seq_id AND (";
        
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondtion("Cluster_Info.filename",input,limit)+")";
        q+=" order by Genome ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getClusterNameSearchQuery(java.util.Collection input, int limit, int[] DBs, int keyType)
    {
        String id="SELECT DISTINCT Sequences.Seq_id,sequences.genome from Cluster_Info, Clusters, Sequences "+
                  "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND "+
                  " Clusters.seq_id=Sequences.seq_id AND (";

        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }

        id+=") AND "+Common.buildDescriptionCondition("cluster_info.name",input);
        id+=" order by genome";
        id+=" limit "+limit;
        logQuery(id);
        
        return id;
    }

    public String getDescriptionSearchQuery(java.util.Collection input, int limit, int[] DBs, int keyType)
    {
        String id="SELECT DISTINCT Sequences.Seq_id, sequences.genome from Sequences "+
                  "WHERE (";
        
        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+Common.buildDescriptionCondition("sequences.description",input)+")";
        id+=" order by Genome ";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getGoSearchQuery(java.util.Collection input, int limit, int keyType)
    {
        String id="SELECT DISTINCT go.Seq_id,Go.Go,sequences.genome from Go,sequences "+
                  "WHERE sequences.seq_id=go.seq_id AND ";
        id+="("+Common.buildIdListCondition("go.go",input,true,limit)+")";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getGoTextSearchQuery(java.util.Collection input, int limit, int keyType)
    {
        String query = "SELECT DISTINCT s.Seq_id, s.genome from go AS g, sequences AS s " + 
                "where g.seq_id = s.seq_id AND ";
        
        query += "(" + Common.buildLikeCondtion("g.text",input,true)+ ")";
        query += " limit " + limit;
        logQuery(query);
        return query;
    }
    
    public String getIdSearchQuery(java.util.Collection input, int limit, int[] DBs, int keyType)
    {
        String id="SELECT DISTINCT a.Seq_id, a.Accession,s.genome FROM Sequences as s, Id_Associations as a "+
                  "WHERE s.seq_id=a.seq_id AND ("; 

        for(int i=0;i<DBs.length;i++)
        {
            id+=" s.Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        String condition;
        if(input.size()!=0 && ((String)input.iterator().next()).equals("exact") )
        {//if first element is 'exaxt', then use an exact match condition, which is a bit faster.
            Iterator i=input.iterator();
            i.next();
            i.remove(); //remove first elemtent
            condition=Common.buildIdListCondition("a.accession",input,true,limit);
        }
        else
            condition=Common.buildLikeCondtion("a.accession",input,limit);
        
        
        id+=") and ("+condition+")";
        id+=" order by genome";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }
   
    public String getQueryCompSearchQuery(String comp_id, String status, int keyType)
    {
        String query="SELECT key_id FROM updates.diffs " +
                     "WHERE comp_id="+comp_id+" AND difference='"+status+"'";
        logQuery(query);
        return query;
    }

    public String getQuerySearchQuery(String queries_id, int keyType)
    {
        String query="select sql from updates.queries where queries_id="+queries_id;
        logQuery(query);
        return query;
    }

    public String getSeqModelSearchQuery(java.util.Collection model_ids, int keyType)
    {
        String query="SELECT ci.method, count(distinct c.cluster_id) " +
            "FROM clusters as c, cluster_info as ci " +
            "WHERE c.cluster_id=ci.cluster_id " +
            "        and " +Common.buildIdListCondition("c.model_id",model_ids)+
            " GROUP BY ci.method";
        logQuery(query);
        return query;
    }

    public String getStatsById(java.util.Collection data)
    {
        String conditions=Common.buildIdListCondition("s.seq_id",data);
        String query=
        "        select 'models', count( distinct m.model_id) from sequences as s, models as m" +
        "        where s.seq_id=m.seq_id and "+conditions +
        "       UNION "+
        "        (select ci.method, count(distinct c.cluster_id) from sequences as s, clusters as c,cluster_info as ci" +
        "        where s.seq_id=c.seq_id and c.cluster_id=ci.cluster_id and "+conditions+
        "        group by ci.method order by ci.method)";     
        logQuery(query);
        return query;
    }

    public String getStatsByQuery(String query)
    {
        String q=
        "        select 'models', count( distinct m.model_id) from sequences as s, models as m, " +
        "           ("+query+") as t "+
        "        where s.seq_id=m.seq_id and s.seq_id=t.seq_id "+
        "       UNION "+
        "        (select ci.method, count(distinct c.cluster_id) from sequences as s, clusters as c,cluster_info as ci, " +
        "           ("+query+") as t "+
        "        where s.seq_id=c.seq_id and c.cluster_id=ci.cluster_id and s.seq_id=t.seq_id "+
        "        group by ci.method order by ci.method)";       
        logQuery(q);
        return q;
    }
    public String getStatsById(Collection data, int stats)
    {
        return getStatsById(data);
    }

    public String getStatsByQuery(String query, int stats)
    {
        return getStatsByQuery(query);
    }
    public String getUnknownClusterIdSearchQuery(int cluster_id, int keyType)
    {
        String query="SELECT key " +
        "   FROM unknowns.unknown_keys as uk, unknowns.clusters as c " +
        "   WHERE uk.key_id=c.key_id AND c.cluster_id="+cluster_id;
        logQuery(query);
        return query;
    }

    
}
