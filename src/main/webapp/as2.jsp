<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />        

<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.COMMON);
   header.printStdHeader(out,"Query Administration", request.getRemoteUser()!=null);
%>   




        
        <%             
            bean.setDefaultDatabase("unknowns2");
            bean.setDatabase("unknowns2");
            bean.initPage(application,request,response);
            bean.setPrintAmdinControls(true);
            bean.setPrintSql(true);
        %>
        <P>
        
        
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
                <td align='left'>
                    <% bean.drawSearchForm(out,new String[]{"common","unknowns","unknowns2","treatment"}); %>
                    
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
