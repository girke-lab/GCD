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
    static Logger log=Logger.getLogger(TextRecordVisitor.class);
    String currentAccession="";
    boolean printDescription;
    LinkedList<String> dataStack=new LinkedList<String>();
    LinkedList<String> headerStack=new LinkedList<String>();
    
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
                ar.up4+"\t"+ar.down4+"\t"+(ar.on==null?"":ar.on)+"\t"+(ar.off==null?"":ar.off)+"\n");
    }
    public void printFooter(Writer out, AffyExpSetRecord ar) throws IOException
    {
    }

    public void printHeader(Writer out, AffyCompRecord ar) throws IOException
    {
        out.write("accession\texperiment_set\tprobe_set_key\tcomparison\tcontrol_mean\ttreatment_mean\t" +
                "control_pma\ttreat_pma\tratio_log2\tcontrast\tP_value" +
                "\tadj_p_value\tpfp_up\tpfp_down\tcontrol_desc\ttreatment_desc\n");
    }
    public void printRecord(Writer out, AffyCompRecord ar) throws IOException
    {
        out.write(currentAccession+"\t"+ar.expSetKey+"\t"+ar.probeSetKey+"\t"+
                ar.comparison+"\t"+ar.controlMean+"\t"+ar.treatmentMean+"\t"+
                ar.controlPMA+"\t"+ar.treatmentPMA+"\t"+
                ar.ratio+"\t"+ar.contrast+"\t"+ar.pValue+"\t"+ar.adjPValue+"\t"+
                ar.pfpUp+"\t"+ar.pfpDown+"\t"+
                ar.controlDesc+"\t"+ar.treatDesc+"\n");
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
        log.debug("composite: printing headers");
        Record r;
        if(i.hasNext())
        {
            r=i.next();
            log.debug("printing header for "+r.getClass().getName());
            //i.next().printHeader(out,this);
            r.printHeader(out,this);
        }
    }
    public void printRecord(Writer out, CompositeRecord cr) throws IOException
    {
        //cr.getFormat().printRecords(out,this,cr.iterator());
        
        for(Iterator<Record> i=cr.iterator();i.hasNext();)
            i.next().printRecord(out, this);
    }



    public void printHeader(Writer out, ProbeSetSummaryRecord psr) throws IOException
    {
    }

    public void printRecord(Writer out, ProbeSetSummaryRecord psr) throws IOException
    {
    }
    public void printFooter(Writer out, ProbeSetSummaryRecord psr) throws IOException
    {
    }

    
    public void printHeader(Writer out, CorrelationRecord cr) throws IOException
    {
        out.write("catagory\taffyID1\taffyID2\tcorrelation\tp_value\taccessions\tdescriptions\n");
    }
    public void printRecord(Writer out, CorrelationRecord cr) throws IOException
    {
        out.write(cr.catagory+"\t"+cr.psk1_key+"\t"+cr.psk2_key+"\t"+
                cr.correlation+"\t"+cr.p_value+"\t");
        for(int i=0;i<cr.accessions.length;i++)
        {
            if(i!=0)
                out.write(",");
            out.write(cr.accessions[i]);
        }
        out.write("\t");
        for(int i=0;i<cr.descriptions.length;i++)
        {
            if(i!=0)
                out.write(",");
            out.write(cr.descriptions[i]);
        }
        out.write("\n");
    }
    public void printFooter(Writer out, CorrelationRecord cr) throws IOException
    {
    }

    

    public void printHeader(Writer out, AffyExpDefRecord ar) throws IOException
    {
        out.write("Experiment name\tCel file name\tType\tGroup number\n");
    }
    public void printRecord(Writer out, AffyExpDefRecord ar) throws IOException
    {
        out.write(ar.expName+"\t"+ar.celFileName+"\t"+ar.expType+"\t"+ar.groupNo+"\n");
    }
    public void printFooter(Writer out, AffyExpDefRecord ar) throws IOException
    {
    }

    
    public void printHeader(Writer out, ComparisonPskRecord cpr) throws IOException
    {
    }

    public void printRecord(Writer out, ComparisonPskRecord cpr) throws IOException
    {
    }

    public void printFooter(Writer out, ComparisonPskRecord cpr) throws IOException
    {
    }

    
    
    public void printHeader(Writer out, ComparisonRecord cr) throws IOException
    {
        headerStack.addLast("Experiment Set\tComparision\tData Source\tControl Description\t" +
                "Treatment Description\tExperiment Set Description");
        if(cr==null || !cr.iterator().hasNext()) //no children
            printStack(out,headerStack);
        else //must explicitly list children here.
            printHeader(out,(ProbeSetKeyRecord)null);
    }

    public void printRecord(Writer out, ComparisonRecord cr) throws IOException
    {
        String data=cr.expSetKey+"\t"+cr.comparison+"\t"+cr.sourceName+"\t"+
                    cr.controlDesc+"\t"+cr.treatmentDesc+"\t"+cr.expDesc;
        
        pushData(out,data,cr);
        
    }

    public void printFooter(Writer out, ComparisonRecord cr) throws IOException
    {
    }

    public void printHeader(Writer out, ProbeSetKeyRecord pskr) throws IOException
    {
        headerStack.addLast("Affy Id\tControl Mean\tTreatment Mean\tControl PMA\ttreatement PMA" +
                "Ratio\tContrast\tp-value\tAdjusted p-value\tPFP up\t PFP down\tClusters");
        if(pskr==null || !pskr.iterator().hasNext()) //no children
            printStack(out,headerStack);
        
    } 

    public void printRecord(Writer out, ProbeSetKeyRecord pskr) throws IOException
    {
        String clusters="";
        for(int i=0;i<pskr.clusterNames.length;i++)
            clusters+=pskr.clusterNames[i]+"("+pskr.sizes[i]+") ";
        
        Object[] values=new Object[]{
            pskr.probeSetKey,
            pskr.controlMean,pskr.treatmentMean, 
            pskr.controlPMA, pskr.treatmentPMA, pskr.ratio,
            pskr.contrast, pskr.pValue, pskr.adjPValue, 
            pskr.pfpUp, pskr.pfpDown
        };
        StringBuilder sb=new StringBuilder();
        for(Object o : values)
            sb.append(o+"\t");
        sb.append(clusters);
        
        pushData(out,sb.toString(),pskr);
    }

    public void printFooter(Writer out, ProbeSetKeyRecord pskr) throws IOException
    {
    }
    
    //////////////////////// Utils /////////////////////////////////////////////
    void printStack(Writer out,LinkedList<String> stack) throws IOException
    {
        String s;
        for(Iterator<String> i=stack.iterator();i.hasNext();)
        {
            out.write(i.next());
            if(i.hasNext())
                out.write("\t");
        }
        out.write("\n");
    }
    void pushData(Writer out,String data, Record r) throws IOException
    {
        dataStack.addLast(data);
        if(!r.iterator().hasNext())
            printStack(out,dataStack);
        else  
            for(Object o : r)
                ((Record)o).printRecord(out,this);
        dataStack.removeLast();
    }
}
