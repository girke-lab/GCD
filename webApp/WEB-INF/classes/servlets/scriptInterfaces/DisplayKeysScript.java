/*
 * DisplayKeysScript.java
 *
 * Created on August 26, 2004, 4:24 PM
 */

package servlets.scriptInterfaces;

/**
 *
 * @author  khoran
 */

import java.net.*;
import java.util.*;
import servlets.Common;
import java.io.*;
import org.apache.log4j.Logger;

public class DisplayKeysScript implements Script 
{
   private static Logger log=Logger.getLogger(DisplayKeysScript.class);
    
    /** Creates a new instance of DisplayKeysScript */
    public DisplayKeysScript() 
    {
   
    }    
    
    public void run(java.io.OutputStream out, java.util.List ids) 
    {
        List data=getData(ids);
        log.debug("got data: "+data);
        if(data==null)
            return;
        printData(new PrintWriter(out),data);
    }
    private void printData(PrintWriter out,List data)
    {        
        for(Iterator i=data.iterator();i.hasNext();)
            for(Iterator j=((List)i.next()).iterator();j.hasNext();)
                out.println(j.next());
        out.flush();
    }
    private List getData(List ids)
    {
        StringBuffer condition=new StringBuffer();
        String query;
        
        condition.append(" s.seq_id in (");
        for(Iterator i=ids.iterator();i.hasNext();)
        {
            condition.append(i.next());
            if(i.hasNext())
                condition.append(",");
        }
        condition.append(")");
        
        query="SELECT s.primary_key FROM sequences as s WHERE "+condition;
        log.info("DisplayKeys.pl query: "+query);
        return Common.sendQuery(query);
    }
    
    public String getContentType() {
        return "text/html";
    }
    
}
