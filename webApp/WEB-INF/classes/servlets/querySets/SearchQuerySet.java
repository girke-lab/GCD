/*
 * SearchQuerySet.java
 *
 * Created on March 25, 2005, 3:13 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */
import java.util.*;

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
    public String getBlastSearchQuery(String blastDb, Collection keys);
    
    //for ClusterIDSearch
    public String getClusterIDSearchQuery(Collection input, int limit, int[] DBs);
    
    // for ClusterNameSearch
    public String getClusterNameSearchQuery(Collection input, int limit, int[] DBs);
    
    // for DescriptionSearch
    public String getDescriptionSearchQuery(Collection input, int limit, int[] DBs);
    
    // for GoSearch 
    public String getGoSearchQuery(Collection input, int limit);
    
    // for GoTextSearch
    public String getGoTextSearchQuery(Collection input, int limit);
    
    // for IdSearch
    public String getIdSearchQuery(Collection input, int limit, int[] DBs);
    
    // for QueryCompSearch
    public String getQueryCompSearchQuery(String comp_id, String status);
    
    // for QuerySearch
    public String getQuerySearchQuery(String queries_id);        
    
    // for SeqModelSearch
    public String getSeqModelSearchQuery(Collection model_ids);
    
    // for UnknowclusterIdSearch
    public String getUnknownClusterIdSearchQuery(int cluster_id);
}
