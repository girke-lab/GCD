/*
 * ClusterIDSearch.java
 *
 * Created on March 3, 2004, 12:50 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*; 
import servlets.search.Search;
import servlets.Common;
import servlets.querySets.*;

public class ClusterIDSearch extends AbstractSearch
{   
   
    /** Creates a new instance of ClusterIDSearch */
    public ClusterIDSearch() 
    {
    }

    void loadData()
    {        
        List rs=null;        
               
        seqId_query=QuerySetProvider.getSearchQuerySet().getClusterIDSearchQuery(input, limit, db);
        rs=Common.sendQuery(seqId_query);
        
        Set al=new LinkedHashSet();
        String lastDb="";
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)
        {
            ArrayList t=(ArrayList)i.next();
            if(!lastDb.equals(t.get(2))){
                lastDb=(String)t.get(2);
                dbStartPositions[Common.getDBid(lastDb)]=c;
            }
            al.add(t.get(0));
            keysFound.add(t.get(1));
        }
        data=new ArrayList(al);    
    }
    
   
    public List notFound() 
    {//find the intersection of inputKeys and keysFound.
        List temp=new ArrayList();
        String el;
        for(Iterator i=input.iterator();i.hasNext();)
        {
            el=(String)i.next();
            if(!el.matches(".*%.*")) //don't add wildcard entries
                temp.add(el);
        }        
        temp.removeAll(keysFound);
        return temp;        
    }                 

 
}
