/*
 * AdvancedSearchServlet.java
 *
 * Created on August 5, 2004, 2:56 PM
 */

package servlets.advancedSearch;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.log4j.Logger;
import javax.servlet.jsp.JspWriter;

import servlets.*;

/**
 *
 * @author  khoran 
 * @version
 */
public class AdvancedSearchBean {  
    
    boolean noNewRow=false;
   
    ServletContext servletContext=null;
    ServletRequest request=null;
    ServletResponse response=null;
    
    public SearchState currentState=null;
    public SearchableDatabase db=null;
    
    private int selectedSearchState;
    private boolean printAdminControls=false;
    private String defaultDb="common";
    private static Logger log=Logger.getLogger(AdvancedSearchBean.class);
    
    public AdvancedSearchBean()
    {                
        //log.debug("createing new bean");        
    }
    public void setDefaultDatabase(String db)
    {
        defaultDb=db;
    }
    public void setDatabase(String name)
    {        //this is not storing state properly!!
        currentState=new SearchState();
        currentState.setDatabase(name);
//        if(db!=null)
//            return;
        
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
   
    public void loadValues(HttpServletRequest request)
    {
        
        setDatabase(request.getParameter("database"));
        
        currentState.setSelectedFields(getIntList(request.getParameterValues("fields")));
        currentState.setSelectedOps(getIntList(request.getParameterValues("ops")));
        currentState.setSelectedBools(getIntList(request.getParameterValues("bools")));
        currentState.setStartParinths(getIntList(request.getParameterValues("startPars")));
        currentState.setEndParinths(getIntList(request.getParameterValues("endPars")));

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
        
        processCommands(request);
    }
    public String selected(List values,int i, int j)
    {
        return values != null && i >= 0 && i<values.size() && ((Integer)values.get(i)).intValue()==j ? 
            "selected" : "";
    }
    public String getValue(int i)
    {        
        return currentState.getValue(i);
    }
    public Object get(List l,int i)
    {
        if(l!=null && i>=0 && i<l.size())
            return l.get(i);
        return null;
    }
    public int printParinth(JspWriter out,int j, int sp,String position,int space) 
    {
        boolean start=position.equals("start");
        List parinths= start? currentState.getStartParinths() : currentState.getEndParinths(); 
        if(sp < parinths.size() && ((Integer)parinths.get(sp)).intValue()==j)            
        {
            try{
                if(start)
                    out.println(printSpace(space)+"(");                
                else
                    out.println(printSpace(space-1)+")");
                        
                String name= start? "startPars" : "endPars";
                out.println("<input type=hidden name='"+name+"' value='"+j+"'>");
                return sp+1;
            }catch(IOException e){}
        }
        return sp;
    }
    public boolean printEndSubButton(int sp,int ep)
    {
        return currentState.getStartParinths().size() > currentState.getEndParinths().size();
    }
    public String printSpace(int level)
    {//used for indenting sub expressions
        String space="";
        for(int i=0;i<level;i++)
            space+="&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        return space;        
    }
    public int getLoopCount()
    {        
        if(noNewRow){
            noNewRow=false;
            return currentState.getSelectedFields().size();
        }
        return currentState.getSelectedFields().size()+1;
    }
    public void setContext(ServletContext sc,HttpServletRequest rq,HttpServletResponse rs)
    {
        servletContext=sc;
        request=rq;
        response=rs;
    }
    public String printStoreOptions()
    {        
        StringBuffer out=new StringBuffer();
        out.append("<table align='center'>\n");
        out.append("<tr><th colspan='3'>Stored Queries</th></tr>\n");
        out.append("<tr><td colspan='3'>");
        out.append(printStoredQueries());
        out.append("</td></tr>\n");
        if(printAdminControls)
        {
            out.append("<tr><th colspan='3'>Admin Controls</th></tr>\n");
            out.append("<tr>\n");
            out.append("<td>"+printSaveOptions()+"</td>\n");
            out.append("<td><INPUT type=submit name='update_query' value='Update'></td>\n");
            out.append("<td><INPUT type=submit name='remove_query' value='Remove'></td>\n");
            out.append("</tr>");            
        }
           
         out.append("</table>");
        return out.toString();
    }
    public String printStoredQueries()
    {
        StringBuffer out=new StringBuffer();
        Collection c=db.getSearchManager().getSearchStateList();
        int count=0;       
        out.append("<SELECT name='stored_query'>\n");
        for(Iterator i=c.iterator();i.hasNext();count++)          
        {
            out.append("<OPTION value='"+count+"'");
            if(count==selectedSearchState)
                out.append(" selected ");
            out.append(">"+i.next()+"\n");
        }
        out.append("</SELECT>\n");
        out.append("<INPUT type=submit name='load_query' value='Load Query'>\n");
        return out.toString();
    }
    public String printSaveOptions()
    {
        StringBuffer out=new StringBuffer();
        //out.append("<h4>Save query: </h4>\n");
        out.append("Description: <INPUT name='description'>\n ");
        out.append("<INPUT type=submit name='store_query' value='Store Query'");
        return out.toString();
    }
    public void adminEnabled(boolean b)
    {
        printAdminControls=b;
    }
    public String selectedDb(String db)
    {
        return "value='"+db+"' "+(db.equals(currentState.getDatabase())? "selected" : "") ;        
    }
    public String printStatusQueries()
    {        
        StringBuffer out=new StringBuffer();
        DbConnection dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
        {
            log.error("could not connect to khoran for status queries");
            return "";
        }
        String query="SELECT queries_id,name FROM updates.queries";
        List results=null,row;
        try{
            results=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("query "+query+" failed: "+e.getMessage());
        }
        
        out.append("<table>\n");        
        out.append("<tr><th>Status Queries</th></tr>\n");
        for(Iterator i=results.iterator();i.hasNext();)
        {
            row=(List)i.next();
            out.append("<tr><td><a href='QueryDispaterServlet?queries_id="+
                row.get(0)+"'>"+row.get(1)+"</a></td></tr>\n");
        }
        out.append("</table>\n");
        return out.toString();
    }
//////////////////// PRIVATE METHODS  ////////////////////////////////////    
    private void processCommands(HttpServletRequest request)
    {        
        String action;
        if(request.getParameter("remove") != null){
            String row=request.getParameter("row");                
            if(row==null)
                return;            
            removeExpression(Integer.decode(row));
        }else if(request.getParameter("search") != null){
            doQuery(); 
        }else if(request.getParameter("add_sub_exp") != null){
            //add parith at this location
            addSubExp(); //add current index to startPars
        }else if(request.getParameter("end_sub_exp") != null){
            endSubExp(); //add current index to endPars
        }else if(request.getParameter("add_exp") != null){
            //nothing to do
        }else if(request.getParameter("load_query")!=null){            
            if(selectedSearchState >=0 && 
               selectedSearchState < db.getSearchManager().getSearchStateList().size())
                currentState=db.getSearchManager().getSearchState(selectedSearchState);
            noNewRow=true;
        }else if(request.getParameter("store_query")!=null){
            String desc=request.getParameter("description");
            if(desc!=null){                
                currentState.setDescription(desc);
                db.getSearchManager().addSearchState(currentState);
            }
            noNewRow=true;
        }else if(request.getParameter("update_query")!=null){
            SearchState ss=db.getSearchManager().getSearchState(selectedSearchState);
            currentState.setDescription(ss.getDescription());
            db.getSearchManager().setSearchState(selectedSearchState, currentState);
            noNewRow=true;
        }else if(request.getParameter("remove_query")!=null){
            db.getSearchManager().removeSearchState(selectedSearchState);
            noNewRow=true;                    
        }else if((action=request.getParameter("action")) !=null){ //this should always be the last case
            log.debug("action="+action);
            if(action.equals("refresh"))
                noNewRow=true;    
            else if(action.equals("reset")){
                log.debug("resetting search state");
                String db=currentState.getDatabase();
                currentState=new SearchState();    
                currentState.setDatabase(db);                
            }
            
        }
        
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
        
        noNewRow=true;
    }
    private void doQuery()
    { 
        if(servletContext==null)
        {
            log.error("could not get servlet context");
            return;
        }
        db.displayResults(currentState, servletContext,(HttpServletRequest)request,(HttpServletResponse)response);
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
