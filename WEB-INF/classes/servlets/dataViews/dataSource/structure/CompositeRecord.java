/*
 * CompositeRecord.java
 *
 * Created on August 30, 2005, 2:55 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.display.CompositeFormat;
import servlets.dataViews.dataSource.display.RecordVisitor;

/**
 *
 * @author khoran
 */
@Deprecated 
public class CompositeRecord extends LeafRecord
{        
    /** this key should be shared by all sub records of this record */
    private Object key;
    private Class recordType=null;
    
    private CompositeFormat format;
    
    private static final Logger log=Logger.getLogger(CompositeRecord.class);
    
    /** Creates a new instance of CompositeRecord */
    public CompositeRecord(Object key, Class recordType, CompositeFormat format)
    {
        this.key=key;
        this.format=format;
        this.recordType=recordType;
    }

    public void addChildRecord(Record r)
    {
        if(recordType != r.getClass())
        {
            log.error("trying to add record of type "+r.getClass()+
                    " to CompositeRecord of type "+recordType+
                    ". This record was dropped");
            return;
        }
        
        super.addChildRecord(r);
    }
    public boolean isOfType(Class c)
    {
        return recordType == c;
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
    public KeyType getPrimaryKeyType()
    {
        Iterator i=this.iterator();
        if(i.hasNext())
            return ((Record)i.next()).getPrimaryKeyType();
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
