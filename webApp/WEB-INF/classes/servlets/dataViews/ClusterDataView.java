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

public class ClusterDataView implements DataView 
{   
    
    List seq_ids;
    int limit,hid;
    String sortCol;
    int[] dbNums;
    
    private final int CLUSTER_ID_COL=0, CLUSTER_NAME_COL=1,
                      ARAB_SIZE_COL=2,  RICE_SIZE_COL=3,
                      SIZE_COL=4;
    private final int PFAM=0,BLAST=1;
    
    /** Creates a new instance of ClusterDataView       */
    public ClusterDataView() {
    }

    
    public void printHeader(java.io.PrintWriter out) 
    {
    }
    public void printData(java.io.PrintWriter out) 
    {
        List data=getData(seq_ids, sortCol, limit, dbNums);
        printCounts(out,data);
        printSummary(out,data,dbNums,hid);
    }
    
    public void setData(java.util.List ids, String sortCol, int limit, int[] dbList, int hid) 
    {//this class expexts cluster_id numbers as input
        this.seq_ids=ids;
        this.limit=limit;
        this.sortCol=sortCol;
        this.hid=hid;
        this.dbNums=dbList;
    }
    private void printCounts(PrintWriter out,List data)
    {
        out.println("clusters found: "+data.size()+"<br>");
    }
    private void printSummary(PrintWriter out, List data, int[] dbNums, int hid)
    {
        String titleColor="AAAAAA", dataColor="D3D3D3";
        int clusterType;
        out.println("<TABLE width='100%' align='center' border='1' cellspacing='0' bgcolor='"+dataColor+"'>");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            List row=(List)i.next();
            //System.out.println(row);
            
            clusterType=((String)row.get(CLUSTER_ID_COL)).startsWith("PF")? PFAM:BLAST;
                        
            out.println("<TR><TD colspan='7' bgcolor='FFFFFF' border='0'>&nbsp</TD></TR>");
            out.println("<TR bgcolor='"+titleColor+"'><TH>Cluster Id</TH>" +
                        "<TH colspan='6'>Cluster Name</TH></TR>");
            out.println("<TR>");
            if(clusterType==PFAM)
            {
                out.println("<TD>");
                 StringTokenizer tok=new StringTokenizer((String)row.get(CLUSTER_ID_COL),"_");
                 while(tok.hasMoreTokens())
                 {
                     String n=tok.nextToken();                     
                     out.println("<a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?"+n.substring(0,n.indexOf('.'))+"'>"+n+"</a>");
                     if(tok.hasMoreTokens())
                         out.println("_");
                 }         
                 out.println("</TD>");
            }
            else
                out.println("<TD>"+row.get(CLUSTER_ID_COL)+"</TD>");
            out.println("<TD colspan='6'>"+row.get(CLUSTER_NAME_COL)+"</TD>");
            out.println("</TR>");
            out.println("<TR bgcolor='"+titleColor+"'><TH>Method</TH><TH nowrap>Total Size</TH>" +
                        "<TH nowrap>Arabidopsis Count</TH><TH nowrap>Rice Count</TH><TH>Memebers</TH>" +
                        "<TH>Alignment</TH><TH>Tree</TH></TR>");
            out.println("<TR>");
            if(clusterType==PFAM)
                out.println("<TD>Domain Composition</TD>");
            else
                out.println("<TD>BLASTCLUST</TD>");
            
            out.println("<TD>"+row.get(SIZE_COL)+"</TD>" +
                        "<TD>"+row.get(ARAB_SIZE_COL)+"</TD>" +
                        "<TD>"+row.get(RICE_SIZE_COL)+"</TD>");
            out.println("<TD><a href='/databaseWeb/index.jsp?fieldName=Cluster Id&limit=0&input="+row.get(CLUSTER_ID_COL)+"'>Retrieve</a></TD>");
            if(Integer.parseInt((String)row.get(SIZE_COL)) > 1)
            {
                String base="http://bioinfo.ucr.edu/projects/ClusterDB/clusters.d/";
                if(clusterType==PFAM)
                    base+="hmmClusters/";
                else
                    base+="blastClusters/";
                out.println("<TD nowrap>");
                out.println("<a href='"+base+row.get(CLUSTER_ID_COL)+".html'>Consensus shaded</a>&nbsp&nbsp");
                out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/domainShader?cid="+row.get(CLUSTER_ID_COL)+"'>Domain shaded</a>");
                out.println("</TD>");
                out.println("<TD><a href='"+base+row.get(CLUSTER_ID_COL)+".jpg'>view</a></TD>");   
            }
            out.println("</TR>");
        }
        out.println("</TABLE>");
    }
       
    private List getData(List input, String order, int limit, int[] db)
    {
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        conditions.append("cluster_info.cluster_id in (");
        for(Iterator it=input.iterator();it.hasNext() && count++ < limit;)
        {
            conditions.append((String)it.next());
            if(it.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");
        rs=Common.sendQuery(buildClusterViewStatement(conditions.toString(),order,limit,db));
        return rs;
    }
    
     private String buildClusterViewStatement(String conditions, String order ,int limit , int[] DBs)
    {
        StringBuffer query=new StringBuffer();
        
//        query.append("SELECT s.genome,ci.filename, s.primary_key,c.model_id, g.go "+
//                "FROM clusters as c, cluster_info as ci, sequences as s LEFT JOIN go as g USING (seq_id) "+
//                "WHERE ci.cluster_id=c.cluster_id AND c.seq_id=s.seq_id ");
  
        query.append("SELECT DISTINCT filename, name,arab_count,rice_count,size " +
            "FROM cluster_info " +
            "WHERE ");
        query.append(" ("+conditions+" )");
                
        query.append("ORDER BY "+order);
        //query.append(" LIMIT "+limit);
        System.out.println("cluster view query: "+query);
        return query.toString();        
    }          
}
