    /*
 * CorrelationsDataView.java
 *
 * Created on November 17, 2005, 10:57 AM
 *
 */

package servlets.dataViews;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.dataViews.queryWideViews.*;
import servlets.dataViews.records.*;
import servlets.search.Search;

/**
 *
 * @author khoran
 */
public class CorrelationsDataView implements DataView
{
    
    private static Logger log=Logger.getLogger(CorrelationsDataView.class);    
    
    private int keyType, hid;
    private String sortDir, sortCol,action,catagory;
    private int[] dbNums;        

    private List corrIds;
    DbConnection dbc=null;  
    
    
    /** Creates a new instance of CorrelationsDataView */
    public CorrelationsDataView()
    {
        sortCol=null;
        sortDir="DESC";        
        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
                        
    }

    public int getKeyType()
    {
        return keyType;
    }

    public QueryWideView getQueryWideView()
    {
        return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out, "Total Query",
                    new String[]{"Keys found"},new Integer[]{new Integer(search.getResults().size())});
            }
            public void printGeneral(PrintWriter out, Search search, String position,Map storage)
            {
                String link="DispatchServlet?hid="+hid+"&script=unknownsText&range=0-"+search.getResults().size();
                out.println("Download data: &nbsp");
                out.println("&nbsp<a href='"+link+"&dataType=Correlation'>Correlations</a>");
            }            

            public void printButtons(PrintWriter out, int hid, int pos, int c, int d)
            {                
            }                                     
         };        
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_CORR};
    }

    public void printData(java.io.PrintWriter out)
    {
        printData(out,getRecords());
    }

    public void printHeader(java.io.PrintWriter out)
    {
        Common.printUnknownHeader(out);                
        out.println(
                "<style type='text/css'>" +
                    ".test a {color: #006699}\n" +
                    ".test a:hover {background-color: #AAAAAA}\n" +
                "</style>");
        
        out.println("<div class='test'>");
        
        Common.printUnknownsSearchLinks(out);
    }

    public void printStats(java.io.PrintWriter out)
    {
        Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(corrIds.size())});
    }

    public void setData(String sortCol, int[] dbList, int hid)
    {        
        this.hid=hid;
        this.dbNums=dbList;     
        
        if(sortCol!=null && sortCol.length() > 0)
            this.sortCol=sortCol;
    }

    public void setIds(java.util.List ids)
    {
        corrIds=ids;
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        this.keyType=keyType;
    }

    public void setParameters(java.util.Map parameters)
    {
        String[] catagories=(String[])parameters.get("catagory");
        if(catagories!=null && catagories.length > 0)
            catagory=catagories[0];
    }

    public void setSortDirection(String dir)
    {
        if(dir!=null && (dir.equalsIgnoreCase("asc") || dir.equalsIgnoreCase("desc")))
            sortDir=dir;  
    }

    public void setStorage(java.util.Map storage)
    {        
    }
    
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private Collection getRecords()
    {                       
        Collection records=null;
        RecordFactory f=RecordFactory.getInstance();
        log.debug("sortCol="+sortCol);
        QueryParameters qp=new QueryParameters(corrIds,sortCol,sortDir);
        qp.setCatagory(catagory);
        records=f.getRecords(CorrelationRecord.getRecordInfo(), qp);
        

        return records;
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        //log.debug("printing "+data.size()+" records");
        out.println("<TABLE bgcolor='"+PageColors.data+"' width='100%'" +
            " align='center' border='1' cellspacing='0' cellpadding='0'>");
        Record rec;
        HtmlRecordVisitor visitor=new HtmlRecordVisitor();                        
        visitor.setHid(hid);
        visitor.setSortInfo(sortCol, sortDir);
        /* we need to find the first psk here.
         * then we need to figure out what catagories we have and print
         * them out in the header
         */                
        boolean isFirst=true;
        
        try{
            for(Iterator i=data.iterator();i.hasNext();)
            {
                rec=(Record)i.next();                
                log.debug("printing "+rec);
                if(isFirst)                                        
                    rec.printHeader(out, visitor);                
                isFirst=false;
                rec.printRecord(out, visitor);
                if(i.hasNext())
                    rec.printFooter(out, visitor);
            }          
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</TABLE></div>");
    }        
    
}
