/*
 * QueryInfo.java
 *
 * Created on March 18, 2003, 11:18 AM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;
import java.io.Serializable;

public class QueryInfo implements Serializable
{
    public int dbsLength;
    public int[] dbNums;
    public int limit;
    private List keys;
    /** Creates a new instance of QueryInfo */
    public QueryInfo(int[] dbNums,int dbsLength,int limit)
    {
        this.dbNums=dbNums;
        this.dbsLength=dbsLength;
        this.limit=limit;
        this.keys=new ArrayList();
    }
    public void addKeySet(List l)
    {
        keys.add(l);
    }
    public List getKeySet(int index)
    {
        return (ArrayList)keys.get(index);
    }    
}
