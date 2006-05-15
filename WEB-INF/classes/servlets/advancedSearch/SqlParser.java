/*
 * SqlParser.java
 *
 * Created on February 7, 2005, 11:47 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author khoran
 */

import servlets.advancedSearch.queryTree.*;
import Zql.*;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;


/**
 * Uses the Zql API to parse sql.  An sql string is parsed
 * into Zql objects, and then converted to another AST composed of
 * objects from the queryTree package.  This way I can use the classes
 * I made, but not have to implement a parser myself.  
 * <P>
 * Zql does not support all sql, in particular, it does no support
 * a LIMIT clause, and it doesn't like postgres's ILIKE keyword. 
 * Because of this, sql strings should not use LIMIT, and all occurences of 
 * ILIKE (case insensitive) will be converted to LIKE while Zql parses it, then
 * all occurences of LIKE will be converted to ILIKE when the second AST is
 * built.  So if you have LIKE in the initial sql, it will become ILIKE in the
 * Query object.  So just use postgreSQL and life will be simple.  
 */
public class SqlParser
{
    private static Logger log=Logger.getLogger(SqlParser.class);
    
    /** Creates a new instance of SqlParser */
    public SqlParser()
    {
    }
    
    public static Query parse(String sql) throws ParseException
    {
        log.debug("input sql: "+sql);
        //sub like for ilike becuase Zql does not like ilike.
        InputStream is=new ByteArrayInputStream(sql.toLowerCase().replaceAll("ilike","like").getBytes());
        
        ZqlParser zp=new ZqlParser(is);
        ZStatement statement=zp.readStatement();
        if(statement==null || !(statement instanceof ZQuery))
        {
            log.error("statement is null or is not a select statement");
            return null;
        }
        Query q=buildQuery((ZQuery)statement);
        return q;
    }
    
    private static Query buildQuery(ZQuery zq)
    {
        Query q=null;
        Expression condition=buildExpression(zq.getWhere());
        List fields=getFields(zq.getSelect());
        List tables=getTables(zq.getFrom());
        Order order=buildOrder(zq.getOrderBy());
        Integer limit=new Integer(100000);
        //need fields, from, condition, order, limit        
        q=new Query(condition,fields,tables,order,limit);
        return q;
    }
    
