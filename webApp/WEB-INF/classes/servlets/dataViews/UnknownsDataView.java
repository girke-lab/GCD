/*
 * UnknownsDataView.java
 *
 * Created on September 8, 2004, 1:04 PM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;

public class UnknownsDataView implements DataView
{
    List seq_ids;
    int hid;
    String sortCol;
    int[] dbNums;
    
    /** Creates a new instance of UnknownsDataView */
    public UnknownsDataView() {
    }
    
    public void printData(java.io.PrintWriter out) 
    {
        List data=getData();
    }
    
    public void printHeader(java.io.PrintWriter out)
    {
        out.println("<h2>Unknowns</h2>");
    }
    
    public void printStats(java.io.PrintWriter out) 
    {
    }
    
    public void setData(java.util.List ids, String sortCol, int[] dbList, int hid) 
    {
        this.seq_ids=ids;
        this.hid=hid;
        this.sortCol=sortCol;
        this.dbNums=dbList;
    }
    
    ////////////////////////////////////////////////////////////////
    
    private List getData()
    {
        for(Iterator i=seq_ids.iterator();i.hasNext();)
        {
            query
        }
    }
    private String buildQuery(String conditions)
    {
        String query="SELECT * FROM unknowns,treats " +
            " WHERE unknonws.unknown_id=treats.unknown_id AND " +
            "("+conditions+") ORDER BY "+sortCol;
        
        return query;

    }
}
