/*
 * Common.java
 *
 * Created on March 13, 2003, 4:11 PM
 */
package servlets;
/**
 *
 * @author  khoran
 */
import java.util.*;
import java.io.*;
import java.sql.*;
import servlets.search.Search;
import org.apache.log4j.Logger;

public class Common {
    public final static int arab=0, rice=1;    
    public final static int dbCount=2;
    public static final int KEY_TYPE_SEQ=0,     KEY_TYPE_MODEL=1,
                            KEY_TYPE_CLUSTER=2, KEY_TYPE_BLAST=3,
                            KEY_TYPE_ACC=4,     KEY_TYPE_QUERY=5;
    public final static String[] dbRealNames=new String[]{"arab","rice"};
    public final static String[] dbPrintNames=new String[]{"Arabidopsis","Rice"};
    public final static String dataColor="D3D3D3",titleColor="AAAAAA";        
    //maximum number of results that can be returned per database query
    public final static int MAXKEYS=100000; 
    public final static int SCRIPT_LIMIT=500;
    public final static int MAX_QUERY_KEYS=10000; //max number of keys to list in a query
    public final static int MAX_SESSIONS=5; //maximum number of sessions a user can store
    
    //the caseless compare keyword is ILIKE in postgres, but LIKE in mysql
    public final static String ILIKE="ILIKE";
                            
//    private static DbConnection dbc=null;
    private static Logger log=Logger.getLogger(Common.class);
    /** Creates a new instance of Common */
    public Common() {
    }
    
    public static List sendQuery(String q)
    {        
        List rs=null;
        DbConnection dbc;
        try{
            dbc=DbConnectionManager.getConnection("khoran");
            rs=dbc.sendQuery(q);        
            log.info("Stats: "+dbc.getStats());
        }catch(Exception e){            
            log.error("query error: "+e.getMessage());         
        }
        if(rs==null)
            log.debug("null rs");
        return rs;
    }       
    
    public static void printList(PrintWriter out,List list)
    {
        int rows=0,cols=0;
        String cell;
        for(ListIterator l=list.listIterator();l.hasNext();)
        {
            rows++;
            cols=0;
            for(ListIterator l2=((ArrayList)l.next()).listIterator();l2.hasNext();)
            {
                cols++;
                out.println(l2.next()+", ");
            }
            out.println("<BR>");    
        }
        out.println(rows+" rows, "+cols+" columns");
    }
    public static void printList(PrintStream out,List list)
    {
        int rows=0,cols=0;
        String cell;
        for(ListIterator l=list.listIterator();l.hasNext();)
        {
            rows++;
            cols=0;
            for(ListIterator l2=((ArrayList)l.next()).listIterator();l2.hasNext();)
            {
                cols++;
                out.println(l2.next()+", ");
            }
            out.println("<BR>");    
        }
        out.println(rows+" rows, "+cols+" columns");
    }
    public static String printArray(int[] a)
    {
        String out="[";
        for(int i=0;i<a.length;i++){
            out+=a[i];
            if(i+1<a.length)
                out+=",";
        }
        return out+"]";            
    }
    public static String printArray(Object[] a)
    {
        String out="[";
        for(int i=0;i<a.length;i++){
            out+=a[i];
            if(i+1<a.length)
                out+=",";
        }
        return out+"]";            
    }
    public static void printForm(PrintWriter out,int hid)
    {
        out.println("\n<FORM method=get name='form1' action='QueryPageServlet'>\n"+  //SequenceServlet
            "<INPUT type=hidden name='hid' value='"+hid+"'>"+
            "<INPUT type=hidden name='displayType' value='modelView'>"+                        
            "<TABLE align='center' border='0'>\n"+
            "\t<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='3'><a href='titleInfo.html'>TU</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='4'><a href='titleInfo.html'>Promoter 3000</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='5'><a href='titleInfo.html'>3' UTR</a></TD>\n"+
            "\t</TR>\n<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='6'><a href='titleInfo.html'>Intergenic</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='7'><a href='titleInfo.html'>CDS</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='8'><a href='titleInfo.html'>5' UTR</a></TD>\n"+
            "\t</TR>\n<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='9'><a href='titleInfo.html'>Protein</a></TD>\n"+
            "\t\t<TD colspan='2'>Length of Sequence to return: <INPUT name='length' value='' size='5'></TD>\n"+
            "</TR><TR>\n"+
            //"\t\t<TD align='center' colspan='1'><INPUT type=checkbox name='format' value='1'>fasta format</TD>\n"+            
            "\t\t<TD align='center' colspan='3'>Format: <SELECT name='format'><OPTION value='0'>html" +
            "\t\t\t<OPTION value='1'>fasta<OPTION value='2'>all fasta</SELECT></TD>\n"+
            "</TR></TABLE>\n"+ 
            "<TABLE align='center' border='0'>\n"+
            "\t<TR>\n"+
            //"\t\t<TD><INPUT type=submit name='seq_fields' value='Sequence Data' ></TD>\n"+
            "\t\t<TD><INPUT type=image name='submit' width='100' height='25' border='0' src='images/sequence.jpg' ></TD>\n"+
            "\t\t<TD><a href='QueryPageServlet?hid="+hid+"&displayType=seqView'><img width='100' height='25' border='0' src='images/summary.jpg'></a></TD>\n"+
            //"\t\t<TD><INPUT type=submit value='Annotation Data' onClick='getDetails();'>\n"+
            "\t</TR>\n"+            
            "</TABLE>\n"+
            "</FORM>\n");
    }
   
