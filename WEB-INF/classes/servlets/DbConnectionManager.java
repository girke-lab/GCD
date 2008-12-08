/*
 * DbConnectionManager.java
 *
 * Created on September 8, 2004, 2:55 PM
 */

package servlets;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * This is a static class that maintains a synchronized collection of DbConnection
 * objects.  Each DbConnection should be to a different database.  Also 
 * creates some default connections.
 * @author khoran
 */
public class DbConnectionManager
{
    /**
     * A Map of connection names to DbConnection objects.
     */
    private static Map<String,DbConnection> connections=null; //this is a shared object.
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
        connections.put(name,dbc);        
    }
    public static boolean removeConnection(String name)
    {
        if(connections==null || !connections.containsKey(name))
            return false;
        DbConnection dbc=connections.remove(name);
        if(dbc==null)
            return false;
        dbc.close();
        return true;
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
        DbConnection dbc=connections.get(name);
        if(dbc==null)
            log.error("db connection '"+name+"' not found");
        return dbc;
    }
    /**
     * Return a list of all connection names available
     * @return a collection of valid connection names
     */
    public static Collection<String> getConnectionNames()
    {
        if(connections==null)
            return new ArrayList<String>(); //empty list of connections
        return connections.keySet();
    }
    /**
     * This method initializes <CODE>connections</CODE> and creates DbConnection 
     * objects for databases: common, unknowns, and khoran
     */
    private static synchronized void initMap()
    {
        connections=Collections.synchronizedMap(new HashMap<String,DbConnection>());
        
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
                //connections.put("khoran",new DbConnection("jdbc:postgresql://bioweb.bioinfo.ucr.edu:5433/khoran","servlet","512256")); //connect to postgres            
                //connections.put("khoran",new DbConnection("jdbc:postgresql://db1.bioinfo.ucr.edu:5432/khoran","servlet","512256")); 
                connections.put("khoran",new DbConnection("jdbc:postgresql://space1.bioinfo.ucr.edu:5432/khoran","servlet","512256")); //connect to postgres
                
                //for testing
                //connections.put("khoran",new DbConnection("jdbc:postgresql://bioweb.bioinfo.ucr.edu:5432/khoran_loading","servlet","512256")); 
                //connections.put("khoran",new DbConnection("jdbc:postgresql://db2.bioinfo.ucr.edu:5432/khoran","servlet","512256")); 
                //connections.put("khoran",new DbConnection("jdbc:postgresql://localhost:5432/khoran","servlet","512256")); //connect to postgres            

                //for home testing  
                //connections.put("khoran",new DbConnection("jdbc:postgresql://localhost:5430/khoran_loading","khoran","512_256_1024")); //connect to postgres            
        }catch(Exception e){            
            log.warn("failed to connect to khoran database: "+e.getMessage());
        }
        
    }
}
