/*
 * SearchQuerySet.java
 *
 * Created on March 25, 2005, 3:13 PM
 */

package servlets.querySets;

import java.util.*;

/**
 * This QuerySet provides queries for Search objects. It also
 * defines some constants ued for setting the type of statistics
 * desired. 
 * @author khoran
 */


public interface SearchQuerySet extends QuerySet
{

    // for AbstractSearch
    public static final int STAT_MODELS         = 1;
    public static final int STAT_CLUSTERS       = 2;
    public static final int STAT_GENOMES        = 4;
    public static final int STAT_MODEL_CLUSTERS = 8;

    public String getStatsById(Collection data);
    public String getStatsByQuery(String query);
    public String getStatsById(Collection data,int stats);
    public String getStatsByQuery(String query,int stats);
    
    // for BlastSearch
    public String getBlastSearchQuery(Collection dbNames, Collection keys, int keyType);
    
    //for ClusterIDSearch
    public String getClusterIDSearchQuery(Collection input, int limit, int[] DBs, int keyType);
    
    // for ClusterNameSearch
    public String getClusterNameSearchQuery(Collection input, int limit, int[] DBs, int keyType);
    
    // for DescriptionSearch
    public String getDescriptionSearchQuery(Collection input, int limit, int[] DBs, int keyType);
    
    // for GoSearch 
    public String getGoSearchQuery(Collection input, int limit, int keyType);
    
    // for GoTextSearch
    public String getGoTextSearchQuery(Collection input, int limit, int keyType);
    
    // for IdSearch
    public String getIdSearchQuery(Collection input, int limit, int[] DBs, int keyType);
    
    // for QueryCompSearch
    public String getQueryCompSearchQuery(String comp_id, String status, int keyType);
    
    // for QuerySearch
    public String getQuerySearchQuery(String queries_id, int keyType);        
    
    // for SeqModelSearch
    public String getSeqModelSearchQuery(Collection model_ids, int keyType);
    
    // for UnknowclusterIdSearch
    public String getUnknownClusterIdSearchQuery(int cluster_id, int keyType);
       
    // for ProbeSetSearch
    public String getProbeSetSearchQuery(Collection input, int limit, int keyType);
    
    // for ProbeSetKeySearch
    public String getProbeSetKeySearchQuery(Collection input, int limit, int keyType);
    
    // for QueryTestSearch
    public String getQueryTestSearchQuery(String query_id, String version, String genome_id);
    
    // for QueryStatsSearch
    public String getQueryStatsSearchQuery(List query_ids,List DBs);
}
