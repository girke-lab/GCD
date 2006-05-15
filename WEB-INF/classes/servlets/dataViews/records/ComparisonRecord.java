/*
 * ComparisonRecord.java
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
public class ComparisonRecord extends AbstractRecord
{
    private static Logger log=Logger.getLogger(ProbeSetKeyRecord.class);     
    private static int reqSize=10;
    
    Integer comparisonId, comparison;
    
    String expSetKey, expDesc,controlDesc,treatmentDesc, sourceName, 
            catagory,infoLink;
    
    /** Creates a new instance of ComparisonRecord */
    public ComparisonRecord(List values)
    {
         if(!checkList("ProbeSetKeyRecord",reqSize,values))
            return;
         
         comparisonId=new Integer((String)values.get(0));
         // experiment_set_id 1
         expSetKey=(String)values.get(2);
         expDesc=(String)values.get(3);
         comparison=new Integer((String)values.get(4));
         controlDesc=(String)values.get(5);
         treatmentDesc=(String)values.get(6);
         sourceName=(String)values.get(7);
         infoLink=(String)values.get(8);
         catagory=(String)values.get(9);         
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
        return comparisonId;
    }
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_COMP;
    }
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ComparisonRecord))
            return false;
        return ((ComparisonRecord)o).getPrimaryKey().equals(getPrimaryKey());
    }
    
    public int[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
    }
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0},0,reqSize){
            public Record getRecord(List l)
            {
                return new ComparisonRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getComparisonRecordQuery(qp.getIds(),
                            qp.getSortCol(),qp.getSortDir(), qp.getUserName(),qp.getDataType());
            }
               
        };
    }   
}
