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
    List getResults();    //results should always be Seq_id numbers.
    
    //return a list of keys that were not found in the database
    //return an empyt list if everything was found, or if this
    //operation does not make sense.
    List notFound();
    
}
