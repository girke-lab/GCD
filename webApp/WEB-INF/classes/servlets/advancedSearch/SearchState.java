/*
 * SearchState.java
 *
 * Created on September 7, 2004, 12:36 PM
 */

package servlets.advancedSearch;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;

public class SearchState 
{
    List selectedFields;
    List selectedOps;
    List selectedBools;    
    List startParinths;
    List endParinths;
    List values;
    
    String limit;
    int sortField;
    
    /** Creates a new instance of SearchState */
    public SearchState()
    {
        selectedFields=new ArrayList();
        selectedOps=new ArrayList();
        selectedBools=new ArrayList();
        startParinths=new ArrayList();
        endParinths=new ArrayList();
        values=new ArrayList();
    }
    
    public void setSelectedFields(List l)
    {
        if(l==null)
            l=new ArrayList();
        selectedFields=l;
    }
    public List getSelectedFields()
    {
        return selectedFields;
    }
    public Integer getSelectedField(int i)
    {
        if(i>=0 && i<selectedFields.size())
            return (Integer)selectedFields.get(i);
        return null;
    }
    
    public void setSelectedOps(List l)
    {
        if(l==null)
            l=new ArrayList();
        selectedOps=l;
    }
    public List getSelectedOps()
    {
        return selectedOps;
    }
    public Integer getSelectedOp(int i)
    {
        if(i>=0 && i<selectedOps.size())
            return (Integer)selectedOps.get(i);
        return null;
    }
    
    public void setSelectedBools(List l)
    {
        if(l==null)
            l=new ArrayList();
        selectedBools=l;
    }
    public List getSelectedBools()
    {
        return selectedBools;
    }
    public Integer getSelectedBool(int i)
    {
        if(i>=0 && i<selectedBools.size())
            return (Integer)selectedBools.get(i);
        return null;
    }
    
    public void setStartParinths(List l)
    {
        if(l==null)
            l=new ArrayList();
        startParinths=l;
    }
    public List getStartParinths()
    {
        return startParinths;
    }
    public Integer getStartParinth(int i)
    {
        if(i>=0 && i<startParinths.size())
            return (Integer)startParinths.get(i);
        return null;
    }
    
    public void setEndParinths(List l)
    {
        if(l==null)
            l=new ArrayList();
        endParinths=l;
    }
    public List getEndParinths()
    {
        return endParinths;
    }
    public Integer getEndParinth(int i)
    {
        if(i>=0 && i<endParinths.size())
            return (Integer)endParinths.get(i);
        return null;
    }
    public void setValues(List l)
    {
        if(l==null)
            l=new ArrayList();
        values=l;        
    }
    public List getValues()
    {
        return values;
    }
    public String getValue(int i)
    {
        if(i >= 0 && i<values.size())
            return (String)values.get(i); 
        return "";
    }
    
    public void setLimit(String l)
    {
        if(l==null || l.equals("0"))
            limit=Integer.toString(Common.MAXKEYS);        
        else
            limit=l;
    }
    public String getLimit()
    {
        return limit;
    }
    
    public void setSortField(int s)
    {
        if(s < 0) //set to default
            s=0;
        sortField=s;
    }
    public int getSortField()
    {
        return sortField;
    }
}
