/*
 * SearchStateManager.java
 *
 * Created on September 22, 2004, 12:40 PM
 */

package servlets.advancedSearch;



import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.log4j.Logger;

/**
 * This class is responsable for managing stored
 *queries persistantly.
 * @author  khoran
 */
public class SearchStateManager
{
    //vectors are synchronized
    private Vector searchStates;
    private String filename;
    private static Logger log=Logger.getLogger(SearchStateManager.class);
    String path="storedQueries/";      
    URL file;
    
    /**
     * Creates a new instance of SearchStateManager
     * @param filename Name of file to store searchStates in.
     */
    public SearchStateManager(String filename) 
    {
        this.filename=filename; 
        try{
            file=Thread.currentThread().getContextClassLoader().getResource(path+filename);
            if(file==null)
                throw new Exception("file not found");                
        }catch(Exception e){
            log.warn("could not find file "+path+filename+": "+e);
        }
        loadSearchStates();        
    }
    public SearchStateManager(URL file)
    {
        this.file=file;
        loadSearchStates();
    }
    private void loadSearchStates()
    { //load searchStates from persistant storage
        try{            
            ObjectInputStream in; 
                
//            url=Thread.currentThread().getContextClassLoader().getResource(path+filename);
//            if(url==null)
//                throw new Exception("file not found");                
//            
            in=new ObjectInputStream(file.openStream());            
            searchStates=(Vector)in.readObject();
            in.close();
        }catch(Exception e){
            log.warn("could not read "+filename+": "+e.getMessage());
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
    /**
     * Adds a new SearchState to storage, and writes it to disk.
     * @param ss SearchState to add.
     */
    public void addSearchState(SearchState ss)
    {     
        searchStates.add(ss);        
        writeSearchStates();
    }
    /**
     * Removes the searchState at index i, and writes changes to disk.
     * @param i index of searchState to remove.
     */
    public void removeSearchState(int i)
    {
        if(i >= 0 && i < searchStates.size())
            searchStates.remove(i);
        writeSearchStates();
    }
    /**
     * Returns a list of all searchStates
     * @return list of searchStates
     */
    public Collection getSearchStateList()
    {
        return searchStates;
    }
    /**
     * Returns searchState at index i.
     * @param i index of searchState
     * @return searchState at index i.
     */
    public SearchState getSearchState(int i)
    {
        return (SearchState)searchStates.get(i);
    }
    /**
     * Used to update the searchState at index i, changes are also written to disk.
     * @param i index
     * @param ss new searchState.
     */
    public void setSearchState(int i, SearchState ss)
    {
        if(i >= 0 && i < searchStates.size())
            searchStates.set(i,ss);
        writeSearchStates();
    }
}