/*
 * SearchableDatabaseQuerySet.java
 *
 * Created on March 29, 2005, 2:51 PM
 */

package servlets.querySets;

import java.util.*;
import servlets.advancedSearch.SearchableDatabase;

/**
 * This QuerySet returns SearchableDatabase ojects for 
 * the different kinds of databases.  It is currently used
 * by the advancedSearchBean. Technically, these are not queries, 
 * but SearchablDatabase objects contain query specific information
 * which must match up with queries returned by other QuerySets.
 * If the queries change, so must the SearchableDatabase objects,
 * so it makes sense to package them together.  
 * @author khoran
 */


public interface DatabaseQuerySet extends QuerySet
{
    public SearchableDatabase getUnknownsDatabase();
    public SearchableDatabase getUnknowns2Database();
    public SearchableDatabase getCommonDatabase();   
    public SearchableDatabase getTreatmentDatabase(String userName);
}
