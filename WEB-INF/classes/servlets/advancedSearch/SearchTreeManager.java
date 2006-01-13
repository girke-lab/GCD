/*
 * SearchTreeManager.java
 *
 * Created on February 7, 2005, 9:28 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author khoran
 */

import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;
import servlets.advancedSearch.queryTree.Query;
import servlets.advancedSearch.visitors.SqlVisitor;

/**
 * This class manages the persistant storage of Query objects as sql strings.
 * 
 */
public class SearchTreeManager 
{
    private static Logger log=Logger.getLogger(SearchTreeManager.class);
    
    Properties queries; 
    final String path="storedQueries/";
    //final String filename; //this should be set by constructor and never changed.
    final URL file;
    
    /** Creates a new instance of SearchTreeManager */
    public SearchTreeManager(String filename)
    {        
        log.debug("filename="+filename);
        queries=new Properties();
        URL temp=null;
        try{
            temp=Thread.currentThread().getContextClassLoader().getResource(path);            
            if(temp==null)
                throw new IOException("could not find directory "+path);
            //we really want a URL to this file, but must use only the directory
            //above beause the file might not exist yet, but we still want at least
            //a url to its future directory.
            log.debug("temp="+temp);
            String seperator="/";
            if(temp.toString().endsWith("/"))
                seperator="";
            temp=new URL("file://"+temp.getPath()+seperator+filename);
            log.debug("new temp="+temp);
        }catch(Exception e){
            log.warn("could not find file "+path+filename+": "+e);  
            file=null;
            return;
        }
        
        file=temp; //make it final.        
        readQueries();
    }    
    /**
     * Create a new SearchTreeManager that reads queries from the 
     * given URL. Can write them also if the URL is a local file.
     * @param file 
     */
    public SearchTreeManager(URL file)
    {
        queries=new Properties();
        this.file=file;
        readQueries();
    }
    /**
     * Retrievies a Query object by its given name. Reads the
     * sql string from file and parses it to create a Query object.
     * @param name assigned name of query
     * @return new Query object
     */
    public Query getSearchState(String name)
    {
        //log.debug("queries are: "+queries);
        String sql=queries.getProperty(name+".sql");
        if(sql==null)
        {
            log.error("no entry found for "+name+".sql");
            return null;
        }
        String limitStr=queries.getProperty(name+".limit");
        Integer limit=new Integer(100000);
        if(limitStr!=null)
            limit=Integer.valueOf(limitStr);
        Query q=null;
        try{//parse this sql into a Query object
            q=SqlParser.parse(sql);
        }catch(Zql.ParseException e){
            log.error("parse error: "+e);
        }
        if(q==null)
        {
            log.error("could not parse sql: "+sql);
            return null;
        }
        q.setLimit(limit);
        return q;
    }
    /**
     * Returns the description if this query
     * @param name name of query
     * @return description string
     */
    public String getDescription(String name)
    {        
        return queries.getProperty(name+".description");
    }

    /**
     * Returns a list of available queries.  Queries are
     * assigned a name when they are stored, so this provides a list
     * of names that can be used to retrieve further info later. 
     * @return a sorted collection of Strings
     */
    public Collection getSearchStateList()
    {
        log.debug("getting search list: "+queries.keySet());
        Set names=new HashSet();
        for(Iterator i=queries.keySet().iterator();i.hasNext();)
        {
            String key=(String)i.next();
            int c=key.indexOf('.');
            if(c!=-1) //a dot was found
                names.add(key.substring(0,c));
            else
                names.add(key);
        }
        List l=new LinkedList(names);        
        Collections.sort(l,new QueryComparator());        
        return l;
    }

    /**
     * Removes given query from storage.  Changes are written immediatly.
     * @param name name of query
     */
    public void removeSearchState(String name)
    {
        queries.remove(name+".sql");
        queries.remove(name+".description");
        queries.remove(name+".limit");
        writeQueries();
    }
   
    /**
     * Add a new Query to storge.  A description can also be
     * passed and stored. A random name is assigned to the 
     * query for later lookup. A list of current names
     * can be retrieved from {@link getSearchStateList()}.
     * @param q a Query object
     * @param description description of query
     */
    public void addSearchState(Query q,String description)
    {
        log.debug("adding search state");
        String key;
        do
        {
            key="query_"+Integer.toString((int)(Math.random()*1000));
        }while(queries.containsKey(key+".sql"));
        log.debug("key="+key);
        queries.setProperty(key+".sql", new SqlVisitor().getSql(q,true));
        queries.setProperty(key+".description",description);
        queries.setProperty(key+".limit",q.getLimit().toString());
        writeQueries();
    }

    /**
     * Update the given query name with a new Query object.  The Query
     * object is converted to sql first and written to the properties file 
     * immediatly.
     * @param name name of query to update 
     * @param q new query to store
     */
    public void setSearchState(String name,Query q)
    {        
        SqlVisitor sv=new SqlVisitor();
        queries.setProperty(name+".sql", sv.getSql(q,true));
        queries.setProperty(name+".limit",q.getLimit().toString());
        writeQueries();
    }
    /**
     * Update the description of a given query
     * @param name query name
     * @param desc new description
     */
    public void setDescription(String name,String desc)
    {
        queries.setProperty(name+".description",desc);
        writeQueries();
    } 
    /**
     * Read in properties file. Uses the file variable to get a path to a 
     * properties file which it then reads in to the queries object.
     */
    private void readQueries()
    {
        try{     
            
            log.debug("fullPath="+file.getPath());
            InputStream is=new FileInputStream(file.getPath());
                        
            queries.load(is);
            log.debug("initial quries are: "+queries);
        }catch(IOException e){
            log.error("could not read queries from file "+file+": "+e);
        }
    }
    /**
     * Write queries in memory to properties file. Uses path from file object
     * to find properties file and writes data. 
     */
    private void writeQueries()
    {
        log.debug("writing tree queries");
        try{
            //URL file=Thread.currentThread().getContextClassLoader().getResource(path);
            //if(file==null)
            //    throw new IOException("could not find directory "+path);
            
//            File f=new File(file.getPath());
//            String fullPath=file.getPath()+filename;
//            log.debug("fullPath="+fullPath);
            OutputStream os=new FileOutputStream(file.getPath());                        
            queries.store(os,"SQL queries used for advanced search page");
            os.close();
        }catch(IOException e){
            log.error("could no write queries: "+e);            
               
        }
    }
    
    /**
     * This Comparator orders query entries by thier description field, 
     * so that the query listing looks nice on the web page.  
     */
    class QueryComparator implements Comparator
    {
        public int compare(Object o1,Object o2)
        {
            if(!(o1 instanceof String) || !(o2 instanceof String))
                throw new ClassCastException("bad types in QueryComparator: o1 is a "+o1.getClass()+", o2 is a "+o2.getClass());
            
            String d1,d2;
            d1=getDescription((String)o1);
            d2=getDescription((String)o2);
            return d1.compareTo(d2);
        }
    }
}
