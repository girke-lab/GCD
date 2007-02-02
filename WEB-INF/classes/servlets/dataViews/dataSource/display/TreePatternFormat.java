/*
 * TreePatternFormat.java
 *
 * Created on December 29, 2006, 3:57 PM
 *
 */

package servlets.dataViews.dataSource.display;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
@Deprecated
public class TreePatternFormat implements PatternFormat<Record>
{
    private static final Logger log=Logger.getLogger(TreePatternFormat.class);
    
    PatternFormat rootFormat;
    List<PatternFormat> childFormats;
    DisplayParameters parameters;
    Writer out;
    
    /** Creates a new instance of TreePatternFormat */
    public TreePatternFormat(PatternFormat rootFormat)
    {
        this.rootFormat=rootFormat;
        childFormats=new LinkedList<PatternFormat>();
        
        out=rootFormat.getParameters().getWriter();
    }

    public RecordPattern getPattern()
    {
        RecordPattern pattern=rootFormat.getPattern();
        
        for(PatternFormat pf : childFormats)
            pattern.addChild(pf.getPattern());
        
        return pattern;
    }

    public void addChildFormat(PatternFormat pf)
    {
        if(pf!=null)
            childFormats.add(pf);
    }
    
    public void printHeader(Record record) throws IOException
    {
        rootFormat.printHeader(record);
    }

    public void printRecord(Record record) throws IOException
    {
        rootFormat.printRecord(record);
        
        Collection<Class> subGroups=record.getGroupList();
       
        PatternFormat groupFormat=null;
        boolean isFirst;
        Record r;
        
        for(Class c : subGroups)    
        {            
            log.debug("printing group "+c.getName());
            isFirst=true;
            for(Iterator<Record> i=record.childGroup(c).iterator();i.hasNext();)
            {
                r=i.next();
                if(isFirst){
                    isFirst=false;
                    groupFormat=findPattern(r);
                    if(groupFormat == null)
                        break;
                    groupFormat.printHeader(r);
                }
                groupFormat.printRecord(r);
                if(!i.hasNext()) //last record
                    groupFormat.printFooter(r);
            }
        }
        
    }
        
    public void printFooter(Record record) throws IOException
    {
        rootFormat.printFooter(record);
    }
    
    private PatternFormat findPattern(Record r)
    {
        for(PatternFormat pf : childFormats)
            if(pf.getPattern().matches(r))
                return pf;
        return null;
    }
        
    public void setParameters(DisplayParameters parameters)
    {
        this.parameters=parameters;
    }

    public DisplayParameters getParameters()
    {
        return parameters;
    }

    public void preProcess(Iterable<Record> records)
    {
    }
    
}
