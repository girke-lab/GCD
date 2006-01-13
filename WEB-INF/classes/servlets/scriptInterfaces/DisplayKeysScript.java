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
import servlets.querySets.*;

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
        //log.debug("got data: "+data);
        if(data==null)
            return;
        printData(new PrintWriter(out),data);
    }
    private void printData(PrintWriter out,List data)
    {        
        out.println("<PRE>");
        for(Iterator i=data.iterator();i.hasNext();)
            for(Iterator j=((List)i.next()).iterator();j.hasNext();)
                out.println(j.next());
        out.println("</PRE>");
        out.flush();
    }
    private List getData(List ids)
    {
        if(QuerySetProvider.getScriptQuerySet()==null)
            log.error("no sript query set in QuerySetProvider");
        return Common.sendQuery(QuerySetProvider.getScriptQuerySet().getDisplayKeysQuery(ids));
    }
    
    public String getContentType() {
        return "text/html";
    }
    
}
