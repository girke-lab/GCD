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
    Integer accId,goId;
    
    private static Logger log=Logger.getLogger(GoRecord.class);
    
    
    public GoRecord(List values)
    {
        if(!checkList("GoRecord",5,values))
            return;
        
        accId=new Integer((String)values.get(0));
        go_number=(String)values.get(1);
        function=(String)values.get(2);
        text=(String)values.get(3);
        goId=new Integer((String)values.get(4));
    }
    public Object getPrimaryKey()
    {
        return goId;
    }
    public int getChildKeyType()
    {
        return -1;
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof GoRecord))
            return false;
        return ((GoRecord)o).goId.intValue()==goId.intValue();
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
        return new RecordInfo(new int[]{0}, 0,5){
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
