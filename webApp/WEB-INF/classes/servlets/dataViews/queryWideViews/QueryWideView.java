/*
 * QueryWideView.java
 *
 * Created on September 22, 2004, 8:50 AM
 */

package servlets.dataViews.queryWideViews;

/**
 *
 * @author  khoran
 */

import java.io.*;
import java.util.Map;
import servlets.search.Search;

/**
 * This interface is used by objects that
 * wish to print information about an entire
 * query result, rather than just the page
 * of results given by a {@link DataView} object.
 */
public interface QueryWideView 
{
    
    /**
     * This method should print an html box displaying the
     * number of records found by the entire query, and any
     * other statistics about the whole query.
     * @param out Used to print the html to.
     * @param search {@link Search} object used to get list of id numbers and stats from.
     */    
    public void printStats(PrintWriter out,Search search);
    /**
     * Prints a form with various buttons that send the given
     * range of id numbers to a {@link script} object.
     * @param out output
     * @param hid history id
     * @param pos current position in id list
     * @param size total number of ids
     * @param rpp number or records to print per page
     */    
    public void printButtons(PrintWriter out, int hid,int pos,int size,int rpp);
    /**
     * Determins if the {@link DataView} this object is
     * associated with should be given all of the id numbers,
     * or just a page full.
     * Currently used only by ModelDataView to allow retrieval
     * of all data in fasta format.
     * @return true if all ids should be given to dataView,
     * false otherwise.
     */    
    public boolean printAllData();
    
    
    public void printGeneral(PrintWriter out, Search search, String position);
    public void printGeneral(PrintWriter out, Search search, String position,Map storage);
}
