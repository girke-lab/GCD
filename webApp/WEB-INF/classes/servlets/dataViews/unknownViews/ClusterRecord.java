/*
 * ClusterRecord.java
 *
 * Created on October 26, 2004, 3:14 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.DbConnection;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class ClusterRecord implements Record
{
    int size, cutoff,cluster_id;
    String name;
    List keys=null;
    boolean showClusterCentricView;
    private static Logger log=Logger.getLogger(ClusterRecord.class);
    
    /** Creates a new instance of ClusterRecord */
    public ClusterRecord(String name,int size, int cutoff,int cluster_id)
    {//used for key centric view
        this.name=name;
        this.size=size;
        this.cutoff=cutoff;
        this.cluster_id=cluster_id;
        showClusterCentricView=false;
    }
    public ClusterRecord(List values)
    {//used for key centric view
        if(values==null || values.size()!=4)
        {
            log.error("invalid list in ClusterRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 3");
            return;
        }
        name=(String)values.get(0);
        size=Integer.parseInt((String)values.get(1));
        cutoff=Integer.parseInt((String)values.get(2));
        cluster_id=Integer.parseInt((String)values.get(3));
        showClusterCentricView=false;
    }
    public ClusterRecord(String name,int size,int cutoff,int cluster_id,List keys)
    {//used for cluster centric view
        this.name=name;
        this.size=size;
        this.cutoff=cutoff;
        this.keys=keys;
        this.cluster_id=cluster_id;
        showClusterCentricView=true;
    }
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ClusterRecord))
            return false;
        ClusterRecord rec=(ClusterRecord)o;        
        return rec.size==size && rec.cutoff==cutoff;
    }
    public int hashCode()
    {
        return new Integer(size+cutoff).hashCode();
    }
    
    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);
    }
    
    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);
    }
    
    public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printFooter(out,this);
    }
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,"cutoff","ASC");
    }
    
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
       String query="SELECT * "+                 
        "   FROM unknowns.cluster_info_and_counts_mv " +        
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
        
        List data=null;
                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send ClusterRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new ClusterRecord(l);
            }
        };                
        return RecordGroup.buildRecordMap(rb,data,1,5);     
        
//        List row,l;
//        Map output=new HashMap(); //need to maintain order here
//        for(Iterator i=data.iterator();i.hasNext();)
//        {
//            row=(List)i.next();
//            l=(List)output.get(row.get(0));
//            if(l==null)
//            {
//                l=new LinkedList();
//                output.put(row.get(0),l);
//            }            
//            l.add(new ClusterRecord(row.subList(1,5)));            
//        }
//        return output;
    }
    
   
    
}
