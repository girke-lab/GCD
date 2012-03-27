/*
 * GoSlimCountsScript.java
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

public class GoSlimCountsScript implements Script 
{
     URL url;
    private static Logger log=Logger.getLogger(GoSlimCountsScript.class);
    
    /** Creates a new instance of GoSlimCountsScript */
    public GoSlimCountsScript() 
    {
        try{
            url=new URL("http://bioweb.ucr.edu/scripts/goSlimCounts");        
        }catch(MalformedURLException e){e.printStackTrace();}
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
        try{
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);            
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());

            //write parameters to output stream
            for(Iterator i=data.iterator();i.hasNext();)
            {
                List row=(List)i.next();
                if(row.get(1)==null){ //no go number
                    dos.writeBytes("mising="+row.get(0)+"&");
                }
                else{
                    dos.writeBytes("go_numbers="+row.get(0)+"_"+row.get(1)+"&");
                }
            }
            dos.writeBytes("total_seq_count="+data.size());
            dos.close();

            //read result from script.  
            BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
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
        return Common.sendQuery(QuerySetProvider.getScriptQuerySet().getGoSlimCountsQuery(ids));
    }
    
    public String getContentType() {
        return "text/html";
    }
    
}