    public static void printHeader(Writer out)
    {
        printHeader(out,"");
    }
    public static void printHeader(Writer out,String subTitle)
    {
        String header=
            "<!--GCD_Tool_Bar-->" +
            "<meta Http-Equiv='Content-Type' content='text/html'; charset='UTF-8'><meta Http-Equiv='Cache-Control' Content='no-cache'/><meta Http-Equiv='Pragma' Content='no-cache'/><meta Http-Equiv='Expires' Content='0'/>" +
            "<style type='text/css'>	body { color: #000000; font-family: avantgarde, sans-serif; font-size: 11pt} " +
            "	a { color: #006699} " +
            "	a:hover { background-color: #AAAAAA} " +
            "	h1, h2, h3, h4, h5, h6 { font-weight: bold} " +
            "	h1 { font-size: 180%} " +
            "	h2 { font-size: 150%} " +
            "	h3 { font-size: 120%} " +
            "	h4 { font-size: 120%} " +
            "	pre { font-family: FreeMono, monospace; font-size: 10pt} " +
            "	tt { font-family: FreeMono, monospace; font-size: 10pt} " +
            "</style>" +
            "<body bgcolor='#fefefe' text='#000000' link='#006699' vlink='#003366'>" +
            "<head>" +
            "	<title>GCD ReadMe</title>" +
            "</head>" +
            "<font face='sans-serif, Arial, Helvetica, Geneva'>" +
            "	<table  border='0' align='center'>" +
            "		<tr>" +
            "			<td nowrap, colspan='11', valign='top', align='center', width=1000><img src='images/GCD.jpg'/></td>" +
            "		</tr>" +
            "		<tr>" +
            "			<td nowrap, colspan='11', valign='top', align='center', width=1000><font SIZE=-1><a href='http://www.cepceb.ucr.edu/'>Center for Plant Cell Biology, UC Riverside</a></font></td>" +
            "		</tr>" +
            "		<tr>" +
            "			<td nowrap, align='center',  width=100><font SIZE=+1>[&nbsp;<a href='about.jsp'>ReadMe</a>&nbsp;]</font></td>" +
            "   		<td valign='top', width=30><font SIZE=+1>&nbsp;</font></td>" +
            "			<td nowrap, align='center',  width=100><font SIZE=+1>[&nbsp;<a href='index.jsp'>Search</a>&nbsp;]</font></td>" +
            "            	<td valign='top', width=30><font SIZE=+1>&nbsp;</font></td>" +
            "                   <td nowrap, align='center',  width=100><font SIZE=+1>[&nbsp;<a href='advancedSearch.jsp'>Advanced</a>&nbsp;]</font></td>" +
            "			<td valign='top', width=30><font SIZE=+1>&nbsp;</font></td>" +
            "			<td nowrap, align='center',  width=100><font SIZE=+1>[&nbsp;<a href='http://bioweb.ucr.edu/scripts/clusterSummary.pl?sort_col=Size' target='_blank'>Table</a>&nbsp;]</font></td>" +
            "			<td valign='top', width=30><font SIZE=+1>&nbsp;</font></td>" +
            "			<td nowrap, align='center',  width=100><font SIZE=+1>[&nbsp;<a href='http://bioweb.ucr.edu/scripts/clusterStats.pl' >Stats</a>&nbsp;]</font></td>" +
            "			<td valign='top', width=30><font SIZE=+1>&nbsp;</font></td>" +
            "			<td nowrap, align='center',  width=100><font SIZE=+1>[&nbsp;<a href='http://bioweb.ucr.edu/databaseWeb/data'>Downloads</a>&nbsp;]</font></td>" +
            "		</tr>" +           
            "	</table>" +
             (!subTitle.equals("")?
                "<table border='0' align='center'>    <tr>" +
                "               <td nowrap colspan='11' align='center' width=1000><font SIZE=+3>"+subTitle+"</td>"+
                "           </tr></table>" 
                : ""  //else don't print the last row
            )+
            "</font>" +
            "<!--GCD_Tool_Bar-->";
        try{
            out.write(header);
        }catch(Exception e){
            log.error("io error: "+e.getMessage());
        }
    }
    
