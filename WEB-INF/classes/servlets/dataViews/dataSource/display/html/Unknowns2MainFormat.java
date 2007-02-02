/*
 * Unknowns2MainFormat.java
 *
 * Created on January 24, 2007, 1:14 PM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import org.apache.log4j.Logger;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.display.PatternFormat;
import servlets.dataViews.dataSource.display.PatternedRecordPrinter;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.display.html.HtmlPatternFactory.UnknownRecordFormat;
import servlets.dataViews.dataSource.records.BlastRecord;
import servlets.dataViews.dataSource.records.ClusterRecord;
import servlets.dataViews.dataSource.records.ExternalUnknownRecord;
import servlets.dataViews.dataSource.records.GoRecord;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.ProbeSetSummaryRecord;
import servlets.dataViews.dataSource.records.ProteomicsRecord;
import servlets.dataViews.dataSource.records.UnknownRecord;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
public class Unknowns2MainFormat extends AbstractPatternFormat<UnknownRecord>        
{    
    private static final Logger log=Logger.getLogger(Unknowns2MainFormat.class);
       
    private static final Class[] sequenceRecords=new Class[]{GoRecord.class, BlastRecord.class,
                                                ProteomicsRecord.class, ExternalUnknownRecord.class,
                                                ClusterRecord.class};
    private static final Class[] expressionRecords = new Class[] {ProbeSetSummaryRecord.class,
                                                    ProbeClusterRecord.class };
    
    private static final RecordPattern pattern=buildPattern();
 
    
    private PatternedRecordPrinter prp;
    private PatternFormat<Record> rootFormat;
    /** Creates a new instance of Unknowns2MainFormat */
    public Unknowns2MainFormat()
    {                
            rootFormat=HtmlPatternFactory.getPattern(UnknownRecordFormat.class);            
    }

    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(UnknownRecord.class);
        for(Class c : sequenceRecords)
            p.addChild(c);
        for(Class c : expressionRecords)
            p.addChild(c);
                
        return p;
    }
    public RecordPattern getPattern()
    {
        return pattern;
    }

    public void printHeader(UnknownRecord r) throws IOException
    {
        rootFormat.printHeader(r);
    }

    public void printRecord(UnknownRecord r) throws IOException
    {
        rootFormat.printRecord(r);        
        printSubTitle("Sequence Data");
        // print all sequence records   
        
        printSubRecords(r,sequenceRecords);
        
        //print all expression records        
        printSubTitle("Expression Data");
        
        printSubRecords(r, expressionRecords);
    }

    public void printFooter(UnknownRecord r) throws IOException
    {
        rootFormat.printFooter(r);
    }

    public void setParameters(DisplayParameters parameters)
    {
        super.setParameters(parameters);
        
        prp=new PatternedRecordPrinter(parameters);      
        prp.addFormat(new BlastPurposeFormat());
        prp.addFormat(HtmlPatternFactory.getAllPatterns());
        
        rootFormat.setParameters(parameters);
    }
    
    private void printSubRecords(UnknownRecord r, Class[] groups) throws IOException
    {
        
        for(Class c : groups)
        {            
            prp.printGroup(r.childGroup(c));
        }
    }
    private void printSubTitle(String title) throws IOException 
    {
        out.write("<table width='100%' border='0' bgcolor='"+PageColors.title+"' ><tr><td align='center'>");
        out.write("<h3>"+title+"</h3>");
        out.write("</td></tr></table>");
    }
}
