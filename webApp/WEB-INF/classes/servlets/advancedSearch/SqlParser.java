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

public class SqlParser
{
    private static Logger log=Logger.getLogger(SqlParser.class);
    
    /** Creates a new instance of SqlParser */
    public SqlParser()
    {
    }
    
    public static Query parse(String sql) throws ParseException
    {
        InputStream is=new ByteArrayInputStream(sql.getBytes());
        
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
        if(ze instanceof ZQuery)
        {
            log.warn("unsupported subquery found, ignoring it");
            return null;
        }
        else if(ze instanceof ZConstant)
        {
            ZConstant zc=(ZConstant)ze;
            if(zc.getType()==ZConstant.COLUMNNAME)
                return new DbField(zc.getValue(),String.class); //don't really know what class is here
            else if(zc.getType()==ZConstant.NUMBER)
                return new IntLiteralValue(Integer.valueOf(zc.getValue()));
            else if(zc.getType()==ZConstant.STRING)
                return new StringLiteralValue(zc.getValue());
            else
                log.warn("unknown constant type found: "+zc.getType());
            return null;
        }
        else if(ze instanceof ZExpression )
        {
            ZExpression zexp=(ZExpression)ze;
            String op=zexp.getOperator();
            
            if(zexp.nbOperands()==0)
                return null;
            else if(zexp.nbOperands()==1)
                return new Operation(op,buildExpression(zexp.getOperand(0)),Operation.LEFT);
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
            
//            if(zfi.getSchema()!=null && zfi.getTable()!=null && zfi.getColumn()!=null)
//                name=zfi.getSchema()+"."+zfi.getTable()+"."+zfi.getColumn();
//            else if(zfi.getTable()!=null && zfi.getColumn()!=null)
//                name=getTable()+"."+zfi.getColumn();
//            else if(zfi.getColumn()!=null)
//                name=zfi.getColumn();

            
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
