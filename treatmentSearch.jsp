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
                        &nbsp&nbsp&nbsp&nbsp&nbsp
                        
                        <A  href='QueryPageServlet?searchType=seq_id&displayType=compCountsView&inputKey=hello' >
                            <font color='#FF0000'>    Select experiment and comparison</font>
                        </a>
                        
                </td>
            </tr>
            <tr>
                <td align='center'>
                    <%  bean.drawSearchForm(out,new String[]{"unknowns","unknowns2","treatment"}); %>
                </td>
            </tr>
            <tr>
                <td>            
                    <% bean.printUsage(out);     %>
                </td>
            </tr>
        </table>

    <% header.printFooter(); %>
</html>
