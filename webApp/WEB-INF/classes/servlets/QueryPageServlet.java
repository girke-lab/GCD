/*
 * ResultSummaryServlet.java
 *
 * Created on February 19, 2003, 2:22 PM
 */
package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;

import servlets.search.*;
import servlets.dataViews.*;
/**
 *
 * @author  Kevin Horan
 * @version 1.0
 */

public class QueryPageServlet extends HttpServlet 
{    
    final int feildCount=17;//values used to initialize arrays
    final int dbCount=2;
    final int arab=0,rice=1; //database names
    
    long ID=0;//id number used to identify query
       
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);       
    }    
    public void destroy()
    {    }    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        HttpSession session = request.getSession(true);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        int hid,pos;
        
        
        if(session.getAttribute("hid")==null)
        {//session was just created.
            session.setAttribute("hid",new Integer(0));
            session.setAttribute("history",new ArrayList());            
        }                        
        ////////////////////////// HTML headers  ////////////////////////////////////////////
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Query Result Page</title>");
        out.println("</head>");      
        Common.printHeader(out);
        /////////////////////////// main   ////////////////////////////////////////////////////
        List returnedKeys,stats;
        Search s=null;
        DataView dv=null;
        QueryInfo qi=null;
         
        try{
            hid=Integer.parseInt(request.getParameter("hid"));           
            if(hid < 0 || hid >= ((ArrayList)session.getAttribute("history")).size())
            {
                Common.quit(out,"hid "+hid+" out of bounds");
                return;
            }
            qi=(QueryInfo)((ArrayList)session.getAttribute("history")).get(hid);                    
        }catch(Exception e){ //no hid, so start a new search       
            qi=getInput(request);
            if(qi==null)
            {
               Common.quit(out,"no results found");
                return;
            }            
            hid=((Integer)session.getAttribute("hid")).intValue();
            session.setAttribute("hid",new Integer(hid+1));
            ((ArrayList)session.getAttribute("history")).add(qi);            
        }
        try{
            pos=Integer.parseInt(request.getParameter("pos"));
            if(pos < 0)
                pos=0;
            System.out.println("setting pos to "+pos);
            qi.setCurrentPos(pos);
        }catch(Exception e){
            pos=qi.getCurrentPos();
            System.out.println("retirvied pos as "+pos);
        }
        
        s=qi.getSearch(); 
        
        String displayType=request.getParameter("displayType");
        if(displayType!=null) //if a display type was given, save it
            qi.setDisplayType(displayType);
        
        if(s.getResults()==null || s.getResults().size()==0){
            Common.quit(out,"no matches found");
            return;
        }
        if(pos < 0 || pos > s.getResults().size() ){
            Common.quit(out, "position "+pos+" is out of bounds");
            return;
        }
        int end=pos+Common.recordsPerPage > s.getResults().size()? s.getResults().size() : pos+Common.recordsPerPage;
        returnedKeys=s.getResults().subList(pos,end);
        stats=s.getStats();
        dv=getDataView(qi.getDisplayType(),qi.getSortCol(),request);                
        dv.setData(returnedKeys, qi.getSortCol(),qi.getDbs(),hid); //rpp is redundent here
        
        //print page
        dv.printHeader(out); //prints form for  seq servlet
        Common.printPageControls(out, pos, s.getResults().size(),s.getDbStartPos(Common.rice),hid); 
        out.println("Keys entered: "+qi.getInputCount()+"<br>");        
        out.println("<table cellspacing='0'><tr><td>");
        Common.printTotals(out,s);
        out.println("</td><td>");
        dv.printStats(out);
        out.println("</td></tr></table>");
        
        dv.printData(out);
                
        printMismatches(out, s.notFound());
        out.println("</body>");
        out.println("</html>");

        out.close();
        /////////////////////////////////  end of main  ////////////////////////////////////////////
    }
    private DataView getDataView(String displayType,String sortCol,HttpServletRequest request)
    {        
        System.out.println("displayType="+displayType);
        if(displayType!=null)
        {
            if(displayType.equals("clusterView"))
                return new ClusterDataView();
            else if(displayType.equals("seqView"))
                return new SeqDataView();
            else if(displayType.equals("modelView"))
                return new ModelDataView(request);
        }
        else if(sortCol!=null)
        {
            if(sortCol.startsWith("sequences"))
                return new SeqDataView();
            else if(sortCol.startsWith("cluster_info"))
                return new ClusterDataView();
        }
        return new SeqDataView();        
    }
    private Search getSearchObj(String type) 
    {        
        System.out.println("Search type="+type);
        if(type.equals("Description"))
            return new DescriptionSearch();
        else if(type.equals("Id"))
            return new IdSearch();
        else if(type.equals("Cluster Id"))
            return new ClusterIDSearch();
        else if(type.equals("Cluster Name"))
            return new ClusterNameSearch();
        else if(type.equals("GO Number"))
            return new GoSearch();
        else if(type.equals("seq_id"))
            return new SeqIdSearch();
        else
            return new IdSearch();   //default to id search
    }   
    
    private QueryInfo getInput(HttpServletRequest request)
    {
        int[] dbNums;
        int limit;
        List inputKeys;
        Search s;
        QueryInfo qi;
        
        String input=request.getParameter("inputKey"); //actual input from form feild
        String searchType=request.getParameter("searchType");
        String[] dbTemp=request.getParameterValues("dbs"); //list of databases to use
        String sortCol=request.getParameter("sortCol");

        try{//test limit
            limit=Integer.parseInt(request.getParameter("limit"));
            if(limit>=Common.MAXKEYS || limit==0) //cap limit at MAXKEYS
                limit=Common.MAXKEYS;
        }catch(NumberFormatException nfe){
            limit=Common.recordsPerPage; //default limit
        }
        try{//test dbNums for valid input and conver text to numbers   // DBfeilds
            dbNums=new int[dbTemp.length];
            for(int i=0;i<dbTemp.length;i++)
                dbNums[i]=Integer.parseInt(dbTemp[i]);            
        }catch(Exception e){
            dbNums=new int[]{0,1};              
        }
        System.out.println("input="+input);
        if(input==null || input.length()==0)
            return null;
        else
        { //put keys in a list, rather then have to parse the sting every time
            inputKeys=new ArrayList();
            StringTokenizer tok=new StringTokenizer(input);
            while(tok.hasMoreTokens())
                inputKeys.add(tok.nextToken());
        }
        qi=new QueryInfo(dbNums,sortCol,"");
        //search all databases simultaniously
        s=getSearchObj(searchType);        
        s.init(inputKeys,limit, dbNums);              
        qi.setSearch(s);
        qi.setInputCount(inputKeys.size());
        
        return qi;
    }
 
    private void printMismatches(PrintWriter out,List keys)
    {
        if(keys.size()==0) //don't print anything if there are no missing keys
            return;
        out.println("Keys not returned:");
        for(int i=0;i<keys.size();i++)
        {
            if(i!=0)
                out.println(", ");
            out.println(keys.get(i));
        }
    }            
    
    ///////////////////////////////////////////////////////////////////////////////////////////
   
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    
}

