/*
 * Operation.java
 *
 * Created on January 26, 2005, 8:33 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */

import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class Operation extends Expression
{
    public static final int LEFT  = 0;
    public static final int RIGHT = 1;
    
    private String operation;
    private Expression left;
    private Expression right;
    
    /** Creates a new instance of Operation */
    public Operation(String op,Expression e,int location)
    {
        operation=op;
        left=null;
        right=null;
        if(location==LEFT)
            left=e;
        else if(location==RIGHT)
            right=e;
        else
            log.error("invalid location number: "+location);
    }
    public Operation(String op,Expression l,Expression r)
    {
        operation=op;
        left=l;
        right=r;
    }
    public String toString(String indent)
    {
        return indent+"operation: "+operation+"\n"+
                indent+"left: "+(left==null?"null\n":left.toString(indent+space))+
                indent+"right: "+(right==null?"null\n":right.toString(indent+space));
    }
    
    public Class getType()
    {                
        return Boolean.class;
    }

    public String getOperation()
    {
        return operation;
    }

    public Expression getLeft()
    {
        return left;
    }

    public Expression getRight()
    {
        return right;
    }
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
