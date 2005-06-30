/*
 * UnsupportedKeyType.java
 *
 * Created on June 16, 2005, 3:27 PM
 *
 */

package servlets.exceptions;

/**
 * This excpetion is thrown when a class is told to use a keytype 
 * that it does not support.
 * @author khoran
 */
public class UnsupportedKeyType extends java.lang.Exception
{
    
    /**
     * Creates a new instance of <code>UnsupportedKeyType</code> without detail message.
     */
    public UnsupportedKeyType()
    {
    }
    
    
    /**
     * Constructs an instance of <code>UnsupportedKeyType</code> with the specified detail message.
     * @param msg the detail message.
     */
    public UnsupportedKeyType(String msg)
    {
        super(msg);
    }
    public UnsupportedKeyType(int[] supportedKeys, int givenKey)
    {
        super(buildMessage(supportedKeys, givenKey));
        
    }
    private static String buildMessage(int[] keys,int givenKey)
    {
        StringBuffer keyList=new StringBuffer();
        for(int i=0;i<keys.length;i++)
            keyList.append(keys[i]+",");
        return "Unsuported key: "+givenKey+", supported keys are: "+keyList;
    }
}
