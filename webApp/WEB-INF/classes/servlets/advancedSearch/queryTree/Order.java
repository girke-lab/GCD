/*
 * Order.java
 *
 * Created on January 26, 2005, 8:38 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
import servlets.advancedSearch.visitors.QueryTreeVisitor;


public class Order extends QueryTreeNode
{
    private Expression order;
    private String direction;
    
    /** Creates a new instance of Order */
    public Order(Expression o,String d)
    {
        order=o;
        direction=d;
    }

    public String toString(String indent)
    {
        return indent+"order: \n"+order.toString(indent+space)+
                indent+"direction: "+direction+"\n";
    }
    public Expression getOrder()
    {
        return order;
    }

    public String getDirection()
    {
        return direction;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
    
}
