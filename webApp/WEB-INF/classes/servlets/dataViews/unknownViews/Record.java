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
public interface Record
{
        public void printHeader(java.io.PrintWriter out);
        public void printRecord(java.io.PrintWriter out);
        
}
