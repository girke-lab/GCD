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

public class SearchTreeManager 
{
    private static Logger log=Logger.getLogger(SearchTreeManager.class);
    
    Properties queries; 
    String path="storedQueries/";
    String filename;
    
    /** Creates a new instance of SearchTreeManager */
    public SearchTreeManager(String filename)
    {
        this.filename=filename;
        queries=new Properties();
        try{
            InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(path+filename);
            if(is==null)
            {
                log.error("could not open inputstream for "+path+filename);
                return;
            }
            queries.load(is);
        }catch(IOException e){
            log.error("could not read queries from file "+filename+": "+e);
        }
    }    

    public Query getSearchState(String name)
    {
        String sql=queries.getProperty(name+".sql");
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
    public String getDescription(String name)
    {
        log.debug("getting description for "+name);
        return queries.getProperty(name+".description");
    }

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
        return names;
    }

    public void removeSearchState(String name)
    {
        queries.remove(name+".sql");
        queries.remove(name+".description");
        writeQueries();
    }
   
    public void addSearchState(Query q,String description)
    {
        log.debug("adding search state");
        String key;
        do
        {
            key="query_"+Integer.toString((int)(Math.random()*1000));
        }while(queries.containsKey(key));
        log.debug("key="+key);
        queries.setProperty(key+".sql", new SqlVisitor().getSql(q));
        queries.setProperty(key+".description",description);
        writeQueries();
    }

    public void setSearchState(String name,Query q)
    {
        SqlVisitor sv=new SqlVisitor();
        queries.setProperty(name+".sql", sv.getSql(q));
        queries.setProperty(name+".limit",q.getLimit().toString());
        writeQueries();
    }
    public void setDescription(String name,String desc)
    {
        queries.setProperty(name+".description",desc);
        writeQueries();
    }
    private void writeQueries()
    {
        log.debug("writing tree queries");
        try{
            URL file=Thread.currentThread().getContextClassLoader().getResource(path);
            if(file==null)
                throw new IOException("could not find directory "+path);
            String fullPath=file.getPath()+filename;
            log.debug("fullPath="+fullPath);
            OutputStream os=new FileOutputStream(fullPath);                        
            queries.store(os,"SQL queries used for advanced search page");
        }catch(IOException e){
            log.error("could no write queries: "+e);            
               
        }
    }
}
