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
import java.io.*;
import khoran.debugPrint.Debug;

public class GoNode implements Serializable
{
    transient ArrayList parents;
    ArrayList children;
    
    private int goNumber;
    private String text;
    private int maxDepth; //the length of the longest route from the root to this node
    private transient Debug d;
    
    /** Creates a new instance of GoNode */
    public GoNode(int num,String t) 
    {
        parents=new ArrayList();
        children=new ArrayList();
        d=new Debug();
//        d.setPrintLevel(2);//turn on printing
        goNumber=num;
        text=t;
        maxDepth=0;
    }    
    public void addParent(ParentLink n)
    {
        d.print(2,"parentLink depth="+n.getDepth()+", current maxDepth="+maxDepth);
        if(n.getDepth() > maxDepth)
            maxDepth=n.getDepth();
        d.print("adding parent "+n.getLink().getGoNumber()+" to "+goNumber);
        parents.add(n);
    }
    public void addChild(GoNode n)
    {
        d.print("adding child "+n.getGoNumber()+" to "+goNumber);
        children.add(n);
    }
    public boolean isChildOf(GoNode gn)
    {
        for(Iterator i=parents.iterator();i.hasNext();)
        {
            ParentLink pl=(ParentLink)i.next();
            if(pl.getLink()==gn || pl.getLink().isChildOf(gn))
                return true;
        }
        return false;
    }
    public int getMaxDepth()
    {
        return maxDepth;
    }
    public int getGoNumber()
    {
        return goNumber;
    }
    public String getText()
    {
        return text;
    }
    public boolean hasParent()
    {
        return parents.size()!=0;
    }
    public String toString()
    {
        return toString("");
    }
    public String toString(String in)
    {
        StringBuffer out=new StringBuffer();
        out.append( in+"Node{\n"+in+"   GO:"+goNumber+"\n"+
                    in+"   text: "+text+"\n"+
                    in+"   maxDepth="+maxDepth+"\n"+                    
                    in+"   parent count: "+parents.size()+"\n"+
                    in+"   child count: "+children.size()+"\n"+
                    in+"}\n");
        for(Iterator i=children.iterator();i.hasNext();)
            out.append(((GoNode)i.next()).toString(in+"   "));
        return out.toString();
    }
}
