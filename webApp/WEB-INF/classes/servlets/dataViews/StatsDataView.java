/*
 * StatsDataView.java
 *
 * Created on August 26, 2004, 2:38 PM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import servlets.DbConnection;
import org.apache.log4j.Logger;

public class StatsDataView implements DataView
{
    private static Logger log=Logger.getLogger(StatsDataView.class);
    
    /** Creates a new instance of StatsDataView */
    public StatsDataView() {
    }
    
    public void printData(java.io.PrintWriter out) {
        //out.println(DbConnection.getStats());
    }
    
    public void printHeader(java.io.PrintWriter out) {
    }
    
    public void printStats(java.io.PrintWriter out) {
    }
    
    public void setData(String sortCol, int[] dbList, int hid) {
    }
    
    public void setIds(java.util.List ids) {
    }
    
    public boolean hasFeature(int f) {
        return false;
    }
    
}
