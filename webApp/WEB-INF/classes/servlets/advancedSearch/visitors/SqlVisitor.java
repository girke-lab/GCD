/*
 * SqlVisitor.java
 *
 * Created on January 26, 2005, 9:42 AM
 */

package servlets.advancedSearch.visitors;

/**
 *
 * @author khoran
 */

import java.util.*;
import servlets.advancedSearch.queryTree.*;
import org.apache.log4j.Logger;

public class SqlVisitor implements QueryTreeVisitor
{
    private static Logger log=Logger.getLogger(SqlVisitor.class);
    StringBuffer sql;
    
    
    /** Creates a new instance of SqlVisitor */
    public SqlVisitor()
    {
        log.debug("built new SqlVisitor");
        sql=new StringBuffer();
    }
    public String getSql(Query q)
    {
        q.accept(this);
        //log.debug("sql="+sql);
        return sql.toString();
    }

    public void visit(servlets.advancedSearch.queryTree.DbField n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append(n.getName());            
    }

    public void visit(servlets.advancedSearch.queryTree.IntLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append(n.getValue());
    }

    public void visit(servlets.advancedSearch.queryTree.ListLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append("(");
        for(Iterator i=n.getValues().iterator();i.hasNext();)
        {
            sql.append(i.next());
            if(i.hasNext())
                sql.append(",");
        }
        sql.append(")");
    }

    public void visit(servlets.advancedSearch.queryTree.Not n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append("NOT (");
        n.getExpression().accept(this); //render the negated expression
        sql.append(")");
    }

    public void visit(servlets.advancedSearch.queryTree.Operation n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append("(");
        if(n.getLeft()!=null)
            n.getLeft().accept(this);
        sql.append(" "+n.getOperation()+" ");
        if(n.getRight()!=null) //could be an unary operator
            n.getRight().accept(this);
        sql.append(")");
    }

    public void visit(servlets.advancedSearch.queryTree.Order n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append("\nORDER BY ");
        n.getOrder().accept(this);
        sql.append(" "+n.getDirection());
    }

    public void visit(servlets.advancedSearch.queryTree.Query n)
    { 
        log.debug("visiting "+n.getClass().getName());
        sql.append("SELECT ");
        
        log.debug("adding fields");
        //add field list
        for(Iterator i=n.getFields().iterator();i.hasNext();)
        {
            sql.append(i.next());
            if(i.hasNext())
                sql.append(",");
        }
        
        sql.append("\nFROM ");
        log.debug("adding tables");
        //add table list
        for(Iterator i=n.getFrom().iterator();i.hasNext();)
        {
            sql.append(i.next());
            if(i.hasNext())
                sql.append(",");
        }
        
        sql.append("\nWHERE ");
        log.debug("adding condtions");
        n.getCondition().accept(this); //add condtion to sql string
        log.debug("adding order");
        n.getOrder().accept(this);
        log.debug("adding limit");
        sql.append("\nLIMIT "+n.getLimit());        
    }

    public void visit(servlets.advancedSearch.queryTree.QueryTreeNode n)
    { //no op
        log.debug("visiting "+n.getClass().getName());
    }

    public void visit(servlets.advancedSearch.queryTree.StringLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append("'"+n.getValue()+"'");
    }

    public void visit(BooleanLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());        
        if(n.getValue().booleanValue())
            sql.append("TRUE");
        else
            sql.append("FALSE");
    }

    public void visit(FloatLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());
        log.debug("float value is "+n.getValue());
        sql.append(n.getValue());
    }
    
}
