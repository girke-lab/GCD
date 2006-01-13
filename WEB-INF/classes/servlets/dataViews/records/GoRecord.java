/*
 * GoRecord.java
 *
 * Created on October 12, 2004, 3:16 PM
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
public class GoRecord extends AbstractRecord
{
    String go_number,text,function;
    Integer accId;
    
    private static Logger log=Logger.getLogger(GoRecord.class);
    
    
    public GoRecord(List values)
    {
        if(values==null || values.size()!=4)
        {
            log.error("invalid values list in GoRecord constructor");
            return;
        }
        accId=new Integer((String)values.get(0));
        go_number=(String)values.get(1);
        function=(String)values.get(2);
        text=(String)values.get(3);
        
    }
    public Object getPrimaryKey()
    {
        return accId;
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof GoRecord))
            return false;
        return ((GoRecord)o).go_number.equals(go_number);
    }
    public int hashCode()
    {
        return go_number.hashCode();
    }
    public String toString()
    {
        return go_number+" "+text;
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
        return new RecordInfo(new int[]{0}, 0,4){
            public Record getRecord(List l)
            {
                return new GoRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getGoRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
        };
    }
    
}
