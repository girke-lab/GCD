/*
 * GoTextSearch.java
 *
 * Created on March 22, 2005, 8:41 AM
 */

package servlets.search;

/**
 *
 * @author jcui
 */
import java.util.*;
import servlets.search.Search;
import servlets.Common;
import servlets.querySets.*;
        
public class GoTextSearch extends AbstractSearch {
    
    /** Creates a new instance of GoTextSearch */
    public GoTextSearch() {
    }
    
    void loadData(){
        List rs = null;
        
        seqId_query=QuerySetProvider.getSearchQuerySet().getGoTextSearchQuery(input, limit, keyType);
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
    
    
    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_SEQ,Common.KEY_TYPE_MODEL};
    }  
 
}
