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
    void setData(java.util.List ids,String sortCol,int limit,int[] dbList,int hid);
    void printData(java.io.PrintWriter out);
}
