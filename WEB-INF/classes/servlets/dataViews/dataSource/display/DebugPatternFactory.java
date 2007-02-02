/*
 * DebugPatternFactory.java
 *
 * Created on December 29, 2006, 12:57 PM
 *
 */

package servlets.dataViews.dataSource.display;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import servlets.dataViews.dataSource.records.*;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
public class DebugPatternFactory
{
    static Class[] classes=new Class[]{
                AffyCompRecord.class,
                AffyDetailRecord.class,
                AffyExpDefRecord.class,
                AffyExpSetRecord.class,
                BlastRecord.class,
                ClusterRecord.class,
                ComparisonPskRecord.class,
                ComparisonRecord.class,
                CorrelationRecord.class,
                ExternalUnknownRecord.class,
                GoRecord.class,
                ProbeSetKeyRecord.class,
                ProbeSetSummaryRecord.class,
                ProteomicsRecord.class,
                UnknownRecord.class,
                ProbeClusterRecord.class
            };
    
    /** Creates a new instance of DebugPatternFactory */
    public DebugPatternFactory()
    {
    }
    
    public static Collection<PatternFormat<? extends Record>> getAllPatterns()
    {
        List<PatternFormat<? extends Record>> patterns=new LinkedList<PatternFormat<? extends Record>>();
        
        for(Class c : classes)
            patterns.add(new DebugPatternFormat(c));
                
        return patterns;
    }
    public static PatternFormat<? extends Record> getPattern(Class c)
    {
        return new DebugPatternFormat(c);
    }
    static class DebugPatternFormat extends AbstractPatternFormat<Record>
    {
        Class c;
        public DebugPatternFormat(Class c)
        {
            this.c=c;
        }
        public RecordPattern getPattern()
        {
            return new RecordPattern(c);
        }      

        public void printHeader(Record record) throws IOException
        {
          out.write("header pattern for "+c+"\n");   
        }

        public void printRecord(Record record) throws IOException
        {
             out.write("  record pattern for "+c+"\n");
        }

        public void printFooter(Record record) throws IOException
        {
             out.write("footer pattern for "+c+"\n");
        }
    }    
}
