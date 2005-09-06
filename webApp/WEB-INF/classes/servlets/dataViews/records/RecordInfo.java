/*
 * RecordInfo.java
 *
 * Created on August 31, 2005, 3:32 PM
 *
 */

package servlets.dataViews.records;

import java.util.List;
import servlets.dataViews.records.formats.CompositeFormat;

/**
 * holds data that is constant over a group of
 * records of the same type, for example, all BlastRecord objects
 * @author khoran
 */
public abstract class RecordInfo
{

    private int start;
    private int end;
    private int[] key;
    
    /** Creates a new instance of RecordInfo */
    public RecordInfo(int[] key, int start, int end)
    {
        this.key=key;
        this.start=start;
        this.end=end;
    }
    
    public abstract String getQuery(QueryParameters qp);
    public abstract Record getRecord(List l);
    public abstract int[] getSupportedKeyTypes();
    
    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }

    public int[] getKeyIndecies(int keyType)
    {
        return key;
    }    
    
    public CompositeFormat getCompositeFormat()
    {
        return new CompositeFormat();
    }
        
}
