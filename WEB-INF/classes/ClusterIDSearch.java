/*
 * ClusterIDSearch.java
 *
 * Created on March 3, 2004, 12:50 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;
public class ClusterIDSearch implements Search {
    
    List input,keysFound;
    int limit;
    int db;

    /** Creates a new instance of ClusterIDSearch */
    public ClusterIDSearch() 
    {
    }

    public void init(List data, int limit, int dbID)
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
        
        while(in.hasNext() && count++< limit)
            conditions.append("Clusters.Cluster_id="+in.next()+" OR ");
        conditions.append("0=1");
        rs=Common.sendQuery(buildClusterStatement(conditions.toString(),limit,db),2);
        
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
    
    private String buildClusterStatement(String conditions, int limit, int currentDB)
    {
        String q="SELECT Sequences.Seq_id, Clusters.Cluster_id from Sequences LEFT JOIN Clusters USING(Seq_id) "+
                 "WHERE ";
        if(currentDB==Common.arab)
            q+=" Genome='arab' and ";
        else if(currentDB==Common.rice)
            q+=" Genome='rice' and ";
        q+="("+conditions+")";
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
