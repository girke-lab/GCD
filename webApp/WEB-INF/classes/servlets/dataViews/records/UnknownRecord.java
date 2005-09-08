/*
 * UnknownRecord.java
 *
 * Created on October 12, 2004, 1:54 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class UnknownRecord extends AbstractRecord
{
    String key,description;
    int estCount,key_id;
    boolean[] go_unknowns;

    //Map subRecords=new LinkedHashMap();
    private static Logger log=Logger.getLogger(UnknownRecord.class);

    
    public UnknownRecord(List values)
    {
        if(values==null || values.size()!=7)
        {
            log.error("invalid list in UnknownRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 6");
            return;
        }
        key_id=Integer.parseInt((String)values.get(0));
        key=(String)values.get(1);
        description=(String)values.get(2);
        estCount=Integer.parseInt((String)values.get(3));
        go_unknowns=new boolean[3];
        go_unknowns[0]=getBoolean((String)values.get(4));
        go_unknowns[1]=getBoolean((String)values.get(5));
        go_unknowns[2]=getBoolean((String)values.get(6));
    }
    private boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
   
    
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof UnknownRecord))
            return false;
        return ((UnknownRecord)o).key.equals(key);
    }
    public int hashCode()
    {
        return key.hashCode();
    }
  
    public String toString()
    {
        StringBuffer out=new StringBuffer();
        out.append("<PRE>\n"+key+"\n");
        for (Iterator i=this.iterator(); i.hasNext(); ) {
            out.append(i.next()+"\n");
        }
        out.append("</PRE>");
        return out.toString();
    }
    public Object getPrimaryKey()
    {
        return key_id+"";
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
        return new RecordInfo(new int[]{0}, 0,7){
            public Record getRecord(List l)
            {
                return new UnknownRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getUnknownRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes() 
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
        };
    }
    
    
 
   
//    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
//    {
//
//        Map[] subRecordMaps=new Map[]{
//            GoRecord.getData(dbc,ids),
//            BlastRecord.getData(dbc,ids),            
//            ProteomicsRecord.getData(dbc,ids),
//            ClusterRecord.getData(dbc,ids),
//            ExternalUnknownRecord.getData(dbc,ids),
//            AffyExpSetRecord.getData(dbc,ids,sortCol,sortDir) 
//        };//array of maps of ids to RecordGroups
//        return getData(dbc,ids,sortCol,sortDir, subRecordMaps);
//    }
   
}
