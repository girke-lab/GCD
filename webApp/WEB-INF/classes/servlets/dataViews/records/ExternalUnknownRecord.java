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
public class ExternalUnknownRecord implements Record
{
    String source;
    boolean isUnknown;
    private static Logger log=Logger.getLogger(ExternalUnknownRecord.class);
    
    /** Creates a new instance of ExternalUnknownRecord */
    public ExternalUnknownRecord(String s, boolean u)
    {
        source=s;
        isUnknown=u;
    }
    public ExternalUnknownRecord(List values)
    {
        if(values==null || values.size()!=2)
        {
            log.error("invalid list in ExternalUnknownRecord constructor");
            return;
        }        
        isUnknown=getBoolean((String)values.get(0));         
        source=(String)values.get(1);
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
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,null,"ASC");
    }
    
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        String query=QuerySetProvider.getRecordQuerySet().getExternlUnknwownsRecordQuery(ids, sortCol, sortDir);
        List data=null;
        try{            
            data=dbc.sendQuery(query);            
        }catch(java.sql.SQLException e){
            log.error("could not send ExternalUnknownRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new ExternalUnknownRecord(l);
            }
        };                
        return RecordGroup.buildRecordMap(rb,data,1,3);     
        
//        List row,l;
//        Map output=new HashMap(); //need to maintain order here
//        for(Iterator i=data.iterator();i.hasNext();)
//        {
//            row=(List)i.next();
//            l=(List)output.get(row.get(0));
//            if(l==null)
//            {
//                l=new LinkedList();
//                output.put(row.get(0),l);
//            }            
//            l.add(new ExternalUnknownRecord(row.subList(1,3)));            
//        }
//        return output;
    }
}
