/*
 * AffyExpSetRecord.java
 *
 * Created on August 3, 2005, 2:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package servlets.dataViews.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.*;
import servlets.querySets.QuerySetProvider;
import servlets.dataViews.AffyKey;
/**
 *
 * @author khoran
 */
public class AffyExpSetRecord extends AbstractRecord
{
    
    String probeSetKey,expSetKey,link;
    String catagory,name,description;
    Integer up4,down4, up2, down2,on, off;
    Integer accId,probeSetId, expSetId;    
    
    private static Logger log=Logger.getLogger(AffyExpSetRecord.class);
    
    
    /** Creates a new instance of AffyExpSetRecord */
    public AffyExpSetRecord(List values)
    {
        int reqSize=15;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyExpSetRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }        
        
        accId=Integer.parseInt((String)values.get(0));
        probeSetId=Integer.parseInt((String)values.get(1));
        probeSetKey=(String)values.get(2);
        expSetId=Integer.parseInt((String)values.get(3));
        expSetKey=(String)values.get(4);
        catagory=(String)values.get(5);
        name=(String)values.get(6);
        description=(String)values.get(7);
        link=(String)values.get(8);
        up4=Integer.parseInt((String)values.get(9));
        down4=Integer.parseInt((String)values.get(10));
        up2=Integer.parseInt((String)values.get(11));
        down2=Integer.parseInt((String)values.get(12));
        on=(values.get(13)==null? null : Integer.parseInt((String)values.get(13)));
        off=(values.get(14)==null? null : Integer.parseInt((String)values.get(14)));     
    }
    public Object getPrimaryKey()
    {
        return probeSetId+"_"+expSetId;
    }
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_COMP;
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
        if(!(o instanceof AffyExpSetRecord))
            return false;
        return ((AffyExpSetRecord)o).probeSetKey.equals(probeSetKey) &&
               ((AffyExpSetRecord)o).expSetKey.equals(expSetKey);
    }
    public int hashCode()
    {
        return probeSetKey.hashCode()+expSetKey.hashCode();
    }
    
    
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{0}, 0,15){
            public Record getRecord(List l)
            {
                return new AffyExpSetRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getAffyExpSetRecordQuery(qp.getIds(),qp.getDataType(), qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
        };
    }        
    
  
}