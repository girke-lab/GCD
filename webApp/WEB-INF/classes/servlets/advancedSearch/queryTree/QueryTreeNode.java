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

public class QueryTreeNode 
{
    static Logger log=Logger.getLogger(QueryTreeNode.class);
    final String space="  ";
    
    /** Creates a new instance of QueryTreeNode */
    public QueryTreeNode() 
    {
    }
    
    public String toString()
    {
        return toString("");
    }
    public String toString(String indent)
    {
        return indent+" bare QueryTreeNode\n";        
    }
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
