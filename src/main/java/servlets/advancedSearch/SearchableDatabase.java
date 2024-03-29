/*
 * SearchableDatabase.java
 *
 * Created on September 7, 2004, 11:46 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author  khoran
 */

import javax.servlet.http.*;
import javax.servlet.ServletContext;
import servlets.advancedSearch.fields.Field;

/**
 * Defines an interface for classes that can be used by the AdvancedSearchBean.
 */
public interface SearchableDatabase 
{
    
    /**
     * returns an array of Field objects
     * @return array of Field objects.
     */
    public servlets.advancedSearch.fields.Field[] getFields();
    /**
     * returns an array of operators.
     * @return an array of Strings
     */
//    public String[] getOperators();
    /**
     * return array of booleans for combining fields. Usually consists of just
     * 'and' and 'or'.
     * @return array of Strings.
     */
    public String[] getBooleans();    
    
    
    /**
     * Takes the current SearchState and creates the corresponding query, sends if
     * off, and displays the results.
     * @param state current SearchState
     * @param context servlet context, used to forward page to another servlet
     * @param request sent to new page (with new parameters)
     * @param response sent to new page
     */
    public void displayResults(SearchState state, ServletContext context,
            HttpServletRequest request, HttpServletResponse response);
    
    public servlets.advancedSearch.queryTree.Query buildQueryTree(SearchState state);
    /**
     * returns a SearchTreeManager for manages sql queries for this database.
     * @return a SearchTreeManager
     */
    public SearchTreeManager getSearchManager();
}
