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

public interface SearchableDatabase 
{
    
    public Field[] getFields();
    public String[] getOperators();
    public String[] getBooleans();    
    
//    public String buildQuery(SearchState state);
//    public String getDestination(); 
//    public java.util.List sendQuery(String query);
    
    public void displayResults(SearchState state, ServletContext context,
            HttpServletRequest request, HttpServletResponse response);
    public SearchStateManager getSearchManager();
}
