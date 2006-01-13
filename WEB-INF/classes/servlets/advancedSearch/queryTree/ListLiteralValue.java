/*
 * ListLiteralValue.java
 *
 * Created on January 26, 2005, 9:17 AM
 */

package servlets.advancedSearch.queryTree;

/**
 *
 * @author khoran
 */
import java.util.*;
import servlets.advancedSearch.visitors.QueryTreeVisitor;


public class ListLiteralValue extends LiteralValue
{//corresponds to a textarea, so users can specifiy a list of values
    
    List values;
    /** Creates a new instance of ListLiteralValue */
    public ListLiteralValue(List l)
    {
        values=l;
    }

    public String toString(String indent)
    {
        return indent+"list values: "+values+"\n";
    }
    public Class getType()
    {//return most specific type available, assume all list 
        //members have the same type.
        if(values==null || values.size()==0)
            return List.class;
        return values.get(0).getClass();
    }
    public List getValues()
    {
        return values;
    }    
    public void accept(QueryTreeVisitor v) 
    { 
        v.visit(this);
    }
}
