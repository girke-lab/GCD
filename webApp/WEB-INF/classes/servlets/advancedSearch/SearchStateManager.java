/*
 * SearchStateManager.java
 *
 * Created on September 22, 2004, 12:40 PM
 */

package servlets.advancedSearch;

/**
 * This class is responsable for managing stored
 *queries persistantly.
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;

public class SearchStateManager
{
    //vectors are synchronized
    private Vector searchStates;
    private String filename;
    private static Logger log=Logger.getLogger(SearchStateManager.class);
    String path="storedQueries/";      
    
    
    /** Creates a new instance of SearchStateManager */
    public SearchStateManager(String filename) 
    {
        this.filename=filename;        
        loadSearchStates();        
    }
    private void loadSearchStates()
    { //load searchStates from persistant storage
        try{
            URL url;
            ObjectInputStream in; 
                
            url=Thread.currentThread().getContextClassLoader().getResource(path+filename);
            if(url==null)
                throw new Exception("file not found");                
            
            in=new ObjectInputStream(url.openStream());            
            searchStates=(Vector)in.readObject();
            in.close();
        }catch(Exception e){
            log.warn("could not open/read "+filename+": "+e.getMessage());
            searchStates=new Vector();
        }
    }
    private void writeSearchStates()
    {//write searchStates to file
       try{           
           URL url;
           ObjectOutputStream out;
           String fullName="";
           
           url=Thread.currentThread().getContextClassLoader().getResource(path);           
           if(url==null)
               throw new Exception("file not found");           
           fullName=url.getPath()+filename;
                          
            out=new ObjectOutputStream(new FileOutputStream(fullName));           
            out.writeObject(searchStates);
            out.close(); 
        }catch(Exception e){
            log.error("could not open/write "+filename+": "+e.getMessage());
        }
    }
    public void addSearchState(SearchState ss)
    {     
        searchStates.add(ss);        
        writeSearchStates();
    }
    public void removeSearchState(int i)
    {
        if(i >= 0 && i < searchStates.size())
            searchStates.remove(i);
        writeSearchStates();
    }
    public Collection getSearchStateList()
    {
        return searchStates;
    }
    public SearchState getSearchState(int i)
    {
        return (SearchState)searchStates.get(i);
    }
    public void setSearchState(int i, SearchState ss)
    {
        if(i >= 0 && i < searchStates.size())
            searchStates.set(i,ss);
        writeSearchStates();
    }
}

//            else if((url=ClassLoader.getSystemResource(path+filename))!=null)
//                log.debug("2 found in file at: "+url);
//            else if((url=ClassLoader.getSystemClassLoader().getResource(path+filename))!=null)
//                log.debug("3 found in file at: "+url);  
//            else if((url=new servlets.QueryPageServlet().getClass().getClassLoader().getResource(path+filename))!=null)            
//                log.debug("4 found in file at: "+url);                              
