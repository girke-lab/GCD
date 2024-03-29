/*
 * AdvancedSearchBean2.java
 *
 * Created on January 26, 2005, 10:20 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author khoran
 */

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import javax.servlet.jsp.JspWriter;

import servlets.*;
import servlets.advancedSearch.queryTree.Query;
import servlets.advancedSearch.visitors.*;
import servlets.querySets.*;

/**
 * This class is the interface between a jsp webpage, and the classes
 * that manage queries.  It is responsable for createing a SearchState
 * object that can be used to create an sql string.
 */
public class AdvancedSearchBean2
{
    private static Logger log=Logger.getLogger(AdvancedSearchBean2.class);
    
    ServletContext servletContext=null;
    HttpServletRequest request=null;
    HttpServletResponse response=null;
    
    SearchState currentState;
    SearchableDatabase db;
    String selectedQueryName,message;
    boolean printAdminControls=false;
    boolean printSql=false;
    String defaultDb;
    Query currentQuery=null;
    boolean drawForm=true; //set to false when search started    
    
    
    /** Creates a new instance of AdvancedSearchBean2 */
    public AdvancedSearchBean2()
    {
        defaultDb="common";
    }
    
    /**
     * set the database this bean will use if the user does not specify one.
     * Current valid names are: common, unknowns,unknowns2.
     * @param db name of database to use.
     */
    public void setDefaultDatabase(String db)
    {
        defaultDb=db;
    }
    /**
     * sets wether this page will display query administration 
     * controls
     * @param b true to display controls, false otherwise 
     */
    public void setPrintAmdinControls(boolean b)
    {
        printAdminControls=b;
    }
    /**
     * Will print generated sql on screen if set to true
     * @param b prints sql if true, nothing if false
     */
    public void setPrintSql(boolean b)
    {
        printSql=b;
    }
    /**
    * set the database to use
    * @param name name of a database
    */
    public void setDatabase(String name)
    {
        log.debug("setting database to "+name);
        currentState=new SearchState();
        currentState.setDatabase(name);
        DatabaseQuerySet dqs=QuerySetProvider.getDatabaseQuerySet();
        
        if(name==null)
            setDatabase(defaultDb);
        else if(name.equals("common"))
            db=dqs.getCommonDatabase();            
        else if(name.equals("treatment"))
            db=dqs.getTreatmentDatabase(request.getRemoteUser());
        else //default to common
            setDatabase(defaultDb);
    }
    /**
     * Returns database currently being used
     * @return name of database
     */
    public String getDatabase()
    {
        return currentState.getDatabase();
    }
    /**
     * Should be called once per jsp page. Just stores some
     * servlet info for later use.
     * @param sc 
     * @param rq 
     * @param rs 
     */
    public void initPage(ServletContext sc,HttpServletRequest rq,HttpServletResponse rs)
    {
        servletContext=sc;
        request=rq;
        response=rs;
        buildState(request);
        processCommands(request);
    }
    /**
     * This will print an error message in red.
     * @param out the output stream to print to.
     */
    public void printMessage(JspWriter out)
    {
        try{
            if(message!=null && !message.equals(""))
                out.println("<span align='center'><font color='#FF0000' size=+1>"+
                        message+"</font></span>");
        }catch(IOException e){}
    }
    /**
     * This will draw the entire search form.
     * @param out stream to print to.
     */
    public void drawSearchForm(JspWriter out)
    {
        drawSearchForm(out,new String[]{});
    }
    /**
     * This will draw the search form, plus a database selector.
     * An array of database names can be passed in and the user
     * will be able to switch between them.
     * @param out output stream.
     * @param dbs list of valid database names, see  {@link setDefaultDatabase(java.lang.String db)}.
     */
    public void drawSearchForm(JspWriter out,String[] dbs)
    {
        if(!drawForm)
            return;
        log.debug("rendering form");
        log.debug("using database: "+db.getClass());
        if(currentQuery==null)
        {//then build it from last state
            log.debug("current state="+currentState.getParameterString());
            currentQuery=db.buildQueryTree(currentState);         
        }
        else
            log.debug("using currentQuery");
        if(currentQuery==null)
            log.warn("query tree is null");
        else
            log.debug("got query tree ok");
        log.debug("tree: "+currentQuery);
        HtmlVisitor hv=new HtmlVisitor(new PrintWriter(out),db);
        hv.setDatabases(dbs, currentState.getDatabase());
        try{
            log.debug("drawing form");
            //out.println("<table border='1'><tr><td width='300'>&nbsp</td><td>");
            //out.println("<table border='1'><tr><td>");
            currentQuery.accept(hv); //renders the html
            
            log.debug("done rendering");
                
        
            out.println("<br>");
            out.println(printStoreOptions());
            //out.println("</td></tr></table>");
            if(printSql)
            {
                SqlVisitor sv=new SqlVisitor();
                out.println("<p><h4>Sql</h4><p>");
                out.println("<textarea cols='100' rows='10' >"+sv.getSql(currentQuery)+"</textarea>");
            }
        }catch(IOException e){
            log.warn("could not print : "+e);
        }
        
    }
    
    
    /**
     * read in data from jsp page. This will update the value of
     * currentState to reflect the users input.
     * @param request servlet request object, used to get parameters
     */
    private void buildState(HttpServletRequest request)
    {       
        setDatabase(request.getParameter("database"));
        
        currentState.setSelectedFields(getIntList(request.getParameterValues("fields")));
        currentState.setSelectedOps(getIntList(request.getParameterValues("ops")));
        currentState.setSelectedBools(getIntList(request.getParameterValues("bools")));
        currentState.setStartParinths(getIntList(request.getParameterValues("startPars")));
        currentState.setEndParinths(getIntList(request.getParameterValues("endPars")));
        
        message=request.getParameter("error_message");
        
        log.debug("getting values");
        currentState.setValues(getList(request.getParameterValues("values")));

        String temp=request.getParameter("sortField");
        if(temp==null)
            currentState.setSortField(-1); //will use default value
        else
            currentState.setSortField(Integer.parseInt(temp));
                
        currentState.setLimit(request.getParameter("limit"));    
        
        selectedQueryName=request.getParameter("stored_query");               
        //if(selectedQueryName==null && db.getSearchManager().getSearchStateList().size() > 0)
        //    selectedQueryName=(String)db.getSearchManager().getSearchStateList().iterator().next();
    }
    
