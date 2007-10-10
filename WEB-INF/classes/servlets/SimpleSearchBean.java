/*
 * SimpleSearchBean.java
 *
 * Created on June 22, 2005, 2:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author khoran
 */
public class SimpleSearchBean
{
    ServletContext servletContext=null;
    HttpServletRequest request=null;
    HttpServletResponse response=null;
        
    String input,limit,fieldName,errMessage,pageName;
    
    /** Creates a new instance of SimpleSearchBean */
    public SimpleSearchBean()
    {
    }
    
    
    public void initPage(String pageName,ServletContext sc,HttpServletRequest rq,HttpServletResponse rs)
    {
        this.pageName=pageName;
        servletContext=sc;
        request=rq;
        response=rs;
    
        input=request.getParameter("input");
        limit=request.getParameter("limit");
        fieldName=request.getParameter("fieldName");
        errMessage=request.getParameter("error_message");
        
        if(input==null)
            input="";
        if(limit==null)
            limit="0";
        if(fieldName==null)
            fieldName="Description";
       
    }
    public void drawForm(Writer w)
    {
        //drawForm(w, new String[]{"seqView","unknowns2View"}, new String[]{"GCD","POND"});
        drawForm(w, new String[]{"seqView"}, new String[]{"GCD"});
    }
    public void drawForm(Writer w,String[] displays,String[] names)
    {
        PrintWriter out=new PrintWriter(w);
        out.println("<FORM  METHOD=POST ACTION='QueryPageServlet'   >   ");
        out.println("<INPUT type=hidden name='origin_page' value='"+pageName+"'>");
        out.println("<TABLE border='0' width='100%' align='center'>\n" + 
        "            <TR>\n" +
        "                    <TD  align='center'>\n" +
        "                        <B>Search string</B>                      \n" +
        "                        <BR>\n" +
        "                        <TEXTAREA NAME='inputKey' cols='40' rows='10'>"+input+"</TEXTAREA>                                                                 \n" +
        "                    </TD>    \n" +
        "                    <TD>                                                \n" +
        "                        Databases to use:\n" +
        "                        <BR>\n" +
        "                        <INPUT type=checkbox name='dbs' value='0' checked>\n" +
        "                        Arabidopsis\n" +
        "                        <BR>\n" +
        "                        <INPUT type=checkbox name='dbs' value='1' checked>\n" +
        "                        Rice     \n" +
        "                        <P>\n"   );
        
        if(displays.length==1)
            out.println("<INPUT type=hidden name='displayType' value='"+displays[0]+"'>");
        else
        {
            out.println("Result View:  <SELECT name='displayType'>" );
            for(int i=0; i<displays.length && i<names.length; i++)
                out.println("<OPTION value='"+displays[i]+"'>"+names[i]);        
            out.println("</SELECT>");
        }
            
        out.println(
        "                    </TD> \n" +
        "                </TR>\n" +
        "                <TR>                    \n" +
        "                    <TD  align='center' >\n" +
        "                        Search by\n" +
        "                        <SELECT name='searchType'>\n" + //TODO: these options should be pulled from the queryPage.properties file
        "                            <OPTION "+(fieldName.equals("Id")?"selected":"")+" value='Id'>Gene ID\n"+
        "                            <OPTION "+(fieldName.equals("Probe Set")?"selected":"")+" value='Probe Set'>Affy ID (ATH1)\n"+
        "                            <OPTION "+(fieldName.equals("Description")?"selected":"")+" value='Description'>Gene Description\n"+
        "                            <OPTION "+(fieldName.equals("Cluster Id")?"selected":"")+" value='Cluster Id'>Protein Cluster ID\n"+
        "                            <OPTION "+(fieldName.equals("Cluster Name")?"selected":"")+" value='Cluster Name'>Protein Cluster Name\n"+
        "                            <OPTION "+(fieldName.equals("GO Number")?"selected":"")+" value='GO Number'>GO ID\n"+
        "                            <OPTION "+(fieldName.equals("GO Text")?"selected":"")+" value='GO Text'>GO Term\n"+
        "                        </SELECT>\n" +
        "                        <BR> \n" +
        "                    </TD>\n" +
        "                    <TD>\n" +
        "                        Records per page:&nbsp&nbsp \n" +
        "                        <INPUT name='rpp' value='50' size='5'>\n" +
        "                    </TD>\n" +
        "                </TR>  \n" +
        "                <TR >  \n" +
        "                    <TD  align='center'>\n" +
        "                       <INPUT TYPE=submit value='Submit Query' >\n" +
        "                    </TD>\n" +
        "                </TR>\n </TABLE>\n</FORM>  " );
                        
    
    }
    public void printMessage(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        
        if(errMessage!=null && !errMessage.equals(""))
            out.println("<span align='center'><font color='#FF0000' size=+1>"+
                    errMessage+"</font></span>");
        
    }
   
}
