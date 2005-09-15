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
    private String currentAccession="";
    private boolean printDescription;
    
    /** Creates a new instance of TextRecordVisitor */
    public TextRecordVisitor()
    {
    }
    public void setPrintDescription(boolean b)
    {
        printDescription=b;
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="misc">
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        out.write("accession\tgo_number\ttext\tfunction\n");
        //out.write("go data,");
    }
    
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write("accession\ttarget\tscore\tevalue\tdbname\n");
        //out.write("blast data,");
    }
    
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        log.debug("printing unknowns header");
        if(printDescription)
            out.write("key\tdescription\n");   
        else
        {        
            log.debug("printing sub record headers");
            Record list;        
            for(Iterator i=ur.iterator();i.hasNext();)
            {
                list=(Record)i.next();
                if(list != null )
                {                    
                    Iterator j=list.iterator();
                    if(j.hasNext())
                    {
                        log.debug("found a record");
                        ((Record)j.next()).printHeader(out,this);
                    }
                }                 
            }        
        }
    }
    
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        out.write(currentAccession+"\t"+gr.go_number+"\t"+gr.function+"\t"+gr.text+"\n");
    }
    
    public void printRecord(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write(currentAccession+"\t"+br.target+"\t"+br.score+"\t"+br.evalue+"\t"+br.dbname+"\n");
    }
    
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {        
        //log.debug("printing unknown record");
        currentAccession=ur.key;
        if(printDescription)
            out.write(ur.key+"\t"+ur.description+"\n");
        else
        {
            Record list;
            for(Iterator i=ur.iterator();i.hasNext();)
            {
                list=(Record)i.next(); //each collection is from a different table
                if(list==null)
                    continue;
                
                for(Iterator j=list.iterator();j.hasNext();)
                    ((Record)j.next()).printRecord(out,this);     
                if(i.hasNext()) ///data from each table goes in one column
                    out.write(",");
            }            
        }            
    }
    public void printFooter(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
    }
     
    public void printHeader(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        //out.write("Proteomics data,");
        out.write("accession\tmolecular weight\tip\tcharge\tprobability\tprobability_is_negative\n");        
    }
    public void printRecord(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        out.write(currentAccession+"\t"+pr.mol_weight+"\t"+pr.ip+"\t"+pr.charge+"\t"+pr.prob+"\t"+pr.prob_is_neg+"\n");
    }
    
    public void printHeader(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        //out.write("Cluster data,");
        out.write("accession\tsize\tmethod\n");
    }
    public void printRecord(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write(currentAccession+"\t"+cr.size+"\t"+cr.method+"\n");
    }                  
    public void printFooter(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
    }
    
    public void printHeader(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        //out.write("External Unknowns,");
        out.write("accession\tsource\tis_unknown\n");
    }
    public void printRecord(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write(currentAccession+"\t"+eur.source+"\t"+(eur.isUnknown? "unknowns":"known")+"\n");
    }
    public void printFooter(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        
    }
    //</editor-fold>

    
    public void printHeader(Writer out, AffyExpSetRecord ar) throws IOException
    {
        log.debug("printing expSet header");
        out.write("accession\taffy_id\texperiment_set\tname\tdescription\t" +
                "up2x\tdown2x\tup4x\tdown4x\tpma_on\tpma_off\n");
    }
    public void printRecord(Writer out, AffyExpSetRecord ar) throws IOException
    {        
        out.write(currentAccession+"\t"+ar.probeSetKey+"\t"+ar.expSetKey+"\t"+
                ar.name+"\t"+ar.description+"\t"+ar.up2+"\t"+ar.down2+"\t"+
                ar.up4+"\t"+ar.down4+"\t"+ar.on+"\t"+ar.off+"\n");
    }
    public void printFooter(Writer out, AffyExpSetRecord ar) throws IOException
    {
    }

    public void printHeader(Writer out, AffyCompRecord ar) throws IOException
    {
        out.write("accession\tcomparison\tcontrol_mean\ttreatment_mean\t" +
                "control_pma\ttreat_pma\tratio_log2\n");
    }
    public void printRecord(Writer out, AffyCompRecord ar) throws IOException
    {
        out.write(currentAccession+"\t"+ar.comparison+"\t"+ar.controlMean+"\t"+
                ar.treatmentMean+"\t"+ar.controlPMA+"\t"+ar.treatmentPMA+"\t"+
                ar.ratio+"\n");
    }
    public void printFooter(Writer out, AffyCompRecord ar) throws IOException
    {
    }
    
    public void printHeader(Writer out, AffyDetailRecord ar) throws IOException
    {
        out.write("accession\ttype\tcel_file\tintensity\tpma\n");
    }
    public void printRecord(Writer out, AffyDetailRecord ar) throws IOException
    {
        out.write(currentAccession+"\t"+ar.type+"\t"+ar.celFile+"\t"+
                ar.intensity+"\t"+ar.pma+"\n");
    }
    public void printFooter(Writer out, AffyDetailRecord ar) throws IOException
    {
    }
    
    public void printFooter(Writer out, CompositeRecord cr) throws IOException
    {
        Iterator<Record> i=cr.iterator();
        if(i.hasNext())
            i.next().printFooter(out,this);
    }
    public void printHeader(Writer out, CompositeRecord cr) throws IOException
    {        
        Iterator<Record> i=cr.iterator();
        if(i.hasNext())
            i.next().printHeader(out,this);
    }
    public void printRecord(Writer out, CompositeRecord cr) throws IOException
    {
        //cr.getFormat().printRecords(out,this,cr.iterator());
                
        for(Iterator<Record> i=cr.iterator();i.hasNext();)
            i.next().printRecord(out, this);
    }



    public void printHeader(Writer out, ProbeSetRecord psr) throws IOException
    {
    }

    public void printRecord(Writer out, ProbeSetRecord psr) throws IOException
    {
    }
    public void printFooter(Writer out, ProbeSetRecord psr) throws IOException
    {
    }
}