    /**
     * Dispatches commands from user.  This will read a parameter called
     * 'action' for an command.  If a command requires further parameters,
     * they will be queried also.  
     * @param request 
     */
    private void processCommands(HttpServletRequest request)
    {        
        String action=request.getParameter("action");
        log.debug("epi="+request.getParameter("epi"));
        if(action==null)
        {
            log.warn("no action given, loading default query");
            action="load_query";
            selectedQueryName="default";
            //return; 
        }
        log.debug("action="+action);
        
        if(action.equals("remove_exp")){
            String row=request.getParameter("row");                
            if(row==null)
                return;            
            removeExpression(Integer.parseInt(row));
        }else if(action.equals("add_exp")){ 
            String row=request.getParameter("row");
            String endParIndx=request.getParameter("epi");
            log.debug("adding expression, row="+row+", end_par_indx="+endParIndx);
            if(row==null || row.equals(""))
                addExpression();
            else if(endParIndx!=null && !endParIndx.equals(""))
                addExpression(Integer.parseInt(row),Integer.parseInt(endParIndx));
        }else if(action.equals("search")){
            doQuery(); 
        }else if(action.equals("add_sub_exp")){
            String row=request.getParameter("row");                
            String endParIndx=request.getParameter("epi");
            if(row==null || row.equals("") || endParIndx==null || endParIndx.equals(""))
                return;                        
            addSubExp(Integer.parseInt(row),Integer.parseInt(endParIndx)); 
//        }else if(action.equals("end_sub_exp")){
//            endSubExp(); //add current index to endPars
        }else if(action.equals("load_query")){ 
            log.debug("loading query: "+selectedQueryName);
            currentQuery=db.getSearchManager().getSearchState(selectedQueryName);
//            if(selectedQueryName >=0 && 
//               selectedQueryName < db.getSearchManager().getSearchStateList().size())
//                currentState=db.getSearchManager().getSearchState(selectedQueryName); 
        }else if(action.equals("store_query")){
            String desc=request.getParameter("description");
            if(desc!=null){          
                currentQuery=db.buildQueryTree(currentState);
                db.getSearchManager().addSearchState(currentQuery,desc);
//                currentState.setDescription(desc);
//                db.getSearchManager().addSearchState(currentState);
            }
        }else if(action.equals("update_query")){
            currentQuery=db.buildQueryTree(currentState);
            db.getSearchManager().setSearchState(selectedQueryName, currentQuery);
            String desc=request.getParameter("description");
            if(desc!=null)
                db.getSearchManager().setDescription(selectedQueryName,desc);
            
//            SearchState ss=db.getSearchManager().getSearchState(selectedQueryName);
//            currentState.setDescription(ss.getDescription());
//            db.getSearchManager().setSearchState(selectedQueryName, currentState);
        }else if(action.equals("remove_query")){    
            db.getSearchManager().removeSearchState(selectedQueryName);
        }else if(action.equals("refresh")){    
        }else if(action.equals("reset")){    
                log.debug("resetting search state");
                String db=currentState.getDatabase();
                currentState=new SearchState();    
                currentState.setDatabase(db);                            
        }                        
        
    }
    
