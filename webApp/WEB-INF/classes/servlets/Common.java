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
    public final static String[] dbRealNames=new String[]{"arab","rice"};
    public final static String[] dbPrintNames=new String[]{"Arabidopsis","Rice"};
    public final static String dataColor="D3D3D3",titleColor="AAAAAA";        
    //maximum number of results that can be returned per database query
    public final static int MAXKEYS=100000; 
    public final static int SCRIPT_LIMIT=500;
    public final static int MAX_QUERY_KEYS=10000; //max number of keys to list in a query
    
    //the caselss compare keyword is ILIKE in postgres, but LIKE in mysql
    public final static String ILIKE="LIKE";
                            
//    private static DbConnection dbc=null;
    private static Logger log=Logger.getLogger(Common.class);
    /** Creates a new instance of Common */
    public Common() {
    }
    
    public static List sendQuery(String q)
    {
        //System.out.println("sending query "+q);
        List rs=null;
        DbConnection dbc;
        try{
            dbc=DbConnectionManager.getConnection("common");
            if(dbc==null)
            {
                dbc=new DbConnection(); //use default connection
                DbConnectionManager.setConnection("name", dbc);
            }
            rs=dbc.sendQuery(q);        
            log.info("Stats: "+dbc.getStats());
        }catch(Exception e){
            System.out.println("query error: "+e.getMessage());
            log.error("query error: "+e.getMessage());         
        }
        if(rs==null)
            System.out.println("null rs");
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
   
    public static void printForm(PrintWriter out,int hid)
    {
        out.println("\n<FORM method=post name='form1' action='QueryPageServlet'>\n"+  //SequenceServlet
            "<INPUT type=hidden name='hid' value='"+hid+"'>"+
            "<INPUT type=hidden name='displayType' value='modelView'>"+                        
            "<TABLE align='center' border='0'>\n"+
            "\t<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='3'><a href='/titleInfo.html'>TU</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='4'><a href='/titleInfo.html'>Promoter 3000</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='5'><a href='/titleInfo.html'>3' UTR</a></TD>\n"+
            "\t</TR>\n<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='6'><a href='/titleInfo.html'>Intergenic</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='7'><a href='/titleInfo.html'>CDS</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='8'><a href='/titleInfo.html'>5' UTR</a></TD>\n"+
            "\t</TR>\n<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='9'><a href='/titleInfo.html'>Protein</a></TD>\n"+
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
        "       Center for Plant Cell Biology at UC Riverside</a></font></td>"+
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
    public static void printHeaderOld(PrintWriter out)
    {   //print the CEPCEB header on the top of every page
        String header=new String(""+ 
        "<table width=\"86%\" border=\"0\">"+
        "<tr> <td width=\"376\" rowspan=\"3\"><i><font face=\"georgia, Times New Roman, Times\"><a href=\"http://www.cepceb.ucr.edu/\"><img src=\"images/header_01.jpg\" width=\"371\" height=\"90\" border=\"0\"></a></font></i></td>"+
        "<td width=\"463\"> <div align=\"center\"><font size=\"+3\" face=\"Arial, Helvetica, sans-serif\"><i><b><font color=\"#339933\" size=\"+4\">Bioinformatics "+
        "Core</font></b></i></font></div>"+"</td>"+"</tr>"+
        "<tr> <td width=\"463\"> <div align=\"center\"><font face=\"georgia, Times New Roman, Times\"><font color=\"#D27E00\"><b><font color=\"#000000\" face=\"Arial, Helvetica, sans-serif\" size=\"2\"><a href=\"http://www.cepceb.ucr.edu/\">Center"+
        "for Plant Cell Biology</a>, UC Riverside</font></b></font></font></div>"+"</td>"+"</tr>"+
        "<tr> <td width=\"463\" height=\"32\"> <div align=\"center\"><font color=\"#339933\" face=\"Arial, Helvetica, sans-serif\" size=\"+3\"><i>Information Retrieval</i></font></div>"+
        "</td>"+"</tr>"+"<tr> <td colspan=\"2\"> <hr size=\"3\">"+"</td>"+"</tr>"+"</table>");
        out.println(header);
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
//    public static void printTotals(PrintWriter out,Search s,String view)
//    {
//        out.println("<table border='1' cellspacing='0' bgcolor='"+dataColor+"'>");
//        out.println("<tr  bgcolor='"+titleColor+"'><th colspan='3'>Total Query</th></tr>");
//        out.println("<tr  bgcolor='"+titleColor+"'><th>Loci</th><th>Models</th><th>Clusters</th></tr>");
//        out.println("<tr>");
//        if(view.equals("clusterView"))
//        {
//            out.println("<td>&nbsp</td><td>&nbsp</td>");
//            out.println("<td>"+s.getResults().size()+"</td>");
//        }
//        else
//        {
//            out.println("<td>"+s.getResults().size()+"</td>");
//            if(s.getStats()!=null && s.getStats().size()==2)
//            {
//                out.println("<td>"+s.getStats().get(0)+"</td>");
//                out.println("<td>"+s.getStats().get(1)+"</td>");
//            }
//            else
//                out.println("<td>&nbsp</td><td>&nbsp</td>");
//        }
//        out.println("</tr></table>");
//    }
//    public static void printPageStats(PrintWriter out,int keys,int models,int clusters)
//    {
//        out.println("<table border='1' cellspacing='0' bgcolor='"+dataColor+"'>");
//        out.println("<tr  bgcolor='"+titleColor+"'><th colspan='3'>On This Page</th></tr>");
//        out.println("<tr  bgcolor='"+titleColor+"'><th>Loci</th><th>Models</th><th>Clusters</th></tr>");
//        out.println("<tr>");
//        // use -1 to signal that a value should not be printed.
//        if(keys >= 0) out.println("<td>"+keys+"</td>");
//            else out.println("<td>&nbsp</td>");
//        if(models >= 0) out.println("<td>"+models+"</td>");
//            else out.println("<td>&nbsp</td>");
//        if(clusters >= 0) out.println("<td>"+clusters+"</td>");
//            else out.println("<td>&nbsp</td>");
//        out.println("</tr></table>");
//        
//    }
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
//    public static void printButtons(PrintWriter out, int hid,int pos,int end, int rpp)
//    {
//        
//        out.println("<FORM METHOD='POST' ACTION='DispatchServlet'>");
//        out.println("<INPUT type=hidden name='hid' value='"+hid+"'>");
//        out.println("<INPUT type=hidden name='script'>");
//        
//        out.println("<TABLE border='0' ><TR>");        
//        out.println("<TD><INPUT type='submit' value='All Gene Structures' " +
//                    " onClick=\"javascript: script.value='multigene.pl'; submit();\" ></TD>");
//        out.println("<TD><INPUT type='submit' value='Chr Map' "+       
//                    " onClick=\"javascript: script.value='chrplot.pl'; submit();\"></TD>");
//        out.println("<TD><INPUT type='submit' value='Go Slim Counts' "+
//                    " onClick=\"javascript: script.value='goSlimCounts'; submit();\"></TD>");
//        out.println("<TD><INPUT type='submit' value='Key List' "+
//                " onClick=\"javascript: script.value='displayKeys.pl'; submit();\"></TD>");
//        out.println("<TD><INPUT type='submit' value='Align to Hmm' "+
//                " onClick=\"javascript: script.value='alignToHmm'; submit();\"></TD>");
//
//        out.println("</TR><TR>");
//        out.println("<TD colspan='4'> Apply buttons to:&nbsp&nbsp ");
//        out.println("<SELECT name='range' >" + 
//                            "<OPTION value='0-"+end+"' >All" +
//                            "<OPTION selected value='"+pos+"-"+(pos+rpp)+"'>Page" +
//                            "<OPTION value='custom'>Range: " +
//                        "</SELECT>&nbsp ");
//        out.println("<INPUT type=text name='range' value=''></TD>");
//        out.println("</TR></TABLE></FORM>");
//        //out.println("range must be in the form a-b,c-d,...,x-y");
//    }
}
