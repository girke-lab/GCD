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

public class TreeViewScript implements Script
{
    private static Logger log=Logger.getLogger(TreeViewScript.class);
    URL url;
    String dndBase="http://bioinfo.ucr.edu/projects/ClusterDB/clusters.d/";
    String clusterId;
    HttpServletResponse response; 
    
    /** Creates a new instance of TreeViewScript */
    public TreeViewScript(HttpServletRequest request,HttpServletResponse response)
    {
        try{
            url=new URL("http://bioinfo.ucr.edu/projects/internal/TreeBrowse/index.pl");        
            //url=new URL("http://bioinfo.ucr.edu/cgi-bin/chrplot2.pl");      
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
        String query="SELECT ci.method,m.model_accession "+
                     "FROM models as m, clusters as c, cluster_info as ci "+
                     "WHERE m.model_id=c.model_id AND c.cluster_id=ci.cluster_Id "+
                     "      AND ci.filename='"+clusterId+"'";                     
        List keys=Common.sendQuery(query);
        
        if(keys==null)
            return;
        
        printData(out, keys);
    }
    
    private void printForm(OutputStream out,List data)
    {// this is ugly but works
        PrintWriter pw=new PrintWriter(out);
        StringBuffer dndData=new StringBuffer();
        
        String dnd,link,lastMethod=null;            
        //link="http://138.23.191.152:8080/databaseWeb/index.jsp?fieldName=Id&input=";
        link="http://138.23.191.152:8080/databaseWeb/QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";
        //log.debug("number of rows: "+data.size());
        //log.debug("data="+data);
        try{
            for(Iterator i=data.iterator();i.hasNext();)
            {
                List row=(List)i.next();
          //      log.debug("row="+row);
                if(lastMethod==null || !lastMethod.equals(row.get(0)))
                {//new cluster
                    //log.debug("new cluster");
                    //dndData.append("data=3434");                
                    dnd=getDnd(clusterId,(String)row.get(0));
                    dndData.append(dnd+"\n");
                }                
                dndData.append(row.get(1)+"\t"+row.get(1)+"\tblue\t\t"+link+row.get(1)+"\n");
                lastMethod=(String)row.get(0);
            }            
        }catch(IOException e){
            log.error("io error: "+e.getMessage());
            Common.quit(pw, "no dnd file exists for this cluster");
            return;
        }
        //log.debug("dndData="+dndData);
        
        pw.println("<html><head/><body onLoad=\"form1.submit()\">");
        pw.println("<form name='form1' action='"+url+"' method='post' >");
        pw.println("<input type=hidden name='action' value='upload'>");
        pw.println("<input type=hidden name='data' value='"+dndData+"'>");
        pw.println("<input type=submit >");
        pw.println("</form></body></html>");
        pw.flush();
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
            
            String dnd,link,lastMethod=null;            
            link="http://138.23.191.152:8080/databaseWeb/QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";
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
                } 
                //log.debug("writing: "+row.get(1)+"\t"+row.get(1)+"\tblue\t\t"+link+row.get(1)+"\n");
                dos.writeBytes(URLEncoder.encode(row.get(1)+"\t"+row.get(1)+"\tblue\t\t"+link+row.get(1)+"\n","UTF-8"));
                lastMethod=(String)row.get(0);
            }            
            dos.writeBytes("&action=machine_upload");
            dos.close();
                                                           
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String newUrl=br.readLine();
            br.close();
            log.debug("newUrl="+newUrl);
            response.sendRedirect(newUrl);
            
            
//            byte[] bytes=new byte[1024];
//            int read;
//            while((read=is.read(bytes))!=-1)
//                out.write(bytes,0,read);
//                
//             is.close();   
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
            clusterType="blastClusters/";
        else if(method.startsWith("Domain Composition"))
            clusterType="hmmClusters/";
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
