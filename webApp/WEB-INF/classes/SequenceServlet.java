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
    final int LINE_SIZE=1000; //number of base pairs to print on a line
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
//        for(int i=0;i<qi.dbsLength;i++)
//        {
//            System.out.println("i="+i);
//            out.println("<P><H3 align='center'>"+dbPrintNames[qi.dbNums[i]]+" search results:</H3>");
            keySet=qi.getKeySet(0); //keySet will be a list of Seq_id numbers, not Accession numbers
            main=searchByKey(keySet,qi.limit,qi.dbNums[0],fieldNums,fieldsLength);
            //Common.printList(out,main);
            //Common.blastLinks(out,qi.dbNums[i],hid);
            out.println("Models found: "+main.size());
            printFasta(out,main,qi.dbNums[0],fieldNums,length,format);            
//        }
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
        int fieldCount=3; //we add accession, model, and description
        int count=0; //used to limit number of keys actually sent to database
        
        currentDB=0;//we no longer need to distiguish between databases, so just use 0
        
        feildCombo.append(fullNames[currentDB][0]);  //accession
        feildCombo.append(", "+fullNames[currentDB][1]); //model number
        feildCombo.append(", "+fullNames[currentDB][2]); //description
        for(int i=0;i<fieldsLength;i++)
            if(fullNames[currentDB][fields[i]].length()!=0)
            {
                feildCombo.append(", "+fullNames[currentDB][fields[i]]);
                fieldCount++;
            }

        feildCombo.append(", Genome"); //always query genome so we know where to put titles
        fieldCount++;
        //StringTokenizer in=new StringTokenizer(inputKey);
        ListIterator in=keys.listIterator();
        
        conditions.append("Sequences.Seq_id in (");
        while(in.hasNext() && count++ < limit)
        {
            conditions.append("'"+in.next()+"'");
            if(in.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");
        
        rs=Common.sendQuery(buildGeneralStatement(feildCombo.toString(),conditions.toString(),limit,currentDB),fieldCount);
        return rs;   
    }       
    private void printFasta(PrintWriter out,List rs,int currentDB,int[] currentFeildNums,int length,int format)
    {
        
        StringBuffer fastaOutput=new StringBuffer();//gets send to blast script
        StringBuffer record=new StringBuffer(); //these get sent to screen
        StringBuffer standard=new StringBuffer();
        StringBuffer temp=new StringBuffer();
        String key,key2,desc,data, tigrDb="ath1";
        int start;
        int lastDB=-1;
        if(rs==null || rs.size()==0)
            return;
        
        int fieldsLength=((ArrayList)rs.get(0)).size()-1; //last entry is genome data, don't print it.
        try{
//            out.println("<FORM METHOD='POST' ACTION='http://138.23.191.152/blast/blastSearch.cgi'>");
//            out.println("<INPUT type='submit' value='Blast it'><BR>");
            System.out.println("size of rs is "+((ArrayList)rs.get(0)).size());
            standard.append("<TABLE align='center'>");
            
            for(ListIterator l=rs.listIterator();l.hasNext();)
            {
                List row=(ArrayList)l.next();
                key=(String)row.get(0); //accession number
                key2=(String)row.get(1); //model accession number
                desc=(String)row.get(2); //description
                start=3;
                
                currentDB=Common.getDBid((String)row.get(row.size()-1)); //genome is always last entry
                if(lastDB!=currentDB)//db has now changed to a new db
                {
                    tigrDb="ath1";
                    if(currentDB==rice) 
                        tigrDb="osa1";
                    standard.append("<TR><TH colspan='2'><H2 align='left'>"+dbPrintNames[currentDB]+" search results:</H2></TH></TR>");
                }
                lastDB=currentDB;
  
                int index=0;                
                for(int f=start;f<fieldsLength;f++)
                {   
                    data=(String)row.get(f);                          
//                    if(fullNames[currentDB][currentFeildNums[index]].length()==0)     //rice should always have utrs now 
//                        index++; //3UTR and 5UTR are empty, so skip them 
                    if(data==null || data.compareTo("")==0 )//|| currentFeildNums[index]==3)
                    {
                        index++;
                        continue;                                        
                    }
                    if(key2.startsWith(key))
                        record.append(">"+key2+" "+desc+": "+printNames[currentFeildNums[index]]+"\n");
                    else
                        record.append(">"+key2+" "+key+" "+desc+": "+printNames[currentFeildNums[index]]+"\n");
                    
                    if(currentFeildNums[index]==4){ //deal with the promoter
                        if(data.length() > 3000) //only trim if it is greater than 3000
                            data=data.substring(0,3000).toUpperCase(); //trim the intergenic to 3000 
                    }
                    else if(currentFeildNums[index]==3) //dont uppercase the TU
                        ; //dont change data at all                    
                    else if(currentFeildNums[index]==9)//dont trim the protein  
                        data=data.toUpperCase();
                    else if(length > 0 && length < data.length())  //trim output feilds to length
                        data=data.substring(0,length).toUpperCase();
                    else if(length < 0 && (-1*length) < data.length())
                        //length is negative, so adding it to data.lenth moves back from the end
                        data=data.substring(data.length()+length,data.length()).toUpperCase();
                    else //length==0
                        data=data.toUpperCase();
                    
                    //insert some spaces into data, so that the text is wrapped
                    if(format==STANDARD)
                    {
                        StringBuffer temp2=new StringBuffer(data);
                        for(int j=LINE_SIZE;j<temp2.length();j+=LINE_SIZE)
                            temp2.insert(j,' ');
                        data=temp2.toString();
                    }
                  
                    temp.append("\t<TR bgcolor='"+colors[currentFeildNums[index]]+"'>"+
                        "<TH align='left'>"+printNames[currentFeildNums[index]]+"</TH>"+
                        "<TD>"+data+"</TD></TR>\n");
                    record.append(data+"\n");
                    index++;
                }
                fastaOutput.append(record);                   
                standard.append("<FORM method=post action='http://138.23.191.152/blast/blastSearch.cgi'>"+
                    "<INPUT type=hidden name='input' value='"+record+"'>\n");
                standard.append("\t<TR bgcolor='"+colors[0]+"'><TH>Links</TH>"+
                    "<TD><a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+key+
                    "'>MIPS</a>&nbsp&nbsp"+
                    "<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+
                    "ORF_infopage.cgi?db="+tigrDb+"&orf="+key+"'>TIGR</a></TD></TR>");      
                standard.append("\t<TR bgcolor='"+colors[0]+"'><TH align='left'>Accession</TH><TD>"+
                    "<A href='http://bioinfo.ucr.edu/cgi-bin/seqview.pl?db=all&accession="+key+"'>"+key+"</A>"+
                    "&nbsp&nbsp<INPUT type=submit value='Blast it'></TD></TR>"+
                    "\t<TR bgcolor='"+colors[1]+"'><TH align='left'>Model Accession</TH><TD>"+key2+"</TD></TR>"+
                    "\t<TR bgcolor='"+colors[2]+"'><TH align='left'>Description</TH><TD>"+desc+"</TD></TR>\n");
                standard.append(temp);
                standard.append("<TR><TD colspan='2'>&nbsp</TD></TR></FORM>\n");
                record.setLength(0); //erase string
                temp.setLength(0);
            }
            standard.append("</TABLE>\n");
            if(format==STANDARD)
                out.println(standard);
            else if(format==FASTA)
                out.println("<PRE>"+fastaOutput.toString()+"</PRE>");
//            out.println("<INPUT type=hidden name='input' value='"+fastaOutput.toString()+"'>");
//            out.println("</FORM>");
        }catch(NullPointerException npe){
            System.out.println("null pointer in fasta: "+npe.getMessage());
            npe.printStackTrace();
        }
    }
///////////////////////////  Query stuff  ///////////////////////////////////////////////////////    
    private String buildGeneralStatement(String feilds, String conditions,int limit,int currentDB)
    {
        StringBuffer general=new StringBuffer();
        general.append("SELECT "+feilds+" FROM Sequences "+
                       " LEFT JOIN Models USING(Seq_id) WHERE "+conditions+
                       " ORDER BY Genome, "+fullNames[0][0]);
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
        exp=new String("Sequences.Seq_id ='"+key+"' OR ");
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
        fullNames[0][0]="Sequences.Primary_Key";fullNames[0][1]="Models.Model_accession";fullNames[0][2]="Sequences.Description";
        fullNames[0][3]="Models.TU";fullNames[0][4]="Sequences.Intergenic";fullNames[0][5]="Models.UTR3";
        fullNames[0][6]="Sequences.Intergenic";fullNames[0][7]="Models.CDS";fullNames[0][8]="Models.UTR5";
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
        colors[0]="AAAAAA";colors[1]="AAAAAA";colors[2]="AAAAAA";colors[3]="D3D3D3";colors[4]="D3D3D3";
        colors[5]="D3D3D3";colors[6]="D3D3D3";colors[7]="D3D3D3";colors[8]="D3D3D3";colors[9]="D3D3D3";
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
