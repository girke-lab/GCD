/*
 * QueryTreeVisitor.java
 *
 * Created on January 26, 2005, 8:28 AM
 */

package servlets.advancedSearch.visitors;

/**
 *
 * @author khoran
 */
import servlets.advancedSearch.queryTree.*;

/**
 * This is a visitor object for traversing the AST classes in 
 * the queryTree package.  
 */
public interface QueryTreeVisitor
{
    
   public void visit(QueryTreeNode n);
   public void visit(Query n); 
   public void visit(Order n); 
   //public void visit(Expression n); 
   public void visit(Not n);    
   public void visit(Operation n); 
   public void visit(DbField n); 
   //public void visit(LiteralValue n); 
   public void visit(IntLiteralValue n); 
   public void visit(StringLiteralValue n); 
   public void visit(ListLiteralValue n); 
   public void visit(BooleanLiteralValue n);
   public void visit(FloatLiteralValue n);
   
   //public void visit(); 
}
