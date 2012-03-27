/*
 * ProteomicsRecord.java
 *
 * Created on October 26, 2004, 3:14 PM
 */

package servlets.dataViews.dataSource.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.LeafRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class ProteomicsRecord extends LeafRecord
{
    public float mol_weight,ip,charge,prob;
    public boolean prob_is_neg;
    public Integer accId,protStatId;
    
    private static Logger log=Logger.getLogger(ProteomicsRecord.class);
    
    private static int reqSize=7;
            
    public ProteomicsRecord(List values)
    {
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
                
        accId=new Integer((String)values.get(0));
        mol_weight=Float.parseFloat((String)values.get(1));
        ip=Float.parseFloat((String)values.get(2));
        charge=Float.parseFloat((String)values.get(3));
        prob=Float.parseFloat((String)values.get(4));
        prob_is_neg=getBoolean((String)values.get(5));
        protStatId=new Integer((String)values.get(6));
    }
    private boolean getBoolean(String str)
    {        
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0 || str.equals("1");
    }
    public Object getPrimaryKey()
    {
        return protStatId;
    }
    
    public KeyType getPrimaryKeyType()
    {      
        return KeyType.PROT;
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
    }
    
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0}, 0,reqSize){
            public Record createRecord(List l)
            {
                return new ProteomicsRecord(l);
            }
            public Class getRecordType()
            {
                return ProteomicsRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getProteomicsRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC};
            }
        };
    }
         
    
}
