/*
 * QueryParameters.java
 *
 * Created on August 30, 2005, 3:42 PM
 *
 */

package servlets.dataViews.records;

import java.util.*; 

/**
 *
 * @author khoran
 */
public class QueryParameters
{

    private String sortCol=null;

    private String sortDir="ASC";

    private Collection ids;

    private Collection affyKeys;
    
    /** Creates a new instance of QueryParameters */
    public QueryParameters()
    {
    }

    public String getSortCol()
    {
        return sortCol;
    }

    public void setSortCol(String sortCol)
    {
        this.sortCol = sortCol;
    }

    public String getSortDir()
    {
        return sortDir;
    }

    public void setSortDir(String sortDir)
    {
        this.sortDir = sortDir;
    }

    public Collection getIds()
    {
        return ids;
    }

    public void setIds(Collection ids)
    {
        this.ids = ids;
    }

    public Collection getAffyKeys()
    {
        return affyKeys;
    }

    public void setAffyKeys(Collection affyKeys)
    {
        this.affyKeys = affyKeys;
    }
    
}
