/*
 * DefaultQueryWIdeView.java
 *
 * Created on September 22, 2004, 8:54 AM
 */

package servlets.dataViews.queryWideViews;

/**
 *
 * @author  khoran
 */

import servlets.search.Search;
import servlets.Common;
import java.io.*;
import java.util.Map;
/**
 * Provides a default implementation of {@link QueryWideView }.
 */
public class DefaultQueryWideView implements QueryWideView
{
    
    /** Creates a new instance of DefaultQueryWIdeView */
    public DefaultQueryWideView() 
    {
    }
    
  
    /**
     * prints some buttons for applying operations to an arrbitrary subset of data.
     * Printed buttons are:
     * All Gene Structures
     * Chr Map
     * Go Slim Counts
     * Key List
     * Align to Hmm
     * @param out for output
     * @param hid current hid
     * @param pos current pos
     * @param end index of last key
     * @param rpp results per page
     */
    public void printButtons(java.io.PrintWriter out, int hid, int pos, int end, int rpp) 
    {
        out.println("<FORM METHOD='GET' ACTION='DispatchServlet'>");
        out.println("<INPUT type=hidden name='hid' value='"+hid+"'>");
        out.println("<INPUT type=hidden name='script'>");
        
        out.println("<TABLE border='0' ><TR>");        
        out.println("<TD><INPUT type='submit' value='All Gene Structures' " +
                    " onClick=\"javascript: script.value='multigene.pl'; submit();\" ></TD>");
        out.println("<TD><INPUT type='submit' value='Chr Map' "+       
                    " onClick=\"javascript: script.value='chrplot.pl'; submit();\"></TD>");
        out.println("<TD><INPUT type='submit' value='Go Slim Counts' "+
                    " onClick=\"javascript: script.value='goSlimCounts'; submit();\"></TD>");
        out.println("<TD><INPUT type='submit' value='Key List' "+
                " onClick=\"javascript: script.value='displayKeys.pl'; submit();\"></TD>");
        out.println("<TD><INPUT type='submit' value='Align to Hmm' "+
                " onClick=\"javascript: script.value='alignToHmm'; submit();\"></TD>");
        out.println("</TR><TR>");
        out.println("<TD colspan='4'> Apply buttons to:&nbsp&nbsp ");
        out.println("<SELECT name='range' >" + 
                            "<OPTION value='0-"+end+"' >All" +
                            "<OPTION selected value='"+pos+"-"+(pos+rpp)+"'>Page" +
                            "<OPTION value='custom'>Range: " +
                        "</SELECT>&nbsp ");
        out.println("<INPUT type=text name='range' value=''></TD>");
        out.println("</TR></TABLE></FORM>");
        //out.println("range must be in the form a-b,c-d,...,x-y");
    }
    
    /**
     * Tries to print the number of sequences, models, and clusters for the
     * current page.
     * @param out output
     * @param search data source
     */
    public void printStats(PrintWriter out, Search search)  
    {
        Object[] values=new Object[4];
        values[0]=new Integer(search.getResults().size());        
        Map stats;
        
        if(search==null || search.getStats()==null )
        {    values[1]=null;values[2]=null; }
        else
        {
            stats=search.getStats();
            values[1]=stats.get("models");
            values[2]=stats.get("BLASTCLUST_35");
            values[3]=stats.get("Domain Composition");
        }
        
        Common.printStatsTable(out,"Total Query",
            new String[]{"Loci","Models","Blast_35 Clusters","HMM Clusters"},values);         
    }
    
    /**
     * returns false
     * @return false
     */
    public boolean printAllData() 
    {
        return false;
    }    
   
    /**
     * this method is empty
     * @param out for output
     * @param search data source
     * @param position page position
     */
    public void printGeneral(PrintWriter out, Search search, String position)
    {
    }    
    
    /**
     * this method is empty.
     * @param out for output
     * @param search data source
     * @param position position in page
     * @param storage persistant storage
     */
    public void printGeneral(PrintWriter out, Search search, String position, Map storage)
    {
    }
    
}
