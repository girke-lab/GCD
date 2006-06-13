/*
 * UnsupportedKeyTypeException.java
 *
 * Created on June 16, 2005, 3:27 PM
 *
 */

package servlets.exceptions;
import servlets.KeyTypeUser;

/**
 * This excpetion is thrown when a class is told to use a keytype 
 * that it does not support.
 * @author khoran
 */
public class UnsupportedKeyTypeException extends java.lang.Exception
{
    
    /**
     * Creates a new instance of <code>UnsupportedKeyTypeException</code> without detail message.
     */
    public UnsupportedKeyTypeException()
    {
    }
    
    
    /**
     * Constructs an instance of <code>UnsupportedKeyTypeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedKeyTypeException(String msg)
    {
        super(msg);
    }
    public UnsupportedKeyTypeException(KeyTypeUser.KeyType[] supportedKeys, KeyTypeUser.KeyType givenKey)
    {
        super(buildMessage(supportedKeys, givenKey));
        
    }
    private static String buildMessage(KeyTypeUser.KeyType[] keys,KeyTypeUser.KeyType givenKey)
    {
        StringBuffer keyList=new StringBuffer();
        for(int i=0;i<keys.length;i++)
            keyList.append(keys[i]+",");
        return "Unsuported key: "+givenKey+", supported keys are: "+keyList;
    }
}
