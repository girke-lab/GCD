/*
 * Query.java
 *
 * Created on January 26, 2005, 8:31 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */

import java.util.*;
import servlets.advancedSearch.visitors.QueryTreeVisitor;

public class Query extends QueryTreeNode
{
    private Expression condition;
    private List fields; //list of field names
    private List from;  //list of table names
    private Order order; 
    private Integer limit;
    
    /** Creates a new instance of Query */
    public Query(Expression c,List fields,List from,Order o,Integer limit)
    {
        condition=c;
        this.fields=fields;
        this.from=from;
        order=o;
        this.limit=limit;
        
        if(c==null)
            log.warn("no condition");
        if(o==null)
            log.warn("no order");
        if(limit==null)
            log.warn("no limit");
    }

    public String toString(String indent)
    {
        return  indent+"fields: "+fields+"\n"+
                indent+"from: "+from+"\n"+
                indent+"condition: \n"+condition.toString(indent+space)+
                indent+"order: \n"+order.toString(indent+space)+
                indent+"limit: "+limit+"\n";
    }
    
    public Expression getCondition()
    {
        return condition;
    }

    public List getFields()
    {
        return fields;
    }

    public List getFrom()
    {
        return from;
    }

    public Order getOrder()
    {
        return order;
    }
    public Integer getLimit()
    {
        return limit;        
    }
    public void setLimit(Integer l)
    {
        limit=l;
    }
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
