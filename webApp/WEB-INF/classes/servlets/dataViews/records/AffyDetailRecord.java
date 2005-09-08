/*
 * AffyRecord.java
 *
 * Created on July 27, 2005, 11:13 AM
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



public class AffyDetailRecord extends AbstractRecord
{

    Integer accId, probeSetId, expSetId,comparison;
    String celFile,type,description,pma;
    Float intensity;        
    
    private static Logger log=Logger.getLogger(AffyDetailRecord.class);
    
    /** Creates a new instance of AffyRecord */
    public AffyDetailRecord(List values)
    {
        int reqSize=9;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyDetailRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        accId=new Integer((String)values.get(0));
        probeSetId=new Integer((String)values.get(1));
        expSetId=new Integer((String)values.get(2));
        comparison=new Integer((String)values.get(3));
        type=(String)values.get(4);
        celFile=(String)values.get(5);
        description=(String)values.get(6);
        intensity=new Float((String)values.get(7));
        pma=(String)values.get(8);                 
    }
    public Object getPrimaryKey()
    {
        return probeSetId+"_"+expSetId+"_"+comparison+"_"+celFile; 
    }
    public int getChildKeyType()
    {
        return -1;
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
        
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,9){
            public Record getRecord(List l)
            {
                return new AffyDetailRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                switch(keyType){
                    case Common.KEY_TYPE_ACC:
                        Collection<AffyKey> affyKeys=new LinkedList<AffyKey>();
                        for(Iterator i=qp.getIds().iterator();i.hasNext();)
                            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
                        return QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(affyKeys,true,qp.getSortCol(), qp.getSortDir());
                    case Common.KEY_TYPE_DETAIL:
                        return QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(qp.getAffyKeys(),qp.isAllGroups(),qp.getSortCol(), qp.getSortDir());
                    default:
                        return null;
                }
                
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC,Common.KEY_TYPE_DETAIL};
            }
            public int[] getKeyIndecies(int keyType)
            {
                switch(keyType)
                {
                    case Common.KEY_TYPE_ACC:
                        return new int[]{0};
                    case Common.KEY_TYPE_DETAIL:
                        return new int[]{1,2,3};
                    default:
                        log.error("invalid key type given: "+keyType);
                        return null;
                }
                
            }
        };
    }
     
  
}
