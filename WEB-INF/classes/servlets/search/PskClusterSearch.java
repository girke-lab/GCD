/*
 * PskClusterSearch.java
 *
 * Created on April 21, 2006, 10:33 AM
 *
 */

package servlets.search;

import java.util.*;
import servlets.Common;
import servlets.querySets.QuerySetProvider;

/**
 * This search takes a cluster_id first and then a comparison_id.  It returns
 * a list of probe_set_key_ids that are in the given cluster.  For each
 * probe_set_key_id, the comparison_id is appended with an underscore.  This is
 * the format that the ProbeSetDataView wants.
 * @author khoran
 */
public class PskClusterSearch extends AbstractSearch
{
    
    /** Creates a new instance of PskClusterSearch */
    public PskClusterSearch()
    {
    }

    void loadData()
    {
            
        int cluster_id;
        int comparison_id;
        
        if(input==null || input.size()!=2)
        {
            log.error("bad input list: "+input);
            data=new ArrayList(0);
            return;
        }
        
        cluster_id=new Integer((String)input.get(0));
        comparison_id=new Integer((String)input.get(1));

        
        seqId_query=QuerySetProvider.getSearchQuerySet().getPskClusterSearchQuery(cluster_id, keyType);
        List rs=Common.sendQuery(seqId_query);
                               
        String lastLabel=null;
        int c=0;
        List output=new ArrayList(rs.size());
        List row;
        
        for(Iterator i=rs.iterator();i.hasNext();c++)
        {
            row=(List)i.next();
            if(!row.get(1).equals(lastLabel)){
                lastLabel=(String)row.get(1);
                addBookmark(lastLabel,c);
            }
            output.add(row.get(0)+"_"+comparison_id);
        }
            
        data=output;
    }
    
    /**
     * The search returns probe_set_key_ids with the comparison_id tacked on.
     * @return  
     */
    public int[] getSupportedKeyTypes()
    { 
        return new int[]{Common.KEY_TYPE_PSK};
    }
}
