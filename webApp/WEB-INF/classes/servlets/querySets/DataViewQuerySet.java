/*
 * DataViewQuerySet.java
 *
 * Created on March 25, 2005, 3:14 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;

public interface DataViewQuerySet extends QuerySet
{
 
    // for BlastDataView
    public String getBlastDataViewQuery(Collection ids,String sortCol, String sortDir, int keyType);
    public String[] getSortableBlastColumns();
    
    // for ClusterDataView
    public String getClusterDataViewQuery(Collection ids, String order, int[] DBs, int keyType);
    
    // for ModelDataView
    public String getModelDataViewQuery(Collection ids, String fields, int keyType);
    public String[] getModelColumns();
    
    // for SeqDataView
    public String getSeqDataViewQuery(Collection ids, String order, int[] DBs, int keyType);
    
    // for UnknownsDataView
    public String getUnknownsDataViewQuery(Collection ids, String sortCol, String sortDir, int keyType);
    public String[] getSortableUnknownsColumns();        
}
