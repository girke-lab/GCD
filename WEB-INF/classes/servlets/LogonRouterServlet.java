/*
 * LogonRouterServlet.java
 *
 * Created on January 31, 2006, 11:01 AM
 */

package servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import servlets.dataViews.StatisticsDataView;

/**
 *
 * @author khoran
 * @version
 */
public class LogonRouterServlet extends HttpServlet
{
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    private static Logger log = Logger.getLogger(LogonRouterServlet.class);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
    { 
        System.out.println("Made it to logon router servlet");
        final String redirectURL = "Proxy.jsp";
        HttpSession ses = request.getSession();
        
        log.debug(" in router, username="+request.getParameter("j_username"));
        
        ses.setAttribute("j_username", request.getParameter("j_username"));
        ses.setAttribute("j_password", request.getParameter("j_password"));
        ses.setAttribute("j_security_check", request.getParameter("j_security_check"));
        // When the originator is passed by the default login page
        if (request.getParameter("originator")!=null)
        {
            log.debug("forwarding originator to "+request.getParameter("originator"));
            ses.setAttribute("originator", request.getParameter("originator"));        
        }
        else  // When the originator is passed by a page using the search include
        {
            log.debug(" setting originator to referer: "+request.getHeader("Referer"));
            ses.setAttribute("originator", request.getHeader("Referer"));
        }
        
        log.debug("redirecting to "+redirectURL);
        response.sendRedirect(response.encodeRedirectURL(redirectURL));
                
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. ">
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
    // </editor-fold>
}
