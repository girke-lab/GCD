/*
 * DbConnection.java
 *
 * Created on June 22, 2004, 8:13 AM
 */

package servlets;

/**
 *
 * @author  khoran
 */

import java.sql.*;
import javax.sql.DataSource;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;


/**
 * This class maintains a connection pool to a single database.
 */
public class DbConnection 
{
    DataSource dataSource=null;
    ObjectPool connectionPool=null;
    private String hostname="";
    static Logger log=Logger.getLogger(DbConnection.class);    
        
    
    /**
     * creates a connection to 138.23.191.152 as 'servlet'. Exists just for
     * convienece.
     * @throws java.lang.Exception Thrown if connection fails.
     */
    public DbConnection() throws Exception
    {        
        connect("jdbc:postgresql://138.23.191.152/common","servlet","512256");
        hostname="localhost";        
    }
    /**
     * creates a connection to url, with given user name and password.
     * @throws java.lang.Exception thrown if connection fails.
     * @param url db url (e.g. jdbc:postgresql:5432//hostname/database_name)
     * @param uName user name
     * @param pwd user password
     */
    public DbConnection(String url,String uName,String pwd) throws Exception 
    {
        connect(url,uName,pwd);  
        hostname=url; //should fix this later.
    }
    /**
     * creates a connetion to <CODE>db</CODE> on <CODE>host</CODE> with given user 
     * name and password.
     * @throws java.lang.Exception thown connection fails
     * @param host name of host db is on
     * @param db database name
     * @param uName user name
     * @param pwd user password
     */
    public DbConnection(String host,String db,String uName,String pwd) throws Exception
    {
        connect("jdbc:postgresql://"+host+"/"+db,uName,pwd);  
        hostname=host;
    }
        /**
     * Takes a list of <CODE>hosts</CODE> and a <CODE>db</CODE> and tries to connect to one of them. 
     * If the first host throws an exception, it is caught and the next host is tried
     * untill one of them connects successfully.  If they all fail an uncaught exception
     * is thrown.
     * @throws java.lang.Exception thown if all connection attempst fail
     * @param hosts list of host names to try
     * @param db database name
     * @param uName user name
     * @param pwd user password
     */
    public DbConnection(String[] hosts,String db,String uName,String pwd) throws Exception
    {
        if(dataSource!=null)
            return; //no sense going through the whole list if we already have a connection
        for(int i=0;i<hosts.length;i++)
        {
            if(hosts[i]==null) continue;
            try{
                connect("jdbc:postgresql://"+hosts[i]+"/"+db,uName,pwd);                
                log.info("connected to "+hosts[i]);
                hostname=hosts[i];
                break;
            }catch(Exception e){
                log.info("attempt to connect to "+hosts[i]+" failed");
                if(i+1==hosts.length) //this was the last try
                    throw e;
            }
        }    
    }
   
