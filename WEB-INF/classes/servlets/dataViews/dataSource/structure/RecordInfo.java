/*
 * RecordInfo.java
 *
 * Created on August 31, 2005, 3:32 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.List;
import servlets.KeyTypeUser;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.CompositeFormat;

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
    public RecordInfo(int key, int start, int end)
    {
        this.key=new int[]{key};
        this.start=start;
        this.end=end;
    }
    public RecordInfo(int start,int end)
    {
        this.start=start;
        this.end=end;
        key=null;
    }
    
    public abstract String getQuery(QueryParameters qp,KeyTypeUser.KeyType keyType);
    public abstract Record createRecord(List l);
    public abstract Class  getRecordType();
    
    public KeyTypeUser.KeyType[] getSupportedKeyTypes()
    {
        return new KeyTypeUser.KeyType[]{};
    }
    
    public int getStart()
    {
        return start;
    }

    public int getEnd()
    {
        return end;
    }

    public int[] getKeyIndecies(KeyTypeUser.KeyType keyType)
    {
        return key;
    }    
    
    public CompositeFormat getCompositeFormat()
    {
        return new CompositeFormat();
    }
        
    /**
     *  This key is used to identify this record in a Map.
     *  It is used only when another record is adding this
     *  record as a subrecord.
     *  The primary key of the parent record will be compared
     *  to this key to see if this record should be a child of
     *  that parent. 
     *
     *  So if several of these records return the same 
     *  key here, they will all become children of the same
     *  parent record.
     *
     *  The default method of building a key is this:
     *      If there is only one item in the list, it is
     *      returned unchanged.
     *
     *      If there are multiple items, they are cast to
     *      Strings and run together with '_' as a separator.
     *
     *  This format must match the format returned by the
     *  getPrimaryKey method of the parent node.
     */
    public Object buildKey(List data,KeyTypeUser.KeyType keyType)
    {
        int[] indecies=getKeyIndecies(keyType);
        
        if(indecies==null || indecies.length==0)
            return "";
        else if(indecies.length==1)
        {
            try{ //try to return an int, if that fails return a String
                return new Integer((String)data.get(indecies[0]));
            }catch(NumberFormatException e){
                return (String)data.get(indecies[0]);
            }            
        }
        
        
        StringBuffer key=new StringBuffer();
        for(int i=0;i<indecies.length;i++)
        {
            key.append(data.get(indecies[i]));
            if(i+1 < indecies.length) //we have at least one more iteration
                key.append("_");
        }        
        return key.toString();
    }
}
