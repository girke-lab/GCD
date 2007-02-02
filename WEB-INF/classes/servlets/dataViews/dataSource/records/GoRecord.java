/*
 * GoRecord.java
 *
 * Created on October 12, 2004, 3:16 PM
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
public class GoRecord extends LeafRecord
{
    public String go_number,text,function;
    public Integer accId,goId;
    
    private static Logger log=Logger.getLogger(GoRecord.class);
    private static int reqSize=5;
    
    
    public GoRecord(List values)
    {
        if(!checkList("GoRecord",reqSize,values))
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
    public KeyType getPrimaryKeyType()
    {
        return KeyType.GO;
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
                return new GoRecord(l);
            }
            public Class getRecordType()
            {
                return GoRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getGoRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC};
            }
        };
    }
    
}
