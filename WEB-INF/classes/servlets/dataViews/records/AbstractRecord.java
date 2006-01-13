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
 *
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
    public void setKeyType(int keyType) throws UnsupportedKeyTypeException
    {
        if(keyType==Common.KEY_TYPE_DEFAULT && this.getSupportedKeyTypes().length!=0)
            this.keyType=this.getSupportedKeyTypes()[0];
        else if(!Common.checkType(this, keyType))
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
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_ACC; //default value.
    }
}
