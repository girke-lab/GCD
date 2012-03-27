/*
 * PatternFormat.java
 *
 * Created on December 29, 2006, 10:05 AM
 *
 */

package servlets.dataViews.dataSource.display;

import java.io.IOException;
import java.io.Writer;
import servlets.dataViews.dataSource.structure.Record;

/**
 * A PatternFormat defines how to print a section of the record
 * tree. This section is defined by a RecordPattern, which can
 * be retrieved with the getPattern method. 
 *  
 *  This class should be paramaterized by the type of the record
 * at the root of the pattern for this format.
 * For example, a format for a Record of type A with child type B
 * should be created like: new PatternFormat<A>();
 *
 * @author khoran
 */
public interface PatternFormat<T extends Record>
{
    /** returns the pattern which defines which
     * section of the record tree this object will print
     */
    RecordPattern getPattern();
    
    /** This method is given all records in the 
     *root group so that it can perform some global 
     *operations, such as finding the longest record or
     * something. 
     *
     *  This method should NOT print anything out.
     */
    void preProcess(Iterable<T> records);
    
    /** prints the header for this group. This
     * method will be called once with the first
     * record of the group. This record is fairly 
     * arbitrary, so no specific assumptions should be made about it.
     * In particular, the child set of this record will not neccasarly 
     * match the child set of any of the other records.
     */
    void printHeader(T r) throws IOException;
    
    /** prints the body of each record. This method
     *will be called for each record of this group. It
     *should print any data associated with the record.     
     */
    void printRecord(T r) throws IOException;
    
    /** prints the footer for this group. This will be called
     * once with the last record of the group.
     */
    void printFooter(T r) throws IOException;
    
    /** set some parameters which can be used by any records
     * while printing them. 
     */
    void setParameters(DisplayParameters parameters);
    DisplayParameters getParameters();
}
