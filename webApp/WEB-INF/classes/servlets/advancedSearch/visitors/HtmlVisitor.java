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
     private String[] dbs=null;
     private String currentDatabase;
     private int lastFieldUsedIndex,depth,fieldId;
     private boolean wasJoinExpression,printParinths,hasSubAdd;
     
    /** Creates a new instance of HtmlVisitor */
    public HtmlVisitor(PrintWriter out,SearchableDatabase db)
    {
        this.out=out;
        this.db=db;
        printParinths=true;
        hasSubAdd=false;
    }
    public void setDatabases(String dbs[],String db)
    {
        if(dbs.length > 1)
            this.dbs=dbs;
        currentDatabase=db;
    } 

    public void visit(servlets.advancedSearch.queryTree.DbField n)
    {        
        out.println("<td>");
        printSpaces(depth);
        out.println("<select name='fields' onChange=\"action.value='refresh'; submit()\">");
        Field[] fields=db.getFields();
        for(int i=0;i<fields.length;i++)
        {
            out.println("<option value='"+i+"'");
            //log.debug("comparing tree name "+n.getName()+" to Field name "+fields[i].dbName+".");
            if(n.getName().equalsIgnoreCase(fields[i].dbName))
            {
                //log.debug("match found");
                out.println(" selected ");
                lastFieldUsedIndex=i;
            }
            out.println(">");
            out.println(fields[i].displayName+"</option>");
        }
        out.println("</select></td>");        
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
         * complications: when to print return, where to put buttons,
         * not printing join conditions.
         */

        //operations between two DbFields are join conditions,
        //and should not be rendered.        
        if(isJoinOperation(n))
        {
            log.debug("join expression found: \n"+n);
            wasJoinExpression=true;
            return;
        }
        boolean isBoolOperation=n.getOperation().equalsIgnoreCase("and") || n.getOperation().equalsIgnoreCase("or");
        boolean localPrintParinths= isBoolOperation && printParinths;
                
        if(localPrintParinths)
        {
            out.println("<input type=hidden name='startPars' value='"+fieldId+"'>");
            out.println("<tr><td colspan='4'>");
            printSpaces(depth);
            out.println("(</td></tr>");
            depth++;
        }        
        
        out.println("<tr>");
        
        wasJoinExpression=false;
        if(n.getLeft()!=null)
        {
            printParinths=(n.getLeft() instanceof Operation 
                    && !((Operation)n.getLeft()).getOperation().equals(n.getOperation()));
            
            n.getLeft().accept(this); //this will set wasJoinExpression if it was a join expression.
            //log.debug("wasJoinExpression="+wasJoinExpression);
        }
            
        
        if(isBoolOperation)
        {
            
            if(!wasJoinExpression)
            {
                out.println("</tr><tr bgcolor='"+Common.titleColor+"'><td colspan='4'>");
                printSpaces(depth);
                out.println("<select name='bools' onChange=\"action.value='refresh'; submit()\">");
                printOptionList(db.getBooleans(),n.getOperation());
                out.println("</select>");
                out.println("</td></tr><tr>");
            }           
        }
        else
        {            
            out.println("<td><select name='ops' onChange=\"action.value='refresh'; submit()\">");
            printOptionList(db.getOperators(),n.getOperation().toUpperCase());
            out.println("</select></td>");         
        }
        
        if(n.getRight()!=null)
        {
            printParinths=(n.getRight() instanceof Operation 
                    && !((Operation)n.getRight()).getOperation().equals(n.getOperation()));
            n.getRight().accept(this);
        }
        if(!isBoolOperation)        
            out.println("<td align='right'><input type=submit name='remove' value='remove'" +
                "   onClick=\"row.value='"+fieldId+"';action.value='remove_exp';submit()\"></td>");
        
        out.println("</tr>");        
        
        
        if(localPrintParinths)
        {
            depth--;            
            out.println("<input type=hidden name='endPars' value='"+(fieldId-1)+"'>");
            out.println("<tr><td colspan='2'>");
            printSpaces(depth);
            out.println(")</td>");
            out.println("<td align='right'>");
            out.println("<input type=submit name='add_exp' value='add'" +
                    " onClick=\"row.value='"+(fieldId-1)+"';action.value='add_exp';submit()\">");
            out.println("</td></tr>");
            hasSubAdd=true;
        }
        if(!isBoolOperation)
            fieldId++;

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
        //log.debug("rendering tree: "+n);
        depth=0;
        fieldId=0;
        
        out.println("\n<form method='post'  >");
        out.println("<table border='0' align='center' bgcolor='"+Common.dataColor+"'>");
        out.println("<input type=hidden name='row'>");
        out.println("<input type=hidden name='action'");
        if(dbs!=null && dbs.length > 1)
        {
            out.println("<tr><td colspan='2'>Database: ");
            out.println("<select name='database' " +
                    "onChange=\"action.value='reset';submit()\">");
            for(int i=0;i<dbs.length;i++)
            {
                out.println("<option ");
                if(dbs[i].equals(currentDatabase))
                    out.println(" selected ");
                out.println(" >"+dbs[i]+"</option>");
            }            
            out.println("</select></td><tr>");
        }
        
        //print condition
        n.getCondition().accept(this);
        log.debug("fieldId="+fieldId);
        //print expression buttons
        //if(fieldId <= 1)
        if(!hasSubAdd)
            out.println("<tr> <td>" +
                "      <input type=submit name='add_exp' value='add expression'" +
                "           onClick=\"action.value='add_exp';submit()\">" +
                "    </td><td>" +
//                "      <input type=submit name='add_sub_exp' value='add sub expression'" +
//                "           onClick=\"action.value='add_sub_exp';submit()\">" +
                "   </td></tr>");        
                
        //print order
        out.println("<tr>");
        n.getOrder().accept(this);
        
        //print limit
        out.println("<td colspan='2'>Limit: <input name='limit' value='"+
                n.getLimit()+"'></td>");
        out.println("</tr>");
        out.println("<tr>" +
                    "    <td colspan='4' align='center'>" +
                    "       <input type=submit name='search' value='Search'" +
                    "           onClick=\"action.value='search';submit()\">" +
                    "    </td>" +
                    "</tr>");   
        out.println("</table>");
    }

    public void visit(servlets.advancedSearch.queryTree.QueryTreeNode n)
    {//no op
    }
    
    public void visit(servlets.advancedSearch.queryTree.IntLiteralValue n)
    {
        printLiteral(n.getValue().toString());    
    }
    public void visit(servlets.advancedSearch.queryTree.ListLiteralValue n)
    {
        printLiteral(n.getValues().toString());    
    }
    public void visit(servlets.advancedSearch.queryTree.StringLiteralValue n)
    {
        printLiteral(n.getValue().toString());    
    }
    public void visit(BooleanLiteralValue n)
    {
        printLiteral(n.getValue().toString());    
    }
    public void visit(FloatLiteralValue n)
    {
        printLiteral(n.getValue().toString());
    }

    
