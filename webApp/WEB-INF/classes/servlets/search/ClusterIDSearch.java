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
        
        rs=Common.sendQuery(buildClusterStatement(conditions.toString(),limit,db));
        ArrayList al=new ArrayList();
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
        data=al;
        stats=Arrays.asList(new String[]{null,Integer.toString(data.size())});        
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
        System.out.println("ClusterID query is:"+q);

        return q;
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
        System.out.println("temp="+temp);
        temp.removeAll(keysFound);
        return temp;        
    }                 
}
