/*
 * WebColor.java
 *
 * Created on August 25, 2005, 10:04 AM
 *
 */

package servlets;

import java.awt.Color;
import org.apache.log4j.Logger;

/**
 * A Color object which makes it easy to deal with colors in the format
 * that html likes.
 * @author khoran
 */
public class WebColor extends Color
{       
    private static Logger log=Logger.getLogger(WebColor.class);
        
    /**
     * Creates a new color from a hex string formated as: 'RRGGBB'
     * @param hex a hex formated color string
     */
    public WebColor(String hex)
    {            
        super(Integer.parseInt(hex.substring(0,2),16), 
              Integer.parseInt(hex.substring(2,4),16),
              Integer.parseInt(hex.substring(4,6),16));                     
    }
    /**
     * Create a new WebColor object from another Color object
     * @param c another {@link java.awt.Color} object
     */
    public WebColor(Color c)
    {
        super(c.getRed(),c.getGreen(),c.getBlue());
    }
    /**
     * Print the color of this object in hex format, as follows: 'RRGGBB'
     * @return a hex color string
     */
    public String toHex()
    {
        return Integer.toHexString(getRed())+
               Integer.toHexString(getGreen())+
               Integer.toHexString(getBlue());
    }
    /**
     * Just calls {@link toHex }
     * @return a hex color string
     */
    public String toString()
    {            
        return toHex();
    }
}