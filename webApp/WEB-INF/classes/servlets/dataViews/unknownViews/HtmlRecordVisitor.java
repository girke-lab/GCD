/*
 * HtmlRecordVisitor.java
 *
 * Created on October 19, 2004, 1:54 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.io.*;
import java.util.*;
import servlets.Common;

public class HtmlRecordVisitor implements RecordVisitor
{
    
    /** Creates a new instance of HtmlRecordVisitor */
    public HtmlRecordVisitor()
    {
    }
    
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
         out.write("<tr bgcolor='"+Common.titleColor+"'><th>Go Number</th><th>Description</th><th>Function</th></tr>\n");
    }
    
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+Common.titleColor+"'><th>Target Key</th><th>E-value</th>" +
                    "<th>Score</th><th>Database</th></tr>\n");
    }
    
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+Common.titleColor+"'><th>Key</th><th>Description</th></tr>\n");
    }
    
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
         out.write("<tr><td>"+gr.go_number+"</td><td>"+gr.text+"</td><td>"+gr.function+"</td></tr>\n");
    }
    
    public void printRecord(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
         out.write("<tr><td>"+br.target+"</td><td>"+br.evalue+"</td><td>"+br.score+"</td>" +
                    "<td>"+br.dbname+"</td></tr>\n");
    }
    
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("<tr><td><a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+
            ur.key.subSequence(0,ur.key.lastIndexOf('.'))+"'>"+ur.key+"</a></td><td>"+ur.description+"</td></tr>\n");
        String[] names=new String[]{"mfu","ccu","bpu"};
        out.write("<tr><td colspan='2'>\n");
        for(int i=0;i<ur.go_unknowns.length;i++)
            out.write("<b>"+names[i]+"</b>: "+ur.go_unknowns[i]+" &nbsp&nbsp&nbsp \n");
        out.write("</td></tr>\n");               
        
        Record rec;
        Collection list;
        boolean firstRecord;
        
        for(Iterator i=ur.subRecords.values().iterator();i.hasNext();) 
        {            
            list=(Collection)i.next();
            if(list==null) continue;
            firstRecord=true;
            out.write("<tr><td colspan='5'><TablE bgcolor='"+Common.dataColor+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='0'>\n");
            
            for(Iterator j=list.iterator();j.hasNext();)
            {
                rec=(Record)j.next();
                if(firstRecord){
                    rec.printHeader(out,this);
                    firstRecord=false;
                }
                rec.printRecord(out,this);
            }            
            out.write("</td></tr></TablE>\n");
        }        
    }
    
}