    /**
     * This sends the currentState to the current database, 
     * which will create the sql, perform the query, and send
     * a redirect to the dataview with the resulting id numbers
     */
    private void doQuery()
    {         
        log.debug("submitting query");
        drawForm=false;
        log.debug("currentState="+currentState);
        db.displayResults(currentState, servletContext,request,response);        
    }
    
    /**
     * Adds a new row to end the search page.
     */
    private void addExpression()
    {
        currentState.getSelectedFields().add(new Integer(0));
        currentState.getSelectedOps().add(new Integer(0));
        currentState.getSelectedBools().add(new Integer(0));
    }
    /**
     * Inserts a new row at the location given by row. Requires
     * the index of the last parinthasis of this sub expression so
     * that it can adjust it accordingly.
     * @param row location to insert row.
     * @param endParIndx location of first end parinth after this row.
     */
    private void addExpression(int row,int endParIndx)
    {
        
        log.debug("adding expression at row "+row+", endParIndx="+endParIndx);
        currentState.getSelectedFields().add(row+1,new Integer(0));
        currentState.getSelectedOps().add(row+1,new Integer(0));
        currentState.getValues().add(row+1,"");
        
        
        log.debug("bools: "+currentState.getSelectedBools());
        log.debug("row="+row+", field size: "+currentState.getSelectedFields().size());
        
        Integer newBool=new Integer(0);                
        if(row > 0 && row-1 < currentState.getSelectedBools().size()) 
        {
            log.debug("using bool at index "+(row-1));
            newBool=currentState.getSelectedBool(row-1);        
        }
            
        if(row <= 0)
            currentState.getSelectedBools().add(newBool);
        else
            currentState.getSelectedBools().add(row-1,newBool);
        
        shiftParinths(endParIndx,row,1);
        
        log.debug("new bools: "+currentState.getSelectedBools());
        
    }
    /**
     * Any parinth with a value greater than threshold gets shiftAmt added to it. 
     * Starts looking at startIndx. 
     * @param startIndx start position of search
     * @param threshold 
     * @param shiftAmt number of rows to shift parinth down
     */
    private void shiftParinths(int startIndx,int threshold,int shiftAmt)
    { 
        
        log.debug("shifting parinths after field "+threshold+" down by "+shiftAmt+" starting at index "+startIndx);
        List[] listRefs=new List[]{
                                   currentState.getEndParinths()};
        List l;
        for(int j=0;j<listRefs.length;j++)
        {
            l=listRefs[j];
            log.debug("list before: "+l);
            for(int i=startIndx;i<l.size();i++)
                if(((Integer)l.get(i)).intValue() >= threshold)
                    l.set(i,new Integer(((Integer)l.get(i)).intValue()+shiftAmt));
            log.debug("list after: "+l);
        }        
    }
    /**
     * Remove entry row from fields, ops, values, and bools
     * @param row index of row
     */
    private void removeExpression(int row)
    { 
        
        log.debug("removing expression at row "+row);
        currentState.getSelectedFields().remove(row);
        currentState.getSelectedOps().remove(row);
        currentState.getValues().remove(row); 
        int j=row;
        if(j > 0)
            j--;
        if(j >=0 && j < currentState.getSelectedBools().size())
            currentState.getSelectedBools().remove(j);
        

//        log.debug("removing "+row);
//        log.debug("start: "+startParinths+", end: "+endParinths);
//        for(int i=0;i<currentState.getStartParinths().size();i++)
//            if(currentState.getStartParinth(i).intValue() > row)
//                currentState.getStartParinths().set(i, new Integer(currentState.getStartParinth(i).intValue()-1));
//        for(int i=0;i<currentState.getEndParinths().size();i++)
//            if(currentState.getEndParinth(i).intValue() >= row)
//                currentState.getEndParinths().set(i, new Integer(currentState.getEndParinth(i).intValue()-1));
           
//        log.debug("2start: "+startParinths+", end: "+endParinths);
        
    }
     
