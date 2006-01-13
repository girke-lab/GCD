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
        //will load the stats variable
        //with clusters and genome stats
        super.getStats();
        
        //add model stats
        stats.put("models", new Integer(model_ids.size()));
        
        //use the genome stats to setup the dbStartPositions variable
        if(stats.get("arab")!=null && stats.get("rice")!=null)
        {
            addBookmark("arab", 0);
            addBookmark("rice", new Integer((String)stats.get("arab")));
        }

        log.debug("new stats:"+stats);
        return stats;
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

    public Collection<String> getBookmarkLabels()
    {
        getStats();
        return super.getBookmarkLabels();
    }

    public Collection<Integer> getBookmarkPositions()
    {
        getStats();
        return super.getBookmarkPositions();
    }

 
}
