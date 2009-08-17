<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<jsp:useBean id='dbm' class='servlets.DbConnectionManager' />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Db Admin</title>
    </head>
    <body>


    <%  String newDb=request.getParameter("db");        
        if(newDb!=null)
        {
            String hostname, port,dbname="khoran";
            port="5432";           
            servlets.DbConnection dbc;
            if(newDb.equals("db1"))
                hostname="db1.bioinfo.ucr.edu";
            else if(newDb.equals("db2"))
                hostname="db2.bioinfo.ucr.edu";
            else if(newDb.equals("bioweb"))
                hostname="bioweb.bioinfo.ucr.edu";
            else if(newDb.equals("space1"))
                hostname="space1.bioinfo.ucr.edu";
            else if(newDb.equals("space2"))
                hostname="space2.bioinfo.ucr.edu";
            else if(newDb.equals("keen-192-131"))
                hostname="keen-192-131.ucr.edu";
            else if(newDb.equals("bioweb_dev"))
            {
                hostname="bioweb.bioinfo.ucr.edu";
                dbname="khoran_loading";
            }
            else if(newDb.equals("db1_db2"))
            {
                hostname="bioweb.bioinfo.ucr.edu";
                port="5433";                
            }
            else
                hostname=null;
        
            if(hostname!=null && (dbc=dbm.getConnection("khoran"))!=null )
            {
                dbc.close();
                dbc.connect("jdbc:postgresql://"+hostname+":"+port+"/"+dbname,"servlet","512256");
            }
            
//            if(hostname!=null && !dbm.removeConnection("khoran"))
//                out.println("could not remove existing connection");
//            else if(hostname!=null)
//                dbm.setConnection("khoran",new servlets.DbConnection());
            
        }
    
    %>

    <h4>Current database connection:</h4>
    <%  out.println(dbm.getConnection("khoran").getHostName()); %>
    <p>
    
    <FORM action='dbAdmin.jsp' method=post>
        
        <select name='db'>
            <option value='db1'>db1.bioinfo.ucr.edu</option>
            <option value='db2'>db2.bioinfo.ucr.edu</option>
            <option value='bioweb'>bioweb.bioinfo.ucr.edu</option>
            <option value='space1'>space1.bioinfo.ucr.edu</option>
            <option value='space2'>space2.bioinfo.ucr.edu</option>
            <option value='bioweb_dev'>bioweb.bioinfo.ucr.edu (dev)</option>
            <option value='db1_db2'>db1 and db2</option>
        </select>
        
        <INPUT type=SUBMIT />
    </FORM>
    
   
    
    </body>
</html>
