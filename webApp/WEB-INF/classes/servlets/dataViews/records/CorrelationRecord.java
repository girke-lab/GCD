/*
 * CorrelationRecord.java
 *
 * Created on November 17, 2005, 11:37 AM
 *
 */

package servlets.dataViews.records;

import java.io.Writer;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.dataViews.AffyKey;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class CorrelationRecord extends AbstractRecord
{
    private static Logger log=Logger.getLogger(CorrelationRecord.class);        
    
    Integer correlationId;
    
    /** Creates a new instance of CorrelationRecord */
    public CorrelationRecord(List values)
    {
        int reqSize=4;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in CorrelationRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        
        
        
    }

    public Object getPrimaryKey()
    {
        return correlationId;
    }
    public int getChildKeyType()
    {
        return -1;
    }
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }


    public void printHeader(Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        //visitor.printHeader(out,this);
    }
    public void printRecord(Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        //visitor.printRecord(out,this);
    }
    public void printFooter(Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        //visitor.printFooter(out,this);
    }
    
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0},0,9){
            public Record getRecord(List l)
            {
                return new CorrelationRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getCorrelationRecordQuery(qp.getIds(),qp.getSortCol(),qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_CORR};
            }
            
        };
    }
}
