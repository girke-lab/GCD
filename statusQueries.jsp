<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Difference Tracking</title>
        <style type='text/css'>
            .test a {color: #006699}
            .test a:hover {background-color: #AAAAAA}
        </style>
    </head>
    <body>

        <jsp:useBean id="bean" scope="session" class="servlets.StatusQueriesBean" />         
        <jsp:useBean id='common' class='servlets.Common' />

        <% common.printUnknownHeader(out); %>
            <h1 align='center'>Difference Tracking Table</h1>    
            <center><%  common.printUnknownsSearchLinks(out); %></center>
            <div class='test'>
                <%= bean.printTrackingTable() %>
                UD: Unknown Descriptor
            </div>
        <% common.printUnknownFooter(out); %>
    </body>
</html>
