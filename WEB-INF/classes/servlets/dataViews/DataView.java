/*
 * DataView.java
 *
 * Created on August 11, 2004, 8:48 AM
 */

package servlets.dataViews;

/**
 * This interface is used to display query results.  It should take a list of keys
 * and perform a query to get back the desired data, format this data, and display
 * it.
 * @author khoran
 */

import java.util.Map;
//import org.apache.naming.java.javaURLContextFactory; 
import servlets.dataViews.queryWideViews.QueryWideView; 

/**
 * The DataView interface is used to print a formatted page of
 * information related to the given id numbers.  It should accept a list
 * of id numbers, perform a query, and display the results
 * in html by printing to the out {@link PrintWriter}.
 * <p>
 * This interface is designed to be given only one pagefull of id
 * numbers at a time.  For this reason, it cannot directly print
 * information related to the results of the whole query.  To do this,
 * use a {@link QueryWideView} object to define how certain details
 * should be printed, and return the object in the getQueryWideView()
 * method.
 */
public interface DataView extends servlets.KeyTypeUser
{
    
    /**
     * Used to set the list of id numbers.  Also sets the field that should
     * be used to sort the results, this should be a fully qualified
     * field name. dbList specifies what databases should be included
     * in the results, usually either arab, or rice.  The hid is used
     * to encode any urls generated by this DataView.
     * @param sortCol Fully qualified field name to sort resutls by
     * @param dbList list of genome ids
     * @param hid history id
     */    
    void setData(String sortCol,int[] dbList,int hid);
    
    /**
     * This method sets the list of id numbers this dataView will operate on.
     * @param ids list of id numbers to retrieve data for.
     */    
    void setIds(java.util.List ids);
    
    void setParameters(Map parameters);
    
    void setStorage(Map storage);
    
    void setUserName(String userName);
    
    /**
     * Should print just the data. This method will be called last.
     * @param out used for output
     */
    void printData(java.io.PrintWriter out);
    /**
     * This will be will be called before any other output is written,
     * so that a custom header can be defined.
     * @param out used for output
     */    
    void printHeader(java.io.PrintWriter out);
    void printFooter(java.io.PrintWriter out); 
    /**
     * This should print a table or something contained at least the
     * number of records actually found, along with any other
     * usefull stats.
     * @param out used for output
     */    
    void printStats(java.io.PrintWriter out);
    
   
    /**
     * Returns the {@link QueryWideView } for this DataView.
     * @return A QueryWideView object for this class.
     */    
    public QueryWideView getQueryWideView();
    
    public void setSortDirection(String dir);    
} 