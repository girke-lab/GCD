/*
 * TextRecordVisitor.java
 *
 * Created on October 19, 2004, 2:11 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * Implements the RecordVisitor interface to provide a text table based view of 
 * Records. Each type of record gets one column, and can use backslashes to
 * seperate fields.
 */
public class TextRecordVisitor implements RecordVisitor
{
    private static Logger log=Logger.getLogger(TextRecordVisitor.class);
    
    /** Creates a new instance of TextRecordVisitor */
    public TextRecordVisitor()
    {
    }
    
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        //out.write("go_number,text,function,");
        out.write("go data,");
    }
    
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        //out.write("target,score,evalue,dbname,");
        out.write("blast data,");
    }
    
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("key,description,");   
        RecordGroup list;        
        for(Iterator i=ur.subRecords.values().iterator();i.hasNext();)
        {
            list=(RecordGroup)i.next();
            if(list != null )
            {
                Iterator j=list.iterator();
                if(j.hasNext())
                    ((Record)j.next()).printHeader(out,this);
            }
                //((Record)list.iterator().next()).printHeader(out,this);                
        }        
    }
    
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        out.write("\""+gr.go_number+" "+gr.function+" "+gr.text+":\"");
    }
    
    public void printRecord(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write(br.target+"\\"+br.score+"\\"+br.evalue+"\\"+br.dbname+"\\");
    }
    
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {        
        out.write("\n"+ur.key+",\""+ur.description+"\",");
        RecordGroup list;
        for(Iterator i=ur.subRecords.values().iterator();i.hasNext();)
        {
            list=(RecordGroup)i.next(); //each collection is from a different table
            if(list==null)
                continue;
            for(Iterator j=list.iterator();j.hasNext();)
                ((Record)j.next()).printRecord(out,this);     
            if(i.hasNext()) ///data from each table goes in one column
                out.write(",");
        }
            
    }
    public void printFooter(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
    }
     
    public void printHeader(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        out.write("Proteomics data,");
    }
    public void printRecord(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        out.write(pr.mol_weight+"\\"+pr.ip+"\\"+pr.charge+"\\"+pr.prob+"\\"+pr.prob_is_neg+"\\");
    }
    
    public void printHeader(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write("Cluster data,");
    }
    public void printRecord(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write(cr.size+"\\"+cr.method+"\\");
    }                  
    public void printFooter(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
    }
    
    public void printHeader(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write("External Unknowns,");
    }
    public void printRecord(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write(eur.source+": "+(eur.isUnknown? "unknowns":"known")+"\\");
    }
    public void printFooter(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        
    }
    
}
