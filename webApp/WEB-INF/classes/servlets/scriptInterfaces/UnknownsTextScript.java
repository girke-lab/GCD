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
    
    private static final int batchSize=1000;
    
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
        RecordVisitor visitor=new TextRecordVisitor();
        //RecordVisitor visitor=new DebugRecordVisitor();
        Collection data=null;
        Record rec=null;
        boolean isFirst=true;
                
        try{
            for(int j=0;j<ids.size();j+=batchSize)
            { //send data in batches to avoid queries that are too long.
                int end=(j+batchSize>ids.size())?ids.size():j+batchSize;
                //log.debug("j="+j+", size="+ids.size()+", end="+end);
                data=getRecords(ids.subList(j,end));
                
                for(Iterator i=data.iterator();i.hasNext();)
                {
                    rec=(Record)i.next();

                    if(isFirst)//print header for first batch                    
                        rec.printHeader(out, visitor);       
                    
                    rec.printRecord(out, visitor);
                    
                    if(j+batchSize >= ids.size() && !i.hasNext()) //footer for last batch                    
                        rec.printFooter(out,visitor);
                                        
                    isFirst=false;
                }
            }
        }catch(IOException e){
            log.error("io error: "+e);
        }

    }   
    private Collection getRecords(List ids)
    { 
        Collection unknowns=null;
        RecordFactory f=RecordFactory.getInstance();
        QueryParameters qp=new QueryParameters();
        qp.setIds(ids);
        
        unknowns=f.getRecords(UnknownRecord.getRecordInfo(), qp);
        
        if(dataType.equals("AffyComp"))
            f.addSubType(unknowns, AffyCompRecord.getRecordInfo(),qp);            
        else if(dataType.equals("AffyDetail"))    
            f.addSubType(unknowns, AffyDetailRecord.getRecordInfo(),qp);            
        else if(dataType.equals("AffyExpSet"))    
            f.addSubType(unknowns, AffyExpSetRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Blast"))    
            f.addSubType(unknowns, BlastRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Cluster")) 
            f.addSubType(unknowns, ClusterRecord.getRecordInfo(),qp);            
        else if(dataType.equals("ExternalUnknown"))    
            f.addSubType(unknowns, ExternalUnknownRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Go"))    
            f.addSubType(unknowns, GoRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Proteomics"))            
            f.addSubType(unknowns, ProteomicsRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Unknown"))
            ;
        else
            log.error("invalid dataType: "+dataType);
        
        return unknowns;
    }
    
  
    public String getContentType()
    {        
        //return "text/csv";
        //return "text/html";
        return "text/plain";
        
    }
}
