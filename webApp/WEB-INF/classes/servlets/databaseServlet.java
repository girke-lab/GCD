/*
 * databaseServlet.java
 *
 * Created on June 25, 2002, 3:16 PM
 */

//package databaseWeb;
package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author  Kevin Horan
 * @version 2.3
 *added: limit on number of keys that can be queryed
 *added: query now runs on a seperate thread using the queryThread class
 *modified: buildGeneralQuery now only includes tables necassary for the feilds selected
 *modified: switched to new database, so all buildQuery functions were rewritten
 *added: statistics tables and supporting functions.  very similar to the orthologs
 *added: pdb links
 *added: check for kesy searched for but not found
 *modified: move PrintWriter out to local scope, global does not work
 *added: cluster ids
 */

public class databaseServlet extends HttpServlet {
//    Connection con;
//    Statement Query;//general purpose querys
//    Statement Query2;
    HttpSession session;
    int currentDB; //stores the name of the current database //TODO: this is a problem!
    String[] fullNames;//names to use in querys
    String[] printNames;//names to print on screen
    String[] dbPrintNames;
    String[] dbRealNames;
    int[] currentFeildNums; //stores list of feilds to use for current database
    int length; //length to clip dna strings to
    int currentFeildLength;//stores number of feilds used for current database
    long ID=0;//id number used to identify query
    
    //queryThread dbConnection=new queryThread("dbc");
    
    final int feildCount=17;//values used to initialize arrays
    final int dbCount=2;
    final int arab=0,rice=1; //database names
    final int MAXKEYS=1000; //maximum number of results that can be returned 
                            //per database query
    
