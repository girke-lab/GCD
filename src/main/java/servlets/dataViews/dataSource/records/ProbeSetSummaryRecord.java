/*
 * ProbeSetSummaryRecord.java
 *
 * Created on September 14, 2005, 12:16 PM
 *
 */

package servlets.dataViews.dataSource.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.MultiChildRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ProbeSetSummaryRecord extends MultiChildRecord
{    
    private static Logger log=Logger.getLogger(ProbeSetSummaryRecord.class);    
    private static int reqSize=5;
    
    public Integer probeSetId;
    public String probeSetKey;
    public Float average,stddev,mfc;
    
    /**
     * Creates a new instance of ProbeSetSummaryRecord
     */
    public ProbeSetSummaryRecord(List values)
    {        
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        probeSetId=new Integer((String)values.get(0));
        probeSetKey=(String)values.get(1);
        average=Float.parseFloat((String)values.get(2));
        stddev=Float.parseFloat((String)values.get(3));
        mfc=Float.parseFloat((String)values.get(4));
        
    }

    public Object getPrimaryKey()
    {
        return probeSetId;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.PSK;
    }
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }

    public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printFooter(out,this); 
    }

    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this); 
    }

    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out, this);
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,1,reqSize+1){
            public Record createRecord(List l)
            {
                return new ProbeSetSummaryRecord(l);
            }
            public Class getRecordType()
            {
                return ProbeSetSummaryRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getProbeSetSummaryRecordQuery(qp.getIds());                
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC};
            }            
        };
    }
    
}
