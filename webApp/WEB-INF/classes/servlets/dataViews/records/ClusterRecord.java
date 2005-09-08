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
        switch(this.getKeyType()){
            case Common.KEY_TYPE_ACC:
                return accId;
            case Common.KEY_TYPE_CLUSTER:
                return cluster_id;
            default:
                log.error("invalid key type: "+this.getKeyType());
                return null;
        }
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
        
    
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,6){
            public Record getRecord(List l)
            {
                return new ClusterRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getClusterRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC,Common.KEY_TYPE_CLUSTER};
            }
            public int[] getKeyIndecies(int keyType)
            {
                switch(keyType){
                    case Common.KEY_TYPE_ACC:
                        return new int[]{0};
                    case Common.KEY_TYPE_CLUSTER:
                        return new int[]{4};
                    default:
                        log.error("invalid key type: "+keyType);
                        return null;
                }
            }
        };
    }
       
}
