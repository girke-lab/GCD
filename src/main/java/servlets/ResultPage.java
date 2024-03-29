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
	boolean printBorder=true; // header and footer
    
    DescriptionManager dm=DescriptionManager.getInstance();
    
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
	 *  set whether to print the header and footer or not.
	 * @param b
	 */
	public void setPrintBorder(boolean b)
	{
		printBorder=b;
	}
    /**
     * Calls the various methods of {@link DataView} and {@link QueryWideView} to
     * print the result page.  Also prints the paging controls and the list of keys
     * not found at the end of the page.
     * @param out where to print html
     */    
    public void dipslayPage(PrintWriter out)
    {
//        int[] positions=new int[search.getDbCount()];
        //this does not actually work becuase we need dbNums[] here to
        //figure out with database we have.
        //But since we only have two databases, it doesn't really matter.
//        for(int i=0;i<positions.length;i++)
//            positions[i]=search.getDbStartPos(i);                        
        
        
		if(printBorder)
			dv.printHeader(out);
		else
			out.println("<link rel='stylesheet' type='text/css' href='style.css'>");

        if(!dv.getQueryWideView().printAllData())
        {
            printControls(out);        
            //printGotoLinks(out, Common.dbPrintNames, positions);
            printGotoLinks(out, search.getBookmarkLabels(),search.getBookmarkPositions());
        }
        out.println("<br>");
        dv.getQueryWideView().printButtons(out, hid, pos, search.getResults().size(), rpp);
                    
        List ids=search.getResults();
        if(dv.getQueryWideView().printAllData())
            dv.setIds(ids);
        else
        {
            int end=pos+rpp > ids.size()? ids.size() : pos+rpp;
            dv.setIds(new ArrayList(ids.subList(pos,end)));
        }
        
        out.println("<br><table cellspacing='0' cellpadding='0'><tr><td nowrap>");
        dv.getQueryWideView().printStats(out,search);            
        out.println("</td><td nowrap>");
        dv.printStats(out);
        out.println("</td><td>");
        dv.getQueryWideView().printGeneral(out, search,"after_stats");
        out.println("</td></tr></table>");
            
        dv.getQueryWideView().printGeneral(out, search,"",storage);        
        dv.printData(out);
        
        if(!dv.getQueryWideView().printAllData())
        {
            printControls(out);
            printGotoLinks(out, search.getBookmarkLabels(),search.getBookmarkPositions());
        }

        printMismatches(out,search.notFound());

		if(printBorder)
			dv.printFooter(out);
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
        if(pos > 0) //if we're not at the beginning
            out.println("<td> "+dm.wrapLink("start-arrow","<img src='images/right_right_arrow.jpg' border='0'/>",action+"&pos=0'")+"</td>");
        
        if(pos-rpp >= 0)        
            //out.println("<td><a href='"+action+"&pos="+(pos-rpp)+"'><img src='images/right_arrow.jpg' border='0'/></a></td>");
            out.println("<td> "+dm.wrapLink("prev-arrow","<img src='images/right_arrow.jpg' border='0'/>",action+"&pos="+(pos-rpp)+"'")+"</td>");
        if(pos+rpp < end)
            //out.println("<td><a href='"+action+"&pos="+(pos+rpp)+"'><img src='images/left_arrow.jpg' border='0' /></a></td>");        
            out.println("<td> "+dm.wrapLink("next-arrow","<img src='images/left_arrow.jpg' border='0'/>",action+"&pos="+(pos+rpp)+"'")+"</td>");
        
        if(pos+rpp < end) // if we're not at the end
            //out.println("<td><a href='"+action+"&pos="+(end-(end%rpp))+"'><img src='images/left_left_arrow.jpg' border='0' />t</a></td>");
            out.println("<td> "+dm.wrapLink("end-arrow","<img src='images/left_left_arrow.jpg' border='0'/>",action+"&pos="+(end-(end%rpp))+"'")+"</td>");
  
        out.println("</tr>");
        out.println("</table>");
    }
    
    private void printGotoLinks(PrintWriter out,Collection<String> names,Collection<Integer> positions)
    {  //print a link for each entry in names that jumps to the corresponding position        
        if(names.size()==0 || positions.size()!=names.size())
        {
            out.println("<br><p>");
            return;
        }
//        boolean isDifferent=false;
//        for(int i=1;i<positions.length;i++)
//            if(positions[i-1]!=positions[i])
//            {
//                isDifferent=true;
//                break;
//            }
//        if(!isDifferent)
//        { //all postions go to same place, so dont bother printing them.
//            out.println("<br><p>");
//            return;
//        }
        if(positions.size()==1 && positions.iterator().next()==0)
        { //we only have one link that goes nowhere
            out.println("<br><p>");
            return;            
        }
        
        String action="QueryPageServlet?hid="+hid;        
        out.println("<table><tr><td>&nbsp&nbsp&nbsp&nbsp Go to: </td>");
        
        for(Iterator nameItr=names.iterator(),posItr=positions.iterator();nameItr.hasNext();)
            out.println("<td><a href='"+action+"&pos="+posItr.next()+"'>"+nameItr.next()+"</a></td>");
        
        out.println("</tr></table>");        
    }
    
}