    /** Initializes the servlet. */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        defineNames();//set values for fullNames[] and printNames[]
        currentFeildNums=new int[feildCount];
        System.out.println("databaseServlet started: "+new GregorianCalendar().getTime());
    }
  
    /** Destroys the servlet. */
    public void destroy()
    {
    }    
    /** Processes requests for both HTTP GET and POST methods.*/   
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, java.io.IOException {
        ////////////   init variables and grab values from web form ////////////////////////////       
        response.setContentType("text/html");
        session=request.getSession(true); //session used to cancel query, if necassary        
        session.setAttribute("QueryStatus",new Boolean(true));
        session.setAttribute("idList",new ArrayList());
        PrintWriter out = response.getWriter();
        
        ID++; //increment the ID number once for each user
        
        List logs=null, main=null, statsList=null,pdbStats=null,keysNotFound=null;
        List clusters=null;
        int[][] feildNums=new int[dbCount][feildCount];
        int[] feildLength=new int[dbCount];
        int[] dbNums=new int[dbCount];
        int dbNumsLength;
        int limit;       
        
        
        String inputKey=request.getParameter("inputKey"); //actual input from form feild
        String searchType=request.getParameter("searchType");//d for descrition, k for key     
        String orthologs=request.getParameter("orthologs");//t for true, otherwise no orthologs
        String stats=request.getParameter("stats"); //t for true, otherwise no stats
        String format=request.getParameter("format");//n for normal print, f for fasta format print
        String[] temp1=request.getParameterValues("feilds");//arab feilds
        String[] temp2=request.getParameterValues("Rfeilds"); //rice feilds
        String[] dbTemp=request.getParameterValues("dbs"); //list of databases to use
        
        ////////////////////////   check the input   ////////////////////////////////////////////
        try{//test limit
            limit=Integer.parseInt(request.getParameter("limit"));
            if(limit>=MAXKEYS || limit==0) //cap limit at MAXKEYS
                limit=MAXKEYS;
        }catch(NumberFormatException nfe){
            limit=10; //default limit
        }try{//test length
            length=Integer.parseInt(request.getParameter("length"));
        }catch(NumberFormatException nfe){
            length=0;// really means everyting
        }try{//test feildNums, feildLength    // ARAB feilds
            for(int i=0;i<temp1.length;i++)
                feildNums[arab][i]=Integer.parseInt(temp1[i]); //[0] is the arab set of  feilds
            feildLength[arab]=temp1.length;
        }catch(Exception e){
            feildNums[arab][0]=0; feildNums[arab][1]=1;
            feildLength[arab]=2;
        }try{//test RfeildNums, RfeildLength    // RICE feilds
            for(int i=0;i<temp2.length;i++)
                feildNums[rice][i]=Integer.parseInt(temp2[i]); //[1] is the rice set of feilds
            feildLength[rice]=temp2.length;
        }catch(Exception e){            
            feildNums[rice][0]=10; feildNums[rice][1]=12;
            feildLength[rice]=2;
        }try{//test dbNums for valid input and conver text to numbers   // DBfeilds
            for(int i=0;i<dbTemp.length;i++)
                dbNums[i]=Integer.parseInt(dbTemp[i]);
            dbNumsLength=dbTemp.length;
        }catch(Exception e){
            dbNums[0]=0;    dbNums[1]=1;
            dbNumsLength=2;
        }
        if(inputKey==null || inputKey.length()==0)
        {
            out.println("no data entered");
            out.println("</body></html>");
            out.close();
            return;
        }
        if(searchType==null)
            searchType=new String("key");
        if(orthologs==null)
            orthologs=new String("f");
        if(stats==null)
            stats=new String("f");
        if(format==null)
            format=new String("n");

           
        //////////////////////////  HTML  stuff   //////////////////////////////////////////////        
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Search Results</title>");
        out.println("</head>");
        out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
        printHeader(out);
        navLinks(out);
        ///////////////////////////////  MAIN ////////////////////////////////////////////////        
        String keys=inputKey;
        StringBuffer allkeys=new StringBuffer();
        for(int i=0;i<dbNumsLength;i++)
        {   //execute a query for each database in list
            out.println("<P><H3 align='center'>"+dbPrintNames[dbNums[i]]+" search results:</H3>");
            //set up the database specific stuff
            currentDB=dbNums[i];   //currentDB is only a number, for faster comparisons
            for(int f=0;f<feildLength[dbNums[i]];f++)//dbNums[i] reprsent the number of the current database
                currentFeildNums[f]=feildNums[dbNums[i]][f];//copy the feildnums from db i to currentFeildNums array
            currentFeildLength=feildLength[dbNums[i]];
    
            if(searchType.charAt(0)=='d')
            { //description search
                keys=searchByDescription(inputKey,limit); //cannot return more then 'limit' keys
                if(orthologs.charAt(0)=='t' && format.charAt(0)=='n') //we only use the logs if not printing if fasta format
                    logs=getOrthologs(keys,limit);
            }
            else if(i==0 && orthologs.charAt(0)=='t' && format.charAt(0)=='n')//first iteration only
                logs=getOrthologs(keys,limit); //only query ortholgs once because keys are not changing from one database to another (does all keys at once)
            if(stats.charAt(0)=='t' && currentDB==arab && format.charAt(0)=='n')
                statsList=getStats(keys,limit);
            if(currentDB==arab && format.charAt(0)=='n')
            {
                pdbStats=getPDB(keys,limit);
                clusters=getClusters(keys,limit);
            }
            main=searchByKey(keys,limit);         
            keysNotFound=findMismatches(keys, main);
            for(ListIterator l=main.listIterator();l.hasNext();)
                allkeys.append(((ArrayList)l.next()).get(0)+" ");
            blastLinks(out, allkeys.toString());
            
            if(format.charAt(0)=='n') //normal format
                printData(out,main,logs,statsList,pdbStats,keysNotFound,clusters); //TODO: change this so we have fewer arguments
            else //fastA format
                printFasta(out, main,keysNotFound);
        }
        
        out.println("<FORM NAME=dform >\n<INPUT type=hidden name='done' value='true'>"+
                "</FORM>");//form used by javascript to determine when the data has been returned
        
        //////////////////////////////////////////////////////////////////////////////////////        
        out.println("</body>");
        out.println("</html>");
         
        out.close();
    }
    
    private void blastLinks(PrintWriter out, String inputKey)
    {   //inputKey is a list of keys only, no words
        //print links to blastp page and tblastn page
        StringBuffer URLprefix=new StringBuffer();
        StringTokenizer keys=new StringTokenizer(inputKey);
        URLprefix.append(" <A href='/databaseWeb/blastPage?");
        out.println("<TABLE width='50%' align='center'><TR>");
        while(keys.hasMoreTokens())
        {
            URLprefix.append("inputKey="+keys.nextToken()+"&");
        }//then add file name as last argument and print at the same time
        if(currentDB==arab)
        {//if using the arab database, send links to arab blast files
            URLprefix.append("db=Cis_Regul&");
            out.println("<TD>"+URLprefix+"file=summary'>Blast Summary</A></TD>");
            out.println("<TD>"+URLprefix+"file=riceCvsArabP'>tBlastn file</A></TD>");     
            out.println("<TD>"+URLprefix+"file=ricePvsArabP'>Blastp file</A></TD>");
        }
        else 
        {//otherwise use rice blast files
            URLprefix.append("db=Rice&");
            out.println("<TD>"+URLprefix+"file=summary'>Blast Summary</A></TD>");
            out.println("<TD>"+URLprefix+"file=ArabPvsRiceP'>tBlastn file</A></TD>");     
            out.println("<TD>"+URLprefix+"file=ArabCvsRiceP'>Blastp file</A></TD>");
        }
        out.println("</TR></TABLE>");
    }
    private void navLinks(PrintWriter out)
    {
        out.println("<TABLLE width='50%' align='right'><TR>");
        out.println("<TD><A href='http://faculty.ucr.edu/~tgirke'>Home</A></TD>");
        out.println("<TD><A href='http://138.23.191.152:/blast/blast.html'>UCR Blast Page</A></TD>");
        out.println("</TR></TABLE>");
    }
    private List getOrthologs(String inputKey,int limit)
    {
        List logs=null;
        int count=0;
        StringBuffer logConditions=new StringBuffer();
        StringTokenizer in=new StringTokenizer(inputKey);
        while(in.hasMoreTokens() && count++ < limit)//limit the number of actual keys sent, but cannot account for wildcards
            logConditions.append("Blast_Results.HitList.Atnum LIKE '"+in.nextToken()+"%' OR ");
        logConditions.append(" 0=1");//or'ing with false does not change expression
        logConditions.append(" ORDER BY Atnum ");
        logConditions.append(" limit "+limit*5); //each key can have up to 5 orthlogs
        logs=sendQuery(buildOrthologStatement(logConditions.toString()),2);
        return logs;
    }
    private List getStats(String inputKey, int limit)
    {
        List stats=null;
        int count=0;
        StringBuffer conditions=new StringBuffer();
        StringTokenizer in=new StringTokenizer(inputKey);
        while(in.hasMoreTokens() && count++ < limit)
            conditions.append("TIGR_left_UTR_stats.Atnum LIKE '"+in.nextToken()+"%' OR ");
        conditions.append(" 0=1");
        conditions.append(" limit "+limit);
        stats=sendQuery(buildStatStatement(conditions.toString()),10); //10 feilds returned
        return stats;
    }
    private List getPDB(String inputKey,int limit)
    {
        List stats=null;
        int count=0;
        StringBuffer conditions=new StringBuffer();
        StringTokenizer in=new StringTokenizer(inputKey);
        while(in.hasMoreTokens() && count++ < limit)
            conditions.append("PDB.Atnum LIKE '"+in.nextToken()+"%' OR ");
        conditions.append(" 0=1");
        conditions.append(" ORDER BY Atnum ");
        conditions.append(" limit "+limit);
        stats=sendQuery(buildPDBStatement(conditions.toString()),3); //3 feilds returned
        return stats;
    }
    private List getClusters(String inputKey, int limit)
    {
        List clusters=null;
        int count=0;
        StringBuffer conditions=new StringBuffer();
        StringTokenizer in=new StringTokenizer(inputKey);
        while(in.hasMoreTokens() && count++ < limit)
            conditions.append("Clusters.Atnum LIKE '"+in.nextToken()+"%' OR ");
        conditions.append(" 0=1");
        conditions.append(" ORDER BY Atnum");
        conditions.append(" limit "+limit);
        System.out.println("conditions are: "+conditions);
        clusters=sendQuery(buildClusterStatement(conditions.toString()),6);
        return clusters;
    }
    private List findMismatches(String inputKey, List main)
    {
        List mismatches=new ArrayList();
        StringTokenizer inputs=new StringTokenizer(inputKey);
        int index=0;
        String input, result;
        int loopCount=0,length;
        length=main.size();
        if(length==0)
        {//no keys at all were found, so add everything to the mismatch list
            while(inputs.hasMoreTokens())
                mismatches.add(inputs.nextToken());
            return mismatches;
        }
        while(inputs.hasMoreTokens())
        {
            loopCount=0;
            input=inputs.nextToken();
            if(currentDB==arab && !input.startsWith("At"))//don't report rice keys as missing
                continue;
            if(currentDB==rice && input.startsWith("At"))//skip arab keys for rice db
                continue;
                
            result=(String)((ArrayList)main.get(index++%length)).get(0);
//            System.out.println("start: comparing "+input+" with "+result+" on loop "+loopCount);
            while(!result.toLowerCase().startsWith(input.toLowerCase()) )
            {//search through all of reslts to find input, and loop the restuls list
                loopCount++;
                result=(String)((ArrayList)main.get(index++%length)).get(0);
                if(loopCount >=length)
                {//we searched the whole list but did not find any match
                    mismatches.add(input);
                    break;
                }
//                System.out.println("inside: comparing "+input+" with "+result+" on loop "+loopCount);
            }
//            System.out.println("end2: comparing "+input+" with "+result+" on loop "+loopCount);
        }
        
//        System.out.print("mismatched keys: ");
//        for(Iterator i=mismatches.iterator();i.hasNext();)
//            System.out.print(i.next()+",");
//        System.out.println("");
        return mismatches;
    }            
    private String searchByDescription(String inputKey,int limit)
    {   /*this method takes an input string consisting of keywords, boolean operators, and parithasis
                    all seperated by white space.  It then queries the database for a list of keys that match 
                    the specified keywords.  This list is then sent to the searchByKeys method to get further 
                    data about the key
                */
        StringTokenizer in=new StringTokenizer(inputKey);
        StringBuffer conditions=new StringBuffer();
        StringBuffer keys=new StringBuffer();
        int wasOp=1;
        List rs;
       
        while(in.hasMoreTokens())
        { //create conditions string
            String temp=new String(in.nextToken());//use temp becuase sucsesive calls to nextToken
                                                    //advace the pointer
            if(temp.compareToIgnoreCase("and")!=0 && temp.compareToIgnoreCase("or")!=0 
                    && temp.compareToIgnoreCase("not")!=0 && temp.compareTo("(")!=0
                    && temp.compareTo(")")!=0)
            //no keywords or parinths
            {
                if(wasOp==0)//last token was not an operator, but we must have an operator between every word
                    conditions.append(" and ");
                conditions.append(regExpression(temp));
                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        conditions.append(" limit "+limit);//set max number of records to return
        rs=sendQuery(buildDescStatement(conditions.toString()),1);
        for(ListIterator l=rs.listIterator();l.hasNext();)
            for(ListIterator l2=((ArrayList)l.next()).listIterator();l2.hasNext();)
                keys.append(l2.next()+" ");
        return keys.toString();
    }   
    private List searchByKey(String inputKey, int limit)
    {//takes a string of keys to search for, and the fields to return
        //also prints the data directly to the screen.
        //returns a string of actual keys returned from database
        System.out.println("input keys are "+inputKey);
        StringBuffer conditions=new StringBuffer();
        StringBuffer feildCombo=new StringBuffer();            
        StringBuffer keys=new StringBuffer();
        List rs=null;
        int count=0; //used to limit number of keys actually sent to database
        for(int i=0;i<currentFeildLength;i++)
        {
            if(i!=0)
                feildCombo.append(", ");
            feildCombo.append(fullNames[currentFeildNums[i]]);
        }

        StringTokenizer in=new StringTokenizer(inputKey);
        while(in.hasMoreTokens() && count++ < limit)
            //conditions.append(likeExpression(in.nextToken()));
            conditions.append(likeExpression(in.nextToken()));

        conditions.append(" 0=1 ");        
        rs=sendQuery(buildGeneralStatement(feildCombo.toString(),conditions.toString(),limit),currentFeildLength+1);
        return rs;
    }
    private void printList(PrintWriter out,List list)
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
    private void printData(PrintWriter out,List rs, List logRs, List stats,List pdbList,List mismatches,List clusters)
    {   /*takes two result sets and  prints the results
                   directly to the screen preceding each element with its title, given by the feilds array
                   Aslo prints first length charecters of each value except the key, description and protein
                   values.  A length of 0 means print the entire value.
                */
        String MipsHyperlink=null;
        String data;
        StringBuffer output=new StringBuffer();
        StringBuffer allLogs=new StringBuffer();
        int f;
        boolean noLogs=false, noStats=false, noPDB=false,noMismatches=false,noClusters=false;
        ListIterator logLI=null,statLI=null,pdbLI=null,mmLI=null,clstrLI=null;
        
        if(logRs==null) noLogs=true;
            else logLI=logRs.listIterator();
        if(stats==null) noStats=true;
            else statLI=stats.listIterator();
        if(pdbList==null) noPDB=true;
            else pdbLI=pdbList.listIterator();
        if(mismatches==null) noMismatches=true;
            else mmLI=mismatches.listIterator();
        if(clusters==null) noClusters=true;
            else clstrLI=clusters.listIterator();
       

        try{
            for(ListIterator l=rs.listIterator();l.hasNext();)
            {                
                out.println("<HR>");
                List row=(ArrayList)l.next();
                if(currentDB==arab)
                    MipsHyperlink=(String)row.get(row.size()-1); 
                for(f=0;f<currentFeildLength;f++)
                {
                    data=(String)row.get(f);
                    out.println("<P>");
                    
                    if(f==0 && currentDB==arab)//print links before the first title
                    { //link to MIPS, and a link to TIGR databases
                        if(MipsHyperlink!=null) //mips link
                            out.println("<a href=\""+MipsHyperlink.substring(1,MipsHyperlink.length()-1)+"\">MIPS</a> ");
                        out.println("<a href=\"http://www.tigr.org/tigr-scripts/e2k1/euk_display.dbi?db=ath1&locus="+
                           row.get(0)+"\">TIGR</a><BR>"); //TIGR link
                        if(!noPDB) //PDB links
                            printPDBs(out,pdbLI,(String)row.get(0));
                        if(!noClusters)
                            printClusters(out,clstrLI,(String)row.get(0));
                    }
                    
                    if(data==null || data.compareTo("")==0)//avoid printing null values
                        output.append("no value");
                    else if(currentFeildNums[f]==3)
                        output.append(data); //don't upper case the TU
                    else if(currentFeildNums[f]<=1 || currentFeildNums[f]==9 ||
                            (currentFeildNums[f]>=10 && currentFeildNums[f]<=13) ) //0 is the key, 1 is the descriptoin, 9 is the protein feild
                        //don't trim protein, title or decription or TU
                        output.append(data.toUpperCase());
                    else if(length > 0 && length < data.length())
                        output.append(data.substring(0,length).toUpperCase());
                    else if(length < 0 && (-1*length) < data.length())
                        //length is negative, so adding it to data.lenth moves back from the end
                        output.append(data.substring(data.length()+length,data.length()).toUpperCase());
                    else //length==0
                        output.append(data.toUpperCase());

                    out.print(printNames[currentFeildNums[f]]+": ");//print descrivptive names rather then table names
                    out.println("<BR>"+output);
                    output.setLength(0); //clear output
                }
                //print orthologs here
                if(!noLogs) //use logRs
                    allLogs.append(printOrthologs(out, logLI,(String)row.get(0)));
                if(!noStats && currentDB==arab)
                    printStats(out, statLI);
            }
            if(!noLogs)
                out.println("<BR><A href='/databaseWeb/index.jsp?limit=0&input="+allLogs+"'>Search on all Orthologs</A><P>");
            if(!noMismatches)
                printMismatches(out, mmLI);
        }catch(Exception e){
            System.out.println("general error in printData: ");
            e.printStackTrace();
        }
    }
    private String printOrthologs(PrintWriter out,ListIterator l2,String currentKey)
    {
        StringBuffer logs=new StringBuffer();
        out.println("<P>Orthologs: ");
     
        while(l2.hasNext())
        {                          //compare current key to last key to check for changes         
            List logRow=(ArrayList)l2.next();
//            System.out.println("comparing "+logRow.get(0)+" to "+currentKey);
            if(((String)logRow.get(0)).compareTo(currentKey)==0 )
            {
                out.println(logRow.get(1)+", ");
                logs.append((String)logRow.get(1)+"+");
            }
            else 
            {
                l2.previous();
                break;
            }
        }
        if(logs.length()!=0) //some orthologs were found
            out.println("<BR><A href='/databaseWeb/index.jsp?limit=0&input="+logs+"'>Search on Orthologs</A><P>");
        else
            out.println(" none<P>");
        return logs.toString();
    }
    private void printStats(PrintWriter out,ListIterator l)
    {
        out.println("<TABLE width='90%' border='2'> ");
        printStatsHeader(out);
        if(l.hasNext())
        {
            out.println("<TR>");
            for(ListIterator l2=((ArrayList)l.next()).listIterator();l2.hasNext();)
                out.println("\t<TD>"+l2.next()+"</TD>");
            out.println("</TR>");
        }
        out.println("</TABLE>");
    }
    private void printPDBs(PrintWriter out,ListIterator it,String currentKey )
    {   //format of pdb data: 
        //      Atnum       code1:code2:...:code_n       score1:score2:...:score_n
        //      code is further broken into root_ext where ext is single a capital letter
        //print format is: " At#   root_ext (score) "  as a link.
        String code, link=new String("http://www.rcsb.org/pdb/cgi/explore.cgi?pid=74921033145243&pdbId=");
        List row;

        if(it.hasNext())
        {//we must match pdb key numbers to current key
            row=(ArrayList)it.next();
//            System.out.println("pdb key="+(String)row.get(0)+", current Key="+currentKey);
            if(!((String)row.get(0)).startsWith(currentKey)) //current key is different then the key we are at now
            {
                it.previous();  //back up one key and stop.  This sets it up for the next call
                return;
            }
            StringTokenizer codes=new StringTokenizer((String)row.get(1),":");
            StringTokenizer scores=new StringTokenizer((String)row.get(2),":");
            out.println("PDB links: &nbsp &nbsp");
            while(codes.hasMoreTokens() && scores.hasMoreTokens())
            { 
                code=codes.nextToken();
                out.println("<A href='"+link+code.substring(0,4)+"'>"+
                        code+"("+scores.nextToken()+")</A>&nbsp &nbsp");
            }
            out.println("<BR>");
        }
    }
    private void printClusters(PrintWriter out,ListIterator it,String currentKey)
    {//feilds 25k,8k, and AFGC are id number seperated by ':' charecters
        //order of fields is: Atnum,ClusterNum,size,AFGC,8k,25k.
        List row;
        String link; //add link text here
        if(!it.hasNext()) //no more records
            return;
        row=(ArrayList)it.next();
        if(!((String)row.get(0)).startsWith(currentKey))
        {//key recived does not match next key in cluster list, so go back one for next iteration
            it.previous();
            return;
        }
        out.println("<TABLE width='50%' >");
        out.println("<TR>");
        out.println("<TH><U>Cluster number</U></TH>\n"+
            "<TH><U>Size</U></TH>\n"+
            "<TH><U>AFGC</U></TH>\n"+
            "<TH><U>8k</U></TH>\n"+
            "<TH><U>25k</U></TH>");
        out.println("</TR>\n<TR ALIGN='center'>");
        out.println("<TD>"+row.get(1)+"</TD><TD>"+row.get(2)+"</TD>");
        out.println("<TD>"+row.get(3)+"</TD><TD>"+row.get(4)+"</TD><TD>"+row.get(5)+"</TD>");
        out.println("</TR>\n</TABLE>");
    }
    private void printMismatches(PrintWriter out,Iterator i)
    {
        if(i.hasNext())//if i is empty, don't print anything
            out.println("<P>Keys not returned: ");
        while(i.hasNext())
            out.print(i.next()+",");
    }
    private void printStatsHeader(PrintWriter out)
    {
        out.println("<TR>");
        out.println("<TH colspan='4'>Left UTR</TH>\n<TH colspan='4'>Right UTR</TH>\n"+
                    "<TH colspan='2'>Model</TH>");
        out.println("</TR><TR>");
        out.println("<TH>UTR length</TH>\n<TH>Number of introns</TH>\n"+
                    "<TH>Intron lengths</TH>\n<TH>DeltaG</TH>\n"+
                    "<TH>UTR length</TH>\n<TH>Number of introns</TH>\n<TH>Intron lengths</TH>\n<TH>DeltaG</TH>\n"+
                    "<TH>Model lengths</TH>\n<TH>Number of models</TH>\n");
        out.println("</TR>");
    }
    private void printFasta(PrintWriter out,List rs,List mismatches)
    {
        StringBuffer fastaOutput=new StringBuffer();
        StringBuffer record=new StringBuffer();
        String key,key2,desc,data;
        int start;
        try{
            out.println("<FORM METHOD='POST' ACTION='http://138.23.191.152/blast/blastSearch.cgi'>");
            out.println("<INPUT type='submit' value='Blast it'><BR>");
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
                    key2=new String("");
                    desc=(String)row.get(1);
                    start=2;
                }
                if(key==null) key=new String("");
                if(key2==null) key2=new String("");
                if(desc==null) desc=new String("");
                for(int f=start;f<currentFeildLength;f++)
                {   
                    data=(String)row.get(f);
                    if(data==null || data.compareTo("")==0 || currentFeildNums[f]==3) continue;                    
                    record.append("&gt "+key+" "+key2+" "+desc+" "+printNames[currentFeildNums[f]]+"\n");
                   
                    out.println(record+"<BR>");
                    fastaOutput.append(record.toString());
                    record.setLength(0);
                    
                    if(currentFeildNums[f]==9 || currentFeildNums[f]==13)   //trim output feilds to length
                        record.append(data.toUpperCase()+"\n");
                    else if(length > 0 && length < data.length())
                        record.append(data.substring(0,length).toUpperCase()+"\n");
                    else if(length < 0 && (-1*length) < data.length())
                        //length is negative, so adding it to data.lenth moves back from the end
                        record.append(data.substring(data.length()+length,data.length()).toUpperCase()+"\n");
                    else //length==0
                        record.append(data.toUpperCase()+"\n");
                    
                    out.println(record+"<BR>");
                    fastaOutput.append(record.toString());
                    record.setLength(0); //erase string
                }
            }
            out.println("<INPUT type=hidden name='input' value='"+fastaOutput.toString()+"'>");
            out.println("</FORM>");
            if(mismatches!=null)
                printMismatches(out,mismatches.iterator());
        }catch(NullPointerException npe){
            System.out.println("null pointer in fasta: "+npe.getMessage());
            npe.printStackTrace();
        }
    }
    private List sendQuery(String q, int length)
    {
        int i=0;
        queryThread dbConnection=new queryThread("Cis_Regul");
        dbConnection.setQuery(q,length);
        dbConnection.start();
        while(dbConnection.isAlive());//wait for query to finish       
        List data=new ArrayList(dbConnection.getResults());
        return data;
    }

    private List sendQuery2(String q,int length)
    {
        queryThread dbConnection=new queryThread("Cis_Regul",10);
        boolean status;        
        int sec=10;
        dbConnection.setQuery(q,length);
        dbConnection.start();
        while(dbConnection.isAlive())//wait for query to finish
        {
            status=((Boolean)session.getAttribute("QueryStatus")).booleanValue();
            System.out.println("status in sendQuery is: "+status);
            if(status==true)//status is true only if cancelQuery has resceived a signal
                //from the web page to set status to true.  By setting the value back to false here,
                //if the web page stops sending the signal to continue, the variable will remain false
                //whcih will cause the query to be canceled.
                session.setAttribute("QueryStatus",new Boolean(false));
            else
            {
                try{
                    System.out.println("killing query");
                    killQuery();
                   // System.exit(1);
                    this.finalize();
                }catch(Exception e){
                    System.out.println("cancelation failed: "+e.getMessage());
                }catch(Throwable t){System.out.println("finalize error");}               
            }
                
            try{
                Thread.currentThread().sleep(sec*1000);//sleep for sec seconds before checking again
            }catch(InterruptedException e){System.out.println("error sleeping in sendQuery:"+e.getMessage());}
             catch(NoSuchMethodError e){System.out.println("servlet exception: "+e.getMessage());}
        }
        
        List data=new ArrayList(dbConnection.getResults());
        return data;
    }
    private void killQuery()
    {
        int processID=0;
        queryThread dbConnection=new queryThread("Cis_Regul");
        dbConnection.setQuery("show processlist;",8);
        dbConnection.start();
        while(dbConnection.isAlive());//wait for query to finish       
        List data=new ArrayList(dbConnection.getResults());
        for(ListIterator l=data.listIterator();l.hasNext();)
        {
            List row=(ArrayList)l.next();
            if(row==null || row.get(7)==null)
                continue;
            if(((String)row.get(7)).startsWith("/*"+ID+"*/"))
            {
                processID=Integer.parseInt(((String)row.get(0)));
                break;
            }
        }
//        printList(data);
        dbConnection=new queryThread("Cis_Regul");
        dbConnection.setQuery("kill "+processID+";",0);
        dbConnection.start();
        while(dbConnection.isAlive());
        System.out.println("process ID is "+processID);
    }
    private String buildGeneralStatement(String feilds, String conditions,int limit)
    {
        StringBuffer general=new StringBuffer();
        if(currentDB==arab)  //ID is a global varibale used to kill the query at a later time
            general.append("/*"+ID+"*/SELECT "+feilds+",Hyperlinks.MIPS FROM "+
                "TIGR_Data LEFT JOIN Hyperlinks ON TIGR_Data.Atnum=Hyperlinks.Atnum "+
                "WHERE "+conditions+"ORDER BY Atnum");
        else if(currentDB==rice)
            general.append("/*"+ID+"*/SELECT "+feilds+",'' FROM Rice.Rice_Data_temp WHERE "+conditions+"ORDER BY Id1");
        else
            System.err.println("invalid DB name in buildGeneralStatement");
        general.append(" limit "+limit);
        System.out.println("general Query: "+general);
        return general.toString();
    }
    private String regExpression(String key)
    {
        if(currentDB==arab) //TIGR_Data.Description REGEXP ...
            return " ( TIGR_Data.Description REGEXP \""+key+"\") ";
        else if(currentDB==rice)
            return " ( Rice.Rice_Data_temp.Description REGEXP \""+key+"\") ";
        else
            System.err.println("invalid DB name in regExpression");
        return null;
    }
    private String likeExpression(String key)
    {
        String exp=null;
        if(currentDB==arab)  //TIGR_Data.Atnum
            exp=new String("TIGR_Data.Atnum LIKE '"+key+"%' OR ");
        else if(currentDB==rice)
            exp=new String("Rice.Rice_Data_temp.Id1 LIKE '"+key+"%' OR "+
                           "Rice.Rice_Data_temp.Id2 LIKE '"+key+"%' OR ");
        else
            System.err.println("invalid DB name in likeExpression");
        return exp;
    }
    private String buildDescStatement(String conditions)
    {
        String desc=null;
        if(currentDB==arab)  //select TIGR_Data.Atnum from TIGR_Data where ...
            desc=new String("/*"+ID+"*/SELECT TIGR_Data.Atnum FROM TIGR_Data WHERE "+ conditions);
        else if(currentDB==rice)
            desc=new String("/*"+ID+"*/SELECT Rice.Rice_Data_temp.Id1  FROM Rice.Rice_Data_temp WHERE "+ conditions);
        else
            System.err.println("invalid DB name in buildDescStatement");
        System.out.println("description query: "+desc);
        return desc;
    }
    private String buildPDBStatement(String conditions)
    {//grab data from Blast_Resutls DB and PDB tables;
        String q=null;
        if(currentDB==arab)
            q=new String("SELECT * FROM Blast_Results.PDB WHERE "+conditions);
        System.out.println("pdb query:"+q);
        return q;
    }
    private String buildClusterStatement(String conditions)
    {
        String q=new String("");
        if(currentDB==arab)
            q=new String("SELECT * FROM Cis_Regul.Clusters WHERE "+conditions);
        System.out.println("cluster query: "+q);
        return q;
    }
