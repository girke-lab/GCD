/*
 * RecordVisitor.java
 *
 * Created on October 19, 2004, 1:50 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */
public interface RecordVisitor
{
    public void printHeader(java.io.Writer out,UnknownRecord ur) throws java.io.IOException;    
    public void printRecord(java.io.Writer out,UnknownRecord ur) throws java.io.IOException;
    public void printFooter(java.io.Writer out,UnknownRecord ur) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out,BlastRecord br) throws java.io.IOException;
    public void printRecord(java.io.Writer out,BlastRecord br) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out,GoRecord gr) throws java.io.IOException;
    public void printRecord(java.io.Writer out,GoRecord gr) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out, ClusterRecord cr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ClusterRecord cr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ClusterRecord cr) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException;
    
    //public void printHeader(java.io.Writer out) throws java.io.IOException;
    //public void printRecord(java.io.Writer out) throws java.io.IOException;
    //public void printFooter(java.io.Writer out) throws java.io.IOException;
}
