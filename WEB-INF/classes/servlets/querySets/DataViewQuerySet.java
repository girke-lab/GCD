/*
 * DataViewQuerySet.java
 *
 * Created on March 25, 2005, 3:14 PM
 */

package servlets.querySets;

import java.util.*;
import servlets.KeyTypeUser;

/**
 * Provides queries for DataView objects. Also provides some 
 * misc data about queries, such as column names.
 * @author khoran
 */



public interface DataViewQuerySet extends QuerySet
{
 
    // for BlastDataView
    public String getBlastDataViewQuery(Collection ids,String sortCol, String sortDir, KeyTypeUser.KeyType keyType);
    public String[] getSortableBlastColumns();
    
    // for ClusterDataView
    public String getClusterDataViewQuery(Collection ids, String order, int[] DBs, KeyTypeUser.KeyType keyType);
    
    // for ModelDataView
    public String getModelDataViewQuery(Collection ids, String fields, KeyTypeUser.KeyType keyType);
    public String[] getModelColumns();
    
    // for SeqDataView
    public String getSeqDataViewQuery(Collection ids, String order, int[] DBs, KeyTypeUser.KeyType keyType);
    
    // for UnknownsDataView
    public String getUnknownsDataViewQuery(Collection ids, String sortCol, String sortDir, KeyTypeUser.KeyType keyType);
    public String[] getSortableUnknownsColumns();        
    
    // for the pfamOptions.jsp page. No, this is not a DataView but I'm
    // putting here anyway. 
    public String getPfamOptionsQuery(String clusterName);
    
    // for DIffTrackingDataVIew
    public String getDiffTrackingDataViewQuery();
    public String getDiffStatsQuery();
    
    // for AffyDataView
    public static final int EXPSET=0, COMP=1, DETAIL=2;
    public String[][] getSortableAffyColumns();
    
    // for CorrelationsDataView
    public String[] getSortableCorrelationColumns();
    public String getClusterMethodsQuery();
    
    // for TreatmentDataView
    public static final int TREAT_COMP=0, TREAT_PSK=1;
    public String[][] getSortableTreatmentColoumns();
    
    public String getCompCountDataViewQuery(String userName);
    
}
