/*
 * GoNode.java
 *
 * Created on January 28, 2004, 2:33 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import java.util.*;

public class GoNode
{
    ArrayList parents;
    ArrayList children;
    
    int goNumber;
    String text;
    
    
    /** Creates a new instance of GoNode */
    public GoNode(int num,String t) 
    {
        parents=new ArrayList();
        children=new ArrayList();
        goNumber=num;
        text=t;
    }
    
    public void addParent(ParentLink n)
    {
        parents.add(n);
    }
    public void addChild(GoNode n)
    {
        children.add(n);
    }
    
}
