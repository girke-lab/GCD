<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id='header' class='servlets.beans.HeaderBean' />
<jsp:useBean id='bean' class='servlets.PfamOptionsBean' scope='request'/>

<html>
<% 
   header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
   header.printStdHeader(out,"Pfam Options", request.getRemoteUser()!=null);
%>    
        
        
        <% bean.processesInput(request); %>                                         
        
        <ul>
            <li>Retrieve all proteins containing this Pfam domain            
                <ul>                
                    <% bean.printDomainSearchLinks(out); %>
                    <!--<li><a href='index.jsp?input=<%=bean.getAccession()%>&fieldName=Cluster Id'><%=bean.getAccession()%></a></li>-->
                </ul>            
            </li>
            <li>Go to Pfam
                <ul>
                    <% bean.printPfamLinks(out);  %>
                </ul>
            </li>
        </ul>
    <% header.printFooter(); %>
</html>
