/*
 * ClusterNameSearch.java
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

public class ClusterNameSearch extends AbstractSearch
{   
    
    /** Creates a new instance of ClusterNameSearch */
    public ClusterNameSearch() 
    {
    }
    
    void loadData()
    {
        Iterator in=input.iterator();
        StringBuffer conditions=new StringBuffer();
        List rs;
        int wasOp=1;
       
        while(in.hasNext())
        { //create conditions string
            String temp=(String)in.next();//use temp becuase sucsesive calls to nextToken
                                                    //advace the pointer
            if(temp.compareToIgnoreCase("and")!=0 && temp.compareToIgnoreCase("or")!=0 
                    && temp.compareToIgnoreCase("not")!=0 && temp.compareTo("(")!=0
                    && temp.compareTo(")")!=0)
            //no keywords or parinths
            {
                if(wasOp==0)//last token was not an operator, but we must have an operator between every word
                    conditions.append(" and ");
                conditions.append(" ( Cluster_Info.Name "+Common.ILIKE+" '%"+temp+"%') ");

                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        seqId_query=buildIdStatement(conditions.toString(),limit,db);
        rs=Common.sendQuery(seqId_query);
        
        ArrayList al=new ArrayList();
        String lastDb="";
        List row;
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)        
        {
            row=(List)i.next();
            if(!lastDb.equals(row.get(1))){
                lastDb=(String)row.get(1);
                dbStartPositions[Common.getDBid(lastDb)]=c;
            }            
            al.add(row.get(0));
        }                
        data=al;        
//        if(data.size() > Common.MAX_QUERY_KEYS)             
//            stats=(List)Common.sendQuery(buildStatsStatement(conditions.toString(),db)).get(0);
        
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
        String query="SELECT t1.count as model_count, t2.count as cluster_count "+
            "FROM " +
                "(select count(distinct m.model_id) from sequences as s, models as m, clusters as c, cluster_info" +
                " where s.seq_id=m.seq_id and s.seq_id=c.seq_id and c.cluster_id=cluster_info.cluster_id and "+conditions+" ) as t1," +
                "(select count(distinct c2.cluster_id) from sequences as s, clusters as c, clusters as c2, cluster_info" +
                " where s.seq_id=c.seq_id and s.seq_id=c2.seq_id and c.cluster_id=cluster_info.cluster_id and "+conditions+" ) as t2";
                
        log.info("Cluster name stats query: "+query);
        return query;
    }
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id,sequences.genome from Cluster_Info, Clusters, Sequences "+
                  "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND "+
                  " Clusters.seq_id=Sequences.seq_id AND (";

        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }

        id+=") AND "+conditions;
        id+=" order by genome";
        id+=" limit "+limit;
        log.info("ClusterNameSearch query: "+id);
        return id;
    }
}
