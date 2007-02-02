/*
 * RecordPattern.java
 *
 * Created on December 29, 2006, 10:05 AM
 *
 */

package servlets.dataViews.dataSource.display;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.structure.Record;

/**
 * A RecordPattern defines a tree of Class objects which should
 * represent Record classes. This tree parallels the tree of records
 * created by the RecordFactory.  A RecordPattern matches a section
 * of the record tree if the roots match, and no part of the pattern
 * extends off the record tree, i.e., every sub-pattern must match
 * some child of the root (where the match starts, not the root
 *  of the entire tree) record on the record tree. It does not need
 * to match every record child however.
 *
 * @author khoran
 */
public class RecordPattern implements Comparable<RecordPattern>
{
    Class root;
    Map<Class,RecordPattern> childPatterns;    
    
    private static final Logger log=Logger.getLogger(RecordPattern.class);
    
    /** Creates a new instance of RecordPattern */
    public RecordPattern(Class root)
    {
        this.root=root;
        childPatterns=new HashMap<Class,RecordPattern>();                
    }
    
    public RecordPattern addChild(Class c)
    {
        return addChild(new RecordPattern(c));
    }
    public RecordPattern addChild(RecordPattern rp)
    {
        if(rp != null)
            childPatterns.put(rp.getRoot(),rp);
        else
            log.info("attemt to add a null pattern");
        
        return this;
    }
    public boolean isLeaf()
    {
        return childPatterns.isEmpty();
    }
    /** true if this record and its children match
     * this pattern. This pattern matches a record r 
     * if every sub-pattern matches some sub-record of r.
     * Not every sub-record of r needs to be matched, so 
     * this pattern is kind of a 'sub-set' of r.
     */
    public boolean matches(Record r)
    {
        //log.debug("matching recorcd "+r.getClass().getName()+" to pattern "+this);                        
        if(!root.equals(r.getClass()))
           return false;              
        
        RecordPattern pattern;
        int matchCount=0;
        
        for(Class c : r.getGroupList())
        {
            pattern=childPatterns.get(c); // see if we have a pattern with the same root type
                        
            if(pattern != null) //some record in this group may be a match
            { 
                for(Record subRecord : r.childGroup(c))
                {
                    if(pattern.matches(subRecord))
                    {// we have at least one match, so we're done
                        matchCount++;
                        break;
                    }
                }                                                
            }
        }
        
        //if equal, we matched all child patterns, so we have a match
        return matchCount == childPatterns.size();             
    }
    /* True only if testPattern matches this pattern. A
     * pattern P matches another pattern Q if P is subset of Q.
     *  To see it another way, if the tree of P was layed over top 
     * of Q, no branch of P would extend beyond Q.
     *
     *  This operation is not commutative. Q.matches(P)==P.matches(Q)
     *  iff P==Q.
     */
    public boolean matches(RecordPattern testPattern)
    {
        if(!root.equals(testPattern.getRoot()))
            return false;                
        
        RecordPattern p;
        for(RecordPattern rp : childPatterns.values())
        {            
            p=testPattern.childPatterns.get(rp.getRoot());
            if(p == null || !rp.matches(p)) // no match for this pattern (rp)
                return false;
        }
        return true;
    }

    /* This method returns a list of groups of records which are just 
     * beyond what this pattern covers, when rooted at the given record. They are the records on the fringe
     * of this pattern, but it does not include the entire sub-tree.
     */
    public List<List<Record>> remainingRecordGroups(Record r)
    {
        List<ListDepth> sorted= remainingRecordGroups(r,0);
        Collections.sort(sorted);
        
        List<List<Record>> results=new LinkedList<List<Record>>();
        
        for(ListDepth ld : sorted)
            results.add(ld.getList());
        
        return results;
    }
    private List<ListDepth> remainingRecordGroups(Record r, int depth)
    {
        List<ListDepth> remaining=new LinkedList<ListDepth>();
        
        if(!root.equals(r.getClass()))
        {
            remaining.add(new ListDepth(r,depth));
            return remaining;
        }
        
        RecordPattern rp;
        List<Record> group;
        
        for(Class c : r.getGroupList())
        {
            rp=childPatterns.get(c);
                       
            if(rp == null)
            {   // no pattern covers this group, so add to remaining          
                //log.debug("found remaining group "+c.getName());
                group=new LinkedList<Record>();
                for(Record r2 : r.childGroup(c))
                    group.add(r2);
                remaining.add(new ListDepth(group,depth));
            }
            else
            {  // some pattern covers this record, ask it for the remaining set
                //log.debug("descending into group "+c.getName());
                for(Record r2 : r.childGroup(c))
                    remaining.addAll(rp.remainingRecordGroups(r2 ,depth+1));
            }
        }        
        
        return remaining;
    }
    
  
    
    /** prefer more complex patterns over
     * simpler patterns.
     * Complexity is defined as the total number
     * of sub patterns.
     */
    public int compareTo(RecordPattern o)
    {
        Integer c1=this.subPatternCount();
        Integer c2=o.subPatternCount();
        
        return c1.compareTo(c2);                
    }
    
    private int subPatternCount()
    {
       int sum=childPatterns.size();
       
       for(RecordPattern rp : childPatterns.values())
           sum+=rp.subPatternCount();
       
       return sum;
    }
    /* return the type of the root of this
     * pattern.
     */
    public Class getRoot()
    {
        return root;
    }
    
    
    public String toString()
    {
        StringBuilder sb=new StringBuilder();
        
        sb.append(root.getName()+" (");
        for(RecordPattern rp : childPatterns.values())
            sb.append(rp.toString()+" ");
        sb.append(")");
        
        return sb.toString();
    }

   
    private class ListDepth implements Comparable<ListDepth>
    {
        List<Record> list;
        Integer depth;
        public ListDepth(List<Record> list, Integer depth)
        {
            this.list=list;
            this.depth=depth;
        }
        public ListDepth(Record r, Integer depth)
        {
            this.depth=depth;
            list=new LinkedList<Record>();
            list.add(r);
        }
        public List<Record> getList(){return list;}
        public int compareTo(RecordPattern.ListDepth l)
        {
            return depth.compareTo(l.depth);
        }       
    }    
}
