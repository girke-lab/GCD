/*
 * ScriptQuerySet.java
 *
 * Created on April 6, 2005, 7:34 AM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */
import java.util.*;

public interface ScriptQuerySet extends QuerySet        
{
    public String getAlignToHmmQuery(Collection ids, int limit);
    
    public String getChrPlotQuery(Collection ids);
    
    public String getDisplayKeysQuery(Collection ids);
 
    public String getGoSlimCountsQuery(Collection ids);
    
    public String getMultigeneQuery(Collection ids, int limit);
    
    public String getTreeViewQuery(String clusterId);
}
