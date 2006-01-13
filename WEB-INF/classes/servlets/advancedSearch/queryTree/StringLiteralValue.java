/*
 * StringLiteralValue.java
 *
 * Created on January 26, 2005, 9:16 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class StringLiteralValue extends LiteralValue
{
    String value;
    
    /** Creates a new instance of StringLiteralValue */
    public StringLiteralValue(String s)
    {
        value=s;
    }

    public String toString(String indent)
    {
        return indent+"string value="+value+"\n";
    }
    public Class getType()
    {
        return String.class;
    }
    
    public String getValue()
    {
        return value;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
    
}
