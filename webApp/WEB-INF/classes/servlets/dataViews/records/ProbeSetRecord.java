/*
 * ProbeSetRecord.java
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
public class ProbeSetRecord extends AbstractRecord
{    
    private static Logger log=Logger.getLogger(ProbeSetRecord.class);
    
    Integer probeSetId;
    String probeSetKey;
    
    /** Creates a new instance of ProbeSetRecord */
    public ProbeSetRecord(List values)
    {
        int reqSize=2;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in ProbeSetRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        
        probeSetId=new Integer((String)values.get(0));
        probeSetKey=(String)values.get(1);
    }

    public Object getPrimaryKey()
    {
        return probeSetId;
    }

    public int[] getSupportedKeyTypes()
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
        return new RecordInfo(1,3){
            public Record getRecord(List l)
            {
                return new ProbeSetRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getProbeSetRecordQuery(qp.getIds());                
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
            public int[] getKeyIndecies(int keyType)
            {
                return new int[]{0};                
            }
        };
    }
    
}
