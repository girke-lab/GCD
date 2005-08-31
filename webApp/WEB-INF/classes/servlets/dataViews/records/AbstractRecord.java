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
import servlets.exceptions.UnsupportedKeyType;

/**
 *
 * @author khoran
 */
public abstract class AbstractRecord implements Record
{
    
    private static Logger log=Logger.getLogger(AbstractRecord.class); 
    private Collection subRecords;
    private int keyType;
    
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

    public java.util.Iterator<Object> iterator()
    {
        return subRecords.iterator();
    }
    public void setKeyType(int keyType) throws UnsupportedKeyType
    {
        if(!Common.checkType(this, keyType))
            throw new UnsupportedKeyType(this.getSupportedKeyTypes(),keyType);
        this.keyType=keyType;
    }
    public int  getKeyType()
    {
        return keyType;
    }
}
