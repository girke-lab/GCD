/*
 * Expression.java
 *
 * Created on January 26, 2005, 8:32 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
public abstract class Expression extends QueryTreeNode
{    
    
    /** Creates a new instance of Expression */
    public Expression()
    {
    }
    
    abstract public Class getType();
}
