/*
 * CompositeFormat.java
 *
 * Created on September 6, 2005, 11:42 AM
 *
 */

package servlets.dataViews.records.formats;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.dataViews.records.*;

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
    
    public void printRecords(Writer out, RecordVisitor visitor,Iterator i) 
        throws IOException
    {
       boolean isFirst=true;
       Record r;
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
    public void printRecords(Writer out, RecordVisitor visitor,Collection records)
        throws IOException
    { // default implementation
        printRecords(out,visitor,records.iterator());
    }
    
}
