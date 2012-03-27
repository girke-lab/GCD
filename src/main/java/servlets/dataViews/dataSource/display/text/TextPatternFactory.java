/*
 * TextPatternFactory.java
 *
 * Created on January 12, 2007, 9:48 AM
 *
 */

package servlets.dataViews.dataSource.display.text;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.PatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.display.html.Utilities;
import servlets.dataViews.dataSource.records.*;
import servlets.dataViews.dataSource.structure.Record;

/**
 * Creates a set of default formats for printing records in
 * plain text format.  The getAllPatterns method can be used
 * to get all the formats at once.  
 *
 * @author khoran
 */
public class TextPatternFactory
{
    private static final Logger log=Logger.getLogger(TextPatternFactory.class);
    private static  TextPatternFactory singleton=null;
    
    private List<PatternFormat<? extends Record>> patterns=null;
    
    /** Creates a new instance of TextPatternFactory */
    public TextPatternFactory()
    {
        patterns=new LinkedList<PatternFormat<? extends Record>>(); 
        createPatterns();
    }
    
    
    private static TextPatternFactory getInstance()
    {
        if(singleton == null)
            singleton=new TextPatternFactory();
        return singleton;
    }
           
    public static Collection<PatternFormat<? extends Record>> getAllPatterns()
    {
        return getInstance().patterns;  
    }
    public static PatternFormat getPattern(Class c)
    {
        try{
            return (PatternFormat)c.getDeclaredConstructor(getInstance().getClass()).newInstance(getInstance());
        }catch(Exception e){
            log.error("could not create class "+c.getName(),e);
            return null;
        }
    }    
    private void createPatterns()
    { //load the patterns list                        
        try{
            for(Class c : this.getClass().getDeclaredClasses())                
                patterns.add((PatternFormat)c.getDeclaredConstructor(this.getClass()).newInstance(this));
        }catch(Exception e){
            log.error("could not create all patterns",e);            
        }        
    }
    
