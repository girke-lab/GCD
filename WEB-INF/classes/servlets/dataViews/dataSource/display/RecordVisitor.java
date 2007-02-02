/*
 * RecordVisitor.java
 *
 * Created on October 19, 2004, 1:50 PM
 */

package servlets.dataViews.dataSource.display;

import servlets.dataViews.dataSource.records.AffyCompRecord;
import servlets.dataViews.dataSource.records.AffyDetailRecord;
import servlets.dataViews.dataSource.records.AffyExpDefRecord;
import servlets.dataViews.dataSource.records.AffyExpSetRecord;
import servlets.dataViews.dataSource.records.BlastRecord;
import servlets.dataViews.dataSource.records.ClusterRecord;
import servlets.dataViews.dataSource.records.ComparisonPskRecord;
import servlets.dataViews.dataSource.records.ComparisonRecord;
import servlets.dataViews.dataSource.records.CorrelationRecord;
import servlets.dataViews.dataSource.records.ExternalUnknownRecord;
import servlets.dataViews.dataSource.records.GoRecord;
import servlets.dataViews.dataSource.records.ProbeSetKeyRecord;
import servlets.dataViews.dataSource.records.ProbeSetSummaryRecord;
import servlets.dataViews.dataSource.records.ProteomicsRecord;
import servlets.dataViews.dataSource.records.UnknownRecord;
import servlets.dataViews.dataSource.structure.CompositeRecord;

/**
 * This interface is used to create classes that can print information
 * stored in Record classes.
 * @author khoran
 */
@Deprecated
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

    public void printHeader(java.io.Writer out, ProbeSetSummaryRecord psr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ProbeSetSummaryRecord psr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ProbeSetSummaryRecord psr) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out, CorrelationRecord cr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, CorrelationRecord cr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, CorrelationRecord cr) throws java.io.IOException;
    
    public void printHeader(java.io.Writer out, AffyExpDefRecord ar) throws java.io.IOException;
    public void printRecord(java.io.Writer out, AffyExpDefRecord ar) throws java.io.IOException;
    public void printFooter(java.io.Writer out, AffyExpDefRecord ar) throws java.io.IOException;

    public void printHeader(java.io.Writer out, ComparisonPskRecord cpr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ComparisonPskRecord cpr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ComparisonPskRecord cpr) throws java.io.IOException;

    public void printHeader(java.io.Writer out, ComparisonRecord cr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ComparisonRecord cr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ComparisonRecord cr) throws java.io.IOException;    

    public void printHeader(java.io.Writer out, ProbeSetKeyRecord pskr) throws java.io.IOException;
    public void printRecord(java.io.Writer out, ProbeSetKeyRecord pskr) throws java.io.IOException;
    public void printFooter(java.io.Writer out, ProbeSetKeyRecord pskr) throws java.io.IOException;    

    
    //public void printHeader(java.io.Writer out) throws java.io.IOException;
    //public void printRecord(java.io.Writer out) throws java.io.IOException;
    //public void printFooter(java.io.Writer out) throws java.io.IOException;    
}
