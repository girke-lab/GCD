/*
 * StatsDataView.java
 *
 * Created on August 26, 2004, 2:38 PM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import servlets.*;
import org.apache.log4j.Logger;
import servlets.dataViews.queryWideViews.*;
import servlets.search.Search;
import java.util.*;
import servlets.beans.HeaderBean;

public class StatsDataView implements DataView
{
    private static Logger log=Logger.getLogger(StatsDataView.class);
    private String userName; 
    private HeaderBean header=new HeaderBean();
    
    
    /** Creates a new instance of StatsDataView */
    public StatsDataView() {
    }
    
    public void printData(java.io.PrintWriter out) { 
        List dbNames=new LinkedList(DbConnectionManager.getConnectionNames());        
        Collections.sort(dbNames);
        DbConnection dbc=null;
        out.println("<table border='1' cellspacing='0' bgcolor='"+PageColors.data+"'>");
        
        for(Iterator i=dbNames.iterator();i.hasNext();)
        {
            String name=(String)i.next();
            dbc=DbConnectionManager.getConnection(name);
            if(dbc==null)
                continue;            
            out.println("<tr><th bgcolor='"+PageColors.title+"'>"+name+"</th><tr>");
            out.println("<tr><td>");
            dbc.printStats(out);
            out.println("</td></tr>");                        
        }
        out.println("</table>");
        
    }
    
    public void printHeader(java.io.PrintWriter out) 
    {
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.COMMON);
        header.printStdHeader(out,"Database Stats", userName!=null);

    }
    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
    }
    
    public void printStats(java.io.PrintWriter out) {
    }
    
    public void setData(String sortCol, int[] dbList, int hid) {
    }
    
    public void setIds(java.util.List ids) {
    }        
    
    public QueryWideView getQueryWideView()
    {
       return new DefaultQueryWideView(){
            public void printStats(java.io.PrintWriter out,Search search){}
            public void printButtons(java.io.PrintWriter out, int hid,int pos,int size,int rpp){}    
            public boolean printAllData(){return true;}
        };
    }
    
    public void setSortDirection(String dir)
    {
    }

    public int[] getSupportedKeyTypes()
    {
         return new int[]{};
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
       
    }
    public void setUserName(String userName)
    {

    }


    public int getKeyType()
    {
        return -1;
    }

    public void setParameters(Map parameters)
    {
    }

    public void setStorage(Map storage)
    {
    }
    
}
