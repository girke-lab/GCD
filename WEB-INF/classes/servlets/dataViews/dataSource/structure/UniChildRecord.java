/*
 * UniChildRecord.java
 *
 * Created on December 14, 2006, 4:45 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.display.RecordPattern;

/**
 * A record which can only have one type of child,
 * but can have many of them.
 *
 * Only one type of record can be added. This type is determined
 * by the first record inserted. After that all records must
 * be of the same time.
 * @author khoran
 */
public abstract class UniChildRecord extends AbstractRecord
{
    private static final Logger log=Logger.getLogger(UniChildRecord.class);
    
    private Collection<Record> childRecords;
    private Class childRecordType=null;
    
    /** Creates a new instance of UniChildRecord */
    public UniChildRecord()
    {
        childRecords=new LinkedList<Record>();
    }

    public void addChildRecord(Record r)
    {
        if(r==null)
            return;
        
        if(!testChildRecordType(r.getClass()))
        {
            log.warn("attempt to add record "+r.getClass().getName()+
                    " to UniChildRecord with pattern: "+getPattern());
            return;
        }
                    
        childRecords.add(r);
        r.setParent(this);
    }

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
        Set<Class> set=new TreeSet<Class>();
        if(childRecordType != null)
            set.add(childRecordType);
        return set;
    }

    public Iterable<Record> allChildren()
    {
        return childRecords;
    }   
    
    /** checks that any records added match the type 
     * given in the pattern
     */
    private boolean testChildRecordType(Class type)
    {
        if(childRecordType!=null)
            return childRecordType.equals(type);
        
        //otherwise see if this type matches our pattern
        RecordPattern p=new RecordPattern(this.getClass());
        p.addChild(type);
        
        if(getPattern().matches(p))
        {
            childRecordType=type;
            return true;
        }
        else
            return false;        
    }
}
