/*
 * ResultSummaryServlet.java
 *
 * Created on February 19, 2003, 2:22 PM
 */
package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import org.apache.log4j.*;
import servlets.exceptions.UnsupportedKeyType;

import servlets.search.*;
import servlets.dataViews.*;
import servlets.dataViews.Unknowns2DataView;
import servlets.querySets.*;
/**
 *
 * @author  Kevin Horan
 * @version 1.0
 */

public class QueryPageServlet extends HttpServlet 
{    

    private static Logger log; 
    //private static Properties searches=null,dataViews=null;
    private static Properties settings=null;
    
    public void init(ServletConfig config) throws ServletException
    {       
        super.init(config);       
        
        //setup logger
        Properties props=new Properties();
        try{        
            props.load(this.getClass().getResourceAsStream("log4j.properties"));
            PropertyConfigurator.configure(props);
        }catch(IOException e){
            System.err.println("could not configure logger");
        }
        log=Logger.getLogger(QueryPageServlet.class);
        
        try{
            settings=new Properties();
            settings.load(this.getClass().getResourceAsStream("queryPage.properties"));                        
        }catch(IOException e){
            log.error("could not load properites: "+e.getMessage());
        }
        
        initQuerySets();
    }    
    public void destroy()
    {     
        log.info("servlet shutting down");
        Collection names=DbConnectionManager.getConnectionNames();
        for(Iterator i=names.iterator();i.hasNext();) 
        {
            String name=(String)i.next();
            log.info("closing connection "+name);
            DbConnection dbc=(DbConnection)DbConnectionManager.getConnection(name);
            if(dbc!=null)
                dbc.close();
        }
        log.info("done disconnecting databases");
    }    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response,boolean wasPost)
        throws ServletException, java.io.IOException
    {
        HttpSession session = request.getSession(true);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        if(wasPost)
            log.info("POST request from "+request.getRemoteAddr());
        else
            log.info("GET request from "+request.getRemoteAddr());
        
        int hid,pos,rpp;
        if(session.getAttribute("hid")==null)
        {//session was just created.
            log.info("createing new session");
            session.setAttribute("hid",new Integer(0));
            session.setAttribute("history",new ArrayList());            
            
        }
        else
        { //make sure we don't get too many sessions
            List history=(List)session.getAttribute("history");
            log.info("history="+history);
            if(history != null && history.size() > Common.MAX_SESSIONS) 
            {
                log.info("pruning old sessions");
                for(int i=0;i< history.size()-Common.MAX_SESSIONS;i++)
                    //set old elments to null se we free up some memory.
                    history.set(i, null);
            }
        }
        
        ////////////////////////// HTML headers  ////////////////////////////////////////////
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Query Result Page</title>");
        out.println("</head>");      
        /////////////////////////// main   ////////////////////////////////////////////////////
        
        Search s=null;
        DataView dv=null;
        QueryInfo qi=null;        
         
        
        //parameters that need to be evaluated for each page should be grabbed
        //here, all others should be grabbed in the getInput() method and stored in
        //a QueryInfo object.
        String origin=request.getParameter("origin_page"); //should be the name of a jsp to send errors back to
        if(origin==null || origin.equals(""))
            origin="index.jsp";
        try{
            hid=Integer.parseInt(request.getParameter("hid"));           
            if(hid < 0 || hid >= ((ArrayList)session.getAttribute("history")).size() ||
                    ((List)session.getAttribute("history")).get(hid)==null)
            {
                Common.sendError(response,origin,"hid "+hid+" out of bounds");
                return;
            }
            qi=(QueryInfo)((ArrayList)session.getAttribute("history")).get(hid);                    
        }catch(Exception e){ //no hid, so start a new search       
            qi=getStaticSettings(request);
            if(qi==null)
            {
                //Common.quit(out,"no results found");
                log.debug("qi was null");
                Common.sendError(response,origin,"No results found");
                return;
            }            
            hid=((Integer)session.getAttribute("hid")).intValue();
            session.setAttribute("hid",new Integer(hid+1));
            ((ArrayList)session.getAttribute("history")).add(qi);            
            log.info("new session for "+request.getRemoteAddr());
        }
        getDynamicSettings(qi,request); //update qi with any given info
        
        rpp=((Integer)qi.getObject("rpp")).intValue();
        pos=qi.getCurrentPos();
        
        //get search and dataview objects
        s=qi.getSearch(); 
        dv=getDataView(qi.getDisplayType(),qi.getSortCol(),request);
        //find a common search key to use
        int commonKeyType=findCommonKeyType(s.getSupportedKeyTypes(), dv.getSupportedKeyTypes());
        
        try{//try setting the key, if a key type is unsupported, an exception is thrown.
            log.debug("setting search key type to "+commonKeyType);
            s.setKeyType(commonKeyType);
            log.debug("setting dataview key type to "+commonKeyType);
            dv.setKeyType(commonKeyType);
        }catch(UnsupportedKeyType e){
            log.error("bad key type: "+e);
        }
        
        //the first call of getResults performs the search
        if(s.getResults()==null || s.getResults().size()==0){
            Common.sendError(response,origin,"No matches found");
            return;
        }
        if(pos < 0 || pos > s.getResults().size() ){
            Common.sendError(response,origin,"Position "+pos+" is out of bounds");
            return;
        }
                
        //finish setting up the dataview
        dv.setData(qi.getSortCol(),qi.getDbs(),hid);
        dv.setSortDirection((String)qi.getObject("sortDirection"));
        
        if(wasPost)
            response.sendRedirect("QueryPageServlet?hid="+hid);
        else
        {
            Map generalStor=(Map)qi.getObject("general_storage");
            ResultPage page=new ResultPage(dv, s, pos, hid, rpp,generalStor); 
            page.dipslayPage(out);
        }
                        
        out.println("</body>");
        out.println("</html>");

        out.close();
        /////////////////////////////////  end of main  ////////////////////////////////////////////
    }
    private DataView getDataView(String displayType,String sortCol,HttpServletRequest request)
    {                
        if(displayType!=null)
        {
            //special cases (need arguments to constructor)
            if(displayType.equals("unknownsView"))
                return new UnknownsDataView(this.getServletContext().getRealPath("/temp"));
            else if(displayType.equals("modelView"))
                return new ModelDataView(request);
            else if(displayType.equals("affyView"))
                return new AffyDataView(request);
            

            //look up the given displayType to get a class name,
            // then instantiate that class with a parameterless constructor
            String dataViewClass=settings.getProperty("data_view."+displayType);
            if(dataViewClass==null)
                return new SeqDataView();
            DataView dv=null;
            try{
                Class c=Class.forName(dataViewClass);
//                java.lang.reflect.Constructor con=c.getConstructor(new Class[]{HttpServletRequest.class});
//                dv=(DataView)con.newInstance(new Object[]{request});
                dv=(DataView)Class.forName(dataViewClass).newInstance();
            }catch(Exception e){
                log.error("could not create class "+dataViewClass);
                return new SeqDataView();
            }
            return dv;
        }
        else if(sortCol!=null)
        {
            if(sortCol.startsWith("sequences"))
                return new SeqDataView();
            else if(sortCol.startsWith("cluster_info"))
                return new ClusterDataView();
        }
        return new SeqDataView();
    }
    private Search getSearchObj(String type) 
    {           
        if(type==null)
            return new IdSearch();
        else
        {
            type=type.replaceAll(" ","_");            
            String searchClass=settings.getProperty("search."+type);
            if(searchClass==null)
                return new IdSearch();
            Search s=null;
            try{
                s=(Search)Class.forName(searchClass).newInstance();
            }catch(Exception e){
                log.error("could not create class "+searchClass);
                return new IdSearch();
            }
            return s;
        }        
    }   
    
    /**
     *This method is used for getting settings that must remain constant for the duration
     *of a search.  It uses these to create a new QueryInfo object.
     */
    private QueryInfo getStaticSettings(HttpServletRequest request)
    {
        int[] dbNums;
        int limit;
        List inputKeys;
        Search s;
        QueryInfo qi;
        
        String input=request.getParameter("inputKey"); //actual input from form feild
        String searchType=request.getParameter("searchType");
        String[] dbTemp=request.getParameterValues("dbs"); //list of databases to use

        log.debug("searchType="+searchType);
        try{//test limit
            limit=Integer.parseInt(request.getParameter("limit"));
            if(limit>=Common.MAXKEYS || limit==0) //cap limit at MAXKEYS
                limit=Common.MAXKEYS;
        }catch(NumberFormatException nfe){
            limit=Common.MAXKEYS; //default limit
        }
        try{//test dbNums for valid input and conver text to numbers   // DBfeilds
            dbNums=new int[dbTemp.length];
            for(int i=0;i<dbTemp.length;i++)
                dbNums[i]=Integer.parseInt(dbTemp[i]);            
        }catch(Exception e){
            dbNums=new int[]{0,1};              
        }
        //log.debug("input="+input);
        if(input==null || input.length()==0)
            return null;
        else
        { //put keys in a list, rather then have to parse the sting every time
            inputKeys=new ArrayList();
            StringTokenizer tok=new StringTokenizer(input);
            while(tok.hasMoreTokens())
                inputKeys.add(tok.nextToken());
        }
        qi=new QueryInfo(dbNums,"",""); //sortCol and displayType should be set later
        s=getSearchObj(searchType); 
        s.init(inputKeys,limit, dbNums);  
        qi.setSearch(s);
        qi.setInputCount(inputKeys.size());
        qi.setObject("general_storage",new HashMap());
        
        return qi;
    }   
    /**
     *This method is used to get settings that can change after the initial query has 
     *been made. It can only modify an existing QueryInfo object.
     */
    private void getDynamicSettings(QueryInfo qi, HttpServletRequest request)
    {
        boolean repeatSearch=false;
        String sortCol=request.getParameter("sortCol");
        if(sortCol!=null){
            qi.setSortCol(sortCol);
            repeatSearch=true;
        }            
        
        String displayType=request.getParameter("displayType");
        log.debug("displayType="+displayType);
        if(displayType!=null) //if a display type was given, save it
            qi.setDisplayType(displayType);
        
        try{            
            qi.setObject("rpp", new Integer(request.getParameter("rpp")));            
        }catch(Exception e){
            if(qi.getObject("rpp")==null) //set to a default value if it has not been set yet
                qi.setObject("rpp", new Integer(50));            
        }
        try{
            qi.setObject("sortDirection",request.getParameter("sortDirection"));
            repeatSearch=true;
        }catch(Exception e){
            if(qi.getObject("sortDirection")==null)
                qi.setObject("sortDirection","asc");
        }
        try{
            int pos=Integer.parseInt(request.getParameter("pos"));
            if(pos < 0)
                pos=0;
            qi.setCurrentPos(pos);
        }catch(Exception e){ }      
        
    }
    private int findCommonKeyType(int[] searchKeys,int[] dataviewKeys)
    {
        int common=-1;
        //simple method
        for(int i=0;i<searchKeys.length;i++) //prefer search keys
            for(int j=0;j<dataviewKeys.length;j++)
                if(searchKeys[i]==dataviewKeys[j])
                {
                    common=searchKeys[i];
                    break;
                }
        log.debug("found common key "+common);
        return common; // -1 indicates an error, will eventually cause an UnsupportedKeyType exception.
    }
    private void initQuerySets()
    {
        //V1QuerySets qs=new V1QuerySets();
        
        V2QuerySets qs=new V2QuerySets();
               
        
        QuerySetProvider.setDataViewQuerySet(qs);
        QuerySetProvider.setSearchQuerySet(qs);
        QuerySetProvider.setRecordQuerySet(qs);
        QuerySetProvider.setDatabaseQuerySet(qs);
        QuerySetProvider.setScriptQuerySet(qs);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
   
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response,false);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response,true);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    
}

