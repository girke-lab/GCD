/*
 * WebPageColors.java
 *
 * Created on August 18, 2005, 2:37 PM
 *
 */

package servlets;

import java.awt.Color;
import java.util.*;

/**
 * stores colors use by other parts of the 
 * web page.
 * @author khoran
 */
public abstract class PageColors
{
    public static WebColor 
            title=new WebColor("AAAAAA"),
            data=new WebColor("D3D3D3")
            ;
    
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
            
}
 