/*
 * SeqIdSearch.java
 *
 * Created on August 6, 2004, 12:53 PM
 */

package servlets.search;
import java.util.ArrayList;
import servlets.Common;

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
        this.keyType=keyType;
    }
    
    public java.util.List notFound() {
        return new ArrayList();
    }
    
    public int getDbStartPos(int i) {
        return 0;
    }
      
    public void loadData() {       
    }
    public int[] getSupportedKeyTypes()
    { //fix this
        return new int[]{Common.KEY_TYPE_ACC,Common.KEY_TYPE_SEQ,
                Common.KEY_TYPE_MODEL,Common.KEY_TYPE_CLUSTER};
    }

 
}
