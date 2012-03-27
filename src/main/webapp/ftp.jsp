<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />
<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
   header.printStdHeader(out,"Data Downloads", request.getRemoteUser()!=null);
%>   
        
        <% 
            header.setWriter(out);
            header.setPageTitle("Data Downloads");
            header.setLoggedOn(request.getRemoteUser()!=null);
            header.printGCDHeader();
        %>   
                        
        
        <ul>
            <li></li>            
            
       </ul>
    <% header.printFooter(); %>
</html>
