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

/**
 * Uses a {@link DataView} object to print a result page.
 */
public class ResultPage 
{
    DataView dv;
    Search search;
    int hid,rpp,pos;    
    Map storage;
    
    /**
     * Creates a new instance of ResultPage
     * @param dv the dataView object to print
     * @param s search object to get id list from
     * @param pos current position in id list
     * @param hid history id
     * @param rpp records per page
     * @param stor A Map used for persistant storage
     */
    public ResultPage(DataView dv,Search s,int pos, int hid, int rpp,Map stor)    
    {
        this.dv=dv;
        this.search=s;
        this.hid=hid;
        this.rpp=rpp;
        this.pos=pos;
        this.storage=stor;
    }
    
    /**
     * Calls the various methods of {@link DataView} and {@link QueryWideView} to
     * print the result page.  Also prints the paging controls and the list of keys
     * not found at the end of the page.
     * @param out where to print html
     */    
    public void dipslayPage(PrintWriter out)
    {
        int[] positions=new int[search.getDbCount()];
        //this does not actually work becuase we need dbNums[] here to
        //figure out with database we have.
        //But since we only have two databases, it doesn't really matter.
        for(int i=0;i<positions.length;i++)
            positions[i]=search.getDbStartPos(i);
                
        dv.printHeader(out);
        if(!dv.getQueryWideView().printAllData())
        {
            printControls(out);        
            printGotoLinks(out, Common.dbPrintNames, positions);
        }
        out.println("<br>");
        dv.getQueryWideView().printButtons(out, hid, pos, search.getResults().size(), rpp);
                    
        List ids=search.getResults();
        if(dv.getQueryWideView().printAllData())
            dv.setIds(ids);
        else
        {
            int end=pos+rpp > ids.size()? ids.size() : pos+rpp;
            dv.setIds(ids.subList(pos,end));
        }
        
        out.println("<table cellspacing='0' cellpadding='0'><tr><td>");
        dv.getQueryWideView().printStats(out,search);            
        out.println("</td><td>");
        dv.printStats(out);
        out.println("</td></tr></table>");
        
        dv.getQueryWideView().printGeneral(out, search,"",storage);        
        dv.printData(out);
        
        if(!dv.getQueryWideView().printAllData())
        {
            printControls(out);
            printGotoLinks(out, Common.dbPrintNames, positions);
        }

        printMismatches(out,search.notFound());
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
        //out.println("<td><a href='"+action+"&pos=0'>Start</a></td>");
        out.println("<td><a href='"+action+"&pos=0'><img src='images/right_right_arrow.jpg' border='0'/></a></td>");
        if(pos-rpp >= 0)        
            //out.println("<td><a href='"+action+"&pos="+(pos-rpp)+"'>Previous</a></td>");
            out.println("<td><a href='"+action+"&pos="+(pos-rpp)+"'><img src='images/right_arrow.jpg' border='0'/></a></td>");
        if(pos+rpp < end)
            //out.println("<td><a href='"+action+"&pos="+(pos+rpp)+"'>Next</a></td>");        
            out.println("<td><a href='"+action+"&pos="+(pos+rpp)+"'><img src='images/left_arrow.jpg' border='0' /></a></td>");        
        //out.println("<td><a href='"+action+"&pos="+(end-(end%rpp))+"'>End</a></td>");
        out.println("<td><a href='"+action+"&pos="+(end-(end%rpp))+"'><img src='images/left_left_arrow.jpg' border='0' /></a></td>");
  
        out.println("</tr>");
        out.println("</table>");
    }
    
    private void printGotoLinks(PrintWriter out,Object[] names,int[] positions)
    {  //print a link for each entry in names that jumps to the corresponding position
        if(names.length==0 || positions.length==0 || positions.length!=names.length){
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
