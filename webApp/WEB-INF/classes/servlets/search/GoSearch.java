/*
 * GoSearch.java
 *
 * Created on March 3, 2004, 12:51 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.search.Search;
import servlets.Common;

public class GoSearch extends AbstractSearch
{
   
    
    /** Creates a new instance of GoSearch */
    public GoSearch() 
    {
    }
      
    void loadData()
    {
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;
        
        conditions.append("Go.Go in (");
        while(in.hasNext() && count++ < limit)
        {
            conditions.append("'"+in.next()+"'");
            if(in.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");
        
        seqId_query=buildIdStatement(conditions.toString(),limit,db);
        rs=Common.sendQuery(seqId_query);

        Set al=new HashSet();
        String lastDb="";
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)
        {
            ArrayList t=(ArrayList)i.next();
            if(!lastDb.equals(t.get(2))){
                lastDb=(String)t.get(2);
                dbStartPositions[Common.getDBid(lastDb)]=c;
            }
            al.add(t.get(0));
            keysFound.add(t.get(1));
        }
        data=new ArrayList(al);
        //if(data.size() > Common.MAX_QUERY_KEYS)         
        //    stats=(List)Common.sendQuery(buildStatsStatement(conditions.toString(),db)).get(0);
    }
    
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT go.Seq_id,Go.Go,sequences.genome from Go,sequences "+
                  "WHERE sequences.seq_id=go.seq_id AND ";
        id+="("+conditions+")";
        id+=" limit "+limit;
        log.info("IdSearch query: "+id);   
        return id;
    }
    private String buildStatsStatement(String conditions,int[] dbs)
    {
        conditions="( "+conditions+") ";
        
        String query="SELECT t1.count as model_count, t2.count as cluster_count" +
        " FROM" +
        "        (select count( distinct m.model_id) from sequences as s, models as m, go " +
        "        where s.seq_id=m.seq_id and s.seq_id=go.seq_id and "+conditions+" ) as t1," +
        "        (select count(distinct c.cluster_id) from sequences as s, clusters as c, go" +
        "        where s.seq_id=c.seq_id and s.seq_id=go.seq_id and "+conditions+" ) as t2";       
                
        log.info("GoSearch stats query: "+query);
        return query;
    }
    public List notFound()
    {//find the intersection of inputKeys and keysFound.
        List temp=new ArrayList();
        String el;
        for(Iterator i=input.iterator();i.hasNext();)
        {
            el=(String)i.next();
            if(!el.matches(".*%.*")) //don't add wildcard entries
                temp.add(el);
        }        
        temp.removeAll(keysFound);
        return temp;        
    }
  
}
