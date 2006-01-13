    <%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>JSP Page</title></head>
    <body>

        <jsp:useBean id='common' class='servlets.Common' scope='application'/>
        <jsp:useBean id='bean' class='servlets.PfamOptionsBean' scope='request'/>
        
        
        <%  common.printHeader(out,"Cluster Retrieval");   
            String input=request.getParameter("input");
            if(input==null)
                input="";  
        %>
        
        <table border='0' align='center' width='400' cellspacing='10'>
            <tr>
                <td>
                    <li><a href='QueryPageServlet?searchType=Cluster Id&displayType=seqView&inputKey=<%=input%>'>Retrieve</a> all members of this cluster.</li>
                </td>
            </tr>
            <tr>
                <td>
                    <li><a href='index.jsp?fieldName=Cluster Id&limit=0&input=<%=input%>'>Send</a>
                    query syntax for retrieving all members of
                    this cluster to search page. This 'loop strategy'
                    provides options for query refinement.
                    </li>
                </td>
            </tr>
        </table>
                
    </body>
</html>
