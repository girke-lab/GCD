/*
 * AbstractSearch.java
 *
 * Created on August 20, 2004, 10:58 AM
 */

package servlets.search;

/**
 *
 * @author  khoran
 */

import servlets.search.Search;
import servlets.Common;
import java.util.*;

public abstract class AbstractSearch implements Search, java.io.Serializable
{
    List input,keysFound,data=null,stats=null;
    int limit;
    int[] db;
    int[] dbStartPositions;  //index of firs occurance of each database in dataset
    
    public int getDbStartPos(int i) {
         if(i < 0 || i > dbStartPositions.length)
            return 0;
        return dbStartPositions[i];
    }
    
    public java.util.List getResults() {
        if(data==null)
            loadData();
        return data;
    }
    
    public void init(java.util.List data, int limit, int[] dbID) {
        this.input=data;
        this.limit=limit;
        this.db=dbID;
        dbStartPositions=new int[Common.dbCount];
        keysFound=new ArrayList();
        stats=new ArrayList();
    }
    
    public java.util.List notFound() {
        return keysFound;
    }
    
    //this method should load the data list.
    abstract void loadData();
    
    public List getStats() {
        if(stats==null)
            loadData();
        return stats;
    }
    
}
