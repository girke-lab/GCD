/*
 * ClusterNameSearch.java
 *
 * Created on March 3, 2004, 12:51 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.Common;
import servlets.KeyTypeUser;
import servlets.KeyTypeUser.KeyType;
import servlets.querySets.*;

public class ClusterNameSearch extends AbstractSearch
{   
    
    /** Creates a new instance of ClusterNameSearch */
    public ClusterNameSearch() 
    {
    }
    
    void loadData()
    {
        List rs;
        
        seqId_query=QuerySetProvider.getSearchQuerySet().getClusterNameSearchQuery(input, limit,db, keyType);
        rs=Common.sendQuery(seqId_query);
        
        List al=new LinkedList();
        String lastDb="";
        List row;
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)        
        {
            row=(List)i.next();
            if(!lastDb.equals(row.get(1))){
                lastDb=(String)row.get(1);
                addBookmark(lastDb, c);                
            }            
            al.add(row.get(0));
        }                
        data=al;                
    }        
    public KeyTypeUser.KeyType[] getSupportedKeyTypes()
    {
        return new KeyTypeUser.KeyType[]{KeyType.SEQ,KeyType.MODEL};
    }
   
}
