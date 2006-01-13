/*
 * IntLiteralValue.java
 *
 * Created on January 26, 2005, 9:16 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class IntLiteralValue extends LiteralValue
{
    Integer value;
    
    /** Creates a new instance of IntLiteralValue */
    public IntLiteralValue(Integer v)
    {
        value=v;
    }
    public IntLiteralValue(int v)
    {
        value=new Integer(v);
    }

    public String toString(String indent)
    {
        return indent+"int value="+value+"\n";
    }
    public Class getType()
    {
        return Integer.class;
    }
    
    public Integer getValue()
    {
        return value;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
