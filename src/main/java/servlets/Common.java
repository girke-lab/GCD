/*
 * Common.java
 *
 * Created on March 13, 2003, 4:11 PM
 */
package servlets;
/**
 *
 * @author  khoran
 */
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;
import servlets.beans.HeaderBean;

public class Common {
    public final static int arab=0, rice=1;    
    public final static int dbCount=2;
    //TODO: change these to enums.
    
    public final static String[] dbRealNames=new String[]{"arab","rice"};
    public final static String[] dbPrintNames=new String[]{"Arabidopsis","Rice"};
//    public final static String dataColor="D3D3D3",titleColor="AAAAAA";        
    //maximum number of results that can be returned per database query
    public final static int MAXKEYS=100000; 
    public final static int SCRIPT_LIMIT=500; 
    public final static int MAX_QUERY_KEYS=10000; //max number of keys to list in a query
    public final static int MAX_SESSIONS=5; //maximum number of sessions a user can store
    
    //the caseless compare keyword is ILIKE in postgres, but LIKE in mysql
    public final static String ILIKE="ILIKE";
                            
//    private static DbConnection dbc=null;
    private static Logger log=Logger.getLogger(Common.class);
    /** Creates a new instance of Common */
    public Common() {
    }
    
    public static List sendQuery(String q)
    {        
        List rs=null;
        DbConnection dbc;
        try{
            dbc=DbConnectionManager.getConnection("khoran");
            rs=dbc.sendQuery(q);         
            log.info("Stats: "+dbc.getStats());
        }catch(Exception e){            
            log.error("query error: "+e.getMessage());         
        }
        if(rs==null)
            log.debug("null rs");
        return rs;
    }       
    
