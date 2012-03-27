/*
 * ClusterCorrSearch.java
 *
 * Created on May 30, 2006, 1:03 PM
 *
 */

package servlets.search;
import java.util.*;
import servlets.Common;
import servlets.KeyTypeUser.KeyType;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ClusterCorrSearch extends AbstractSearch
{
    
    /** Creates a new instance of ClusterCorrSearch */
    public ClusterCorrSearch()
    {
    }

    /**
     *  set data to null so we don't store large key sets
     *  in the session.
     */
    @Override
    public void compress()
    {
        data=null;
    }

    void loadData()
    {
        
        int cluster_id;
        int psk_id;
        if(input==null || input.size()!=2)
        {
            log.error("bad input for ClusterCorrSearch, usage: <cluster_id> <psk_id>");
            data=new ArrayList(0);
            return;
        }
        cluster_id=Integer.parseInt((String)input.get(0));
        psk_id=Integer.parseInt((String)input.get(1));
        
        seqId_query=QuerySetProvider.getSearchQuerySet().getClusterCorrSearchQuery(cluster_id,psk_id, keyType);
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
            output.add(row.get(0));
        }
            
        data=output;
        
    }
    public KeyType[] getSupportedKeyTypes()
    { 
        return new KeyType[]{KeyType.CORR};
    }
}
