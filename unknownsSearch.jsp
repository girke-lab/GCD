<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Plant Unknowns</title></head>
    <body>

        <jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
        <jsp:useBean id='common' class='servlets.Common' />


        <%
            bean.setDefaultDatabase("unknowns2");
            //bean.setDatabase("unknowns");
            bean.initPage(application,request,response);
        %>



        <p><p>  
            <% common.printUnknownHeader(out); %>

                <center><%  common.printUnknownsSearchLinks(out); %></center
                <P>
                <%  bean.printMessage(out); %>
                <p align='center'>
                     &nbsp&nbsp&nbsp&nbsp 
                    <a href='QueryAdmin?database=<%=bean.getDatabase()%>'>Admin Page</a>                        
                </p>
                <%  bean.drawSearchForm(out,new String[]{"unknowns","unknowns2"}); %>



                <h4> Usage: </h4>
                <p>
                Most operators work as expected.  The LIKE and NOT LIKE operators can be used
                to match patterns.  The symbol '%' will match any number of characters,
                while the '_' will match any one character. 
                <p>                    
                The limit field determines the total number of results returned.                    

            <% common.printUnknownFooter(out); %>
    </body>
</html>
