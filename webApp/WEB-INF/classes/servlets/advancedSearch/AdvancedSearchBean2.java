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
import servlets.advancedSearch.visitors.HtmlVisitor;

public class AdvancedSearchBean2
{
    private static Logger log=Logger.getLogger(AdvancedSearchBean2.class);
    
    ServletContext servletContext=null;
    HttpServletRequest request=null;
    HttpServletResponse response=null;
    
    SearchState currentState;
    SearchableDatabase db;
    int selectedSearchState;
    boolean printAdminControls=false;
    String defaultDb;
    
    
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
    public void drawSearchPage(JspWriter out)
    {
        Query q=db.buildQueryTree(currentState);         
        HtmlVisitor hv=new HtmlVisitor(new PrintWriter(out),db);
        q.accept(hv); //renders the html
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
        
        try{
               selectedSearchState=Integer.parseInt(request.getParameter("stored_query"));               
        }catch(Exception e){ 
               selectedSearchState=0; 
        }                
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
            removeExpression(Integer.decode(row));
        }else if(action.equals("add_exp")){            
            addExpression();
        }else if(action.equals("search")){
            doQuery(); 
        }else if(action.equals("add_sub_exp")){
            addSubExp(); //add current index to startPars
        }else if(action.equals("end_sub_exp")){
            endSubExp(); //add current index to endPars
        }else if(action.equals("load_query")){
            if(selectedSearchState >=0 && 
               selectedSearchState < db.getSearchManager().getSearchStateList().size())
                currentState=db.getSearchManager().getSearchState(selectedSearchState);
        }else if(action.equals("store_query")){
            String desc=request.getParameter("description");
            if(desc!=null){                
                currentState.setDescription(desc);
                db.getSearchManager().addSearchState(currentState);
            }
        }else if(action.equals("update_query")){
            SearchState ss=db.getSearchManager().getSearchState(selectedSearchState);
            currentState.setDescription(ss.getDescription());
            db.getSearchManager().setSearchState(selectedSearchState, currentState);
        }else if(action.equals("remove_query")){    
            db.getSearchManager().removeSearchState(selectedSearchState);
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
        db.displayResults(currentState, servletContext,request,response);
    }
    private void addExpression()
    {
        
    }
    private void removeExpression(Integer row)
    { //remove entry row from fields, ops, values, and bools
        currentState.getSelectedFields().remove(row.intValue());
        currentState.getSelectedOps().remove(row.intValue());
        currentState.getValues().remove(row.intValue()); 
        currentState.getSelectedBools().remove(row.intValue());
        
//        log.debug("removing "+row);
//        log.debug("start: "+startParinths+", end: "+endParinths);
        for(int i=0;i<currentState.getStartParinths().size();i++)
            if(currentState.getStartParinth(i).intValue() > row.intValue())
                currentState.getStartParinths().set(i, new Integer(currentState.getStartParinth(i).intValue()-1));
        for(int i=0;i<currentState.getEndParinths().size();i++)
            if(currentState.getEndParinth(i).intValue() >= row.intValue())
                currentState.getEndParinths().set(i, new Integer(currentState.getEndParinth(i).intValue()-1));
           
//        log.debug("2start: "+startParinths+", end: "+endParinths);
        
    }
     
    private void addSubExp()
    { //add the length of fields at the end of startPars
        currentState.getStartParinths().add(new Integer(currentState.getSelectedFields().size()));                
    }
    private void endSubExp()
    { //add the length of fields at end of endPars
        currentState.getEndParinths().add(new Integer(currentState.getSelectedFields().size()));                
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
