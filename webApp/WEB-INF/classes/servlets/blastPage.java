/*
 * blastPage.java
 *
 * Created on July 24, 2002, 12:27 PM
 */

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.io.RandomAccessFile.*;

/**
 *
 * @author  Kevin Horan
 * @version 2.0
 *
 *This servlet prints a blast summary page using information from the database if 
 *the file variable equals summary.  If file is a filename, it prints an excerpt of the blast
 *file pointed to by the file name, and only prints the keys that have been inputed.  
 */
public class blastPage extends HttpServlet {
    final int arab=0, rice=1;
    String tblastnFile=new String("riceCvsArabP");
    String blastpFile=new String("ricePvsArabP");
    String blastRoot=new String("/usr/local/blast/blast2Results/");
    String indexRoot=new String("/usr/local/tomcat/jakarta-tomcat-4.0.4/webapps/databaseWeb/indexFiles/");
    
    /** Initializes the servlet.*/
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    /** Destroys the servlet. */
    public void destroy() {

    }    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods. */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        HttpSession session=request.getSession(false); //a session must already exist
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
	
	if(session==null)
	{
		out.println("no session");
		return ;
	}
        int db;
        boolean isArab;
        List keySet;
        int hid=Integer.parseInt((String)request.getParameter("hid"));
        QueryInfo qi=(QueryInfo)((ArrayList)session.getAttribute("history")).get(hid);
        
        
        String fileName=request.getParameter("file");//can be summary, or a file name to print
        String dbTemp=request.getParameter("db");//database to use
//        List allkeys=(ArrayList)session.getAttribute("keys");
        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Blast Results</title>");
        out.println("</head>");
        out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
        Common.printHeader(out);
        /////////// MAIN /////////////////////////////////////////////////////////////////////
        //check input
        if(qi==null || dbTemp==null)
        {
            out.println("no data or no database entered");
            out.println("</body></html>");
            out.close();
            return;
        }
        try{
            db=Integer.parseInt(dbTemp);
        }catch(NumberFormatException nfe){
            db=arab;
        }
        Common.navLinks(out);
        Common.blastLinks(out,db,hid);
        
        if(qi.dbsLength==2)//both databases
            keySet=qi.getKeySet(db);
        else //just one set of keys
            keySet=qi.getKeySet(0);
        
        if(fileName==null || fileName.compareTo("summary")==0)
            getBlastStats(out, keySet,db);
        else
            printBlastResults(out,keySet,fileName);
        
        //////////////////////////////////////////////////////////////////////////////////////
        out.println("</body>");
        out.println("</html>");

