<%@page contentType="text/html"%>
<html>
<head>
    <title>Arabidopsis Sequence Retrieval</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript" type="text/JavaScript">
<!--
     function send()
     {
        var options="width=700,height=500";
        options+=",scrollbars=yes";
        options+=",status=yes";
        options+=",toolbar=yes";
        options+=",resize=yes";
        result=window.open("","rd",options);
        document.form1.submit();    
       // statusW=window.open("","sw","width=200,height=100");
       // checkWindow();
        statusW=null;
     }
     function checkWindow()
     {          
        if(result.document==null) //window closed
        {
            statusW.document.location.href="/databaseWeb/cancelQuery.jsp?status=closed";
            statusW.close();
            return; //break timer loop
        }
        else if(statusW.document==null) //do nothing, query will die on its own
            return; //breaks timer loop
        else if(result.document.dform!=null)
        {
            statusW.close();
            return;
        }    
        else 
            statusW.document.location.href="/databaseWeb/cancelQuery.jsp?status=open";
        setTimeout("checkWindow()",4000);
     }
/////////////////////////////////////////////////////////////////////
function MM_preloadImages() { //v3.0
  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
}

function MM_findObj(n, d) { //v4.01
  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
  if(!x && d.getElementById) x=d.getElementById(n); return x;
}

function MM_nbGroup(event, grpName) { //v6.0
  var i,img,nbArr,args=MM_nbGroup.arguments;
  if (event == "init" && args.length > 2) {
    if ((img = MM_findObj(args[2])) != null && !img.MM_init) {
      img.MM_init = true; img.MM_up = args[3]; img.MM_dn = img.src;
      if ((nbArr = document[grpName]) == null) nbArr = document[grpName] = new Array();
      nbArr[nbArr.length] = img;
      for (i=4; i < args.length-1; i+=2) if ((img = MM_findObj(args[i])) != null) {
        if (!img.MM_up) img.MM_up = img.src;
        img.src = img.MM_dn = args[i+1];
        nbArr[nbArr.length] = img;
    } }
  } else if (event == "over") {
    document.MM_nbOver = nbArr = new Array();
    for (i=1; i < args.length-1; i+=3) if ((img = MM_findObj(args[i])) != null) {
      if (!img.MM_up) img.MM_up = img.src;
      img.src = (img.MM_dn && args[i+2]) ? args[i+2] : ((args[i+1])? args[i+1] : img.MM_up);
      nbArr[nbArr.length] = img;
    }
  } else if (event == "out" ) {
    for (i=0; i < document.MM_nbOver.length; i++) {
      img = document.MM_nbOver[i]; img.src = (img.MM_dn) ? img.MM_dn : img.MM_up; }
  } else if (event == "down") {
    nbArr = document[grpName];
    if (nbArr)
      for (i=0; i < nbArr.length; i++) { img=nbArr[i]; img.src = img.MM_up; img.MM_dn = 0; }
    document[grpName] = nbArr = new Array();
    for (i=2; i < args.length-1; i+=2) if ((img = MM_findObj(args[i])) != null) {
      if (!img.MM_up) img.MM_up = img.src;
      img.src = img.MM_dn = (args[i+1])? args[i+1] : img.MM_up;
      nbArr[nbArr.length] = img;
  } }
}
//-->
</script>


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
<!--  
<table width="800" border="1" cellspacing="0" cellpadding="0">
<tr>
<td width="210" rowspan="2"><a href="http://www.cepceb.ucr.edu/" target="_blank"><img src="images/cepceb.jpg" alt="cepceb" width="210" height="90" border="0"></a></td>
<td width="590"><img src="images/bioinform.jpg" alt="Bioinformatics Core" width="590" height="70"></td>
</tr>
<tr>
<td height="29" valign="top"><div align="center"><a href="http://www.cepceb.ucr.edu/"></a><font face="Geneva, Arial, Helvetica, sans-serif"><a href="http://www.ucr.edu/" target="_blank">UC
        Riverside</a></font></div></td>
