/*
 * PatternedRecordPrinter.java
 *
 * Created on December 29, 2006, 10:44 AM
 *
 */

package servlets.dataViews.dataSource.display;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.structure.Record;

/**
 * This class prints the record tree. It uses a collection of 
 * PatternFormats to cover the tree and then uses these formats to 
 * print out each record.  Formats can be added with the addFormat method.
 * If several formats match at a certain record, the format with the 
 * larger pattern will be used.  If the two formats have the same size, 
 * the format which was inserted first will be used.  
 *
 *  There are three main print methods; printRecord, printGroup, and 
 * printTabular. The printRecord method will print a header and footer
 * for each record given.  The printGroup method will print just one header
 * and footer for the group of records given. The printTabular method 
 * tries to print the tree out in a single table, like a spreadsheet.  So it
 * will print the header of every type of record in the entire tree first, then
 * print the records, repeating parent records for each child record. It does
 * not print any footer.
 *
 *
 * @author khoran
 */
public class PatternedRecordPrinter 
{
    private static final Logger log=Logger.getLogger(PatternedRecordPrinter.class);        
    
    Map<Class,Collection<PatternFormat<? extends Record>>> formats;    
    
    Stack<RecordStackEl> recordStack=new Stack<RecordStackEl>();
    DisplayParameters parameters;
    
    /**
     * Creates a new instance of PatternedRecordPrinter
     */
    public PatternedRecordPrinter(DisplayParameters parameters)
    {        
        this.parameters=parameters;
        formats=new HashMap<Class,Collection<PatternFormat<? extends Record>>>();
    }
    
   
    /** prints a single record, includeing all sub-records
     */
    public void printRecord(Record r) throws IOException
    {
        PatternFormat format = getFormat(r);        
        if(format==null) {
            log.error("no format found for record "+r.getClass().getName());
            return;
        }
        
        format.printHeader(r);
        format.printRecord(r);
        
        //print remaining records        
        for(List<Record> group : format.getPattern().remainingRecordGroups(r))
            printGroup(group);                        
        
        format.printFooter(r);
    }         
    /** 
     * prints several records, with a header and footer for each record.
     */
    public void printRecord(Collection<Record> records) throws IOException
    {
        //dumpInfo();
        for(Record r : records)
            printRecord(r);
    }
    /** Prints a group of records. A header is printed once, then all 
     * the records are printed, then a footer is printed. This method
     * assumes that all records in the input are of the same type, if they
     * are not, you will get ClassCastExceptions.
     */
    public void printGroup(Iterable<Record> records) throws IOException
    {
        boolean isFirst=true;
        Record r;
        PatternFormat groupFormat=null;
        
        for(Iterator<Record> i=records.iterator();i.hasNext();)
        {
            r=i.next();
            if(isFirst){
                isFirst=false;
                groupFormat=getFormat(r);
                if(groupFormat == null)
                    break;
                groupFormat.preProcess(records);
                groupFormat.printHeader(r);
            }
            groupFormat.printRecord(r);
            
            for(List<Record> group : groupFormat.getPattern().remainingRecordGroups(r))
                    printGroup(group);

            if(!i.hasNext()) //last record            
                groupFormat.printFooter(r);            
        }
    }    
    
    /** prints records in tabular format. The header for all records is printed
     * first, then the records. Parent records are repeated for each child record.
     * This method really only works properly when each record has only one
     * type of child record.
     */
    public void printTabular(Collection<Record> records) throws IOException
    {
        printTabular(records,true);
    }
    /** This method allows you to specify whether or not a header is printed. This 
     * is needed since sometimes records are printed in batches, and only the first
     * batch needs a header.
     */
    public void printTabular(Collection<Record> records,boolean printHeader) throws IOException
    {
        if(printHeader)
            printAllHeaders(records);
        
        recordStack.clear();
        printAllRecords(records);
    }
       
    private void printAllHeaders(Iterable<Record> records) throws IOException
    {        
        PatternFormat groupFormat;
        Record r;
        Iterator<Record> i=records.iterator();
        if(!i.hasNext())
            return;

        r=i.next();
        groupFormat=getFormat(r.getPattern());                      
        
        groupFormat.printHeader(r);
        
        for(List<Record> group : groupFormat.getPattern().remainingRecordGroups(r))
                    printAllHeaders(group);
        
    }
    
