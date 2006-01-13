/*
 * Not.java
 *
 * Created on January 26, 2005, 8:34 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class Not extends Expression
{
    Expression expression;
    /** Creates a new instance of Not */
    public Not(Expression e)
    {
        expression=e;
    }

    public String toString(String indent)
    {
        return indent+"not expression: "+expression.toString(indent+space);
    }
    public Class getType()
    {
        return Boolean.class;
    }
    public Expression getExpression()
    {
        return expression;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
