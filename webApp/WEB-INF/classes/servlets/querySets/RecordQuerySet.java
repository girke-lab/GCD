/*
 * RecordQuerySet.java
 *
 * Created on March 29, 2005, 8:17 AM
 */

package servlets.querySets;

import java.util.*;

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
    public String getUnknownRecordQuery(Collection ids, String sortCol, String sortDir);
    
    // for Affy*Record
    public String getAffyDetailRecordQuery(Collection psk_ids,Collection es_ids, Collection groups, String sortcol, String sortDir);
    public String getAffyCompRecordQuery(Collection psk_ids,Collection es_ids, String sortcol, String sortDir);
    public String getAffyExpSetRecordQuery(Collection ids, String sortcol, String sortDir);
}
