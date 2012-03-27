/*
 * AffyRecord.java
 *
 * Created on July 27, 2005, 11:13 AM
 * 
 */

package servlets.dataViews.dataSource.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.AffyKey;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.LeafRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */



public class AffyDetailRecord extends LeafRecord
{

    public Integer accId, probeSetId, expSetId,comparison;
    public String celFile,type,description,pma;
    public Float intensity;        
    
    private static Logger log=Logger.getLogger(AffyDetailRecord.class);
    private static int reqSize=9;
    
    /** Creates a new instance of AffyRecord */
    public AffyDetailRecord(List values)
    {
               
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        accId=new Integer((String)values.get(0));
        probeSetId=new Integer((String)values.get(1));
        expSetId=new Integer((String)values.get(2));
        comparison=new Integer((String)values.get(3));
        type=(String)values.get(4);
        celFile=(String)values.get(5);
        description=(String)values.get(6);
        intensity=new Float((String)values.get(7));
        pma=(values.get(8)==null? "" : (String)values.get(8));                                
    }
    public Object getPrimaryKey()
    {
        return probeSetId+"_"+expSetId+"_"+comparison+"_"+celFile; 
    }
    
    public KeyType getPrimaryKeyType()
    {      
        return KeyType.PSK_EXP_COMP_CEL;
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
        
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,reqSize){
            public Record createRecord(List l)
            {
                return new AffyDetailRecord(l);
            }
            public Class getRecordType()
            {
                return AffyDetailRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                switch(keyType){
                    case ACC:
                        Collection<AffyKey> affyKeys=new LinkedList<AffyKey>();
                        for(Iterator i=qp.getIds().iterator();i.hasNext();)
                            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
                        return QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(affyKeys,qp.getDataType(), true,qp.getSortCol(), qp.getSortDir());
                    case PSK_EXP_COMP:
                        return QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(qp.getAffyKeys(),qp.getDataType(), qp.isAllGroups(),qp.getSortCol(), qp.getSortDir());
                    default:
                        return null;
                }
                
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC,KeyType.PSK_EXP_COMP};
            }
            public int[] getKeyIndecies(KeyType keyType)
            {
                switch(keyType)
                {
                    case ACC:
                        return new int[]{0};
                    case PSK_EXP_COMP:
                        return new int[]{1,2,3};
                    default:
                        log.error("invalid key type given: "+keyType);
                        return null;
                }
                
            }
        };
    }
     
  
}
