/*
 * Search.java
 *
 * Created on March 3, 2004, 12:42 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.List;

public interface Search 
{
    //initialize the search object with the data
    void init(List data,int limit,int dbID);
    
    //perform the query and return the results
    List getResults();    
    
}