    /**
     * Does the acutal connection and creates a connetion pool.  This does
     * nothing is a connection already exists, to make a new connection, first
     * call the <CODE>close()</CODE>  method.
     * @param connectURI url to connect to
     * @param name usr name
     * @param password user password
     * @throws java.lang.Exception thrown if connection fails
     */
    public void connect(String connectURI,String name,String password)  throws Exception 
    {        
        log.setLevel(org.apache.log4j.Level.WARN);
        //log.setLevel(org.apache.log4j.Level.INFO);
        
        if(dataSource!=null)
            return;
        Class.forName ("org.postgresql.Driver").newInstance ();
        connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,name,password);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
        dataSource = new PoolingDataSource(connectionPool);        
    }
    /**
     * closes all connections.
     */
    public void close()
    {
        try{
            if(connectionPool!=null)
                connectionPool.close();
        }catch(Exception e){}
        dataSource=null;
        connectionPool=null;
    }
    /**
     * Returns the host currently connected to. This usefull when list of hosts was 
     * used to make a connection.
     * @return name of host currently connected to
     */
    public String getHostName()
    {
        return hostname;
    }
    /**
     * closes connection.
     */
    public void finish()
    {
        close();
    }
    /**
     * returns the number of active and idle connections in pool.
     * @return stats about connection pool
     */
    public String getStats()
    {
        return "active: "+connectionPool.getNumActive()+", idle: "+connectionPool.getNumIdle();
    }
    /**
     * Used to send a sql query.  The results are copied into an ArrayList, 
     * and all fields are stored as Strings.  A ResultSet cannot be obtained
     * because the associated connection whould remain open as long as the ResultSet,
     * and this would eventually drain the connection pool.
     * @param q sql query to send
     * @throws java.sql.SQLException thrown if query fails
     * @return a List of Lists of Strings
     */
    public List sendQuery(String q) throws SQLException 
    {
        long startTime=0;
        if(log.isInfoEnabled())
        {
            startTime=System.currentTimeMillis();
            StackTraceElement[] stes=new Exception("").getStackTrace();
            if(stes.length > 1)
            {
                String className=stes[1].getClassName();
                log.info("query from "+className.substring(className.lastIndexOf('.')==-1?0:className.lastIndexOf('.')+1)+
                    ":"+stes[1].getMethodName()+": "+q);            
            }                
        }
        List l=null;
        Connection conn=null;
        ResultSet rs=null;
        Statement stmt=null;
        try{
            conn=dataSource.getConnection();
            stmt=conn.createStatement();       
            rs=stmt.executeQuery(q);                    
            l=reformat(rs);
        }catch(SQLException e){
            throw e;
        }finally{ //make sure the connection if closed if an error occurs, or the pool empties.            
            if(rs!=null)
                rs.close();
            if(stmt!=null)
                stmt.close();
            if(conn!=null)
                conn.close();
        }
        if(log.isInfoEnabled())
            log.info("time: "+((System.currentTimeMillis()-startTime)/1000.0));
        return l;
    }
      
    /**
     * Creates and sends a prepared query, using the given array for the values.
     * @param sql sql query
     * @param values array of values to be placed in query
     * @throws java.sql.SQLException thrown if query fails
     * @return returns a List of Lists of Strings
     */
    public List sendPreparedQuery(String sql, String[] values) throws SQLException
    {
        //log.info("sending query "+sql);
        Connection conn=dataSource.getConnection();
        PreparedStatement pstmt=conn.prepareStatement(sql);        
        if(values!=null)
            for(int i=0;i<values.length;i++)
                pstmt.setString(i+1, values[i]); //indexes start at 1
        ResultSet rs=pstmt.executeQuery();
        List l=reformat(rs);
        conn.close();
        return l;        
    }        
    /**
     * copies data from a ResultSet into a List of Lists of Strings. Will attempt
     * to convert string data into UTF-8.
     * @param rs an open ResultSet
     * @throws java.sql.SQLException thrown if any ResultSet operations fail
     * @return returns a List of Lists of Strings
     */
    private List reformat(ResultSet rs) throws SQLException
    {        
        if(rs==null)
            return null;
        ArrayList list=new ArrayList(); 
        int size=rs.getMetaData().getColumnCount();

//        ArrayList names=new ArrayList();
//        for(int i=0;i<size;i++)
//            names.add(rs.getMetaData().getColumnName(i+1));
//        list.add(names);
        
        while(rs.next())
        {
            ArrayList row=new ArrayList(); 
            for(int i=1;i<=size;i++)
            {                
                try{
                    int t=rs.getMetaData().getColumnType(i);
                    if(t==Types.LONGVARCHAR || t==Types.CHAR ||
                       t==Types.VARCHAR || t==Types.VARBINARY)
                        if(rs.getBytes(i)==null)
                        {
                            //log.warn("getBytes null for column "+i);
                            row.add(null);
                        }
                        else
                            row.add(new String(rs.getBytes(i),"UTF-8"));
                    else
                        row.add(rs.getString(i));                    
                }catch(java.io.UnsupportedEncodingException e){
                    log.warn("column "+i+" threw an unsopportedEncodingException");
                    row.add(null);
                }
            }
            list.add(row);
        }
        return list;
    }    
}
