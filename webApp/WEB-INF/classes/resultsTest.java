/*
 * resultsTest.java
 *
 * Created on February 7, 2003, 12:50 PM
 */

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author  khoran
 * @version
 */
import java.util.*;
public class resultsTest extends HttpServlet {
    
    java.io.PrintWriter out;
    
    
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
    throws ServletException, java.io.IOException {
        response.setContentType("text/html");
        out = response.getWriter();
        String query=request.getParameter("query");
        String queryString=null;
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("servlet test");
        
        if(query!=null && query.equals("one"))
            queryString=new String("SELECT * FROM TIGR_Data where Atnum LIKE "+
                                      "'At1g1%' ");
        else
            queryString=new String("SELECT * FROM TIGR_Data where Atnum='At1g01010.1'");
        
        queryThread dbConn=new queryThread("Cis_Regul");
        dbConn.setQuery(queryString,7);
        dbConn.start();
        while(dbConn.isAlive());
        List data=new ArrayList(dbConn.getResults());
        printList(data);
        
        
        
        
        
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
    private void printList(List list)
    {
        int rows=0,cols=0;
        for(ListIterator l=list.listIterator();l.hasNext();)
        {
            rows++;
            cols=0;
            for(ListIterator l2=((ArrayList)l.next()).listIterator();l2.hasNext();)
            {
                cols++;
                out.println(l2.next()+", ");
            }
            out.println("<BR>");    
        }
        out.println(rows+" rows, "+cols+" columns");
    }
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
