<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Login</title>
    </head>
    <body>
        <jsp:useBean id='common' class='servlets.Common' scope='application'/>        

    
        <% common.printHeader(out,"Logon"); %>
        <% String status=request.getParameter("status"); %>
        
        <% if(status!=null && status.equals("error")){ %>
            <font color='#FF0000' size=+1>Logon failed</font>
        <%}%>
        
        <p>
        <form method='post' action='j_security_check' >
            <table border='0' align='center'  >
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
    
    
    
    
    </body>
</html>
