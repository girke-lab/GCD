/*
 * TextRecordVisitorFactory.java
 *
 * Created on November 22, 2005, 12:01 PM
 *
 */

package servlets.dataViews.records;


import java.io.IOException;
import java.io.Writer;
import org.apache.log4j.Logger;

/**
 *
 * @author khoran
 */
public class TextRecordVisitorFactory
{
    
    private static final Logger log=Logger.getLogger(TextRecordVisitorFactory.class);
    
    private static TextRecordVisitorFactory instance;
    
    public enum VisitorType {GENERAL,AFFY_COMP, AFFY_DETAIL};
    
    /** Creates a new instance of TextRecordVisitorFactory */
    private TextRecordVisitorFactory()
    {
    }
    public static TextRecordVisitorFactory getInstance()
    {
        if(instance==null)
            instance=new TextRecordVisitorFactory();
        return instance;
    }
    public RecordVisitor buildVisitor(VisitorType vt)
    {
        switch(vt){
            case GENERAL:
                return new TextRecordVisitor();
            case AFFY_COMP:
                return new AffyCompVisitor();
            case AFFY_DETAIL:
                return new AffyDetailVisitor();
        }
        return null;
    }
    
    private class AffyCompVisitor extends TextRecordVisitor
    {
        public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
        {
            printHeader(out,(AffyExpSetRecord)null);
            printHeader(out,(AffyCompRecord)null);
        }                

        public void printHeader(Writer out, AffyExpSetRecord ar) throws IOException
        {

            out.write("accession\taffy_id\texperiment_set\t");
        }
        public void printRecord(Writer out, AffyExpSetRecord ar) throws IOException
        {        
            out.write(currentAccession+"\t"+ar.probeSetKey+"\t"+ar.expSetKey+"\t"); 
            
        }


        public void printHeader(Writer out, AffyCompRecord ar) throws IOException
        {
            out.write("comparison\tcontrol_mean\ttreatment_mean\t" +
                    "control_pma\ttreat_pma\tratio_log2\tcontrast\tP_value" +
                    "\tadj_p_value\tpfp_up\tpfp_down\n");
        }
        public void printRecord(Writer out, AffyCompRecord ar) throws IOException
        {
            out.write(ar.comparison+"\t"+ar.controlMean+"\t"+ar.treatmentMean+"\t"+
                    ar.controlPMA+"\t"+ar.treatmentPMA+"\t"+
                    ar.ratio+"\t"+ar.contrast+"\t"+ar.pValue+"\t"+ar.adjPValue+"\t"+
                    ar.pfpUp+"\t"+ar.pfpDown+"\n");
        }
    }
    
    private class AffyDetailVisitor extends TextRecordVisitor
    {
        
    }
}
