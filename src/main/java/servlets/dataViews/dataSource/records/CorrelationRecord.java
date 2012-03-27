/*
 * CorrelationRecord.java
 *
 * Created on November 17, 2005, 11:37 AM
 *
 */

package servlets.dataViews.dataSource.records;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.KeyTypeUser.KeyType;
import servlets.PageColors;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.CompositeFormat;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.MultiChildRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class CorrelationRecord extends MultiChildRecord
{
    private static Logger log=Logger.getLogger(CorrelationRecord.class);    
    private static int reqSize=8;    
    
    public Long corrId;
    public Integer psk1_id,psk2_id;
    public String catagory,psk1_key,psk2_key;
    //public Float correlation,p_value;
    public Float pearson,spearman;
    public Object acc;
    public int[] cluster_ids,sizes;
    public double[] confidences;
    public String[] clusterNames,parentNames, methods,accessions,descriptions;
    
    /** Creates a new instance of CorrelationRecord */
    public CorrelationRecord(List values)
    {
                
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        corrId=Long.parseLong((String)values.get(0));
        psk1_id=Integer.parseInt((String)values.get(1));
        psk2_id=Integer.parseInt((String)values.get(2));
        catagory=(String)values.get(3);
        psk1_key=(String)values.get(4);
        psk2_key=(String)values.get(5);
        
        pearson=Float.parseFloat((String)values.get(6));
        spearman=Float.parseFloat((String)values.get(7));
    }

    
    public Object getPrimaryKey()
    {        
        return corrId;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.CORR;
    }
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }


    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{1},0,reqSize){
            public Record createRecord(List l)
            {
                return new CorrelationRecord(l);
            }
            public Class getRecordType()
            {
                return CorrelationRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getCorrelationRecordQuery(qp.getIds(),qp.getSortCol(),qp.getSortDir(), qp.getCatagory());
            }
            
        };
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
    
}
