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
import servlets.querySets.*;

public class BlastDataView implements DataView
{
    
    List seq_ids;
    int hid;
    int keyType;

    String sortCol,sortDir;
    
    private static String[] titles=new String[] {"Hit ID","Description","Organism","E Value","Score","% Identity","Length"};
    private static String[] dbColNames=QuerySetProvider.getDataViewQuerySet().getSortableBlastColumns();

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
        Common.printHeader(out,"Cross-Species Profile");
        out.println("<h5 align='center'>BLASTP against " +
                "<a href='http://www.expasy.uniprot.org/index.shtml'>UniProt</a>" +
                " (e-value cutoff: 1e-4)</h5>");
    }
    public void printStats(PrintWriter out)
    {
//         Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
//            new Object[]{new Integer(seq_ids.size())});
    }
    public void printData(PrintWriter out)
    {
        out.println("click on titles to sort");
        //out.println("[ Database is currently updating ]");
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
        
//        Set models=new HashSet();
//        for(Iterator i=data.iterator();i.hasNext();)        
//            models.add(((String)((List)i.next()).get(0)).replaceAll("\\.","_"));
//        
//        out.println(" Go to: ");
//        for(Iterator i=models.iterator();i.hasNext();)
//        {
//            String model=(String)i.next();
//            out.println("<a href='#"+model+"'>"+model+"</a>");
//        }
        
        
        out.println("<TABLE border='1' width='100%' cellspacing='0' cellpadding='0' bgcolor='"+PageColors.data+"'>");
        
        List row;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            if(lastKeyId==null || !lastKeyId.equals(row.get(0))) //print title
            {
                out.println("<a name='"+((String)row.get(0)).replaceAll("\\.","_")+"'></a>"); //add anchor for each model
                out.println("<tr align='left' bgcolor='"+PageColors.title+"'><th colspan='7'>"+row.get(0)+"</th></tr>");
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
        out.println("<tr bgcolor='"+PageColors.title+"'>");
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
                
        String query=QuerySetProvider.getDataViewQuerySet().getBlastDataViewQuery(seq_ids, sortCol, sortDir, -1);
        
        try{
            return dbc.sendQuery(query);            
        }catch(java.sql.SQLException e){
            log.error("could not send query: "+e.getMessage());
        }
        return null;
    }

    public int[] getSupportedKeyTypes()
    {
         return new int[]{Common.KEY_TYPE_BLAST};
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        boolean isValid=false;
        int[] keys=getSupportedKeyTypes();
        for(int i=0;i<keys.length;i++)
            if(keyType == keys[i]){
                isValid=true;
                break;
            }
        if(!isValid)
            throw new servlets.exceptions.UnsupportedKeyTypeException(keys,keyType);
        this.keyType=keyType;
    }

    public int getKeyType()
    {
        return keyType;
    }

    public void setParameters(Map parameters)
    {
    }

    public void setStorage(Map storage)
    {
    }
}