    public static void printHeaderOld(Writer out)
    {   //print the CEPCEB header on the top of every page        
        int cs=1,space=10;;
        String header=""+ 
        "<table width='100%' border='0' cellspacing='0' cellpadding='0'>"+
        "<tr bgcolor='AAAAAA'><td colspan='"+cs+"'>&nbsp</td></tr>"+
        "<tr>"+
        "   <td align='center' colspan='"+cs+"' bgcolor='AAAAAA' ><h1>Genome Cluster Database</h1></td>"+
        "</tr>"+
        "<tr>"+
        "    <td align='center' colspan='"+cs+"' bgcolor='AAAAAA'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://www.cepceb.ucr.edu/' target='_blank'>"+
        "       Center for \nPlant Cell Biology at UC Riverside</a></font></td>"+
        "</tr>"+
        "<tr bgcolor='AAAAAA'><td colspan='"+cs+"'>&nbsp</td></tr>"+
        "<tr><td colspan='"+cs+"'>&nbsp</td></tr>"+
        "<tr><table align='center' cellspacing='0' cellpadding='0'><tr>"+        
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='index.jsp'>"+
        "        <img src='images/search.jpg' width='100' height='25' border='0'></a></font></div></td>"+
        "    <td><img src='images/spacer.jpg' width='"+space+"' height='25' border='0'></td>"+
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='advancedSearch.jsp'>"+
        "        <img src='images/advSearchLong.jpg' width='150' height='25' border='0'></a></font></div></td>"+
        "    <td><img src='images/spacer.jpg' width='"+space+"' height='25' border='0'></td>"+
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/projects/PlantFam/Readme/about.html'>"+
        "        <img src='images/aboutDB.jpg' width='100' height='25' border='0'></a></font></div></td>"+
        "    <td><img src='images/spacer.jpg' width='"+space+"' height='25' border='0'></td>"+
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/cgi-bin/clusterSummary.pl?sort_col=Size'>"+
        "        <img src='images/clusterTable.jpg' width='100' height='25' border='0'></a></font></div></td>"+
        "    <td><img src='images/spacer.jpg' width='"+space+"' height='25' border='0'></td>"+
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/cgi-bin/clusterStats.pl'>"+
        "        <img src='images/clusterStats.jpg' width='100' height='25' border='0'></a></font></div></td>"+
        "</tr></table></tr>"+
        "</table><p>";        
        try{
            out.write("<BODY bgcolor='#fefefe' text='#000000' link='#006699' vlink='#003366'>");  
            out.write(header);
        }catch(Exception e){  
            log.error("io error: "+e.getMessage());
        }
    }
    
    public static void printUnknownHeader(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        String base="http://bioinfo.ucr.edu/projects/internal/Unknowns/external";
        out.println(
        "  <font face='sans-serif, Arial, Helvetica, Geneva'>"+
        "  <img alt='Unknown Database' src='images/unknownspace3.png'>"+
        "  <table>"+
        "  <tr>"+
        "  <td valign='top' bgcolor='#F0F8FF'' width=180 nowrap ><font SIZE=-1>"+
        "  <a href='"+base+"/index.html'><li>Project</a></li>"+
        "  <a href='"+base+"/descriptors.html'><li>Unknown Descriptors</a></li>"+
        "  <a href='"+base+"/retrieval.html'><li>Search Options</a></li>"+
        "  <a href='"+base+"/interaction.html'><li>Protein Interaction</a></li>"+
        "  <a href='"+base+"/KO_cDNA.html'><li>KO & cDNA Results</a></li>"+
        "  <a href='"+base+"/profiling.html'><li>Chip Profiling</a></li>"+
        "  <a href='"+base+"/tools.html'><li>Protocols</a></li>"+
        "  <a href='"+base+"/external.html'><li>Literature</a></li>"+
        "  <a href='"+base+"/links.html'><li>Links</a></li>"+
        "  <a href='"+base+"/downloads.html'><li>Downloads</a></li>"+
        "  </font></td>"+
        "  <td>&nbsp;&nbsp;&nbsp;</td>"+
        "  <td valign='top'' > "); //width=600
    }
    public static void printUnknownFooter(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        out.println("</td></tr></table></font>");
    }
    public static void printUnknownsSearchLinks(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        out.println("<A href='unknownsBasicSearch.jsp'>Basic Search</A>" +
                "&nbsp&nbsp&nbsp" +
                "<A href='unknownsSearch.jsp'>Advanced Search</A>" +
                "&nbsp&nbsp&nbsp" +
                "<a href='QueryPageServlet?searchType=seq_id&displayType=diffTrackingView&inputKey=hello'>Difference Tracking</a>");
    }
    
