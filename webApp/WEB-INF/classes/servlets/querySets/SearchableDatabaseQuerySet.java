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

public interface SearchableDatabaseQuerySet extends QuerySet
{
    public Map getUnknownsColumnNames();
    public Map getUnknowns2ColumnNames();
    public Map getCommonColumnNames();
}
