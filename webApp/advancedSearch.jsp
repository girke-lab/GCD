<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head><title>Advanced Search</title></head>
<body>

<jsp:useBean id='bean' class='servlets.AdvancedSearchBean' scope='session'/>
<jsp:useBean id='common' class='servlets.Common' />
<%      
        common.printHeader(out);
        bean.setContext(application,request,response);
        bean.loadValues(request); //get entered values for fields, opts, bools , etc ...
%>

<p><p> 
<form method='post' action='/databaseWeb/advancedSearch.jsp' >
    <table border='0' align='center' bgcolor='D3D3D3'>
    <!-- first print the forms entered so far, then print a new form  window.location.reload()-->
    <input type=hidden name='row'>
    <input type=hidden name='action'>
    <% int sp=0,ep=0;%>
    <% int count=bean.getLoopCount(); %>
    <%for(int j=0;j<count;j++){%>

        <tr>
            <td>
                
                <% sp=bean.printParinth(out,j,sp,"start",sp-ep);%>
            </td>
        </tr>
        <tr>
            <td>
                <%= bean.printSpace(sp-ep) %>
                <select name='fields' onChange="action.value='refresh'; submit()">
                <%for(int i=0;i<bean.fields.length;i++){%>
                    <option value='<%=i%>'
                            <%=bean.selected(bean.selectedFields,j,i)%>>
                        <%=bean.fields[i].displayName%>
                    </option>
                <%}%>
                </select>
            </td>
            <td>
                <select name='ops'>
                <% for(int i=0;i<bean.operators.length;i++){%>
                    <option value='<%=i%>'
                            <%=bean.selected(bean.selectedOps,j,i)%>>
                        <%=bean.operators[i]%>
                    </option>
                <%}%>        
                </select>            
            </td>
            <td>
                <% if(j<bean.selectedFields.size()){
                    out.println(bean.fields[((Integer)bean.selectedFields.get(j)).intValue()].render(bean.getValue(j)));
                }else{
                    out.println(bean.fields[0].render(bean.getValue(j)));
                }%>
<!--                <input type=text name='values' value='<%=bean.getValue(j)%>'> -->
            </td>
            <td>
                <input type=submit name='remove' value='remove' 
                    onClick='row.value=<%=j%>;submit()'> 
            </td>
        </tr>            
        <tr>
            <td>
                
                <% ep=bean.printParinth(out,j,ep,"end",sp-ep);%>
            </td>
        </tr>
        <tr bgcolor='AAAAAA'>
            <td colspan='4'>
                <%= bean.printSpace(sp-ep) %>
                <select name='bools'>
                <%for(int i=0;i<bean.booleans.length;i++){%>
                    <option value='<%=i%>'
                            <%=bean.selected(bean.selectedBools,j,i)%>>
                        <%=bean.booleans[i]%>
                    </option>
                <%}%>
                </select>
            </td>
        </tr>
    <%}%>
        <tr>
            <td>
                <input type=submit name='add_exp' value='add expression'>
            </td>
            <td>
                <input type=submit name='add_sub_exp' value='add sub expression'>
            </td>
            <% if(bean.printEndSubButton(sp,ep)){ %>
            <td>
                <input type=submit name='end_sub_exp' value='end sub expression'>
            </td>
            <%}%>
        </tr>
        <tr>
            <td colspan='2' align='center'>
                Sort by: 
                <select name='sortField' >
                <%for(int i=0;i<bean.fields.length;i++){%>
                    <option value='<%=i%>'
                        <%if(bean.sortField==i){
                            out.print(" selected "); } %> >
                        <%=bean.fields[i].displayName%>
                    </option>
                <%}%>
            </td>
            <td colspan='2'>
                Limit: <input name='limit' value='<%=bean.limit%>'>
            </td>
        </tr>
        <tr>            
            <td colspan='4' align='center'>
                <input type=submit name='search' value='Search'>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
