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

public abstract class AbstractSearch implements Search, java.io.Serializable
{
    List input,keysFound,data=null,stats=null;
    int limit;
    int[] db;
    int[] dbStartPositions;  //index of firs occurance of each database in dataset
    protected static Logger log=Logger.getLogger(AbstractSearch.class);
    
    public int getDbStartPos(int i) {
         if(i < 0 || i > dbStartPositions.length)
            return 0;
        return dbStartPositions[i];
    }
    
    public java.util.List getResults() {
        if(data==null)
            loadData();
        return data;
    }
    
    public void init(java.util.List data, int limit, int[] dbID) {
        this.input=data;
        this.limit=limit;
        this.db=dbID;
        dbStartPositions=new int[Common.dbCount];
        keysFound=new ArrayList();
        //stats=new ArrayList();
    }
    
    public java.util.List notFound() {
        return keysFound;
    }
    
    //this method should load the data list.
    abstract void loadData();
    
    public List getStats() {
        if(data==null)
            loadData(); //make sure we have some data.
        if(stats!=null)
            return stats;
        if(data.size() > Common.MAX_QUERY_KEYS){
            stats=new ArrayList();
            return stats;
        }
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
