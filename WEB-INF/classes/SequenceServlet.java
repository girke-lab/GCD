/*
 * SequenceServlet.java
 *
 * Created on February 24, 2003, 12:26 PM
 */

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author  khoran
 * @version
 */
import java.io.*;
import java.util.*;
public class SequenceServlet extends HttpServlet 
{
    final int fieldCount=10;    
    final int dbCount=2;
    final int STANDARD=0, FASTA=1;
    final int arab=0,rice=1; //database names
    final int MAXKEYS=1000; //maximum number of results that can be returned 
                            //per database query
    long ID=0;
    String[][] fullNames;//names to use in querys
    String[] printNames;//names to print on screen
    String[] dbPrintNames;
    String[] dbRealNames;
    String[] colors;
    
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config);
        defineNames();
    }   
    public void destroy() 
    {
    }
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        HttpSession session=request.getSession(false); //a session must already exist
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter();
        
        int hid=Integer.parseInt((String)request.getParameter("hid"));
        QueryInfo qi=(QueryInfo)((ArrayList)session.getAttribute("history")).get(hid);

        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        Common.javaScript(out);
        out.println("</head>");
        out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
        Common.printHeader(out);
        Common.navLinks(out);
        Common.printForm(out,hid);
////////////////////////////////////////////////////////////////////////////////////////////////
        if(session==null)
        {
            out.println("no session");
            exit(out);
        }
        
        int[] fieldNums=new int[fieldCount];       
        int fieldsLength=0;
        int length,format;
        
