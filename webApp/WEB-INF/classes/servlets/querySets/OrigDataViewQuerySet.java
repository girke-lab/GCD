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

public class OrigDataViewQuerySet implements DataViewQuerySet, RecordQuerySet, SearchableDatabaseQuerySet
{
     private static Logger log=Logger.getLogger(OrigDataViewQuerySet.class);
     private Properties columnNames=null;
     
    /** Creates a new instance of OrigDataViewQuerySet */
    public OrigDataViewQuerySet()
    {
        columnNames=new Properties();
        try{
            columnNames.load(servlets.QueryPageServlet.class.getResourceAsStream("columnNames.properties"));
            //log.debug("column names="+columnNames);
        }catch(Exception e){
            log.error("could not find or read from columnNames.properties: "+e);
        }
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
    
    public String getClusterDataViewQuery(java.util.Collection ids, String order, int[] DBs)
    {
        StringBuffer query=new StringBuffer();        
  
        query.append("SELECT DISTINCT filename, name,arab_count,rice_count,size,method " +
            "FROM cluster_info " +
            "WHERE ");
        query.append(" ("+Common.buildIdListCondition("cluster_info.cluster_id",ids)+" )");
                
        query.append("ORDER BY "+order);        
        log.info("cluster view query: "+query);
        return query.toString();    
    }   

    public String getModelDataViewQuery(Collection ids, String fields)
    {
        String query="SELECT "+fields+" FROM Sequences "+
                       " LEFT JOIN Models USING(Seq_id) WHERE "+Common.buildIdListCondition("sequences.seq_id",ids)+
                       " ORDER BY Genome, "+getModelColumns()[0]+","+getModelColumns()[1];
        logQuery(query);
        return query;
    }

    public String getSeqDataViewQuery(java.util.Collection ids, String order, int[] DBs)
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
    public String getUnknownsDataViewQuery(Collection ids, String sortCol, String sortDir)
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
         String query="SELECT * " +
        "   FROM unknowns.unknown_keys " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
                
        logQuery(query);
        return query;
    }
    
    public String getBlastRecordQuery(java.util.Collection ids, String sortCol, String sortDir)
    {
        if(sortCol==null)
            sortCol="db_name";
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
        String query="SELECT * "+
        "   FROM unknowns.proteomics_stats " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids);

        logQuery(query);
        return query;
    }

    //////////////////////////////////////////////////
    //////////////// SearchbleDatabaseQuerySet methods
    //////////////////////////////////////////////////
    public Map getCommonColumnNames()
    {
        return getSubSet(columnNames,"servlets.advancedSearch.CommonDatabase");
    }
    public Map getUnknowns2ColumnNames()
    {
        return getSubSet(columnNames,"servlets.advancedSearch.Unknowns2Database");
    }
    public Map getUnknownsColumnNames()
    {
        return getSubSet(columnNames,"servlets.advancedSearch.UnknownsDatabase");
    }
    private Map getSubSet(Properties props,String prefix)
    {
        Map names=new HashMap();
        for(Iterator i=props.entrySet().iterator();i.hasNext();)
        {
            Map.Entry set=(Map.Entry)i.next();
            if(((String)set.getKey()).startsWith(prefix))
                names.put(set.getKey(), set.getValue());
        }
        return names;
    }
}
