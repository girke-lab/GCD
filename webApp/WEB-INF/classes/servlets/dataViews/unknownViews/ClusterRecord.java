/*
 * ClusterRecord.java
 *
 * Created on October 26, 2004, 3:14 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;

public class ClusterRecord implements Record
{
    int size, cutoff;
    List keys=null;
    boolean showClusterCentricView;
    private static Logger log=Logger.getLogger(ClusterRecord.class);
    
    /** Creates a new instance of ClusterRecord */
    public ClusterRecord(int size, int cutoff)
    {//used for key centric view
        this.size=size;
        this.cutoff=cutoff;
        showClusterCentricView=false;
    }
    public ClusterRecord(List values)
    {//used for key centric view
        if(values==null || values.size()!=2)
        {
            log.error("invalid list in ClusterRecord constructor");
            return;
        }
        size=Integer.parseInt((String)values.get(0));
        cutoff=Integer.parseInt((String)values.get(1));
        showClusterCentricView=false;
    }
    public ClusterRecord(int size,int cutoff,List keys)
    {//used for cluster centric view
        this.size=size;
        this.cutoff=cutoff;
        this.keys=keys;
        showClusterCentricView=true;
    }
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ClusterRecord))
            return false;
        ClusterRecord rec=(ClusterRecord)o;        
        return rec.size==size && rec.cutoff==cutoff;
    }
    public int hashCode()
    {
        return new Integer(size+cutoff).hashCode();
    }
    
    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);
    }
    
    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);
    }
    
    public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printFooter(out,this);
    }
    
}
