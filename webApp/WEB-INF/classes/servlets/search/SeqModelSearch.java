/*
 * SeqModelSearch.java
 *
 * Created on December 16, 2004, 11:13 AM
 */

package servlets.search;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.*;
import org.apache.log4j.Logger;
import servlets.querySets.*;

public class SeqModelSearch implements Search
{
    private static long sereialVersionUID=324;
    private static Logger log=Logger.getLogger(BlastSearch.class);
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");
    
    List model_ids,seq_ids;
    Map stats=null;
    
    
    /** Creates a new instance of SeqModelSearch */
    public SeqModelSearch()
    {
    }

    public void init(List data, int limit, int[] dbID)
    {
        
        int modelIndex=Integer.parseInt((String)data.get(0));
        
        //use hashes here to make sure we get uniques sets of numbers
        Set seq=new LinkedHashSet(), models=new LinkedHashSet();
        
        int count=0;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            if(count%2==0)
                seq.add(i.next());
            else
                models.add(i.next());
            count++;
        }
                
        seq_ids=new ArrayList(seq);
        model_ids=new ArrayList(models);
        log.debug("seq_ids size: "+seq_ids.size());
        log.debug("model_ids size: "+model_ids.size());
    }

    public List getResults()
    {
        return seq_ids;
    }

    public Map getStats()
    {
        if(stats!=null)
            return stats;
        if(model_ids.size() >= Common.MAX_QUERY_KEYS)
        {
            log.info(model_ids.size()+" is too many keys, skipping stats");
            stats=new HashMap();
            return stats;
        }
        
        Map stats=new HashMap();
        stats.put("models",new Integer(model_ids.size()));
        
        //then find the cluster counts
        String query=QuerySetProvider.getSearchQuerySet().getSeqModelSearchQuery(model_ids);
        List row;
        for(Iterator i=Common.sendQuery(query).iterator();i.hasNext();) 
        {
            row=(List)i.next();            
            stats.put(row.get(0),row.get(1));
        }
        return stats;
    }



    public int getDbCount()
    {
        return 0;
    }

    public int getDbStartPos(int i)
    {
        return 0;
    }

    public List notFound()
    {
        return new ArrayList();
    }
    
}
