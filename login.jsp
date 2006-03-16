<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id='header' class='servlets.beans.HeaderBean' />        

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<%
   //first make sure we are accessed through https.
   if(!request.isSecure())
   {
       //out.println("request url: "+request.getScheme()+"://"+request.getServerName()+
       //        ":"+request.getServerPort()+""+request.getRequestURI()+"/"+request.getQueryString()+"<br>");       
       //out.println(request.getRequestURI()+"<br>");
       //out.println(request.getRequestURL()+"<br>");
       //out.println(request.getPathInfo()+"<br>");
       //out.println(request.getPathTranslated()+"<br>");
       java.net.URL secureUrl=new java.net.URL("https",request.getServerName(),
                                      //request.getServerPort(),
                                      request.getRequestURI()+
                            (request.getQueryString()==null?"":"?"+request.getQueryString()));
       //out.println("new url: "+secureUrl);
       if(request.getServerPort()!=8084)
            response.sendRedirect(secureUrl.toString());
   }
   
    // Redirect to custom authentication and remove attributes to prevent infinite loop
    if (((request.getRemoteUser()==null)) && (!(session.getAttribute("j_username")==null))){
        String redirectURL = "j_security_check"+  // "/enterprise/jsp/" + session.getAttribute("j_security_check") +
            "?j_username=" + session.getAttribute("j_username") + "&j_password=" + session.getAttribute("j_password");
        session.removeAttribute("j_username");
        session.removeAttribute("j_password");
        session.removeAttribute("j_security_check");
        response.sendRedirect(response.encodeRedirectURL(redirectURL));
   }
%>
<html>   
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.COMMON);
   header.printStdHeader(out,"Login",request.getRemoteUser()!=null);
%>
   
        
        <% String action=request.getParameter("action"); %>
        
        
        <p>
        
        
         <%  // Determine whether normal form-based authentication or custom drop-down should be used
            boolean directAccess = false;
            
            //if(request.isSecure())
            //    out.println("this page is secure");
            //else
            //    out.println("this page is not secure");
            
            // Referer is used to determine if URL was entered directly
            String referer = request.getHeader("Referer");
            System.out.println("referer="+referer);
            String baseURL = "://" + request.getServerName() + request.getContextPath();
            System.out.println("baseURL: "+baseURL);
            
            for(java.util.Enumeration e=request.getParameterNames();e.hasMoreElements();)
            {
                String name=(String)e.nextElement();
                System.out.println(name+" = "+request.getParameter(name));
            }
                
            
            if ( ("logout".equals(action)) ||
                 (referer == null) ||
                 //(referer.indexOf(baseURL))==-1) ||
                ((!("protected".equals(action))) &&
                (!("error".equals(action)))))
            {
                System.out.println(" directAccess true");
                directAccess = true;
        %>
                <FORM ACTION="LogonRouterServlet" METHOD="POST">
                <INPUT TYPE="HIDDEN" NAME="j_security_check" VALUE="/j_security_check"/>
                <input type='hidden' name='originator' value='<%=referer%>'>
        <%  } else 
            {          
                System.out.println(" indirect access ");     %>                
                <FORM ACTION="j_security_check" METHOD="POST">
        <%  } %>

                        
            <table border='0' align='center'  >
                <% if(action!=null && action.equals("error")){ %>
                    <tr><td align='center'  colspan='2'><font color='#FF0000' size='+1'>Login failed</font></td></tr>                    
                <%}%>

                <% if(action!=null && action.equals("log_off")){ 
                    session.invalidate();
                %>
                    <tr><td align='center'  colspan='2'><font color='#FF0000' size='+1'>You have been logged off</font></td></tr>                    
                <%}%>

                <tr>
                    <td align='right'>User name: </td>
                    <td align='left'><input type='text' name='j_username'></td>
                </tr>
                <tr>
                    <td align='right'>Password:</td>
                    <td align='left'><input type='password' name='j_password'></td>
                </tr>
                <tr>
                    <td colspan='2' align='center' ><input type='submit' value='Login'></td>
                </tr>
            </table>            
            
        </form>
    
<% header.printFooter(); %>
</html>
