/*
 * ParentLink.java
 *
 * Created on January 28, 2004, 2:33 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import java.io.Serializable;

public class ParentLink implements Serializable
{
    public final static int PART_OF=0;
    public final static int IS_A=1;
    private GoNode link;
    private int relation; //either is-a or part-of
    private int depth; //depth of parent of link
    
    /** Creates a new instance of ParentLink */
    public ParentLink(GoNode l,int r,int d) 
    {
        link=l;
        relation=r;
        depth=d;
    }        
    public GoNode getLink()
    {
        return link;
    }
    public int getDepth()
    {
        return depth;
    }
    public int getRealtion()
    {
        return relation;
    }
    
}
