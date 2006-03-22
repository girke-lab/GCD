/*
 * HeaderBean.java
 *
 * Created on February 2, 2006, 12:01 PM
 *
 */

package servlets.beans;

import java.io.*;
import org.apache.log4j.Logger;

/**
 *
 * @author khoran
 */
public class HeaderBean
{
    private static final Logger log=Logger.getLogger(HeaderBean.class);
    
    public enum HeaderType {GCD,POND,PED,COMMON};
    
    PrintWriter out;
    String pageTitle="",sidebarTitle="";
    boolean loggedOn=false;
    boolean showLogOnLink=true;
    HeaderType headerType;
    
    
    /** Creates a new instance of HeaderBean */
    public HeaderBean()
    {        
    }
    
    public void setWriter(Writer out)
    { 
        this.out=new PrintWriter(out);
    }
    public void setPageTitle(String t)
    {
        this.pageTitle=t;
    }
    public void setSidebarTitle(String t)
    {
        this.sidebarTitle=t;
    }
    public void setLoggedOn(boolean b)
    {
        loggedOn=b;
    }
    public void setShowLogOnLink(boolean b)
    {
        showLogOnLink=b;
    }
    public void setHeaderType(HeaderType headerType)
    {
        this.headerType=headerType;
        switch(this.headerType){
            case GCD:
                setSidebarTitle("GCD Toolbar"); break;
            case POND:
                setSidebarTitle("POND Toolbar"); break;
            case COMMON:
                setSidebarTitle("Search Toolbar"); break;
            case PED:
                setSidebarTitle("PED Toolbar"); break;
        }
    }
    
    public void printStdHeader(Writer out, String page, boolean loggedOn)
    {
        setWriter(out);
        setPageTitle(page);        
        setLoggedOn(loggedOn);
        printHead();
        printBody();
        printSystomicsHeader();
        
        switch(headerType){
            case GCD:
                printGCDHeader(); break;
            case POND:
            case PED:
                printUnknownsHeader(); 
                break;    
            case COMMON:
                printCommonHeader(); break;
        }        
        this.out.println("<P><h2 align='center' >"+page+"</h2>");
    }
    public void printHead()
    {
        String title;
        switch(headerType){
            case GCD:
                title="GCD"; break;
            case POND:
                title="POND"; break;
            case COMMON:
                title="Systomics"; break;            
            case PED:
                title="PED"; break;
            default:
                title="";                    
        }
        out.println(
                "<head>" +
                "   <title>"+title+"</title>" +
                "   <meta http-equiv=Content-Type content='text/html; charset=iso-8859-1'>" +
                "   <link rel='stylesheet' type='text/css' href='style.css'>" +
                "</head>");
    }
    public void printBody()
    {
        out.println(
                
          "<body text=#333333 vLink=#666666 aLink=#ff0000 link=#333333 bgColor=#e8e8e8 " +
                "leftMargin=0 background='resources/background.gif' " +
                "topMargin=0 marginheight='0' marginwidth='0'>      "
        );
    }
    public void printFooter()
    {            
        for(int i=0;i<50;i++)
            out.println("<br>");
        out.println(
            "</td></tr></table>" +
            "<p> <p>" +
            "<table class=foot cellSpacing=0 cellPadding=1 width='100%' bgColor=#aaaaaa border=0><tbody>" +
            "  <tr>" +
            "    <td class=foot noWrap width='1%'>&nbsp;&nbsp;</td>" +
            "    <td class=foot noWrap align=left width='49%'>Thomas Girke, UC Riverside, Email:&nbsp;<a href='mailto:thomas.girke@ucr.edu'>thomas.girke@ucr.edu</a></td>" +            
            "    <td noWrap align=right width='1%'>&nbsp;&nbsp;</td>" +
            "  </tr>" +
            "</tbody></table>" +
            "</body>" 
        );        
    }

   
   
