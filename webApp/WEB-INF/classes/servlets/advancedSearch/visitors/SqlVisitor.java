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
import servlets.Common;
import org.apache.log4j.Logger;

public class SqlVisitor implements QueryTreeVisitor
{
    private static Logger log=Logger.getLogger(SqlVisitor.class);
    StringBuffer sql;
    boolean printParinths,printLimit=true;
    
    /** Creates a new instance of SqlVisitor */
    public SqlVisitor()
    {
        log.debug("built new SqlVisitor");
        sql=new StringBuffer();
        printParinths=true;
    }
    public String getSql(Query q)
    {        
        return getSql(q,true);
    }
    public String getSql(Query q, boolean printLimit)
    {
        this.printLimit=printLimit;
        q.accept(this);
        //log.debug("sql="+sql);
        return sql.toString();
    }

    public void visit(servlets.advancedSearch.queryTree.DbField n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append(n.getName());            
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
        
        //check if one of the expressions is a ListLiteralValue
        //if so, use a differnt function to print this Operation
        if(((n.getLeft() instanceof ListLiteralValue) || (n.getRight() instanceof ListLiteralValue)) &&
                !(n.getOperation().equalsIgnoreCase("IN") || n.getOperation().equalsIgnoreCase("NOT IN")))
        {
            printListOperation(n);
            return;
        }
        
        //store initial value first, as we may change it later
        boolean localPrintParinths=(n.getOperation().equals("and") || 
                n.getOperation().equals("or")) && printParinths;
        if(localPrintParinths)
            sql.append("(");
        
        if(n.getLeft()!=null)
        {
            printParinths=(n.getLeft() instanceof Operation 
                    && !((Operation)n.getLeft()).getOperation().equals(n.getOperation()));
            n.getLeft().accept(this);
        }
        
        sql.append(" "+n.getOperation()+" ");
                
        if(n.getRight()!=null) //could be an unary operator
        {
            printParinths=(n.getRight() instanceof Operation 
                    && !((Operation)n.getRight()).getOperation().equals(n.getOperation()));
            n.getRight().accept(this);
        }
        
        if(localPrintParinths)
            sql.append(")\n");
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
        printDistinct(n);
        
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
        if(printLimit)
            sql.append("\nLIMIT "+n.getLimit()); 
        sql.append(";");
    }

    public void visit(servlets.advancedSearch.queryTree.QueryTreeNode n)
    { //no op
        log.debug("visiting "+n.getClass().getName());
    }
    
    public void visit(servlets.advancedSearch.queryTree.IntLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());
        sql.append(n.getValue());
    }

    public void visit(servlets.advancedSearch.queryTree.ListLiteralValue n)
    {
        log.debug("visiting "+n.getClass().getName());
        if(n.getValues()==null || n.getValues().size()==0)
        {
            sql.append("('')");
            return;
        }
        sql.append("(");
        for(Iterator i=n.getValues().iterator();i.hasNext();)
        {
            //sql.append(i.next());
            ((LiteralValue)i.next()).accept(this);
            if(i.hasNext())
                sql.append(",");
        }
        sql.append(")");
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
        sql.append(n.getValue());        
    }
       ////////////////////////////////////////////////////
    private void printListOperation(Operation n)
    { //need one ListLiteralValue, and one DbField
        
        log.debug("printing an expanded list operation");
        ListLiteralValue llv=null;
        DbField dbf=null;
        if(n.getLeft() instanceof ListLiteralValue)
            llv=(ListLiteralValue)n.getLeft();
        else if(n.getLeft() instanceof DbField)
            dbf=(DbField)n.getLeft();
        
        if(n.getRight() instanceof ListLiteralValue)
            llv=(ListLiteralValue)n.getRight();
        else if(n.getRight() instanceof DbField)
            dbf=(DbField)n.getRight();
        if(llv==null || dbf==null || n.getOperation().equals("IN") ||
                n.getOperation().equals("NOT IN"))
        {
            log.debug("proper conditions not met, doing nothing");
            return;
        }
        if(llv.getValues().size()==0)
        {
            sql.append(dbf.getName()+" "+n.getOperation()+" ''");
            return;
        }
            
        
        sql.append("(");
        for(Iterator i=llv.getValues().iterator();i.hasNext();)
        {
            sql.append(dbf.getName()+" "+n.getOperation()+" ");
            ((LiteralValue)i.next()).accept(this);
            if(i.hasNext())
                sql.append(" OR ");
        }
        sql.append(")");
    }
    private void printDistinct(Query n)
    {
        if(n.isDistinct())
        {
            sql.append("DISTINCT ");
            if(n.getDistinctFields().size()!=0)            
            {
                sql.append("ON (");
                for(Iterator i=n.getDistinctFields().iterator();i.hasNext();)
                {
                    sql.append(i.next());
                    if(i.hasNext())
                        sql.append(",");
                }
                sql.append(")");
            }
        }
    }
}
