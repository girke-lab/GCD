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

public class StatusQueriesBean
{
    private static Logger log=Logger.getLogger(StatusQueriesBean.class);
    private static DbConnection dbc=DbConnectionManager.getConnection("khoran");
    
    /** Creates a new instance of StatusQueriesBean */
    public StatusQueriesBean()
    {
        if(dbc==null)
            log.error("could not get db connection");
    }
    
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
}
