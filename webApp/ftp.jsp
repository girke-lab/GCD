<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>JSP Page</title></head>
    <body>
        <jsp:useBean id='common' class='servlets.Common' scope='application'/>
        <% common.printHeader(out,"Data Downloads"); %>
        
        <ul>
            <li><a href='data/clusters'>Cluster data</a></li>
            <li><a href='ftp://138.23.191.152/pub/Cluster_data/mul.tgz'>Alignments in mul format</a></li>
            <li><a href='ftp://138.23.191.152/pub/Cluster_data/dnd.tgz'>Trees in dnd format</a></li>
            
       </ul>
    </body>
</html>
