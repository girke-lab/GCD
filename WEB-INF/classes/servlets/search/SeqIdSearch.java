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
    
    public java.util.List getResults() 
    {
        return data;
    }
    
    public void init(java.util.List data, int limit, int[] dbID) 
    {
        this.data=data;
        this.keyType=keyType;
    }
    
    public java.util.List notFound() {
        return new ArrayList();
    }       
      
    public void loadData() {       
    }
    public KeyType[] getSupportedKeyTypes()
    { //fix this
        return new KeyType[]{KeyType.ACC,KeyType.SEQ,
                KeyType.MODEL,KeyType.CLUSTER,KeyType.PSK};
    }

 
}
