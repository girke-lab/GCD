/*
 * SearchableDatabaseQuerySet.java
 *
 * Created on March 29, 2005, 2:51 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;
import servlets.advancedSearch.SearchableDatabase;

public interface DatabaseQuerySet extends QuerySet
{
    public SearchableDatabase getUnknownsDatabase();
    public SearchableDatabase getUnknowns2Database();
    public SearchableDatabase getCommonDatabase();    
}