    /**
     * Creates a new set of parinths and puts two new expressions inside them.
     * The operator used will be the opposite of whatever comes before it. 
     * (otherwise the parinths will be removed since they would be unessasary).
     * @param row 
     * @param endParIndx 
     */
    private void addSubExp(int row,int endParIndx)
    { //add the length of fields at the end of startPars
        
        log.debug("adding sub expression at row "+row);
        log.debug("state="+currentState);
        //add bool, start par, an expression,a bool,another expression, end par
        Integer oppositeBool=new Integer(0), middleBool=new Integer(1); //should be set based on previous bools
        
        //currentState.getSelectedBools().add(oppositeBool);             
        //currentState.getStartParinths().add(new Integer(row+1));
        
        addExpression(row,endParIndx);
        addExpression(row+1,endParIndx);
        currentState.getStartParinths().add(new Integer(row+1));
        currentState.getEndParinths().add(new Integer(row+2));
        if(currentState.getSelectedBools().size() > 1)
        {
            middleBool=(Integer)currentState.getSelectedBools().get(row-1);
            oppositeBool=new Integer((middleBool.intValue()+1)%2);
        }
        currentState.getSelectedBools().set(row,oppositeBool);
        currentState.getSelectedBools().set(row+1,middleBool);
        log.debug("new state="+currentState);
        
        //currentState.getEndParinths().add(new Integer(row+2));                
    }
    
    /**
     * no longer used.
     */
    private void endSubExp()
    { //add the length of fields at end of endPars
        currentState.getEndParinths().add(new Integer(currentState.getSelectedFields().size()));                
    } 
     
