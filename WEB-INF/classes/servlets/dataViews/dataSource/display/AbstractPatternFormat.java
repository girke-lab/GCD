/*
 * AbstractPatternFormat.java
 *
 * Created on January 3, 2007, 3:37 PM
 *
 */

package servlets.dataViews.dataSource.display;

import java.io.Writer;
import servlets.dataViews.dataSource.structure.Record;

/**
 * This is an abstract partial implementation of a PatternFormat.
 * It manages getting and setting of the DisplayParameters object.
 * It will also pull the Writer out of the parameters object and store
 * it in a variabl called 'out'. This makes it easier to sub-classes
 * to access.
 *
 *  It also implements the preProcess method, which does nothing.
 * @author khoran
 */
public abstract class AbstractPatternFormat<T extends Record> implements PatternFormat<T>
{
    
    // these are variables which are the same for all recoreds, but
    // can change between requests.
    private DisplayParameters parameters=null;
    
    protected Writer out=null;
    
    public void setParameters(DisplayParameters parameters)
    {
        this.parameters=parameters;
        out=parameters.getWriter();
    }
    public DisplayParameters getParameters()
    {
        return parameters;
    }
    
    public String toString()
    {
        return getPattern().toString();
    }

    public void preProcess(Iterable<T> records)
    {
    }
}
