<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id='header' class='servlets.beans.HeaderBean' />
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<jsp:useBean id='bean'   class='servlets.SimpleSearchBean' scope='page'/>


<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.POND);
   header.printStdHeader(out,"Search", request.getRemoteUser()!=null);
%>

        <% bean.initPage("unknownsBasicSearch.jsp",application,request,response); %>           

                

            <center><%  common.printUnknownsSearchLinks(out); %></center>
            <P>
            <%  bean.printMessage(out); %>


            <%  bean.drawForm(out,new String[]{"unknowns2View"},new String[]{"POND"}); %>		                                                                 

        <% header.printFooter(); %>
   
       
</html>
