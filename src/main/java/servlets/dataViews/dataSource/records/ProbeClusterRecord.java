/*
 * ProbeClusterRecord.java
 *
 * Created on January 8, 2007, 10:47 AM
 *
 */

package servlets.dataViews.dataSource.records;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.KeyTypeUser;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.dataViews.dataSource.structure.UniChildRecord;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ProbeClusterRecord extends  UniChildRecord
{
    private static final Logger log=Logger.getLogger(ProbeClusterRecord.class);
    private static final int reqSize=9;
        
    public Integer probeClusterId,pskId,size;    
    public Float confidence=null;
    public String name,method,pskKey,methodDesc;    
    
    /** Creates a new instance of ProbeClusterRecord */
    public ProbeClusterRecord(List values)
    {
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;  
        
        probeClusterId=new Integer((String)values.get(0));
        pskId=new Integer((String)values.get(1));
        name=(String)values.get(2);
        method=(String)values.get(3);
        methodDesc=(String)values.get(4);
        size=new Integer((String)values.get(5));                   
        if(Common.getBoolean((String)values.get(6)))
            method+=" split";
        if(values.get(7) != null)
            confidence=new Float((String)values.get(7));
        pskKey=(String)values.get(8);
        
            
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
        return probeClusterId;
    }

    public KeyTypeUser.KeyType getPrimaryKeyType()
    {
        return KeyType.PROBE_CLUST;
    }

    public KeyTypeUser.KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,reqSize){
            public Record createRecord(List l)
            {
                return new ProbeClusterRecord(l);
            }
            public Class getRecordType()
            {
                return ProbeClusterRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {                
                return QuerySetProvider.getRecordQuerySet().getProbeClusterRecordQuery(qp.getIds(),qp.getSortCol(),qp.getSortDir(),keyType);
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC,KeyType.PSK,KeyType.CORR,KeyType.MODEL};
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
						  case MODEL:
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
