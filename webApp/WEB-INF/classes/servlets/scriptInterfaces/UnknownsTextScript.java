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
    
    private static final int batchSize=5000;
    
    private String dataType;
    
    /** Creates a new instance of UnknownsTextScript */
    public UnknownsTextScript(Map parameters)
    {        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection for text dump");
        
        if(parameters!=null && parameters.containsKey("dataType") &&
                ((String[])parameters.get("dataType")).length!=0 )
            dataType=((String[])parameters.get("dataType"))[0];
    }    

    public void run(java.io.OutputStream os, java.util.List ids)
    {        
        PrintWriter out=new PrintWriter(os);
        writeData(out,ids);
        out.close();
    }
    
    private void writeData(PrintWriter out,List ids)
    {
        TextRecordVisitor visitor=new TextRecordVisitor();
        Collection data=null;
        RecordGroup rg=null;
        
        try{
            for(int j=0;j<ids.size();j+=batchSize)
            { //send data in batches to avoid quries that are too long.
                int end=j+batchSize>ids.size()?ids.size():j+batchSize;
                log.debug("j="+j+", size="+ids.size()+", end="+end);
                data=getRecords(ids.subList(j,j+batchSize));

                for(Iterator i=data.iterator();i.hasNext();)
                {
                    rg=(RecordGroup)i.next();
                    if(j==0)//print header for first batch
                        rg.printRecords(out,visitor,true,false);
                    else if(j+batchSize >= ids.size()) //footer for last batch
                        rg.printRecords(out,visitor, false,true);
                    else //no header or footer
                        rg.printRecords(out,visitor,false,false);
                }
            }
        }catch(IOException e){
            log.error("io error: "+e);
        }

    }   
    private Collection getRecords(List ids)
    { 
        Map records=null;
        if(dataType.equals("AffyComp"))
            records=AffyCompRecord.getDataByAcc(dbc,ids);
        else if(dataType.equals("AffyDetail"))    
            records=AffyDetailRecord.getDataByAcc(dbc,ids);
        else if(dataType.equals("AffyExpSet"))    
            records=AffyExpSetRecord.getData(dbc,ids);
        else if(dataType.equals("Blast"))    
            records=BlastRecord.getData(dbc,ids);
        else if(dataType.equals("Cluster")) 
            records=ClusterRecord.getData(dbc,ids);
        else if(dataType.equals("ExternalUnknown"))    
            records=ExternalUnknownRecord.getData(dbc,ids);
        else if(dataType.equals("Go"))    
            records=GoRecord.getData(dbc,ids);
        else if(dataType.equals("Proteomics"))            
            records=ProteomicsRecord.getData(dbc,ids);
        else if(dataType.equals("Unknown"))
            records=UnknownRecord.getData(dbc,ids);
        else
            log.error("invalid dataType: "+dataType);
            
        if(records==null)
            return null;
        return records.values();                        
    }
    
  
    public String getContentType()
    {        
        //return "text/csv";
        //return "text/html";
        return "text/plain";
        
    }
}
