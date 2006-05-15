/*
 * AbstractRecord.java
 *
 * Created on August 30, 2005, 2:57 PM
 *
 */

package servlets.dataViews.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
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
    private Collection subRecords;
    private int keyType=-1;
    
    /** Creates a new instance of AbstractRecord */
    public AbstractRecord()
    {
        subRecords=new LinkedList();
    }
    public AbstractRecord(Collection c)
    {
        subRecords=c;
    }
    
    
    public void addSubRecord(Record r)
    {
        subRecords.add(r); 
    }

    public java.util.Iterator<Record> iterator()
    {
        return subRecords.iterator();
    }
    /**
     *  Sets the type of key that this record should produce.
     *  When this record is added as a child of a parent record, 
     *  the parent will specify which type of key it requires (with the getChildKeyType method).
     *  If this record supports this key, as returned by the 
     *  getSupportedKeyTypes method, then these records can
     *  be linked.
     * @param keyType value of keyType to use
     * @throws servlets.exceptions.UnsupportedKeyTypeException if the given keyType is not supported by this record
     */
    public void setKeyType(int keyType) throws UnsupportedKeyTypeException
    {
        if(!Common.checkType(this, keyType))
            throw new UnsupportedKeyTypeException(this.getSupportedKeyTypes(),keyType);
        else
            this.keyType=keyType;
    }
    public int  getKeyType()
    {
        if(keyType==-1)
            log.error("key type not set for record "+this.getClass());
        return keyType;
    }
//    public int getChildKeyType()
//    {
//        return Common.KEY_TYPE_ACC; //default value.
//    }
    
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
}
