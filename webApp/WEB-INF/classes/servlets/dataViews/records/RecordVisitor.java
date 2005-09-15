/*
 * RecordVisitor.java
 *
 * Created on October 19, 2004, 1:50 PM
 */

package servlets.dataViews.records;

/**
 * This interface is used to create classes that can print information
 * stored in Record classes.
 * @author khoran
 */
public interface RecordVisitor
{
    /**
     * Prints the header for a UnknownRecord
     */
    public void printHeader(java.io.Writer out,UnknownRecord ur) throws java.io.IOException;    
    /**
     * Prints the data for a UnknownRecord
     */
    public void printRecord(java.io.Writer out,UnknownRecord ur) throws java.io.IOException;
    /**
     * Prints the footer for a UnknownRecord
     */
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
    
    public void printHeader(java.io.Writer out, AffyDetailRecord ar) throws java.io.IOException;
    public void printRecord(java.io.Writer out, AffyDetailRecord ar) throws java.io.IOException;
    public void printFooter(java.io.Writer out, AffyDetailRecord ar) throws java.io.IOException;

    public void printHeader(java.io.Writer out, AffyCompRecord ar) throws java.io.IOException;
    public void printRecord(java.io.Writer out, AffyCompRecord ar) throws java.io.IOException;
    public void printFooter(java.io.Writer out, AffyCompRecord ar) throws java.io.IOException;

    public void printHeader(java.io.Writer out, AffyExpSetRecord ar) throws java.io.IOException;
    public void printRecord(java.io.Writer out, AffyExpSetRecord ar) throws java.io.IOException;
    public void printFooter(java.io.Writer out, AffyExpSetRecord ar) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out, CompositeRecord cr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, CompositeRecord cr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, CompositeRecord cr) throws java.io.IOException;

    public void printHeader(java.io.Writer out, ProbeSetRecord psr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ProbeSetRecord psr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ProbeSetRecord psr) throws java.io.IOException;
    
    //public void printHeader(java.io.Writer out) throws java.io.IOException;
    //public void printRecord(java.io.Writer out) throws java.io.IOException;
    //public void printFooter(java.io.Writer out) throws java.io.IOException;
}
