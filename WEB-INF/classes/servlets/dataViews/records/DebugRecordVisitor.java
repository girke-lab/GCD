/*
 * DebugRecordVisitor.java
 *
 * Created on September 7, 2005, 12:54 PM
 *
 */

package servlets.dataViews.records;

import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

/**
 *
 * @author khoran
 */
public class DebugRecordVisitor implements RecordVisitor
{
    
    private static Logger log=Logger.getLogger(DebugRecordVisitor.class);
    private int depth=0;
    private String space="   ";
    
    /** Creates a new instance of DebugRecordVisitor */
    public DebugRecordVisitor()
    {
    }

    private void indent(Writer out) throws IOException
    {
        for(int i=0;i<depth;i++)
            out.write(space);
    }
    private void printChildren(Writer out, Record r) throws IOException
    {
        Record child;
        
        depth++;        
        for(Iterator i=r.iterator();i.hasNext();)
        {
            child=(Record)i.next();
            child.printHeader(out,this);
            child.printRecord(out,this);
            child.printFooter(out,this);
        }
        depth--;
    }

    public void printHeader(java.io.Writer out, AffyCompRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+ar.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, AffyDetailRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+ar.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, AffyExpSetRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+ar.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+br.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+cr.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, CompositeRecord cr) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+cr.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+eur.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+gr.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+pr.getClass()+"\n");
    }
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        indent(out);
        out.write("header for "+ur.getClass()+"\n");
    }

    
    
    
    
    
    public void printRecord(java.io.Writer out, AffyCompRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write(ar.getClass()+": "+ar.getPrimaryKey()+", "+ar.comparison+"\n");
        
        printChildren(out,ar);
    }
    public void printRecord(java.io.Writer out, AffyDetailRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write(ar.getClass()+": "+ar.getPrimaryKey()+", "+ar.celFile+"\n");
        printChildren(out,ar);
    }
    public void printRecord(java.io.Writer out, AffyExpSetRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write(ar.getClass()+": "+ar.getPrimaryKey()+", "+ar.expSetKey+"\n");
        printChildren(out,ar);
    }
    public void printRecord(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        indent(out);
        out.write(br.getClass()+": "+br.getPrimaryKey()+", "+br.target+"\n");
        printChildren(out,br);
    }
    public void printRecord(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        indent(out);
        out.write(cr.getClass()+": "+cr.getPrimaryKey()+", "+cr.key+"\n");
        printChildren(out,cr);
    }
    public void printRecord(java.io.Writer out, CompositeRecord cr) throws java.io.IOException
    {
        indent(out);
        out.write(cr.getClass()+": "+cr.getPrimaryKey()+"\n");
        printChildren(out,cr);
        //or
        //cr.getFormat().printRecords(out,this,cr.iterator());
    }
    public void printRecord(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        indent(out);
        out.write(eur.getClass()+": "+eur.getPrimaryKey()+", "+eur.source+"\n");
        printChildren(out,eur);
    }
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
        indent(out);
        out.write(gr.getClass()+": "+gr.getPrimaryKey()+", "+gr.go_number+"\n");
        printChildren(out,gr);
    }
    public void printRecord(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        indent(out);
        out.write(pr.getClass()+": "+pr.getPrimaryKey()+", "+pr.ip+"\n");
        printChildren(out,pr);
    }
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        indent(out);
        out.write(ur.getClass()+": "+ur.getPrimaryKey()+", "+ur.key+"\n");
        printChildren(out,ur);
    }
    
    
    
    
    public void printFooter(java.io.Writer out, AffyCompRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+ar.getClass()+"\n");
    }
    public void printFooter(java.io.Writer out, AffyDetailRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+ar.getClass()+"\n");
    }
    public void printFooter(java.io.Writer out, AffyExpSetRecord ar) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+ar.getClass()+"\n");
    }
    public void printFooter(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+cr.getClass()+"\n");
    }
    public void printFooter(java.io.Writer out, CompositeRecord cr) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+cr.getClass()+"\n");
    }
    public void printFooter(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+eur.getClass()+"\n");
    }
    public void printFooter(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        indent(out);
        out.write("footer for "+ur.getClass()+"\n");
    }

    public void printFooter(Writer out, ProbeSetRecord psr) throws IOException
    {
    }

    public void printHeader(Writer out, ProbeSetRecord psr) throws IOException
    {
    }

    public void printRecord(Writer out, ProbeSetRecord psr) throws IOException
    {
    }

    public void printFooter(Writer out, CorrelationRecord cr) throws IOException
    {
    }

    public void printHeader(Writer out, CorrelationRecord cr) throws IOException
    {
    }

    public void printRecord(Writer out, CorrelationRecord cr) throws IOException
    {
    }
}
