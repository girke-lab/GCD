/*
 * SeqIdSearch.java
 *
 * Created on August 6, 2004, 12:53 PM
 */

package servlets;

/**
 *
 * @author  khoran
 */

import java.util.List;
import java.util.ArrayList;

public class SeqIdSearch implements Search
{
    List data;
    /** Creates a new instance of SeqIdSearch */
    public SeqIdSearch() {
    }
    
    public java.util.List getResults() {
        return data;
    }
    
    public void init(java.util.List data, int limit, int[] dbID) {
        this.data=data;
    }
    
    public java.util.List notFound() {
        return new ArrayList();
    }
    
}
