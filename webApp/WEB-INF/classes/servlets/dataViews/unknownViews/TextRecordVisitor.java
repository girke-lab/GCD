/*
 * TextRecordVisitor.java
 *
 * Created on October 19, 2004, 2:11 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.io.*;
import java.util.*;

public class TextRecordVisitor implements RecordVisitor
{
    
    /** Creates a new instance of TextRecordVisitor */
    public TextRecordVisitor()
    {
    }
    
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        out.write("go_number,text,function,");
    }
    
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write("target,score,evalue,dbname,");
    }
    
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("key,description,");   
        Collection list;
        for(Iterator i=ur.subRecords.values().iterator();i.hasNext();)
        {
            list=(Collection)i.next();
            if(list==null)
                continue;
            for(Iterator j=list.iterator();j.hasNext();)
                ((Record)j.next()).printHeader(out,this);     
        }        
    }
    
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        out.write(gr.go_number+","+gr.text+","+gr.function+",");
    }
    
    public void printRecord(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write(br.target+","+br.score+","+br.evalue+","+br.dbname+",");
    }
    
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("\n"+ur.key+","+ur.description+",");
        Collection list;
        for(Iterator i=ur.subRecords.values().iterator();i.hasNext();)
        {
            list=(Collection)i.next();
            if(list==null)
                continue;
            for(Iterator j=list.iterator();j.hasNext();)
                ((Record)j.next()).printRecord(out,this);     
        }
            
    }
    
}
