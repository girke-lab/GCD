<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head><title>Advanced Search</title></head>
<body>

<jsp:useBean id='bean' class='servlets.advancedSearch.AdvancedSearchBean' scope='page'/>
<jsp:useBean id='common' class='servlets.Common' />
<%      
        common.printHeader(out);
        bean.setDatabase("common");
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
                <%for(int i=0;i<bean.db.getFields().length;i++){%>
                    <option value='<%=i%>'
                            <%=bean.selected(bean.currentState.getSelectedFields(),j,i)%>>
                        <%=bean.db.getFields()[i].displayName%>
                    </option>
                <%}%>
                </select>
            </td>
            <td>
                <select name='ops'>
                <% for(int i=0;i<bean.db.getOperators().length;i++){%>
                    <option value='<%=i%>'
                            <%=bean.selected(bean.currentState.getSelectedOps(),j,i)%>>
                        <%=bean.db.getOperators()[i]%>
                    </option>
                <%}%>        
                </select>            
            </td>
            <td>
                <% if(j<bean.currentState.getSelectedFields().size()){
                    out.println(bean.db.getFields()[bean.currentState.getSelectedField(j).intValue()].render(bean.getValue(j)));
                }else{
                    out.println(bean.db.getFields()[0].render(bean.getValue(j)));
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
                <%for(int i=0;i<bean.db.getBooleans().length;i++){%>
                    <option value='<%=i%>'
                            <%=bean.selected(bean.currentState.getSelectedBools(),j,i)%>>
                        <%=bean.db.getBooleans()[i]%>
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
                <%for(int i=0;i<bean.db.getFields().length;i++){%>
                    <option value='<%=i%>'
                        <%if(bean.currentState.getSortField()==i){
                            out.print(" selected "); } %> >
                        <%=bean.db.getFields()[i].displayName%>
                    </option>
                <%}%>
            </td>
            <td colspan='2'>
                Limit: <input name='limit' value='<%=bean.currentState.getLimit()%>'>
            </td>
        </tr>
        <tr>            
            <td colspan='4' align='center'>
                <input type=submit name='search' value='Search'>
            </td>
        </tr>
        <tr>
            <td colspan='4'>
                <%= bean.printStoreOptions() %>
            </td>
        </tr>
    </table>
</form>

<h4> Usage: </h4>
<p>
Most operators work as expected.  The LIKE and NOT LIKE operators can be used
to match patterns.  The symbol '%' will match any number of characters,
while the '_' will match any one character. These operators should only 
be used for text fields, spceifically, they will not work on the cluster
size or arab/rice count fields.
<p>
The sort column will also determine the result view. Sorting by sequence
attributes will result in a sequence oriented view, while sorting
by cluster attributes will result in a cluter oriented view. Sorting
by go number or database will give a sequence oriented view.
<p>
The limit field determines the total number of results returned, which 
will be displayed 50 at a time. 

</body>
</html>
