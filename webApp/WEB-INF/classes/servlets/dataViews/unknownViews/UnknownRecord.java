/*
 * UnknownRecord.java
 *
 * Created on October 12, 2004, 1:54 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;
import org.apache.log4j.Logger;

class UnknownRecord implements Record
{
    public String key,description;
    public int estCount;
    public boolean[] go_unknowns;

    private Map subRecords=new HashMap();
    private static Logger log=Logger.getLogger(UnknownRecord.class);

    public UnknownRecord(String key, String desc, int estCount, String[] go_unknowns)
    {
        this.key=key;
        this.description=desc;
        this.estCount=estCount;
        this.go_unknowns=new boolean[3];
        this.go_unknowns[0]=getBoolean(go_unknowns[0]);
        this.go_unknowns[1]=getBoolean(go_unknowns[1]);
        this.go_unknowns[2]=getBoolean(go_unknowns[2]);        
    }
    public UnknownRecord(List values)
    {
        if(values==null || values.size()!=6)
        {
            log.error("invalid list in UnknownRecord constructor");
            return;
        }
        key=(String)values.get(0);
        description=(String)values.get(1);
        estCount=Integer.parseInt((String)values.get(2));
        go_unknowns=new boolean[3];
        go_unknowns[0]=getBoolean((String)values.get(3));
        go_unknowns[1]=getBoolean((String)values.get(4));
        go_unknowns[2]=getBoolean((String)values.get(5));
    }
    private boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
    public void addSubRecord(String name,Object record)
    {
        Object collection=subRecords.get(name);
        if(collection==null)
        {
            collection=new HashSet();
            subRecords.put(name, collection);
        }
        ((Collection)collection).add(record);             
    }

    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof UnknownRecord))
            return false;
        return ((UnknownRecord)o).key.equals(key);
    }
    public int hashCode()
    {
        return key.hashCode();
    }
  
    public String toString()
    {
        StringBuffer out=new StringBuffer();
        out.append("<PRE>\n"+key+"\n");
        for (Iterator i=subRecords.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            out.append(e.getKey()+"\n");
            out.append(e.getValue()+"\n");
        }
        out.append("</PRE>");
        return out.toString();
    }
    
    public void printHeader(java.io.PrintWriter out)
    {
        out.println("<tr bgcolor='"+Common.titleColor+"'><th>Key</th><th>Description</th><th>Molecluar Function Unknown</th>" +
                    "<th>Cellular Component Unknown</th><th>Biological Process Unknown</th></tr>");
    }
    
    public void printRecord(java.io.PrintWriter out)
    {        
        out.println("<tr><td>"+key+"</td><td>"+description+"</td>");
        for(int i=0;i<go_unknowns.length;i++)
            out.println("<td>"+go_unknowns[i]+"</td>");
        out.println("</tr>");               
        
        Record rec;
        Collection list;
        boolean firstRecord;
        
        for(Iterator i=subRecords.values().iterator();i.hasNext();)
        {            
            list=(Collection)i.next();
            if(list==null) continue;
            firstRecord=true;
            out.println("<tr><td colspan='5'><TablE bgcolor='"+Common.dataColor+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='0'>");
            
            for(Iterator j=list.iterator();j.hasNext();)
            {
                rec=(Record)j.next();
                if(firstRecord){
                    rec.printHeader(out);
                    firstRecord=false;
                }
                rec.printRecord(out);
            }            
            out.println("</td></tr></TablE>");
        }        
    }
    
}
