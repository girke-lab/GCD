/*
 * CommonQuerySet.java
 *
 * Created on March 29, 2005, 8:20 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;

public interface CommonQuerySet extends QuerySet
{
    // for AbstractSearch
    public String getStatsById(Collection data);
    public String getStatsByQuery(String query);        
    
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
}
