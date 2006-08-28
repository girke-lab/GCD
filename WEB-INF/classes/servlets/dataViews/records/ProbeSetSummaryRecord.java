/*
 * ProbeSetSummaryRecord.java
 *
 * Created on September 14, 2005, 12:16 PM
 *
 */

package servlets.dataViews.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.DbConnection;
import servlets.dataViews.AffyKey;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ProbeSetSummaryRecord extends AbstractRecord
{    
    private static Logger log=Logger.getLogger(ProbeSetSummaryRecord.class);
    
    Integer probeSetId;
    String probeSetKey;
    Float controlAverage, treatAverage;
    Float controlStddev, treatStddev;
    
    /**
     * Creates a new instance of ProbeSetSummaryRecord
     */
    public ProbeSetSummaryRecord(List values)
    {
        int reqSize=6;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in ProbeSetRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        
        probeSetId=new Integer((String)values.get(0));
        probeSetKey=(String)values.get(1);
        controlAverage=Float.parseFloat((String)values.get(2));
        controlStddev=Float.parseFloat((String)values.get(3));
        treatAverage=Float.parseFloat((String)values.get(4));
        treatStddev=Float.parseFloat((String)values.get(5));
        
    }

    public Object getPrimaryKey()
    {
        return probeSetId;
    }
    public KeyType getChildKeyType()
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
        return new RecordInfo(0,1,7){
            public Record getRecord(List l)
            {
                return new ProbeSetSummaryRecord(l);
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