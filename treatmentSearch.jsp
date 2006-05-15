<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />

<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.PED);
   header.printStdHeader(out,"Differential Expression Search", request.getRemoteUser()!=null);
%>

        <%
            bean.setDefaultDatabase("treatment");            
            bean.initPage(application,request,response);
        %>

        <table border='0'>
            <tr>
                <td>
                    <center><%  common.printUnknownsSearchLinks(out); %></center>
                </td>
            </tr>
            <tr>
                <td>                
                    <%  bean.printMessage(out); %>
                </td>
            </tr>
            <tr>
                <td align='center'>
                        
                        <a href='QueryAdmin?database=<%=bean.getDatabase()%>'>Admin Page</a>                        
                </td>
            </tr>
            <tr>
                <td align='center'>
                    <%  bean.drawSearchForm(out,new String[]{"unknowns","unknowns2","treatment"}); %>
                </td>
            </tr>
            <tr>
                <td>
            

                    <h4> Usage: </h4>
                    <p>
                    Most operators work as expected.  The LIKE and NOT LIKE operators can be used
                    to match patterns.  The symbol '%' will match any number of characters,
                    while the '_' will match any one character. 
                    <p>                    
                    The limit field determines the total number of results returned.                    
                </td>
            </tr>
        </table>

    <% header.printFooter(); %>
</html>
