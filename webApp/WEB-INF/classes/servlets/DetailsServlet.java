/*
 * DetailsServlet.java
 *
 * Created on February 24, 2003, 12:26 PM
 */
package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;
/**
 *
 * @author  khoran
 * @version
 */

public class DetailsServlet extends HttpServlet {
    
    String[] colors;
    String[] fieldNames;
    String[] titles;
    final int fieldCount=19;    
    final int arab=0, rice=1;
    int count=0;
    public void init(ServletConfig config) throws ServletException 
    {
        super.init(config); 
        defineNames();
    }
    public void destroy() 
    {}
    
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
//        List keys=(ArrayList)session.getAttribute("keys");
//        int limit=((Integer)session.getAttribute("limit")).intValue();
//        int[] dbNums=(int[])session.getAttribute("dbs");        
//        int dbNumsLength=((Integer)session.getAttribute("dbsLength")).intValue();

        List data,logs,keySet;
        //////////////////////////////////////////////////////////////////////
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Annotation Data</title>");

        out.println("</head>");
        out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");     
        Common.printHeader(out);

        ///////////////////////////////////////////////////////////////////////
//        logs=getOrthologs(keys,limit,dbNums[0]);
        for(int i=0;i<qi.dbsLength;i++)
        {
            keySet=qi.getKeySet(i);
            logs=getOrthologs(keySet,qi.limit,qi.dbNums[i]);
            data=getData(keySet,qi.limit,qi.dbNums[i]);
//            Common.printList(out,data);            
//            Common.printList(out,logs);
            printData(out, data,logs,qi.dbNums[i],hid);
        }
        
