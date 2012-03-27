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
import servlets.dataViews.dataSource.display.PatternedRecordPrinter;
import servlets.dataViews.dataSource.display.html.BlastPurposeFormat;
import servlets.dataViews.dataSource.records.BlastRecord;
import servlets.dataViews.dataSource.records.ClusterRecord;
import servlets.dataViews.dataSource.records.ExternalUnknownRecord;
import servlets.dataViews.dataSource.records.GoRecord;
import servlets.dataViews.dataSource.records.ProbeSetSummaryRecord;
import servlets.dataViews.dataSource.records.ProteomicsRecord;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.structure.RecordFactory;
import servlets.dataViews.dataSource.records.UnknownRecord;
import servlets.dataViews.queryWideViews.*; 
import servlets.search.Search;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.beans.HeaderBean;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.display.html.*;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;

/**
 * This is the main view for the new unknowns database
 */
public class Unknowns2DataView implements DataView
{
    List seq_ids;
    int hid;
    KeyType keyType;
    String sortCol,sortDir;
    int[] dbNums;        
    DbConnection dbc=null;    
    private String userName; 
    
    private static Logger log=Logger.getLogger(Unknowns2DataView.class);    
    
    private HeaderBean header;
    
    /** Creates a new instance of Unknowns2DataView */
    public Unknowns2DataView()
    {
        sortDir="asc"; //default sort direction
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
        header=new HeaderBean();        
    }        
    
    
    /**
     * prints page.
     * @param out used for printing
     */
    public void printData(java.io.PrintWriter out)
    {                
        //printData(out,parseData(getData(seq_ids)));
        printData(out,getRecords(seq_ids));        
        out.println("<script language='JavaScript' type='text/javascript' src='wz_tooltip.js'></script>");  
    }
    
    /**
     * prints page title
     * @param out for output
     */
    public void printHeader(java.io.PrintWriter out)
    {
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.POND);
        header.printStdHeader(out,"", userName!=null);
        

        Common.printUnknownsSearchLinks(out);
        
        out.println(
                "<style type='text/css'>" +
                    ".test a {color: #006699}\n" +
//                    ".test a:visited {color: #00FF00}\n" +
                    ".test a:hover {background-color: #AAAAAA}\n" +
//                    ".test a:active {color: #0000FF}\n" +
                "</style>");
        out.println("<div class='test'>");
        
    }
    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
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
    public void setUserName(String userName)
    {
        this.userName=userName;
        header.setLoggedOn(userName!=null);
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
                out.println("Download data: &nbsp");
                Common.printUnknownDownloadLinks(out, hid, search.getResults().size());
            }
         };
    }
  //////////////////////////////////////////////////////////////////////////////
  ///////////// Private methods  ////////////////////////////////////

    private Collection getRecords(List ids)
    { //method 2, multiple queries
        
        //Map records=UnknownRecord.getData(dbc,ids,sortCol,sortDir);
        //return records.values(); 
        
        Collection unknowns ;//,go, blast, protomics, cluster, external, expSet;
        RecordFactory f=RecordFactory.getInstance();
        QueryParameters qp=new QueryParameters(ids);
        qp.setUserName(userName);

        unknowns=f.getRecords(UnknownRecord.getRecordInfo(), qp);
        f.addSubType(unknowns,GoRecord.getRecordInfo(),qp); 
        f.addSubType(unknowns,BlastRecord.getRecordInfo(),qp);
        f.addSubType(unknowns,ProteomicsRecord.getRecordInfo(),qp);        
        f.addSubType(unknowns,ExternalUnknownRecord.getRecordInfo(),qp);        
        f.addSubType(unknowns,ClusterRecord.getRecordInfo(),qp);
        
        f.addSubType(unknowns,ProbeSetSummaryRecord.getRecordInfo(),qp);        
        f.addSubType(unknowns,ProbeClusterRecord.getRecordInfo(),qp);
        
        return unknowns;        
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        try{                        
            DisplayParameters dp=new DisplayParameters(out);
            dp.setHid(hid);
                        
            PatternedRecordPrinter prp=new PatternedRecordPrinter(dp);            
            prp.addFormat(new Unknowns2MainFormat());
            prp.addFormat(new BlastPurposeFormat());
            prp.addFormat(HtmlPatternFactory.getAllPatterns());
            
            prp.printRecord(data);            
            
//            Writer out2=new BufferedWriter(new FileWriter("/home/khoran/debug.out"));            
//             dp=new DisplayParameters(out2);
//             dp.setHid(hid);
//             
//             prp=new PatternedRecordPrinter(dp);            
//             prp.addFormat(DebugPatternFactory.getAllPatterns());            
//             prp.printRecord(data);                                                
//            out2.close();
            
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</div>");        
    }
    
    

    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.MODEL};
    }

    public void setKeyType(KeyType keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {                
        if(!Common.checkType(this, keyType))
            throw new servlets.exceptions.UnsupportedKeyTypeException(this.getSupportedKeyTypes(),keyType);
        this.keyType=keyType;
    }

    public KeyType getKeyType()
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
