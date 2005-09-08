/*
 * AffyCompRecord.java
 *
 * Created on August 3, 2005, 2:35 PM
 * 
 */

package servlets.dataViews.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.DbConnection;
import servlets.dataViews.AffyKey;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class AffyCompRecord extends AbstractRecord
{
    
    String probeSetKey,expSetKey,controlPMA,treatmentPMA;
    Integer comparison;
    Float controlMean, treatmentMean,ratio;
    Integer accId, probeSetId, expSetId;
    List subRecords;
    
    private static Logger log=Logger.getLogger(AffyCompRecord.class);        
    
    
    /** Creates a new instance of AffyCompRecord */
    public AffyCompRecord(List values)
    {
        int reqSize=11;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyCompRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        accId=new Integer((String)values.get(0));
        probeSetId=new Integer((String)values.get(1));
        probeSetKey=(String)values.get(2);
        expSetId=new Integer((String)values.get(3));
        expSetKey=(String)values.get(4);        
        comparison=new Integer((String)values.get(5));
        controlMean=new Float((String)values.get(6));
        treatmentMean=new Float((String)values.get(7));
        controlPMA=(String)values.get(8);
        treatmentPMA=(String)values.get(9);
        ratio=new Float((String)values.get(10));
        
    }
    public Object getPrimaryKey()
    {
        return probeSetId+"_"+expSetId+"_"+comparison;
    }
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_DETAIL;
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
    
    
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof AffyCompRecord))
            return false;
        return ((AffyCompRecord)o).probeSetId.equals(probeSetId) &&
               ((AffyCompRecord)o).expSetId.equals(expSetId) &&
               ((AffyCompRecord)o).comparison.equals(comparison);
    }
    public int hashCode()
    {
        return probeSetKey.hashCode()+expSetKey.hashCode()+
                comparison.hashCode();
    }
    
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,11){
            public Record getRecord(List l)
            { 
                return new AffyCompRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {                
                switch(keyType){
                    case Common.KEY_TYPE_ACC:
                        Collection<AffyKey> affyKeys=new LinkedList<AffyKey>();
                        for(Iterator i=qp.getIds().iterator();i.hasNext();)
                            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
                        return QuerySetProvider.getRecordQuerySet().getAffyCompRecordQuery(affyKeys,qp.getSortCol(), qp.getSortDir());        
                    case Common.KEY_TYPE_COMP:
                        return QuerySetProvider.getRecordQuerySet().getAffyCompRecordQuery(qp.getAffyKeys(),qp.getSortCol(), qp.getSortDir());
                    default:
                        return null;
                }                
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC,Common.KEY_TYPE_COMP};
            }
            public int[] getKeyIndecies(int keyType)
            {
                switch(keyType)
                {
                    case Common.KEY_TYPE_ACC:
                        return new int[]{0};
                    case Common.KEY_TYPE_COMP:
                        return new int[]{1,3};
                    default:
                        log.error("invalid key type given: "+keyType);
                        return null;
                }
                
            }
        };
    }    
 
}
