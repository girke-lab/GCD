/*
 * TreeViewScript.java
 *
 * Created on February 4, 2005, 9:41 AM
 */

package servlets.scriptInterfaces;

/**
 *
 * @author khoran
 */

import org.apache.log4j.Logger;
import java.util.*;
import servlets.*;
import java.net.*;
import java.io.*;
import javax.servlet.http.*;
import servlets.querySets.*;

public class TreeViewScript implements Script
{
    private static Logger log=Logger.getLogger(TreeViewScript.class);
    URL url;
    String dndBase="http://bioweb.ucr.edu/databaseWeb/clusters/";
    String clusterId;
    HttpServletResponse response; 
    
    /** Creates a new instance of TreeViewScript */
    public TreeViewScript(HttpServletRequest request,HttpServletResponse response)
    {
        try{
            //url=new URL("http://bioinfo.ucr.edu/projects/internal/TreeBrowse/index.pl");        
            //url=new URL("http://bioinfo.ucr.edu/cgi-bin/chrplot2.pl");      
            url=new URL(" http://www.biocluster.ucr.edu/projects/internal/TreeBrowse/index.pl");
        }catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        clusterId=request.getParameter("clusterId");
        this.response=response;
    }

    public String getContentType()
    { 
        return "text/html";
    }

    public void run(java.io.OutputStream out, java.util.List ids)
    {
        if(clusterId==null)
        {
            Common.quit(new PrintWriter(out), "no cluster id given");
            return;
        }
        
        List keys=Common.sendQuery(QuerySetProvider.getScriptQuerySet().getTreeViewQuery(clusterId));
        
        if(keys==null)
            return;
        
        printData(out, keys);
    }
    
  
    
    private void printData(OutputStream out,List data)
    {//this is better, but does not preserve the context of the other page,
        //so none of the relative links work.
        log.debug("printing data");
        try{
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");            
            conn.setDoInput(true);
            conn.setDoOutput(true);            
            DataOutputStream dos=new DataOutputStream(conn.getOutputStream());
           
//            String testData="(((At2g31360.1:0.17120,At1g06080.1:0.12539):0.15959,((At1g06350.1:0.11652,At1g06360.1:0.11664):0.08670,((At1g06090.1:0.05493,At1g06120.1:0.06737):0.05045,At1g06100.1:0.13378):0.05571):0.1506):0.11621,(At3g15850.1:0.35574,At3g15870.1:0.46298):0.00718);\n" +
//                    "row.get(1)+\t+row.get(1)+\tblue\t\t+link+row.get(1)+\n";
//               
//            dos.writeBytes("data=");
//            dos.writeBytes(URLEncoder.encode(testData,"UTF-8"));
//            dos.writeBytes("&action=machine_upload");
//            dos.close();
            
            
            String dnd,link,lastMethod=null;            
            link="http://bioweb.ucr.edu/databaseWeb/QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";
            log.debug("number of rows: "+data.size());
            log.debug("data="+data);
            for(Iterator i=data.iterator();i.hasNext();)
            {
                List row=(List)i.next();
                //log.debug("row="+row);
                if(lastMethod==null || !lastMethod.equals(row.get(0)))
                {//new cluster
                    log.debug("new cluster");
                    
                    dos.writeBytes("data=");
                    try{
                        dnd=getDnd(clusterId,(String)row.get(0));
                    }catch(FileNotFoundException e){
                        Common.quit(new PrintWriter(out),"no tree found for "+clusterId);
                        return;
                    }
                    dos.writeBytes(URLEncoder.encode(dnd+"\n","UTF-8"));
                    //log.debug(URLEncoder.encode(dnd+"\n","UTF-8"));
                } 
                //log.debug("writing: "+row.get(1)+"\t"+row.get(1)+"\tblue\t\t"+link+row.get(1)+"\n");
                dos.writeBytes(URLEncoder.encode(row.get(1)+"\t"+row.get(1)+"\tblue\t\t"+link+row.get(1)+"\n","UTF-8"));
                //log.debug(URLEncoder.encode(row.get(1)+"\t"+row.get(1)+"\tblue\t\t"+link+row.get(1)+"\n","UTF-8"));
                lastMethod=(String)row.get(0);
            }            
            dos.writeBytes("&action=machine_upload");
            //log.debug("&action=machine_upload");
            dos.close();
                                   
            
            
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String newUrl=br.readLine();
            br.close();
            log.debug("newUrl="+newUrl);
            response.sendRedirect(newUrl);                        

        }catch(IOException e){
            new PrintWriter(out).println("could no open connection to "+url.getFile()+":"+e.getMessage());
            log.error("could no open connection to "+url.getFile()+":"+e.getMessage());
            e.printStackTrace();
        }finally{
            try{out.flush();}catch(IOException e){}
        }
    }
    
    private String getDnd(String clusterId,String method) throws IOException
    {
        log.debug("getting dnd for cluster "+clusterId+", of method "+method);
        String clusterType;
        if(method.startsWith("BLASTCLUST"))
            clusterType="blastClusters/data/";
        else if(method.startsWith("Domain Composition"))
            clusterType="hmmClusters/data/";
        else
        {
            log.error("invalid cluster method: "+method);
            return "";
        }
        log.debug("opening url");
        URL dnd=new URL(dndBase+clusterType+clusterId+".dnd");
        log.debug("url="+dnd);
        BufferedReader br=new BufferedReader(new InputStreamReader(dnd.openStream()));
        StringBuffer str=new StringBuffer();
        String s;
        log.debug("reading dnd file");
        while((s=br.readLine())!=null) //read contents of dnd file
            str.append(s);
        log.debug("got dnd : "+str);
        return str.toString();        
    }
}
