/*
 * StatusQueriesBean.java
 *
 * Created on November 12, 2004, 9:33 AM
 */

package servlets;

/**
 * This bean is used by the statusQueries jsp page.
 * @author  khoran
 */

import java.util.*;
import servlets.*;
import org.apache.log4j.Logger;

public class StatusQueriesBean
{
    private static Logger log=Logger.getLogger(StatusQueriesBean.class);
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");
    
    private static final int VERSION=0,     QUERIES_ID=1,   NAME=2,
                             PURPOSE=3,     DESCRIPTION=4,  LINK=5,
                             COUNT=6,       VERSION_A=7,    UPDATED_ON=8,
                             ADDED=9,       REMOVED=10,     UNCHANGED=11,
                             COMP_ID=12,    GENOME=13;
    
    /** Creates a new instance of StatusQueriesBean */
    public StatusQueriesBean()
    {
        if(dbc==null)
            log.error("could not get db connection");
    }
    
    public String printTrackingTable()
    {
        StringBuffer out=new StringBuffer();        
        
        String queryStr=
            "SELECT" +
            "   vi.version," +
            "   q.queries_id, q.name,q.purpose," +
            "   q.description, q.link," +
            "   qc.count," +
            "   csv.version_a,vi.updated_on,csv.added,csv.removed,csv.unchanged,csv.comp_id, " +
            "   gd.db_name "+
            " FROM    updates.queries as q" +
            "   JOIN updates.query_counts as qc USING(queries_id)" +
            "   JOIN unknowns.version_info as vi USING(version)" +
            "   JOIN general.genome_databases as gd USING(genome_db_id) "+
            "   LEFT JOIN updates.comparison_summary_view as csv" +
            "       ON(qc.version=csv.version_b AND qc.genome_db_id=csv.genome_db_id_b)" +
            " WHERE   (csv.queries_id_a is null OR qc.queries_id=csv.queries_id_a )" +
            " ORDER BY qc.genome_db_id,vi.version desc,q.queries_id ";

        
        
        List data=null;
        try{
            data=dbc.sendQuery(queryStr);
        }catch(java.sql.SQLException e){
            log.error("query failed: "+e.getMessage());
        }
        if(data==null || data.size()==0)
            return "No results found";
        
        List row;                        
        Map genomes=new HashMap();
        Genome genome=null;
        Version version=null;
        Query query=null;
        Stat stat=null;
        for(Iterator i=data.iterator();i.hasNext();)
        { 
            row=(List)i.next();
            
            genome=(Genome)genomes.get(row.get(GENOME));
            if(genome==null)
            {
                genome=new Genome((String)row.get(GENOME));
                genomes.put(row.get(GENOME), genome);
            }
            
            version=(Version)genome.versions.get(row.get(VERSION));
            if(version==null)
            {
                version=new Version((String)row.get(UPDATED_ON));
                genome.versions.put(row.get(VERSION),version);
            }
            
            query=(Query)version.queries.get(row.get(QUERIES_ID));
            if(query==null)
            {
                query=new Query((String)row.get(NAME),(String)row.get(PURPOSE),
                    (String)row.get(DESCRIPTION),(String)row.get(LINK),
                    (String)row.get(COUNT),(String)row.get(COMP_ID));
                version.queries.put(row.get(QUERIES_ID),query);
            }
            stat=new Stat((String)row.get(ADDED),(String)row.get(REMOVED),
                    (String)row.get(UNCHANGED));
            query.stats.put(row.get(VERSION_A),stat);
        }
        
        out.append("<table border='0' width='100%' >");
        for(Iterator i=genomes.entrySet().iterator();i.hasNext();)
        {
            Map.Entry set=(Map.Entry)i.next();
            out.append(((Genome)set.getValue()).toHtml((String)set.getKey()));
        }
        out.append("</table>");
        return out.toString();
    }
    
 
    class Genome
    {
        public String name;
        public Map versions;
        private Map realNames;
        
