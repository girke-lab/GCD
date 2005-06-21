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

public class SeqModelSearch extends AbstractSearch
{
    private static long sereialVersionUID=324;    
        
    List model_ids,seq_ids;    
    
    
    /** Creates a new instance of SeqModelSearch */
    public SeqModelSearch()
    {
    }

    public void init(List l, int limit, int[] dbID)
    {
        //sets l to input
        super.init(l,limit,dbID);
        
        int modelIndex=Integer.parseInt((String)input.get(0));
        
        //use hashes here to make sure we get uniques sets of numbers
        Set seq=new LinkedHashSet(), models=new LinkedHashSet();
        
        int count=0;
        for(Iterator i=input.iterator();i.hasNext();)
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


    public Map getStats()
    {        
        if(stats!=null)
            return stats;
        
        //will load the stats variable
        //with clusters and genome stats
        super.getStats();
        
        //add model stats
        stats.put("models", new Integer(model_ids.size()));
        
        //use the genome stats to setup the dbStartPositions variable
        if(stats.get("arab")==null || stats.get("rice")==null)
            db=null; //disable the whole startPositions thing
        else
        {//then we have both arab and rice
            //log.debug("arab="+stats.get("arab")+", and is of type "+stats.get("arab").getClass().getName());
            dbStartPositions[Common.arab]=0;
            dbStartPositions[Common.rice]=Integer.parseInt((String)stats.get("arab"));
                                        
        }
        
//        if(stats!=null)
//            return stats;
//        if(model_ids.size() >= Common.MAX_QUERY_KEYS)
//        {
//            log.info(model_ids.size()+" is too many keys, skipping stats");
//            stats=new HashMap();
//            return stats;
//        }
//        
//        Map stats=new HashMap();
//        stats.put("models",new Integer(model_ids.size()));
        
        //then find the cluster counts
//        String query=QuerySetProvider.getSearchQuerySet().getSeqModelSearchQuery(model_ids);
//        List row;
//        for(Iterator i=Common.sendQuery(query).iterator();i.hasNext();) 
//        {
//            row=(List)i.next();            
//            stats.put(row.get(0),row.get(1));
//        }
        log.debug("new stats:"+stats);
        return stats;
    }
    public int getDbStartPos(int i) {        
        getStats(); //we need stats to setup the db positions
        if(i < 0 || i > dbStartPositions.length)
            return 0;
        return dbStartPositions[i];
    }
    public int getDbCount()
    {
        getStats();
        if(db==null)
            return 0;
        return db.length;
    }
    protected int getStatTypes()
    {
        return SearchQuerySet.STAT_MODEL_CLUSTERS | SearchQuerySet.STAT_GENOMES;
    }
   
    public java.util.List getResults() {        
        //use these keys to look up data
        return seq_ids;
    }
    public void loadData()
    {
        //use these keys for the stats
        data=model_ids;
    }
    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_SEQ};
    }

 
}
