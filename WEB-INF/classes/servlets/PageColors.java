/*
 * WebPageColors.java
 *
 * Created on August 18, 2005, 2:37 PM
 *
 */

package servlets;

import java.awt.Color;
import java.io.IOException;
import java.util.*;
import java.io.Writer;
import org.apache.log4j.Logger;


/**
 * stores colors use by other parts of the 
 * web page.
 * @author khoran
 */
public abstract class PageColors
{
    private static final Logger log=Logger.getLogger(PageColors.class);

    /**
     * This color should be used for all title lines in tables.
     */
    public static WebColor title = new WebColor("AAAAAA");
    
    /**
     * This color should be used for data rows in tables.
     */
    public static WebColor data=new WebColor("D3D3D3");
    
    /**
     * A map of name-color associations. The key is the catagory name, which
     * should be the same name stored in the database. The value is a WebColor
     * object which contains the color value in hex.
     */
    public static Map<String,WebColor> catagoryColors=new HashMap<String,WebColor>(); 
    
    static {
        catagoryColors.put("Abiotic Stress", new WebColor("D5E9F8"));        
        catagoryColors.put("Biotic Stress", new WebColor("EFEEC3"));
        //catagoryColors.put("Abiotic treatment", new WebColor("D5E9F8"));        
        //catagoryColors.put("Biotic treatment", new WebColor("EFEEC3"));
        catagoryColors.put("Chemical Treatment", new WebColor("d5f8ee"));
        catagoryColors.put("Development", new WebColor("B3DCD2"));
        catagoryColors.put("Genotype", new WebColor("d1ccf8"));
        catagoryColors.put("Hormone Treatment", new WebColor("f8e1cb"));
    }
            
    /**
     * Prints a legend in html which shows the mapping between catagory names
     * and colors.
     * @param out The output stream to write the html to
     */
    public static void printColorKey(Writer out)
    {
        try {
            out.write("<table cellspacing='0' cellpadding='3'><tr>\n");
            out.write("<td nowrap >Experiment set catagories: &nbsp&nbsp</td>\n");
            for(Map.Entry<String,WebColor> r : PageColors.catagoryColors.entrySet())
                out.write("<td nowrap bgcolor='"+r.getValue()+"'>"+r.getKey()+"</td>\n");        
            out.write("</tr></table>\n");
        } catch (IOException ex){
            log.warn("io error printing catagory color key: "+ex);
        }
    }
}
 