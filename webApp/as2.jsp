<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>JSP Page</title></head>
    <body>

        <jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
        <jsp:useBean id='common' class='servlets.Common' />
        <%
            common.printHeader(out,"Advanced Search 2");
            bean.setDefaultDatabase("unknowns2");
            bean.setDatabase("unknowns2");
            bean.initPage(application,request,response);
            bean.setPrintAmdinControls(true);
            
            bean.printMessage(out);
                        
            bean.drawSearchForm(out,new String[]{"common","unknowns","unknowns2"});            
        %>
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
    </body>
</html>
