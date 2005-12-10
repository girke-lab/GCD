/*
 * ProbeSetKeySearch.java
 *
 * Created on November 29, 2005, 12:25 PM
 *
 */

package servlets.search;

import org.apache.log4j.Logger;
import servlets.Common;
import java.util.*;
import servlets.querySets.QuerySetProvider;

/**
 * given psk id numbers, returns correlation_ids.
 * @author khoran
 */
public class ProbeSetKeySearch extends AbstractSearch
{
    
    
    private static Logger log=Logger.getLogger(ProbeSetKeySearch.class);
    
    /** Creates a new instance of ProbeSetKeySearch */
    public ProbeSetKeySearch()
    {
    }

    
    void loadData()
    {
        log.debug("loading correlation ids for psks: "+input);
        seqId_query=QuerySetProvider.getSearchQuerySet().getProbeSetKeySearchQuery(input,limit, keyType);
        List rs=Common.sendQuery(seqId_query);
                
        List output=new ArrayList(rs.size());
        for(Iterator i=rs.iterator();i.hasNext();)
            output.add(((List)i.next()).get(0));
        data=output;
    }
           
  
    public int[] getSupportedKeyTypes()
    { 
        return new int[]{Common.KEY_TYPE_CORR};
    }

    
    
}
