/*
 * ClusterRecord.java
 *
 * Created on October 26, 2004, 3:14 PM
 */

package servlets.dataViews.dataSource.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.MultiChildRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class ClusterRecord extends MultiChildRecord
{
    public int size,cluster_id;
    public String name,method,key;
    public Integer accId;
    public int arabCount, riceCount;
    
    private static Logger log=Logger.getLogger(ClusterRecord.class);
    private static int reqSize=8;
    
    
    public ClusterRecord(List values)
    {//used for key centric view
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        accId=new Integer((String)values.get(0));
        name=(String)values.get(1);
        size=Integer.parseInt((String)values.get(2));        
        method=(String)values.get(3);        
        cluster_id=Integer.parseInt((String)values.get(4));
        key=(String)values.get(5);        
        arabCount=Integer.parseInt((String)values.get(6));        
        riceCount=Integer.parseInt((String)values.get(7));        
    }
    public Object getPrimaryKey()
    {
        return cluster_id;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.CLUSTER;
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
        return new RecordInfo(0,0,reqSize){
            public Record createRecord(List l)
            {
                return new ClusterRecord(l);
            }
            public Class getRecordType()
            {
                return ClusterRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getClusterRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir(),keyType);
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC,KeyType.SEQ};
            }
             public int[] getKeyIndecies(KeyType keyType)
             {
                switch(keyType) {
                    case ANY:
                    case ACC:
                        return new int[]{0};
                    case SEQ:
                        return new int[]{0};
                    default:
                        log.error("invalid keyType: "+keyType);
                        return null;
                }
             }
           
        };
    }
       
}
