<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<html>
<head>
    <title>GCD</title>

    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <meta Http-Equiv='Content-Type' content='text/html'; charset='UTF-8'>
    <meta Http-Equiv='Cache-Control' Content='no-cache'/>
    <meta Http-Equiv='Pragma' Content='no-cache'/>
    <meta Http-Equiv='Expires' Content='0'/>
    
    <style type='text/css'>	
        body { color: #000000; font-family: avantgarde, sans-serif; font-size: 11pt} 	
        a { color: #006699} 	
        a:hover { background-color: #AAAAAA} 	
        h1, h2, h3, h4, h5, h6 { font-weight: bold}
        h1 { font-size: 180%} 
        h2 { font-size: 150%} 	
        h3 { font-size: 120%} 	
        h4 { font-size: 120%} 	
        pre { font-family: FreeMono, monospace; font-size: 10pt} 	
        tt { font-family: FreeMono, monospace; font-size: 10pt} 
    </style>
</head>	

<body bgcolor='#fefefe' text='#000000' link='#006699' vlink='#003366'>




        <jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
        <jsp:useBean id='common' class='servlets.Common' />
        <h1 align='center'>Query Administration</h1>
        <%
            //common.printHeader(out,"Query Administration");
            
            bean.setDefaultDatabase("unknowns2");
            bean.setDatabase("unknowns2");
            bean.initPage(application,request,response);
            bean.setPrintAmdinControls(true);
            bean.setPrintSql(true);
            
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