    class UnknownRecordFormat extends AbstractPatternFormat<UnknownRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(UnknownRecord.class);
        }
        public void printHeader(UnknownRecord r) throws IOException
        {
            keys(out,"accession");
            datas(out,r,"description");
        }
        public void printRecord(UnknownRecord r) throws IOException
        {            
            newline(out, r);
            keys(out,r.key);
            datas(out,r,r.description);            
        }
        public void printFooter(UnknownRecord r) throws IOException
        {
        }        
    }           
    class GoRecordFormat extends AbstractPatternFormat<GoRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(GoRecord.class);
        }
        public void printHeader(GoRecord r) throws IOException
        {
            keys(out,"go_number");
            datas(out,r,"text","function");            
        }
        public void printRecord(GoRecord r) throws IOException
        {
            newline(out, r);
            keys(out,r.go_number );
            datas(out,r,r.function,r.text );            
        }
        public void printFooter(GoRecord r) throws IOException
        {
        }        
    }
    class BlastRecordFormat extends AbstractPatternFormat<BlastRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(BlastRecord.class);
        }
        public void printHeader(BlastRecord r) throws IOException
        {
            //out.write("target\tscore\tevalue\tdbname\t");
            keys(out, "target");
            datas(out,r,"score","evalue","dbname" );            
        }
        public void printRecord(BlastRecord r) throws IOException
        {
            newline(out, r);
            //out.write(r.target+"\t"+r.score+"\t"+r.evalue+"\t"+r.dbname+"\t");
            
            keys(out, r.target);
            datas(out,r, r.score,r.evalue,r.dbname);
        }
        public void printFooter(BlastRecord r) throws IOException
        {
        }
    }        
    class ProteomicsRecordFormat extends AbstractPatternFormat<ProteomicsRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ProteomicsRecord.class);
        }
        
        public void printHeader(ProteomicsRecord r) throws IOException
        {
            out.write("molecular weight\tip\tcharge\tprobability\tprobability_is_negative\t");              
        }        
        public void printRecord(ProteomicsRecord r) throws IOException
        {
            newline(out, r);
            out.write(r.mol_weight+"\t"+r.ip+"\t"+r.charge+"\t"+r.prob+"\t"+r.prob_is_neg+"\t");
        }        
        public void printFooter(ProteomicsRecord r) throws IOException
        {
        }
    }
    class ClusterRecordFormat extends AbstractPatternFormat<ClusterRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ClusterRecord.class);
        }
        public void printHeader(ClusterRecord r) throws IOException
        {
            //out.write("size\tmethod\t");
            keys(out,"cluster key");
            datas(out,r,"size","method");
        }
        public void printRecord(ClusterRecord r) throws IOException
        {
            newline(out, r); 
            keys(out,r.key);
            datas(out,r,r.size,r.method);
            //out.write(r.size+"\t"+r.method+"\t");
        }
        public void printFooter(ClusterRecord r) throws IOException
        {
        }
    }
    class ExternalUnknownRecordFormat extends AbstractPatternFormat<ExternalUnknownRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ExternalUnknownRecord.class);
        }
        public void printHeader(ExternalUnknownRecord r) throws IOException
        {
            out.write("source\tis_unknown\t");
        }
        public void printRecord(ExternalUnknownRecord r) throws IOException
        {
            newline(out, r);
            out.write(r.source+"\t"+(r.isUnknown? "unknowns":"known")+"\t");
        }
        public void printFooter(ExternalUnknownRecord r) throws IOException
        {
        }
    }
    class AffyExpSetRecordFormat extends AbstractPatternFormat<AffyExpSetRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( AffyExpSetRecord.class);
        }
        public void printHeader(AffyExpSetRecord r) throws IOException
        {            
            //out.write("affy_id\texperiment_set\tname\tdescription\t" +
            //    "up2x\tdown2x\tup4x\tdown4x\tpma_on\tpma_off\t");
            keys(out,"affy_id","experiment_set");
            datas(out,r,"name","description","up2x\tdown2x\tup4x\tdown4x\tpma_on\tpma_off");
        }
        public void printRecord(AffyExpSetRecord r) throws IOException
        {
            newline(out, r);
//            out.write(r.probeSetKey+"\t"+r.expSetKey+"\t"+
//                r.name+"\t"+r.description+"\t"+r.up2+"\t"+r.down2+"\t"+
//                r.up4+"\t"+r.down4+"\t"+(r.on==null?"":r.on)+"\t"+(r.off==null?"":r.off)+"\t");
            keys(out,r.probeSetKey,r.expSetKey);
            datas(out,r,r.name,r.description,r.up2,r.down2,r.up4,r.down4, (r.on==null?"":r.on), (r.off==null?"":r.off));
        }
        public void printFooter(AffyExpSetRecord r) throws IOException
        {
        }
    }
    class AffyCompRecordFormat extends AbstractPatternFormat<AffyCompRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( AffyCompRecord.class);
        }
        public void printHeader(AffyCompRecord r) throws IOException
        {
//            out.write("experiment_set\tprobe_set_key\tcomparison\tcontrol_mean\ttreatment_mean\t" +
//                "control_pma\ttreat_pma\tratio_log2\tcontrast\tP_value" +
//                "\tadj_p_value\tpfp_up\tpfp_down\tcontrol_desc\ttreatment_desc\t");
            keys(out,"experiment_set","probe_set_key","comparison");
            datas(out,r,"control_mean\ttreatment_mean\t" +
                "control_pma\ttreat_pma\tratio_log2\tcontrast\tP_value" +
                "\tadj_p_value\tpfp_up\tpfp_down\tcontrol_desc\ttreatment_desc");
        }
        public void printRecord(AffyCompRecord r) throws IOException
        {
            newline(out, r);
//            out.write(r.expSetKey+"\t"+r.probeSetKey+"\t"+
//                r.comparison+"\t"+r.controlMean+"\t"+r.treatmentMean+"\t"+
//                r.controlPMA+"\t"+r.treatmentPMA+"\t"+
//                r.ratio+"\t"+r.contrast+"\t"+r.pValue+"\t"+r.adjPValue+"\t"+
//                r.pfpUp+"\t"+r.pfpDown+"\t"+
//                r.controlDesc+"\t"+r.treatDesc+"\t");
            keys(out,r.expSetKey,r.probeSetKey,r.comparison);
            datas(out,r,  r.controlMean+"\t"+r.treatmentMean+"\t"+
                Utilities.bn(r.controlPMA)+"\t"+Utilities.bn(r.treatmentPMA)+"\t"+
                r.ratio+"\t"+r.contrast+"\t"+r.pValue+"\t"+r.adjPValue+"\t"+
                r.pfpUp+"\t"+r.pfpDown+"\t"+
                r.controlDesc+"\t"+r.treatDesc );
        }
        public void printFooter(AffyCompRecord r) throws IOException
        {
        }
    }
    class AffyDetailRecordFormat extends AbstractPatternFormat<AffyDetailRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( AffyDetailRecord.class);
        }
        public void printHeader(AffyDetailRecord r) throws IOException
        {
            //out.write("type\tcel_file\tintensity\tpma\t"); 
            keys(out,"type","cel_file");
            datas(out,r,"intensity","pma");
        }
        public void printRecord(AffyDetailRecord r) throws IOException
        {
            newline(out, r);
//            out.write(r.type+"\t"+r.celFile+"\t"+
//                r.intensity+"\t"+r.pma+"\t");
            keys(out,r.type,r.celFile);
            datas(out,r,r.intensity,Utilities.bn(r.pma));
        }
        public void printFooter(AffyDetailRecord r) throws IOException
        {
        }
    }
    class ProbeSetSummaryRecordFormat extends AbstractPatternFormat<ProbeSetSummaryRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ProbeSetSummaryRecord.class);
        }
        public void printHeader(ProbeSetSummaryRecord r) throws IOException
        {
            
        }
        public void printRecord(ProbeSetSummaryRecord r) throws IOException
        {
            newline(out, r);
        }
        public void printFooter(ProbeSetSummaryRecord r) throws IOException
        {
        }
    }
    class CorrelationRecordFormat extends AbstractPatternFormat<CorrelationRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( CorrelationRecord.class);
        }
        public void printHeader(CorrelationRecord r) throws IOException
        {
            //out.write("catagory\taffyID1\taffyID2\tpearson_correlation\tspearman_correlation\taccessions\tdescriptions\t");
            keys(out,"catagory","affyID1","affyID2");
            datas(out,r,"pearson_correlation","spearman_correlation");
        }
        public void printRecord(CorrelationRecord r) throws IOException
        {
            newline(out, r);
//            out.write(r.catagory+"\t"+r.psk1_key+"\t"+r.psk2_key+"\t"+
//                r.pearson+"\t"+r.spearman+"\t");
            keys(out,r.catagory,r.psk1_key,r.psk2_key);
            datas(out,r,r.pearson,r.spearman);
        }
        public void printFooter(CorrelationRecord r) throws IOException
        {
        }
    }
    class AffyExpDefRecordFormat extends AbstractPatternFormat<AffyExpDefRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( AffyExpDefRecord.class);
        }
        public void printHeader(AffyExpDefRecord r) throws IOException
        {
            out.write("Experiment name\tCel file name\tType\tGroup number\t");
        }
        public void printRecord(AffyExpDefRecord r) throws IOException
        {
            newline(out, r);
            out.write(r.expName+"\t"+r.celFileName+"\t"+r.expType+"\t"+r.groupNo+"\t");
        }
        public void printFooter(AffyExpDefRecord r) throws IOException
        {
        }
    }
    class ComparisonRecordFormat extends AbstractPatternFormat<ComparisonRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ComparisonRecord.class);
        }
        public void printHeader(ComparisonRecord r) throws IOException
        {
//            out.write("Experiment Set\tComparision\tData Source\tControl Description\t" +
//                "Treatment Description\tExperiment Set Description\t");
            keys(out,"Experiment Set","Comparison");
            datas(out,r,"Data Source\tControl Description\t" +
                "Treatment Description\tExperiment Set Description");
        }
        public void printRecord(ComparisonRecord r) throws IOException
        {
            newline(out, r);
//            out.write(r.expSetKey+"\t"+r.comparison+"\t"+r.sourceName+"\t"+
//                    r.controlDesc+"\t"+r.treatmentDesc+"\t"+r.expDesc+"\t");
            keys(out,r.expSetKey,r.comparison);
            datas(out,r,r.sourceName,r.controlDesc,r.treatmentDesc,r.expDesc);
        }
        public void printFooter(ComparisonRecord r) throws IOException
        {
        }
    }
    class ProbeSetKeyRecordFormat extends AbstractPatternFormat<ProbeSetKeyRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ProbeSetKeyRecord.class);
        }
        public void printHeader(ProbeSetKeyRecord r) throws IOException
        {                           
//            out.write("Affy Id\tControl Mean\tTreatment Mean\tControl PMA\ttreatement PMA" +
//                "Ratio\tContrast\tp-value\tAdjusted p-value\tPFP up\t PFP down\tClusters\t");
            keys(out,"Affy Id");
            datas(out,r,"Control Mean\tTreatment Mean\tControl PMA\ttreatement PMA" +
                "Ratio\tContrast\tp-value\tAdjusted p-value\tPFP up\t PFP down");
        }
        public void printRecord(ProbeSetKeyRecord r) throws IOException
        {
            newline(out, r);
            keys(out,r.probeSetKey);
            datas(out,r, r.controlMean,r.treatmentMean, 
                Utilities.bn(r.controlPMA), Utilities.bn(r.treatmentPMA), r.ratio,
                r.contrast, r.pValue, r.adjPValue, 
                r.pfpUp, r.pfpDown);
                        
//            Object[] values=new Object[]{
//                r.probeSetKey,
//                r.controlMean,r.treatmentMean, 
//                Utilities.cn(r.controlPMA), Utilities.cn(r.treatmentPMA), r.ratio,
//                r.contrast, r.pValue, r.adjPValue, 
//                r.pfpUp, r.pfpDown
//            };
//            StringBuilder sb=new StringBuilder();
//            for(Object o : values)
//                sb.append(o+"\t");
//            out.write(sb.toString());
        }
        public void printFooter(ProbeSetKeyRecord r) throws IOException
        {
        }
    }
    class ProbeClusterRecordFormat extends AbstractPatternFormat<ProbeClusterRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( ProbeClusterRecord.class);
        }
        public void printHeader(ProbeClusterRecord r) throws IOException
        {
            keys(out,"name");
            datas(out,r,"method","size","confidence");
        }
        public void printRecord(ProbeClusterRecord r) throws IOException
        {
            newline(out, r);
            keys(out,r.name);
            datas(out,r,r.method,r.size,r.confidence==null?"":r.confidence);
        }
        public void printFooter(ProbeClusterRecord r) throws IOException
        {
        }
    }
    class SequenceRecordFormat extends AbstractPatternFormat<SequenceRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern( SequenceRecord.class);
        }
        public void printHeader(SequenceRecord r) throws IOException
        {
            keys(out,"accession");
            datas(out,r,"description");
        }
        public void printRecord(SequenceRecord r) throws IOException
        {
            newline(out,r);
            keys(out,r.key);
            datas(out,r,r.description);
        }
        public void printFooter(SequenceRecord r) throws IOException
        {
        }
    }
    ///////////////////////////////////////////////////////
    private void newline(Writer out, Record r) throws IOException
    {
        if(r.getParent()==null)
            out.write("\n");
    }
    private void keys(Writer out, Object... keys) throws IOException
    {
        for(Object o : keys)
            out.write(o+"\t");
    }
    private void datas(Writer out, Record r,Object... data ) throws IOException
    {
        if(r.getPattern().isLeaf())
            for(Object o : data)
                out.write(o+"\t");
    }
}