        ///////////////////////////////////////////////////////////////////////
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
    private List getData(List keys,int limit,int currentDB)
    {
        StringBuffer condition=new StringBuffer();
        for(Iterator i=keys.iterator();i.hasNext();)
            condition.append(likeExpression((String)i.next(),currentDB));
        condition.append("0=1 "); //terminate last OR statement
        return Common.sendQuery(detailsQuery(condition.toString(),limit,currentDB));           
    }
    private List getOrthologs(List keys,int limit,int currentDB)
    {
        StringBuffer condition=new StringBuffer();        
        for(Iterator i=keys.iterator();i.hasNext();)
            condition.append("Blast_Results.HitList.Atnum "+Common.ILIKE+" '"+i.next()+"%' OR ");        
        condition.append("0=1 ");
        return Common.sendQuery(orthologQuery(condition.toString(),limit*10,currentDB));
    }
    private List getClusterKeys(List data,int limit,int currentDB)
    {
        StringBuffer condition=new StringBuffer();
        for(Iterator i=data.iterator();i.hasNext();)
            condition.append(clusterLike((String)((ArrayList)i.next()).get(1),currentDB));
        condition.append("0=1 ");
        return Common.sendQuery(clusterQuery(condition.toString(),limit,currentDB));
    }   
    private String hexColor(int r, int g, int b)
    {
        StringBuffer color=new StringBuffer();
        int a,c;// range of values;
        a=50; c=150;
        r%=0xff; 
        g%=0xff;
        b=b%(c-a)+a;
        if(r<16)
            color.append("0");
        color.append(Integer.toHexString(r));
        if(g<16)
            color.append("0");
        color.append(Integer.toHexString(g));
        if(b<16)
            color.append("0");
        color.append(Integer.toHexString(b));
        return color.toString();
    }
    private void printData(PrintWriter out,List data,List logs,int currentDB,int hid)
    {
        ArrayList row;
        String key;
        StringBuffer allorthologs=new StringBuffer();
        ListIterator l=logs.listIterator();        
        int r,g,b;
        r=0; g=200; b=10;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(ArrayList)i.next();
            key=(String)row.get(0);
            out.println("<TABLE align='center' border='0' bgcolor='"+hexColor(r,g,b)+"' >");        
            out.println("\t<TR align='left'>");
                out.println("\t\t<TH><b>"+key+"</b></TH>");
                out.println("\t\t<TD><a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+
                               key+"'>MIPS</a></TD><TD>");            
                out.println("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+
                               "ORF_infopage.cgi?db=ath1&orf="+key+"'>TIGR</a></TD>");                
            out.println("<TD colspan='2'>&nbsp</TD></TR>");
            allorthologs.append(printOrthologs(out,l,key));
            printClusters(out,row,hid);
            printPDB(out,row);
            printArray(out,row);
            printStats(out,row);            
            out.println("<TR></TR>");
            out.println("</TABLE>");            
            b+=30; 
        }
        if(allorthologs.length()!=0)
            out.println("<A href='/index.jsp?limit=0&input="+allorthologs+"'>Search on all Orthologs</A>");
    }
    private String printOrthologs(PrintWriter out,ListIterator l,String currentKey)
    {
        StringBuffer keys=new StringBuffer();
        String key="";
        ArrayList row;
        out.println("\t<TR align='left'>");
            out.println("\t\t<TD>&nbsp</TD><TH>Orthologs</TH>");
        out.println("<TD colspan='3'>&nbsp</TD></TR>\n\t<TR align='left'>");
            out.println("\t\t<TD colspan='2'>&nbsp</TD><TH>ID</TH>");
        out.println("<TD colspan='2'>&nbsp</TD></TR>");
            //loop thru all the orthlogs
        while(l.hasNext())
        {
            row=(ArrayList)l.next();
            if(!((String)row.get(0)).equals(currentKey))
            {
                l.previous();
                break;
            }
            key=(String)row.get(1);
            //then print the orthlog key
            //orthologs keys have the Id1 appended to the Id2.
            //we need to serparate these because the rice db only has one or the
            //other.  Assume all Id2's start with a letter, and all Id1's start
            //with a number.
            
            if(key.charAt(5) >='A' && key.charAt(5) <='Z')//starts with a capital letter
                key=key.substring(key.indexOf('_')+1); //then chop off the first part of the string
            
            out.println("<TR><TD colspan='2'>&nbsp</TD><TD>&nbsp <A href='/index.jsp?limit=0&input="+key+"'>"+key+"</A></TD>"+
                "<TD colspan='2'>&nbsp</TD></TR>");
            keys.append(key+"+");
        }
        if(keys.length()!=0)
            out.println("<TR><TD>&nbsp</TD><TD colspan='2'><A href='/index.jsp?limit=0&input="+keys+"'>Search on Orthologs</A>"+
                "</TD><TD colspan='2'>&nbsp</TD></TR>");
        return keys.toString();
    }
    private void printClusters(PrintWriter out,List dataRow,int hid)
    {//dataRow is just a single row of the main table        
        StringBuffer ids=new StringBuffer();
        out.println("<TR><TD>&nbsp</TD><TH>Cluster</TH><TD colspan='3'>&nbsp</TD></TR>");
        out.println("<TR><TD colspan='2'>&nbsp</TD><TH>Cluster ID</TH><TH>Size</TH>"+
            "<TH>&nbsp</TH></TR>");
        out.println("<TR><TD colspan='2'>&nbsp</TD><TD>&nbsp "+dataRow.get(1)+"</TD>"+
            "<TD>&nbsp "+dataRow.get(2)+"</TD><TD>"+
            "<A target='clusters' href='/databaseWeb/ClusterServlet?hid="+hid+"&clusterID="+dataRow.get(1)+"'>"+
            "Cluster Members</A></TD></TR>");
    }
    private void printPDB(PrintWriter out,List dataRow)
    {
        if(dataRow.get(16)==null || dataRow.get(17)==null)
        {
            System.out.println("null in PDB");
            return;
        }
        String code, link=new String("http://www.rcsb.org/pdb/cgi/explore.cgi?pid=74921033145243&pdbId=");
        StringTokenizer codes=new StringTokenizer((String)dataRow.get(16),":");
        StringTokenizer scores=new StringTokenizer((String)dataRow.get(17),":");

        out.println("<TR><TD>&nbsp</TD><TH>PDB</TH><TD colspan='3'>&nbsp</TD></TR>");
        out.println("<TR><TD colspan='2'>&nbsp</TD><TH>PDB ID</TH><TH>e value</TH><TD>&nbsp</TD></TR>");
        while(codes.hasMoreTokens() && scores.hasMoreTokens())
        { 
                code=codes.nextToken();
                out.println("<TR><TD colspan='2'>&nbsp</TD><TD><A href='"+link+code.substring(0,4)+"'>"+
                    code+"</TD><TD>&nbsp "+scores.nextToken()+"</TD><TD>&nbsp</TD></TR>");
        }
    }
    private void printArray(PrintWriter out,List dataRow)
    {
        StringTokenizer afgc=new StringTokenizer((String)dataRow.get(3),":");
        StringTokenizer k8=new StringTokenizer((String)dataRow.get(4),":");
        StringTokenizer k25=new StringTokenizer((String)dataRow.get(5),":");
        String temp=null,blank=new String("<TD>&nbsp</TD>");
        out.println("<TR><TD>&nbsp</TD><TH>Array/chip ID</TH><TD colspan='3'>&nbsp</TD></TR>");
        out.println("<TR><TD colspan='2'>&nbsp</TD><TH>AFGC</TH><TH>8k</TH><TH>25k</TH></TR>");
        while(afgc.hasMoreTokens() || k8.hasMoreTokens() || k25.hasMoreTokens())
        {
            out.println("<TR><TD colspan=2'>&nbsp</TD>");
            if(afgc.hasMoreTokens())    
            {
                temp=afgc.nextToken();
                out.println("<TD>&nbsp <A href='http://www.arabidopsis.org/cgi-bin/afgc/atExpressioncgi.pl?clone_id="+
                    temp+"'>"+temp+"</A></TD>");
            }
            else out.println(blank);
            if(k8.hasMoreTokens())
                out.println("<TD>&nbsp "+k8.nextToken()+"</TD>");
            else out.println(blank);
            if(k25.hasMoreTokens())
                out.println("<TD>&nbsp "+k25.nextToken()+"</TD>");
            else out.println(blank);            
            out.println("</TR>");
        }
//        out.println("<TR><TD colspan='2'>&nbsp</TD><TD>&nbsp "+dataRow.get(3)+"</TD><TD>&nbsp "+dataRow.get(4)+
//            "</TD><TD>&nbsp "+dataRow.get(5)+"</TD></TR>");
    }
    private void printStats(PrintWriter out,List dataRow)
    {        
        out.println("<TR><TD>&nbsp</TD><TH>Model</TH><TD colspan='3'>&nbsp</TD></TR>");
        out.println("<TR><TD colspan='2'>&nbsp</TD><TH>Model Lengths</TH>"+
            "<TH>Number of Models</TH><TD>&nbsp</TD></TR>");
        out.println("<TR><TD colspan='2'>&nbsp</TD><TD>"+dataRow.get(14)+
            "</TD><TD>"+dataRow.get(15)+"</TD><TD>&nbsp</TD></TR>");
        out.println("<TR><TD>&nbsp</TD><TH>UTR length</TH><TH>Number of introns</TH>"+
                    "<TH>Intron lengths</TH><TH>DeltaG</TH></TR>");
        out.println("<TH>Left UTR</TH>");
        for(int i=6;i<=9;i++)
            out.println("<TD>&nbsp "+dataRow.get(i)+"</TD>");        
        out.println("</TR><TR><TH>Right UTR</TH>");
        for(int i=10;i<=13;i++)
            out.println("<TD>&nbsp "+dataRow.get(i)+"</TD>");
        out.println("</TR>");        
    }
    /////////////////////////  query stuff /////////////////////////////////////////////////////
    private String detailsQuery(String condition,int limit,int currentDB)
    {      
        String query="";
        if(currentDB==arab)
        {
            String l=new String("TIGR_left_UTR_stats");
            String r=new String("TIGR_right_UTR_stats");
            String m=new String("TIGR_model_stats");
            String feilds=new String(l+".Atnum,"+ // 1  key
                "ClusterNum,Size,AFGC,8k,25k,"+ //5 
                l+".Length,"+l+".Intron_count,"+l+".Intron_lengths,"+l+".DeltaG,"+ //4
                r+".Length,"+r+".Intron_count,"+r+".Intron_lengths,"+r+".DeltaG,"+ //4
                m+".Lengths,"+m+".Count,"+ //2
                "Blast_Results.PDB.codes,Blast_Results.PDB.scores "); //2
            String join=new String("TIGR_left_UTR_stats "+
                "LEFT JOIN TIGR_right_UTR_stats USING (Atnum) "+
                "LEFT JOIN TIGR_model_stats USING (Atnum) "+
                "LEFT JOIN Clusters USING (Atnum) "+
                "LEFT JOIN  Blast_Results.PDB USING (Atnum) ");
           query=new String("SELECT "+feilds+"FROM "+join+"WHERE "+condition+
                "ORDER BY "+l+".Atnum DESC limit "+limit);
        }
        System.out.println("details query:"+query);
        return query;
    }
    private String orthologQuery(String condition,int limit,int currentDB)
    {
        String query="";
        String db="";
        query=new String("SELECT Atnum, Orthologs From Blast_Results.HitList WHERE "+ condition+
            " ORDER BY Blast_Results.HitList.Atnum DESC limit "+ limit);
/*        if(currentDB==arab)  //to get the description from the correct table          
            query=new String("SELECT Blast_Results.HitList.Atnum,Blast_Results.HitList.Orthologs,Rice.Rice_Data.Description "+
                "FROM Rice.Rice_Data, Blast_Results.HitList "+
                "WHERE (Rice.Rice_Data.Id1=Blast_Results.HitList.Orthologs OR "+
                "Rice.Rice_Data.Id2=Blast_Results.HitList.Orthologs) "+
                "AND "+condition+" ORDER BY Blast_Results.HitList.Atnum limit "+limit);
        else if(currentDB==rice)
            query=new String("SELECT Blast_Results.HitList.Atnum,Blast_Results.HitList.Orthologs,TIGR_Data.Description "+
                "FROM TIGR_Data, Blast_Results.HitList "+
                "WHERE TIGR_Data.Atnum=Blast_Results.HitList.Orthologs "+
                "AND "+condition+" ORDER BY Blast_Results.HitList.Atnum limit "+limit);
 */
        System.out.println("ortholog query: "+query);
        return query;
    }
    private String hitsQuery(String condition,int limit,int currentDB)
    {
        String query="";
        query=new String("SELECT Blast_Results.HitList.Atnum, Blast_Results.HitList.Orthologs "+
            "FROM Blast_Results.HitList WHERE "+condition+" ORDER BY "+
            "Blast_Results.HitList.Atnum limit "+limit);
/*        if(currentDB==arab)
            query=new String("SELECT Id1,Description FROM "+
                "Rice.Rice_Data WHERE "+condition+" ORDER BY ID1 limit "+limit);
        else if(currentDB==rice)
            query=new String("SELECT Atnum,Description FROM "+
                "TIGR_Data WHERE "+condition+" ORDER BY Atnum limit "+limit);
 */
        return query;
    }
    private String clusterQuery(String condition,int limit,int currentDB)
    {
        String query="";
        if(currentDB==arab)
            query=new String("SELECT Clusters.ClusterNum, Clusters.Atnum,"+
                "TIGR_Data.Description FROM "+
                "TIGR_Data LEFT JOIN Clusters USING (Atnum) WHERE "+
                condition+" ORDER BY Clusters.Atnum"); //+" limit "+limit);
        System.out.println("cluster query is "+query);
        return query;
    }
    private String likeExpression(String key,int currentDB)
    {
        String like="";
        if(currentDB==arab)
            like=new String("TIGR_left_UTR_stats.Atnum "+Common.ILIKE+" '"+key+"%' OR ");
        else if(currentDB==rice)
            like=new String("0=1");//unknown still
        return like;
    }
    private String hitKeysLike(String key, int currentDB)
    {
        String like="";
        if(currentDB==arab)
            like=new String("Rice.Rice_Data.ID1 "+Common.ILIKE+" '"+key+"%' OR "+
                "Rice.Rice_Data.ID2 "+Common.ILIKE+" '"+key+"%' OR ");
        else if(currentDB==rice)
            like=new String("TIGR_Data.Atnum "+Common.ILIKE+" '"+key+"%' OR ");
        return like;
    }
    private String clusterLike(String key,int currentDB)
    {
        String like="";
        if(currentDB==arab)
            like=new String("Clusters.ClusterNum='"+key+"' OR ");
        return like;
    }
////////////////////////////////////////////////////////////////////////////////////////////////    
    private void defineNames()
    {
        colors=new String[fieldCount];
        fieldNames=new String[fieldCount];
        titles=new String[fieldCount];
    }
////////////////////////////////////////////////////////////////////////////////////////////////        
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
