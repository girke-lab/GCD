/*
 * CompositeFormat.java
 *
 * Created on September 6, 2005, 11:42 AM
 *
 */

package servlets.dataViews.records;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;


/**
 *
 * @author khoran
 */
public class CompositeFormat
{
    
    private static Logger log=Logger.getLogger(CompositeFormat.class);
    /** Creates a new instance of CompositeFormat */
    public CompositeFormat()
    {
    }
    
    public void printHeader(Writer out, RecordVisitor visitor,Iterable ib)
        throws IOException
    {        
    }    
    public void printRecords(Writer out, RecordVisitor visitor,Iterable ib) 
        throws IOException
    {
       boolean isFirst=true;
       Record r;
       Iterator i=ib.iterator();
       //log.debug("using default composite format");
       while(i.hasNext())
       {
           r=(Record)i.next();
           
           if(isFirst)
               r.printHeader(out, visitor);
           isFirst=false;
           
           r.printRecord(out, visitor);
           
           if(!i.hasNext())
               r.printFooter(out, visitor);
       } 
    }    
    public void printFooter(Writer out, RecordVisitor visitor, Iterable ib)
        throws IOException
    {        
    }
    
}
