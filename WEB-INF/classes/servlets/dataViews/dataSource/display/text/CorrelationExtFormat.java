/*
 * CorrelationExtFormat.java
 *
 * Created on January 16, 2007, 3:57 PM
 *
 */

package servlets.dataViews.dataSource.display.text;

import java.io.IOException;
import java.util.Iterator;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.CorrelationRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.dataSource.structure.Record;

/**
 * 
 * @author khoran
 */
public class CorrelationExtFormat extends AbstractPatternFormat<CorrelationRecord>
{
    private static final RecordPattern pattern=buildPattern();
    
    /**
     * Creates a new instance of CorrelationExtFormat
     */
    public CorrelationExtFormat()
    {        
    }
    
    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(CorrelationRecord.class);        
        p.addChild(SequenceRecord.class);
        return p;
    }
    public RecordPattern getPattern()
    {
        return pattern;
    }

    public void printHeader(CorrelationRecord r) throws IOException
    {
        out.write("catagory\taffyID1\taffyID2\tpearson\tspearman\taccessions\tdescriptions\n");
    }

    public void printRecord(CorrelationRecord r) throws IOException
    {
        Object[] values=new Object[]{
            r.catagory,r.psk1_key,r.psk2_key,r.pearson,r.spearman
        };
        for(Object o : values)
            out.write(o+"\t");
        
        SequenceRecord sr;
        Iterator<Record> i;
        
        for(i=r.childGroup(SequenceRecord.class).iterator();i.hasNext();)
        {
            sr=(SequenceRecord)i.next();
            out.write(sr.key);
            if(i.hasNext())
                out.write(",");
        }
        
        out.write("\t");
        
        for(i=r.childGroup(SequenceRecord.class).iterator();i.hasNext();)
        {
            sr=(SequenceRecord)i.next();
            out.write(sr.description);
            if(i.hasNext())
                out.write(",");
        }
        out.write("\n");
    }

    public void printFooter(CorrelationRecord r) throws IOException
    {
    }
    
}
