/*
 * HeaderBean.java
 *
 * Created on February 2, 2006, 12:01 PM
 *
 */

package servlets.beans;

import java.io.*;
import org.apache.log4j.Logger;
import servlets.HitCounter;

/**
 * This bean is used to print out various headings and footers.
 * The web application is divided into several sections (4 right now), that 
 * each need a different heading and sidbar links. The HeaderType enum
 * determines what section this object is for.
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
    
    /**
     * Sets the writer to use for all subsequent method calls.  This should be
     * set before any other calls.  If it is not, a NullPointerException will
     * occur.  (Basically, we don't check if a writer has been set, we just use it)
     * 
     * This can also be set through the {@link printStdHeader } method.
     * @param out writer to use for print html
     */
    public void setWriter(Writer out)
    { 
        this.out=new PrintWriter(out);
    }
    /**
     * Sets the title of the page.
     * @param t title string
     */
    public void setPageTitle(String t)
    {
        this.pageTitle=t;
    }
    /**
     * Sets the title that wil appear at the top of the sidebar.
     * This usually does not need to be called directly.  It will be called
     * by the setHeaderType method with the correct title.
     * @param t title bar title string
     */
    public void setSidebarTitle(String t)
    {
        this.sidebarTitle=t;
    }
    /**
     * State whether or not a user is logged on. This determines wether
     * to print a 'log on' link or a 'log off' link.
     * @param b log on stat of user
     */
    public void setLoggedOn(boolean b)
    {
        loggedOn=b;
    }
    /**
     * State whether to show a log on/off link at all.
     * @param b 
     */
    public void setShowLogOnLink(boolean b)
    {
        showLogOnLink=b;
    }
    /**
     * Set the section that this headerBean is for.  This will also
     * set the correct titleBar title.
     * This should be called before any method that prints html.
     * @param headerType the type of header to use
     */
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
    
    /**
     * This prints the complete header for the headerType which has been set.
     * This is usually the only thing that needs to be called, after the headerType
     * has been set.  You should also call the footer method after the rest of the page
     * has been printed.
     * @param out writer to use. This will be stored for use by further calls
     * @param page title of a particular page
     * @param loggedOn whether or not a user is logged on or not
     */
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
                printUnknownsHeader(); break;
            case PED:
                printPEDHeader(); break;    
            case COMMON:
                printCommonHeader(); break;
        }        
        this.out.println("<P><h2 align='center' >"+page+"</h2>");
    }
    /**
     * prints the head tag. This will set the page title and include the correct 
     * style sheet.
     */
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
                "   <META NAME='ROBOTS' CONTENT='INDEX, NOFOLLOW'>"+
                "   <link rel='stylesheet' type='text/css' href='style.css'>" +
                "</head>");
    }
    /**
     * print the  body tag. This will set the correct colors and background
     */
    public void printBody()
    {
        out.println(
                
          "<body text=#333333 vLink=#666666 aLink=#ff0000 link=#333333 bgColor=#e8e8e8 " +
                //"leftMargin=0 background='resources/background.gif' " +
                "leftMargin=0  " +
                "topMargin=0 marginheight='0' marginwidth='0'>      "
        );
    }
    /**
     * prints the footer.  This will close the table started by the header
     * and also print a reference line.
     *  Also increment the page counter here since every page has a footer.
     */
    public void printFooter()
    {            
        
        
        for(int i=0;i<50;i++)
            out.println("<br>");

        //printCustomCounter();
        printStatsCounter();

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
    private void printCustomCounter()
    {
        HitCounter.increment(); 
        out.println( "This site has been hit "+HitCounter.getHitCount()+" times.");

    }
    private void printStatsCounter()
    {
        out.println(
            "<!-- Start of StatCounter Code -->"+
            "<script type='text/javascript'>"+
            "var sc_project=4274229; "+
            "var sc_invisible=0; "+
            "var sc_partition=48; "+
            "var sc_click_stat=1; "+
            "var sc_security='5b04e840'; "+
            "var sc_text=2; "+
            "</script>"+

            "<script type='text/javascript' src='http://www.statcounter.com/counter/counter.js'></script><noscript><div class='statcounter'><a title='hits counter' href='http://www.statcounter.com/' target='_blank'><img class='statcounter' src='http://c.statcounter.com/4274229/0/5b04e840/0/' alt='hits counter' ></a></div></noscript>"+
            "<!-- End of StatCounter Code --><br><a href='http://my.statcounter.com/project/standard/stats.php?project_id=4274229&amp;guest=1'>View Stats</a>"
        );


    }
   
   
    /**
     * Print the top bar for the specified headerType
     */
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
                urlPrefix="http://"+servlets.Common.hostname+"/databaseWeb/";
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
                "	   <tr> <td valign=center align=center height=15 bgColor=#6D7B8D>" +
                                    //"<b><i><font size=5 color='#AFDCEC'>"+title+"</font></i></b>" +
                                    "<b><font style='font-family: monospace', size=5, color='#000033'>"+title+"</font></b>" +
                "          </td></tr>" +
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
            "               <a href='/unknowns/express.html'><font color='#FFFFFF'>Expression</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            //"               <a href='"+urlPrefix+"unknownsBasicSearch.jsp'><font color='#FFFFFF'>POND</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://pond.bioinfo.ucr.edu'><font color='#FFFFFF'>POND</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://bioweb.ucr.edu/Cellwall/index.pl'><font color='#FFFFFF'>CWN</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://bioweb.ucr.edu/bapdb/'><font color='#FFFFFF'>BAP DB</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://bioweb.ucr.edu/ChemMine/search.php'><font color='#FFFFFF'>ChemMine</font></a>&nbsp;&nbsp;|&nbsp;&nbsp;" +
            "               <a href='http://faculty.ucr.edu/~tgirke/Links.htm'><font color='#FFFFFF'>Links</font></a></td></tr>" +                
            "</tbody></table>" 
        );                       
    }
    
    /**
     * print the second level bar for the unknowns section.  This does
     * not usually need to be called directly, printStdHeader will call it.
     */
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
            "                       <A href='/unknowns/index.html'>Project</a></td></tr>" +            
            "			<tr><td class=foot bgColor=#f0f0f0><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://bioweb.ucr.edu/bapdb'>BAP DB</a></td></tr>" +                
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='index.jsp'>GCD Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            //"                       <A href='unknownsBasicSearch.jsp'>POND Search</a></td></tr>" +            
            "                       <A href='http://pond.bioinfo.ucr.edu'>POND Search</a></td></tr>" +            
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/express.html'>Expression (PED)</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/interaction.html'>Interactome</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/tools.html'>Protocols</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/external.html'>Literature</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/downloads.html'>Downloads</a></td></tr>" +            
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/links.html'>Links</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='/unknowns/index.html'>Contacts</a></td></tr>" 
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
     *  The two headers could theortically vary indepedantly later though, so we leave this here 
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
                "                    <A href='/unknowns/express.html'>About PED</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='index.jsp'>Gene Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='treatmentSearch.jsp'>DEG Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                    <A href='http://"+servlets.Common.hostname+"/scripts/plotAffyCluster.pl'>R/BioC Tools</a></td></tr>"+
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                //"                   <A href='unknownsBasicSearch.jsp'>POND Search</a></td></tr>" +
                "                   <A href='http://pond.bioinfo.ucr.edu'>POND Search</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                    <A href='http://bioweb.ucr.edu/bapdb'>BAP DB</a></td></tr>"+
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='/unknowns/links.html'>Links</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0 nowrap ><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
                "                   <A href='/unknowns/index.html#participants'>Contacts</a></td></tr>"                 
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
     * print header for GCD section
     */
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
            "                       <A href='http://"+servlets.Common.hostname+"/scripts/clusterSummary.pl?sort_col=Size'>Table</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://"+servlets.Common.hostname+"/scripts/clusterStats.pl'>Stats</a></td></tr>" +
            "			<tr><td class=foot BgColor=#f0f0f0><A href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://"+servlets.Common.hostname+"/databaseWeb/data'>Downloads</a></td></tr>" 

        );   
                    
                        
        out.println(
            "		</tbody></table>" +
            "	</td>  " +
            "	<td class=content vAlign=top width='80%'>"                
        );                
    }
    /**
     * Print header for common section. This is usually for
     * pages that are not specific to any section. For example, the log in
     * page uses this, as well as the query admin page.
     */
    public void printCommonHeader()
    { 
        //make sure all links off of this page point to 'http' since we may be under 'https', but
        //don't want other pages to be.
        out.println( 
            "<table cellSpacing=0 cellPadding=4 width='100%' bgColor=#ffffff border=0><tbody>" +
            "	<tr><td class=navcol vAlign=top width='13%' height=500 bgColor=#d8d8d8>"+sidebarTitle+"<br><br>" +
            "		<table cellSpacing=3 cellPadding=4 width='100%' border=0><tbody>" +
            "			<tr><td class=foot bgColor=#c0c0c0 nowrap><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            "                       <A href='http://"+servlets.Common.hostname+"/databaseWeb/index.jsp'>GCD Search</a></td></tr>" +

            "			<tr><td class=foot bgColor=#c0c0c0 nowrap><a href='./index.html'><IMG height=7 src='./images/bullet.gif' width=4 border=0 alt='Home'></a> " +
            //"                       <A href='http://"+servlets.Common.hostname+"/databaseWeb/unknownsBasicSearch.jsp'>POND Search</a></td></tr>" 
            "                       <A href='http://pond.bioinfo.ucr.edu'>POND Search</a></td></tr>" 
            
        );   
                           
                        
        out.println(
            "		</tbody></table>" +
            "	</td>  " +
            "	<td class=content vAlign=top width='80%'>"                
        );                
    }
    
}