    public static int getDBid(String name)
    {//takes a Genome string from database and reutrn an integer id number for it
        if(name.equals("arab"))
            return arab;
        if(name.equals("rice"))
            return rice;
        return -1;
    }

    public static void quit(PrintWriter out,String message)
    {
        out.println(message);
        out.println("</body></html>");
        out.close();
    }
    public static void sendError(javax.servlet.http.HttpServletResponse response,String page,String error)
    {
        try{
            response.sendRedirect(page+"?error_message="+error);
        }catch(IOException e){
            log.error("could not redirect to "+page+", error: "+error+
                    ", exception: "+e);
        }
    }
    public static void printStatsTable(PrintWriter out,String title,String[] subTitles,Object[] values)
    {        
        out.println("<table border='1' cellspacing='0' bgcolor='"+Common.dataColor+"'>");
        out.println("<tr  bgcolor='"+Common.titleColor+"'><th colspan='"+subTitles.length+"'>"+title+"</th></tr>");
        out.println("<tr  bgcolor='"+Common.titleColor+"'>");
        for(int i=0;i<subTitles.length;i++)
            out.println("<th>"+subTitles[i]+"</th>");
        out.println("</tr><tr>");
        for(int i=0;i<values.length;i++)
        {
            if(values[i]!=null)
                out.println("<td>"+values[i]+"</td>");
            else
                out.println("<td>&nbsp</td>");
        }
        out.println("</tr></table>");        
    }
    public static String buildIdListCondition(String varName,Collection ids)
    {
        return buildIdListCondition(varName,ids,false,-1); //default to no quotes.
    }
        public static String buildIdListCondition(String varName,Collection ids, boolean b)
    {
        return buildIdListCondition(varName,ids,b,-1); 
    }
    public static String buildIdListCondition(String varName,Collection ids,int limit)
    {
        return buildIdListCondition(varName,ids,false,limit); //default to no quotes.
    }
    public static String buildIdListCondition(String varName,Collection ids,boolean quoteIt,int limit)
    {
        StringBuffer out=new StringBuffer();
        if(ids.size()==0)
            return "0=1"; //since list is empty, return a false statement, while avoiding syntax errors.
        int count=0;
        out.append(varName+" in (");
        for(Iterator i=ids.iterator();i.hasNext() && (limit==-1 || count < limit);count++)
        {
            if(quoteIt)
                out.append("'"+i.next()+"'");
            else
                out.append(i.next());
            if(i.hasNext() && (limit==-1 || count < limit))
                out.append(",");
        }
        out.append(")");
        return out.toString();
    }
    public static String buildLikeCondtion(String varName, Collection ids)
    {
        return buildLikeCondtion(varName, ids, -1,false);// no limit
    }            
    public static String buildLikeCondtion(String varName, Collection ids,int limit)
    {
        return buildLikeCondtion(varName, ids, limit,false); 
    }            
    public static String buildLikeCondtion(String varName, Collection ids,boolean b)
    {
        return buildLikeCondtion(varName, ids, -1,b);// no limit
    }            
    public static String buildLikeCondtion(String varName, Collection ids, int limit,boolean addWildcard)
    {
        StringBuffer out=new StringBuffer();
        if(ids.size()==0)
            return "0=1";
        out.append("(");
        String wildCard=(addWildcard?"%":"");
                
        //while(in.hasNext() && count++ < limit)
        int count=0;
        for(Iterator in=ids.iterator();in.hasNext() && (limit==-1 || count < limit);count++)
        {
            out.append(varName+" ilike '"+wildCard+in.next()+wildCard+"'");
            if(in.hasNext() && (limit==-1 || count < limit))
                out.append(" OR ");
        }
        out.append(")");
        return out.toString();
    }
    public static String buildDescriptionCondition(String varName, Collection ids)
    {
        
        Iterator in=ids.iterator();
        StringBuffer conditions=new StringBuffer();
        int wasOp=1;
       
        while(in.hasNext())
        { //create conditions string
            String temp=(String)in.next();//use temp becuase sucsesive calls to nextToken
                                                    //advace the pointer
            if(temp.compareToIgnoreCase("and")!=0 && temp.compareToIgnoreCase("or")!=0 
                    && temp.compareToIgnoreCase("not")!=0 && temp.compareTo("(")!=0
                    && temp.compareTo(")")!=0)
            //no keywords or parinths
            {
                if(wasOp==0)//last token was not an operator, but we must have an operator between every word
                    conditions.append(" and ");
                conditions.append(" ( "+varName+" "+ILIKE+" '%"+temp+"%') ");

                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        return conditions.toString();
    }
    public static boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
}
