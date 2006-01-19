/*
 * QueryStatsSearch.java
 *
 * Created on January 3, 2006, 1:03 PM
 *
 */

package servlets.search;

import java.util.*;
import servlets.querySets.QuerySetProvider;
import servlets.Common;

/**
 *
 * @author khoran
 */
public class QueryStatsSearch extends AbstractSearch
{
    
    /** Creates a new instance of QueryStatsSearch */
    public QueryStatsSearch()
    {
    }

    void loadData()
    { //input has a list of query ids, then a list of database names
        log.debug("input = "+input);
        List query_ids,dbs;        

        int boundry=input.indexOf("$");
        if(boundry==-1)
        {
            log.error("invalid input: "+input);
            log.error("syntax: <list if query ids> $ <list of database names>");
            return;
        }
        query_ids=input.subList(0, boundry);
        dbs=input.subList(boundry+1, input.size());
        
                     
        seqId_query=QuerySetProvider.getSearchQuerySet().
                getQueryStatsSearchQuery(query_ids,dbs);
                
                    
                        
        List rs=Common.sendQuery(seqId_query);

        List output=new ArrayList(rs.size());
        for(Iterator i=rs.iterator();i.hasNext();)
            output.add(((List)i.next()).get(0));
        data=output;
    }
    
    public int[] getSupportedKeyTypes()
    { 
        return new int[]{Common.KEY_TYPE_MODEL};
    }
}
