/*
 * AlignToHmmScript.java
 *
 * Created on September 24, 2004, 7:59 AM
 */

package servlets.scriptInterfaces;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.querySets.*;

public class AlignToHmmScript implements Script
{
    URL url;
    private final int LIMIT=1000;
    boolean tooMany=false;
    private static Logger log=Logger.getLogger(AlignToHmmScript.class);
    
    /** Creates a new instance of AlignToHmmScript */
    public AlignToHmmScript() 
    {
        try{
            url=new URL("http://bioweb.ucr.edu/scripts/alignToHmm");        
        }catch(MalformedURLException e){e.printStackTrace();}
    }
    
    public String getContentType() 
    {
        return "text/html";
    }
    
    public void run(java.io.OutputStream out, java.util.List ids) 
    {
        tooMany=(ids.size() > LIMIT);
        List data=getData(ids);        
        if(data==null)
            return;
        printData(new PrintWriter(out),data);
    }
    private void printData(PrintWriter out,List data)
    {
        try{
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);            
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());

            dos.writeBytes("alignment=");
            for(Iterator i=data.iterator();i.hasNext();)
            {
                List row=(List)i.next(); //should be 2 elements per row
                if(row.size()!=2 || row.get(0).equals("") || row.get(1).equals(""))
                    continue;
                dos.writeBytes(">"+row.get(0)+"\n"+row.get(1)+"\n");
            }                        
            dos.close();

            //read result from script.  
            BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            if(tooMany)
                out.println("Number of sequences limited to 1000 <br>");
            while(in.ready())
                out.println(in.readLine());
            in.close();
        }catch(IOException e){
            out.println("could no open connection to "+url.getFile()+":"+e.getMessage());
        }finally{
            out.flush();
        }
    }
    private List getData(List ids)
    {        
        String query=QuerySetProvider.getScriptQuerySet().getAlignToHmmQuery(ids,LIMIT);
        return Common.sendQuery(query);
    }
}
