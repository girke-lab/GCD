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

public class RecordGroup 
{
    List records; //list of Record objects    
    
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
        Record rec;
        boolean firstRecord=true;
        for(Iterator i=records.iterator();i.hasNext();)
        {
            rec=(Record)i.next();
            if(firstRecord)
            {
                rec.printHeader(out, visitor);
                firstRecord=false;
            }            
            rec.printRecord(out, visitor);
            if(!i.hasNext()) //last record
                rec.printFooter(out, visitor);
        }
    }
    
    public static Map buildRecordMap(RecordBuilder rb,List data,int start,int end)
    {
        return buildRecordMap(rb,data,0,start,end); //default to first column for key.
    }
    public static Map buildRecordMap(RecordBuilder rb,List data,int key,int start,int end)
    {
        List row;
        RecordGroup rg;
        Map output=new HashMap(); 
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            rg=(RecordGroup)output.get(row.get(key));
            if(rg==null)
            {                
                rg=rb.buildRecordGroup();
                output.put(row.get(key),rg);
            }
            rg.addRecord(rb.buildRecord(row.subList(start,end)));            
        }
        return output;
    }
    public Iterator iterator()
    {
        return records.iterator();
    }
}
