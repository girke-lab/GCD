/*
 * Unknowns2DataView.java
 *
 * Created on October 12, 2004, 12:17 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.*;
import servlets.dataViews.queryWideViews.*; 
import servlets.dataViews.DataView;
import servlets.search.Search;
import org.apache.log4j.Logger;

public class Unknowns2DataView implements DataView
{
    List seq_ids;
    int hid;
    String sortCol,sortDir;
    int[] dbNums;        
    DbConnection dbc=null;
    Collection records=null;
    
    private static Logger log=Logger.getLogger(Unknowns2DataView.class);    
    private final FieldRange unknown_key=new FieldRange(1,7),
                             blast_results=new FieldRange(9,17),
                             go_numbers=new FieldRange(18,21);
    
    /** Creates a new instance of Unknowns2DataView */
    public Unknowns2DataView()
    {
        sortDir="asc"; //default sort direction
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
    }        
    
    public void printData(java.io.PrintWriter out)
    {        
        if(records==null)
            loadData();
        printData(out,records);
        out.println("</td></table>"); //close page level table
    }
    
    public void printHeader(java.io.PrintWriter out)
    {
        printUnknownHeader(out);
    }
    
    public void printStats(java.io.PrintWriter out)
    {
        Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(seq_ids.size())});
    }
    
    public void setData(String sortCol, int[] dbList, int hid)
    {
        this.hid=hid;
        this.sortCol=sortCol;
        this.dbNums=dbList;
    }
    
    public void setIds(java.util.List ids)
    {
         this.seq_ids=ids;   
    }
    
    public void setSortDirection(String dir)
    {
        if(dir!=null && (dir.equals("asc") || dir.equals("desc")))
            sortDir=dir;   
    }
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
         };
    }
  //////////////////////////////////////////////////////////////////////////////
    ///////////// Private methods  ////////////////////////////////////
    private void loadData()
    {
        if(seq_ids.size()==0)
            records=new ArrayList();
        else
            records=parseData(getData());
    }
    private Collection parseData(List raw_data)
    {  //recivies unformatted data from database
        List row;
        UnknownRecord rec;
        BlastRecord br;
        //Set records=new HashSet();
        Map records=new HashMap();        
        log.debug("parsing "+raw_data.size()+" rows");
        
        for(Iterator i=raw_data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            //log.debug(row);
            rec=(UnknownRecord)records.get(row.get(0));
            if(rec==null)
            {
                rec=new UnknownRecord(row.subList(unknown_key.s,unknown_key.e));            
                records.put(row.get(0),rec);
            }            
            rec.addSubRecord("blast_results",new BlastRecord(row.subList(blast_results.s,blast_results.e))); 
            rec.addSubRecord("go_numbers",new GoRecord(row.subList(go_numbers.s,go_numbers.e)));            
        }
        return records.values();
    }
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of Records        
        
        log.debug("printing "+data.size()+" records");
        out.println("<TABLE bgcolor='"+Common.dataColor+"' width='100%'" +
            " align='center' border='1' cellspacing='0' cellpadding='0'>");
        Record rec;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            rec=(Record)i.next();
            rec.printHeader(out);
            rec.printRecord(out);
            out.println("<tr><td bgcolor='FFFFFF' colspan='5'>&nbsp</td></tr>");
        }            
        
        out.println("</TABLE>");
    }
    
    private List getData()
    {
        StringBuffer conditions=new StringBuffer();
        conditions.append("unknowns.unknown_keys.key_id in (");
        for(Iterator i=seq_ids.iterator();i.hasNext();)
        {
            conditions.append(i.next());
            if(i.hasNext())
                conditions.append(",");
        }
        conditions.append(")");
        try{
            return dbc.sendQuery(buildQuery(conditions.toString()));
        }catch(Exception e){
            log.error("could not send query: "+e.getMessage());
        }
        return null;
    }
    private String buildQuery(String conditions)
    {
        String query="SELECT unknowns.unknown_keys.*,unknowns.blast_results.*,go.go_numbers.* "+
            " FROM unknowns.unknown_keys, unknowns.blast_results,go.go_numbers,go.seq_gos "+
            " WHERE unknowns.unknown_keys.key_id=unknowns.blast_results.key_id "+
                " AND substring(unknowns.unknown_keys.key from 1 for 9)=go.seq_gos.accession " +
                " AND go.seq_gos.go_id=go.go_numbers.go_id "+
                " AND ("+conditions+")"+
            " ORDER BY "+sortCol+" "+sortDir;
               
        log.info("query is: "+query);
        return query;
    }
    private void printUnknownHeader(PrintWriter out)
    {
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
        "  <a href='"+base+"/tools.html'><li>Technical Tools</a></li>"+
        "  <a href='"+base+"/external.html'><li>External Resources</a></li>"+
        "  <a href='"+base+"/downloads.html'><li>Downloads</a></li>"+
        "  </font></td>"+
        "  <td>&nbsp;&nbsp;&nbsp;</td>"+
        "  <td valign='top'' width=600> ");
    }        
    
    class FieldRange
    {
        public int s,e;
        public FieldRange(int s,int e)
        {
            this.s=s;
            this.e=e;
        }
    }
}
