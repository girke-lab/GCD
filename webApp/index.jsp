<%@page contentType="text/html"%>
<html>
<head>
    <title>Arabidopsis Sequence Retrieval</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">



</head>
<body bgcolor="#FFFFFF" leftmargin="1" topmargin="1" marginwidth="1" marginheight="1" >

     <% String input=request.getParameter("input");
        String limit=request.getParameter("limit");
        String fieldName=request.getParameter("fieldName");
        if(input==null)
            input="";
        if(limit==null)
            limit="0";
        if(fieldName==null)
            fieldName="Description";
    %>   

<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<% common.printHeader(out,"Single or Batch Search"); %>

	<FORM NAME='form1' METHOD=POST ACTION='/databaseWeb/QueryPageServlet'   >   
            <TABLE width='70%' align='center' border='0'>
                <TR>
                    <TD  align='center'>
<!--                        1.Obtain selected sequences for one or many ID numbers.
                        <BR>
                        2.Text search of putative gene function. Booleans may be used: and, or, not.
                        <BR>
                        3.For perfomance reasons, the maximum number of query results is 1000.
                         To download entire datasets, please use our 
                        <A href='ftp://138.23.191.152/pub/'> FTP site.</A>
-->
                        &nbsp<P>
                        GCD is a database for genome-wide sequence <BR> family mining in Arabidopsis and rice.
                        <P>
                        <A href='about.jsp#search'>
                            How to Search GCD</A>
                    </TD>                    
                </TR>  
                <TR >
                    <TD  align='center'>
                        <B>Search string</B>                      
                        <BR>
                        <TEXTAREA NAME="inputKey" cols='40' rows='10'><%=input%></TEXTAREA>                                                                 
                    </TD>    
                    <TD>
                        Databases to use:
                        <BR>
                        <INPUT type=checkbox name='dbs' value='0' checked>
                        Arabidopsis
                        <BR>
                        <INPUT type=checkbox name='dbs' value='1' checked>
                        Rice     
                    </TD> 
                </TR>
                <TR>                    
                    <TD  align='center' >

                        Search by
                        <SELECT name='searchType'>
                            <OPTION <%if(fieldName.equals("Id"))
                                        out.println("selected"); %>>Id
                            <OPTION <%if(fieldName.equals("Description"))
                                        out.println("selected"); %>>Description
                            <OPTION <%if(fieldName.equals("Cluster Id"))
                                        out.println("selected"); %>>Cluster Id
                            <OPTION <%if(fieldName.equals("Cluster Name"))
                                        out.println("selected"); %>>Cluster Name
                            <OPTION <%if(fieldName.equals("GO Number"))
                                        out.println("selected"); %>>GO Number

                        </SELECT>
                        <BR>                       
                    </TD>
                    <TD>
                        Records per page:&nbsp&nbsp 
                        <INPUT name="rpp" value="50" size='5'>
                    </TD>

                </TR>    
                <TR >                    
                    <TD  align='center'>
                       <INPUT TYPE=submit value='Submit Query' ><!-- onClick="send();"> -->
                               
                    </TD>
                </TR>   
                <tr>
                    <td colspan='1'>
                        &nbsp<p>
                        Browser specific behavior of GCD: Internet Explorer requires 'page refreshing'
                        to view the content of a previous page.
                    </td>
                </tr>
            </TABLE> 
            <P>      
	</FORM>
</body>
</html>
