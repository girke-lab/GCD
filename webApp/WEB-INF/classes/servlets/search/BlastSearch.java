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
        
//        StringBuffer condition=new StringBuffer();
//        condition.append(" (");
//        for(Iterator i=keys.iterator();i.hasNext();)
//        {
//            condition.append("query.accession ILIKE '"+i.next()+"%'");
//            if(i.hasNext())
//                condition.append(" OR ");
//        }
//        condition.append(") ");
        
        String query=
            "SELECT br.blast_id " +
            "FROM general.blast_results as br, general.accessions as query, " +
            "   general.accessions as target, general.genome_databases as gd " +
            "WHERE gd.db_name='"+blastDb+"' and gd.genome_db_id=target.genome_db_id and " +
            "   query.accession_id=br.query_accession_id AND target.accession_id=br.target_accession_id AND "+
                Common.buildIdListCondition("query.accession",keys,true);
                //condition;
                       
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
}
