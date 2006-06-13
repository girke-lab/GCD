/*
 * CompositeRecord.java
 *
 * Created on August 30, 2005, 2:55 PM
 *
 */

package servlets.dataViews.records;

import java.util.*;
import servlets.dataViews.records.CompositeFormat;

/**
 *
 * @author khoran
 */
public class CompositeRecord extends AbstractRecord
{
    /** this key should be shared by all sub records of this record */
    private Object key;
    
    private CompositeFormat format;
    
    /** Creates a new instance of CompositeRecord */
    public CompositeRecord(Object key,CompositeFormat format)
    {
        this.key=key;
        this.format=format;
    }

    public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printFooter(out,this); 
    }

    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this); 
    }

    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this); 
    }

    public KeyType[] getSupportedKeyTypes()
    {
        Iterator i=this.iterator();
        if(i.hasNext())
            return ((Record)i.next()).getSupportedKeyTypes();
        else
            return new KeyType[]{};
    }
    public KeyType getChildKeyType()
    {
        Iterator i=this.iterator();
        if(i.hasNext())
            return ((Record)i.next()).getChildKeyType();
        else
            return null;
    }
    public Object getPrimaryKey()
    {
        return key;
    }
    public CompositeFormat getFormat()
    {
        return format;        
    }
}
