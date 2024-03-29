/*
 * QueryParameters.java
 *
 * Created on August 30, 2005, 3:42 PM
 *
 */

package servlets.dataViews.dataSource;

import java.util.*; 
import servlets.dataViews.AffyKey;

/**
 *
 * @author khoran
 */
public class QueryParameters
{
    private Collection ids;
    private Collection<AffyKey> affyKeys;
    private Collection<Integer> comparisonIds;
    
    private String sortCol=null;
    private String sortDir="ASC";    
    private String dataType="mas5";
    private String catagory=null;
    private String userName="public";
    
    private boolean allGroups=false;
    
    /** Creates a new instance of QueryParameters */
    public QueryParameters()
    {
    }
    public QueryParameters(Collection ids)
    {
        this.ids=ids;
    }
    public QueryParameters(Collection ids,String sortCol, String sortDir)
    {
        this.ids=ids;
        this.sortCol=sortCol;
        this.sortDir=sortDir;
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

    public void setAffyKeys(Collection<AffyKey> affyKeys)
    {
        this.affyKeys = affyKeys;
    }

    public boolean isAllGroups()
    {
        return allGroups;
    }

    public void setAllGroups(boolean allGroups)
    {
        this.allGroups = allGroups;
    }

    public String getDataType()
    {
        return dataType;
    }

    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }

    public String getCatagory()
    {
        return catagory;
    }

    public void setCatagory(String catagory)
    {
        this.catagory = catagory;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        if(userName!=null && !userName.equals(""))
            this.userName = userName;
    }

    public Collection<Integer> getComparisonIds()
    {
        return comparisonIds;
    }

    public void setComparisonIds(Collection<Integer> comparisonIds)
    {
        this.comparisonIds = comparisonIds;
    }
    
}
