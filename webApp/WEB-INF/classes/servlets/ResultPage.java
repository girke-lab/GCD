/*
 * ResultPage.java
 *
 * Created on September 15, 2004, 2:19 PM
 */

package servlets;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.dataViews.*;
import servlets.search.*;
import javax.servlet.http.*;

public class ResultPage 
{
    DataView dv;
    Search search;
    int hid,rpp,pos;
    HttpServletRequest request;
    
    
    //available features
    public static final int BUTTONS=0, ALL=1;
    
    /** Creates a new instance of ResultPage */
    public ResultPage(DataView dv,Search s,HttpServletRequest request, int hid, int rpp)    
    {
        this.dv=dv;
        this.search=s;
        this.hid=hid;
        this.rpp=rpp;
        this.request=request;        
        try{
            pos=Integer.parseInt(request.getParameter("pos"));
        }catch(Exception e){
            pos=0;
        }
    }
    
    public void dipslayPage(PrintWriter out)
    {
        int[] positions=new int[search.getDbCount()];
        for(int i=0;i<positions.length;i++)
            positions[i]=search.getDbStartPos(i);
        
        Common.printHeader(out);        
        dv.printHeader(out);
        printControls(out);
        printGotoLinks(out, Common.dbPrintNames, positions);
        if(dv.hasFeature(BUTTONS))
            Common.printButtons(out,hid,pos,search.getResults().size(),rpp); 
            
        List ids=search.getResults();
        if(dv.hasFeature(ALL))
            dv.setIds(ids);
        else
        {
            int end=pos+rpp > ids.size()? ids.size() : pos+rpp;
            dv.setIds(ids.subList(pos,end));
        }
        
        out.println("<table cellspacing='0' cellpadding='0'><tr><td>");
        printTotals(out); 
        out.println("</td><td>");
        dv.printStats(out);
        out.println("</td></tr></table>");
        
        dv.printData(out);

        
        printMismatches(out,search.notFound());
    }
    private void printTotals(PrintWriter out)
    {
        out.println("<table border='1' cellspacing='0' bgcolor='"+Common.dataColor+"'>");
        out.println("<tr  bgcolor='"+Common.titleColor+"'><th colspan='3'>Total Query</th></tr>");
        out.println("<tr  bgcolor='"+Common.titleColor+"'><th>Loci</th><th>Models</th><th>Clusters</th></tr>");
        out.println("<tr>");
        out.println("<td>"+search.getResults().size()+"</td>");
        if(search.getStats()!=null && search.getStats().size()==2)
        {
            out.println("<td>"+search.getStats().get(0)+"</td>");
            out.println("<td>"+search.getStats().get(1)+"</td>");
        }
        else
            out.println("<td>&nbsp</td><td>&nbsp</td>");
        out.println("</tr></table>");
    }
    private void printMismatches(PrintWriter out,List keys)
    {
        if(keys.size()==0) //don't print anything if there are no missing keys
            return;
        out.println("Keys not returned:");
        for(int i=0;i<keys.size();i++)
        {
            if(i!=0)
                out.println(", ");
            out.println(keys.get(i));
        }
    }         
    
    private void printControls(PrintWriter out)
    {
        String action="QueryPageServlet?hid="+hid;        
        int end=search.getResults().size();
        
        out.println("<table align='left' border='0'>");
        out.println("<tr>");        
        out.println("<td><a href='"+action+"&pos=0'>Start</a></td>");
        if(pos-rpp >= 0)        
            out.println("<td><a href='"+action+"&pos="+(pos-rpp)+"'>Previous</a></td>");
        if(pos+rpp < end)
            out.println("<td><a href='"+action+"&pos="+(pos+rpp)+"'>Next</a></td>");        
        out.println("<td><a href='"+action+"&pos="+(end-(end%rpp))+"'>End</a></td>");
  
        out.println("</tr>");
        out.println("</table>");
    }
    
    private void printGotoLinks(PrintWriter out,Object[] names,int[] positions)
    {  //print a link for each entry in names that jumps to the corresponding position
        if(names.length==0 || positions.length==0){
            out.println("<br><p>");
            return;
        }
        
        String action="QueryPageServlet?hid="+hid;        
        out.println("<table><tr><td>&nbsp&nbsp&nbsp&nbsp Go to: </td>");
        for(int i=0;i<names.length;i++)        
            out.println("<td><a href='"+action+"&pos="+positions[i]+"'>"+names[i]+"</a></td>");
        out.println("</tr></table>");        
    }
    
}
