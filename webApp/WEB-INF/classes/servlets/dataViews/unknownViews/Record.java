/*
 * Record.java
 *
 * Created on October 13, 2004, 9:28 AM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.DbConnection;

public interface Record
{
        public void printHeader(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        public void printRecord(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        public void printFooter(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        
        //public Map getData(DbConnection dbc, List ids,String sortCol,String sortDir);
        //public Map getData(DbConnection dbc, List ids);
}
