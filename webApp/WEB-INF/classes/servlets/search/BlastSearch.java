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
    String blastDb;
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
                "required format: <blast db name> <list of accessions>");            
            log.error("data="+data);            
            return;
        }
        log.debug("inputKey="+data);
        blastDb=(String)data.get(0);
        keys=data.subList(1,data.size());
    }
    public List getResults()
    {
        if(data==null)
            loadData();
        return data;
    }
    
    private void loadData()
    {
        if(keys==null || keys.size()==0)
        {
            data=new ArrayList();
            return;
        }
                
        String query=QuerySetProvider.getSearchQuerySet().getBlastSearchQuery(blastDb,keys);

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
    public int getDbStartPos(int i)
    {
        return 0;
    }
    public int getDbCount()
    {
        return 0;
    }
    public int getKeyType()
    {
        return Common.KEY_TYPE_BLAST;
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_BLAST};
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyType
    {
    }
}
