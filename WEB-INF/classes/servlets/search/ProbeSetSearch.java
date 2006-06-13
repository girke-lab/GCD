/*
 * ProbeSetSearch.java
 *
 * Created on August 29, 2005, 9:55 AM
 *
 */

package servlets.search;

import java.util.*;
import servlets.Common;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ProbeSetSearch extends AbstractSearch
{
    
    /** Creates a new instance of ProbeSetSearch */
    public ProbeSetSearch()
    {
    }
    
    void loadData(){
        List rs = null;
        
        seqId_query=QuerySetProvider.getSearchQuerySet().getProbeSetSearchQuery(input, limit, keyType);
        rs = Common.sendQuery(seqId_query);
        
        List al = new ArrayList();
        String lastDb = "";
        List row;
        int index = 0;
        
        for (Iterator i=rs.iterator();i.hasNext();index++){
            row = (List)i.next();
            if (!lastDb.equals(row.get(1))){
                lastDb=(String)row.get(1);
                addBookmark(lastDb, index);                
            }
            al.add(row.get(0));
        }
        data = al;
    }
    
    
    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.SEQ,KeyType.MODEL,
                         KeyType.CORR};
    }  
}
