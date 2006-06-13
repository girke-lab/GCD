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
public class ClusterRecord extends AbstractRecord
{
    int size,cluster_id;
    String name,method,key;
    Integer accId;
    
    private static Logger log=Logger.getLogger(ClusterRecord.class);
    
    
    public ClusterRecord(List values)
    {//used for key centric view
        if(values==null || values.size()!=6)
        {
            log.error("invalid list in ClusterRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 6");
            return;
        }
        accId=new Integer((String)values.get(0));
        name=(String)values.get(1);
        size=Integer.parseInt((String)values.get(2));        
        method=(String)values.get(3);        
        cluster_id=Integer.parseInt((String)values.get(4));
        key=(String)values.get(5);        
    }
    public Object getPrimaryKey()
    {
//        switch(this.getKeyType()){
//            case KeyType.ACC:
//                return accId;
//            case KeyType.CLUSTER:
//                return cluster_id;
//            default:
//                log.error("invalid key type: "+this.getKeyType());
//                return null;
//        }
        return cluster_id;
    }
    public KeyType getChildKeyType()
    {
        return KeyType.CLUSTER;
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ClusterRecord))
            return false;
        ClusterRecord rec=(ClusterRecord)o;        
        return rec.cluster_id==cluster_id;        
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
        
    
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,0,6){
            public Record getRecord(List l)
            {
                return new ClusterRecord(l);
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getClusterRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC};
            }
           
        };
    }
       
}
