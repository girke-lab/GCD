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
import servlets.dataViews.unknownViews.*;
import servlets.*;

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
        
        records=UnknownRecord.getData(dbc,ids); 
        
        for(Iterator i=records.entrySet().iterator();i.hasNext();)
        {            
            Map.Entry set=(Map.Entry)i.next();         
            for(int j=0;j<subRecordMaps.length;j++)
                ((UnknownRecord)set.getValue()).setSubRecordList(names[j], (List)subRecordMaps[j].get(set.getKey())); 
        }        
        return records.values();
    }
    
    private void writeTempFile(java.io.OutputStream out, Collection data)
    {                
        RecordVisitor visitor=new TextRecordVisitor();
        PrintWriter fw;
        try{
            fw=new PrintWriter(out);            
            //print title row
            Record rec;
            boolean isFirst=true;
            for(Iterator i=data.iterator();i.hasNext();)
            {
                rec=(Record)i.next();
                if(isFirst){
                    rec.printHeader(fw, visitor);
                    isFirst=false;
                }
                rec.printRecord(fw,visitor);
            }                        
            fw.close();
        }catch(IOException e){
            log.error("could not write output: "+e.getMessage());
        }       
    }
    public String getContentType()
    {
        return "text/x-comma-separated-values";
        //return "text/html";
    }
}