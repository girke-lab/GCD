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
 *
 *  The getSupportedKeyTypes method here is interpreted as returning 
 *  a list of keys that this record can be linked with. i.e., possable
 *  parents of this record are those whose childKeyType is one of
 *  these supported keys. 
 *
 *  If this record is not intended to have any parents, the 
 *  getSupportedKeyTypes method can return an empty array. 
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
        
        /**
         *  This key is used to lookup subrecords in a Map.
         *  This is ONLY used when adding subrecords.
         *  This key should be unqiue for each instance of the record
         */
        public Object getPrimaryKey();
        
        /**
         *  This should return the type of the primary key of this record.
         *  When adding a subrecord, they must support this key type or 
         *  they cannot be linked to this record.
         */
        public KeyType getChildKeyType();
}
