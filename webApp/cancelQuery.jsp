<%@page contentType="text/html"%>
<html>
    <head>
        <title>Servlet</title>
        <script language="JavaScript">
             function send()
             {
                document.form2.submit();    
                opener.result.close();
                window.close();
             }
        </script>
    </head>
    <body>
   
 <%
        //HttpSession session;
        session=request.getSession();
        String status=request.getParameter("status");
        System.out.println("status="+status);
        if(status.compareTo("open")==0)         
            session.setAttribute("QueryStatus",new Boolean(true));
        else if(status.compareTo("closed")==0)
            session.setAttribute("QueryStatus",new Boolean(false));
    %>
        <H3>Processing request...</H3><P>
        <FORM NAME='form2' ACTION='/databaseWeb/cancelQuery.jsp'>
            <INPUT type=hidden name='status' value='closed'>
            <INPUT type=button value='Cancel Query' onClick='send();'>
        </FORM>





    </body>
</html>
