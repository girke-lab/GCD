/*
 * ExternalUnknownRecord.java
 *
 * Created on November 5, 2004, 8:36 AM
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
public class ExternalUnknownRecord extends AbstractRecord
{
    String source;
    boolean isUnknown;
    Integer accId;
    
    private static Logger log=Logger.getLogger(ExternalUnknownRecord.class);
        
    public ExternalUnknownRecord(List values)
    {
        if(values==null || values.size()!=3)
        {
            log.error("invalid list in ExternalUnknownRecord constructor");
            return;
        }        
        accId=new Integer((String)values.get(0));
        isUnknown=getBoolean((String)values.get(1));         
        source=(String)values.get(2);
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
        if(!(o instanceof ExternalUnknownRecord))
            return false;
        ExternalUnknownRecord eur=(ExternalUnknownRecord)o;        
        return source.equals(eur.source) && isUnknown==eur.isUnknown;
    }
    public int hashCode()
    {
        return source.hashCode();
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
        return new RecordInfo(new int[]{0}, 0,3){
            public Record getRecord(List l)
            {
                return new ExternalUnknownRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getExternlUnknwownsRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
        };
    }
    
  
}
