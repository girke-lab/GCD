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
                    <h4> Usage: </h4>
                    <p>
                    Most operators work as expected.  The 'ILIKE' and 'NOT ILIKE' operators can be used
                    to match patterns.  The symbol '%' will match any number of characters,
                    while the '_' will match any one character. These operators should only 
                    be used for text fields. Specifically, they will not work on numeric fields.
                    <p>
                    The sort column will also determine the result view. Sorting by sequence
                    attributes will result in a sequence oriented view, while sorting
                    by cluster attributes will result in a cluter oriented view. Sorting
                    by go number or database will give a sequence oriented view.
                    <p>
                    The limit field determines the total number of results returned, which 
                    will be displayed 50 at a time. 
                </td>
            </tr>
        </table>
    <% header.printFooter(); %>
</html>
