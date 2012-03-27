/*
 * BlastSearch.java
 *
 * Created on December 14, 2004, 10:28 AM
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

public class BlastSearch implements Search
{
    //String blastDb;
    Collection blastDbs;
    List keys=null;
    List data=null;
    
    private static Logger log=Logger.getLogger(BlastSearch.class);
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");
    
    /** Creates a new instance of BlastSearch */
    public BlastSearch()
    {        
        if(dbc==null)
            log.error("could not get a db connection");
    }
    
    public void init(List data,int limit,int[]dbID)
    {
        if(data==null || data.size() < 2)
        {
            log.error("invalid inputKey list, " +
                "required format: dbs: <blast db name> keys: <list of accessions>");            
            log.error("data="+data);            
            return;
        }
        log.debug("inputKey="+data);
        blastDbs=new LinkedList();
        keys=new LinkedList();
        boolean onDbs=true;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            String el=(String)i.next();
            if(el.equals("dbs:"))
                onDbs=true;
            else if(el.equals("keys:"))
                onDbs=false;
            else if(onDbs)
                blastDbs.add(el);
            else
                keys.add(el);            
        }
        log.debug("dbs: "+blastDbs);
        log.debug("keys: "+keys);
    }
    public List getResults()
    {
        if(data==null)
            loadData();
        return data;
    }

    public void compress()
    {
    }
    
    private void loadData()
    {
        if(keys==null || keys.size()==0)
        {
            data=new ArrayList();
            return;
        }
                
        String query=QuerySetProvider.getSearchQuerySet().getBlastSearchQuery(blastDbs,keys, null);

        List results=null;
        try{
            results=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("query failed: "+e.getMessage());            
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
    
    
    
    public List notFound()
    {
        return new ArrayList();
    }
    public Map getStats()
    {
        return new HashMap();
    }
    
    public KeyType getKeyType()
    {
        return KeyType.BLAST;
    }

    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.BLAST};
    }

    public void setKeyType(KeyType keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
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
