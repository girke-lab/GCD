/*
 * CompositeRecord.java
 *
 * Created on August 30, 2005, 2:55 PM
 *
 */

package servlets.dataViews.records;

import java.util.*;

/**
 *
 * @author khoran
 */
public class CompositeRecord extends AbstractRecord
{
    /** this key should be shared by all sub records of this record */
    String key;
    
    /** Creates a new instance of CompositeRecord */
    public CompositeRecord(String key)
    {
        this.key=key;
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

    public int[] getSupportedKeyTypes()
    {
        Iterator i=this.iterator();
        if(i.hasNext())
            return ((Record)i.next()).getSupportedKeyTypes();
        else
            return new int[]{};
    }

    public Object getPrimaryKey()
    {
        return key;
    }
    
}
