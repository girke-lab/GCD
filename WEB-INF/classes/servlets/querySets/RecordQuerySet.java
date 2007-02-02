/*
 * RecordQuerySet.java
 *
 * Created on March 29, 2005, 8:17 AM
 */

package servlets.querySets;

import java.util.*;
import servlets.KeyTypeUser.KeyType;

/**
 * This is a QuerySet that provides queries for Record objects.
 * It is used by classes in the servlets.dataViews.records package.
 * 
 * @author khoran
 */



public interface RecordQuerySet extends QuerySet
{
    // for BlastRecord
    public String getBlastRecordQuery(Collection ids, String sortCol, String sortDir);
    
    // for ClusterRecord
    public String getClusterRecordQuery(Collection ids, String sortCol, String sortDir);
    
    // for ExternalUnknownsRecord
    public String getExternlUnknwownsRecordQuery(Collection ids, String sortCol, String sortDir);
    
    // for GoRecord
    public String getGoRecordQuery(Collection ids, String sortCol, String sortDir);
    
    // for ProteomicsRecord
    public String getProteomicsRecordQuery(Collection ids, String sortCol, String sortDir);
 
    // for UnknownRecord
    public String getUnknownRecordQuery(Collection ids, String sortCol, String sortDir,KeyType keyType);
    
    // for SequenceRecord
    public String getSequenceRecordQuery(Collection ids, String sortCol, String sortDir,KeyType keyType);
    
    // for Affy*Record
    public String getAffyDetailRecordQuery(Collection affyKeys, String dataType, boolean allGroups, String sortcol, String sortDir);
    public String getAffyCompRecordQuery(Collection affyKeys, String dataType, String sortcol, String sortDir,String userName);
    public String getAffyExpSetRecordQuery(Collection ids, String dataType, String sortcol, String sortDir, String userName);
    
    // for ProbeSetRecord
    public String getProbeSetSummaryRecordQuery(Collection ids);
    
    // for CorrelationRecord
    public String getCorrelationRecordQuery(Collection ids, String sortcol, String sortDir, String catagory);
    
    // for AffyExpDefRecord
    public String getAffyExpDefRecordQuery(Collection ids);
    
    // for ComparisonPskRecord
    public String getComparisonPskRecordQuery(Collection pskIds,Collection comparisonIds,String sortCol, String sortDir, String userName,String dataType);
    
    // for ComparisonRecord
    public String getComparisonRecordQuery(Collection comparisonIds,String sortCol, String sortDir, String userName,String dataType);
    
    // for ProbeSetKeyRecord
    public String getProbeSetKeyRecordQuery(Collection pskIds,Collection compIds,String sortCol, String sortDir, String dataType);
    
    // for ProbeClusterRecord
    public String getProbeClusterRecordQuery(Collection ids, String sortCol, String sortDir,KeyType keyType);
}
