/*
 * ResultSummaryServlet.java
 *
 * Created on February 19, 2003, 2:22 PM
 */

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
/**
 *
 * @author  Kevin Horan
 * @version 1.0
 */

public class QueryPageServlet extends HttpServlet 
{
    final int feildCount=17;//values used to initialize arrays
    final int dbCount=2;
    final int arab=0,rice=1; //database names
    final int MAXKEYS=1000; //maximum number of results that can be returned 
                            //per database query
    long ID=0;//id number used to identify query
    
    String[] fullNames;//names to use in querys
    String[] printNames;//names to print on screen
    String[] dbPrintNames;
    String[] dbRealNames;
    
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        defineNames();
    }    
    public void destroy()
    {    }    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException
    {
        HttpSession session = request.getSession(true);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        int[] dbNums=new int[dbCount];
        int dbNumsLength,limit,hid; //hid is history id      
        List inputKeys;
        /////////////////////////  get input  //////////////////////////////////////////////////
        String input=request.getParameter("inputKey"); //actual input from form feild
        String searchType=request.getParameter("searchType");//d for descrition, k for key     
        String[] dbTemp=request.getParameterValues("dbs"); //list of databases to use

        try{//test limit
            limit=Integer.parseInt(request.getParameter("limit"));
            if(limit>=MAXKEYS || limit==0) //cap limit at MAXKEYS
                limit=MAXKEYS;
        }catch(NumberFormatException nfe){
            limit=10; //default limit
        }try{//test dbNums for valid input and conver text to numbers   // DBfeilds
            for(int i=0;i<dbTemp.length;i++)
                dbNums[i]=Integer.parseInt(dbTemp[i]);
            dbNumsLength=dbTemp.length;
        }catch(Exception e){
            dbNums[0]=0;    dbNums[1]=1;
            dbNumsLength=2;
        }
        if(input==null || input.length()==0)
        {
            out.println("no data entered");
            out.println("</body></html>");
            out.close();
            return;
        }
        else
        { //put keys in a list, rather then have to parse the sting every time
            inputKeys=new ArrayList();
            StringTokenizer tok=new StringTokenizer(input);
            while(tok.hasMoreTokens())
                inputKeys.add(tok.nextToken());
        }
        if(searchType==null)
            searchType=new String("k");        
        
        if(session.getAttribute("hid")==null)
        {//session was just created.
            session.setAttribute("hid",new Integer(0));
            session.setAttribute("history",new ArrayList());
        }
        hid=((Integer)session.getAttribute("hid")).intValue();
        session.setAttribute("hid",new Integer(hid+1));
        QueryInfo qi=new QueryInfo(dbNums,dbNumsLength,limit);
        ((ArrayList)session.getAttribute("history")).add(qi);
        
//        session.setAttribute("dbs",dbNums);  
//        session.setAttribute("dbsLength",new Integer(dbNumsLength));
//        session.setAttribute("limit",new Integer(limit)); 

        ////////////////////////// HTML headers  ////////////////////////////////////////////
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        Common.javaScript(out);
        out.println("</head>");
        out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
        Common.printHeader(out);
        Common.navLinks(out);
        Common.printForm(out,hid);
        /////////////////////////// main   ////////////////////////////////////////////////////
        List main=null,keysNotFound=null;
        List actualKeys=inputKeys,returnedKeys;
//        session.setAttribute("keys",new ArrayList());
        for(int i=0;i<dbNumsLength;i++)
        {
            out.println("<P><H3 align='center'>"+dbPrintNames[dbNums[i]]+" search results:</H3>");
            if(searchType.charAt(0)=='d') //descritption search
                returnedKeys=searchByDescription(inputKeys,limit,dbNums[i]);
            else 
            {
                returnedKeys=searchByKey(inputKeys,limit,dbNums[i]);
                //keysNotFound=findMismatches(inputKeys, main,dbNums[i]);
            }
            
//            returnedKeys=getKeysReturned(main); //gets the actual keys returned by the DB
             //these should be Seq_id numbers, not accession numbers.
            qi.addKeySet(returnedKeys);
  
            main=getData(returnedKeys,limit,dbNums[i]);
            //Common.blastLinks(out,dbNums[i],hid);
            printSummary(out,main,dbNums[i],hid);
            if(keysNotFound!=null)
                printMismatches(out,keysNotFound);
        }
        out.println("</body>");
        out.println("</html>");

        out.close();
        /////////////////////////////////  end of main  ////////////////////////////////////////////
    }
    private List getKeysReturned(List data)
    {
        List keys=new ArrayList();
        for(Iterator i=data.iterator();i.hasNext();)
            keys.add( ((ArrayList)i.next()).get(2));//get key from data  //use 2 for now, but we will need to change this later
        return keys;
    }
    private List searchByDescription(List input,int limit,int db)
    {
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs;
        int wasOp=1;
       
        while(in.hasNext())
        { //create conditions string
            String temp=(String)in.next();//use temp becuase sucsesive calls to nextToken
                                                    //advace the pointer
            if(temp.compareToIgnoreCase("and")!=0 && temp.compareToIgnoreCase("or")!=0 
                    && temp.compareToIgnoreCase("not")!=0 && temp.compareTo("(")!=0
                    && temp.compareTo(")")!=0)
            //no keywords or parinths
            {
                if(wasOp==0)//last token was not an operator, but we must have an operator between every word
                    conditions.append(" and ");
                conditions.append(regExpression(temp,db));
                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),1);
        return rs;
    }
    private List searchByKey(List input,int limit,int db)
    {//takes a string of keys to search for, and the fields to return        
        //returns a list of data from database
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        while(in.hasNext() && count++ < limit) //build condtions
            conditions.append(likeExpression((String)in.next(),db));
        conditions.append(" 0=1 ");                
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),1);
        return rs;
    }
    private List getData(List input, int limit, int db)
    {
        ListIterator it=input.listIterator();
        StringBuffer condition=new StringBuffer();
        List rs=null;
        int count=0;
        
        while(it.hasNext() && count++ < limit)
            conditions.append(Seq_idExpression((String)in.next())); 
        conditions.append("0=1");
        rs=Common.sendQuery(buildKeyStatement(conditions.toString(),limit,db),4);
        return rs;
    }
    private void printSummary(PrintWriter out,List data,int currentDB,int hid)
    {
        ListIterator li=data.listIterator();
        List row;
        String key;
	String clusterNum;
	StringBuffer keyList=new StringBuffer();
        int count=0;
        String[] colors=new String[2];
        colors[0]=new String("29F599"); //00aa00
        colors[1]=new String("26F5CC"); //00cc00

        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/multigene.pl'>\n");
        out.println("<INPUT type='submit' value='All Gene Structures'><BR>\n");
	for(Iterator i=data.iterator();i.hasNext();)
            out.println("<INPUT type=hidden name='accession' value='"+((ArrayList)i.next()).get(0)+"'/>\n");
        //	    keyList.append("accession="+((ArrayList)i.next()).get(0)+"&");        
        out.println("</FORM>");
  
        
        out.println("<TABLE align='center' border='0'>");
//	out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/multigene.pl?"+keyList+"'>All Gene Strucures</a>");
        while(li.hasNext())
        {
            row=(ArrayList)li.next();
            
            //do this for both databases now
            key=(String)row.get(0);
            clusterNum=(String)row.get(3);
            out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Links</TH>");
            printArabLinks(out,key,clusterNum,hid,currentDB);
            out.println("</TR>");

            out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Key</TH>");
            out.println("<TD>"+row.get(0)+"</TD></TR>");
/*            
            if(currentDB==arab)
            {
                key=(String)row.get(0);
                //key=key.substring(0,key.length()-2);//cut off decimal
		clusterNum=(String)row.get(2);
                out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Links</TH>");
		printArabLinks(out,key,clusterNum,hid);
                out.println("</TR>");
            }
            if(currentDB==arab)
            {
                out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Key</TH>");
                out.println("<TD>"+row.get(0)+"</TD></TR>");
            }
            else if(currentDB==rice)
            {
                out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Id 1</TH>");
                out.println("<TD>"+row.get(0)+"</TD></TR>");
                out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Id 2</TH>");
                out.println("<TD>"+row.get(1)+"</TD></TR>");
            }                
 */
            out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Description</TH>");
//            if(currentDB==arab)
                out.println("<TD>"+row.get(1)+"</TD></TR>");
//            else if(currentDB==rice)
//                out.println("<TD>"+row.get(2)+"</TD></TR>");
            out.println("<TR><TD colspan='2'>&nbsp</TD></TR>");
            
        }
        out.println("</TABLE>");                    
    }
    private void printArabLinks(PrintWriter out,String key,String clusterNum,int hid,int currentDB)
    {
         String db=null;
         System.out.println("currentDB="+currentDB);
    	 if(currentDB==arab)
             db="ath1";
         else if(currentDB==rice)
             db="osa1";
    	 out.println("\t\t<TD>");
    	 out.println("<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+ key+"'>MIPS</a>&nbsp&nbsp");                 
         out.println("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+ "ORF_infopage.cgi?db="+db+"&orf="+key+"'>TIGR</a>&nbsp&nbsp");
	 out.println("<a href='/databaseWeb/ClusterServlet?hid="+hid+"&clusterID="+clusterNum+"'>Cluster "+clusterNum+"</a>&nbsp&nbsp");
//we need the cluster size here as well, since we don't want to print alignment or tree links for singletons         
	 out.println("<a href='http://bioinfo.ucr.edu/projects/PlantFam/FinalAlignments/"+clusterNum+".html'>Alignments</a>&nbsp&nbsp");
	 out.println("<a href='http://bioinfo.ucr.edu/projects/PlantFam/FinalAlignments/"+clusterNum+".png'>Trees</a>&nbsp&nbsp");
	 out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/geneview.pl?accession="+key+"'>GeneStructure*</a>&nbsp&nbsp");
	 out.println("<a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?PF'>PFAM</a>&nbsp&nbsp");
	 //expression link goes here or here
	 out.println("<a href='http://signal.salk.edu/cgi-bin/tdnaexpress?GENE="+key+"&FUNCTION=&JOB=HITT&DNA=&INTERVAL=10'>KO</a>&nbsp&nbsp");
	 out.println("<a href='http://www.geneontology.org/doc/index.shtml#downloads'>GO</a>&nbsp&nbsp");
         //does this link work for rice?
	 out.println("<a href='http://www.genome.ad.jp/dbget-bin/www_bget?ath:"+key+"'>KEGG</a>&nbsp&nbsp");
	 out.println("\t\t</TD>");
                

    }
    private void printMismatches(PrintWriter out,List keys)
    {
        if(keys.size()==0) //don't print anything if there are no missing keys
            return;
        out.println("Keys not returned:");
        for(int i=0;i<keys.size();i++)
        {
            if(i!=0)
                out.println(", ");
            out.println(keys.get(i));
        }
    }            
    private List findMismatches(List inputKey, List main,int currentDB)
    {
        List mismatches=new ArrayList();
        ListIterator inputs=inputKey.listIterator();
        int index=0;
        String input, result1=null,result2=null;
        int loopCount=0,length;
        length=main.size();
        if(length==0)
        {//no keys at all were found, so add everything to the mismatch list
            while(inputs.hasNext())
            {
                input=(String)inputs.next();
                if((currentDB==arab && input.startsWith("At")) ||
                   (currentDB==rice && !input.startsWith("At")) )
                    mismatches.add(inputs.next());
            }
            return mismatches;
        }        
        while(inputs.hasNext())
        {
            loopCount=0;
            input=(String)inputs.next();
            if(currentDB==arab && !input.startsWith("At"))//don't report rice keys as missing
                continue;
            if(currentDB==rice && input.startsWith("At"))//skip arab keys for rice db
                continue;                

//            System.out.println("start: comparing "+input+" with "+result1+" on loop "+loopCount);
//            System.out.println("length="+length);
            do
            {//search through all of reslts to find input, and loop the restuls list
                if(loopCount >=length)
                {//we searched the whole list but did not find any match
                    mismatches.add(input);
                    break;
                }
                loopCount++;
                result1=(String)((ArrayList)main.get(index%length)).get(0);
                if(currentDB==rice)
                    result2=(String)((ArrayList)main.get(index%length)).get(1);                
                index++;
//                System.out.println("inside: comparing "+input+" with "+result1+" on loop "+loopCount);
            }while(!result1.toLowerCase().startsWith(input.toLowerCase()) &&
                   !(currentDB==rice && result2.toLowerCase().startsWith(input.toLowerCase()) ));
//            System.out.println("end2: comparing "+input+" with "+result1+" on loop "+loopCount);
        }
        
//        System.out.print("mismatched keys: ");
//        for(Iterator i=mismatches.iterator();i.hasNext();)
//            System.out.print(i.next()+",");
//        System.out.println("");
        return mismatches;
    }            
    ////////////////////////////  Query stuff    ////////////////////////////////////////////////
    
/*    
    private String buildDescStatement(String conditions,int currentDB)
    {//this should no longer be used
        String desc=null;
        desc=new String("SELECT "+fullNames[0]+" FROM Id_Associations LEFT JOIN "+
                        "Sequences USING(Seq_id)  WHERE ");
        
        if(currentDB==arab)
            desc+=" Genome='arab' and ";
        else if(currentDB==rice)
            desc+=" Genome='rice' and ";
        desc+=conditions;
       
//        if(currentDB==arab)  //select TIGR_Data.Atnum from TIGR_Data where ...
//            desc=new String("SELECT TIGR_Data.Atnum FROM TIGR_Data WHERE "+ conditions);
//        else if(currentDB==rice)
//            desc=new String("SELECT Rice.Rice_Data.Id1  FROM Rice.Rice_Data WHERE "+ conditions);
//        else
//            System.err.println("invalid DB name in buildDescStatement");
//        
        System.out.println("description query: "+desc);
        return desc;      
    }
*/
    private String buildIdStatement(String conditions, int limit,int currentDB)
    {
        String id="SELECT Sequences.Seq_id from Sequences LEFT JOIN Id_Associations USING(Seq_id) "+
                  "WHERE ";
        if(currentDB==arab)
            id+=" Genome='arab' and ";
        else if(currentDB==rice)
            id+=" Genome='rice' and ";
        id+="("+conditions+")";
        System.out.println("id query: "+id);   
        return id;
    }
    private String buildKeyStatement(String conditions,int limit, int currentDB)
    {
        StringBuffer general=new StringBuffer();
                                           //Accession                               //description
        general.append("SELECT DISTINCT Sequences.Primary_Key, "+fullNames[1]+",Sequences.Seq_id, Clusters.Cluster_id "+
                       "FROM  Clusters LEFT JOIN Sequences USING(Seq_id) LEFT JOIN Id_Associations USING(Seq_id) "+                       
                       //"FROM Id_Associations, Sequences, Clusters "+
                       //"WHERE Sequences.Seq_id=Id_Associations.Seq_id AND Sequences.Seq_id=Clusters.Seq_id AND ");                        
                       "WHERE ");
        
        if(currentDB==arab)
            general.append(" Genome='arab' and ");
        else if(currentDB==rice)
            general.append(" Genome='rice' and ");        
        
        general.append("( "+conditions+" ) ORDER BY "+fullNames[0]);
        /*
        if(currentDB==arab)  //ID is a global varibale used to kill the query at a later time
            general.append("SELECT TIGR_Data.Atnum,TIGR_Data.Description, Clusters.ClusterNum"+
                " FROM TIGR_Data LEFT JOIN Clusters on TIGR_Data.Atnum=Clusters.Atnum"+
		" WHERE "+conditions+" ORDER BY Atnum");
        else if(currentDB==rice)
            general.append("SELECT Rice.Rice_Data.Id1, Rice.Rice_Data.Id2,Rice.Rice_Data.Description"+
                " FROM Rice.Rice_Data WHERE "+conditions+" ORDER BY Id1");
        else
            System.err.println("invalid DB name in buildGeneralStatement");
        */
        general.append(" limit "+limit);
        System.out.println("general Query: "+general);
        return general.toString();
    }
    private String Seq_idExpression(String id)
    {
        return "Sequences.Seq_id="+id+" OR ";
    }
    private String regExpression(String key,int currentDB)
    {
        return " ( "+fullNames[1]+" REGEXP \""+key+"\") ";
        /*
        if(currentDB==arab) //TIGR_Data.Description REGEXP ...
            return " ( TIGR_Data.Description REGEXP \""+key+"\") ";
        else if(currentDB==rice)
            return " ( Rice.Rice_Data.Description REGEXP \""+key+"\") ";
        else
            System.err.println("invalid DB name in regExpression");
        return null;
         */
    }
    private String likeExpression(String key,int currentDB)
    {
        String exp=null;
        exp=new String(fullNames[0]+" LIKE '"+key+"' OR ");
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
    
    private void defineNames()
    {
        //assign names for later lookup
        fullNames=new String[feildCount];
        //names for old database setup
//        fullNames[0]="TIGR_Data.Atnum";fullNames[1]="TIGR_Data.Description";fullNames[2]="TIGR_Data.Promoter";
//        fullNames[3]="TIGR_Data.TU";fullNames[4]="TIGR_Data.5UTR";fullNames[5]="Rice.Rice_Data.Intergenic";
//        fullNames[6]="TIGR_Data.ORF";fullNames[7]="Rice.Rice_Data.Promoter";fullNames[8]="TIGR_Data.3UTR";
//        fullNames[9]="TIGR_Data.Protein";fullNames[10]="Rice.Rice_Data.Id1";fullNames[11]="Rice.Rice_Data.Id2";
//        fullNames[12]="Rice.Rice_Data.Description";fullNames[13]="Rice.Rice_Data.Protein";fullNames[14]="Rice.Rice_Data.CDS";
//        fullNames[15]="TIGR_Data.Intergenic";fullNames[16]="Rice.Rice_Data.TU";
        fullNames[0]="Id_Associations.Accession";fullNames[1]="Sequences.Description";fullNames[2]="Sequences.Intergenic";
        fullNames[3]="Models.TU";fullNames[4]="Models.5UTR";fullNames[5]="Sequences.Intergenic";
        fullNames[6]="Models.CDS";fullNames[7]="Sequences.Intergenic";fullNames[8]="Models.3UTR";
        fullNames[9]="Models.Protein";fullNames[10]="Id_Associations.Accession";fullNames[11]="Id_Associations.OS_id";
        fullNames[12]="Sequences.Description";fullNames[13]="Models.Protein";fullNames[14]="Models.CDS";
        fullNames[15]="Sequences.Intergenic";fullNames[16]="Models.TU";

        //names to be printed on the screen
        printNames=new String[feildCount];
        printNames[0]="Key";printNames[1]="Description" ;printNames[2]="Promoter 1500";printNames[3]="Transcription Model";
        printNames[4]="5`UTR";printNames[5]="Intergenic";printNames[6]="ORF";printNames[7]="Promoter_1500";
        printNames[8]="3`UTR";printNames[9]="Protein";printNames[10]="ID 1 (TIGR)";printNames[11]="ID 2 (TIGR)";
        printNames[12]="Description";printNames[13]="Protein";printNames[14]="CDS";printNames[15]="Intergenic Region";
        printNames[16]="TU";
        //names for databases
        dbPrintNames=new String[dbCount];
        dbPrintNames[0]="Arabidopsis"; dbPrintNames[1]="Rice";
        //actual database names
        dbRealNames=new String[dbCount];
        dbRealNames[0]="Cis_Regul"; dbRealNames[1]="Rice";
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
   
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
