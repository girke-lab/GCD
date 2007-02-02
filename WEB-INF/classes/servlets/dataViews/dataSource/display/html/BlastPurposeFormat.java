/*
 * BlastPurposeFormat.java
 *
 * Created on January 6, 2007, 11:30 AM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.display.PatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.BlastRecord;

/**
 *
 * @author khoran
 */
public class BlastPurposeFormat extends AbstractPatternFormat<BlastRecord>
{
    private static final Logger log=Logger.getLogger(BlastPurposeFormat.class);
    
    Map<String,String> titles;
    String lastPurpose=null;
    
    PatternFormat<BlastRecord> defaultFormat;
    
    /** Creates a new instance of BlastPurposeFormat */
    public BlastPurposeFormat()
    {
        defaultFormat=HtmlPatternFactory.getPattern(HtmlPatternFactory.BlastRecordFormat.class);
        
        
        titles=new HashMap<String,String>(); 
        titles.put("UD","Unknown Searches");
        titles.put("orthologs","Ortholog Searches");
    }

    public RecordPattern getPattern()
    {
        return new RecordPattern(BlastRecord.class);
    }

    public void printHeader(BlastRecord r) throws IOException
    {     
        lastPurpose=null;
        defaultFormat.printHeader(r);
    }
    public void printRecord(BlastRecord r) throws IOException
    {     
        if(r.target.equals("no hit"))
            return;
        
        if(lastPurpose==null || !lastPurpose.equals(r.purpose))
        {
            out.write("<tr><th align='left' colspan='4' bgcolor='"+PageColors.title+"'>"+
                    titles.get(r.purpose)+"</th></tr>");
            lastPurpose=r.purpose;
        }
        
        defaultFormat.printRecord(r);
    }
    public void printFooter(BlastRecord r) throws IOException
    {        
        defaultFormat.printFooter(r);
    }

    public void setParameters(DisplayParameters parameters)
    {
        super.setParameters(parameters);
        defaultFormat.setParameters(parameters);
    }
    
}
