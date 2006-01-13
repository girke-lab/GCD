/*
 * UnknownClusterIdSearch.java
 *
 * Created on November 23, 2004, 8:21 AM
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

/**
 * Takes a unknowns db cluster_id and returns a list of common db seq_ids which
 * correspond to the keys that are in the given cluster, according to the unknowns
 * database.
 *
 *This class is not used for version2 quries.
 */
public class UnknownClusterIdSearch implements Search
{
    
    Search seqIdSearch;
    
    private static Logger log=Logger.getLogger(UnknownClusterIdSearch.class);
    private static DbConnection dbc=null;
    
    /** Creates a new instance of UnknownClusterIdSearch */
    public UnknownClusterIdSearch()
    {
        if(dbc==null)        
            dbc=DbConnectionManager.getConnection("khoran");        
        seqIdSearch=new IdSearch();
    }    

   
    public void init(java.util.List data, int limit, int[] dbID)
    {
        if(data==null || data.size() < 1 || !(data.get(0) instanceof String))
        {
            log.warn("invalid input data");
            seqIdSearch.init(new ArrayList(),limit,dbID);            
            return;
        }
        int cluster_id=Integer.parseInt((String)data.get(0));        
        seqIdSearch.init(getKeys(cluster_id),limit, dbID);
    }
    private List getKeys(int cluster_id)
    {
        String query=QuerySetProvider.getSearchQuerySet().getUnknownClusterIdSearchQuery(cluster_id, seqIdSearch.getKeyType());
        List results=null;
        try{
            results=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("query failed: "+e.getMessage());
            results=new ArrayList();
        }
        List seqIds=new LinkedList();
        seqIds.add("exact"); //makes IdSearch go faster
        for(Iterator i=results.iterator();i.hasNext();)
            seqIds.add(((List)i.next()).get(0));        
        return seqIds;
    }
    
    public java.util.List getResults()
    {
        return seqIdSearch.getResults();
    }
    public java.util.List notFound()
    {
        return seqIdSearch.notFound();
    }
   
    public java.util.Map getStats()
    {
        return seqIdSearch.getStats();
    }
    public int getKeyType()
    {
        return seqIdSearch.getKeyType();
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_ACC};
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        seqIdSearch.setKeyType(keyType);
    }

    public Collection<String> getBookmarkLabels()
    {
        return seqIdSearch.getBookmarkLabels();
    }

    public Collection<Integer> getBookmarkPositions()
    {
        return seqIdSearch.getBookmarkPositions();
    }
}
