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
import servlets.KeyTypeUser.KeyType;
import servlets.beans.HeaderBean;
import servlets.dataViews.dataSource.display.html.CorrelationSetFormat;
import servlets.dataViews.dataSource.records.CorrelationRecord;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.display.PatternedRecordPrinter;
import servlets.dataViews.dataSource.display.html.HtmlPatternFactory;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.dataSource.structure.RecordFactory;
import servlets.dataViews.queryWideViews.*;
import servlets.search.Search;

/**
 *
 * @author khoran
 */
public class CorrelationsDataView implements DataView
{
    
    private static Logger log=Logger.getLogger(CorrelationsDataView.class);    
    
    private KeyType keyType;
    private int hid;
    private String sortDir, sortCol,action,catagory;
    private int[] dbNums;        
    private String userName;     
    
    
    private List corrIds;
    DbConnection dbc=null;  
    private HeaderBean header;
    
    /** Creates a new instance of CorrelationsDataView */
    public CorrelationsDataView()
    {
        sortCol=null; 
        sortDir="DESC";        
        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
        header=new HeaderBean();        
                                
    }

    public KeyType getKeyType()
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
                out.println(DescriptionManager.wrapText("download","Download data:")+" &nbsp");
                out.println("&nbsp<a href='"+link+"&dataType=Correlation'>Correlations</a>");
            }            

            public void printButtons(PrintWriter out, int hid, int pos, int c, int d)
            {                
            }                                     
         };        
    }

    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.CORR};
    }

    public void printData(java.io.PrintWriter out)
    {
        printData(out,getRecords());
        out.println("<script language='JavaScript' type='text/javascript' src='wz_tooltip.js'></script>");
    }

    public void printHeader(java.io.PrintWriter out)
    {
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.PED);
        header.printStdHeader(out,"", userName!=null);
                
        
        
//        out.println(
//                "<style type='text/css'>" +
//                    ".test a {color: #006699}\n" +
//                    ".test a:hover {background-color: #AAAAAA}\n" +
//                "</style>");
//        
//        out.println("<div class='test'>");
        
        Common.printUnknownsSearchLinks(out);
    }
    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
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

    public void setKeyType(KeyType keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        if(!Common.checkType(this, keyType))
            throw new servlets.exceptions.UnsupportedKeyTypeException(this.getSupportedKeyTypes(),keyType);
        this.keyType=keyType;
    }
    public void setUserName(String userName)
    {
        this.userName=userName;
        header.setLoggedOn(userName!=null);
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
        qp.setUserName(userName);
        qp.setCatagory(catagory);
        
        records=f.getRecords(CorrelationRecord.getRecordInfo(), qp);
        
        f.addSubType(records,ProbeClusterRecord.getRecordInfo(),qp);
        f.addSubType(records,SequenceRecord.getRecordInfo(),qp);
        

        return records;
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
                
        try{
            
            DisplayParameters dp=new DisplayParameters(out);
            dp.setHid(hid);
            dp.setSortCol(sortCol);
            dp.setSortDir(sortDir);
                        
            PatternedRecordPrinter prp=new PatternedRecordPrinter(dp);            
            prp.addFormat(new CorrelationSetFormat());
            prp.addFormat(HtmlPatternFactory.getAllPatterns());
            prp.printGroup(data);            
            
 
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</div>");
    }        
    
}