    private void printAllRecords(Iterable<Record> records) throws IOException
    {
        PatternFormat groupFormat=null;
        List<List<Record>> remaining;
        
        //log.info("stack depth: "+recordStack.size());
        
        for(Record r : records)
        {
            //log.info("processing record "+r+", primary key: "+r.getPrimaryKey());
            if(groupFormat == null)
            {
                groupFormat=getFormat(r);
                if(groupFormat==null) //no format found
                    break;
            }
                                    
            remaining=groupFormat.getPattern().remainingRecordGroups(r);
            //log.info("number remaining: "+remaining.size());
            if(!hasRemaining(remaining))//  remaining.isEmpty()) //this is a leaf
            { //print the entire stack
                //log.info("\tthis is a leaf record");
                for(RecordStackEl rse : recordStack)
                    rse.printRecord();
                groupFormat.printRecord(r);
            }
            else
            {
                //log.info("\tpushing this record and going deeper");
                recordStack.push(new RecordStackEl(groupFormat,r));
                for(List<Record> group : remaining)
                    printAllRecords(group);    
                recordStack.pop();
                //log.info("back to "+r+", with primary key: "+r.getPrimaryKey());
            }
        }
    }
    private boolean hasRemaining(List<List<Record>> remaining)
    {
        for(List<Record> l : remaining)
            if(!l.isEmpty()) // there is at least one list which is not empty
                return true;
        return false;
    }
    
    /** get for format that can print this record
     */
    private PatternFormat getFormat(Record r)
    {                        
        return getFormat(r.getPattern());
    }
    /** get a format which matches this pattern.
     * More complex patterns are chosen over simpler patterns. If 
     * two or more patterns match and have the same complexity, the 
     * first pattern added is used.
     */
    private PatternFormat getFormat(RecordPattern pattern)
    {                   
        Class clazz=pattern.getRoot();
        Collection<PatternFormat<? extends Record>> possibleFormats=formats.get(clazz);
        
        if(possibleFormats == null)
        {
            log.warn("no possible formats found for "+pattern);
            return null;
        }
        
        log.debug("possible formats: "+possibleFormats);        
                        
        PatternFormat bestFormat=null;
        for(PatternFormat pf : possibleFormats)
            if(pf.getPattern().matches(pattern) && 
                    (bestFormat==null || bestFormat.getPattern().compareTo(pf.getPattern()) < 0))
                bestFormat=pf;
            else
                log.debug(clazz.getName()+" did not match with "+pf.getPattern());
                
        if(bestFormat != null)
            log.debug("found format "+bestFormat.getClass().getName()+" for record "+clazz.getName());
        else
            log.warn("not match found for pattern "+pattern+" possibilities: "+possibleFormats);
        return bestFormat;        
    }
    
    /** remove all formats.
     */
    public void clear()
    {
        formats.clear();
    }
    /** Adds a new format to the list of possible formats.
     */
    public void addFormat(PatternFormat<? extends Record> pf)
    {
        log.debug("adding pattern "+pf.getPattern());
        Collection<PatternFormat<? extends Record>> list=formats.get(pf.getPattern().getRoot());
        if(list == null) //first format with this root
        {
            list=new LinkedList<PatternFormat<? extends Record>>();
            formats.put(pf.getPattern().getRoot(),list);
        }
        
        pf.setParameters(parameters);
        list.add(pf);
                        
    }
    /** adds a collection of formats to the set of possible formats
     */
    public void addFormat(Collection<PatternFormat<? extends Record>> pfs)
    {
        for(PatternFormat pf : pfs)
            addFormat(pf);
    }
    /** remove any PatternFormat objects witch match the given class. 
     * This really should not be used right now.
     */
    public void removeFormat(Class c)
    {                                
        for(Collection<PatternFormat<? extends Record>> list : formats.values())
            for(Iterator<PatternFormat<? extends Record>> i=list.iterator();i.hasNext();)        
                if(i.next().getClass().equals(c))
                    i.remove();                
    }
    
    
    private void dumpInfo()
    {
        log.debug("available patterns: ");
        for(Map.Entry<Class,Collection<PatternFormat<? extends Record>>> entry :  formats.entrySet())
            for(PatternFormat pf : entry.getValue())
                log.debug(entry.getKey().getName()+": "+pf.getPattern());
                
    }
    
    class RecordStackEl
    {
        public Record record;
        public PatternFormat format;
        public RecordStackEl(PatternFormat format,Record record)
        {
            this.format=format;
            this.record=record;
        }
        public void printRecord() throws IOException
        {
            format.printRecord(record);
        }
    }
    
}
