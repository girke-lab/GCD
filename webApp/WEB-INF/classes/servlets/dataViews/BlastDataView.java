/*
 * BlastDataView.java
 *
 * Created on December 14, 2004, 11:02 AM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.*;
import servlets.search.Search;
import servlets.dataViews.queryWideViews.*; 
import org.apache.log4j.Logger;

public class BlastDataView implements DataView
{
    
    List seq_ids;
    int hid;
    String sortCol,sortDir;
    
    private static String[] titles=new String[] {"Hit ID","Description","Organism","E Value","Score","% Identity","Length"};
    private static String[] dbColNames=new String[] { "target.accession","target.description",
                            "o.name","br.e_value","br.score","br.identities","br.length"};
    
//    {"br.target_accession_id","br.target_description","uo.organism",
//                                    "br.e_value","br.score","br.identities","br.length"};
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");
    private static Logger log=Logger.getLogger(BlastDataView.class);
    
    /** Creates a new instance of BlastDataView */
    public BlastDataView()
    {
        sortCol="br.e_value";
        sortDir="asc";
        if(dbc==null)
            log.error("could not get db connection to khoran");
    }
 
    public void setData(String sortCol,int[] dbList,int hid)
    {
        if(sortCol!=null && sortCol.length() > 0)
            this.sortCol=sortCol;
        this.hid=hid;
    }
    public void setIds(List ids)
    {
        seq_ids=ids;
    }
    public void setSortDirection(String dir)
    {
        if(dir!=null && (dir.equals("asc") || dir.equals("desc")))
            sortDir=dir;     
    }
    
    public void printHeader(PrintWriter out)
    {
        Common.printHeader(out);
    }
    public void printStats(PrintWriter out)
    {
//         Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
//            new Object[]{new Integer(seq_ids.size())});
    }
    public void printData(PrintWriter out)
    {
        out.println("click on titles to sort");
        displayData(out,getData());
        out.println("</td></tr></table></font></body></html>"); //close header
    }
    
    public QueryWideView getQueryWideView()
    {
         return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out,"Total Query",new String[]{"Hits found"},
                    new Object[]{new Integer(search.getResults().size())});
            }
            public void printButtons(PrintWriter out, int hid,int pos,int size,int rpp)
            {}            
            public boolean printAllData()
            {
                return true;
            }
        };
    }
    
    private void displayData(PrintWriter out,List data)
    {
        String lastKeyId=null;
        if(data==null)
            return;
        
        out.println("<TABLE border='1' width='100%' cellspacing='0' cellpadding='0' bgcolor='"+Common.dataColor+"'>");
        
        List row;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            if(lastKeyId==null || !lastKeyId.equals(row.get(0))) //print title
            {
                out.println("<tr align='left' bgcolor='"+Common.titleColor+"'><th colspan='7'>"+row.get(0)+"</th></tr>");
                printTableTitles(out);                
            }
            lastKeyId=(String)row.get(0);
            out.println("<tr>");
            
            out.println("<td><a href='"+((String)row.get(2)).replaceAll("\\$V\\{key\\}",(String)row.get(1))+"'>"+
                        row.get(1)+"</a></td>");
            for(int j=3;j<row.size();j++)
                out.println("<td>"+(row.get(j)==null?"&nbsp":row.get(j))+"</td>");
            out.println("</tr>");
        }
        out.println("</TABLE>");
    }
    private void printTableTitles(PrintWriter out)
    {
        String newDir;
        out.println("<tr bgcolor='"+Common.titleColor+"'>");
        for(int i=0;i<titles.length;i++)
        {
            newDir="asc"; //default to asc
            if(sortCol.equals(dbColNames[i])) //reverse current sort col
                newDir=(sortDir.equals("asc"))? "desc":"asc"; //flip direction
            out.println("<th nowrap ><a href='QueryPageServlet?hid="+hid+"&sortCol="+dbColNames[i]+
                "&sortDirection="+newDir+"'>"+titles[i]+"</a></th>");             
        }
        out.println("</tr>");
    }
    private List getData()
    {
                
        String query=
            "SELECT query.accession,target.accession,gd.link,target.description, " +
            "   o.name,br.e_value,br.score,br.identities,br.length " +
            "FROM general.blast_results as br, general.accessions as query, " +
            "   general.accessions as target LEFT JOIN general.organisms as o USING(organism_id), " +
            "   general.genome_databases as gd " +
            "WHERE br.query_accession_id=query.accession_id AND " +
            "   br.target_accession_id=target.accession_id AND " +
            "   target.genome_db_id=gd.genome_db_id AND " +
                Common.buildIdListCondition("br.blast_id",seq_ids)+
            "ORDER BY query.accession, "+sortCol+" "+sortDir;

        
//                    "SELECT key,br.target_accession,bd.link,br.target_description,uo.organism,br.e_value," +
//                        " br.score,br.identities,br.length " +
//                     "FROM unknowns.unknown_keys as uk, unknowns.blast_results as br LEFT JOIN" +
//                     "      unknowns.uniprot_orgs as uo ON (br.target_accession=uo.uniprot_acc), "+
//                     "      unknowns.blast_databases as bd "+
//                     "WHERE uk.key_id=br.key_id AND br.blast_db_id=bd.blast_db_id AND  "+
//                            Common.buildIdListCondition("br.blast_id",seq_ids)+
//                     " ORDER BY br.key_id, "+sortCol+" "+sortDir;
        try{
            return dbc.sendQuery(query);            
        }catch(java.sql.SQLException e){
            log.error("could not send query: "+e.getMessage());
        }
        return null;
    }
}
