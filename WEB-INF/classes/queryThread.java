/*
 * queryThread.java
 *
 * Created on August 27, 2002, 1:57 PM
 */

/**
 *
 * @author  khoran
 */


import java.net.*;
import java.sql.*;
import java.util.*;
import java.io.*;

public class queryThread extends Thread
   
{
    Connection con;
    String QueryString;
    public Statement stmt;
    ResultSet rs;
    List data=new ArrayList();
    int length,timeOut;
    int count=0;
    public queryThread(String name,int t) 
    {
        super(name);
        connect("common");//connects to database and creates con.
        timeOut=t;        
    }
    public queryThread(String name) 
    {
        super(name);
        connect(name);//connects to database and creates con.
        timeOut=10;
    }
    public void close()
    {
//        System.out.println("closing connection");
        try{
            con.close();
        }catch(SQLException e){
            System.out.println("could not close connection: "+e.getMessage());
        }
    }
    public void run()
    {
        stmt=null;
        rs=null;
//        System.out.println("sending query number "+count++);
        try{
            stmt=con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmt.setEscapeProcessing(false);
            rs=stmt.executeQuery(QueryString);
        }catch(SQLException e){
            System.out.println("queryThread error in run: "+e.getMessage());
            System.out.println("query was: "+QueryString);
            return;
        }catch(Exception e){ return; }
        copyToArray();
        close();
        rs=null;
        stmt=null;
    }
    private void copyToArray()
    {        
        String cell;
        try{
            while(rs.next())
            {
                List row=new ArrayList(length);                
                for(int i=1;i<=length;i++)
                {
                    if(rs.getString(i)==null)
                        row.add("");
                    else
                        row.add(rs.getString(i));
                }
                data.add(row);
            }
        }catch(SQLException e){
            System.out.println("copy error in queryThread: "+e.getMessage());
        }catch(NullPointerException e){}
         catch(Exception e){}
    }
    public void setQuery(String q, int l)
    {
        QueryString=new String(q);
        length=l;
    }
    public List getResults()
    {
        return data;
    }
    public void printData()
    {
        ListIterator l=data.listIterator();
        while(l.hasNext())
        {
            ListIterator l2=((ArrayList)l.next()).listIterator();
            while(l2.hasNext())
                System.out.println(l2.next()+", ");
            System.out.println("");
        }
    }
    private void connect(String DB)
    {        
        //open a connnection with the database server
        //String url="jdbc:mysql://138.23.191.152/"+DB+"?autoReconnect=false"; //was true
        String url="jdbc:postgresql://138.23.191.152/" +DB;
//        try{
//            Class.forName("org.gjt.mm.mysql.Driver").newInstance();
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        try{
            con=DriverManager.getConnection(url,"servlet","512256");
        }catch(SQLException e){
            System.out.println("connection error:"+e.getMessage());
            con=null;
        }  
    }
}
