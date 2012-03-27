/*
 * MultiChildRecord.java
 *
 * Created on December 14, 2006, 4:45 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.display.RecordPattern;

/**
 * A record that can have serveral different types
 * of children. This is more expensive than the 
 * UniChildRecord implementation.
 *
 * @author khoran
 */
public abstract  class MultiChildRecord extends AbstractRecord
{
    private static final Logger log=Logger.getLogger(MultiChildRecord.class);
    
    private Map<Class,Collection<Record>> subRecordMap;
    private Set<Class> validChildTypes;
    
    /** Creates a new instance of MultiChildRecord */
    public MultiChildRecord()
    {
        subRecordMap=new LinkedHashMap<Class,Collection<Record>>();
        validChildTypes=new HashSet<Class>();
    }
    
    
    public void addChildRecord(Record r)
    {
        if(r==null)
            return;
        
        if(!checkType(r.getClass()))
        {
            log.warn("attempt to add record "+r.getClass().getName()+
                    " to MultiChildRecord with pattern: "+getPattern());
            return;
        }
        
        Collection<Record> list=subRecordMap.get(r.getClass());
        if(list == null)
        {
            list=new LinkedList<Record>();
            subRecordMap.put(r.getClass(),list);
        }
        
        list.add(r);
        r.setParent(this);
    }

    public Iterator<Record> iterator()
    {
        return allChildren().iterator();
    }
       
    public Set<Class> getGroupList()
    {
        return subRecordMap.keySet();
    }
    public Iterable<Record> childGroup(Class c)
    {
        Collection<Record> col=subRecordMap.get(c);
        if(col == null)
        {
            log.info("no class of type "+c.getName()+" found in MultiChildRecord");
            return new LinkedList<Record>();
        }
        return col;
    }   
    public Iterable<Record> allChildren()
    {
        LinkedList<Record> allRecords=new LinkedList<Record>();
        
        for(Collection<Record> c : subRecordMap.values())
            allRecords.addAll(c);
        
        return allRecords;
    }

    /* checks that the given type matches the pattern
     * for this record. This way it is harder to get them out
     * of sync. ( it is still possible of course, since 
     * the pattern cab be replaced at any time). 
     */
    private boolean checkType(Class c)
    {
        log.debug("checking type "+c.getName());
        if(validChildTypes.contains(c))
            return true;
        
        log.debug(" this is a new type");
        
        RecordPattern p=new RecordPattern(this.getClass());
        p.addChild(c);
        log.debug("created pattern "+p+" to test");
        if(p.matches(getPattern()))
        {
            log.debug("pattern matched, type "+c.getName()+" is ok");
            validChildTypes.add(c);
            return true;
        }
        else
        {
            log.debug("match failed. real pattern is: "+getPattern());
            return false;        
        }
    }  
}
