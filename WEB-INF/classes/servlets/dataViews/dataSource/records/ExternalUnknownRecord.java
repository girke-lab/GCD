/*
 * ExternalUnknownRecord.java
 *
 * Created on November 5, 2004, 8:36 AM
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
public class ExternalUnknownRecord extends LeafRecord
{
    public String source;
    public boolean isUnknown;
    public Integer accId,externalId;
    
    private static Logger log=Logger.getLogger(ExternalUnknownRecord.class);
    private static int reqSize=4;
        
    public ExternalUnknownRecord(List values)
    {
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        accId=new Integer((String)values.get(0));
        isUnknown=getBoolean((String)values.get(1));         
        source=(String)values.get(2);
        externalId=new Integer((String)values.get(3));
    }
    private boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
    public Object getPrimaryKey()
    {
        return externalId;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.EXTERNAL;
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
                return new ExternalUnknownRecord(l);
            }
            public Class getRecordType()
            {
                return ExternalUnknownRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getExternlUnknwownsRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir(),keyType);
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC, KeyType.MODEL,KeyType.SEQ};
            }
        };
    }
    
  
}
