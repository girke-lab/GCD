/*
 * Unknowns2DataView.java
 *
 * Created on October 12, 2004, 12:17 PM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.*;
import servlets.dataViews.queryWideViews.*; 
import servlets.search.Search;
import org.apache.log4j.Logger;

import servlets.dataViews.records.HtmlRecordVisitor;
import servlets.dataViews.records.RecordGroup;
import servlets.dataViews.records.UnknownRecord;
import servlets.dataViews.records.RecordVisitor;

/**
 * This is the main view for the new unknowns database
 */
public class Unknowns2DataView implements DataView
{
    List seq_ids;
    int hid;
    String sortCol,sortDir;
    int[] dbNums;        
    DbConnection dbc=null;    
    File tempDir=null;
    
    private static Logger log=Logger.getLogger(Unknowns2DataView.class);    
    
    /** Creates a new instance of Unknowns2DataView */
    public Unknowns2DataView()
    {
        sortDir="asc"; //default sort direction
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
    }        
    /**
     * Constructor that also takes path to a temp director, for putting temp files
     * in.
     * @param tempPath path to temp directory.
     */
    public Unknowns2DataView(String tempPath)
    {
        sortDir="asc"; //default sort direction
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
        tempDir=new File(tempPath);
    }        
    
    /**
     * prints page.
     * @param out used for printing
     */
    public void printData(java.io.PrintWriter out)
    {                
        //printData(out,parseData(getData(seq_ids)));
        printData(out,getRecords(seq_ids));
        out.println("</td></table>"); //close page level table
    }
    
    /**
     * prints page title
     * @param out for output
     */
    public void printHeader(java.io.PrintWriter out)
    {
        printUnknownHeader(out);
    }
    
    /**
     * prints number of records displayed on current page
     * @param out for output
     */
    public void printStats(java.io.PrintWriter out)
    {
        Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(seq_ids.size())});
    }
    
    /**
     * sets information about how to get and display data
     * @param sortCol name of column to sort results by
     * @param dbList list of db ids to use. (not used for this view)
     * @param hid current hid.
     */
    public void setData(String sortCol, int[] dbList, int hid)
    {
        this.hid=hid;
        this.sortCol=sortCol;
        this.dbNums=dbList;
    }
    
    /**
     * used to set the list of key_ids to display
     * @param ids list of key_ids
     */
    public void setIds(java.util.List ids)
    {
         this.seq_ids=ids;   
    }
    
    /**
     * used to change the sort direction of the current sort column.
     * @param dir should be either "asc", or "desc", case insensitive.
     */
    public void setSortDirection(String dir)
    {
        if(dir!=null && (dir.equals("asc") || dir.equals("desc")))
            sortDir=dir;   
    }
    /**
     * Returns a QueryWideView as defined by this DataView.
     * Implements stats, no buttons, and a general that prints
     * after stats, but before the data.
     * @return a customized QueryWideView.
     */
    public servlets.dataViews.queryWideViews.QueryWideView getQueryWideView()
    {
         return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out, "Total Query",
                    new String[]{"Keys found"},new Integer[]{new Integer(search.getResults().size())});
            }
            public void printButtons(PrintWriter out, int a, int b, int c, int d)
            {                
            }
            public void printGeneral(PrintWriter out, Search search, String pos,Map storage)
            {
                out.println(" &nbsp&nbsp&nbsp <a href='/databaseWeb/DispatchServlet.csv?hid="+hid+
                            "&script=unknownsText&range=0-"+search.getResults().size()+
                            "'>download in excel format</a>");
            }
         };
    }
  //////////////////////////////////////////////////////////////////////////////
  ///////////// Private methods  ////////////////////////////////////

    private Collection getRecords(List ids)
    { //method 2, multiple queries
        
        Map records=UnknownRecord.getData(dbc,ids,sortCol,sortDir);
        return records.values();                                 
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        //log.debug("printing "+data.size()+" records");
        out.println("<TABLE bgcolor='"+Common.dataColor+"' width='100%'" +
            " align='center' border='1' cellspacing='0' cellpadding='0'>");
        RecordGroup rec;
        RecordVisitor visitor=new HtmlRecordVisitor();
        try{
            for(Iterator i=data.iterator();i.hasNext();)
                ((RecordGroup)i.next()).printRecords(out,visitor);
//            {            
//                rec=(Record)i.next();
//                rec.printHeader(out,visitor);
//                rec.printRecord(out,visitor);
//                rec.printFooter(out,visitor);                
//            }            
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</TABLE>");
    }
    
    private void printUnknownHeader(PrintWriter out)
    {
        String base="http://bioinfo.ucr.edu/projects/internal/Unknowns/external";
        out.println(
        "  <body link='#006699' vlink='#003366'>\n"+
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
        "  <a href='"+base+"/tools.html'><li>Technical Tools</a></li>"+
        "  <a href='"+base+"/external.html'><li>External Resources</a></li>"+
        "  <a href='"+base+"/downloads.html'><li>Downloads</a></li>"+
        "  </font></td>"+
        "  <td>&nbsp;&nbsp;&nbsp;</td>"+
        "  <td valign='top'' width=600> ");
    }                
}
