/*
 * DescriptionSearch.java
 *
 * Created on March 3, 2004, 12:49 PM
 */
package servlets.search;

/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.search.*;
import servlets.Common;

public class DescriptionSearch extends AbstractSearch
{
   
    /** Creates a new instance of DescriptionSearch */
    public DescriptionSearch() 
    {
    }
    public void init(java.util.List data, int limit, int[] dbID) {
        super.init(data,limit, dbID);
        //set stats to null so that data will be loaded before we use it.
        stats=null;
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
                conditions.append(" ( Sequences.Description "+Common.ILIKE+" '%"+temp+"%') ");
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
//        {
//            stats=new HashMap();
//            for(Iterator i=Common.sendQuery(buildStatsStatement(conditions.toString(),db)).iterator();
//                i.hasNext();)
//            {
//                row=(List)i.next();
//                stats.put(row.get(0),row.get(1));
//            }
//        }
    }
    private String printList(int[] a)
    {
        String out="[";
        for(int i=0;i<a.length;i++)
            out+=a[i]+",";
        return out+"]";
    }
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id, sequences.genome from Sequences "+
                  "WHERE (";
        
        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+conditions+")";
        id+=" order by Genome ";
        id+=" limit "+limit;
        log.info("Description query: "+id);   
        return id;
    }        
//    private String buildStatsStatement(String conditions,int[] dbs)
//    {
//        conditions="( "+conditions+") AND (";
//        for(int i=0;i<dbs.length;i++)
//        {
//            conditions+=" Genome='"+Common.dbRealNames[dbs[i]]+"' ";
//            if(i < dbs.length-1)//not last iteration of loop
//                conditions+=" or ";
//        }
//        conditions+=" )";
//        String query="SELECT t1.count as model_count, t2.count as cluster_count "+
//            "FROM " +
//                "select 'models', count(distinct m.model_id) from sequences , models as m" +
//                " where sequences.seq_id=m.seq_id and "+conditions +
//                " UNION "+
//                " (select ci.method, count(distinct c.cluster_id) from sequences , clusters as c, cluster_info as ci " +
//                " where sequences.seq_id=c.seq_id and c.cluster_id=ci.cluster_id and "+conditions+
//                " group by ci.method)";
//                
//        log.info("Description stats query: "+query);
//        return query;
//    }
}
