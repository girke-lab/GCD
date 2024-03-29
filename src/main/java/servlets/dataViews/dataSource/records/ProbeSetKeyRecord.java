/*
 * ProbeSetKeyRecord.java
 *
 * Created on April 24, 2006, 1:22 PM
 *
 */

package servlets.dataViews.dataSource.records;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
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
public class ProbeSetKeyRecord extends MultiChildRecord
{
    private static Logger log=Logger.getLogger(ProbeSetKeyRecord.class);        
    private static int reqSize=15;
    
    public Integer pskId,expSetId,comparisonId;
    
    public String probeSetKey,controlPMA,treatmentPMA;    
    
    public int[] cluster_ids,sizes;
    public String[] clusterNames, methods,accessions,accDescriptions;
    
    public Float controlMean, treatmentMean,ratio;
    public Float contrast, pValue, adjPValue, pfpUp,pfpDown;
    
    
    /** Creates a new instance of ProbeSetKeyRecord */
    public ProbeSetKeyRecord(List values)
    {
        if(!checkList("ProbeSetKeyRecord",reqSize,values))
            return;        
        
        pskId=new Integer((String)values.get(0));
        probeSetKey=(String)values.get(1);
        expSetId=new Integer((String)values.get(2));
        controlMean=new Float((String)values.get(3));
        treatmentMean=new Float((String)values.get(4));
        controlPMA=(String)values.get(5);
        treatmentPMA=(String)values.get(6);
        ratio=new Float((String)values.get(7));
        // data_type 8
        contrast=new Float((String)values.get(9));
        pValue=new Float((String)values.get(10));
        adjPValue=new Float((String)values.get(11));
        pfpUp=new Float((String)values.get(12));
        pfpDown=new Float((String)values.get(13));        
        comparisonId=new Integer((String)values.get(14));    
        
//        cluster_ids=Common.getIntArray((java.sql.Array)values.get(15));
//        clusterNames=Common.getStringArray((java.sql.Array)values.get(16));
//        methods=Common.getStringArray((java.sql.Array)values.get(17));
//        sizes=Common.getIntArray((java.sql.Array)values.get(18));
//        
//        accessions=Common.getStringArray((java.sql.Array)values.get(19));
//        accDescriptions=Common.getStringArray((java.sql.Array)values.get(20));
        
//        if(controlPMA==null) controlPMA="";
//        if(treatmentPMA==null) treatmentPMA="";        
    }

   
    
    public void printHeader(Writer out, RecordVisitor visitor) throws IOException
    {
        visitor.printHeader(out,this);
    }
    public void printRecord(Writer out, RecordVisitor visitor) throws IOException
    {
        visitor.printRecord(out,this);
    }
    public void printFooter(Writer out, RecordVisitor visitor) throws IOException
    {
        visitor.printFooter(out,this);        
    }
    public Object getPrimaryKey()
    {
        return pskId;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.PSK;
    }
        
    
    public KeyType[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
    }
     public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(14,0,reqSize){
            public Record createRecord(List l)
            {
                return new ProbeSetKeyRecord(l);
            }
            public Class getRecordType()
            {
                return ProbeSetKeyRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getProbeSetKeyRecordQuery(qp.getIds(),qp.getComparisonIds(),
                            qp.getSortCol(),qp.getSortDir(), qp.getDataType());
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.COMP};
            }            
        };
    }

   
}