     /**
     * Used to print the query retrieval options. Also prints the admin
     * controls if admin priviledges present.
     * @return html
     */
    private String printStoreOptions()
    {        
        log.debug("printing store options");
        StringBuffer out=new StringBuffer();
        out.append("<table border='0' align='center' bgcolor='"+PageColors.data+"'>\n");
        out.append("<tr><th colspan='3'>Stored Queries</th></tr>\n");
        out.append("<tr><td colspan='3'>");
        out.append(printStoredQueries());
        out.append("</td></tr>\n");
        if(printAdminControls)
        {
            out.append("<tr><th colspan='3'>Admin Controls</th></tr>\n");
            out.append("<tr>\n");
            out.append("<td>"+printSaveOptions()+"</td>\n");
            out.append("<td><INPUT type=submit name='update_query' value='Update' " +
                            " onClick=\"action.value='update_query'; submit()\" ></td>\n");
            out.append("<td><INPUT type=submit name='remove_query' value='Remove' " +
                            " onClick=\"action.value='remove_query'; submit()\" ></td>\n");
            out.append("</tr>");            
        }
           
         out.append("</table>");
        return out.toString();
    }
    /**
     * Used by printStoreOptions() to print list of stored queries.
     * @return html
     */
    private String printStoredQueries()
    {
        StringBuffer out=new StringBuffer();
        SearchTreeManager stm=db.getSearchManager();        
        Collection c=stm.getSearchStateList();        
        String name;
        out.append("<SELECT name='stored_query' onChange=\"action.value='load_query';submit()\" >\n");
        out.append("<OPTION value=''>--- Stored Queries ---</OPTION>");
        for(Iterator i=c.iterator();i.hasNext();)          
        {
            name=(String)i.next();
            out.append("<OPTION value='"+name+"'");
            if(name.equals(selectedQueryName))
                out.append(" selected ");
            out.append(">"+stm.getDescription(name)+"\n");
        }
        out.append("</SELECT>\n");
//        out.append("<INPUT type=submit name='load_query' value='Load Query' " +
//                        " onClick=\"action.value='load_query';submit()\" >\n");
        return out.toString();
    }
    /**
     * prints a button to save queries.
     * @return html
     */
    private String printSaveOptions()
    {
        StringBuffer out=new StringBuffer();
        String name=db.getSearchManager().getDescription(selectedQueryName);
        if(name==null)
            name="";
        //out.append("<h4>Save query: </h4>\n");
        out.append("Description: <INPUT name='description' value='"+name+"'>\n ");
        out.append("<INPUT type=submit name='store_query' value='Store Query' " +
                        " onClick=\"action.value='store_query'; submit()\" >\n");
        return out.toString();
    }
     
    public void printUsage(Writer out) 
    {
        try{
            out.write(
                        "<h4> Usage: </h4>" +
            "                    <p>" +
            "                    This page allows you to create a more complex and detailed query.  " +
            "                    The first field specifies the data field to restrict.  The second" +
            "                    field is a list of operators which  can be applied to the selected" +
            "                    data.  The third column is the value of the data." +
            "                    <p>" +
            "                    The 'IN' operator lets you specifiy a list of strings in the value field, " +
            "                    it will return all records that match one of them exactly." +
            "                    This is faster than 'ILIKE', so you should use 'IN' if an exact match will work." +
            "                    <p>                    " +
            "                    'NOT IN' will match any record wich does not match any of the given strings" +
            "                    exactly." +
            "                    <p>                    " +
            "                    'ILIKE' will match any record which contains any of the given strings.  " +
            "                    You can also specify a list of pattern matches. " +
            "                    If you use the '%', any number of characters can appear there and " +
            "                    it will still match.  If you use the '_', any one character can" +
            "                    apear there.  For example, if you wanted to find any string containing" +
            "                    the word 'kinase' followed by 'protein' later in the string, you could enter: '%kinase%protein%'. " +
            "                    <p>                    " +
            "                    'NOT ILIKE' is like 'ILIKE', except that only records that don't" +
            "                    match any pattern are returned." +
            "                    <P>" +
            "                    You can set the 'sort by' field to sort the whole result set by that " +
            "                    field.  Once you get to the result set, you can only sort records" +
            "                    on the current page." +
            "                    <P>" +
            "                    The 'add sub expression' button is still experimental, but you can" +
            "                    try it if you like.  It tries to add a sub expression inside a set of parenthesis" +
            "                    so you can create more complex expressions.   " 
            );
        }catch(IOException e){
            log.error("io error: "+e);
        }
    }
    
    private List getIntList(String[] strings)
    {
        if(strings==null)
            return null;
        List ints=new ArrayList();
        for(int i=0;i<strings.length;i++)
            ints.add(Integer.decode(strings[i]));
        return ints;
    }
    
    private List getList(String[] strings)
    {
        if(strings==null)
            log.debug("strings is null");
        if(strings==null)
            return null;
        List strs=new ArrayList();
        for(int i=0;i<strings.length;i++)
            strs.add(strings[i]);
        return strs;
    }
    
    private int[] getIntArray(String[] strings)
    {
        if(strings==null)
            return null;
        int[] ints=new int[strings.length];
        for(int i=0;i<strings.length;i++)
            ints[i]=Integer.parseInt(strings[i]);
        return ints;
    }      
}
