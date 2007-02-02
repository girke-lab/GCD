/*
 * ComparisonPskRecord.java
 *
 * Created on April 14, 2006, 12:44 PM
 *
 */

package servlets.dataViews.dataSource.records;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.CompositeFormat;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.dataViews.dataSource.structure.UniChildRecord;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ComparisonPskRecord extends UniChildRecord 
{
    private static Logger log=Logger.getLogger(ComparisonPskRecord.class);        
    private static int reqSize=26;
    
    public Integer psk_id, comparison_id, expSet_id, group_no;

    public String probeSetKey,expSetKey,expDesc,controlPMA,treatmentPMA;
    public String controlDesc,treatDesc,sourceName,info_link;
    
    public int[] cluster_ids,sizes;
    public String[] clusterNames, methods;
    
    public Float controlMean, treatmentMean,ratio;
    public Float contrast, pValue, adjPValue, pfpUp,pfpDown;
    
    /** Creates a new instance of ComparisonPskRecord */
    public ComparisonPskRecord(List values)
    {
        if(!checkList("ComparisonPskRecord",reqSize,values))
            return;
            
        psk_id=new Integer((String)values.get(0));
        probeSetKey=(String)values.get(1);
        expSet_id=new Integer((String)values.get(2));
        expSetKey=(String)values.get(3);
        expDesc=(String)values.get(4);
        group_no=new Integer((String)values.get(5));    
        controlMean=new Float((String)values.get(6));
        treatmentMean=new Float((String)values.get(7));
        controlPMA=(String)values.get(8);
        treatmentPMA=(String)values.get(9);
        ratio=new Float((String)values.get(10));
        // data_type 11
        contrast=new Float((String)values.get(12));
        pValue=new Float((String)values.get(13));
        adjPValue=new Float((String)values.get(14));
        pfpUp=new Float((String)values.get(15));
        pfpDown=new Float((String)values.get(16));
        controlDesc=(String)values.get(17);
        treatDesc=(String)values.get(18);
        comparison_id=new Integer((String)values.get(19));    
        sourceName=(String)values.get(20);
        info_link=(String)values.get(21);
        
        cluster_ids=Common.getIntArray((java.sql.Array)values.get(22));
        clusterNames=Common.getStringArray((java.sql.Array)values.get(23));
        methods=Common.getStringArray((java.sql.Array)values.get(24));
        sizes=Common.getIntArray((java.sql.Array)values.get(25));
        
        if(controlPMA==null) controlPMA="&nbsp";
        if(treatmentPMA==null) treatmentPMA="&nbsp";
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
        return  psk_id+"_"+comparison_id;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.PSK_COMP;
    }
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0,18},0,reqSize){
            public Record createRecord(List l)
            {
                return new ComparisonPskRecord(l);
            }
            public Class getRecordType()
            {
                return ComparisonPskRecord.class;                
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getComparisonPskRecordQuery(qp.getIds(),qp.getComparisonIds(),
                            qp.getSortCol(),qp.getSortDir(), qp.getUserName(),qp.getDataType());
            }
            

            public CompositeFormat getCompositeFormat()
            {
                return new CompositeFormat(){
                    
                    public void printRecords(Writer out,RecordVisitor visitor,Iterable ib)
                        throws IOException
                    {
                        for(Iterator i=ib.iterator();i.hasNext();)
                            ((Record)i.next()).printRecord(out,visitor);
                    }
                    public void printHeader(Writer out, RecordVisitor visitor, Iterable ib)
                        throws IOException 
                    {
                        Iterator itr=ib.iterator();
                        if(itr.hasNext())
                            ((Record)itr.next()).printHeader(out,visitor);
                    
                    }
                };
            }
            
        };
    }
}
