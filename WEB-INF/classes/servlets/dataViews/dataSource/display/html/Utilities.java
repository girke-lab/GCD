/*
 * Utilities.java
 *
 * Created on January 4, 2007, 9:42 AM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import org.apache.log4j.Logger;
import servlets.DescriptionManager;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.structure.Record;

/** 
 *  Some common html blocks, for use by formats printing in HTML.
 * @author khoran
 */
public class Utilities
{
    private static final Logger log=Logger.getLogger(Utilities.class);
    
    public static void startTable(Writer out) throws IOException
    {
        out.write("<TABLE bgcolor='"+PageColors.data+"' width='100%'" +
                    " align='center' border='1' cellspacing='0' cellpadding='0'>\n");  
    }
    public static void endTable(Writer out) throws IOException
    {
        out.write("</TABLE>");
    }
    
    public static void openChildCel(Writer out, int span) throws IOException
    {
        openChildCel(out,span,0,"&nbsp");
    }
    public static void openChildCel(Writer out, int span,int shift) throws IOException
    {
        openChildCel(out,span,shift,"&nbsp");
    }
    public static void openChildCel(Writer out, int span,int shift,String spaceData) throws IOException
    {
        out.write("<tr>");                
        for(int i=0;i<shift;i++)
            out.write("<td>"+spaceData+"</td>");
        out.write("<td colspan='"+span+"'>");        
    }
    public static void closeChildCel(Writer out) throws IOException
    {
        out.write("</td></tr>");
    }
    
    public static boolean hasChildren(Record r)
    {
        return r.allChildren().iterator().hasNext();
    }
    
    public static void printTreeControls(Writer out, String link, String key,Iterator recordItr)  throws IOException
    {
        if(recordItr==null || !recordItr.hasNext()) //no children
            printTreeControls(out,link,key,true);
        else
            printTreeControls(out,link,key,false);
    }
    public static void printTreeControls(Writer out, String link, String key,boolean expand)  throws IOException
    {
        String imageOptions=" border='0' height='10' width='15' ";
     
        out.write("<td nowrap>"+"<a name='"+key+"'></a>"); 
        if(expand) 
            out.write("<a href='"+link+"&action=expand#"+key+"'><img src='images/arrow_down.png' title='expand' "+imageOptions+" ></a>&nbsp&nbsp\n");
        else 
            out.write("<a href='"+link+"&action=collapse#"+key+"'><img src='images/arrow_up.png' title='collapse' "+imageOptions+" ></a>\n");
        out.write("</td>");
    }
    
    public static void printTableTitles(PrintWriter out,DisplayParameters params,String[] titles, String[] dbColNames,String prefix,String anchor)
    {
        printTableTitles(out,params,titles,dbColNames,prefix,anchor,"");
    }
    public static void printTableTitles(PrintWriter out,DisplayParameters params,String[] titles, String[] dbColNames,
                                    String prefix,String anchor,String headerOptions)
    {
        String newDir;
        if(titles.length!=dbColNames.length)
        {
            log.error("length mismatch while printing header: titles.length="+titles.length+
                    ", feilds.length="+dbColNames.length);
            //print bare titles
            for(String title : titles)
                out.println("<th>"+title+"</th>");
            return;
        }        
        for(int i=0;i<titles.length;i++)
        {
            newDir="asc"; //default to asc
            
            if(params.getSortCol()!=null && params.getSortCol().equals(prefix+"_"+dbColNames[i])) //reverse current sort col
                newDir=(params.getSortDir().equals("asc"))? "desc":"asc"; //flip direction
            
            out.println("<th nowrap "+headerOptions+"  >");
            out.println(DescriptionManager.wrapLink(dbColNames[i],titles[i],  "QueryPageServlet?hid="+params.getHid()+"&sortCol="+prefix+"_"+dbColNames[i]+
                "&sortDirection="+newDir+"#"+anchor )); 
            out.println("</th>");
            
            
            //out.println("<th nowrap "+headerOptions+"  ><a href='QueryPageServlet?hid="+params.getHid()+"&sortCol="+prefix+"_"+dbColNames[i]+
                //"&sortDirection="+newDir+"#"+anchor+"'>"+titles[i]+"</a></th>");             
        }
    }
    public static String cn(String s)
    {
        return s==null?"&nbsp":s;
    }
    public static String cn(Boolean s)
    {
        return s==null?"&nbsp":s.toString();
    }
    public static String bn(String s)
    {
        return s==null?"":s;
    }
}
