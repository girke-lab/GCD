/*
 * QuerySearch.java
 *
 * Created on November 11, 2004, 3:41 PM
 */

package servlets.search;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.*;
import org.apache.log4j.Logger;

public class QuerySearch implements Search
{
    private static long serialVersionUID=12;
    String queries_id;
    int limit;
    boolean noData=false;
    List data=null;
    
    private static DbConnection dbc=null;
    private static Logger log=Logger.getLogger(QuerySearch.class);
    
    /** Creates a new instance of QuerySearch */
    public QuerySearch()
    {
        dbc=DbConnectionManager.getConnection("khoran");
    }
    public void init(List data,int limit,int[] dbID)
    {
        if(data.size() < 1)
            noData=true;
        else
            queries_id=(String)data.get(0);
        this.limit=limit;
    }
    public List getResults()
    {
        if(data==null)
            loadData();
        return data;        
    } 
    public List notFound()
    {
      return new ArrayList();  
    }
    public int getDbStartPos(int i)
    {
        return 0;
    }
    public int getDbCount()
    {
        return 0;
    }
    public List getStats()
    {
        return new ArrayList();
    }
    
    private void loadData()
    {
        if(dbc==null)
        {
            log.error("could not get db connection in QuerySearch");
            data=new ArrayList();
        }
        List results=null;
        try{
            log.debug("looking for "+queries_id);
            results=dbc.sendQuery("select sql from updates.queries where queries_id="+queries_id);
            if(results==null || results.size()==0)
                throw new Exception("no query found for "+queries_id);
            log.debug("queries_id results: "+results);
            results=dbc.sendQuery((String)((List)results.get(0)).get(0));
            log.debug("sql results: "+results);
        }catch(Exception e){
            log.error("query failed: "+e.getMessage());
            results=new ArrayList();
        }
        data=new LinkedList();
        for(Iterator i=results.iterator();i.hasNext();)
            data.add(((List)i.next()).get(0));        
    }
}