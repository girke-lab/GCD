/*
 * SeqIdSearch.java
 *
 * Created on August 6, 2004, 12:53 PM
 */

package servlets.search;

/**
 *
 * @author  khoran
 */

import java.util.List;
import java.util.ArrayList;
import servlets.search.AbstractSearch;

public class SeqIdSearch extends AbstractSearch //implements Search, java.io.Serializable
{    
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
    
    public int getDbStartPos(int i) {
        return 0;
    }
      
    void loadData() {       
    }
    
}
