/*
 * ClusterServlet.java
 *
 * Created on March 12, 2003, 2:31 PM
 */

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author  khoran
 * @version
 */
public class ClusterServlet extends HttpServlet {
    
    
     final int arab=0, rice=1;
    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);        
    }    
    /** Destroys the servlet.
     */
    public void destroy()
    { }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException 
    {
        HttpSession session=request.getSession(false); //a session must already exist
        response.setContentType("text/html");
//        java.io.PrintWriter out = response.getWriter();
        if(session==null)
        {
//            out.println("no session");
            return;
        }
        
        String cid=request.getParameter("clusterID");        
//        int[] dbNums=(int[])session.getAttribute("dbs");        
//        int dbNumsLength=((Integer)session.getAttribute("dbsLength")).intValue();
        int hid=Integer.parseInt((String)request.getParameter("hid"));
        QueryInfo qi=(QueryInfo)((ArrayList)session.getAttribute("history")).get(hid);
        List data;
	RequestDispatcher dispatcher=null;
        
        if(cid==null || cid=="")
        {
 //           out.println("No data");
            return;
        }            
////////////////////////////////////////////////////////////////////////////////
   //     out.println("<html>");
   //     out.println("<head>");
   //     out.println("<title>Clusters</title>");
   //     Common.javaScript(out);
   //     out.println("</head>");
   //     out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");     
   //     Common.printHeader(out);
   //     Common.navLinks(out);
////////////////////////////////////////////////////////////////////////////////         
        for(int i=0;i<1  /*qi.dbsLength*/  ;i++)
        {//temporarily use only arab because rice query is still emtpy
            data=getClusters(cid,qi.dbNums[i]);
     //       Common.printList(out,data);
	    dispatcher=getServletContext().getRequestDispatcher(getQueryURL(data));
	    if(dispatcher==null){
//	    	out.println("no dispatcher");
		return;
	    }
	    dispatcher.forward(request,response);
            //printClusters(out,data);
        }        
//        out.println("</body>");
//        out.println("</html>");
//        out.close();
    }
    
    private List getClusters(String clusterID, int currentDB)
    {
        return Common.sendQuery(clusterQuery(clusterID,currentDB),3);
    }
    private String getQueryURL(List data)
    {
	StringBuffer queryURL=new StringBuffer();
	queryURL.append("/index.jsp?limit=0&input=");
	ArrayList row;
	for(Iterator i=data.iterator();i.hasNext();)
	{
		row=(ArrayList)i.next();
		queryURL.append(row.get(1));
		if(i.hasNext())
			queryURL.append("+");
	}
	return queryURL.toString();
    }
    private void printClusters(PrintWriter out,List data)
    {
        ArrayList row;
        if(data==null || data.size()==0)
            return;
        out.println("<TABLE align='center' bgcolor='00bb00' border='0'>");
        out.println("<TR><TH colspan='2'>Cluster Number: "+((ArrayList)data.get(0)).get(0)+"</TH></TR>"+
            "<TR><TH align='left'>Arabidopsis Number</TH><TH align='left'>Description</TH></TR>");
        for(int i=0;i<data.size();i++)
        {
            row=(ArrayList)data.get(i);
            out.println("<TR><TD>&nbsp "+row.get(1)+"</TD><TD>&nbsp "+row.get(2)+
                "</TD></TR>");
        }
        out.println("</TABLE>");
    }
///////////////////////  query stuff  //////////////////////////////////////////    
    private String clusterQuery(String id,int currentDB)
    {
        String query="";
        query=new String("SELECT Clusters.Cluster_id, Sequences.Primary_Key, Sequences.Description "+
                         "FROM Clusters LEFT JOIN Sequences USING(Seq_id) "+
                         //"FROM Sequences, Clusters "+
                         //"WHERE Sequences.Seq_id=Clusters.Seq_id AND Clusters.Cluster_id='"+id+"'");
                         "WHERE Clusters.Cluster_id='"+id+"'");
        
//        if(currentDB==arab)
//            query=new String("Select Clusters.ClusterNum,Clusters.Atnum,"+
//                "TIGR_Data.Description FROM TIGR_Data LEFT JOIN Clusters "+
//                "USING (Atnum) WHERE Clusters.ClusterNum='"+id+"'");
//        else if(currentDB==rice)
//            ;
        System.out.println("Cluster query is "+query);
        return query;            
    }
/////////////////////////////  auto code ///////////////////////////////////////    
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
