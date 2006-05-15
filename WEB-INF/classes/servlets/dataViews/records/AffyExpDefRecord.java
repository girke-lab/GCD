/*
 * AffyExpDefRecord.java
 *
 * Created on January 20, 2006, 9:39 AM
 *
 */

package servlets.dataViews.records;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.querySets.QuerySetProvider;


/**
 *
 * @author khoran
 */
public class AffyExpDefRecord extends AbstractRecord
{
    private static Logger log=Logger.getLogger(AffyExpDefRecord.class);
          
    String expName, celFileName, expType;
    Integer groupNo;
    
    /** Creates a new instance of AffyExpDefRecord */
    public AffyExpDefRecord(List values)
    {
        int reqSize=4;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyExpDefRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        expName=(String)values.get(0);
        celFileName=(String)values.get(1);
        expType=(String)values.get(2);
        groupNo=Integer.parseInt((String)values.get(3));
    }

    public Object getPrimaryKey()
    {
        return expName+"_"+celFileName+"_"+expType+"_"+groupNo;
    }
    public int getChildKeyType()
    { 
        return Common.KEY_TYPE_EXP_DEF;
    }
    public int[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
    }
  

    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);
    }

    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);
    }
     public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printFooter(out,this); 
    }
     
     public static RecordInfo getRecordInfo()
     {
         return new RecordInfo(new int[]{0,1,2,3},0,4){
            public Record getRecord(List l)
            {
                return new AffyExpDefRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {                
                return QuerySetProvider.getRecordQuerySet(). 
                        getAffyExpDefRecordQuery(qp.getIds());
            }
           
         };
     }
}
