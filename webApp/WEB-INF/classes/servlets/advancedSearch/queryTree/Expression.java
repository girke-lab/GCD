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

/**
 * This is a base class for all the differnt types of expressions.  
 * Each subclass should implement the getType class so we can 
 * tell what its type is ( for quoting purposes). 
 */
public abstract class Expression extends QueryTreeNode
{    
    
    /** Creates a new instance of Expression */
    public Expression()
    {
    }
    
    abstract public Class getType();
}
