/*
 * QueryDispatcherServlet.java
 *
 * Created on November 10, 2004, 12:25 PM
 */

package servlets.advancedSearch;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.log4j.Logger;
import servlets.*;
import java.sql.SQLException;

/**
 *
 * @author  khoran
 * @version
 */
public class QueryDispatcherServlet extends HttpServlet
{
    private static Logger log=Logger.getLogger(QueryDispatcherServlet.class);    
    
    private static QuerySet[] querySets=null;
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        response.setContentType("text/html");        
       
        if(request.getParameter("reload")!=null)
        {//re-read the query files
            for(int i=0;i<querySets.length;i++)
                querySets[i].refresh();
            return;
        }
        String queryName=request.getParameter("query_name");                
        sendQuery(queryName,request,response);                
    }
    private void sendQuery(String queryName, HttpServletRequest request,HttpServletResponse response)
    {
        QuerySet qs=null;
        List results=null;
        
        //find query 
        for(int i=0;i<querySets.length;i++)
            if(querySets[i].getQuery(queryName)!=null)
                qs=querySets[i];

        if(qs==null) //no query found
        {
            log.warn("query "+queryName+" was not found anywhere");
            return;
        }        
        log.debug("found "+queryName+" for view "+qs.getView());
        //send query
        try{
            results=qs.sendQuery(qs.getQuery(queryName));
        }catch(SQLException e){ 
            log.error("error sending query: "+e.getMessage());
        }
        //call dispatch
        dispatch(qs.getView(),results,request,response);
    }
    private void dispatch(String view,List results, HttpServletRequest request,HttpServletResponse response)
    { 
        
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");                                        
        mRequest.getParameterMap().put("displayType",view); 
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        try{            
            getServletContext().getRequestDispatcher("/QueryPageServlet").forward(mRequest, response);            
        }catch(Exception e){
            log.error("could not forward to QueryPageServlet: "+e.getMessage());
            e.printStackTrace();
        }
    }
    
    
  /////////////////////////////////////////////////////////////////////////////  
  /////////////////////////////////////////////////////////////////////////////
    
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        
        String dir="storedQueries/";
        querySets=new QuerySet[]{  
                new QuerySet("common","seqView",dir+"common_queries.properties"),
                new QuerySet("khoran","unknowns2View",dir+"uk2_queries.properties")
        };        
    }
    
    /** Destroys the servlet.
     */
    public void destroy()
    {
        
    }
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo()
    {
        return "Short description";
    }
    
    class QuerySet
    {
        private DbConnection dbc;
        private String view;
        private Properties queries;
        private String propFile;
        
        QuerySet(String dbName,String view,String propFile)
        {
            dbc=DbConnectionManager.getConnection(dbName);
            if(dbc==null)
                log.error("could not get db connection for "+dbName);
            this.view=view;
            this.propFile=propFile;               
            //refresh(); //load properties
            
            try{        
                log.debug("loading prop file "+propFile);
                queries=new Properties();  
                //queries.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(propFile));
                queries.load(Common.class.getClassLoader().getResourceAsStream(propFile));
            }catch(IOException e){
                log.error("could not load query file "+propFile+": "+e.getMessage());
            }

        }
        
        public String getQuery(String queryName)
        {
            return queries.getProperty(queryName);
        }
        public List sendQuery(String query) throws SQLException
        {
            return dbc.sendQuery(query);
        }
        public String getView()
        {
            return view;
        }            
        public void refresh()
        {
            try{        
                log.debug("reloading prop file "+propFile);
                queries=new Properties();  
                queries.load(Common.class.getClassLoader().getResourceAsStream(propFile));
            }catch(IOException e){
                log.error("could not load query file "+propFile+": "+e.getMessage());
            }
        }
    }
}
