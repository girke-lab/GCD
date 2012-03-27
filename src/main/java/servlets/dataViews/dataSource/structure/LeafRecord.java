/*
 * LeafRecord.java
 *
 * Created on December 14, 2006, 4:45 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * A Record which cannot have children
 * @author khoran
 */
public abstract class LeafRecord extends AbstractRecord
{
    
    /** Creates a new instance of LeafRecord */
    public LeafRecord()
    {
    }
    
    /** this does nothing
     */
    public void addChildRecord(Record r)
    {
    }

    /** return an empty iterator
     */
    public Iterator<Record> iterator()
    {
        return allChildren().iterator();
    }

    public Iterable<Record> childGroup(Class c)
    {
        return allChildren();
    }

    public Set<Class> getGroupList()
    {
        return new TreeSet<Class>();
    }

    public Iterable<Record> allChildren()
    {
        return new LinkedList<Record>();        
    }

    public void addChildRecordType(Class c)
    { // do nothing.
    }
    
}
