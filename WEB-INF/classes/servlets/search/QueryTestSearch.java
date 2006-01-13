/*
 * QueryTestSearch.java
 *
 * Created on January 2, 2006, 11:05 AM
 *
 */

package servlets.search;

import servlets.Common;
import java.util.*;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class QueryTestSearch extends AbstractSearch
{
    
    /** Creates a new instance of QueryTestSearch */
    public QueryTestSearch()
    {
    }

    void loadData()
    {
        log.debug("input = "+input);
        final int QUERY_ID=0, VERSION=1, GENOME_ID=2;
        seqId_query=QuerySetProvider.getSearchQuerySet().getQueryTestSearchQuery(
                (String)input.get(QUERY_ID), 
                (String)input.get(VERSION),
                (String)input.get(GENOME_ID));
                    
                        
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
