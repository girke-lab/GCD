<%@page contentType="text/html"%>
<html>
<head>
    <title>Arabidopsis Sequence Retrieval</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">



</head>
<body bgcolor="#FFFFFF" leftmargin="1" topmargin="1" marginwidth="1" marginheight="1" onLoad="MM_preloadImages('images/home_down.gif','images/home_over.gif','images/training_down.gif','images/training_over.gif','images/blast_down.gif','images/blast_over.gif','images/gcg_down.gif','images/gcg_over.gif','images/tools_down.gif','images/tools_over.gif','images/database_down.gif','images/database_over.gif','images/bioinfo_down.gif','images/bioinfo_over.gif','images/links_down.gif','images/links_over.gif')">

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
<
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<% common.printHeader(out); %>

	<FORM NAME='form1' METHOD=POST ACTION='/databaseWeb/QueryPageServlet'   >   
            <TABLE width='70%' align='center' border='0'>
                <TR>
                    <TD colspan='2' align='center'>
<!--                        1.Obtain selected sequences for one or many ID numbers.
                        <BR>
                        2.Text search of putative gene function. Booleans may be used: and, or, not.
                        <BR>
                        3.For perfomance reasons, the maximum number of query results is 1000.
                         To download entire datasets, please use our 
                        <A href='ftp://138.23.191.152/pub/'> FTP site.</A>
-->
                        <A href='http://bioinfo.ucr.edu/projects/internal/PlantFam/Readme/about.html#search'>
                            How to Search this Site</A>
                    </TD>                    
                </TR>  
                <TR >
                    <TD colspan='2' align='center'>
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
                    <TD colspan='2' align='center' >

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
                    <TD colspan='2' align='center'>
                       <INPUT TYPE=submit value='Submit Query' ><!-- onClick="send();"> -->
                               
                    </TD>
                </TR>   
            </TABLE> 
            <P>            
	</FORM>
</body>
</html>
