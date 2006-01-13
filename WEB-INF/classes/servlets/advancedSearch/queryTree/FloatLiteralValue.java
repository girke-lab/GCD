/*
 * FloatLiteralValue.java
 *
 * Created on February 1, 2005, 10:57 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */

import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class FloatLiteralValue extends LiteralValue
{
    Float value;
    
    /** Creates a new instance of FloatLiteralValue */
    public FloatLiteralValue(Float f)
    {
        value=f;
    }
    public FloatLiteralValue(float f)
    {
        value=new Float(f);
    }
    
    public String toString(String indent)
    {
        return indent+"float value: "+value+"\n";        
    }
    public Float getValue()
    {
        return value;
    }
    public Class getType()
    {
        return Float.class;
    }
    public void accept(QueryTreeVisitor v)
    {
        v.visit(this);
    }
}
