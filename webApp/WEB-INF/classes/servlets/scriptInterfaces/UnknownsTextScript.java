/*
 * UnknownsTextScript.java
 *
 * Created on November 10, 2004, 8:17 AM
 */

package servlets.scriptInterfaces;

/**
 *
 * @author  khoran
 */

import java.net.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.dataViews.records.*;


public class UnknownsTextScript implements Script
{
    
    private static Logger log=Logger.getLogger(UnknownsTextScript.class);
    private DbConnection dbc=null;
    /** Creates a new instance of UnknownsTextScript */
    public UnknownsTextScript()
    {        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection for text dump");
    }    

    public void run(java.io.OutputStream out, java.util.List ids)
    {
        writeTempFile(out,getRecords(ids));
    }
    
    private Collection getRecords(List ids)
    { //method 2, multiple queries
        
        Map records=UnknownRecord.getData(dbc,ids);
        return records.values();                        
    }
    
    private void writeTempFile(java.io.OutputStream os, Collection data)
    {               
        RecordVisitor visitor=new TextRecordVisitor();
        PrintWriter out;        
        
        try{
            out=new PrintWriter(os);            
            
            for(Iterator i=data.iterator();i.hasNext();)
                ((RecordGroup)i.next()).printRecords(out,visitor);  

            
//            //print title row
//            Record rec;
//            boolean isFirst=true;
//            for(Iterator i=data.iterator();i.hasNext();)
//            {
//                rec=(Record)i.next();
//                if(isFirst){
//                    rec.printHeader(fw, visitor);
//                    isFirst=false;
//                }
//                rec.printRecord(fw,visitor);
//            }                        
            out.close();
        }catch(IOException e){
            log.error("could not write output: "+e.getMessage());
        }       
    }
    public String getContentType()
    {
        /* possabilties
         *  text/comma-separated-values
            text/csv
            application/csv
            application/excel
            application/vnd.ms-excel
            application/vnd.msexcel
            text/anytext
         */
        return "text/csv";
        //return "text/html";
        //return "text/csv";
    }
}
