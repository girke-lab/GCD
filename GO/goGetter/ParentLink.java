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
public class ParentLink 
{
    GoNode link;
    int relation; //either is-a or part-of
    int depth; //depth of parent of link
    
    /** Creates a new instance of ParentLink */
    public ParentLink(GoNode l,int r,int d) 
    {
        link=l;
        relation=r;
        depth=d;
    }
    
}
