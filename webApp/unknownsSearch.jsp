<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
    <head><title>Plant Unknowns</title></head>
    <body>

        <jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean2' scope='page'/>
        <jsp:useBean id='common' class='servlets.Common' />


        <%
            bean.setDefaultDatabase("unknowns");
            bean.setDatabase("unknowns");
            bean.initPage(application,request,response);
        %>



        <p><p>  
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
                        <a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/tools.html"><li>Technical Tools</a></li> 
                        <a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/external.html"><li>External Resources</a></li>
                        <a href="http://bioinfo.ucr.edu/projects/internal/Unknowns/external/downloads.html"><li>Downloads</a></li>
                    </font></td>
                    <td>&nbsp;&nbsp;&nbsp;</td>
                    <td valign="top" > 
		
		
                        <%  bean.printMessage(out); %>
                        <p align='center'>
                            <a href='statusQueries.jsp'>Difference Tracking</a> &nbsp&nbsp&nbsp&nbsp 
                            <a href='as2.jsp?database=<%=bean.getDatabase()%>'>Admin Page</a>                        
                        </p>
                        <%  bean.drawSearchForm(out,new String[]{"unknowns","unknowns2"}); %>
		
                        

                        <h4> Usage: </h4>
                        <p>
                        Most operators work as expected.  The LIKE and NOT LIKE operators can be used
                        to match patterns.  The symbol '%' will match any number of characters,
                        while the '_' will match any one character. 
                        <p>                    
                        The limit field determines the total number of results returned.                    
                    
                    </td>                
                </tr>
            </table>

        </font>
    </body>
</html>
