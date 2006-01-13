<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>



<html>
    <head>
        
        <title>JSP Page</title>
    </head>
    <body>
        <jsp:useBean id='common' class='servlets.Common' scope='application'/>
        <jsp:useBean id='bean'   class='servlets.SimpleSearchBean' scope='page'/>

        <% bean.initPage("unknownsBasicSearch.jsp",application,request,response); %>           

        <% common.printUnknownHeader(out); %>

            <%  common.printUnknownsSearchLinks(out); %>
            <P>
            <%  bean.printMessage(out); %>


            <%  bean.drawForm(out,new String[]{"unknowns2View"},new String[]{"POND"}); %>		                                                                 

        <% common.printUnknownFooter(out); %>
   
    
    </body>
</html>
