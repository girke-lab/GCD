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
import org.apache.log4j.Logger;

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
    private static Logger log=Logger.getLogger(QueryPageServlet.class);
    
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
        String format=request.getParameter("format");
        boolean allFasta=(format!=null && format.equals("2"));
        
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
//        Common.printHeader(out);
        /////////////////////////// main   ////////////////////////////////////////////////////
        List returnedKeys,stats;
        Search s=null;
        DataView dv=null;
        QueryInfo qi=null;
        int rpp; //results per page
         
        //parameters that need to be evaluated for each page should be grabbed
        //here, all others should be grabbed in the getInput() method and stored in
        //a QueryInfo object.
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
            qi.setCurrentPos(pos);
        }catch(Exception e){
            pos=qi.getCurrentPos();
        }
        rpp=((Integer)qi.getObject("rpp")).intValue();
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
        
        
        dv=getDataView(qi.getDisplayType(),qi.getSortCol(),request);
        dv.setData(qi.getSortCol(),qi.getDbs(),hid);
        ResultPage page=new ResultPage(dv, s, pos, hid, rpp);
        page.dipslayPage(out);
        
        
        
//        
//        int end=pos+rpp > s.getResults().size()? s.getResults().size() : pos+rpp;
//        if(allFasta)
//            returnedKeys=s.getResults();
//        else
//            returnedKeys=s.getResults().subList(pos,end);
//        
//        dv=getDataView(qi.getDisplayType(),qi.getSortCol(),request);                
//        dv.setData(returnedKeys, qi.getSortCol(),qi.getDbs(),hid); //rpp is redundent here
//        
//        //print page
//        dv.printHeader(out); //prints form for  seq servlet
//        //Common.printPageControls(out,rpp, pos, s.getResults().size(),s.getDbStartPos(Common.rice),hid); 
//        out.println("Keys entered: "+qi.getInputCount()+"<br>");        
//        
//        out.println("<table cellspacing='0' cellpadding='0'><tr><td>");
//        Common.printTotals(out,s,qi.getDisplayType()); 
//        out.println("</td><td>");
//        dv.printStats(out);
//        out.println("</td></tr></table>");
//        
//        printPageControls(out,qi,hid);
//        if(!qi.getDisplayType().equals("clusterView"))
//            Common.printButtons(out,hid,pos,s.getResults().size(),rpp); 
//
//        dv.printData(out);
//                
//        printMismatches(out, s.notFound());
        out.println("</body>");
        out.println("</html>");

        out.close();
        /////////////////////////////////  end of main  ////////////////////////////////////////////
    }
    private DataView getDataView(String displayType,String sortCol,HttpServletRequest request)
    {                
        if(displayType!=null)
        {
            if(displayType.equals("clusterView"))
                return new ClusterDataView();
            else if(displayType.equals("seqView"))
                return new SeqDataView();
            else if(displayType.equals("modelView"))
                return new ModelDataView(request);
            else if(displayType.equals("statsView"))
                return new StatsDataView();
            else if(displayType.equals("unknownsView"))
                return new UnknownsDataView(this.getServletContext().getRealPath("/temp"));
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
            limit=Common.MAXKEYS; //default limit
        }
        try{//test dbNums for valid input and conver text to numbers   // DBfeilds
            dbNums=new int[dbTemp.length];
            for(int i=0;i<dbTemp.length;i++)
                dbNums[i]=Integer.parseInt(dbTemp[i]);            
        }catch(Exception e){
            dbNums=new int[]{0,1};              
        }
        
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
        s=getSearchObj(searchType);        
        s.init(inputKeys,limit, dbNums);              
        qi.setSearch(s);
        qi.setInputCount(inputKeys.size());
        
        try{            
            qi.setObject("rpp", new Integer(request.getParameter("rpp")));            
        }catch(Exception e){
            qi.setObject("rpp", new Integer(50));            
        }
        return qi;
    }
 
    private void printPageControls(PrintWriter out,QueryInfo qi,int hid)
    {
        String action="QueryPageServlet?hid="+hid;        
        int pos=qi.getCurrentPos();
        int rpp=((Integer)qi.getObject("rpp")).intValue();
        int end=qi.getSearch().getResults().size();
        int c=2;
        if(pos-rpp >=0 ) c++;
        if(pos+rpp < end) c++;
        
        out.println("<table align='left' border='0'>");
//        out.println("<tr><th colspan='"+c+"'>Page Switch</th>" +
//            "<th colspan='"+qi.getDbs().length+"'>Go to: </th></tr>");
        out.println("<tr>");        
        out.println("<td><a href='"+action+"&pos=0'>Start</a></td>");
        if(pos-rpp >= 0)        
            out.println("<td><a href='"+action+"&pos="+(pos-rpp)+"'>Previous</a></td>");
        if(pos+rpp < end)
            out.println("<td><a href='"+action+"&pos="+(pos+rpp)+"'>Next</a></td>");        
        out.println("<td><a href='"+action+"&pos="+(end-(end%rpp))+"'>End</a></td>");
  
        if(!qi.getDisplayType().equals("clusterView"))
        {
            out.println("<td>&nbsp&nbsp&nbsp&nbsp Go to: </td>");
            for(int i=0;i<qi.getDbs().length;i++)        
                out.println("<td><a href='"+action+"&pos="+qi.getSearch().getDbStartPos(qi.getDbs()[i])+"'>" +
                    Common.dbPrintNames[qi.getDbs()[i]]+"</a></td>");
        }
        out.println("</tr>");
        out.println("</table><p><br>");
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

