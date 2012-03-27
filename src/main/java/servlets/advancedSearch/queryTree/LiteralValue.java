/*
 * LiteralValue.java
 *
 * Created on January 26, 2005, 8:34 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */

/**
 * This is a base class for expressions that store an actual
 * value, rather than a variable of field name.  It is just
 * used for identification purposes.  
 */
public abstract class LiteralValue extends Expression
{
    
    /** Creates a new instance of LiteralValue */
    public LiteralValue()
    {
    }
    
}
