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
    private final FieldRange unknown_key=new FieldRange(0,6),
                             blast_results=new FieldRange(6,17),
                             go_numbers=new FieldRange(17,20),
                             clusters=new FieldRange(20,23),
                             proteomics=new FieldRange(23,28),
                             externals=new FieldRange(28,30);
    
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
        Map records,t;
        Map[] subRecordMaps=new Map[]{
            GoRecord.getData(dbc,ids),
            BlastRecord.getData(dbc,ids),            
            ProteomicsRecord.getData(dbc,ids),
            ClusterRecord.getData(dbc,ids),
            ExternalUnknownRecord.getData(dbc,ids)
        };
        //these names must appear in the same order as the subRecordMaps array
        String[] names=new String[]{"go_numbers","blast_results","proteomics","clusters","externals"};
        
        records=UnknownRecord.getData(dbc,ids,sortCol,sortDir);
        
        for(Iterator i=records.entrySet().iterator();i.hasNext();)
        {            
            Map.Entry set=(Map.Entry)i.next();
         //   log.debug("working on key "+set.getKey());
            for(int j=0;j<subRecordMaps.length;j++)
                ((UnknownRecord)set.getValue()).setSubRecordList(names[j], (List)subRecordMaps[j].get(set.getKey()));
        }        
        return records.values();
    }
    
    
    private Collection parseData(List raw_data)
    { //method 1, one big query
        //recivies unformatted data from database
        List row;
        UnknownRecord rec;
        BlastRecord br;
        //Set records=new HashSet();
        Map records=new LinkedHashMap();        
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
            rec.addSubRecord("go_numbers",new GoRecord(row.subList(go_numbers.s,go_numbers.e))); 
            rec.addSubRecord("blast_results",new BlastRecord(row.subList(blast_results.s,blast_results.e))); 
            rec.addSubRecord("proteomics",new ProteomicsRecord(row.subList(proteomics.s,proteomics.e)));
            rec.addSubRecord("clusters",new ClusterRecord(row.subList(clusters.s,clusters.e)));
            rec.addSubRecord("externals",new ExternalUnknownRecord(row.subList(externals.s,externals.e))); 
        }
        return records.values(); //this is  a list of Record objects
    }
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of Records        
        
        //log.debug("printing "+data.size()+" records");
        out.println("<TABLE bgcolor='"+Common.dataColor+"' width='100%'" +
            " align='center' border='1' cellspacing='0' cellpadding='0'>");
        Record rec;
        RecordVisitor visitor=new HtmlRecordVisitor();
        try{
            for(Iterator i=data.iterator();i.hasNext();)
            {            
                rec=(Record)i.next();
                rec.printHeader(out,visitor);
                rec.printRecord(out,visitor);
                rec.printFooter(out,visitor);                
            }            
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</TABLE>");
    }
    private List getData(List seq_ids)
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
        return new ArrayList(); //prevents some null pointer problems
    }
    private String buildQuery(String conditions)
    {
        //THIS IS CURRENTLY BROKEN! (but not currently used)
        String[] tables=new String[]{"unknowns.unknown_keys","unknowns.blast_results",
                                     "unknowns.blast_databases","go.go_numbers","go.seq_gos",
                                     "unknowns.cluster_info_and_counts_view","unknowns.proteomics_stats",
                                     "unknowns.external_unknowns"};
        String[][] fields=new String[][]{
            {"key","description","est_count","mfu","ccu","bpu"},        //[0-6)
            {"target_accession","target_description","e_value","score","identities","length","positives","gaps"}, //[6-14)
            {"db_name,link,method"}, //[14,17)
            {"go_number","function","text"}, //[17-20)
            {}, //no fields for seq_gos
            {"cluster_name","size","cutoff"},  //[20,23)
            {"mol_weight","ip","charge","prob_in_body","prob_is_neg"}, //[23,28)
            {"is_unknown","source"} //[28,30)
        };
        StringBuffer fieldList=new StringBuffer();
        StringBuffer tableList=new StringBuffer();
        
        for(int i=0;i<tables.length;i++)
        {
            tableList.append(tables[i]+",");
            for(int j=0;j<fields[i].length;j++)
                fieldList.append(tables[i]+"."+fields[i][j]+",");            
        }
        fieldList.deleteCharAt(fieldList.length()-1); //cut off last ','                     
        tableList.deleteCharAt(tableList.length()-1);
        
        String query="SELECT "+fieldList+"\n"+
            " FROM " +tableList+
                ", (select distinct on (br.key_id,br.e_value)  br.blast_id,br.key_id \n" +
                 "from unknowns.blast_results as br, \n" +
                    "(select key_id,min(e_value) as e_value \n" +
                     "from unknowns.blast_results \n" +
                     "group by blast_db_id,key_id) as mins \n" +
                 "where br.key_id=mins.key_id and br.e_value=mins.e_value \n" +
                 "order by br.key_id) as min_blast_ids \n" +
            " WHERE       unknowns.unknown_keys.key_id=unknowns.blast_results.key_id \n" +
            "        AND substring(unknowns.unknown_keys.key from 1 for 9)=go.seq_gos.accession \n" +
            "        AND go.seq_gos.go_id=go.go_numbers.go_id \n" +
            "        AND unknowns.unknown_keys.key_id=min_blast_ids.key_id \n" +
            "        AND min_blast_ids.blast_id=unknowns.blast_results.blast_id \n" +
            "        AND unknowns.blast_results.blast_db_id=unknowns.blast_databases.blast_db_id \n"+
            "        AND unknowns.unknown_keys.key_id=unknowns.cluster_info_and_counts_view.key_id \n"+
            "        AND unknowns.unknown_keys.key_id=unknowns.proteomics_stats.key_id \n"+
            "        AND unknowns.unknown_keys.key_id=unknowns.external_unknowns.key_id \n"+
            "        AND ("+conditions+")\n"+
            " ORDER BY "+sortCol+" "+sortDir;

               
        log.info("query is: "+query);
        return query;
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
