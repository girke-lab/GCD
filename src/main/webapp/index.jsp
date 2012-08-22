<%@page contentType="text/html"%>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />        
<jsp:useBean id='bean'   class='servlets.SimpleSearchBean' scope='page'/>

<html>

<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
   header.printStdHeader(out,"Single or Batch Search",
           request.getRemoteUser()!=null);
%>


    <% bean.initPage("index.jsp",application,request,response); %>   
            
            
            <TABLE width='70%' align='center' border='0'>
                <TR>
                    <TD  align='center'>                                              
                        
                        GCD is a database for genome-wide sequence <BR> family mining in Arabidopsis and Rice.
                        Detailed information about this resource <BR> is available on the <a href='about.jsp'>ReadMe</a> page
                        and the associated publication in <a href='http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=pubmed&dopt=Abstract&list_uids=15888677&query_hl=2'>
                        Plant Physiology: 138, 47-54</a>.
                        <P>
                       <center>
                            <font color='#FF0000'>News: incorporation of expression data from 1309 Affymetrix chips.</font>
                            <br><br>                            
                            <A href='about.jsp#search'>
                                How to Search GCD</A>                        
                        </center>
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
                        <center>
                        <A href='/unknowns/index.html'>
                        This Project is funded by the NSF 2010 grant # IOB-0420152
                        </center>
                        </A>
                   </TD>
                </TR>
            </TABLE> 
            <P>     
<% header.printFooter(); %> 	
</html>
