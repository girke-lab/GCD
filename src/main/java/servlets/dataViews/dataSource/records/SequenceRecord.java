/*
 * SequenceRecord.java
 *
 * Created on January 10, 2007, 11:28 AM
 *
 */

package servlets.dataViews.dataSource.records;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.MultiChildRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class SequenceRecord extends MultiChildRecord
{
    private static final Logger log=Logger.getLogger(SequenceRecord.class);
    private static final int reqSize=4;
    
    public Integer seqId;
    public String key,description,genome;
    
    /** Creates a new instance of SequenceRecord */
    public SequenceRecord(List values)
    {
         if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;    
         
         seqId=new Integer((String)values.get(0));
         key=(String)values.get(1);
         description=(String)values.get(2);
         genome=(String)values.get(3);
    }

    @Deprecated
    public void printHeader(Writer out, RecordVisitor visitor) throws IOException
    {
    }

    @Deprecated
    public void printRecord(Writer out, RecordVisitor visitor) throws IOException
    {
    }

    @Deprecated
    public void printFooter(Writer out, RecordVisitor visitor) throws IOException
    {
    }

    public Object getPrimaryKey()
    {
        return seqId;
    }

    public KeyTypeUser.KeyType getPrimaryKeyType()
    {
        return KeyType.SEQ;
    }

    public KeyTypeUser.KeyType[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
    }
   
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,reqSize){
            public Record createRecord(List l)
            {
                return new SequenceRecord(l);
            }
            public Class getRecordType()
            {
                return SequenceRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getSequenceRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir(),keyType);
            }
            public KeyType[] getSupportedKeyTypes() 
            { 
                return new KeyType[]{KeyType.ACC,KeyType.PSK,KeyType.CORR};
            }
            public Object buildKey(List data,KeyType keyType)
            {
                if(keyType==KeyType.CORR)
                    return Long.parseLong((String)data.get(reqSize));
                else
                    return super.buildKey(data,keyType);
            }
            public int[] getKeyIndecies(KeyType keyType)
            {
                switch(keyType)
                {
                    case ANY:
                    case ACC:
                        return new int[]{0};
                    case PSK:
                    case CORR:
                        return new int[]{reqSize};
                    default:
                        log.error("invalid key type given: "+keyType);
                        return null;
                }
            }
            
        };
    }    
}
