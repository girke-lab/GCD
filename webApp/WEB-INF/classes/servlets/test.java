/*
 * cancelQuery.java
 *
 * Created on August 27, 2002, 1:30 PM
 */
package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.net.*;
/**
 *
 * @author  khoran
 * @version
 */
public class test extends HttpServlet {
    
    queryThread dbConnection=new queryThread("dbc",10);
    HttpSession session;
    java.io.PrintWriter out;
    
    /** Initializes the servlet. */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
       
    }
    
    /** Destroys the servlet.  */
    public void destroy() {
        
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        response.setContentType("text/html");
        session=request.getSession();
        out = response.getWriter();
        String status=request.getParameter("status");
        
        /////////////////////////////////////
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        out.println("</head>");
        out.println("<body>");
        ////////////////////////////////////////
        
        System.out.println("status="+status);
        
        out.println("<H3>Processing request...</H3><P>");
        out.println("<FORM ACTION='/databaseWeb/cancelQuery'>"+
                    "<INPUT type=hidden name='status' value='closed'>"+
                    "<INPUT type=submit value='Cancel Query'>"+
                    "</FORM>");
        
        
        if(status.compareTo("open")==0)         
            session.setAttribute("QueryStatus",new Boolean(true));
        else if(status.compareTo("closed")==0)
            session.setAttribute("QueryStatus",new Boolean(false));
        
        
        
        ////////////////////////////////////////
        out.println("</body>");
        out.println("</html>");
        ///////////////////////////////////////// 
        out.close();
    }
   
    
    
    private void test()
    {
         String q=new String("select Promoter_1500.Atnum, MIPS_UTRs.MIPS_Description"+
                    " from Promoter_1500, MIPS_UTRs where Promoter_1500.Atnum="+
                    "MIPS_UTRs.Atnum limit 300;");
        String q2=new String("SELECT Promoter_1500.Atnum, MIPS_UTRs.MIPS_Description, Intergenic.DNA, Arabi_all_proteins.Protein, '', Hyperlinks.MIPS FROM Promoter_1500 LEFT JOIN TIGR_cDNA_UTRs ON Promoter_1500.Atnum=TIGR_cDNA_UTRs.Atnum LEFT JOIN MIPS_UTRs ON Promoter_1500.Atnum=MIPS_UTRs.Atnum LEFT JOIN Arabi_all_proteins ON Promoter_1500.Atnum=Arabi_all_proteins.Atnum LEFT JOIN Hyperlinks ON Promoter_1500.Atnum=Hyperlinks.Atnum LEFT JOIN Intergenic ON Promoter_1500.Atnum=Intergenic.Atnum WHERE Promoter_1500.Atnum LIKE 'At1g010%' OR  0=1  limit 5;");

        dbConnection.setQuery(q,2);
        dbConnection.start();

        for(int i=0;i<10;i++)
            System.out.println("  test3  ");

        while(dbConnection.isAlive()); //wait for dbConnection to finish    


        List data=dbConnection.getResults();
        ListIterator l=data.listIterator();
        while(l.hasNext())
            out.println(l.next()+"<BR>");
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
