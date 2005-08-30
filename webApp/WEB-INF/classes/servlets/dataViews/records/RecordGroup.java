/*
 * RecordGroup.java
 *
 * Created on January 21, 2005, 12:15 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author khoran
 */

import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

public class RecordGroup implements  Iterable
{
    List records; //list of Record objects    
    private static Logger log=Logger.getLogger(RecordGroup.class);
    
    /** Creates a new instance of RecordGroup */
    public RecordGroup() 
    {
        records=new LinkedList();
    }
    public RecordGroup(List l)
    {
        records=l;
    }
    
    public void addRecord(Record r)
    {
        records.add(r);        
    }
    
    public void printRecords(Writer out, RecordVisitor visitor) 
        throws IOException
    {
        printRecords(out,visitor,true,true);
    }
    public void printRecords(Writer out, RecordVisitor visitor,boolean printHeader,boolean printFooter) 
        throws IOException
    {
        Record rec;
        boolean firstRecord=true;        
        for(Iterator i=records.iterator();i.hasNext();)
        {
            rec=(Record)i.next();            
            if(printHeader && firstRecord)
                rec.printHeader(out, visitor);
            firstRecord=false;
                        
            rec.printRecord(out, visitor);
            
            if(printFooter && !i.hasNext()) //last record
                rec.printFooter(out, visitor);
        }
    }
    
    public static Map buildRecordMap(RecordBuilder rb,List data,int start,int end)
    {
        return buildRecordMap(rb,data,0,start,end); //default to first column for key.
    }
    public static Map buildRecordMap(RecordBuilder rb,List data,int key,int start,int end)
    {
        return buildRecordMap(rb,data,new int[]{key},start,end); 
    }
    public static Map buildRecordMap(RecordBuilder rb,List data,int[] keys,int start,int end)
    {
        List row;
        RecordGroup rg;
        String key;
        Map output=new HashMap(); 
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            key=buildKey(row,keys);
            //try to find an existing RecordGroup for this key
            rg=(RecordGroup)output.get(key);
            if(rg==null)
            { //build and add a new RecordGroup                 
                rg=rb.buildRecordGroup();
                output.put(key,rg);
            }
            //create a new record from data, and add to existing 
            // RecordGroup
            rg.addRecord(rb.buildRecord(row.subList(start,end)));            
        }
        return output;
    }
    private static String buildKey(List data,int[] indecies)
    {
        if(indecies==null || indecies.length==0)
            return "";
        else if(indecies.length==1)
            return (String)data.get(indecies[0]);
        
        
        StringBuffer key=new StringBuffer();
        for(int i=0;i<indecies.length;i++)
        {
            key.append(data.get(indecies[i]));
            if(i+1 < indecies.length) //we have at least one more iteration
                key.append("_");
        }        
        return key.toString();
    }
    public Iterator iterator()
    {
        return records.iterator();
    }
}
