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
        drawForm(w, new String[]{"seqView","unknowns2View"},
                    new String[]{"GCD","POND"});
    }
    public void drawForm(Writer w,String[] displays,String[] names)
    {
        PrintWriter out=new PrintWriter(w);
        out.println("<FORM NAME='form1' METHOD=POST ACTION='QueryPageServlet'   >   ");
        out.println("<INPUT type=hidden name='origin_page' value='"+pageName+"'>");
        out.println("<TABLE border='0' width='100%' align='center'>" +
        "            <TR>" +
        "                    <TD  align='center'>" +
        "                        <B>Search string</B>                      " +
        "                        <BR>" +
        "                        <TEXTAREA NAME='inputKey' cols='40' rows='10'>"+input+"</TEXTAREA>                                                                 " +
        "                    </TD>    " +
        "                    <TD>                                                " +
        "                        Databases to use:" +
        "                        <BR>" +
        "                        <INPUT type=checkbox name='dbs' value='0' checked>" +
        "                        Arabidopsis" +
        "                        <BR>" +
        "                        <INPUT type=checkbox name='dbs' value='1' checked>" +
        "                        Rice     " +
        "                        <P>"   );
        
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
        "                    </TD> " +
        "                </TR>" +
        "                <TR>                    " +
        "                    <TD  align='center' >" +
        "                        Search by" +
        "                        <SELECT name='searchType'>" +
        "                            <OPTION "+(fieldName.equals("Id")?"selected":"")+">Id"+
        "                            <OPTION "+(fieldName.equals("Description")?"selected":"")+">Description"+
        "                            <OPTION "+(fieldName.equals("Cluster Id")?"selected":"")+">Cluster Id"+
        "                            <OPTION "+(fieldName.equals("Cluster Name")?"selected":"")+">Cluster Name"+
        "                            <OPTION "+(fieldName.equals("GO Number")?"selected":"")+">GO Number"+
        "                            <OPTION "+(fieldName.equals("GO Text")?"selected":"")+">GO Text"+
        "                        </SELECT>" +
        "                        <BR> " +
        "                    </TD>" +
        "                    <TD>" +
        "                        Records per page:&nbsp&nbsp " +
        "                        <INPUT name='rpp' value='50' size='5'>" +
        "                    </TD>" +
        "                </TR>  " +
        "                <TR >  " +
        "                    <TD  align='center'>" +
        "                       <INPUT TYPE=submit value='Submit Query' >" +
        "                    </TD>" +
        "                </TR> </TABLE></FORM>  " );
                        
    
    }
    public void printMessage(Writer w)
    {
        PrintWriter out=new PrintWriter(w);
        
        if(errMessage!=null && !errMessage.equals(""))
            out.println("<span align='center'><font color='#FF0000' size=+1>"+
                    errMessage+"</font></span>");
        
    }
   
}
