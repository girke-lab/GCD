/*
 * AffyExpSetRecord.java
 *
 * Created on August 3, 2005, 2:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package servlets.dataViews.dataSource.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.dataViews.dataSource.structure.UniChildRecord;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class AffyExpSetRecord extends UniChildRecord
{
    
    public String probeSetKey,expSetKey,link;
    public String catagory,name,description;
    public String short_name, long_name, info_link;
    public Integer up4,down4, up2, down2,on, off;
    public Integer accId,probeSetId, expSetId;    
    public Float controlAverage, treatAverage;
    public Float controlStddev, treatStddev;
    
    private static Logger log=Logger.getLogger(AffyExpSetRecord.class);
    private static int reqSize=23;
    
    
    /** Creates a new instance of AffyExpSetRecord */
    public AffyExpSetRecord(List values)
    {
        
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        //log.debug("data="+values);
        accId=Integer.parseInt((String)values.get(0));
        probeSetId=Integer.parseInt((String)values.get(1));
        probeSetKey=(String)values.get(2);
        expSetId=Integer.parseInt((String)values.get(3));
        expSetKey=(String)values.get(4);
        catagory=(String)values.get(5);
        name=(String)values.get(6);
        description=(String)values.get(7);
        //link=(String)values.get(8); //skip link, we don't ever use it.
        up4=Integer.parseInt((String)values.get(9));
        down4=Integer.parseInt((String)values.get(10));
        up2=Integer.parseInt((String)values.get(11));
        down2=Integer.parseInt((String)values.get(12));
        on=(values.get(13)==null? null : Integer.parseInt((String)values.get(13)));        
        off=(values.get(14)==null? null : Integer.parseInt((String)values.get(14)));     
        //skip data_type field, 15
        controlAverage=getFloat(values.get(16));
        treatAverage=getFloat(values.get(17));
        controlStddev=getFloat(values.get(18));
        treatStddev=getFloat(values.get(19));
        //skip short_name, 20
        long_name=(String)values.get(21);
        info_link=(String)values.get(22);
    }
    private Float getFloat(Object o)
    {
        return o==null? null : Float.parseFloat((String)o);        
    }
    public Object getPrimaryKey()
    {
        return probeSetId+"_"+expSetId;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.PSK_EXP;
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
        return new RecordInfo(new int[]{0}, 0,reqSize){
            public Record createRecord(List l)
            {
                return new AffyExpSetRecord(l);
            }
            public Class getRecordType()
            {
                return AffyExpSetRecord.class;
            }
            
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getAffyExpSetRecordQuery(
                        qp.getIds(),qp.getDataType(), qp.getSortCol(), qp.getSortDir(), qp.getUserName());
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC};
            }
        };
    }        
    
  
}