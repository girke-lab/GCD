/*
 * DbConnectionManager.java
 *
 * Created on September 8, 2004, 2:55 PM
 */

package servlets;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;

/**
 * This is a static class that maintains a synchronized collection of DbConnection
 * objects.  Each DbConnection should be to a different database.  Also 
 * creates some default connections.
 */
public class DbConnectionManager
{
    /**
     * A Map of connection names to DbConnection objects.
     */
    private static Map connections=null; //this is a shared object.
    /**
     * logger
     */
    private static Logger log=Logger.getLogger(DbConnectionManager.class);
    
    /** Creates a new instance of DbConnectionManager */
    public DbConnectionManager() 
    {
    }
    
    /**
     * Add a {@link DbConnection} to the collection with the given name.
     * Then name is usally the database name, but does not have to be.
     * @param name name of connection
     * @param dbc connection
     */
    public static void setConnection(String name,DbConnection dbc)
    {
        if(connections==null)
            initMap();
        //must be syncronized
        synchronized(connections){
            connections.put(name,dbc);        
        }
    }
    /**
     * get the given connection.
     * @param name connection name
     * @return a reference to a DbConnection
     */
    public static DbConnection getConnection(String name)
    {
        if(connections==null)
            initMap();
        return (DbConnection)connections.get(name);        
    }
    public static Collection getConnectionNames()
    {
        if(connections==null)
            return new ArrayList(); //empty list of connections
        return connections.keySet();
    }
    /**
     * This method initializes <CODE>connections</CODE> and creates DbConnection 
     * objects for databases: common, unknowns, and khoran
     */
    private static synchronized void initMap()
    {
        connections=new HashMap();
        
        //add some default connections
//        try{
//            Class.forName("org.postgresql.Driver").newInstance();            
//            connections.put("common",new DbConnection("jdbc:postgresql://bioweb.bioinfo.ucr.edu/khoran_loading","servlet","512256")); //connect to postgres                        
//        }catch(Exception e){            
//            log.warn("failed to connect to common database: "+e.getMessage());
//        }

        try{
            Class.forName("org.postgresql.Driver").newInstance();
                //for deployment
                connections.put("khoran",new DbConnection("jdbc:postgresql://bioweb.bioinfo.ucr.edu:5433/khoran","servlet","512256")); //connect to postgres            
                //for testing
                //connections.put("khoran",new DbConnection("jdbc:postgresql://bioweb.bioinfo.ucr.edu:5432/khoran_loading","servlet","512256")); //connect to postgres            
                //connections.put("khoran",new DbConnection("jdbc:postgresql://localhost:5430/khoran_loading","khoran","512_256_1024")); //connect to postgres            
        }catch(Exception e){            
            log.warn("failed to connect to khoran database: "+e.getMessage());
        }
        
    }
}
