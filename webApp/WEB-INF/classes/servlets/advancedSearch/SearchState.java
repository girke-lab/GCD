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
import org.apache.log4j.Logger;

/**
 * Stores all information needed to store a query. Can also be serialized for
 * persistant storage (See {@link SearchStateManager }) ( but really shouldn't since
 * it stores index numbers which will likely change, invalidateing all these SearchState
 * objects.  See {@link SearchTreeManager} for a better solution).
 */
public class SearchState implements java.io.Serializable
{
    
    public static final long serialVersionUID = new Long("-1226915453818595933").longValue();
    transient private static Logger log=Logger.getLogger(SearchState.class);
    //public static final long serialVersionUID = 2;
    
    List selectedFields;
    List selectedOps;
    List selectedBools;    
    List startParinths;
    List endParinths;
    List values;
    
    String limit;
    int sortField;
    String description;
    String database;
    
    /** Creates a new instance of SearchState */
    public SearchState()
    {
        selectedFields=new ArrayList();
        selectedOps=new ArrayList();
        selectedBools=new ArrayList();
        startParinths=new ArrayList();
        endParinths=new ArrayList();
        values=new ArrayList();
        limit=Integer.toString(Common.MAXKEYS); 
    }
    
    
    public String getParameterString()
    {
        StringBuffer out=new StringBuffer();
                
        out.append("limit="+limit);
        out.append("&sortField="+sortField);
        out.append("&database="+database);
        out.append("&action=refresh"); //keeb page from adding an additional row.
        
        String[] listNames=new String[]{"fields","ops","bools",
                "startPars","endPars","values"};
        List[] listRefs=new List[]{selectedFields,selectedOps,
                selectedBools,startParinths,endParinths,values};
        try{
            for(int c=0;c<listNames.length;c++)
                for(Iterator i=((List)listRefs[c]).iterator();i.hasNext();)  
                {
                    Object o=i.next();
//                    log.debug("list name="+listNames[c]);
//                    if(o==null)
//                        log.debug("o is null");
//                    else
//                        log.debug("o is a "+o.getClass());
                    out.append("&"+listNames[c]+"="+
                            java.net.URLEncoder.encode(o.toString(),"ISO-8859-1"));            
                }
        }catch(java.io.UnsupportedEncodingException e){
            log.error("could not perform  url encoding: "+e.getMessage());
            return "";
        }
        return out.toString();
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
            log.debug("values list is null");
        if(l==null)
            l=new ArrayList();
        log.debug("setting values list to : "+l);
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
    
    public void setDescription(String d)
    {
        description=d;
    }
    public String getDescription()
    {
        return description;
    }
    
    public void setDatabase(String db)
    {
        database=db;        
    }
    public String getDatabase()
    {
        return database;
    }
    
    public String toString()
    {
        return "search state: \n"+
               "\t fields: "+selectedFields+
               "\n\t ops: "+selectedOps+
               "\n\t values: "+values+
               "\n\t bools: "+selectedBools+
               "\n\t startParints: "+startParinths+
               "\n\t end Parinths: "+endParinths;
    }
}
