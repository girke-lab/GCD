/*
 * RecordBuilder.java
 *
 * Created on January 21, 2005, 2:16 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author khoran
 */
public abstract class RecordBuilder 
{
    abstract Record buildRecord(java.util.List l);
    
    RecordGroup buildRecordGroup()
    {
        return new RecordGroup();
    }
}
