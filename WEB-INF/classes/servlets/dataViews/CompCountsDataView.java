/*
 * CompCountsDataView.java
 *
 * Created on May 22, 2006, 1:37 PM
 *
 */

package servlets.dataViews;


import servlets.dataViews.queryWideViews.DefaultQueryWideView;
import servlets.querySets.QuerySetProvider;
import servlets.search.Search;
import java.util.*;
import servlets.*;
import servlets.beans.HeaderBean;

/**
 *
 * @author khoran
 */
public class CompCountsDataView implements DataView
{
    
    int keyType;
    String userName;
    private HeaderBean header;
    
    private static final int    CATAGORY=0,    KEY=1,
                    NAME=2,     GROUP_NO=3,     C_DESC=4,
                    T_DESC=5,   USER_NAME=5;
    
    /** Creates a new instance of CompCountsDataView */
    public CompCountsDataView()
    {
        header=new HeaderBean();
    }

    public int getKeyType()
    {
        return keyType;
    }

    public void printData(java.io.PrintWriter out)
    {
        PageColors.printColorKey(out);
        out.println("<P>");
        List data=Common.sendQuery(QuerySetProvider.getDataViewQuerySet().getCompCountDataViewQuery(userName));
        
        out.println("<table cellspacing='0' border='1'>");
        out.println("<tr bgcolor='"+PageColors.title+"'><th>Experiment Set</th>" +
                "<th>Name</th></td>");
        List row;        
        String lastES=null;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            if(lastES==null || !lastES.equals(row.get(KEY)))
            {
                if(lastES!=null) //close last set
                {
                    out.println("</table></td></tr>");
                }
                out.println("<tr bgcolor='"+PageColors.catagoryColors.get(row.get(CATAGORY))+"'>");
                out.println("<td>"+row.get(KEY)+"</td><td>"+row.get(NAME)+"</td>");
                out.println("</tr>");
                out.println("<tr><td colspan='2'>" +
                        "<table cellspacing='0' bgcolor='"+PageColors.data+"' border='1' width='100%'>");
                out.println("<tr bgcolor='"+PageColors.title+"'>" +
                        "<th>&nbsp</th><th>Comparison</th><th>Control Description</th><th>Treatment Description</th></tr>");
                lastES=(String)row.get(KEY);
            }
            
            out.println("<tr><td>&nbsp&nbsp</td>");
            out.println("<td>"+row.get(GROUP_NO)+"</td><td>"+row.get(C_DESC)+"</td><td>"+
                                row.get(T_DESC)+"</td>");
            out.println("</tr>");
        }
        out.println("</table>");
    }
    
//////////////////////////////////////////////////////////////////////    
    public servlets.dataViews.queryWideViews.QueryWideView getQueryWideView()
    {
        return new DefaultQueryWideView(){
            public void printStats(java.io.PrintWriter out,Search search){}
            public void printButtons(java.io.PrintWriter out, int hid,int pos,int size,int rpp){}    
            public boolean printAllData(){return true;}
        };
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_SEQ,Common.KEY_TYPE_ACC,Common.KEY_TYPE_BLAST,
                        Common.KEY_TYPE_CLUSTER,Common.KEY_TYPE_MODEL,Common.KEY_TYPE_QUERY};
    }

   

    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
    }

    public void printHeader(java.io.PrintWriter out)
    {
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.PED);
        header.printStdHeader(out,"Experiment Set Summary", userName!=null);
        
        out.println("<center>");
        Common.printUnknownsSearchLinks(out);
        out.println("</center>");
    }

    public void printStats(java.io.PrintWriter out)
    {
    }

    public void setData(String sortCol, int[] dbList, int hid)
    {
    }

    public void setIds(java.util.List ids)
    {
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        this.keyType=keyType;
    }

    public void setParameters(java.util.Map parameters)
    {
    }

    public void setSortDirection(String dir)
    {
    }

    public void setStorage(java.util.Map storage)
    {
    }

    public void setUserName(String userName)
    {        
        this.userName=userName;
        header.setLoggedOn(userName!=null);
    }
    
}
