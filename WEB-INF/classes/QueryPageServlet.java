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
        HashMap goNumbers=null;
        Search s=null;
//        session.setAttribute("keys",new ArrayList());
        for(int i=0;i<dbNumsLength;i++)
        {
            out.println("<P><H3 align='center'>"+dbPrintNames[dbNums[i]]+" search results:</H3>");
            
            s=getSearchObj(searchType);
            s.init(inputKeys,limit, dbNums[i]);
            returnedKeys=s.getResults();
//            
//            if(searchType.charAt(0)=='d') //descritption search
//                returnedKeys=searchByDescription(inputKeys,limit,dbNums[i]);
//            else if(searchType.charAt(0)=='c')
//                returnedKeys=searchByClusterNum(inputKeys,limit,dbNums[i]);
//            else 
//            {
//                returnedKeys=searchByKey(inputKeys,limit,dbNums[i]);
//                //TODO: fix findMismatches
//                //keysNotFound=findMismatches(inputKeys, main,dbNums[i]);
//            }
            
             //these should be Seq_id numbers, not accession numbers.
            qi.addKeySet(returnedKeys);
            
            goNumbers=findGoNumbers(returnedKeys);
              
            main=getData(returnedKeys,limit,dbNums[i]);
            //Common.blastLinks(out,dbNums[i],hid);
            printSummary(out,main,goNumbers,dbNums[i],hid);
            if(keysNotFound!=null)
                printMismatches(out,keysNotFound);
        }
        out.println("</body>");
        out.println("</html>");

        out.close();
        /////////////////////////////////  end of main  ////////////////////////////////////////////
    }
    
    private Search getSearchObj(String type)
    {
        Search s=null;
        if(type=="Description")
            return new DescriptionSearch();
        else if(type=="Id")
            return new IdSearch();
        else if(type=="Cluster Id")
            return new ClusterIDSearch();
        else if(type=="Cluster Name")
            return new ClusterNameSearch();
        else if(type=="Go Number")
            return new GoSearch();
        else
            return null;   
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
        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)        
            al.add(((ArrayList)i.next()).get(0));
        return al;
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

        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)        
            al.add(((ArrayList)i.next()).get(0));
        return al;
    }
    private List searchByClusterNum(List input, int limit, int db)
    {
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;
        
        while(in.hasNext() && count++< limit)
            conditions.append(clusterExpression((String)in.next()));
        conditions.append("0=1");
        rs=Common.sendQuery(buildClusterStatement(conditions.toString(),limit,db),1);
        
        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)
            al.add(((ArrayList)i.next()).get(0));
        return al;
    }
    private HashMap findGoNumbers(List data)
    {//query the go numbers from the GO table and organize them in a hashMap.
        StringBuffer conditions=new StringBuffer();
        System.out.println("data="+data);
        for(Iterator i=data.iterator();i.hasNext();)
            conditions.append("Seq_id="+(String)i.next()+" OR ");
        conditions.append("0=1");

        System.out.println("sending query: "+buildGoStatement(conditions.toString()));
        List results=Common.sendQuery(buildGoStatement(conditions.toString()),2); 
        if(results.size()==0)
            System.out.println("no results");
        
        //create one entry in a hashMap for each set of Seq_id's
        HashMap gos=new HashMap();
        
        for(Iterator rit=results.iterator();rit.hasNext();)
        {
            ArrayList row=(ArrayList)rit.next();
            
            ArrayList goSet=(ArrayList)gos.get(row.get(0)); //look up the Seq_id's array
            if(goSet==null)//no array found
            {//create a new array and add it to the hash
                goSet=new ArrayList();
                goSet.add(row.get(1)); //add one go number to array
                gos.put(row.get(0), goSet); 
            } 
            else //an array was found for this seq_id, so add this go number to it
                goSet.add(row.get(1));//add go number to array            
        }
        if(gos==null || gos.size()==0)
            System.out.println("nothing in gos");
        else
            System.out.println(gos.size()+" elements in go");
        return gos;
    }
    private List getData(List input, int limit, int db)
    { 
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;
        
        for(Iterator it=input.iterator();it.hasNext() && count++ < limit;)
            conditions.append(Seq_idExpression((String)it.next())); 
        conditions.append("0=1");

        rs=Common.sendQuery(buildKeyStatement(conditions.toString(),limit,db),5);
        return rs;
    }
    private void printSummary(PrintWriter out,List data,HashMap goNumbers,int currentDB,int hid)
    {
        ListIterator li=data.listIterator();
        List row;
        String key;
	String clusterNum="",clusterSize="";
	StringBuffer keyList=new StringBuffer();
        int count=0;
        String[] colors=new String[2];
        colors[0]=new String("29F599"); //00aa00
        colors[1]=new String("26F5CC"); //00cc00

        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/multigene.pl'>\n");
        out.println("<INPUT type='submit' value='All Gene Structures'><BR>\n");
	for(Iterator i=data.iterator();i.hasNext();)
            out.println("<INPUT type=hidden name='accession' value='"+((ArrayList)i.next()).get(0)+"'/>\n");
        out.println("</FORM>");
//http://bioinfo.ucr.edu/cgi-bin/chrplot.pl?database=all&accession=At1g18690.1
        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/chrplot.pl'>\n");
        out.println("<INPUT type=hidden name='database' value='all'/>");
        out.println("<INPUT type='submit' value='Chr Map'><BR>\n");
	for(Iterator i=data.iterator();i.hasNext();)
            out.println("<INPUT type=hidden name='accession' value='"+((ArrayList)i.next()).get(0)+"'/>\n");
        out.println("</FORM>");
  
        
        out.println("<TABLE align='center' border='0'>");
        while(li.hasNext())
        {
            row=(ArrayList)li.next();
            
            //do this for both databases now
            key=(String)row.get(0);
            if(row.get(3)!=null && row.get(2)!=null)
            {
                clusterNum=(String)row.get(2);
                clusterSize=(String)row.get(3);
            }
            out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Links</TH>");
            printLinks(out,key,clusterNum,hid,currentDB,clusterSize,(String)row.get(4),goNumbers);
            out.println("</TR>");

            out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Key</TH>");
            out.println("<TD><A href='http://bioinfo.ucr.edu/cgi-bin/seqview.pl?database=all&accession="+row.get(0)+"'>"+row.get(0)+"</A></TD></TR>");
            out.println("<TR bgcolor='"+colors[count++%2]+"'><TH align='left'>Description</TH>");
            out.println("<TD>"+row.get(1)+"</TD></TR>");
            out.println("<TR><TD colspan='2'>&nbsp</TD></TR>");
        }
        out.println("</TABLE>");                    
    }
    private void printLinks(PrintWriter out,String key,String clusterNum,int hid,int currentDB,String size,String Seq_id,HashMap goNumbers)
    {//size is cluster size
         String db=null;
         System.out.println("currentDB="+currentDB);
    	 if(currentDB==arab)
             db="ath1";
         else if(currentDB==rice)
             db="osa1";
    	 out.println("\t\t<TD>");
         if(currentDB==arab)
            out.println("<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+ key+"'>MIPS</a>&nbsp&nbsp");                 
         out.println("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+ "ORF_infopage.cgi?db="+db+"&orf="+key+"'>TIGR</a>&nbsp&nbsp");
         if(size!="")
            out.println("<a href='/databaseWeb/ClusterServlet?hid="+hid+"&clusterID="+clusterNum+"'>Cl "+clusterNum+"("+size+")</a>&nbsp&nbsp");
         if(!size.equals("1") && size!= "")
         {
             out.println("<a href='http://bioinfo.ucr.edu/projects/PlantFam/FinalAlignments/"+clusterNum+".html'>Alignments</a>&nbsp&nbsp");
             out.println("<a href='http://bioinfo.ucr.edu/projects/PlantFam/FinalAlignments/"+clusterNum+".png'>Trees</a>&nbsp&nbsp");
         }
	 out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/geneview.pl?accession="+key+"'>GeneStructure*</a>&nbsp&nbsp");
	 out.println("<a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?PF'>PFAM</a>&nbsp&nbsp");
	 //expression link goes here
         if(currentDB==arab)
            out.println("<a href='http://signal.salk.edu/cgi-bin/tdnaexpress?GENE="+key+"&FUNCTION=&JOB=HITT&DNA=&INTERVAL=10'>KO</a>&nbsp&nbsp");
         
         //here we want an array of go numbers         
         StringBuffer querys=new StringBuffer();
         ArrayList gos=(ArrayList)goNumbers.get(Seq_id);
         if(gos!=null) //gos may be null if this Seq_id does not have any GO numbers
           for(Iterator i=gos.iterator();i.hasNext();)
                 querys.append("query="+((String)i.next()).replaceFirst(":","%3A")+"&"); //the : must be encoded     
         if(querys.length()!=0)//we have at least one go number
            out.println("<a href='http://www.godatabase.org/cgi-bin/go.cgi?depth=0&advanced_query=&search_constraint=terms&"+querys+"action=replace_tree'>GO</a>&nbsp&nbsp");
         
//         out.println("<a href='http://www.godatabase.org/cgi-bin/go.cgi?view=query&action=query&search_constraint=terms&"+querys+"'>GO</a>&nbsp&nbsp");

         
         //does this link work for rice? no
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
        String id="SELECT DISTINCT Sequences.Seq_id from Sequences LEFT JOIN Id_Associations USING(Seq_id) "+
                  "WHERE ";
        if(currentDB==arab)
            id+=" Genome='arab' and ";
        else if(currentDB==rice)
            id+=" Genome='rice' and ";
        id+="("+conditions+")";
        id+=" limit "+limit;
        System.out.println("id query: "+id);   
        return id;
    }
    private String buildClusterStatement(String conditions, int limit, int currentDB)
    {
        String q="SELECT Sequences.Seq_id from Sequences LEFT JOIN Clusters USING(Seq_id) "+
                 "WHERE ";
        if(currentDB==arab)
            q+=" Genome='arab' and ";
        else if(currentDB==rice)
            q+=" Genome='rice' and ";
        q+="("+conditions+")";
        System.out.println("cluster query is:"+q);
        
        return q;
    }
    private String buildKeyStatement(String conditions,int limit, int currentDB)
    {
        StringBuffer general=new StringBuffer();
                                        
        general.append("SELECT DISTINCT Primary_Key, Description, Clusters.Cluster_id, Size, Sequences.Seq_id "+
                       "FROM Sequences LEFT JOIN Clusters USING(Seq_id) LEFT JOIN Cluster_Counts USING(Cluster_id) "+                                             
                       "WHERE ");
        
        if(currentDB==arab)
            general.append(" Genome='arab' and ");
        else if(currentDB==rice)
            general.append(" Genome='rice' and ");        
        
        general.append("( "+conditions+" ) ORDER BY Primary_Key");
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
    private String buildGoStatement(String conditions)
    {
        return "SELECT Seq_id, Go from Go WHERE "+conditions +" ORDER BY Seq_id";
    }
    private String clusterExpression(String id)
    {
        return "Clusters.Cluster_id="+id+" OR ";
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