</tr>
<tr>
<td colspan="2"><table border="0" cellpadding="0" cellspacing="0">
<tr>
<td><a href="http://www.faculty.ucr.edu/~tgirke/index.html"  onClick="MM_nbGroup('down','group1','Home','images/home_down.gif',1)" onMouseOver="MM_nbGroup('over','Home','images/home_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/home_up.gif" alt="Home" name="Home" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/Workshops.htm"  onClick="MM_nbGroup('down','group1','training','images/training_down.gif',1)" onMouseOver="MM_nbGroup('over','training','images/training_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/training_up.gif" alt="Training" name="training" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/LocalBLAST.htm"  onClick="MM_nbGroup('down','group1','blast','images/blast_down.gif',1)" onMouseOver="MM_nbGroup('over','blast','images/blast_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/blast_up.gif" alt="BLAST" name="blast" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/Tools.htm"  onClick="MM_nbGroup('down','group1','tools','images/tools_down.gif',1)" onMouseOver="MM_nbGroup('over','tools','images/tools_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/tools_up.gif" alt="Tools" name="tools" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/Databases.htm"  onClick="MM_nbGroup('down','group1','database','images/database_down.gif',1)" onMouseOver="MM_nbGroup('over','database','images/database_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/database_up.gif" alt="Databases" name="database" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/GCG.htm"  onClick="MM_nbGroup('down','group1','gcg','images/gcg_down.gif',1)" onMouseOver="MM_nbGroup('over','gcg','images/gcg_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/gcg_up.gif" alt="GCG" name="gcg" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/Bioinfo@UCR.htm"  onClick="MM_nbGroup('down','group1','bioinfo','images/bioinfo_down.gif',1)" onMouseOver="MM_nbGroup('over','bioinfo','images/bioinfo_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/bioinfo_up.gif" alt="Bioinformatics " name="bioinfo" width="100" height="25" border="0" onload=""></a></td>
<td><a href="http://www.faculty.ucr.edu/~tgirke/Links.htm"  onClick="MM_nbGroup('down','group1','links','images/links_down.gif',1)" onMouseOver="MM_nbGroup('over','links','images/links_over.gif','',1)" onMouseOut="MM_nbGroup('out')"><img src="images/links_up.gif" alt="Links" name="links" width="100" height="25" border="0" onload=""></a></td>
</tr>
</table></td>
</tr>
</table>
-->
 
<!--
<table width='900' border='0' cellspacing='0' cellpadding='0'>
<tr>
<td width='210' rowspan='2'><a href='http://www.cepceb.ucr.edu/' target='_blank'><img src='images/cepceb.jpg' alt='cepceb' width='210' height='90' border='0'></a></td>
<td width='590' align='center' colspan='2'><h1>Genome Cluster Database</h1></td>
</tr>
<tr>
    <td height='29' valign='top' colspan='2'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://www.ucr.edu/' target='_blank'>
        UC Riverside</a></font></div></td>
</tr>
<tr>
    <td colspan='3'><hr></td>
</tr>
<tr>
    
    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/projects/PlantFam/Readme/about.html'>
        <b>About this Database</b></a></font></div></td>
    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/cgi-bin/clusterSummary.pl?sort_col=Size'>
        <b>Cluster Table</b></a></font></div></td>
    <td  valign='top'><div align='center'><font face='Geneva, Arial, Helvetica, sans-serif'><a href='http://bioinfo.ucr.edu/cgi-bin/clusterStats.pl'>
        <b>Cluster Pies</b></a></font></div></td>
</tr>
</table>
-->
<jsp:useBean id='common' class='servlets.Common' scope='application'/>
<% common.printHeader(out); %>

	<FORM NAME='form1' METHOD=POST ACTION='/databaseWeb/QueryPageServlet'   >   
            <TABLE width='70%' align='center' border='0'>
<!--	target='rd'    	<TR>
		    <TD colspan='2' align='center'>
		    	<h2>Full-Genome Cluster Database</h2>
		    </TD>
		    <TD>&nbsp</TD>
		</TR> 
		<TR>
		    <TD align='center'>
		         <A href='http://bioinfo.ucr.edu/projects/PlantFam/Readme/about.html'>About this Database</A>	
		    </TD>
		    <TD align='center'>
			 <A href='http://bioinfo.ucr.edu/cgi-bin/clusterStats.pl'>Cluster Statistics</A>
		    </TD>
                </TR> -->
                <TR>
                    <TD colspan='2'>
                        1.Obtain selected sequences for one or many ID numbers.
                        <BR>
                        2.Text search of putative gene function. Booleans may be used: and, or, not.
                        <BR>
                        3.For perfomance reasons, the maximum number of query results is 1000.
                         To download entire datasets, please use our 
                        <A href='ftp://138.23.191.152/pub/'> FTP site.</A>
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
<!--                    <TD>
                        Number of results to return:&nbsp&nbsp 
                        <INPUT name="limit" value="<%=limit%>" size='5'>
                    </TD> -->
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
<!--                <TR>
                    <TD colspan='2' align='center'>
                        <A href='advancedSearch.jsp'>Advanced Search Page</A>
                   </TD>
                </TR> -->
            </TABLE> 
            <P>            
	</FORM>
</body>
</html>