    private static Expression buildExpression(ZExp ze)
    {
        log.debug("building expression of type "+ze.getClass());
        if(ze instanceof ZQuery)
        {
            log.warn("unsupported subquery found, ignoring it");
            return null;
        }
        else if(ze instanceof ZConstant)
        {
            ZConstant zc=(ZConstant)ze;
            log.debug("value of constant: "+zc.getValue());
            if(zc.getValue().equalsIgnoreCase("TRUE") || zc.getValue().equalsIgnoreCase("FALSE"))
                return new BooleanLiteralValue(new Boolean(zc.getValue()));
            else if(zc.getType()==ZConstant.COLUMNNAME)
                return new DbField(zc.getValue(),String.class); //don't really know what class is here
            else if(zc.getType()==ZConstant.NUMBER)
            {
                if(zc.getValue().indexOf('.')!=-1) //contains a decimal
                    return new FloatLiteralValue(Float.valueOf(zc.getValue()));    
                else
                    return new IntLiteralValue(Integer.valueOf(zc.getValue()));
            }
                
            else if(zc.getType()==ZConstant.STRING)
            {
                log.debug("found string value: "+zc.getValue());
                
                return new StringLiteralValue(zc.getValue());
            }                
            else
                log.warn("unknown constant type found: "+zc.getType());
            return null;
        }
        else if(ze instanceof ZExpression )
        {
            ZExpression zexp=(ZExpression)ze;
            String op=zexp.getOperator();            
            
            log.debug("operation of expression: "+op+", "+zexp.nbOperands()+" operands");
            
            if(op.toLowerCase().indexOf("like")!=-1)
            {
                log.info("subbing 'ILIKE' for operator 'like'");
                op=op.toUpperCase().replaceAll("LIKE","ILIKE");
            }
            
            if(zexp.nbOperands()==0)
                return null;
            else if(zexp.nbOperands()==1)
            {
                Expression e=buildExpression(zexp.getOperand(0));
                
                // zql though this was an expression, but maybe it was just a negative
                // number, so see if we have minus sign and a number.
                if(op.equals("-"))
                    if(e instanceof IntLiteralValue)
                        return new IntLiteralValue(-1*((IntLiteralValue)e).getValue());
                    else if(e instanceof FloatLiteralValue)
                        return new FloatLiteralValue(-1*((FloatLiteralValue)e).getValue());
                    
                return new Operation(op, e ,Operation.LEFT);
            }
            else if(op.equalsIgnoreCase("in") || op.equalsIgnoreCase("not in"))
            { //first vector element is db field name, rest are values in list
                log.debug("building in expression");
                Expression fieldExp=buildExpression(zexp.getOperand(0));
                if(!(fieldExp instanceof DbField))
                {
                    log.error("first operand of an 'in' operation should be a field name, but got a "+fieldExp.getClass());
                    return null;
                }
                //rest of operands should be String, Integer, or Boolean LiteralValues
                Expression exp;
                List list=new LinkedList();
                for(int i=1;i<zexp.nbOperands();i++)
                {
                    exp=buildExpression(zexp.getOperand(i));
                    if(!(exp instanceof LiteralValue))
                    {
                        log.error("non literal value in list: "+exp.getClass());
                        continue;
                    }
                    list.add(exp);
                }
                log.debug("list="+list);
                return new Operation(op,(DbField)fieldExp,new ListLiteralValue(list));
            }
            else
            {
                Expression exp=buildExpression(zexp.getOperand(0));
                for(int i=1;i<zexp.nbOperands();i++)                
                    exp=new Operation(op,exp,buildExpression(zexp.getOperand(i)));                
                return exp;
            }
        }
        log.warn("invalid expression type: "+ze);
        return null;
    }
    private static List getFields(Vector v)
    { //vector of ZSelectItems
      //only supports a list of table names
        
        List fields=new LinkedList();
        ZSelectItem zsi;
        ZExp ze;
        for(Iterator i=v.iterator();i.hasNext();)
        {
            zsi=(ZSelectItem)i.next();
            if(!zsi.isExpression())
            {
                log.warn("expression found in field list, ignoring it");
                continue;
            }
            ze=zsi.getExpression();
            if(ze instanceof ZConstant && ((ZConstant)ze).getType()==ZConstant.COLUMNNAME)            
                fields.add(((ZConstant)ze).getValue());     
            else
                log.warn("non-columnname found in field list, ignoring it");
        }
        return fields;
    }
    private static List getTables(Vector v)
    {//vector of ZFromItems
     //only supports a list of table names
        List tables=new LinkedList();
        ZFromItem zfi;
        
        for(Iterator i=v.iterator();i.hasNext();)
        {
            zfi=(ZFromItem)i.next();
            String name="";
            boolean hadSchema=false,hadTable=false;
            

            if(zfi.getSchema()!=null)
            {
                name=zfi.getSchema();
                hadSchema=true;
            }
            if(zfi.getTable()!=null)
            {
                if(hadSchema)
                    name+=".";
                name+=zfi.getTable();
                hadTable=true;
            }
            if(zfi.getColumn()!=null)
            {
                if(hadTable || hadSchema)
                    name+=".";
                name+=zfi.getColumn();
            }
            tables.add(name);
        }
        return tables;
    }
    private static Order buildOrder(Vector v)
    {//vector of ZOrderBys
        //only supports 1 element here
        if(v.size() < 1)
        {
            log.warn("no order given, returning null");
            return null;
        }
        ZOrderBy zob=(ZOrderBy)v.get(0);
        String dir="ASC";
        if(!zob.getAscOrder())
            dir="DESC";
        Expression e=buildExpression(zob.getExpression());
        return new Order(e,dir);
    }
}