        public Genome(String name)
        {
            this.name=name;
            versions=new  LinkedHashMap();
            realNames=new HashMap();
            realNames.put("arab", "Arabidopsis");
            realNames.put("rice","Rice");
        }
        public String toHtml(String genome)
        {
            StringBuffer out=new StringBuffer();
            out.append("<tr><th align='center'><h2>"+realNames.get(genome)+"</h2></th><tr>");
            out.append("<tr><td><table border='1' cellspacing='0' width='100%' bgcolor='"+PageColors.data+"'>");            
            
            for(Iterator i=versions.entrySet().iterator();i.hasNext();)
            {
                Map.Entry set=(Map.Entry)i.next();
                out.append(((Version)set.getValue()).toHtml((String)set.getKey()));
            }
            out.append("</table></td></tr>");
            return out.toString();
        }
    }
    class Version
    {
        public String date;        
        public Map queries; //use queries_id as key to query objects
        
        public Version(String date)
        {
            this.date=date;            
            queries=new HashMap();
        }
        public String toHtml(String version)
        {
            StringBuffer out=new StringBuffer();
            out.append("<tr bgcolor='"+PageColors.title+"' align='left'>" +
                    "<th colspan='7'>"+date+" Version "+version+"</th></td>\n");
            out.append("<tr bgcolor='"+PageColors.title+"'><th>Query</th><th>Purpose</th><th>Description"+
                "</th><th>Size</th><th>Overlaps</th><th>New</th><th>"+
                "Removed</th></tr>");
            for(Iterator i=queries.values().iterator();i.hasNext();)                                        
                out.append(((Query)i.next()).toHtml(version));
                            
            return out.toString();
        }
    }
    class Query
    {
        public String name,purpose,description,link,count,comp_id;
        public Map stats; //use version_a as key to stat objects
        
        public Query(String name,String purpose,String desc,String link,String count,String comp_id)
        {
            this.name=name;
            this.purpose=purpose;
            this.description=desc;
            this.link=link;
            this.count=count;
            this.comp_id=comp_id;
            stats=new HashMap();
        }
        public String toHtml(String version)
        {            
            String linkedDesc=description;
            String added="&nbsp",removed="&nbsp",unchanged="&nbsp";
            String url="QueryPageServlet?searchType=query_comp&displayType=unknowns2View&rpp=25&origin_page=statusQueries.jsp&";
            if(link!=null && link.length() > 0)
                linkedDesc="<a href='"+link+"'>"+description+"</a>";
            
            for(Iterator i=stats.entrySet().iterator();i.hasNext();)
            {
                Map.Entry set=(Map.Entry)i.next();
                if(((Stat)set.getValue()).added!=null)
                    added="V"+version+"/"+set.getKey()+
                        ": <a href='"+url+"inputKey="+comp_id+" added'>"+
                        ((Stat)set.getValue()).added+"</a>\n";
                if(((Stat)set.getValue()).removed!=null)
                    removed="V"+version+"/"+set.getKey()+
                        ": <a href='"+url+"inputKey="+comp_id+" removed'>"+
                        ((Stat)set.getValue()).removed+"</a>\n";
                if(((Stat)set.getValue()).unchanged!=null)
                    unchanged="V"+version+"/"+set.getKey()+
                        ": <a href='"+url+"inputKey="+comp_id+" unchanged'>"+
                        ((Stat)set.getValue()).unchanged+"</a>\n";                
            }
            
            return "<tr><td>"+name+"</td><td>"+purpose+"</td><td>"+linkedDesc+
                "</td><td>"+count+"</td><td>"+unchanged+"</td><td>"+added+"</td><td>"+
                removed+"</tr>";            
        }
    }
    class Stat
    {
        public String added,removed,unchanged;
        
        public Stat(String added,String removed,String unchanged)
        {            
            this.added=added;
            this.removed=removed;
            this.unchanged=unchanged;
        }
    }
}
