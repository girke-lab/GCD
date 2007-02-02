/*
 * AbstractRecord.java
 *
 * Created on August 30, 2005, 2:57 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.exceptions.InvalidPatternException;
import servlets.exceptions.UnsupportedKeyTypeException;

/**
 * This class provides a partial implementation of the Record interface.
 * It manages child records and which keyType has been set.
 * It also provides implementations of equals and hashCode so that 
 * sub-classes work properly in all Collections.
 * @author khoran
 */
public abstract class AbstractRecord implements Record 
{
    
    private static Logger log=Logger.getLogger(AbstractRecord.class); 
    private KeyType keyType=null;
    private Record parent=null;
    
    private RecordPattern pattern=null;
    
    /** Creates a new instance of AbstractRecord */
   
    public Record getParent()
    {
        return parent;
    }
    public void setParent(Record parent)
    {
        this.parent=parent;
    }
    

    /**
     *  Sets the type of key that this record should produce.
     *  When this record is added as a child of a parent record, 
     *  the parent will specify which type of key it requires (with the getPrimaryKeyType method).
     *  If this record supports this key, as returned by the 
     *  getSupportedKeyTypes method, then these records can
     *  be linked.
     * @param keyType value of keyType to use
     * @throws servlets.exceptions.UnsupportedKeyTypeException if the given keyType is not supported by this record
     */
    public void setKeyType(KeyType  keyType) throws UnsupportedKeyTypeException
    {
        if(!Common.checkType(this, keyType))
            throw new UnsupportedKeyTypeException(this.getSupportedKeyTypes(),keyType);
        else
            this.keyType=keyType;
    }
    public KeyType getKeyType()
    {
        if(keyType==null)
            log.error("key type not set for record "+this.getClass());
        return keyType;
    }
    
    protected boolean checkList(String name,int reqSize,List values)
    {                
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in "+name+" constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return false;
        }
        return true;
    }
    
    /** compares records based on the getPrimaryKey method
     */
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof Record) || o==null)
            return false;
        //check that they are the same class and have the same primary key
        return this.getClass().equals(o.getClass()) &&
                ((Record)o).getPrimaryKey().equals(this.getPrimaryKey());
    }
    public int hashCode()
    {
        return getPrimaryKey().hashCode();
    }
    public String toString()
    {
        return this.getClass().getName();
    }

    public void setPattern(RecordPattern pattern) throws InvalidPatternException
    {
        if(!this.getClass().equals(pattern.getRoot()))
            throw new InvalidPatternException("pattern root does not match class type",pattern);
        
        this.pattern=pattern;
    }

    public RecordPattern getPattern()
    {
        return pattern;
    }
}