//        List keys=(ArrayList)session.getAttribute("keys");
//        int[] dbNums=(int[])session.getAttribute("dbs");        
//        int dbNumsLength=((Integer)session.getAttribute("dbsLength")).intValue();
//        int limit=((Integer)session.getAttribute("limit")).intValue();
        
        //get the list of feilds from the web page
        String[] temp1=request.getParameterValues("fields");//all fields                
        try{//test feildNums, feildLength    
            int i;
            for(i=0;i<temp1.length;i++)
                fieldNums[i]=Integer.parseInt(temp1[i]);           
            fieldsLength=i;
        }catch(Exception e){
        }try{//test length
            length=Integer.parseInt(request.getParameter("length"));
        }catch(NumberFormatException nfe){
            length=0;// really means everyting
        }try{ //test format, if not defined, set to STANDARD
            format=Integer.parseInt(request.getParameter("format"));
        }catch(Exception e){
            format=STANDARD;
        }
        
        ///////////////////////  main /////////////////////////////////////////////////////////
        List keySet,main;
        for(int i=0;i<qi.dbsLength;i++)
        {
            System.out.println("i="+i);
            out.println("<P><H3 align='center'>"+dbPrintNames[qi.dbNums[i]]+" search results:</H3>");
            keySet=qi.getKeySet(i);
//            keySet=(ArrayList)keys.get(i);
            main=searchByKey(keySet,qi.limit,qi.dbNums[i],fieldNums,fieldsLength);
            Common.blastLinks(out,qi.dbNums[i],hid);
            printFasta(out,main,qi.dbNums[i],fieldNums,length,format);            
//            Common.printList(out,main);
            
        }
        exit(out);    
    }

    private void exit(PrintWriter out)
    {
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
    private List searchByKey(List keys,int limit,int currentDB,int[] fields,int fieldsLength)
    {   //takes a List of keys to search for, and the fields to return
        //returns a string of actual keys returned from database
        StringBuffer conditions=new StringBuffer();
        StringBuffer feildCombo=new StringBuffer();            
        List rs=null;
        int fieldCount=2; //we add id and description, and maybe Id2
        int count=0; //used to limit number of keys actually sent to database
        
        //always add the key field, then, if we have the rice DB, add the second key field
        //then append the description field
        feildCombo.append(fullNames[currentDB][0]);
        if(currentDB==rice)
        {
            feildCombo.append(", "+fullNames[currentDB][1]);
            fieldCount++;
        }
        feildCombo.append(", "+fullNames[currentDB][2]);
        for(int i=0;i<fieldsLength;i++)
            if(fullNames[currentDB][fields[i]].length()!=0)
            {
                feildCombo.append(", "+fullNames[currentDB][fields[i]]);
                fieldCount++;
            }

        //StringTokenizer in=new StringTokenizer(inputKey);
        ListIterator in=keys.listIterator();
        while(in.hasNext() && count++ < limit)            
            conditions.append(likeExpression((String)in.next(),currentDB));
        conditions.append(" 0=1 ");        
        rs=Common.sendQuery(buildGeneralStatement(feildCombo.toString(),conditions.toString(),limit,currentDB),fieldCount);
        return rs;   
    }       
    private void printFasta(PrintWriter out,List rs,int currentDB,int[] currentFeildNums,int length,int format)
    {
        /* The rs List is a two dimensional array which contains the id fields first, then the description, and then
                 * the selected fields.  However, rice has 2 id's, and arab has only one, so we must find out
                 * what the proper offset is to the selected fields.  This is done with the start variable.
                 */
        StringBuffer fastaOutput=new StringBuffer();//gets send to blast script
        StringBuffer record=new StringBuffer(); //these get sent to screen
        StringBuffer standard=new StringBuffer();
        String key,key2,desc,data;
        int start;
        if(rs==null || rs.size()==0)
            return;
        int fieldsLength=((ArrayList)rs.get(0)).size();
        try{
            out.println("<FORM METHOD='POST' ACTION='http://138.23.191.152/blast/blastSearch.cgi'>");
            out.println("<INPUT type='submit' value='Blast it'><BR>");
            System.out.println("size of rs is "+((ArrayList)rs.get(0)).size());
            standard.append("<TABLE align='center'>");
            for(ListIterator l=rs.listIterator();l.hasNext();)
            {
                List row=(ArrayList)l.next();
                key=(String)row.get(0);
                if(currentDB==rice)
                {
                    key2=(String)row.get(1);
                    desc=(String)row.get(2);
                    start=3;
                }
                else
                {
                    key2=new String("&nbsp");
                    desc=(String)row.get(1);
                    start=2;
                }
                if(currentDB==arab)
                    standard.append("\t<TR bgcolor='"+colors[0]+"'><TH>Links</TH>"+
                        "<TD><a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+key.substring(0,key.length()-2)+
                        "'>MIPS</a>&nbsp&nbsp"+
                        "<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+
                        "ORF_infopage.cgi?db=ath1&orf="+key.substring(0,key.length()-2)+"'>TIGR</a></TD></TR>");      
                standard.append("\t<TR bgcolor='"+colors[0]+"'><TH align='left'>Id 1</TH><TD>"+key+"</TD></TR>"+
                    "\t<TR bgcolor='"+colors[1]+"'><TH align='left'>Id 2</TH><TD>"+key2+"</TD></TR>"+
                    "\t<TR bgcolor='"+colors[2]+"'><TH align='left'>Description</TH><TD>"+desc+"</TD></TR>\n");

                int index=0;                
                for(int f=start;f<fieldsLength;f++)
                {   
                    data=(String)row.get(f);      
                    if(fullNames[currentDB][currentFeildNums[index]].length()==0)
                        index++; //3UTR and 5UTR are empyt, so skip them 
                    if(data==null || data.compareTo("")==0 )//|| currentFeildNums[index]==3)
                    {
                        index++;
                        continue;                                        
                    }
                    record.append("&gt "+key+" "+key2+" "+desc+": "+printNames[currentFeildNums[index]]+"\n");
                    fastaOutput.append(record.toString());
                    record.setLength(0);
                    
                    if(currentFeildNums[index]==9)   //trim output feilds to length
                        record.append(data.toUpperCase()+"\n");
                    else if(length > 0 && length < data.length())
                        record.append(data.substring(0,length).toUpperCase()+"\n");
                    else if(length < 0 && (-1*length) < data.length())
                        //length is negative, so adding it to data.lenth moves back from the end
                        record.append(data.substring(data.length()+length,data.length()).toUpperCase()+"\n");
                    else //length==0
                        record.append(data.toUpperCase()+"\n");
                    
                    standard.append("\t<TR bgcolor='"+colors[currentFeildNums[index]]+"'>"+
                        "<TH align='left'>"+printNames[currentFeildNums[index]]+"</TH>"+
                        "<TD>"+record.toString()+"</TD></TR>\n");
                    fastaOutput.append(record.toString());
                    record.setLength(0); //erase string
                    index++;
                }
                standard.append("<TR><TD colspan='2'>&nbsp</TD></TR>\n");
            }
            standard.append("</TABLE>\n");
            if(format==STANDARD)
                out.println(standard);
            else if(format==FASTA)
                out.println("<PRE>"+fastaOutput.toString()+"</PRE>");
            out.println("<INPUT type=hidden name='input' value='"+fastaOutput.toString()+"'>");
            out.println("</FORM>");
        }catch(NullPointerException npe){
            System.out.println("null pointer in fasta: "+npe.getMessage());
            npe.printStackTrace();
        }
    }
///////////////////////////  Query stuff  ///////////////////////////////////////////////////////    
    private String buildGeneralStatement(String feilds, String conditions,int limit,int currentDB)
    {
        StringBuffer general=new StringBuffer();
        general.append("SELECT "+feilds+" FROM Id_Associations LEFT JOIN Sequences "+
                       "USING(Seq_id) LEFT JOIN Models USING(Seq_id) WHERE "+conditions+
                       " ORDER BY "+fullNames[0][0]);
//        if(currentDB==arab)  //ID is a global varibale used to kill the query at a later time
//            general.append("/*"+ID+"*/SELECT "+feilds+" FROM TIGR_Data WHERE "+conditions+" ORDER BY Atnum");
//        else if(currentDB==rice)
//            general.append("/*"+ID+"*/SELECT "+feilds+" FROM Rice.Rice_Data WHERE "+conditions+" ORDER BY Id1");
//        else
//            System.err.println("invalid DB name in buildGeneralStatement");
        general.append(" limit "+limit);
        System.out.println("general Query: "+general);
        return general.toString();
    }
    private String likeExpression(String key,int currentDB)
    {
        String exp=null;
        exp=new String(fullNames[0][0]+" LIKE '"+key+"%' OR ");
        /*
        if(currentDB==arab)  //TIGR_Data.Atnum
            exp=new String("TIGR_Data.Atnum LIKE '"+key+"%' OR ");
        else if(currentDB==rice)
            exp=new String("Rice.Rice_Data.Id1 LIKE '"+key+"%' OR "+
                           "Rice.Rice_Data.Id2 LIKE '"+key+"%' OR ");
        else
            System.err.println("invalid DB name in likeExpression");
         */
        return exp;
    }
///////////////////////////////////////////////////////////////////////////////////////////////    
    private void defineNames()
    {
        //assign names for later lookup
        fullNames=new String[dbCount][fieldCount];        
//        fullNames[0][0]="TIGR_Data.Atnum";fullNames[0][1]="";fullNames[0][2]="TIGR_Data.Description";
//        fullNames[0][3]="TIGR_Data.TU";fullNames[0][4]="TIGR_Data.Promoter";fullNames[0][5]="TIGR_Data.3UTR";
//        fullNames[0][6]="TIGR_Data.Intergenic";fullNames[0][7]="TIGR_Data.ORF";fullNames[0][8]="TIGR_Data.5UTR";
//        fullNames[0][9]="TIGR_Data.Protein";
//        fullNames[1][0]="Rice.Rice_Data.Id1";fullNames[1][1]="Rice.Rice_Data.Id2";fullNames[1][2]="Rice.Rice_Data.Description";
//        fullNames[1][3]="Rice.Rice_Data.TU";fullNames[1][4]="Rice.Rice_Data.Promoter";fullNames[1][5]="";
//        fullNames[1][6]="Rice.Rice_Data.Intergenic";fullNames[1][7]="Rice.Rice_Data.CDS";fullNames[1][8]="";
//        fullNames[1][9]="Rice.Rice_Data.Protein";
        fullNames[0][0]="Id_Associations.Accession";fullNames[0][1]="Id_Associations.OS_id";fullNames[0][2]="Sequences.Description";
        fullNames[0][3]="Models.TU";fullNames[0][4]="Sequences.Intergenic";fullNames[0][5]="Models.3UTR";
        fullNames[0][6]="Sequences.Intergenic";fullNames[0][7]="Models.CDS";fullNames[0][8]="Models.5UTR";
        fullNames[0][9]="Models.Protein";
        //these should no longer be used
        fullNames[1][0]="Id_Associations.Accession";fullNames[1][1]="Rice.Rice_Data.Id2";fullNames[1][2]="Rice.Rice_Data.Description";
        fullNames[1][3]="Rice.Rice_Data.TU";fullNames[1][4]="Rice.Rice_Data.Promoter";fullNames[1][5]="";
        fullNames[1][6]="Rice.Rice_Data.Intergenic";fullNames[1][7]="Rice.Rice_Data.CDS";fullNames[1][8]="";
        fullNames[1][9]="Rice.Rice_Data.Protein";
        
        //names to be printed on the screen
        printNames=new String[fieldCount];
        printNames[0]="Id 1";printNames[1]="Id 2";printNames[2]="Description";printNames[3]="Transcription Model";
        printNames[4]="Promoter 1500";printNames[5]="3`UTR";printNames[6]="Intergenic";printNames[7]="CDS";
        printNames[8]="5`UTR";printNames[9]="Protein";
        
        //colors for each feilds for printing on web page
        colors=new String[fieldCount];
        colors[0]="29F599";colors[1]="29F599";colors[2]="29F599";colors[3]="26F5CC";colors[4]="26F5CC";
        colors[5]="26F5CC";colors[6]="26F5CC";colors[7]="26F5CC";colors[8]="26F5CC";colors[9]="26F5CC";
        //names for databases
        dbPrintNames=new String[dbCount];
        dbPrintNames[0]="Arabidopsis"; dbPrintNames[1]="Rice";
        //actual database names
        dbRealNames=new String[dbCount];
        dbRealNames[0]="Cis_Regul"; dbRealNames[1]="Rice";
    }
//////////////////////  auto code  //////////////////////////////////////////////////////////////    
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
