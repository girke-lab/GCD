/*
 * ClusterDataView.java
 *
 * Created on August 11, 2004, 8:52 AM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;
import org.apache.log4j.Logger;
import servlets.ResultPage;
import servlets.dataViews.queryWideViews.*; 
import servlets.search.Search;
import servlets.querySets.*;

public class ClusterDataView implements DataView 
{   
    
    List seq_ids;
    int hid;
    String sortCol;
    int[] dbNums;
    
    private final int CLUSTER_ID_COL=0, CLUSTER_NAME_COL=1,
                      ARAB_SIZE_COL=2,  RICE_SIZE_COL=3,
                      SIZE_COL=4,METH_COL=5;
    private final int PFAM=0,BLAST=1;
    private static Logger log=Logger.getLogger(ClusterDataView.class);
    
    /** Creates a new instance of ClusterDataView       */
    public ClusterDataView() {
    }

    
    public void printHeader(java.io.PrintWriter out) 
    {
        Common.printHeader(out,"Cluster View");                
    }
    public void printData(java.io.PrintWriter out) 
    {
        List data=getData(seq_ids, sortCol, dbNums);
        printCounts(out,data);
        printSummary(out,data,dbNums,hid);
    }
    
    public void setData(String sortCol, int[] dbList, int hid) 
    {//this class expexts cluster_id numbers as input    
        this.sortCol=sortCol;
        this.hid=hid;
        this.dbNums=dbList;
    }
    public void setIds(java.util.List ids) 
    {
        this.seq_ids=ids;                
    }
   
    public QueryWideView getQueryWideView() 
    {
        return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out,"Total Query", new String[]{"Clusters"},
                    new Object[]{new Integer(search.getResults().size())});
            }
            public void printButtons(PrintWriter out, int hid,int pos,int size,int rpp){}            
        };
    }   
    
    private void printCounts(PrintWriter out,List data)
    {
        //out.println("clusters found on this page: "+data.size()+"<br>");
    }
    private void printSummary(PrintWriter out, List data, int[] dbNums, int hid)
    {
        String titleColor="AAAAAA", dataColor="D3D3D3";
        int clusterType;
        out.println("<TABLE width='100%' align='center' border='1' cellspacing='0' bgcolor='"+dataColor+"'>");
        for(Iterator i=data.iterator();i.hasNext();) 
        {
            List row=(List)i.next();            
            
            clusterType=((String)row.get(METH_COL)).equals("Domain Composition")? PFAM:BLAST;
                        
            out.println("<TR><TD colspan='7' bgcolor='FFFFFF' border='0'>&nbsp</TD></TR>");
            out.println("<TR bgcolor='"+titleColor+"'><TH>Cluster Id</TH>" +
                        "<TH colspan='6'>Cluster Name</TH></TR>");
            out.println("<TR>");
            if(clusterType==PFAM)
            {
                out.println("<TD>");
                out.println("<a href='pfamOptions.jsp?accession="+row.get(CLUSTER_ID_COL)+"'>"+row.get(CLUSTER_ID_COL)+"</a>");                     
//                 StringTokenizer tok=new StringTokenizer((String)row.get(CLUSTER_ID_COL),"_");
//                 while(tok.hasMoreTokens())
//                 {
//                     String n=tok.nextToken();                     
//                     out.println("<a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?"+n.substring(0,n.indexOf('.'))+"'>"+n+"</a>");
//                     if(tok.hasMoreTokens())
//                         out.println("_");
//                 }         
                 out.println("</TD>");
            }
            else
                out.println("<TD>"+row.get(CLUSTER_ID_COL)+"</TD>");
            out.println("<TD colspan='6'>"+(row.get(CLUSTER_NAME_COL)!=null?row.get(CLUSTER_NAME_COL):"&nbsp")+"</TD>");
            out.println("</TR>");
            out.println("<TR bgcolor='"+titleColor+"'><TH>Method</TH><TH nowrap>Total Size</TH>" +
                        "<TH nowrap>Arabidopsis Count</TH><TH nowrap>Rice Count</TH><TH>Memebers</TH>" +
                        "<TH>Alignment</TH><TH>Tree</TH></TR>");
            out.println("<TR>");
            
            
            out.println("<TD>"+row.get(METH_COL)+"</TD>"+
                        "<TD>"+row.get(SIZE_COL)+"</TD>" +
                        "<TD>"+row.get(ARAB_SIZE_COL)+"</TD>" +
                        "<TD>"+row.get(RICE_SIZE_COL)+"</TD>");
            out.println("<TD><a href='/databaseWeb/index.jsp?fieldName=Cluster Id&limit=0&input="+row.get(CLUSTER_ID_COL)+"'>Retrieve</a></TD>");
            if(Integer.parseInt((String)row.get(SIZE_COL)) > 1 && 
                  !((String)row.get(METH_COL)).endsWith("_50") && 
                  !((String)row.get(METH_COL)).endsWith("_70"))
            {
//                String base="http://bioinfo.ucr.edu/projects/ClusterDB/clusters.d/";
//                if(clusterType==PFAM)
//                    base+="hmmClusters/";
//                else
//                    base+="blastClusters/";
                
//                csLink=webBase+"cgi-bin/getClusterFiles.pl?cid="+cs.clusterNum+ 
//                         "&cluster_type="+cs.method+"&file_type=html"; 
                
                String fileUrl="http://bioinfo.ucr.edu/cgi-bin/getClusterFiles.pl?cid="+row.get(CLUSTER_ID_COL)+
                        "&cluster_type="+row.get(METH_COL)+"&file_type=";
                out.println("<TD nowrap>");
                //out.println("<a href='"+base+row.get(CLUSTER_ID_COL)+".html'>Consensus shaded</a>&nbsp&nbsp");
                out.println("<a href='"+fileUrl+"html'>Consensus shaded</a>&nbsp&nbsp");
                out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/domainShader?cid="+row.get(CLUSTER_ID_COL)+"'>Domain shaded</a>");
                out.println("</TD>");
                
                
                String treeViewLink="DispatchServlet?hid="+hid+"&script=treeViewer.pl&range=0&clusterId="+row.get(CLUSTER_ID_COL);
                
                out.println("\t\t<TD><a href='"+treeViewLink+"'>view</a></TD>");
                //out.println("<TD><a href='"+base+row.get(CLUSTER_ID_COL)+".jpg'>view</a></TD>");   
                //out.println("<TD><a href='"+fileUrl+"jpg'>view</a></TD>");   
            }
            else
                out.println("<td>&nbsp</td><td>&nbsp</td>");
            out.println("</TR>");
        }
        out.println("</TABLE>");
    }
       
    private List getData(List input, String order, int[] db)
    {
        return Common.sendQuery(QuerySetProvider.getDataViewQuerySet().getClusterDataViewQuery(input, order, db));         
    }
    
     private String buildClusterViewStatement(String conditions, String order, int[] DBs)
    {
        StringBuffer query=new StringBuffer();        
  
        query.append("SELECT DISTINCT filename, name,arab_count,rice_count,size,method " +
            "FROM cluster_info " +
            "WHERE ");
        query.append(" ("+conditions+" )");
                
        query.append("ORDER BY "+order);        
        log.info("cluster view query: "+query);
        return query.toString();        
    }          
     
     public void printStats(java.io.PrintWriter out) {
         Common.printStatsTable(out,"On This Page", new String[]{"Clusters"},new Object[]{new Integer(seq_ids.size())});         
     }
     
     public void setSortDirection(String dir)
     {
     }     
     
      
     
     
}
