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
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class ClusterRecord implements Record
{
    int size,cluster_id;
    String name,method;
    List keys=null;
    boolean showClusterCentricView;
    private static Logger log=Logger.getLogger(ClusterRecord.class);
    
    /** Creates a new instance of ClusterRecord */
    public ClusterRecord(String name,int size, String method,int cluster_id)
    {//used for key centric view
        this.name=name;
        this.size=size;
        this.method=method;
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
        method=(String)values.get(2);        
        cluster_id=Integer.parseInt((String)values.get(3));
        showClusterCentricView=false;
    }
    public ClusterRecord(String name,int size,String method,int cluster_id,List keys)
    {//used for cluster centric view
        this.name=name;
        this.size=size;
        this.method=method;
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
        return rec.size==size && rec.method.equals(method);
    }
    public int hashCode()
    {
        return cluster_id;
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
        return getData(dbc,ids,null,"ASC");
    }
    
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        String query=QuerySetProvider.getRecordQuerySet().getClusterRecordQuery(ids, sortCol,sortDir);
                
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
        log.debug("cluster data, data="+data);
        return RecordGroup.buildRecordMap(rb,data,1,5);             
    }
    
   
    
}
