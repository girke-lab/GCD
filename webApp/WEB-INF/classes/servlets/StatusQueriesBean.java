/*
 * StatusQueriesBean.java
 *
 * Created on November 12, 2004, 9:33 AM
 */

package servlets;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.*;
import org.apache.log4j.Logger;

/**
 * This bean is used by the statusQueries jsp page.
 */
public class StatusQueriesBean
{
    private static Logger log=Logger.getLogger(StatusQueriesBean.class);
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");
    
    private static final int VERSION=0,     QUERIES_ID=1,   NAME=2,
                             PURPOSE=3,     DESCRIPTION=4,  LINK=5,
                             COUNT=6,       VERSION_A=7,    UPDATED_ON=8,
                             ADDED=9,       REMOVED=10,     UNCHANGED=11,
                             COMP_ID=12;
    
    /** Creates a new instance of StatusQueriesBean */
    public StatusQueriesBean()
    {
        if(dbc==null)
            log.error("could not get db connection");
    }
    
    public String printTrackingTable()
    {
        StringBuffer out=new StringBuffer();
        
        String queryStr="SELECT vi.version,q.queries_id, " +
        "    q.name,q.purpose,q.description,q.link, " +
        "    qc.count,csv.version_a,vi.updated_on,csv.added,csv.removed,csv.unchanged,csv.comp_id " +
        "FROM updates.query_counts as qc LEFT JOIN updates.comparison_summary_view as csv ON(qc.version=csv.version_b), " +
        "     updates.queries as q, unknowns.version_info as vi " +
        "WHERE   qc.queries_id=q.queries_id AND qc.version=vi.version  AND " +
        "        (csv.queries_id_a is null OR qc.queries_id=csv.queries_id_a) " +
        "ORDER BY vi.version,q.queries_id ";
        
        List data=null;
        try{
            data=dbc.sendQuery(queryStr);
        }catch(java.sql.SQLException e){
            log.error("query failed: "+e.getMessage());
        }
        if(data==null || data.size()==0)
            return "No results found";
        
        List row;                
        Map versions=new HashMap();        
        Version version=null;
        Query query=null;
        Stat stat=null;
        for(Iterator i=data.iterator();i.hasNext();)
        { 
            row=(List)i.next();
            
            version=(Version)versions.get(row.get(VERSION));
            if(version==null)
            {
                version=new Version((String)row.get(UPDATED_ON));
                versions.put(row.get(VERSION),version);
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
        
        out.append("<table border='1' cellspacing='0' width='100%' bgcolor='"+Common.dataColor+"'>");
        for(Iterator i=versions.entrySet().iterator();i.hasNext();)
        {
            Map.Entry set=(Map.Entry)i.next();
            out.append(((Version)set.getValue()).toHtml((String)set.getKey()));
        }
        out.append("</TABLE>");
        return out.toString();
    }
    
    
    /**
     * Prints a list of queries and thier descriptions.  Displays all queries in the
     * updates.queries table.
     * @return html
     */
    public String printQueries()
    {
        String query="SELECT queries_id,name,description FROM updates.queries " +
                     "ORDER BY name";
        List data=null;
        try{
            data=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("query failed: "+e.getMessage());
            log.error("query was "+query);
        }
        if(data==null || data.size()==0)
            return "<tr><td colspan='2'>No queries found</td><tr>";
        StringBuffer out=new StringBuffer();
        List row;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            out.append("<tr><td nowrap>"+
                "<a href='QueryPageServlet?inputKey="+
                row.get(0)+"&searchType=query&displayType=unknowns2View&rpp=25'>"+
                row.get(1)+"</a></td><td>"+row.get(2)+"</td></tr>\n");
        }
        return out.toString();
    }
    /**
     * Print a list of comparison results.  Gets info from updates.comparisons table.
     * @return html
     */
    public String printComparisons()
    {
        String query="SELECT * FROM updates.comparison_summary_view";
        List data=null;
        StringBuffer out=new StringBuffer();
        try{
            data=dbc.sendQuery(query);            
        }catch(java.sql.SQLException e){
            out.append("error: "+e.getMessage());
            log.error("comparison query failed: "+e.getMessage());
        }
        if(data==null || data.size()==0)
            return "<tr><td colspan='10'>No Comparisons Found</td></tr>"+out;
        
        List row;
        String qps="QueryPageServlet?searchType=query&displayType=unknowns2View&rpp=25&";
        String qps2="QueryPageServlet?searchType=query_comp&displayType=unknowns2View&rpp=25&";
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            out.append("<tr>");
            out.append("<td nowrap><a href='"+qps+"inputKey="+row.get(2)+"'>"+row.get(1)+"</td>");
            out.append("<td>"+row.get(3)+"</td>");
            out.append("<td>"+row.get(5)+"</td>");
            out.append("<td>"+(row.get(6)==null? "&nbsp":row.get(6))+"</td>");
            out.append("<td nowrap><a href='"+qps+"inputKey="+row.get(8)+"'>"+row.get(7)+"</td>");                        
            out.append("<td>"+row.get(9)+"</td>");            
            out.append("<td>"+row.get(11)+"</td>");            
            out.append("<td>"+(row.get(12)==null? "&nbsp":row.get(12))+"</td>");                        
            out.append("<td nowrap><a href='"+qps2+"inputKey="+row.get(0)+" t'>"+row.get(13)+"</td>");
            out.append("<td nowrap><a href='"+qps2+"inputKey="+row.get(0)+" f'>"+row.get(14)+"</td>");
            out.append("</tr>\n");
        }
        return out.toString();
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
            out.append("<tr bgcolor='"+Common.titleColor+"' align='left'><th colspan='7'>"+date+
                        " Version "+version+"</th></td>\n");
            out.append("<tr bgcolor='"+Common.titleColor+"'><th>Query</th><th>Purpose</th><th>Description"+
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
            String url="QueryPageServlet?searchType=query_comp&displayType=unknowns2View&rpp=25&";
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
//      queries=(Map)versions.get(row.get(VERSION));
//            if(queries==null)
//            {
//                queries=new HashMap();
//                versions.put(row.get(VERSION),queries);
//            }
//            queries.put(row.get(QUERIES_ID),row.subList(2,row.size()));
}