    public void printSystomicsHeader()
    {
        String title="";
        String urlPrefix="";
        switch(headerType){
            case GCD:
                //title="<img src='images/GCD.jpg' alt='GCD' border=0 >"; break;
                title="Genome Cluster Database (GCD)"; break;
            case POND:
                title="Plant Unknown-eome DB (POND)"; break;    
            case COMMON:
                title="Systomics Network"; 
                urlPrefix="http://bioweb.ucr.edu/databaseWeb/";
                break;
            case PED:
                title="Plant Gene Expression Database (PED)";
        }
        out.println(
            "<table class=head cellSpacing=0 cellPadding=1 width='100%' bgColor=#ffffff border=0><tbody>" +
                "          <tr> <td class=foot2 valign=center align=center bgColor=#6D7B8D rowspan =3 width='13%'>" +
                "                   <a href='http://www.cepceb.ucr.edu/' target='_blank'>" +
                "                       <img src='images/cepceb.gif' width=150 alt=cepceb, border=0><br>Center for Plant Cell Biology</a></td>" +
                "               <td class=foot align=center bgColor=#6D7B8D width='74%'>" +
                "                   <a href='http://faculty.ucr.edu/~tgirke/Databases.htm'><i><b><font color='#FFFFFF'>Systomics Network</font></b></i></a></td>" +
                "               <td valign=center align=center bgColor=#6D7B8D rowspan =3 width='13%'></td>" +
                "          </tr>" +
                "	   <tr> <td valign=center align=center height=15 bgColor=#6D7B8D><b><i><font size=5 color='#AFDCEC'>"+title+"</font></i></b></td>" +
                "          </tr>" +
                "	   <tr> <td class=foot align=center bgColor=#6D7B8D>" +
                "                   <a href='http://www.cepceb.ucr.edu/'><font color='#151B54'>CEPCEB</font></a>&nbsp;&nbsp;" +
                "                       <font color='#151B54'>|</font>&nbsp;&nbsp;<a href='http://www.genomics.ucr.edu/'><font color='#151B54'>IIGB</font></a>&nbsp;&nbsp;" +
                "                       <font color='#151B54'>|</font>&nbsp;&nbsp;<a href='http://www.ucr.edu/'><font color='#151B54'>UC Riverside</font></a></td>" +
                "          </tr>" +
                "</tbody></table>"
        );
        
        out.println(
            "<table class=path cellSpacing=0 cellPadding=1 width='100%' bgColor=#aaaaaa border=0><tbody>" +
            "		<tr><td class=foot align=center bgColor=#646D7E>&nbsp;&nbsp;" +
            "               <a href='http://faculty.ucr.edu/~tgirke/Databases.htm'><font color='#FFFFFF'>Systomics Network</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='"+urlPrefix+"index.jsp'><font color='#FFFFFF'>GCD</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://bioinfo.ucr.edu/projects/Unknowns/external/express.html'><font color='#FFFFFF'>Expression</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='"+urlPrefix+"unknownsBasicSearch.jsp'><font color='#FFFFFF'>POND</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://bioweb.ucr.edu/Cellwall/index.pl'><font color='#FFFFFF'>CWN</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://bioweb.ucr.edu/ChemMine/search.php'><font color='#FFFFFF'>ChemMine</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://faculty.ucr.edu/~tgirke/Links.htm'><font color='#FFFFFF'>Links</font></a></td></tr>" +                
            "</tbody></table>" 
        );                       
    }
    
