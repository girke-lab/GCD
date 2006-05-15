/*
 * ComparisonPskRecord.java
 *
 * Created on April 14, 2006, 12:44 PM
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
public class ComparisonPskRecord extends AbstractRecord
{
    private static Logger log=Logger.getLogger(ComparisonPskRecord.class);        
    private static int reqSize=26;
    
    Integer psk_id, comparison_id, expSet_id, group_no;

    String probeSetKey,expSetKey,expDesc,controlPMA,treatmentPMA;
    String controlDesc,treatDesc,sourceName,info_link;
    
    int[] cluster_ids,sizes;
    String[] clusterNames, methods;
    
    Float controlMean, treatmentMean,ratio;
    Float contrast, pValue, adjPValue, pfpUp,pfpDown;
    
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
        
        cluster_ids=getIntArray((java.sql.Array)values.get(22));
        clusterNames=getStringArray((java.sql.Array)values.get(23));
        methods=getStringArray((java.sql.Array)values.get(24));
        sizes=getIntArray((java.sql.Array)values.get(25));
        
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
        return  psk_id+"_"+comparison_id;
    }
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_COMP_PSK;
    }
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0,18},0,reqSize){
            public Record getRecord(List l)
            {
                return new ComparisonPskRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
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
