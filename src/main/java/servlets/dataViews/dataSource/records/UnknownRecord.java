/*
 * UnknownRecord.java
 *
 * Created on October 12, 2004, 1:54 PM
 */

package servlets.dataViews.dataSource.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.MultiChildRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class UnknownRecord extends MultiChildRecord 
{
    public String key;
    public String description,dbName;    
    public Integer estCount;
    public int key_id;
    public Boolean[] go_unknowns;
    //public Boolean swp_is_unknown, pfam_is_unknown;
    
    
//    public int[] cluster_ids;
//    public int[] sizes;
//    public String[] clusterNames;
//    public String[] methods;    
//    public int probe_set_key_id;
    
    private static Logger log=Logger.getLogger(UnknownRecord.class);
    private static int reqSize=8;
    
    public UnknownRecord(List values)
    {
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        

        key_id=Integer.parseInt((String)values.get(0));
        key=(String)values.get(1);
        description=(String)values.get(2);
        estCount=values.get(3)==null?null:Integer.parseInt((String)values.get(3));
        go_unknowns=new Boolean[3];
        go_unknowns[0]=getBoolean((String)values.get(4));
        go_unknowns[1]=getBoolean((String)values.get(5));
        go_unknowns[2]=getBoolean((String)values.get(6));
        //pfam_is_unknown=getBoolean((String)values.get(7));
        //swp_is_unknown=getBoolean((String)values.get(8));
        dbName=(String)values.get(7);
        
//        if(values.get(7)!=null)
//        {
//            probe_set_key_id=Integer.parseInt((String)values.get(7));
//            cluster_ids=Common.getIntArray((java.sql.Array)values.get(8));
//            clusterNames=Common.getStringArray((java.sql.Array)values.get(9));
//            methods=Common.getStringArray((java.sql.Array)values.get(10));
//            sizes=Common.getIntArray((java.sql.Array)values.get(11));
//        }
//        else
//        {
//            int[] emptyInts=new int[0];
//            String[] emptyStrings=new String[0];
//            
//            probe_set_key_id=-1;
//            cluster_ids=emptyInts;
//            clusterNames=emptyStrings;
//            methods=emptyStrings;
//            sizes=emptyInts;
//        }
    }
    private Boolean getBoolean(String str)
    {
        if(str==null)
            return null;
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }           
        
    public Object getPrimaryKey()
    {
        return key_id;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.ACC;
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
        return new RecordInfo(0,reqSize){
            public Record createRecord(List l)
            {
                return new UnknownRecord(l);
            }
            public Class getRecordType()
            {
                return UnknownRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getUnknownRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir(),keyType);
            }
            public KeyType[] getSupportedKeyTypes() 
            { 
                return new KeyType[]{KeyType.ACC,KeyType.PSK};
            }
            public int[] getKeyIndecies(KeyType keyType)
            {
                switch(keyType)
                {
                    case ANY:
                    case ACC:
                        return new int[]{0};
                    case PSK:
                        return new int[]{8};
                    default:
                        log.error("invalid key type given: "+keyType);
                        return null;
                }
            }
            
        };
    }                
}
