/*
 * RecordQuerySet.java
 *
 * Created on March 29, 2005, 8:17 AM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;

public interface RecordQuerySet extends QuerySet
{
    // for BlastRecord
    public String getBlastRecordQuery(Collection ids, String sortCol, String sortDir);
    
    // for ClusterRecord
    public String getClusterRecordQuery(Collection ids, String sortcol, String sortDir);
    
    // for ExternalUnknownsRecord
    public String getExternlUnknwownsRecordQuery(Collection ids, String sortcol, String sortDir);
    
    // for GoRecord
    public String getGoRecordQuery(Collection ids, String sortcol, String sortDir);
    
    // for ProteomicsRecord
    public String getProteomicsRecordQuery(Collection ids, String sortcol, String sortDir);
 
    // for UnknownRecord
    public String getUnknownRecordQuery(Collection ids, String sortcol, String sortDir);
}
