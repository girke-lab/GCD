/*
 * ClusterIDSearch.java
 *
 * Created on March 3, 2004, 12:50 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*; 
import servlets.search.Search;
import servlets.Common;

public class ClusterIDSearch extends AbstractSearch
{   
   
    /** Creates a new instance of ClusterIDSearch */
    public ClusterIDSearch() 
    {
    }

    void loadData()
    {
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;
                
        conditions.append("Cluster_Info.filename in (");
        while(in.hasNext() && count++ < limit)
        {
            conditions.append("'"+in.next()+"'");
            if(in.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");
        
        seqId_query=buildClusterStatement(conditions.toString(),limit,db);
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
//        if(data.size() > Common.MAX_QUERY_KEYS)         
//            stats=(List)Common.sendQuery(buildStatsStatement(conditions.toString(),db)).get(0);
    }
    
    private String buildClusterStatement(String conditions, int limit, int[] DBs)
    {
        String q="SELECT distinct  Sequences.Seq_id, Cluster_Info.filename,sequences.genome "+
                 "FROM Sequences, Cluster_Info, Clusters "+
                 "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND Sequences.seq_id=Clusters.seq_id AND (";
        for(int i=0;i<DBs.length;i++)
        {
            q+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+conditions+")";
        q+=" order by Genome ";
        q+=" limit "+limit;
        log.info("ClusterID query is:"+q);

        return q;
    }
    private String buildStatsStatement(String conditions,int[] dbs)
    {
        conditions="( "+conditions+") AND (";
        for(int i=0;i<dbs.length;i++)
        {
            conditions+=" Genome='"+Common.dbRealNames[dbs[i]]+"' ";
            if(i < dbs.length-1)//not last iteration of loop
                conditions+=" or ";
        }
        conditions+=" )";
        String query= //This is a hopeless mess.
                "select 'models', count(distinct m.model_id) from sequences as s, models as m, clusters as c, cluster_info" +
                " where s.seq_id=m.seq_id and s.seq_id=c.seq_id and c.cluster_id=cluster_info.cluster_id and "+conditions +
                " UNION "+
                "select count(distinct c2.cluster_id) from sequences as s, clusters as c, clusters as c2, cluster_info as ci" +
                " where s.seq_id=c.seq_id and s.seq_id=c2.seq_id and c.cluster_id=ci.cluster_id and "+conditions+
                " ";
                
        log.info("ClusterID stats query: "+query);
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
