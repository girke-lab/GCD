/*
 * HtmlVisitor.java
 *
 * Created on January 26, 2005, 9:42 AM
 */

package servlets.advancedSearch.visitors;

/**
 *
 * @author khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;
import servlets.advancedSearch.SearchableDatabase;
import servlets.advancedSearch.Field;
import servlets.advancedSearch.queryTree.*;
import org.apache.log4j.Logger;


public class HtmlVisitor implements QueryTreeVisitor
{
     private static Logger log=Logger.getLogger(HtmlVisitor.class);
     private PrintWriter out;
     private SearchableDatabase db;
     private int lastFieldUsedIndex;
     
    /** Creates a new instance of HtmlVisitor */
    public HtmlVisitor(PrintWriter out,SearchableDatabase db)
    {
        this.out=out;
        this.db=db;
    }

    public void visit(servlets.advancedSearch.queryTree.DbField n)
    {
        out.println("<td><select name='fields' onChange=\"action.value=refresh'; submit()\">");
        Field[] fields=db.getFields();
        for(int i=0;i<fields.length;i++)
        {
            out.println("<option value='"+i+"'");
            if(n.getName().equals(fields[i].dbName))
            {
                out.println(" selected ");
                lastFieldUsedIndex=i;
            }
            out.println(">");
            out.println(fields[i].displayName+"</option>");
        }
        out.println("</select>");
    }

    public void visit(servlets.advancedSearch.queryTree.IntLiteralValue n)
    {
        if(lastFieldUsedIndex==-1)
        {
            log.error("no DbField tied to this literal value");
            return;
        }
        Field f=db.getFields()[lastFieldUsedIndex];
        out.println(f.render(n.getValue().toString()));
        lastFieldUsedIndex=-1;
    }

    public void visit(servlets.advancedSearch.queryTree.ListLiteralValue n)
    {
        if(lastFieldUsedIndex==-1)
        {
            log.error("no DbField tied to this literal value");
            return;
        }
        Field f=db.getFields()[lastFieldUsedIndex];        
        out.println(f.render(n.getValues().toString()));
        lastFieldUsedIndex=-1;
    }

    public void visit(servlets.advancedSearch.queryTree.Not n)
    {
        n.getExpression().accept(this); //do nothing for now, just print the expression
    }

    public void visit(servlets.advancedSearch.queryTree.Operation n)
    {
        /* basic idea:
         * print left
         * print operation
         * print right
         * complications: when to print return, where to put buttons
         */
        out.println("<tr>");
        
        if(n.getLeft()!=null)
            n.getLeft().accept(this);
        
        if(n.getOperation().equals("and") || n.getOperation().equals("or"))
        {
            out.println("</tr><tr bgcolor='"+Common.titleColor+"'><td colspan='4'>");
            out.println("<select name='bools'>");
            printOptionList(db.getBooleans(),n.getOperation());
            out.println("</select>");
            out.println("</td></tr><tr>");
        }
        else
        {
            out.println("<td><select name='ops'>");
            printOptionList(db.getOperators(),n.getOperation());
            out.println("</select></td>");
        }
        
        n.getRight().accept(this);
        
        out.println("</tr>");
        
    }

    public void visit(servlets.advancedSearch.queryTree.Order n)
    {
        out.println("<td colspan='2' align='center'>" +
                "Sort by: <select name='sortField' >");
        for(int i=0;i<db.getFields().length;i++)
        {
            out.println("<option value='"+i+"'");
            if(n.getOrder() instanceof DbField && ((DbField)n.getOrder()).getName()==db.getFields()[i].dbName)
                out.println("selected");
            out.println(">");
            out.println(db.getFields()[i].displayName);
            out.println("</option>");
        }                                
        out.println("</td>");
    }

    public void visit(servlets.advancedSearch.queryTree.Query n)
    {
        out.println("<form method='post' action='as2.jsp' >");
        out.println("<table border='0' align='center' gbcolor='"+Common.dataColor+"'>");
        out.println("<input type=hidden name='row'>");
        out.println("<input type=hidden name='action'");
        
        //print condition
        n.getCondition().accept(this);
        
        //print expression buttons
        out.println("<tr> <td>" +
                "      <input type=submit name='add_exp' value='add expression'>" +
                "    </td><td>" +
                "      <input type=submit name='add_sub_exp' value='add sub expression'>" +
                "   </td></tr>");        
                
        //print order
        out.println("<tr>");
        n.getOrder().accept(this);
        
        //print limit
        out.println("<td> colspan='2'>Limit: <input name='limit' value='"+
                n.getLimit()+"'></td>");
        out.println("</tr>");
        out.println("<tr>" +
                    "    <td colspan='4' align='center'>" +
                    "       <input type=submit name='search' value='Search'>" +
                    "    </td>" +
                    "</tr>");        
    }

    public void visit(servlets.advancedSearch.queryTree.QueryTreeNode n)
    {//no op
    }

    public void visit(servlets.advancedSearch.queryTree.StringLiteralValue n)
    {
        if(lastFieldUsedIndex==-1)
        {
            log.error("no DbField tied to this literal value");
            return;
        }
        Field f=db.getFields()[lastFieldUsedIndex];
        out.println(f.render(n.getValue().toString()));
        lastFieldUsedIndex=-1;
    }
    public void visit(BooleanLiteralValue n)
    {
        if(lastFieldUsedIndex==-1)
        {
            log.error("no DbField tied to this literal value");
            return;
        }
        Field f=db.getFields()[lastFieldUsedIndex];
        out.println(f.render(n.getValue().toString()));
        lastFieldUsedIndex=-1;
    }
    public void visit(FloatLiteralValue n)
    {
        if(lastFieldUsedIndex==-1)
        {
            log.error("no DbField tied to this literal value");
            return;
        }
        Field f=db.getFields()[lastFieldUsedIndex];
        out.println(f.render(n.getValue().toString()));
        lastFieldUsedIndex=-1;
    }
    private void printOptionList(Object[] values,Object value)
    {
        for(int i=0;i<values.length;i++)
        {
            out.println("<option value='"+i+"'");
            if(values[i].equals(value))
                out.println(" selected ");
            out.println(">"+values[i]+"</option>");
        }
    }

    
    
    
}