        out.close();
    }
    private void getBlastStats(PrintWriter out,List keys,int db)
    {   /*This method prints the blast statistics for each key in the input array.  
         *It gets these stats from the mySQL database.
         *
         */
        StringBuffer conditions=new StringBuffer();
        List rs;
//        String table, blastQuery, keyTitle;
//        keyTitle=new String("Atnum");
        for(Iterator i=keys.iterator();i.hasNext();)
            conditions.append("Atnum LIKE '"+i.next()+"%' OR ");
        
//        for(int i=0;i<keys.length;i++)//LIKE allows for partial keys to be used
//            conditions.append(keyTitle+" LIKE '"+keys[i]+"%' OR ");
        conditions.append("1=0");//just to end the statement
        rs=Common.sendQuery(buildBlastStatement(conditions.toString(),"blastp",db),11);
        rs.addAll(Common.sendQuery(buildBlastStatement(conditions.toString(),"tblastn",db),11));
        Collections.sort(rs,new blastComparer(db));
        printBlastStats(out, rs);
    }
    private void printBlastStats(PrintWriter out,List rs)
    {
        out.println("<TABLE border='1' align='left' cellpadding='2' cellspacing='0'>");
        //print titles
        out.println("<caption align='left'><font size='4'><strong>"+
            "Blast Results Summary </strong></font></caption>");


        printTableTitles(out);
        for(ListIterator l=rs.listIterator();l.hasNext();)
        {               
            List row=(ArrayList)l.next();
            out.println("\t<TR>");
            out.println("\t\t<TD>"+row.get(0)+"</TD>");
//            out.println("\t\t<TD>hit</TD>");                
            for(int k=1;k<11;k++)
                out.println("\t\t<TD>&nbsp "+row.get(k)+"</TD>");
            out.println("\t</TR>");
        }
        out.println("</TABLE>");
    }
    String buildBlastStatement(String conditions,String type,int db)
    {
        StringBuffer blastQuery=new StringBuffer();
        if(db==arab)
            blastQuery.append("SELECT * FROM Blast_Results.AtagOs_"+type+" WHERE "+conditions+" ORDER BY Atnum");
        else if(db==rice)
            blastQuery.append("SELECT * FROM Blast_Results.OsagAt_"+type+" WHERE "+conditions+" ORDER BY Atnum");
        System.out.println("blastQuery="+blastQuery);
        return blastQuery.toString();
    }
    private void printBlastResults(PrintWriter out,List keys, String file)
    {   /*This method takes an array of keys and a file name which should point to a 
         *blast output file.  It uses a lookup table stored in a file called blastIndex, which can be 
         *built using the blastIndex program.  The method searches the lookup file for the key
         *and grabs the corresponding byte offset for the blast file and prints out the sequence blast results.
         */
         for(int r=0;r<10;r++) //try a max of 10 times before giving up
         {//loop may not be necassary, check more.
             try{
                RandomAccessFile blastFile=new RandomAccessFile(blastRoot+file,"r");
                RandomAccessFile lookupFile=new RandomAccessFile(indexRoot+file+"index","r");
                String blastLine,line,breakline,key;
                long offset;
                blastFile.seek(0);//move to begining of file
                breakline=blastFile.readLine();//use first line of file to break on
                out.println("<PRE>");
                for(int i=0;i<keys.size();i++)
                {
                    //key=(String)((ArrayList)keys.get(db)).get(i);
                    key=(String)keys.get(i);
        //                System.out.println("searching for "+key);
                    while((line=lookupFile.readLine())!=null)
                    {
                        StringTokenizer tok=new StringTokenizer(line);
                        if(tok.nextToken().startsWith(key))//found a matching key in the lookup file
                        {
                            offset=Long.parseLong(tok.nextToken())-1;//grab offset from tokenizer
                            blastFile.seek(offset);//move file pointer to offset byte in file
                            while((blastLine=blastFile.readLine())!=null && blastLine.compareTo(breakline)!=0)
                                out.println(blastLine+"\n");
                            break;
                        }
                    }
                    lookupFile.seek(0);
                    out.println("<HR>");
                }
                out.println("</PRE>");
                blastFile.close();
                lookupFile.close();
                break; //stop loop once file access succeedes
             }catch(IOException e){
                 System.err.println("file error: "+e.getMessage());
                 try{
                    this.wait(2000); //file was being accessed, so wait 2 secondes and try again
                 }catch(InterruptedException ie){
                 }
             }
         }
    }
    private void printTableTitles(PrintWriter out)
    {//prints the titles for the summary table
        out.println("<TR bgcolor='7CD3A8'>");
        out.println("<TH>Query ID</TH>");
//        out.println("<TH>&nbsp</TH>");
        out.println("<TH>Hit ID</TH>");
        out.println("<TH>Description</TH>");
        out.println("<TH>Score</TH>");
        out.println("<TH>E-value</TH>");
        out.println("<TH>Identities</TH>");
        //may be scapped.
        out.println("<TH>Length</TH>");
        out.println("<TH>Positives</TH>");
        out.println("<TH>Gaps</TH>");
        out.println("<TH>Frame</TH>");
        out.println("<TH>Type</TH>");
        out.println("</TR>");
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

class blastComparer implements Comparator
{//expect object to be an List of strings
    public blastComparer(int db)
    {
    }
    public int compare(Object o1,Object o2)
    { //order lists by first string in each list, which should be the key       
        String s1=(String)((ArrayList)o1).get(0);
        String s2=(String)((ArrayList)o2).get(0);
        return s1.compareTo(s2);
    }
}
