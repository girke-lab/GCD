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
    {//first element should be index of start of model_id list
        
        int modelIndex=Integer.parseInt((String)data.get(0));
        
        //use hashes here to make sure we get uniques sets of numbers
        Set seq=new HashSet(), models=new HashSet();
        
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
        String query="SELECT ci.method, count(distinct c.cluster_id) " +
            "FROM clusters as c, cluster_info as ci " +
            "WHERE c.cluster_id=ci.cluster_id " +
            "        and " +Common.buildIdListCondition("c.model_id",model_ids)+
            " GROUP BY ci.method";
        
        List row;
        for(Iterator i=Common.sendQuery(query).iterator();i.hasNext();) 
        {
            row=(List)i.next();
            log.debug("row="+row);
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
