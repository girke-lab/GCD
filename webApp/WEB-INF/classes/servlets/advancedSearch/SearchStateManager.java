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
import org.apache.log4j.Logger;

public class SearchStateManager
{
    //vectors are synchronized
    private Vector searchStates;
    private String filename;
    private static Logger log=Logger.getLogger(SearchStateManager.class);
    private ObjectOutputStream out;
    private ObjectInputStream in; 
    
    /** Creates a new instance of SearchStateManager */
    public SearchStateManager(String filename) 
    {
        this.filename=filename;        
        loadSearchStates();        
    }
    private void loadSearchStates()
    { //load searchStates from persistant storage
        try{
            in=new ObjectInputStream(new FileInputStream(filename));            
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
            out=new ObjectOutputStream(new FileOutputStream(filename));
            //opening file zeros it, so write vector out now
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
//        log.debug("retrieving list: "+searchStates);
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
