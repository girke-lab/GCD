/*
 * UnknownResultsServlet.java
 *
 * Created on September 8, 2004, 1:01 PM
 */

package servlets;

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

import servlets.dataViews.*;
import java.util.*;

/**
 *
 * @author  khoran
 * @version
 */
public class UnknownResultsServlet extends HttpServlet {
    
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);        
    }
    
    /** Destroys the servlet.
     */
    public void destroy() {
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
              
        //more of a temporary servlet to test the uknownsDataView
        //retrive inputKey, limit, sortCol
        String inputKeys=request.getParameter("inputKey");
        String limit=request.getParameter("limit");
        String sortCol=request.getParameter("sortCol");
        
        if(inputKeys==null || limit==null || sortCol==null)
        {
            out.println("no data");
            return;
        }
        DataView dv=new UnknownsDataView();
        List ids=new LinkedList();
        StringTokenizer tok=new StringTokenizer(inputKeys);
        while(tok.hasMoreTokens())
           ids.add(tok.nextToken());
        dv.setData(ids, sortCol, new int[]{},0);
        
        dv.printHeader(out);
        dv.printStats(out);
        dv.printData(out);
        
        out.close();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    
}
