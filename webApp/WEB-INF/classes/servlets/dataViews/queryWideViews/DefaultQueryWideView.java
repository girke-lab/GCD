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
    
  
    public void printButtons(java.io.PrintWriter out, int hid, int pos, int end, int rpp) 
    {
        out.println("<FORM METHOD='POST' ACTION='DispatchServlet'>");
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
    
    public void printStats(PrintWriter out, Search search)  
    {
        Object[] values=new Object[3];
        values[0]=new Integer(search.getResults().size());        
        
        if(search==null || search.getStats()==null || search.getStats().size()!=2)
        {    values[1]=null;values[2]=null; }
        else
        {
            values[1]=search.getStats().get(0);
            values[2]=search.getStats().get(1);
        }
        
        Common.printStatsTable(out,"Total Query",new String[]{"Loci","Models","Clusters"},values);         
    }
    
    public boolean printAllData() 
    {
        return false;
    }    
   
    public void printGeneral(PrintWriter out, Search search, String position)
    {
    }    
    
    public void printGeneral(PrintWriter out, Search search, String position, Map storage)
    {
    }
    
}
