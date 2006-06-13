/*
 * UnknownsDataView.java
 *
 * Created on September 8, 2004, 1:04 PM
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
import servlets.beans.HeaderBean;
import servlets.querySets.*;

public class UnknownsDataView implements DataView
{
    List seq_ids;
    int hid;
    KeyType keyType;
    String sortCol,sortDir;
    int[] dbNums;    
    File tempDir;
    String[] dbColNames=QuerySetProvider.getDataViewQuerySet().getSortableUnknownsColumns();
    private String userName; 
    
    static DbConnection dbc=null;
    static Logger log=Logger.getLogger(UnknownsDataView.class);   
    
    private HeaderBean header;
    
    /** Creates a new instance of UnknownsDataView */
    public UnknownsDataView() 
    {        
        sortDir="asc"; //default sort direction
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to unknowns");
        //try to use the system temp dir, though this will not always work  
        tempDir=new File(System.getProperty("java.io.tmpdir"));
        TempFileCleaner.getInstance().setDirectory(tempDir);
        if(!TempFileCleaner.getInstance().isAlive())
            TempFileCleaner.getInstance().start();
        
        header=new HeaderBean();        
    }
    public UnknownsDataView(String tempPath) 
    {
        sortDir="asc"; //default sort direction
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to unknowns");
        
        log.debug("temp dir in UnknownsDataview is "+tempPath);        
        tempDir=new File(tempPath);
        TempFileCleaner.getInstance().setDirectory(tempDir);
        if(!TempFileCleaner.getInstance().isAlive())
            TempFileCleaner.getInstance().start();
    }
    public void printData(java.io.PrintWriter out) 
    {
        List data=getData();
        printData(out,data);        
    }    
    public void printHeader(java.io.PrintWriter out)
    {
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
        header.printStdHeader(out,"", userName!=null);
  
    }    
    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
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
    public void setUserName(String userName)
    {
        this.userName=userName;
        header.setLoggedOn(userName!=null);
    }
    
    public void setSortDirection(String dir)
    {//make sure dir is valid before we assign it
        if(dir!=null && (dir.equals("asc") || dir.equals("desc")))
            sortDir=dir;    
    }
    public QueryWideView getQueryWideView() 
    {
        return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out,"Total Query",new String[]{"Records found"},
                    new Object[]{new Integer(search.getResults().size())});
            }
            public void printButtons(PrintWriter out, int hid,int pos,int size,int rpp)
            {}
            public void printGeneral(PrintWriter out, Search search, String pos,Map storage)
            {
                File tempFile=(File)storage.get("unknowns.tempfile");
                if(tempFile==null)
                { //create temp file for entire query.            
                    log.debug("generating a new temp file");
                    List temp=seq_ids; //backup old id list
                    setIds(search.getResults());
                    tempFile=writeTempFile(getData());
                    storage.put("unknowns.tempfile", tempFile);
                    seq_ids=temp;
                }
                if(tempFile!=null) //make sure tempFile is still not null
                    out.println(" &nbsp&nbsp&nbsp <A href='/databaseWeb/temp/"+tempFile.getName()+"'>download in excel format</A>");
                else
                    log.warn("could not create csv file");
                
            }
        };
    }   
    ////////////////////////////////////////////////////////////////

    private void printData(PrintWriter out,List data)
    {         
         String lastId="";                          
         if(data==null)
             return;         
         out.println("<TABLE border='1' cellspacing='0' cellpadding='0' bgcolor='"+PageColors.data+"'>");
         out.println("<TR bgcolor='"+PageColors.title+"'>");
         
         //print titles
         String newDir;
         for(int i=0;i<printNames.length;i++)
         {
             newDir="asc"; //default to asc
             if(sortCol.equals(dbColNames[i])) 
                 newDir=(sortDir.equals("asc"))? "desc":"asc"; //flip direction
             out.println("<th><a href='QueryPageServlet?hid="+hid+"&sortCol="+dbColNames[i]+
                "&sortDirection="+newDir+"'>"+printNames[i]+"</a></th>");             
         }
         //out.println("<th colspan='5'>"+printNames[printNames.length-1]+"</th>");
         out.println("</TR><tr>");
         for(Iterator i=data.iterator();i.hasNext();)
         {
            List row=(List)i.next();
            if(lastId.equals(row.get(0))) //additional treatment
            {               
                //then just print the last element
                out.println(" &nbsp&nbsp&nbsp "+row.get(row.size()-1));
            }
            else{ //new record
                lastId=(String)row.get(0);
                out.println("</td></tr><tr>");
                int c=0;
                for(Iterator j=row.iterator();j.hasNext();c++)
                {
                    String t=(String)j.next();
                    out.print("<td>");
                    if(t==null || t.equals(""))
                        out.print("&nbsp");   
                    else if(c==1) //second column is at number
                        out.println("<a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+
                        t.subSequence(0,t.indexOf('.'))+"'>"+t+"</a>&nbsp&nbsp");
                    else
                        out.print(t);             
                    if(j.hasNext()) //don't print last td, so we can put more treatments in cell
                        out.println("</td>");                    
                }
            }            
         }
         out.println("</td></tr></TABLE>");
    }
    private File writeTempFile(List data)
    {
        File tempFile=null;        
        FileWriter fw;
        try{            
            tempFile=File.createTempFile("results",".csv",tempDir);             
        }catch(IOException e){
             log.warn("could not create temp file: "+e.getMessage());
        }
        if(tempFile==null)
            return null;
        try{
            fw=new FileWriter(tempFile);
            //print title row
            for(int i=0;i<printNames.length;i++)
            {
                fw.write(printNames[i]);
                if(i+1<printNames.length)
                    fw.write(',');
            }
            fw.write('\n');
            //print data
            Object temp;
            List row;
            Object lastID=null;
            for(Iterator i=data.iterator();i.hasNext();)
            {
                row=(List)i.next();
                if(lastID!=null && lastID.equals(row.get(0)))
                    fw.write(","+row.get(row.size()-1).toString());
                else
                {
                    lastID=row.get(0);
                    fw.write('\n');
                    for(Iterator j=row.iterator();j.hasNext();)
                    {
                        temp=j.next();
                        if(temp!=null)
                            fw.write(temp.toString());
                        if(j.hasNext())
                            fw.write(',');
                    }
                }                
            }
            fw.write('\n');
            fw.close();
        }catch(IOException e){
            log.warn("could not write to temp file "+tempFile.getPath()+":"+e.getMessage());
        }
        return tempFile;
    }
    private List getData()
    {        
        try{
            return dbc.sendQuery(QuerySetProvider.getDataViewQuerySet().getUnknownsDataViewQuery(seq_ids,sortCol,sortDir, null));
        }catch(Exception e){
            log.error("could not send query: "+e.getMessage());
        }
        return null;
    }
    
    
   
    
    String[] printNames=new String[]{
            "Unknown id",
            "At Key" ,
            "Description" ,
            "Unknown Method TIGR" ,
            "Unknown Method SWP_BLAST" ,
            "Unknown Method GO: MFU OR CCU OR BPU" ,
            "Unknown Method GO: MFU" ,
            "Unknown Method InterPro" ,
            "Unknown Method Pfam" ,
            "Citosky Small List" ,
            "SALK tDNA-Insertion" ,
            "EST avail" ,
            "avail" ,
            "flcDNA TIGR (XML) avail" ,
            "Nottingham Chips: 3x >90" ,
            "Rice Orth E-value" ,
            "Gene Family Size 35%_50%_70% ident" ,
            "Pet Gene from" ,
            "Targeting Ipsort" ,
            "Targeting Predotar" ,
            "Targeting Targetp" ,
            "Membr dom Hmmtop" ,
            "Membr dom Thumbup" ,
            "Membr dom TMHMM" ,
            "Focus list of grant" ,
            "Selected by" ,
            "Multiple selects" ,
            "Occurrence in treaments",        
            "HumanRatMouse Orth E-value" ,
            "S. cerevisiae E-value" ,                    
            "Treatments"
        };

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
