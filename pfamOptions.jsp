<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<html>
    <head><title>JSP Page</title></head>
    <body>
        <jsp:useBean id='common' class='servlets.Common' scope='application'/>
        <jsp:useBean id='bean' class='servlets.PfamOptionsBean' scope='request'/>
        
        <% bean.processesInput(request); %>   
        <% common.printHeader(out,"Pfam Options"); %>
        
        <ul>
            <li>Retrieve all proteins containing this Pfam domain            
                <ul>                
                    <% bean.printDomainSearchLinks(out); %>
                    <!--<li><a href='index.jsp?input=<%=bean.getAccession()%>&fieldName=Cluster Id'><%=bean.getAccession()%></a></li>-->
                </ul>            
            </li>
            <li>Go to Pfam
                <ul>
                    <% bean.printPfamLinks(out);  %>
                </ul>
            </li>
        </ul>
    </body>
</html>
