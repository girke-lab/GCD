/*
 * OrigSearchQuerySet.java
 *
 * Created on March 25, 2005, 3:17 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;

public class OrigSearchQuerySet implements SearchQuerySet
{
    private static Logger log=Logger.getLogger(OrigSearchQuerySet.class);
    
    /** Creates a new instance of OrigSearchQuerySet */
    public OrigSearchQuerySet()
    {
    }

    private void logQuery(String q)
    {
        log.info("query from is: "+q); // use reflection to get calling method here
    }
    public String getBlastSearchQuery(String blastDb, java.util.Collection keys)
    {
        String query=
            "SELECT br.blast_id " +
            "FROM general.blast_results as br, general.accessions as query, " +
            "   general.accessions as target, general.genome_databases as gd " +
            "WHERE gd.db_name='"+blastDb+"' and gd.genome_db_id=target.genome_db_id and " +
            "   query.accession_id=br.query_accession_id AND target.accession_id=br.target_accession_id AND "+
                Common.buildIdListCondition("query.accession",keys,true);
        logQuery(query);
        return query;
    }

    public String getClusterIDSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        
        String q="SELECT distinct  Sequences.Seq_id, Cluster_Info.filename,sequences.genome "+
                 "FROM Sequences, Cluster_Info, Clusters "+
                 "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND Sequences.seq_id=Clusters.seq_id AND (";
        
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondtion("Cluster_Info.filename",input,limit)+")";
        q+=" order by Genome ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getClusterNameSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id,sequences.genome from Cluster_Info, Clusters, Sequences "+
                  "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND "+
                  " Clusters.seq_id=Sequences.seq_id AND (";

        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }

        id+=") AND "+Common.buildDescriptionCondition("cluster_info.name",input);
        id+=" order by genome";
        id+=" limit "+limit;
        logQuery(id);
        
        return id;
    }

    public String getDescriptionSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id, sequences.genome from Sequences "+
                  "WHERE (";
        
        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+Common.buildDescriptionCondition("sequences.description",input)+")";
        id+=" order by Genome ";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getGoSearchQuery(java.util.Collection input, int limit)
    {
        String id="SELECT DISTINCT go.Seq_id,Go.Go,sequences.genome from Go,sequences "+
                  "WHERE sequences.seq_id=go.seq_id AND ";
        id+="("+Common.buildIdListCondition("go.go",input,true,limit)+")";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getGoTextSearchQuery(java.util.Collection input, int limit)
    {
        String query = "SELECT DISTINCT s.Seq_id, s.genome from go AS g, sequences AS s " + 
                "where g.seq_id = s.seq_id AND ";
        
        query += "(" + Common.buildLikeCondtion("g.text",input,true)+ ")";
        query += " limit " + limit;
        logQuery(query);
        return query;
    }
    
    public String getIdSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        String id="SELECT DISTINCT a.Seq_id, a.Accession,s.genome FROM Sequences as s, Id_Associations as a "+
                  "WHERE s.seq_id=a.seq_id AND ("; 

        for(int i=0;i<DBs.length;i++)
        {
            id+=" s.Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        String condition;
        if(input.size()!=0 && ((String)input.iterator().next()).equals("exact") )
        {//if first element is 'exaxt', then use an exact match condition, which is a bit faster.
            Iterator i=input.iterator();
            i.next();
            i.remove(); //remove first elemtent
            condition=Common.buildIdListCondition("a.accession",input,true,limit);
        }
        else
            condition=Common.buildLikeCondtion("a.accession",input,limit);
        
        
        id+=") and ("+condition+")";
        id+=" order by genome";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }
   
    public String getQueryCompSearchQuery(String comp_id, String status)
    {
        String query="SELECT key_id FROM updates.diffs " +
                     "WHERE comp_id="+comp_id+" AND difference='"+status+"'";
        logQuery(query);
        return query;
    }

    public String getQuerySearchQuery(String queries_id)
    {
        String query="select sql from updates.queries where queries_id="+queries_id;
        logQuery(query);
        return query;
    }

    public String getSeqModelSearchQuery(java.util.Collection model_ids)
    {
        String query="SELECT ci.method, count(distinct c.cluster_id) " +
            "FROM clusters as c, cluster_info as ci " +
            "WHERE c.cluster_id=ci.cluster_id " +
            "        and " +Common.buildIdListCondition("c.model_id",model_ids)+
            " GROUP BY ci.method";
        logQuery(query);
        return query;
    }

    public String getStatsById(java.util.Collection data)
    {
        String conditions=Common.buildIdListCondition("s.seq_id",data);
        String query=
        "        select 'models', count( distinct m.model_id) from sequences as s, models as m" +
        "        where s.seq_id=m.seq_id and "+conditions +
        "       UNION "+
        "        (select ci.method, count(distinct c.cluster_id) from sequences as s, clusters as c,cluster_info as ci" +
        "        where s.seq_id=c.seq_id and c.cluster_id=ci.cluster_id and "+conditions+
        "        group by ci.method order by ci.method)";     
        logQuery(query);
        return query;
    }

    public String getStatsByQuery(String query)
    {
        String q=
        "        select 'models', count( distinct m.model_id) from sequences as s, models as m, " +
        "           ("+query+") as t "+
        "        where s.seq_id=m.seq_id and s.seq_id=t.seq_id "+
        "       UNION "+
        "        (select ci.method, count(distinct c.cluster_id) from sequences as s, clusters as c,cluster_info as ci, " +
        "           ("+query+") as t "+
        "        where s.seq_id=c.seq_id and c.cluster_id=ci.cluster_id and s.seq_id=t.seq_id "+
        "        group by ci.method order by ci.method)";       
        logQuery(q);
        return q;
    }

    public String getUnknownClusterIdSearchQuery(int cluster_id)
    {
        String query="SELECT key " +
        "   FROM unknowns.unknown_keys as uk, unknowns.clusters as c " +
        "   WHERE uk.key_id=c.key_id AND c.cluster_id="+cluster_id;
        logQuery(query);
        return query;
    }
    
}
