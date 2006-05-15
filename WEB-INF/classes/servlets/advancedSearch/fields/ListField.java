/*
 * ListField.java
 *
 * Created on April 6, 2006, 10:37 AM
 *
 */

package servlets.advancedSearch.fields;

import java.sql.SQLException;
import java.util.*;
import servlets.*;

/**
 * This field displays a list of values to choose from.
 * These values can be given in an array, or queried from 
 * the database with a given query.
 * @author khoran
 */
public class ListField extends Field
{    
    private Object[] values=null;
    private String[] titles=null;
    private Class type=String.class;
    
    private static final String[] validOps=new String[]{"=","!="};
    
    /**
     * Create a list with the given values. These will
     * also be used as the titles.
     * @param displayName field name
     * @param dbName db column name
     * @param values list of possable values
     */
    public ListField(String displayName,String dbName,Object[] values)
    {
        super(displayName,dbName);
        
        this.values=values;        
    }
    
    /**
     * Create an new list with the given values, and use the 
     * given titles to display on the web page.
     * @param displayName 
     * @param dbName 
     * @param titles a list of titles for each value
     * @param values a list of possable values
     */
    public ListField(String displayName,String dbName,String[] titles,Object[] values)
    {
        super(displayName,dbName);
        
        this.titles=titles;
        this.values=values;
    }
    
    /**
     * Create a new list with values returned from the given query.
     * If the query has only 1 column, it will be used as a list of values.
     * If the query has 2 columns, the first column will be the title, and the
     * second will be the value.  This is so you can display a name, but return an id 
     * number, which will make the join faster. If more than 2 fields are given,
     * the data is not used and an error is logged.
     * @param displayName 
     * @param dbName 
     * @param query an sql query with only 1 or 2 fields
     */
    public ListField(String displayName,String dbName,String query)
    {
        super(displayName,dbName);
        
        //this will query the database to populate values and possibly titles
        getData(query);
    }
        
    
    public String[] getValidOps()
    {
        return validOps;
    }
    
    /**
     * Since a list can contain anything, we must set
     * the element type explicitly.
     * @param type the type of the elements of this list
     * @return The modified ListField
     */
    public ListField setElementType(Class type)
    {
        this.type=type;
        return this;
    }
    
    public String render(String currentValue)
    { //render a drop down list                
        
        StringBuilder output=new StringBuilder("<SELECT name='values'>\n");
        
        for(int i=0;i<values.length;i++)
        {
            String str=values[i].toString();
            output.append("<OPTION ");
            if(currentValue.equalsIgnoreCase(str))
                output.append("selected ");
            if(titles!=null && i < titles.length) 
                output.append(" value='"+str+"' >"+titles[i]+"</OPTION>");
            else
                output.append(">"+str+"</OPTION>");
        }
        output.append("</SELECT>");
        return output.toString();
    }

    private void getData(String query)
    {
        DbConnection dbc=DbConnectionManager.getConnection("khoran");
        List data=null;
        int size;
        
        try{            
            data=dbc.sendQuery(query);
        } catch (SQLException ex) {
            log.error("query failed: "+ex);
        }
        
        if(data==null || data.size()==0)
        { //set an empty list
            log.warn("no values returned for query: "+query);
            values=new String[0];            
        }
        else if( ( size = ((List)data.get(0)).size() ) <= 2)
        { //just set values if size is 1, if size is 2 set titles also            
                        
            values=new String[data.size()];
            if(size==2)
                titles=new String[data.size()];
            
            List row=null;
            int j=0;
            for(Iterator i=data.iterator();i.hasNext();)
            {
                row=(List)i.next();
                if(size==1) //if only one column, it is the value
                    values[j]=(String)row.get(0);
                else
                { //if 2 columns, first is title, second is value
                    titles[j]=(String)row.get(0);
                    values[j]=(String)row.get(1);
                }
                j++;
            }
        }
        else
        { //error
            log.error("ListField query may only contain 1 or 2 columns");
            values=new String[0];
        }                        
    }

    public Class getType()
    {
        return type;
    }
}