    public static void printList(PrintWriter out,List list)
    {
        int rows=0,cols=0;
        String cell;
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
    public static void printList(PrintStream out,List list)
    {
        int rows=0,cols=0;
        String cell;
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
    public static String printArray(int[] a)
    {
        String out="[";
        for(int i=0;i<a.length;i++){
            out+=a[i];
            if(i+1<a.length)
                out+=",";
        }
        return out+"]";            
    }
    public static String printArray(Object[] a)
    {
        if(a==null)
            return null;
        String out="[";
        for(int i=0;i<a.length;i++){
            out+=a[i];
            if(i+1<a.length)
                out+=",";
        }
        return out+"]";            
    }
    public static void printForm(PrintWriter out,int hid)
    {
        out.println("\n" +
            "<table><tr><td width='300'>&nbsp</td><td align='left'>"+
            "<FORM method=get name='form1' action='QueryPageServlet'>\n"+  //SequenceServlet
            "<INPUT type=hidden name='hid' value='"+hid+"'>"+
            "<INPUT type=hidden name='displayType' value='modelView'>"+                        
            "<TABLE align='center' border='0'>\n"+
            "\t<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='3'><a href='titleInfo.html'>TU</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='4'><a href='titleInfo.html'>Promoter 3000</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='5'><a href='titleInfo.html'>3' UTR</a></TD>\n"+
            "\t</TR>\n<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='6'><a href='titleInfo.html'>Intergenic</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='7'><a href='titleInfo.html'>CDS</a></TD>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='8'><a href='titleInfo.html'>5' UTR</a></TD>\n"+
            "\t</TR>\n<TR>\n"+
            "\t\t<TD><INPUT type=checkbox name='fields' value='9'><a href='titleInfo.html'>Protein</a></TD>\n"+
            "\t\t<TD colspan='2'><a href='titleInfo.html'>Length of Sequence to return:</a> <INPUT name='length' value='' size='5'></TD>\n"+
            "</TR><TR>\n"+
            //"\t\t<TD align='center' colspan='1'><INPUT type=checkbox name='format' value='1'>fasta format</TD>\n"+            
            "\t\t<TD align='center' colspan='3'>Format: <SELECT name='format'><OPTION value='0'>html" +
            "\t\t\t<OPTION value='1'>fasta<OPTION selected value='2'>all fasta</SELECT></TD>\n"+
            "</TR></TABLE>\n"+ 
            "<TABLE align='center' border='0'>\n"+
            "\t<TR>\n"+
            //"\t\t<TD><INPUT type=submit name='seq_fields' value='Sequence Data' ></TD>\n"+
            "\t\t<TD><INPUT type=image name='submit' width='100' height='25' border='0' src='images/sequence.jpg' ></TD>\n"+
            "\t\t<TD><a href='QueryPageServlet?hid="+hid+"&displayType=seqView'><img width='100' height='25' border='0' src='images/summary.jpg'></a></TD>\n"+
            //"\t\t<TD><INPUT type=submit value='Annotation Data' onClick='getDetails();'>\n"+
            "\t</TR>\n"+            
            "</TABLE>\n"+
            "</FORM></td></tr></table>\n");
    }
   
   
    
    
    
   
    public static void printUnknownsSearchLinks(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        out.println( "<A href='treatmentSearch.jsp'>Expression Search</A>");
    }
    public static void printUnknownDownloadLinks(Writer w,int hid,int end)
    {
        printUnknownDownloadLinks(w,hid,end,"mas5");
    }
    public static void printUnknownDownloadLinks(Writer w,int hid,int end,String intensityType)
    {
        
        PrintWriter out=new PrintWriter(w);
        String link="DispatchServlet?hid="+hid+"&script=unknownsText&range=0-"+end;
        //String[] dataTypes=new String[]{"Unknown","Go","Blast","Proteomics","Cluster","ProbeCluster", "ExternalUnknown","AffyExpSet","AffyComp","AffyDetail"};
        //String[] linkNames=new String[]{"Keys","Go","Blast","Proteomics","Sequence Clusters","Expression Clusters", "External Sources","Experiment Sets","Comparisons","Cel"};
        String[] dataTypes=new String[]{"Cluster","ProbeCluster","AffyExpSet","AffyComp","AffyDetail"};
        String[] linkNames=new String[]{"Sequence Clusters","Expression Clusters", "Experiment Sets","Comparisons", "Cel"};
        for(int i=0;i<linkNames.length;i++)
            out.println("&nbsp<a href='"+link+"&dataType="+dataTypes[i]+"&intensityType="+intensityType+"'>"+
                    linkNames[i]+"</a>");
    }
    
    
    public static int getDBid(String name)
    {//takes a Genome string from database and reutrn an integer id number for it
        if(name.equals("arab"))
            return arab;
        if(name.equals("rice"))
            return rice;
        return -1;
    }

    public static void quit(PrintWriter out,String message)
    {
        out.println(message);
        out.println("</body></html>");
        out.close();
    }
    public static void sendError(javax.servlet.http.HttpServletResponse response,String page,String error)
    {
        try{
            response.sendRedirect(page+"?error_message="+error);
        }catch(IOException e){
            log.error("could not redirect to "+page+", error: "+error+
                    ", exception: "+e);
        }
    }
    public static void printStatsTable(PrintWriter out,String title,String[] subTitles,Object[] values)
    {        
        out.println("<table border='1' cellspacing='0' bgcolor='"+PageColors.data+"'>");
        out.println("<tr  bgcolor='"+PageColors.title+"'><th colspan='"+subTitles.length+"'>"+title+"</th></tr>");
        out.println("<tr  bgcolor='"+PageColors.title+"'>");
        for(int i=0;i<subTitles.length;i++)
            out.println("<th>"+subTitles[i]+"</th>");
        out.println("</tr><tr>");
        for(int i=0;i<values.length;i++)
        {
            if(values[i]!=null)
                out.println("<td>"+values[i]+"</td>");
            else
                out.println("<td>&nbsp</td>");
        }
        out.println("</tr></table>");        
    }
    public static String buildIdListCondition(String varName,Collection ids)
    {
        return buildIdListCondition(varName,ids,false,-1); //default to no quotes.
    }
        public static String buildIdListCondition(String varName,Collection ids, boolean quoteIt)
    {
        return buildIdListCondition(varName,ids,quoteIt,-1); 
    }
    public static String buildIdListCondition(String varName,Collection ids,int limit)
    {
        return buildIdListCondition(varName,ids,false,limit); //default to no quotes.
    }
    public static String buildIdListCondition(String varName,Collection ids,boolean quoteIt,int limit)
    {
        StringBuffer out=new StringBuffer();
        if(ids.size()==0)
            return "0=1"; //since list is empty, return a false statement, while avoiding syntax errors.
        int count=0;
        out.append(varName+" in (");
        for(Iterator i=ids.iterator();i.hasNext() && (limit==-1 || count < limit);count++)
        {
            if(quoteIt)
                out.append("'"+i.next()+"'");
            else
                out.append(i.next());
            if(i.hasNext() && (limit==-1 || count < limit))
                out.append(",");
        }
        out.append(")");
        return out.toString();
    }
    public static String buildLikeCondition(String varName, Collection ids)
    {
        return buildLikeCondition(varName, ids, -1,false);// no limit
    }            
    public static String buildLikeCondition(String varName, Collection ids,int limit)
    {
        return buildLikeCondition(varName, ids, limit,false); 
    }            
    public static String buildLikeCondition(String varName, Collection ids,boolean b)
    {
        return buildLikeCondition(varName, ids, -1,b);// no limit
    }            
    public static String buildLikeCondition(String varName, Collection ids, int limit,boolean addWildcard)
    {
        StringBuffer out=new StringBuffer();
        if(ids.size()==0)
            return "0=1";
        out.append("(");
        String wildCard=(addWildcard?"%":"");
                
        //while(in.hasNext() && count++ < limit)
        int count=0;
        for(Iterator in=ids.iterator();in.hasNext() && (limit==-1 || count < limit);count++)
        {
            out.append(varName+" ilike '"+wildCard+in.next()+wildCard+"'");
            if(in.hasNext() && (limit==-1 || count < limit))
                out.append(" OR ");
        }
        out.append(")");
        return out.toString();
    }
    public static String buildDescriptionCondition(String varName, Collection ids)
    {
        
        Iterator in=ids.iterator();
        StringBuffer conditions=new StringBuffer();
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
                conditions.append(" ( "+varName+" "+ILIKE+" '%"+temp+"%') ");

                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        return conditions.toString();
    }
   
    public static boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
    
    public static KeyTypeUser.KeyType findCommonKeyType(KeyTypeUser.KeyType[] searchKeys,KeyTypeUser.KeyType[] dataviewKeys)
    {
        //log.debug("comparingin "+printArray(searchKeys)+" to "+printArray(dataviewKeys));
        KeyTypeUser.KeyType common=null;
        //simple method
        for(int i=0;i<searchKeys.length;i++) //prefer search keys
            for(int j=0;j<dataviewKeys.length;j++)
                if(searchKeys[i]==dataviewKeys[j])
                {
                    common=searchKeys[i]; 
                    break;
                }
        if(common==null)
        {
            log.warn("could not find a common key");
            log.warn("possable search keys: "+printArray(searchKeys));
            log.warn("possable dataview keys: "+printArray(dataviewKeys));
        }
        else
            log.debug("found common key "+common);
        return common; // -1 indicates an error, will eventually cause an UnsupportedKeyType exception.
    }
    public static boolean checkType(KeyTypeUser ktu, KeyTypeUser.KeyType keyType)
    {
        return checkKeyType(ktu.getSupportedKeyTypes(),keyType);
    }
    public static boolean checkKeyType(KeyTypeUser.KeyType[] keys, KeyTypeUser.KeyType keyType)
    {
        if(keyType == KeyTypeUser.KeyType.ANY)
            return true;
        for(int i=0;i<keys.length;i++)
            if(keyType == keys[i])
                return true;
        return false;
    }

    public static String[] getStringArray(java.sql.Array a)
    {
        String[] strings;
        try{
            if(a==null)
                strings=new String[]{};
            else
                strings=(String[])(a.getArray());            
        }catch(java.sql.SQLException e){
            log.warn("exception while grabbing array: "+e);
            strings=new String[]{};
        }        
        return strings;
    }
    public static int[] getIntArray(java.sql.Array a)
    {
        int[] values=null;
        try{
            if(a==null)
                values=new int[]{};
            else 
                values=(int[])(a.getArray());                        
        }catch(java.sql.SQLException e){
            log.warn("exception while grabbing array: "+e);
            values=new int[]{};
        }        
        return values;
    }
    public static double[] getDoubleArray(java.sql.Array a)
    {
        double[] values=null;
        try{
            if(a==null)
                values=new double[]{};
            else 
                values=(double[])(a.getArray());                        
        }catch(java.sql.SQLException e){
            log.warn("exception while grabbing array: "+e);
            values=new double[]{};
        }        
        return values;
    }
    
//    public static void printHeader(PrintWriter out, boolean b)
//    {//print the gcd header
//        printHeader(out,"",b);
//    }
//    public static void printHeader(PrintWriter out, String title,boolean b)
//    {//print the gcd header
//        HeaderBean header=new HeaderBean();
//        header.setWriter(out);
//        header.setPageTitle(title);
//        header.setLoggedOn(b);
//        header.printGCDHeader();        
//    }

//    public static void printUnknownHeader(PrintWriter out,boolean b)
//    {
//        HeaderBean header=new HeaderBean();
//        header.setWriter(out);
//        header.setLoggedOn(b);
//        header.printUnknownsHeader();
//    }

}

