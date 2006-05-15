/*
 * ProbeSetKeyRecord.java
 *
 * Created on April 24, 2006, 1:22 PM
 *
 */

package servlets.dataViews.records;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.PageColors;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ProbeSetKeyRecord extends AbstractRecord
{
    private static Logger log=Logger.getLogger(ProbeSetKeyRecord.class);        
    private static int reqSize=19;
    
    Integer pskId,expSetId,comparisonId;
    
    String probeSetKey,controlPMA,treatmentPMA;    
    
    int[] cluster_ids,sizes;
    String[] clusterNames, methods;
    
    Float controlMean, treatmentMean,ratio;
    Float contrast, pValue, adjPValue, pfpUp,pfpDown;
    
    
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
        cluster_ids=getIntArray((java.sql.Array)values.get(15));
        clusterNames=getStringArray((java.sql.Array)values.get(16));
        methods=getStringArray((java.sql.Array)values.get(17));
        sizes=getIntArray((java.sql.Array)values.get(18));
        
        if(controlPMA==null) controlPMA="&nbsp";
        if(treatmentPMA==null) treatmentPMA="&nbsp";        
    }

    private String[] getStringArray(java.sql.Array a)
    {
        String[] strings;
        try{
            if(a==null)
                strings=new String[]{};
            else
                strings=(String[])(a.getArray());            
        }catch(java.sql.SQLException e){
            log.warn("exception while grabbing array: "+e);
            strings=new String[]{};
        }        
        return strings;
    }
    private int[] getIntArray(java.sql.Array a)
    {
        int[] values=null;
        try{
            if(a==null)
                values=new int[]{};
            else 
                values=(int[])(a.getArray());                        
        }catch(java.sql.SQLException e){
            log.warn("exception while grabbing array: "+e);
            values=new int[]{};
        }        
        return values;
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
        return pskId+"";
    }
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_PSK;
    }
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ProbeSetKeyRecord))
            return false;
        return ((ProbeSetKeyRecord)o).getPrimaryKey().equals(getPrimaryKey());
    }
    
    public int[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
    }
     public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{14},0,reqSize){
            public Record getRecord(List l)
            {
                return new ProbeSetKeyRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getProbeSetKeyRecordQuery(qp.getIds(),qp.getComparisonIds(),
                            qp.getSortCol(),qp.getSortDir(), qp.getDataType());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_COMP};
            }            
        };
    }

   
}
