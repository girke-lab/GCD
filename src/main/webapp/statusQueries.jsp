<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id="bean" scope="session" class="servlets.StatusQueriesBean" />         
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />
<jsp:useBean id='common' class='servlets.Common' scope='application' />
<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.POND);
   header.printStdHeader(out,"", request.getRemoteUser()!=null);
%>
             
        
                
            <h1 align='center'>Difference Tracking Table</h1>    
            <center><%  common.printUnknownsSearchLinks(out); %></center>
            <div class='test'>
                <%= bean.printTrackingTable() %>
                UD: Unknown Descriptor
            </div>
    <% header.printFooter(); %>   
</html>
