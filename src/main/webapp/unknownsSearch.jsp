<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />

<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.POND);
   header.printStdHeader(out,"Advanced Search", request.getRemoteUser()!=null);
%>

        <%
            bean.setDefaultDatabase("unknowns2");
            //bean.setDatabase("unknowns");
            bean.initPage(application,request,response);
        %>



        
                    

                <center><%  common.printUnknownsSearchLinks(out); %></center>
                <P>
                <%  bean.printMessage(out); %>
                <center>
                     &nbsp&nbsp&nbsp&nbsp 
                    <a href='QueryAdmin?database=<%=bean.getDatabase()%>'>Admin Page</a>                        
                </center>
                <%  bean.drawSearchForm(out,new String[]{"unknowns","unknowns2","treatment"}); %>



                <% bean.printUsage(out);     %>                

    <% header.printFooter(); %>
</html>