/*    private String buildGeneralStatement2(String feilds, String conditions)
    {   /*creaties a template for querying the entire database, or most of it.  The query 
                   joins the specifed tables together with successive left join statements.  Promoter_1500
                   is garenteed to have all keys, so use it first.  
                   The feilds string specifies which columns to return.  It must consist of absolute column
                   names seperated by commas.  
                   The conditoins statement can be any valid SQL function that evaluates to true of false.
                   It can also contain a limit clause.
                */
/*        StringBuffer join=new StringBuffer();
        boolean addTigr=false, addArabi=false,addIntergenic=false;
        if(currentDB==arab)
        {
            
            //select feilds from TIGR_Data where conditions;
            join.append(
                "SELECT "+feilds+       //MIPS_UTRs.MIPS_Description, Promoter_1500.Atnum, 
                ", Hyperlinks.MIPS "+
                "FROM Promoter_1500 "+
                "LEFT JOIN MIPS_UTRs ON Promoter_1500.Atnum=MIPS_UTRs.Atnum "+
                "LEFT JOIN Hyperlinks ON Promoter_1500.Atnum=Hyperlinks.Atnum ");
            for(int i=0;i<currentFeildLength;i++) //find out which tables we are useing
            {                                  // so that we only join the tables we need.
                int t=currentFeildNums[i];
                if(t==4 || t==6 || t==8)
                    addTigr=true;
                else if(t==9)
                    addArabi=true;
                else if(t==15)
                    addIntergenic=true;
            }
            if(addTigr)
                join.append("LEFT JOIN TIGR_cDNA_UTRs ON Promoter_1500.Atnum=TIGR_cDNA_UTRs.Atnum ");
            if(addArabi)
                join.append("LEFT JOIN Arabi_all_proteins ON Promoter_1500.Atnum=Arabi_all_proteins.Atnum ");
            if(addIntergenic)
                join.append("LEFT JOIN Intergenic ON Promoter_1500.Atnum=Intergenic.Atnum ");
            join.append("WHERE "+conditions+";");
        }
        else if(currentDB==rice)
            join.append(
                "SELECT "+feilds+       
                ",'' FROM Rice.rice_all_proteins LEFT JOIN Rice.rice_all_cds ON "+
                "Rice.rice_all_proteins.Id1=Rice.rice_all_cds.Id1 "+
                "WHERE "+conditions+";"
            );
        else
            System.err.println("invalid DB name in buildGeneralStatement");
        System.out.println("general statment query:"+join);
        return join.toString();
    }
    
    private String regExpression2(String key)
    {
        if(currentDB==arab) //TIGR_Data.Description REGEXP ...
            return " ( MIPS_UTRs.MIPS_Description REGEXP \""+key+"\") ";
        else if(currentDB==rice)
            return " ( Rice.rice_all_proteins.Description REGEXP \""+key+"\") ";
        else
            System.err.println("invalid DB name in regExpression");
        return null;
    }
    private String likeExpression2(String key)
    {
        String exp=null;
        if(currentDB==arab)  //TIGR_Data.Atnum
            exp=new String("Promoter_1500.Atnum LIKE '"+key+"%' OR ");
        else if(currentDB==rice)
            exp=new String("Rice.rice_all_proteins.Id1 LIKE '"+key+"%' OR ");
        else
            System.err.println("invalid DB name in likeExpression");
        return exp;
    }
    private String buildDescStatement2(String conditions)
    {
        String desc=null;
        if(currentDB==arab)  //select TIGR_Data.Atnum from TIGR_Data where ...
            desc=new String("SELECT MIPS_UTRs.Atnum FROM MIPS_UTRs WHERE "+ conditions);
        else if(currentDB==rice)
            desc=new String("SELECT Rice.rice_all_proteins.Id1  FROM Rice.rice_all_proteins WHERE "+ conditions);
        else
            System.err.println("invalid DB name in buildDescStatement");
        System.out.println("description query: "+desc);
        return desc;
    }
*/    
    private String buildOrthologStatement(String conditions)
    {   //special query for orthologs
        String query=new String("SELECT Atnum, Orthologs From Blast_Results.HitList WHERE "+ conditions);
        System.out.println("ortholog query="+query);
        return query;
    }
    private String buildStatStatement(String conditions)
    {   
        String l=new String("TIGR_left_UTR_stats");
        String r=new String("TIGR_right_UTR_stats");
        String m=new String("TIGR_model_stats");
        String feilds=new String(l+".Length,"+l+".Intron_count,"+l+".Intron_lengths,"+l+".DeltaG,"+
                                 r+".Length,"+r+".Intron_count,"+r+".Intron_lengths,"+r+".DeltaG,"+
                                 m+".Lengths,"+m+".Count");
        String query=new String("SELECT "+feilds+" FROM TIGR_left_UTR_stats LEFT JOIN TIGR_right_UTR_stats"+
                                " ON TIGR_left_UTR_stats.Atnum=TIGR_right_UTR_stats.Atnum"+
                                " LEFT JOIN TIGR_model_stats ON TIGR_left_UTR_stats.Atnum=TIGR_model_stats.Atnum"+
                                " WHERE "+conditions);
        System.out.println("stat Query="+query);
        return query;
    }
    private void printHeader(PrintWriter out)
    {   //print the CEPCEB header on the top of every page
        String header=new String(""+ 
        "<table width=\"86%\" border=\"0\">"+
        "<tr> <td width=\"376\" rowspan=\"3\"><i><font face=\"georgia, Times New Roman, Times\"><a href=\"http://www.cepceb.ucr.edu/\"><img src=\"header_01.jpg\" width=\"371\" height=\"90\" border=\"0\"></a></font></i></td>"+
        "<td width=\"463\"> <div align=\"center\"><font size=\"+3\" face=\"Arial, Helvetica, sans-serif\"><i><b><font color=\"#339933\" size=\"+4\">Bioinformatics "+
        "Core</font></b></i></font></div>"+"</td>"+"</tr>"+
        "<tr> <td width=\"463\"> <div align=\"center\"><font face=\"georgia, Times New Roman, Times\"><font color=\"#D27E00\"><b><font color=\"#000000\" face=\"Arial, Helvetica, sans-serif\" size=\"2\"><a href=\"http://www.cepceb.ucr.edu/\">Center"+
        "for Plant Cell Biology</a>, UC Riverside</font></b></font></font></div>"+"</td>"+"</tr>"+
        "<tr> <td width=\"463\" height=\"32\"> <div align=\"center\"><font color=\"#339933\" face=\"Arial, Helvetica, sans-serif\" size=\"+3\"><i>Information Retrieval</i></font></div>"+
        "</td>"+"</tr>"+"<tr> <td colspan=\"2\"> <hr size=\"3\">"+"</td>"+"</tr>"+"</table>");
        out.println(header);
    }
    private void defineNames()
    {
        //assign names for later lookup
        fullNames=new String[feildCount];
        fullNames[0]="TIGR_Data.Atnum";fullNames[1]="TIGR_Data.Description";fullNames[2]="TIGR_Data.Promoter";
        fullNames[3]="TIGR_Data.TU";fullNames[4]="TIGR_Data.5UTR";fullNames[5]="Rice.Rice_Data_temp.Intergenic";
        fullNames[6]="TIGR_Data.ORF";fullNames[7]="Rice.Rice_Data_temp.Promoter";fullNames[8]="TIGR_Data.3UTR";
        fullNames[9]="TIGR_Data.Protein";fullNames[10]="Rice.Rice_Data_temp.Id1";fullNames[11]="Rice.Rice_Data_temp.Id2";
        fullNames[12]="Rice.Rice_Data_temp.Description";fullNames[13]="Rice.Rice_Data_temp.Protein";fullNames[14]="Rice.Rice_Data_temp.CDS";
        fullNames[15]="TIGR_Data.Intergenic";fullNames[16]="Rice.Rice_Data_temp.TU";
/*
        fullNames[0]="Promoter_1500.Atnum";fullNames[1]="MIPS_UTRs.MIPS_Description";fullNames[2]="Promoter_1500.Promoter";
        fullNames[3]="MIPS_UTRs.5UTR";fullNames[4]="TIGR_cDNA_UTRs.5UTR";fullNames[5]="MIPS_UTRs.ORF";
        fullNames[6]="TIGR_cDNA_UTRs.ORF";fullNames[7]="MIPS_UTRs.3UTR";fullNames[8]="TIGR_cDNA_UTRs.3UTR";
        fullNames[9]="Arabi_all_proteins.Protein";fullNames[10]="Rice.rice_all_proteins.Id1";fullNames[11]="Rice.rice_all_proteins.Id2";
        fullNames[12]="Rice.rice_all_proteins.Description";fullNames[13]="Rice.rice_all_proteins.Protein";fullNames[14]="Rice.rice_all_cds.Cds";
        fullNames[15]="Intergenic.DNA";fullNames[16]="Blast_Results.HitList";//16 requires a special query because several keys are returned for each key given
 */
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
////////////////////////////////  auto code  ////////////////////////////////////////
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
        return "Searches database";
    }
    
}