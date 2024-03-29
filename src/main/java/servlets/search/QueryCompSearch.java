/*
 * QueryCompSearch.java
 *
 * Created on November 12, 2004, 10:41 AM
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
 * Expects a string consisting of a comparision id, from the updates.comparisons
 * table, a space, and then an operation. The operation should be on of 'added',
 * 'removed', or 'unchanged'. Returns a list of key_ids corresponding the keys
 * added or removed according to the given comparison.
 */
public class QueryCompSearch implements Search
{
    String comp_id,status;
    List data=null;
    boolean noData=false;
    KeyType keyType;
    
    private static Logger log=Logger.getLogger(QueryCompSearch.class);
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");    
    
    /** Creates a new instance of QueryCompSearch */
    public QueryCompSearch()
    {
        if(dbc==null)
            log.error("could not get db connection");
    }


    public java.util.List getResults()
    {
        if(data==null)
            loadData();        
        return data;
    }

    public void compress()
    {
    }

    
    public void init(java.util.List data, int limit, int[] dbID)
    {
        if(data==null || data.size() < 2)
        {
            log.error("invalid inputKey list," +
                "required format: <comp_id> (added|removed|unchanged)");            
            log.error("data="+data);
            noData=true;
            return;
        }
        comp_id=(String)data.get(0);
        status=(String)data.get(1);
    }

    private void loadData()
    {
        if(noData)
        {
            data=new ArrayList();
            return;
        }
        
        String query=QuerySetProvider.getSearchQuerySet().getQueryCompSearchQuery(comp_id, status, keyType);
        
        List results=null;
        try{
            results=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("query failed: "+e.getMessage());
            log.error("query was: "+query);            
        }
        if(results==null)
        {
            data=new ArrayList();
            return;
        }
        data=new LinkedList();
        for(Iterator i=results.iterator();i.hasNext();)
            data.add(((List)i.next()).get(0));        
    }
    
    
  
    public java.util.Map getStats()
    {
        return new HashMap();
    }
    public java.util.List notFound()
    {
        return new ArrayList();
    }
    public KeyType getKeyType()
    {
        return keyType;
    }
   
    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.SEQ,KeyType.MODEL};
    }  
    public void setKeyType(KeyType keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        boolean isValid=false;
        KeyType[] keys=getSupportedKeyTypes();
        for(int i=0;i<keys.length;i++)
            if(keyType == keys[i]){
                isValid=true;
                break;
            }
        if(!isValid)
            throw new servlets.exceptions.UnsupportedKeyTypeException(keys,keyType);
        this.keyType=keyType;
    }

    public Collection<String> getBookmarkLabels()
    {
        return Collections.emptyList();
    }

    public Collection<Integer> getBookmarkPositions()
    {
        return Collections.emptyList();
    }
}
