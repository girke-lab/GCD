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

import servlets.Common;

/**
 *
 * @author  khoran
 * @version
 */
public class AdvancedSearchBean {  
    
    boolean lastWasRemove=false;
    ServletContext servletContext=null;
    ServletRequest request=null;
    ServletResponse response=null;
    
    public SearchState currentState;
    public SearchableDatabase db;
    
    private static Logger log=Logger.getLogger(AdvancedSearchBean.class);
    
    public AdvancedSearchBean()
    {        
        currentState=new SearchState();
        db=new CommonDatabase();        
    }
    public void setDatabase(String name)
    {
        currentState=new SearchState();
        if(name.equals("common"))
            db=new CommonDatabase();
        else if(name.equals("unknowns"))
            db=new UnknownsDatabase();
        else //default to common
            db=new CommonDatabase();
    }
    public void loadValues(HttpServletRequest request)
    {
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
        if(lastWasRemove){
            lastWasRemove=false;
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
    
//////////////////// PRIVATE METHODS  ////////////////////////////////////    
    private void processCommands(HttpServletRequest request)
    {
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
        }else if(request.getParameter("action") !=null)
            lastWasRemove=true;
        
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
        
        lastWasRemove=true;
    }
    private void doQuery()
    { //put all the conditions together to build a query
        
        if(servletContext==null)
        {
            log.error("could not get servlet context");
            return;
        }
        db.displayResults(currentState, servletContext,(HttpServletRequest)request,(HttpServletResponse)response);
        
        if(true)
            return;
        
        
        
        String query=db.buildQuery(currentState);  
                        
        
        log.info("query is: "+query);
        List results=db.sendQuery(query);
        //List results=Common.sendQuery(query);
        if(results==null)
            results=new ArrayList(); //let someone else report that their are no results.
     
        sendToServlet(results,db.getDestination());
    }
    private void sendToServlet(List results,String destination)
    {        
        //then figure out how to pass this info to QueryPageServlet via post.
        //set the parameters needed by QueryPageServlet
        
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", currentState.getLimit());
        mRequest.getParameterMap().put("sortCol",db.getFields()[currentState.getSortField()].dbName);         
                
        if(db.getFields()[currentState.getSortField()].dbName.startsWith("cluster_info"))
            mRequest.getParameterMap().put("displayType","clusterView");
        else
            mRequest.getParameterMap().put("displayType","seqView");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        try{
            servletContext.getRequestDispatcher("/"+destination).forward(mRequest, response);    
        }catch(Exception e){
            log.error("could not forward to QueryPageServlet: "+e.getMessage());
            e.printStackTrace();
        }
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
