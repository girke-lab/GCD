/*
 * ClusterDataView.java
 *
 * Created on August 11, 2004, 8:52 AM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;

public class ClusterDataView implements DataView
{
    
    /** Creates a new instance of ClusterDataView */
    public ClusterDataView() {
    }
    
    public void printData(java.io.PrintWriter out) {
    }
    
    public void setData(java.util.List ids, String sortCol, int limit, int[] dbList, int hid) {
    }
    
    
    private List getData(List input, String order, int limit, int[] db)
    {
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        conditions.append("s.seq_id in (");
        for(Iterator it=input.iterator();it.hasNext() && count++ < limit;)
        {
            conditions.append((String)it.next());
            if(it.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");
        rs=Common.sendQuery(buildClusterViewStatement(conditions.toString(),order,limit,db));
        return rs;
    }
    
    private String buildClusterViewStatement(String conditions,String order,int limit, int[] DBs)
    {
        StringBuffer query=new StringBuffer();
        
        query.append("SELECT s.genome, s.primary_key,c.model_id, g.go, ci.filename "+
                "FROM clusters as c, cluster_info as ci, sequences as s LEFT JOIN go as g USING (seq_id) "+
                "WHERE ci.cluster_id=c.cluster_id AND c.seq_id=s.seq_id ");
        
                
        query.append(" AND (");
        for(int i=0;i<DBs.length;i++)
        {
            query.append("s.genome='"+Common.dbRealNames[DBs[i]]+"' ");
            if(i+1 < DBs.length)
                query.append(" or ");
        }
        
        query.append(") AND ( "+conditions+" ) ");
        query.append("ORDER BY s.genome,s.primary_key,c.model_id, g.go,ci.filename ");
        query.append("LIMIT "+limit);
        System.out.println("cluster view query: "+query);
        return query.toString();
    }
}
