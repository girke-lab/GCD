/*
 * DescriptionSearch.java
 *
 * Created on March 3, 2004, 12:49 PM
 */
package servlets.search;

/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.search.*;
import servlets.Common;
import servlets.querySets.*;

public class DescriptionSearch extends AbstractSearch
{
   
    /** Creates a new instance of DescriptionSearch */
    public DescriptionSearch() 
    {
    }
    public void init(java.util.List data, int limit, int[] dbID) {
        super.init(data,limit, dbID);
        //set stats to null so that data will be loaded before we use it.
        stats=null;
    }
    void loadData()
    {
        List rs;
        
        seqId_query=QuerySetProvider.getSearchQuerySet().getDescriptionSearchQuery(input,limit, db);
        rs=Common.sendQuery(seqId_query);
        
        ArrayList al=new ArrayList();
        String lastDb="";
        List row;
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)        
        {
            row=(List)i.next();
            if(!lastDb.equals(row.get(1))){
                lastDb=(String)row.get(1);
                dbStartPositions[Common.getDBid(lastDb)]=c;
            }            
            al.add(row.get(0));
        }
        data=al;     
                
    }
    private String printList(int[] a)
    {
        String out="[";
        for(int i=0;i<a.length;i++)
            out+=a[i]+",";
        return out+"]";
    }   
}
