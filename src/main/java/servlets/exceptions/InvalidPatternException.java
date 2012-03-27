/*
 * InvalidPatternException.java
 *
 * Created on January 22, 2007, 9:49 AM
 *
 */

package servlets.exceptions;

import servlets.dataViews.dataSource.display.RecordPattern;

/**
 *
 * @author khoran
 */
public class InvalidPatternException extends Exception 
{
    
    /** Creates a new instance of InvalidPatternException */    
    public InvalidPatternException(String msg)
    {
        super(msg);
    }
    public InvalidPatternException(String msg, RecordPattern pattern)
    {
        super(msg+"\nInvalid Pattern: "+pattern);
    }
}
