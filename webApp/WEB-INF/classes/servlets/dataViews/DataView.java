/*
 * DataView.java
 *
 * Created on August 11, 2004, 8:48 AM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */
public interface DataView 
{
    void setData(java.util.List ids,String sortCol,int[] dbList,int hid);
    
    /** 
     *shoul first print the number of keys and models found, then
     *the data.
     */
    void printData(java.io.PrintWriter out);
    void printHeader(java.io.PrintWriter out);
    void printStats(java.io.PrintWriter out);
} 
