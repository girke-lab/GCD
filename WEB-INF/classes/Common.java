/*
 * Common.java
 *
 * Created on March 13, 2003, 4:11 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;
import java.io.*;
public class Common {
    final static int arab=0, rice=1;
    /** Creates a new instance of Common */
    public Common() {
    }
    public static List sendQuery(String q, int length)
    {
        int i=0;
        queryThread dbConnection=new queryThread("common");
        dbConnection.setQuery(q,length);
        dbConnection.start();
        while(dbConnection.isAlive());//wait for query to finish       
        List data=new ArrayList(dbConnection.getResults());
        return data;
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
    public static void blastLinks(PrintWriter out,int currentDB,int hid)
    {   //inputKey is a list of keys only, no words
        //print links to blastp page and tblastn page
        StringBuffer URLprefix=new StringBuffer();
        URLprefix.append(" <A href='/databaseWeb/blastPage?hid="+hid+"&");
        out.println("<TABLE width='50%' align='center'><TR>");

        if(currentDB==arab)
        {//if using the arab database, send links to arab blast files
            URLprefix.append("db="+arab+"&");
            out.println("<TD>"+URLprefix+"file=summary'>Blast Summary</A></TD>");
            out.println("<TD>"+URLprefix+"file=riceCvsArabP'>tBlastn file</A></TD>");     
            out.println("<TD>"+URLprefix+"file=ricePvsArabP'>Blastp file</A></TD>");
        }
        else if(currentDB==rice)
        {//otherwise use rice blast files
            URLprefix.append("db="+rice+"&");
            out.println("<TD>"+URLprefix+"file=summary'>Blast Summary</A></TD>");
            out.println("<TD>"+URLprefix+"file=ArabCvsRiceP'>tBlastn file</A></TD>");     
            out.println("<TD>"+URLprefix+"file=ArabPvsRiceP'>Blastp file</A></TD>");
        }
        out.println("</TR></TABLE>");
    }
    public static void printForm(PrintWriter out,int hid)
    {
        out.println("<FORM method=post name='form1'>\n"+
            "<INPUT type=hidden name='hid' value='"+hid+"'>"+
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
            "\t\t<TD align='center' colspan='3'><INPUT type=checkbox name=format value='1'>fasta format</TD>\n"+            
            "</TR></TABLE>\n"+ 
            "<TABLE align='center' border='0'>\n"+
            "\t<TR>\n"+
            "\t\t<TD><INPUT type=submit value='Sequence Data' onClick='getSequences();'>\n"+
            //"\t\t<TD><INPUT type=submit value='Annotation Data' onClick='getDetails();'>\n"+
            "\t</TR>\n"+            
            "</TABLE>\n"+
            "</FORM>\n");
    }
    public static void javaScript(PrintWriter out)
    {
        out.println("<script language='JavaScript' type='text/JavaScript'>\n"+
            "<!--\n"+
            "function getSequences()\n{\n"+
            "document.form1.action='SequenceServlet';"+
            "document.form1.submit();\n}\n"+
            "function getDetails()\n{\n"+
            "document.form1.action='DetailsServlet';"+
            "document.form1.submit();\n}\n"+
            "-->\n"+
            "</script>\n");
    }
    public static void printHeader(PrintWriter out)
    {   //print the CEPCEB header on the top of every page
        String header=""+ 
        "<table width='900' border='0' cellspacing='0' cellpadding='0'>"+
        "<tr>"+
        "<td width='210' rowspan='2'><a href='http://www.cepceb.ucr.edu/' target='_blank'><img src='images/cepceb.jpg' alt='cepceb' width='210' height='90' border='0'></a></td>"+
        "<td width='590' align='center' colspan='2'><h1>Genome Cluster Database</h1></td>"+
        "</tr>"+
        "<tr>"+
        "    <td height='29' valign='top' colspan='2'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://www.ucr.edu/' target='_blank'>"+
        "        UC Riverside</a></font></div></td>"+
        "</tr>"+
        "<tr>"+
        "    <td colspan='3'><hr></td>"+
        "</tr>"+
        "<tr>"+        
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/projects/PlantFam/Readme/about.html'>"+
        "        <b>About this Database</b></a></font></div></td>"+
        "    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/cgi-bin/clusterSummary.pl?sort_col=Size'>"+
        "        <b>Cluster Table</b></a></font></div></td>"+
"            <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/cgi-bin/clusterStats.pl'>"+
"                <b>Cluster Statistics</b></a></font></div></td>"+
        "</tr>"+
"        </table>";
        out.println(header);
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
    public static void navLinks(PrintWriter out)
    {
    	return; //don't print the nav links anymore
        //out.println("<TABLLE width='50%' align='right'><TR>");
        //out.println("<TD><A href='http://faculty.ucr.edu/~tgirke'>Home</A></TD>");
        //out.println("<TD><A href='http://138.23.191.152:/blast/blast.html'>UCR Blast Page</A></TD>");
        //out.println("</TR></TABLE>");
    }
    
}
