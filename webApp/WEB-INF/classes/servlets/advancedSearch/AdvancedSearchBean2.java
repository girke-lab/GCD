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

public class AdvancedSearchBean2
{
    private static Logger log=Logger.getLogger(AdvancedSearchBean2.class);
    
    ServletContext servletContext=null;
    HttpServletRequest request=null;
    HttpServletResponse response=null;
    
    SearchState currentState;
    SearchableDatabase db;
    String selectedQueryName;
    boolean printAdminControls=false;
    String defaultDb;
    Query currentQuery=null;
    
    
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
    public void setPrintAmdinControls(boolean b)
    {
        printAdminControls=b;
    }
    
    /**
    * set the database to use
    * @param name name of a database
    */
    public void setDatabase(String name)
    {
        currentState=new SearchState();
        currentState.setDatabase(name);
        
        if(name==null)
            setDatabase(defaultDb);
        else if(name.equals("common"))
            db=new CommonDatabase();
        else if(name.equals("unknowns"))
            db=new UnknownsDatabase();
        else if(name.equals("unknowns2"))
            db=new Unknowns2Database();
        else //default to common
            setDatabase(defaultDb);
    }
       
    public void initPage(ServletContext sc,HttpServletRequest rq,HttpServletResponse rs)
    {
        servletContext=sc;
        request=rq;
        response=rs;
        buildState(request);
        processCommands(request);
    }
    public void drawSearchForm(JspWriter out)
    {
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
        log.debug("drawing form");
        currentQuery.accept(hv); //renders the html
        log.debug("done rendering");
        
        try{
            out.println("<p>");
            out.println(printStoreOptions());
            SqlVisitor sv=new SqlVisitor();
            out.println("<p><h4>Sql</h4><p>");
            out.println("<pre>"+sv.getSql(currentQuery)+"</pre>");
        }catch(IOException e){
            log.warn("could not print : "+e);
        }
        
    }
    
    /**
     * read in data from jsp page
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
        
        log.debug("getting values");
        currentState.setValues(getList(request.getParameterValues("values")));

        String temp=request.getParameter("sortField");
        if(temp==null)
            currentState.setSortField(-1); //will use default value
        else
            currentState.setSortField(Integer.parseInt(temp));
                
        currentState.setLimit(request.getParameter("limit"));    
        
        selectedQueryName=request.getParameter("stored_query");               
        if(selectedQueryName==null && db.getSearchManager().getSearchStateList().size() > 0)
            selectedQueryName=(String)db.getSearchManager().getSearchStateList().iterator().next();
    }
    
    private void processCommands(HttpServletRequest request)
    {        
        String action=request.getParameter("action");
        if(action==null)
        {
            log.warn("no action given");
            return; 
        }
        log.debug("action="+action);
        
        if(action.equals("remove_exp")){
            String row=request.getParameter("row");                
            if(row==null)
                return;            
            removeExpression(Integer.valueOf(row));
        }else if(action.equals("add_exp")){ 
            String row=request.getParameter("row");
            if(row==null || row.equals(""))
                addExpression();
            else 
                addExpression(Integer.valueOf(row));
        }else if(action.equals("search")){
            doQuery(); 
        }else if(action.equals("add_sub_exp")){
            addSubExp(); //add current index to startPars
        }else if(action.equals("end_sub_exp")){
            endSubExp(); //add current index to endPars
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
    
    private void doQuery()
    {         
        log.debug("submitting query");
        db.displayResults(currentState, servletContext,request,response);
    }
    private void addExpression()
    {
        currentState.getSelectedFields().add(new Integer(0));
        currentState.getSelectedOps().add(new Integer(0));
        currentState.getSelectedBools().add(new Integer(0));
    }
    private void addExpression(Integer row)
    {
        log.debug("adding expression at row "+row);
        currentState.getSelectedFields().add(row.intValue()+1,new Integer(0));
        currentState.getSelectedOps().add(row.intValue()+1,new Integer(0));
        currentState.getValues().add(row.intValue()+1,"");
        
        
        log.debug("bools: "+currentState.getSelectedBools());
        log.debug("row="+row+", field size: "+currentState.getSelectedFields().size());
        
        Integer newBool=new Integer(0);        
        if(row.intValue()-1 < currentState.getSelectedBools().size()) 
        {
            log.debug("using bool at index "+(row.intValue()-1));
            newBool=currentState.getSelectedBool(row.intValue()-1);        
        }
            
            
        currentState.getSelectedBools().add(row.intValue()-1,newBool);
        
        log.debug("new bools: "+currentState.getSelectedBools());
        
    }
    private void removeExpression(Integer row)
    { //remove entry row from fields, ops, values, and bools
        log.debug("removing expression at row "+row);
        currentState.getSelectedFields().remove(row.intValue());
        currentState.getSelectedOps().remove(row.intValue());
        currentState.getValues().remove(row.intValue()); 
        int j=row.intValue();
        if(j > 0)
            j--;
        if(j >=0 && j < currentState.getSelectedBools().size())
            currentState.getSelectedBools().remove(j);
        

//        log.debug("removing "+row);
//        log.debug("start: "+startParinths+", end: "+endParinths);
//        for(int i=0;i<currentState.getStartParinths().size();i++)
//            if(currentState.getStartParinth(i).intValue() > row.intValue())
//                currentState.getStartParinths().set(i, new Integer(currentState.getStartParinth(i).intValue()-1));
//        for(int i=0;i<currentState.getEndParinths().size();i++)
//            if(currentState.getEndParinth(i).intValue() >= row.intValue())
//                currentState.getEndParinths().set(i, new Integer(currentState.getEndParinth(i).intValue()-1));
           
//        log.debug("2start: "+startParinths+", end: "+endParinths);
        
    }
     
    private void addSubExp()
    { //add the length of fields at the end of startPars
        log.debug("adding sub expression");
        currentState.getStartParinths().add(new Integer(currentState.getSelectedFields().size()));              
        addExpression();
        currentState.getEndParinths().add(new Integer(currentState.getSelectedFields().size()));                
    }
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
        out.append("<table border='0' align='center' bgcolor='"+Common.dataColor+"'>\n");
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
        out.append("<SELECT name='stored_query'>\n");
        for(Iterator i=c.iterator();i.hasNext();)          
        {
            name=(String)i.next();
            out.append("<OPTION value='"+name+"'");
            if(name.equals(selectedQueryName))
                out.append(" selected ");
            out.append(">"+stm.getDescription(name)+"\n");
        }
        out.append("</SELECT>\n");
        out.append("<INPUT type=submit name='load_query' value='Load Query' " +
                        " onClick=\"action.value='load_query';submit()\" >\n");
        return out.toString();
    }
    /**
     * prints a button to save queries.
     * @return html
     */
    private String printSaveOptions()
    {
        StringBuffer out=new StringBuffer();
        //out.append("<h4>Save query: </h4>\n");
        out.append("Description: <INPUT name='description' value='"+db.getSearchManager().getDescription(selectedQueryName)+"'>\n ");
        out.append("<INPUT type=submit name='store_query' value='Store Query' " +
                        " onClick=\"action.value='store_query'; submit()\" >\n");
        return out.toString();
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
