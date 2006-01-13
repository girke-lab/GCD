/*
 * ProteomicsRecord.java
 *
 * Created on October 26, 2004, 3:14 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.DbConnection;
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class ProteomicsRecord extends AbstractRecord
{
    float mol_weight,ip,charge,prob;
    boolean prob_is_neg;
    Integer accId;
    
    private static Logger log=Logger.getLogger(ProteomicsRecord.class);
            
    public ProteomicsRecord(List values)
    {
        if(values==null || values.size()!=6)
        {
            log.error("invalid list in ProteomicsRecord constructor");
            return;
        }
        accId=new Integer((String)values.get(0));
        mol_weight=Float.parseFloat((String)values.get(1));
        ip=Float.parseFloat((String)values.get(2));
        charge=Float.parseFloat((String)values.get(3));
        prob=Float.parseFloat((String)values.get(4));
        prob_is_neg=getBoolean((String)values.get(5));
    }
    private boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
    public Object getPrimaryKey()
    {
        return accId;
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ProteomicsRecord))
            return false;
        ProteomicsRecord rec=(ProteomicsRecord)o;
        return mol_weight==rec.mol_weight && ip==rec.ip &&
               charge==rec.charge && prob==rec.prob &&
               prob_is_neg==rec.prob_is_neg;                
    }
    public int hashCode()
    {
        return new Integer((int)(mol_weight+charge+prob+ip)).hashCode();
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
    
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0}, 0,6){
            public Record getRecord(List l)
            {
                return new ProteomicsRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getProteomicsRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
        };
    }
         
    
}
