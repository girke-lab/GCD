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


public class DbConnection 
{
    DataSource dataSource=null;
    ObjectPool connectionPool=null;
    private String hostname="";
    static Logger log=Logger.getLogger(DbConnection.class);    
    
    /** Creates a new instance of DbConnection */
    public DbConnection() throws Exception
    {
        connect("jdbc:postgresql://138.23.191.152/common","servlet","512256");
        hostname="localhost";        
    }
    public DbConnection(String url,String uName,String pwd) throws Exception 
    {
        connect(url,uName,pwd);  
        hostname=url; //should fix this later.
    }
    public DbConnection(String host,String db,String uName,String pwd) throws Exception
    {
        connect("jdbc:postgresql://"+host+"/"+db,uName,pwd);  
        hostname=host;
    }
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
   
    public void connect(String connectURI,String name,String password)  throws Exception 
    {
        if(dataSource!=null)
            return;
        Class.forName ("org.postgresql.Driver").newInstance ();
        connectionPool = new GenericObjectPool(null);
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI,name,password);
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory,connectionPool,null,null,false,true);
        dataSource = new PoolingDataSource(connectionPool);        
    }
    public void close()
    {
        try{
            if(connectionPool!=null)
                connectionPool.close();
        }catch(Exception e){}
        dataSource=null;
        connectionPool=null;
    }
    public String getHostName()
    {
        return hostname;
    }
    public void finish()
    {
        close();
    }
    public String getStats()
    {
        return "active: "+connectionPool.getNumActive()+", idle: "+connectionPool.getNumIdle();
    }
    public List sendQuery(String q) throws SQLException 
    {
        //log.info("sending query "+q);
        Connection conn=dataSource.getConnection();
        Statement stmt=conn.createStatement();        
        ResultSet rs=stmt.executeQuery(q);        
        List l=reformat(rs);
        if(l==null)
            System.out.println("null list in sendQuery of dbc");
        else
            System.out.println("list is not null in sendQuery of dbc");
        conn.close();
        return l;
    }
    
    // these are bad becuase the ResultSet closes with the connection,
    //but the connection must be closed or it is not returned to pool;
//    public ResultSet sendQueryRS(String q) throws SQLException
//    {
//        log.info("sending query "+q);
//        Connection conn=dataSource.getConnection();
//        Statement stmt=conn.createStatement();
//        ResultSet rs=stmt.executeQuery(q);        
//        conn.close();
//        return rs;
//    }
//    public ResultSet sendPreparedQueryRS(String sql, String[] values) throws SQLException
//    {
//        
//    }
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
    private List reformat(ResultSet rs) throws SQLException
    {        
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
