/*
 * ChrPlotScript.java
 *
 * Created on August 27, 2004, 10:40 AM
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

public class ChrPlotScript  implements Script 
{
     URL url;
    private static Logger log=Logger.getLogger(ChrPlotScript.class);
    
    /** Creates a new instance of ChrPlotScript */
    public ChrPlotScript() 
    {
        try{
            url=new URL("http://"+servlets.Common.hostname+"/scripts/chrplot.pl");        
        }catch(MalformedURLException e){e.printStackTrace();}
    }
    
    public void run(java.io.OutputStream out, java.util.List ids) 
    {    
        List data=getData(ids);
        //log.debug("got data: "+data);
        if(data==null)
            return;
        printData(out,data);
    }
    private void printData(OutputStream out,List data)
    {
        try{
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);            
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());

            //write parameters to output stream
            for(Iterator i=data.iterator();i.hasNext();)
                dos.writeBytes("accession="+((List)i.next()).get(0)+"&");            
            dos.close();

            InputStream is=conn.getInputStream();
            byte[] bytes=new byte[1000];
            int read;
            while((read=is.read(bytes))!=-1)
                out.write(bytes,0,read);
                
             is.close();   
        }catch(IOException e){
            new PrintWriter(out).println("could not open connection to "+url.getFile()+":"+e.getMessage());
        }finally{
            try{out.flush();}catch(IOException e){}
        }
    }
    private List getData(List ids)
    {
        return Common.sendQuery(QuerySetProvider.getScriptQuerySet().getChrPlotQuery(ids));
    }
    
    public String getContentType() {         
        return "text/html";    
    }
    
}
