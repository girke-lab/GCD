/*
 * Record.java
 *
 * Created on October 13, 2004, 9:28 AM
 */

package servlets.dataViews.records;

import servlets.KeyTypeUser;

/**
 *
 * @author  khoran
 */



/**
 * This interface is used to store information that can be printed in a formatted
 * way.  A RecordVisitor is used to do the acutal printing, so a given set of
 * records can be printed in many different ways.
 */
public interface Record extends Iterable, KeyTypeUser
{
        /**
         * Called first for each record, should print a title or something.
         * @param out Writer for output
         * @param visitor should be used to do actual printing
         * @throws java.io.IOException Thrown if output fails
         */
        public void printHeader(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        /**
         * should print the acutal data
         * @param out Writer for output
         * @param visitor should be used to do actual printing
         * @throws java.io.IOException Thrown if output fails
         */
        public void printRecord(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        /**
         * called last for each record, could be used to close a table or something.
         * @param out Writer for output
         * @param visitor should be used to do actual printing
         * @throws java.io.IOException Thrown if output fails
         */
        public void printFooter(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        
        public void addSubRecord(Record r);
        
        public Object getPrimaryKey();
}
