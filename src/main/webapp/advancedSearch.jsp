<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />
<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
   header.printStdHeader(out,"Advanced Search", request.getRemoteUser()!=null);
%>    

        

         <%             
            bean.setDefaultDatabase("common");
            bean.setDatabase("common");
            bean.initPage(application,request,response);
        %>
        <p>
        <center>
            <a href='QueryAdmin?database=<%=bean.getDatabase()%>'>Admin Page</a>
        </center>
        <p>
        <%
            bean.printMessage(out);
                        
            bean.drawSearchForm(out); //,new String[]{"common","unknowns","unknowns2"});            
        %>

        <% bean.printUsage(out);     %>

    <% header.printFooter(); %>
</html>
