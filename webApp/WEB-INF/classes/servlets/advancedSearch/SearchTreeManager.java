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
    final String path="storedQueries/";
    //final String filename; //this should be set by constructor and never changed.
    final URL file;
    
    /** Creates a new instance of SearchTreeManager */
    public SearchTreeManager(String filename)
    {
        //this.filename=filename;
        queries=new Properties();
        URL temp=null;
        try{
            temp=Thread.currentThread().getContextClassLoader().getResource(path);            
            if(temp==null)
                throw new IOException("could not find directory "+path);
            //we really want a URL to this file, but must use only the directory
            //above beause the file might not exist yet, but we still want at least
            //a url to its future directory.
            temp=new URL("file://"+temp.getPath()+filename);
        }catch(Exception e){
            log.warn("could not find file "+path+filename+": "+e);  
            file=null;
            return;
        }
        
        file=temp; //make it final.        
        readQueries();
    }    
    public SearchTreeManager(URL file)
    {
        queries=new Properties();
        this.file=file;
        readQueries();
    }
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
    public String getDescription(String name)
    {        
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
        List l=new LinkedList(names);        
        Collections.sort(l,new QueryComparator());        
        return l;
    }

    public void removeSearchState(String name)
    {
        queries.remove(name+".sql");
        queries.remove(name+".description");
        queries.remove(name+".limit");
        writeQueries();
    }
   
    public void addSearchState(Query q,String description)
    {
        log.debug("adding search state");
        String key;
        do
        {
            key="query_"+Integer.toString((int)(Math.random()*1000));
        }while(queries.containsKey(key+".sql"));
        log.debug("key="+key);
        queries.setProperty(key+".sql", new SqlVisitor().getSql(q));
        queries.setProperty(key+".description",description);
        queries.setProperty(key+".limit",q.getLimit().toString());
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
    private void readQueries()
    {
        try{     
            
            log.debug("fullPath="+file.getPath());
            InputStream is=new FileInputStream(file.getPath());
                        
            queries.load(is);
            //log.debug("initial quries are: "+queries);
        }catch(IOException e){
            log.error("could not read queries from file "+file+": "+e);
        }
    }
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
