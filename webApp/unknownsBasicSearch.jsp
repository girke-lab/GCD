<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <jsp:useBean id='common' class='servlets.Common' scope='application'/>
        <jsp:useBean id='bean'   class='servlets.SimpleSearchBean' scope='page'/>

        <% bean.initPage("unknownsBasicSearch.jsp",application,request,response); %>           

        <font face="sans-serif, Arial, Helvetica, Geneva">
            <img alt="Unknown Database" src="images/unknownspace3.png">		
            <table border ='0'>
                <tr>
                    <td valign="top" bgcolor="#F0F8FF" width=180><font SIZE=-1>
                        <a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/index.html"><li>Project</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/descriptors.html"><li>Unknown Descriptors</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/retrieval.html"><li>Search Options</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/interaction.html"><li>Protein Interaction</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/KO_cDNA.html"><li>KO & cDNA Results</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/profiling.html"><li>Chip Profiling</a></li> 
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/tools.html"><li>Protocols</a></li> 
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/external.html"><li>Literature</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/links.html"><li>Links</a></li>
			<a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/downloads.html"><li>Downloads</a></li>                                            
                    </font></td>
                    <td>&nbsp;&nbsp;&nbsp;</td>
                    <td valign="top" > 
		
                        <%  common.printUnknownsSearchLinks(out); %>
                        <P>
                        <%  bean.printMessage(out); %>
                        
                        
                        <%  bean.drawForm(out,new String[]{"unknowns2View"},new String[]{"POND"}); %>		                                                                 
                    
                    </td>                
                </tr>
            </table>

        </font>
    
   
    
    </body>
</html>
