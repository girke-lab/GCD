<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Difference Tracking</title></head>
    <body>

        <jsp:useBean id="bean" scope="session" class="servlets.StatusQueriesBean" />         
        <jsp:useBean id='common' class='servlets.Common' />

        <h2 align='center'>Difference Tracking Table</h2>
        <!--
        <u><h4 align='left'>Queries</h4></u>
        <table bgcolor='<%=common.dataColor%>' width='100%'
               align='center' border='1' cellspacing='0' cellpadding='0'>
            <tr bgcolor='<%=common.titleColor%>'>
                <th>Name</th><th>Description</th>
            </tr>
            <%= bean.printQueries() %>
        </table>

        <u><h4 align='left'>Comparisons</h4></u>
        <table bgcolor='<%=common.dataColor%>' width='100%'
               align='center' border='1' cellspacing='0' cellpadding='0'> 
            <tr bgcolor='<%=common.titleColor%>'>
                <th colspan='4'>Query A</th><th colspan='4'>Query B</th>
                <th colspan='2'>Stats</th>
            </tr>
            <tr bgcolor='<%=common.titleColor%>'>
                <th>Name</th><th>Run on</th><th>Size</th><th>Notes</th>
                <th>Name</th><th>Run on</th><th>Size</th><th>Notes</th>
                <th>Added</th><th>Removed</th> 
            </tr>
            <%= bean.printComparisons() %>
        </table>
        --> 
        <%= bean.printTrackingTable() %>
        UD: Unknown Descriptor
    </body>
</html>
