/*
 * ComparisonExtFormat.java
 *
 * Created on January 17, 2007, 12:03 PM
 *
 */

package servlets.dataViews.dataSource.display.text;

import java.io.IOException;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.display.html.Utilities;
import servlets.dataViews.dataSource.records.ComparisonRecord;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.ProbeSetKeyRecord;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
public class ComparisonExtFormat extends AbstractPatternFormat<ComparisonRecord>
{
    private static final RecordPattern pattern=buildPattern();
    
    /** Creates a new instance of ComparisonExtFormat */
    public ComparisonExtFormat()
    {
    }

    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(ComparisonRecord.class);
        RecordPattern p2=new RecordPattern(ProbeSetKeyRecord.class);
        
        p2.addChild(ProbeClusterRecord.class);        
        p.addChild(p2);
        return p;
    }
    public RecordPattern getPattern()
    {
        return pattern;
    }
    public void printHeader(ComparisonRecord r) throws IOException
    {
        out.write("Experiment Set\tComparision\tData Source\tControl Description\t" +
                "Treatment Description\tExperiment Set Description\t");
        out.write("Affy Id\tControl Mean\tTreatment Mean\tControl PMA\ttreatement PMA\t" +
                "Ratio\tContrast\tp-value\tAdjusted p-value\tPFP up\t PFP down\tClusters"); 
        out.write("\n");
    }

    public void printRecord(ComparisonRecord r) throws IOException
    {
        Object[] comparisonData=new Object[]{
            r.expSetKey,r.comparison,r.sourceName,r.controlDesc,r.treatmentDesc,
            r.expDesc
        };
        Object[] probeSetData,clusterData;
        
        ProbeSetKeyRecord pskr;
        ProbeClusterRecord pcr;
        for(Record t1 : r.childGroup(ProbeSetKeyRecord.class))
        {
            pskr=(ProbeSetKeyRecord)t1;
            probeSetData=new Object[]{
                pskr.probeSetKey,pskr.controlMean,pskr.treatmentMean,bn(pskr.controlPMA),
                bn(pskr.treatmentPMA),pskr.ratio,pskr.contrast,pskr.pValue,pskr.adjPValue,
                pskr.pfpUp,pskr.pfpDown
            };
            printArray(comparisonData);
            printArray(probeSetData);
            
            for(Record t2 : pskr.childGroup(ProbeClusterRecord.class))
            {
                pcr=(ProbeClusterRecord)t2;                
                out.write(pcr.name+" "+(pcr.confidence==null?"":pcr.confidence)+
                            " ("+pcr.size+") ");
            }
            out.write("\n");
        }        
    }

    public void printFooter(ComparisonRecord r) throws IOException
    {
    }
    
    private void printArray(Object[] data) throws IOException
    {
        for(Object o : data)
            out.write(o+"\t");
    }

    
    private String bn(Object o)
    {
        return o==null?"":o.toString();
    }
}
