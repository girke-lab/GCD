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
import servlets.advancedSearch.SearchableDatabase;
import servlets.advancedSearch.fields.Field;
import servlets.advancedSearch.queryTree.*;
import org.apache.log4j.Logger;
import servlets.PageColors;


/**
 * This class traverses an AST and prints out the HTML required to
 * display the search page.  It also tries to be somewhat intelligent
 * abount not printing a million parinthasies.  
 */
public class HtmlVisitor implements QueryTreeVisitor
{
     private static Logger log=Logger.getLogger(HtmlVisitor.class);
     private PrintWriter out;
     private SearchableDatabase db;
     private String[] dbs=null;
     private String currentDatabase;
     private int lastFieldUsedIndex,depth,fieldId,startParIndx,endParIndx;
     private boolean wasJoinExpression,printParinths,hasSubAdd;
     
    /**
     * Creates a new instance of HtmlVisitor
     * @param out output stream
     * @param db SearchableDatabase to get field info from
     */
    public HtmlVisitor(PrintWriter out,SearchableDatabase db)
    {
        this.out=out;
        this.db=db;
        printParinths=true;
        hasSubAdd=false;
    }
    /**
     * Takes a list of database names to display on webpage, sets the 
     * currently selected one. 
     * @param dbs list of db names
     * @param db name of selected db.
     */
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
            startParIndx++;
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
                out.println("</tr><tr bgcolor='"+PageColors.title+"'><td colspan='4'>");
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
            //printOptionList(db.getOperators(),n.getOperation().toUpperCase());
            if(lastFieldUsedIndex < 0 || lastFieldUsedIndex >= db.getFields().length)
            {
                log.error("lastFieldUsed value is out of range: "+lastFieldUsedIndex);
                log.error("trying to print operation: "+n);
            }
            else
                printOptionList(db.getFields()[lastFieldUsedIndex].getValidOps(),n.getOperation().toUpperCase());
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
                    " onClick=\" action.value='add_exp'; " +
                    " epi.value='"+endParIndx+"';" +
                    " row.value='"+(fieldId-1)+"';" +
                    " submit();\">");
            out.println("</td><td>");
            out.println("<input type=submit name='add_sub_exp' value='add sub expression' " +
                    " onClick=\"row.value='"+(fieldId-1)+"';action.value='add_sub_exp';" +
                    " epi.value='"+endParIndx+"';submit();\">");
            out.println("</tr>");
            hasSubAdd=true;
            endParIndx++;
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
            if(!db.getFields()[i].isSortable()) //only print sortable fields
                continue;
            out.println("<option value='"+i+"'");
            log.debug(n.getOrder().getClass()+", name="+((DbField)n.getOrder()).getName()+
                    ", fields name="+db.getFields()[i].dbName);
            if(n.getOrder() instanceof DbField && ((DbField)n.getOrder()).getName().indexOf(db.getFields()[i].dbName)!=-1)
                out.println("selected");
            out.println(">");
            out.println(db.getFields()[i].displayName);
            out.println("</option>");
        }                                
        out.println("</td>");
    }

    /**
     * This method is the entry point of this visitor. Calling
     * it with a valid Query object will result in HTML being printed
     * to the output stream given in the constructor.
     */
    public void visit(servlets.advancedSearch.queryTree.Query n)
    {
        //log.debug("rendering tree: "+n);
        depth=0;
        fieldId=0;
        startParIndx=endParIndx=0;
        
        out.println("\n<form method='get' name='search_form'  >");
        out.println("<table border='0' align='center' bgcolor='"+PageColors.data+"'>");
        out.println("<input type=hidden name='row'>");
        out.println("<input type=hidden name='action'>");
        out.println("<input type=hidden name='epi'>");
        
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
        StringBuffer values=new StringBuffer();
        Object o;
        for(Iterator i=n.getValues().iterator();i.hasNext();)
        {
            o=i.next();
            if(o instanceof IntLiteralValue)
                values.append(((IntLiteralValue)o).getValue().toString());
            else if(o instanceof StringLiteralValue)
                values.append(((StringLiteralValue)o).getValue());
            else if(o instanceof BooleanLiteralValue)
                values.append(((BooleanLiteralValue)o).getValue().toString());
            else if(o instanceof FloatLiteralValue)
                values.append(((FloatLiteralValue)o).getValue().toString());
            else
                values.append(i.next());
            values.append(" ");
        }
        printLiteral(values.toString());    
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
