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

public class DbConnectionManager
{
    private static Map connections=null; //this is a shared object.
    private static Logger log=Logger.getLogger(DbConnectionManager.class);
    
    /** Creates a new instance of DbConnectionManager */
    public DbConnectionManager() 
    {
    }
    
    public static void setConnection(String name,DbConnection dbc)
    {
        if(connections==null)
            initMap();
        //must be syncronized
        synchronized(connections){
            connections.put(name,dbc);        
        }
    }
    public static DbConnection getConnection(String name)
    {
        if(connections==null)
            initMap();
        return (DbConnection)connections.get(name);        
    }
    
    private static synchronized void initMap()
    {
        connections=new HashMap();
        
        //add some default connections
        try{
            Class.forName("org.postgresql.Driver").newInstance();
            connections.put("common",new DbConnection()); //connect to postgres            
        }catch(Exception e){            
            log.warn("failed to connect to common database: "+e.getMessage());
        }
        try{
            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
            connections.put("unknowns",new DbConnection("jdbc:mysql://138.23.191.152/unknowns","servlet","512256"));            
        }catch(Exception e){
            log.warn("failed to connect to unknonws database: "+e.getMessage());
        }
        try{
            Class.forName("org.postgresql.Driver").newInstance();
            connections.put("khoran",new DbConnection("jdbc:postgresql://bioinfo.ucr.edu/khoran","servlet","512256")); //connect to postgres            
        }catch(Exception e){            
            log.warn("failed to connect to khoran database: "+e.getMessage());
        }
        
    }
}
