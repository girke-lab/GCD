/*
 * DistributorServlet.java
 *
 * Created on August 26, 2004, 3:50 PM
 */

package servlets;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import servlets.scriptInterfaces.*;

/**
 *
 * @author  khoran
 * @version
 */
public class DispatchServlet extends HttpServlet {
    private static Logger log=Logger.getLogger(DispatchServlet.class);
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
        //response.setContentType("text/html");
        //PrintWriter out = response.getWriter();
        HttpSession session=request.getSession(false);
        if(session==null || session.getAttribute("history")==null){
            Common.quit( response.getWriter(), "no session or history");
            return;
        }
        
        String script=request.getParameter("script");        
        String hid_str=request.getParameter("hid");
        String[] range_set=request.getParameterValues("range");
        String range= "custom".equals(range_set[0])? range_set[1] : range_set[0];
                
        if(script==null || hid_str==null || range_set==null){
            Common.quit( response.getWriter(), "must specify 'hid' and 'script' parameters");
            return;
        }
        int hid;
        try{
            hid=Integer.parseInt(hid_str);
            if(hid < 0 || hid >= ((List)session.getAttribute("history")).size()){
                System.out.println("hid "+hid+", is out of bounds");
                return;
            }
        }catch(NumberFormatException e){
            System.out.println("bad hid");
            return;
        }
        
        QueryInfo qi=(QueryInfo)((List)session.getAttribute("history")).get(hid);
        List ids=getIds(qi.getSearch().getResults(),range);
//        System.out.println("ids are: "+ids);
        if(ids==null){
             response.getWriter().println("no id numbers");            
            return;
        }
        Script scriptRunner=getScript(script);
        if(scriptRunner==null){
             response.getWriter().println("invalid script name");
            return;
        } 
        response.setContentType(scriptRunner.getContentType());
        scriptRunner.run(response.getOutputStream(), ids);
        
    
    }
    private Script getScript(String script)
    {
        if(script.equals("displayKeys.pl"))
            return new DisplayKeysScript();
        else if(script.equals("goSlimCounts"))
            return new GoSlimCountsScript();
        else if(script.equals("multigene.pl"))
            return new MultigeneScript();
        else if(script.equals("chrplot.pl"))
            return new ChrPlotScript();
        else if(script.equals("alignToHmm"))
            return new AlignToHmmScript();
        else if(script.equals("unknownsText"))
            return new UnknownsTextScript();
        return null;
    }
        
    private List getIds(List seq_ids,String range)
    {//take a list of indexes and retrive seq_ids/cluster_ids from qi
        //range should be of the form "a-b,c-d,...,x-y"
//        System.out.println("range="+range);
        List ids=new LinkedList();
        StringTokenizer tok=new StringTokenizer(range,",");
        int s,e;
        while(tok.hasMoreTokens())
        {
            try{
                String[] r=tok.nextToken().split("-");
                if(r==null || r.length==0)
                    continue;               
                s=Integer.parseInt(r[0]);
                if(s < 0 || s >= seq_ids.size())
                    continue;
                if(r.length==1)
                    ids.add(seq_ids.get(s));
                else 
                {
                    e=Integer.parseInt(r[1]);
                    ids.addAll(seq_ids.subList(s, e > seq_ids.size()? seq_ids.size() : e));
                }                
            }catch(NumberFormatException ex){
                System.out.println("NAN: "+ex.getMessage());
            }
        }
        return ids;        
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
