/*
 * Record.java
 *
 * Created on October 13, 2004, 9:28 AM
 */

package servlets.dataViews.dataSource.structure;

import java.util.Set;
import servlets.KeyTypeUser;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.exceptions.InvalidPatternException;

/**
 *
 * @author  khoran
 */



/**
 * This interface is used to store information that can be printed in a formatted
 * way.  An external class is used to do the acutal printing, so a given set of
 * records can be printed in many different ways.
 *
 *  The getSupportedKeyTypes method here is interpreted as returning 
 *  a list of keys that this record can be linked with. i.e., possable
 *  parents of this record are those whose primaryKeyType is one of
 *  these supported keys. 
 *
 *  If this record is not intended to have any parents, the 
 *  getSupportedKeyTypes method can return an empty array. 
 */
public interface Record extends Iterable<Record>, KeyTypeUser
{
        /**
         * Called first for each record, should print a title or something.
         * @param out Writer for output
         * @param visitor should be used to do actual printing
         * @throws java.io.IOException Thrown if output fails
         */
        @Deprecated public void printHeader(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        /**
         * should print the acutal data
         * @param out Writer for output
         * @param visitor should be used to do actual printing
         * @throws java.io.IOException Thrown if output fails
         */
        @Deprecated public void printRecord(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
        /**
         * called last for each record, could be used to close a table or something.
         * @param out Writer for output
         * @param visitor should be used to do actual printing
         * @throws java.io.IOException Thrown if output fails
         */
        @Deprecated public void printFooter(java.io.Writer out,RecordVisitor visitor) throws java.io.IOException;
                
        public void addChildRecord(Record r);
        
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
        public KeyType getPrimaryKeyType();
        
        public void setParent(Record parent);
        public Record getParent();
        
        /** Set a pattern representing the tree from this point down.
         *  This should be updated whenever a new child type is 
         *  added to this record. This way we can tell what types
         *  should be present, even if some are currently missing.
         */
        public void setPattern(RecordPattern pattern) throws InvalidPatternException;
        /* returns the pattern representing this subtree
         */
        public RecordPattern getPattern();
        
        /** provides a unique list of child types.
         * Each type defines a group, which can be retrieved
         * with the childGroup method
         */
        public Set<Class> getGroupList();
        /** returns an iterator over all children, regardless
         * of type.
         */
        public Iterable<Record> allChildren();
        /** returns an iterator over children of the given type.
         */
        public Iterable<Record> childGroup(Class c);
}