    public void printUnknownsHeader()
    {
        String title="Log on",query="";
        if(loggedOn){
            title="Log off";
            query="?action=log_off";
        }            
        
        out.println(                
            "<table cellSpacing=0 cellPadding=4 width='100%' bgColor=#ffffff border=0><tbody>" +
            "	<tr><td class=navcol vAlign=top width='13%' height=500 bgColor=#d8d8d8>"+sidebarTitle+"<br><br>" +
            "		<table cellSpacing=3 cellPadding=4 width='100%' border=0><tbody>" +
            "			<tr><td class=foot bgColor=#f0f0f0><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/index.html'>Project</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='index.jsp'>GCD Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='unknownsBasicSearch.jsp'>POND Search</a></td></tr>" +            
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/express.html'>Expression (PED)</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/interaction.html'>Interactome</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/tools.html'>Protocols</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/external.html'>Literature</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/downloads.html'>Downloads</a></td></tr>" +            
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/links.html'>Links</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/index.html'>Contacts</a></td></tr>" 
        );
        
        out.println( 
            "<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "    <A href='login.jsp"+query+"'>"+title+"</a></td></tr>"
        );

        out.println(
            "		</tbody></table>" +
            "	</td>  " +
            "	<td class=content vAlign=top width='80%'>"
        );
    }
    /**
     *  This is currently identical to the unknowns header, so it is not used.
     *  The two header could theortically vary indepedantly later thought, so we leave this here 
     */
    public void printPEDHeader() //PED is plant gene expression database
    {
        String title="Log on",query="";
        if(loggedOn){
            title="Log off";
            query="?action=log_off";
        }            
        
        
        out.println(
            "<table cellSpacing=0 cellPadding=4 width='100%' bgColor=#ffffff border=0><tbody>" +
            "	<tr><td class=navcol vAlign=top width='13%' height=500 bgColor=#d8d8d8>"+sidebarTitle+"<br><br>" +
            "		<table cellSpacing=3 cellPadding=4 width='100%' border=0><tbody>" +
            "			<tr><td class=foot bgColor=#f0f0f0 nowrap ><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a>" +
                "                    <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/index.html'>Project</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='index.jsp'>GCD Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='unknownsBasicSearch.jsp'>POND Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/express.html'>Expression (PED)</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/interaction.html'>Interactome</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/tools.html'>Protocols</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/external.html'>Literature</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/downloads.html'>Downloads</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/links.html'>Links</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/index.html#participants'>Contacts</a></td></tr>"                 
                                
        );
                       
        
        out.println( 
            "<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "    <A href='login.jsp"+query+"'>"+title+"</a></td></tr>"
        );

        out.println(
            "		</tbody></table>" +
            "	</td>  " +
            "	<td class=content vAlign=top width='80%'>"
        );
    }
    public void printGCDHeader()
    {        
        String title="Log on",query="";
        if(loggedOn){
            title="Log off";
            query="?action=log_off";
        }
        
        out.println(
            "<table cellSpacing=0 cellPadding=4 width='100%' bgColor=#ffffff border=0><tbody>" +
            "	<tr><td class=navcol vAlign=top width='13%' height=500 bgColor=#d8d8d8>"+sidebarTitle+"<br><br>" +
            "		<table cellSpacing=3 cellPadding=4 width='100%' border=0><tbody>" +
            "			<tr><td class=foot bgColor=#f0f0f0><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='about.jsp'>Readme</a></td></tr>" + 
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='index.jsp'>Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='advancedSearch.jsp'>Advanced Search</a></td></tr>" +            
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioweb.ucr.edu/scripts/clusterSummary.pl?sort_col=Size'>Table</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioweb.ucr.edu/scripts/clusterStats.pl'>Stats</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioweb.ucr.edu/databaseWeb/data'>Downloads</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/external.html'>Literature</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/downloads.html'>Downloads</a></td></tr>"
        );   
                    
        out.println( 
            "<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "    <A href='login.jsp"+query+"'>"+title+"</a></td></tr>"
        );
                        
        out.println(
            "		</tbody></table>" +
            "	</td>  " +
            "	<td class=content vAlign=top width='80%'>"                
        );                
    }
    public void printCommonHeader()
    { 
        //make sure all links off of this page point to 'http' since we may be under 'https', but
        //don't want other pages to be.
        out.println( 
            "<table cellSpacing=0 cellPadding=4 width='100%' bgColor=#ffffff border=0><tbody>" +
            "	<tr><td class=navcol vAlign=top width='13%' height=500 bgColor=#d8d8d8>"+sidebarTitle+"<br><br>" +
            "		<table cellSpacing=3 cellPadding=4 width='100%' border=0><tbody>" +
            "			<tr><td class=foot bgColor=#c0c0c0 nowrap><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioweb.ucr.edu/databaseWeb/index.jsp'>GCD Search</a></td></tr>" +

            "			<tr><td class=foot bgColor=#c0c0c0 nowrap><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioweb.ucr.edu/databaseWeb/unknownsBasicSearch.jsp'>POND Search</a></td></tr>" 
            
        );   
                           
                        
        out.println(
            "		</tbody></table>" +
            "	</td>  " +
            "	<td class=content vAlign=top width='80%'>"                
        );                
    }
    
}