//////////////////////////////////////////////////////////////////////
    private void printLiteral(String value)
    {
        if(lastFieldUsedIndex==-1)
        {
            log.error("no DbField tied to this literal value");
            return;
        }
        Field f=db.getFields()[lastFieldUsedIndex];
        out.println("<td>");
        out.println(f.render(value));
        out.println("</td>");
        lastFieldUsedIndex=-1;
    }
    private void printOptionList(Object[] values,Object value)
    {
        
        for(int i=0;i<values.length;i++)
        {
            out.println("<option value='"+i+"'");
            //log.debug("comparing "+values[i]+" to "+value+(values[i].equals(value)?" match":" no match"));
            if(values[i].equals(value))
                out.println(" selected ");
            out.println(">"+values[i]+"</option>");
        }
    }

    private boolean isJoinOperation(Operation op)
    {
        if(op.getLeft() instanceof DbField && op.getRight() instanceof DbField)
            return true;
        else if(op.getLeft() instanceof DbField && op.getRight() instanceof Operation)
            return isJoinOperation((Operation)op.getRight());
        else if(op.getLeft() instanceof Operation && op.getRight() instanceof DbField)
            return isJoinOperation((Operation)op.getLeft());
        else if(op.getLeft() instanceof Operation && op.getRight() instanceof Operation)
            return isJoinOperation((Operation)op.getLeft()) && isJoinOperation((Operation)op.getRight());
        else 
            return false;
    }
    private void printSpaces(int count)
    {
        int width=8;
        for(int i=0;i<count;i++)
            for(int c=0;c<width;c++)
                out.print("&nbsp");
    }
    
}
