/*
 * AbstractSearch.java
 *
 * Created on August 20, 2004, 10:58 AM
 */

package servlets.search;

/**
 *
 * @author  khoran
 */

import servlets.search.Search;
import servlets.Common;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * This is a handy base class for classes implementing the Search
 * interface.  It implements all the methods in the Search interface,
 * and requires that subclasses implement the loadData method.
 */
public abstract class AbstractSearch implements Search, java.io.Serializable
{
    List input,keysFound,data=null,stats=null;
    int limit;
    int[] db;
    int[] dbStartPositions;  //index of firs occurance of each database in dataset
    /**
     * This logger may be used by all subclasses, so we don't have to
     * declare a million loggers.
     */    
    protected static Logger log=Logger.getLogger(AbstractSearch.class);
    
    /**
     * Returns the value at dbStartPositions[i].  If i is out of bounds,
     * 0 is returned.  Subclasses should load this array in the loadData()
     * method.
     * @param i id number of genome.
     * @return index of first occurance of this genome in results list.
     */    
    public int getDbStartPos(int i) {
         if(i < 0 || i > dbStartPositions.length)
            return 0;
        return dbStartPositions[i];
    }
    
    /**
     * Returns the <CODE>data</CODE> list.  If <CODE>data</CODE> is null,
     * <CODE>loadData()</CODE> is called first.
     * @return contents of the data variable.
     */    
    public java.util.List getResults() {
        if(data==null)
            loadData();
        return data;
    }
    
    /**
     * Loads the variables input, limit, and db with the given parameters.
     * Also sets dbStartPositions to an empty list of the same size as dbID, and
     * sets <CODE>keysFound</CODE> to an empty arraylist.
     * @param data input data
     * @param limit query limit
     * @param dbID array of genome ids.
     */    
    public void init(java.util.List data, int limit, int[] dbID) {
        this.input=data;
        this.limit=limit;
        this.db=dbID;
        dbStartPositions=new int[Common.dbCount];
        keysFound=new ArrayList();
        //stats=new ArrayList();
    }
    
    /**
     * Returns the <CODE>keysFound</CODE> list, which is really the list of
     * keys not found, despite the name.  <CODE>keysFound</CODE> defaults
     * to an empty list, and this class does not gaurentee that loadData()
     * will be called before this method returns, even though loadData()
     * is responsable for defineing it.  This way, subclasses needn't
     * worry about the <CODE>keysFound</CODE> list if they are not useing it.
     * @return contents of <CODE>keysFound</CODE>
     */    
    public java.util.List notFound() {
        return keysFound;
    }
    
    //this method should load the data list.
    abstract void loadData();
    
    /**
     * If <CODE>data</CODE> is null, calls loadData(), which should
     * load <CODE>stats</CODE>.  if <CODE>stats</CODE> is not null, it is
     * returned.  If stats is still null, returns an empty list if
     * the result size is greater than <CODE>Common.MAX_QUERY_KEYS</CODE>.
     * Otherwise a default query is executed to fetch the model and cluster
     * counts, using the ids in data as the condition.
     * @return A list with at most 2 elements.
     */    
    public List getStats() {
        if(data==null)
            loadData(); //make sure we have some data.
        if(stats!=null)
            return stats;
        if(data.size() >= Common.MAX_QUERY_KEYS){
            log.info(data.size()+" is too many keys, skipping the stats");
            stats=new ArrayList();
            return stats;
        }
        log.debug("querying "+data.size()+" keys");
        String conditions=buildCondition();
        String query="SELECT t1.count as model_count, t2.count as cluster_count" +
        " FROM" +
        "        (select count( distinct m.model_id) from sequences as s, models as m" +
        "        where s.seq_id=m.seq_id and "+conditions+" ) as t1," +
        "        (select count(distinct c.cluster_id) from sequences as s, clusters as c" +
        "        where s.seq_id=c.seq_id and "+conditions+" ) as t2";       
                
        log.info("AbstactSearch stats query: "+query);
        stats=(List)Common.sendQuery(query).get(0);
        return stats;
    }
    private String buildCondition()
    {
        StringBuffer condition=new StringBuffer();
        condition.append(" s.seq_id in (");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            condition.append(i.next());
            if(i.hasNext())
                condition.append(",");
        }
        condition.append(")");
        return condition.toString();
    }
}
