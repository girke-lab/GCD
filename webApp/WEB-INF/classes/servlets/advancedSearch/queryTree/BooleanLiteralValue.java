/*
 * BooleanLiteralValue.java
 *
 * Created on February 1, 2005, 10:31 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */

import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class BooleanLiteralValue extends LiteralValue
{
    Boolean value;
    /** Creates a new instance of BooleanLiteralValue */
    public BooleanLiteralValue(boolean b)
    {
        value=new Boolean(b);
    }
    public BooleanLiteralValue(Boolean b)
    {
        value=b;
    }
    public String toString(String indent)
    {
        return indent+"boolean value="+value+"\n";
    }
    public Class getType()
    {
        return Boolean.class;
    }
    public Boolean getValue()
    {
        return value;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
