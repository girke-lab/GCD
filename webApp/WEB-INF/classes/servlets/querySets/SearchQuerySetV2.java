/*
 * Version2SearchQuerySet.java
 *
 * Created on March 25, 2005, 3:18 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;

public class SearchQuerySetV2 implements SearchQuerySet
{
    private static Logger log=Logger.getLogger(SearchQuerySetV2.class);
    
    /** Creates a new instance of Version2SearchQuerySet */
    public SearchQuerySetV2()
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
        String q="SELECT distinct accessions.accession_id,clusters.key, genome_databases.db_name " +
                "FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                "   JOIN general.clusters USING(cluster_id) JOIN general.genome_databases USING(genome_db_id) " +
                "WHERE accessions.is_model=FALSE AND (";                       
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondtion("clusters.key",input,limit)+")";
        q+=" order by genome_databases.db_name ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getClusterNameSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {
        String q="SELECT distinct accessions.accession_id,clusters.name, genome_databases.db_name " +
                "FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                "   JOIN general.clusters USING(cluster_id) JOIN general.genome_databases USING(genome_db_id) " +
                "WHERE accessions.is_model=FALSE AND (";                       
        
        for(int i=0;i<DBs.length;i++)
        {
            q+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                q+=" or ";
        }

        q+=") and ("+Common.buildLikeCondtion("clusters.name",input,limit)+")";
        q+=" order by genome_databases.db_name ";
        q+=" limit "+limit;
        logQuery(q);        
        return q;
    }

    public String getDescriptionSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {        
        
        String id="SELECT DISTINCT accessions.accession_id, genome_databases.db_name " +
                "FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "WHERE accessions.is_model=FALSE AND (";
        for(int i=0;i<DBs.length;i++)
        {
            id+=" genome_databases.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+Common.buildDescriptionCondition("accessions.description",input)+")";
        id+=" order by genome_databases.db_name ";
        id+=" limit "+limit;
        logQuery(id);
        return id;
    }

    public String getGoSearchQuery(java.util.Collection input, int limit)
    {
        String query="SELECT DISTINCT accessions.accession_id, go_numbers.go_number, " +
                "       genome_databases.db_name" +
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "   JOIN general.accession_gos USING(accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                " WHERE "+Common.buildIdListCondition("go_numbers.go_number",input,true,limit)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        logQuery(query);
        return query;
    }

    public String getGoTextSearchQuery(java.util.Collection input, int limit)
    {
        String query="SELECT DISTINCT accessions.accession_id, go_numbers.go_number, " +
                "       genome_databases.db_name" +
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                "   JOIN general.accession_gos USING(accession_id) " +
                "   JOIN general.go_numbers USING(go_id) " +
                " WHERE "+Common.buildIdListCondition("go_numbers.text",input,true,limit)+
                " ORDER BY genome_databases.db_name "+
                " limit "+limit;
        logQuery(query);
        return query;
    }

    public String getIdSearchQuery(java.util.Collection input, int limit, int[] DBs)
    {        
        String id="SELECT DISTINCT accessions.accession_id, accessions.accession, genome_databases.db_name "+
                " FROM general.accessions JOIN general.genome_databases USING(genome_db_id) " +
                " WHERE accessions.is_model=FALSE AND (";
        for(int i=0;i<DBs.length;i++)
        {
            id+=" genome_databaes.db_name='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        String condition;
        if(input.size()!=0 && ((String)input.iterator().next()).equals("exact") )
        {//if first element is 'exaxt', then use an exact match condition, which is a bit faster.
            Iterator i=input.iterator();
            i.next();
            i.remove(); //remove first elemtent
            condition=Common.buildIdListCondition("accessions.accession",input,true,limit);
        }
        else
            condition=Common.buildLikeCondtion("accessions.accession",input,limit);
        
        
        id+=") and ("+condition+")";
        id+=" order by genome_databases.db_name";
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
       //TODO: needs testing.
        String query="SELECT clusters.method, count(distinct cluster_members.cluster_id) " +
                " FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                "       JOIN general.clusters USING(cluster_id) " +
                " WHERE "+Common.buildIdListCondition("accessions.accession_id",model_ids) +
                "       AND accessions.is_model=TRUE " +
                " GROUP BY clusters.method ";
        
        logQuery(query);
        return query;
    }

    public String getStatsById(java.util.Collection data)
    {
        String condition=Common.buildIdListCondition("md.sequence_accession_id",data);
        String query=
                "SELECT 'models', count(distinct md.accession_id) "+
                " FROM common.model_data as md" +
                " WHERE "+condition+
            " UNION "+
                "SELECT c.method, count(distinct cm.cluster_id) " +
                " FROM common.model_data md JOIN general.accessions as a ON(md.accession_id=a.accession_id)" +
                "   JOIN general.cluster_members as cm ON(a.accession_id=cm.accession_id) JOIN general.clusters as c USING(cluster_id) " +
                " WHERE "+condition+
                " GROUP BY c.method";
                
//select c.method, count(distinct cm.cluster_id) from common.model_data as md JOIN general.accessions as a ON(md.accession_id=a.accession_id) JOIN general.cluster_members as cm USING(accession_id) JOIN general.clusters as c USING(cluster_id)  where md.sequence_accession_id in  (683939,683217) group by c.method;
        logQuery(query);
        return query;
                
    }

    public String getStatsByQuery(String idQuery)
    {
        String condition="md.sequence_accession_id in ("+idQuery+")";
        String query=
                "SELECT 'models', count(distinct md.accession_id) "+
                " FROM common.model_data as md" +
                " WHERE "+condition+
            " UNION "+
                "SELECT c.method, count(distinct cm.cluster_id) " +
                " FROM common.model_data md JOIN general.accessions as a ON(md.accession_id=a.accession_id)" +
                "   JOIN general.cluster_members as cm ON(a.accession_id=cm.accession_id) JOIN general.clusters as c USING(cluster_id) " +
                " WHERE "+condition+
                " GROUP BY c.method";
        logQuery(query);
        return query;
    }

    public String getUnknownClusterIdSearchQuery(int cluster_id)
    {
        //TODO: should not be used for version 2.
        String query="SELECT DISTINCT accessions.accession" +
                " FROM general.accessions JOIN general.cluster_members USING(accession_id) " +
                " WHERE cluster_member.cluster_id="+cluster_id;
        logQuery(query);
        return query;
    }
    
}
