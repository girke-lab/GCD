/*
 * QueryInfo.java
 *
 * Created on March 18, 2003, 11:18 AM
 */
package servlets;
/**
 *
 * @author  khoran
 */
import java.util.*;
import java.io.Serializable;
import servlets.search.Search;

public class QueryInfo implements Serializable
{
    //////////  deprecated  ///////
//    public int dbsLength;
//    public int[] dbNums;    
//    public int limit;    
//    private List keys;
    ///////////////////////////
    
    private Search s;
    private int[] dbs;
    private String sortCol,displayType;
    private int inputCount; //number of keys
    private int currentPos;
    private Map storage; //object for different classes to store data in, should all be serializable
    private String userName;
    
    /** Creates a new instance of QueryInfo */
//    public QueryInfo(int[] dbNums,int dbsLength,int limit)
//    {
//        this.dbNums=dbNums;
//        this.dbsLength=dbsLength;
//        this.limit=limit;
//        this.keys=new ArrayList();
//        storage=new HashMap();
//    }
    public QueryInfo(int[] dbs,String sc,String dt)
    {
        this.dbs=dbs;
        sortCol=sc;
        displayType=dt;
        storage=new HashMap();
    }
   
    // add/get KeySet is deprecated.
//    public void addKeySet(List l)
//    {
//        keys.add(l);
//    }
//    public List getKeySet(int index)
//    {
//        return (ArrayList)keys.get(index);
//    }    
    
    
    public void setSearch(Search s)    {
        this.s=s;
    }
    public Search getSearch()    {
        return s;
    }
    public void setDbs(int[] d)    {
        dbs=d;
    }
    public int[] getDbs()    {
        return dbs;
    }
    public void setSortCol(String sc)    {
        sortCol=sc;
    }
    public String getSortCol()    {
        return sortCol;
    }
    public void setDisplayType(String dt)    {
        displayType=dt;
    }
    public String getDisplayType()    {
        return displayType;
    }
    public void setInputCount(int c)    {
        inputCount=c;
    }
    public int getInputCount()    {
        return inputCount;
    }
    public void setCurrentPos(int pos)    {
        currentPos=pos;
    }
    public int getCurrentPos()    {
        return currentPos;
    }
    public void setObject(Object name,Object o)
    {
        storage.put(name, o);
    }
    public Object getObject(Object name)
    {
        return storage.get(name);
    }

    public String getUserName()    {
        return userName;
    }
    public void setUserName(String userName)    {
        this.userName = userName;
    }
}
