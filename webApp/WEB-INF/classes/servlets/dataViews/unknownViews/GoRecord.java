/*
 * GoRecord.java
 *
 * Created on October 12, 2004, 3:16 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;

public class GoRecord implements Record
{
    String go_number,text,function;
    
    private static Logger log=Logger.getLogger(GoRecord.class);
    
    /** Creates a new instance of GoRecord */
    public GoRecord(String go_number,String text,String function)
    {
        this.go_number=go_number;
        this.text=text;
        this.function=function;
    }
    public GoRecord(List values)
    {
        if(values==null || values.size()!=3)
        {
            log.error("invalid values list in GoRecord constructor");
            return;
        }
        go_number=(String)values.get(0);
        function=(String)values.get(1);
        text=(String)values.get(2);
        
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof GoRecord))
            return false;
        return ((GoRecord)o).go_number.equals(go_number);
    }
    public int hashCode()
    {
        return go_number.hashCode();
    }
    public String toString()
    {
        return go_number+" "+text;
    }
    
    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);
        //out.write("<tr bgcolor='"+Common.titleColor+"'><th>Go Number</th><th>Description</th><th>Function</th></tr>\n");
    }
    
    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);
        //out.write("<tr><td>"+go_number+"</td><td>"+text+"</td><td>"+function+"</td></tr>\n");
    }
       
}
