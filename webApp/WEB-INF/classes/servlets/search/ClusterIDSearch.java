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

public class ClusterIDSearch implements Search { 
    
    List input,keysFound;
    int limit;
    int[] db;

    /** Creates a new instance of ClusterIDSearch */
    public ClusterIDSearch() 
    {
    }

    public void init(List data, int limit, int[] dbID)
    {
        this.input=data;
        this.limit=limit;
        this.db=dbID;
        keysFound=new ArrayList();
    }
     
    public List getResults() 
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
        ArrayList t;
        for(Iterator i=rs.iterator();i.hasNext();)
        {
            t=(ArrayList)i.next();
            al.add(t.get(0));
            keysFound.add(t.get(1));            
        }
        return al;

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
