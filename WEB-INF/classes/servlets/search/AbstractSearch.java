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
import servlets.querySets.*;

/**
 * This is a handy base class for classes implementing the Search
 * interface.  It implements all the methods in the Search interface,
 * and requires that subclasses implement the loadData method.
 */
public abstract class AbstractSearch implements Search, java.io.Serializable
{
    List input,keysFound,data=null;
    Map stats=null;
    String seqId_query=null;
    int limit,keyType;
    int[] db=null;
    //int[] dbStartPositions;  //index of first occurance of each database in dataset
    Map<Integer,String> bookmarks=new TreeMap();
    /**
     * This logger may be used by all subclasses, so we don't have to
     * declare a million loggers.
     */    
    protected static Logger log=Logger.getLogger(AbstractSearch.class);
    
    /**
     * Returns the value at dbStartPositions[i].  If i is out of bounds,
     * 0 is returned.  Subclasses should load this array in the loadData()
     * method.
     * @deprecated
     * @param i id number of genome.
     * @return index of first occurance of this genome in results list.
     */    
//    public int getDbStartPos(int i) {        
//         if(i < 0 || i > dbStartPositions.length)
//            return 0;
//        return dbStartPositions[i];
//    }
//    public int getDbCount()
//    {
//        if(db==null)
//            return 0;
//        return db.length;
//    }
    
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
    
    /**this method should load the data list.
     */
    abstract void loadData();
    
    /**
     * If <CODE>data</CODE> is null, calls loadData(), which should
     * load <CODE>stats</CODE>.  if <CODE>stats</CODE> is not null, it is
     * returned.  If stats is still null, returns an empty list if
     * the result size is greater than <CODE>Common.MAX_QUERY_KEYS</CODE>.
     * Otherwise a default query is executed to fetch the model and cluster
     * counts, using the ids in data as the condition.
     * @return A Map.
     */    
    public Map getStats() {
        if(data==null)
            loadData(); //make sure we have some data.
        if(stats!=null)
            return stats;
        
        stats=new HashMap();
        log.debug("number of keys: "+data.size());
        if(data.size() < Common.MAX_QUERY_KEYS)
            stats=statsById();
        else if(seqId_query!=null)
            stats=statsByQuery();
        else
            log.info(data.size()+" is too many keys, and no predefind seq_id query, skipping the stats");
        
        log.debug("stats are: "+stats);
        return stats;
    }
    private Map statsById()
    {
        log.debug("getting stats with id numbers");
        String query=QuerySetProvider.getSearchQuerySet().getStatsById(data, getStatTypes());
        
        Map sMap=new HashMap();                       
        for(Iterator i=Common.sendQuery(query).iterator();i.hasNext();)
        {
            List row=(List)i.next();
            sMap.put(row.get(0),row.get(1));
        }        
        return sMap;
    }
    private Map statsByQuery()
    {
        log.debug("getting stats with query");
        
        String query=QuerySetProvider.getSearchQuerySet().getStatsByQuery(seqId_query,getStatTypes() );

        Map sMap=new HashMap();                       
        for(Iterator i=Common.sendQuery(query).iterator();i.hasNext();)
        {
            List row=(List)i.next();
            sMap.put(row.get(0),row.get(1));
        }        
        return sMap;
    }
    protected int getStatTypes()
    {
        return SearchQuerySet.STAT_CLUSTERS | SearchQuerySet.STAT_MODELS;
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_SEQ};
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        boolean isValid=false;
        int[] keys=getSupportedKeyTypes();
        for(int i=0;i<keys.length;i++)
            if(keyType == keys[i]){
                isValid=true;
                break;
            }
        if(!isValid)
            throw new servlets.exceptions.UnsupportedKeyTypeException(keys,keyType);
        this.keyType=keyType;
    }
    public int getKeyType()
    {
        return keyType;
    }

    protected void addBookmark(String label,Integer position)
    {
        bookmarks.put(position,label);
    }
    public Collection<String> getBookmarkLabels()
    {
        return Collections.unmodifiableCollection(bookmarks.values());
    }

    public Collection<Integer> getBookmarkPositions()
    {
        return Collections.unmodifiableSet(bookmarks.keySet());
    }

}
