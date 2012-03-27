/*
 * QueryTreeNode.java
 *
 * Created on January 26, 2005, 8:25 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */

import servlets.advancedSearch.visitors.QueryTreeVisitor;
import org.apache.log4j.Logger;

/**
 * This class is the base class of all AST objects. It provides a logger
 * for use in extending classes, and also defines the indent size with
 * the space variable, which should always be prefixed to any printed text.
 */
public class QueryTreeNode 
{
    static Logger log=Logger.getLogger(QueryTreeNode.class);
    final String space="  ";
    
    /** Creates a new instance of QueryTreeNode */
    public QueryTreeNode() 
    {
    }
    
    /**
     * print an indented AST
     * @return 
     */
    public String toString()
    {
        return toString("");
    }
    /**
     * print and indented AST starting at 
     * the given indention level
     * @param indent 
     * @return 
     */
    public String toString(String indent)
    {
        return indent+" bare QueryTreeNode\n";        
    }
    /**
     * visit this object.  Should be overridden by 
     * all subclasses, otherwise this object will be
     * visited rather than the sub ojbect. 
     * @param v 
     */
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
