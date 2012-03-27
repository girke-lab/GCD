/*
 * DbField.java
 *
 * Created on January 26, 2005, 8:35 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class DbField extends Expression
{
    private String name;
    private Class type;
    
    /** Creates a new instance of DbField */
    public DbField(String n,Class t)
    {
        name=n;
        type=t;
    }

    public String toString(String indent)
    {
        return indent+"field name: "+name+"\n"+
                indent+"type: "+type+"\n";
    }
    public Class getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
    
}
