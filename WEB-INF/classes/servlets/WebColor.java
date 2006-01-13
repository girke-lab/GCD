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
 *
 * @author khoran
 */
public class WebColor extends Color
{       
        private static Logger log=Logger.getLogger(WebColor.class);
        
        public WebColor(String hex)
        {            
            super(Integer.parseInt(hex.substring(0,2),16), 
                  Integer.parseInt(hex.substring(2,4),16),
                  Integer.parseInt(hex.substring(4,6),16));                     
        }
        public WebColor(Color c)
        {
            super(c.getRed(),c.getGreen(),c.getBlue());
        }
        public String toHex()
        {
            return Integer.toHexString(getRed())+
                   Integer.toHexString(getGreen())+
                   Integer.toHexString(getBlue());
        }
        public String toString()
        {            
            return toHex();
        }
    }