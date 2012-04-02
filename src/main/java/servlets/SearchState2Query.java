/*
 * SearchState2Query.java
 *
 * Created on February 16, 2005, 1:01 PM
 */

package servlets;

/**
 *
 * @author khoran
 */


import servlets.advancedSearch.*;
import servlets.advancedSearch.queryTree.*;
import java.util.*;
import java.net.*;

public class SearchState2Query
{
    
    /** Creates a new instance of SearchState2Query */
    public SearchState2Query()
    {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    { //args should contain a list of SearchState seriealized objects
        
        if(args.length < 2)
        {
            System.out.println("Usage: java SearchState2Query <db name> <list of serialized SearchStates>");
            return;
        }
        SearchState2Query ssq=new SearchState2Query();
        SearchableDatabase sd;       
        String baseName;
        

        sd=ssq.getSearchableDatabase(args[0]);
        if(sd==null)
        {
            System.out.println("could not create a SearchableDatabase");
            return;
        }
        
        for(int i=1;i<args.length;i++)
        {
            int j=args[i].lastIndexOf('.');
            baseName=args[i];
            if(j!=-1)
                baseName=args[i].substring(0,j);
            ssq.convert(sd,args[i],baseName+".properties");                  
        }
        
    }
    
    public void convert(SearchableDatabase sd,String ssFile,String propFile)
    {        
        URL source,dest;
        try{
            source=new URL("file://"+ssFile);
            dest=new URL("file://"+propFile);
        }catch(MalformedURLException e){
            System.out.println("url error with \n"+ssFile+" or \n"+propFile+
                    "error: "+e);
            return;
        }
        
        SearchStateManager ssm=new SearchStateManager(source);
        Collection states=ssm.getSearchStateList();
        SearchState ss;
        Query q;
        SearchTreeManager stm=new SearchTreeManager(dest);
        
        
        for(Iterator i=states.iterator();i.hasNext();)
        {
            ss=(SearchState)i.next();
            q=sd.buildQueryTree(ss);
            stm.addSearchState(q,ss.getDescription());
        }
    }
    public SearchableDatabase getSearchableDatabase(String dbName)
    {
        SearchableDatabase sd=null;
        
        if(dbName==null || dbName.equals(""))
        {
            System.out.println("no database name given");
            return null;
        }
        
        if(dbName.equals("common"))
            sd=new CommonDatabase();
        
        if(sd==null)
            System.out.println("could not find class for database "+dbName);
        return sd;
    }
}
