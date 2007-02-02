/*
 * AffyExpDefRecord.java
 *
 * Created on January 20, 2006, 9:39 AM
 *
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
public class AffyExpDefRecord extends UniChildRecord
{
    private static Logger log=Logger.getLogger(AffyExpDefRecord.class);
    private static int reqSize=4;
          
    public String expName, celFileName, expType;
    public Integer groupNo;
    
    /** Creates a new instance of AffyExpDefRecord */
    public AffyExpDefRecord(List values)
    {
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        expName=(String)values.get(0);
        celFileName=(String)values.get(1);
        expType=(String)values.get(2);
        groupNo=Integer.parseInt((String)values.get(3));
    }

    public Object getPrimaryKey()
    {
        return expName+"_"+celFileName+"_"+expType+"_"+groupNo;
    }
    public KeyType getPrimaryKeyType()
    { 
        return KeyType.EXP_DEF;
    }
    public KeyType[] getSupportedKeyTypes()
    {
        return getRecordInfo().getSupportedKeyTypes();
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
     
     public static RecordInfo getRecordInfo()
     {
         return new RecordInfo(new int[]{0,1,2,3},0,reqSize){
            public Record createRecord(List l)
            {
                return new AffyExpDefRecord(l);
            }
            public Class getRecordType()
            {
                return AffyExpDefRecord.class;
            }            
            public String getQuery(QueryParameters qp,KeyType keyType)
            {                
                return QuerySetProvider.getRecordQuerySet(). 
                        getAffyExpDefRecordQuery(qp.getIds());
            }
           
         };
     }
}
