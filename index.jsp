<%@page contentType="text/html"%>
<html>
<head>
    <title>GCD</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

</head>
<body bgcolor="#FFFFFF" leftmargin="1" topmargin="1" marginwidth="1" marginheight="1" >
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<jsp:useBean id='bean'   class='servlets.SimpleSearchBean' scope='page'/>

    <% bean.initPage("index.jsp",application,request,response); %>   

    <% common.printHeader(out,"Single or Batch Search"); %>

	
            <TABLE width='70%' align='center' border='0'>
                <TR>
                    <TD  align='center'>                                              
                        &nbsp<P>
                        GCD is a database for genome-wide sequence <BR> family mining in Arabidopsis and Rice.
                        Detailed information about this resource <BR> is available on the <a href='about.jsp'>ReadMe</a> page
                        and the associated publication in <a href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15888677&query_hl=2'>
                        Plant Physiology: 138, 47-54</a>.
                        <P>
                        <font color='#FF0000'>News: incorporation of expression data from 1309 Affymetrix chips. TESTING</font>
                        <P>
                        <A href='about.jsp#search'>
                            How to Search GCD</A>                        
                    </TD>                    
                </TR>
                <TR>
                    <TD align='center'>
                        <% bean.printMessage(out); %>
                    </TD>
                </TR>
                <TR>
                    <TD>
                        <% bean.drawForm(out); %>
                    </TD>
                </TR>   
                <TR>
                    <TD  align='center'>
                        &nbsp<P>
                        <A href='http://bioinfo.ucr.edu/projects/Unknowns/external/index.html'>
                        This Project is partially funded by the NSF 2010 grant # IOB-0420152
                        </a>
                   </td>
                </TR>
            </TABLE> 
            <P>      	
</body>
</html>
