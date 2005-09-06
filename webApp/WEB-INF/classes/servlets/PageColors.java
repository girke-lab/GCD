/*
 * WebPageColors.java
 *
 * Created on August 18, 2005, 2:37 PM
 *
 */

package servlets;

import java.awt.Color;

/**
 * stores colors use by other parts of the 
 * web page.
 * @author khoran
 */
public interface PageColors
{
    public static WebColor 
            title=new WebColor("AAAAAA"),
            data=new WebColor("D3D3D3"),
            
            // catagory colors (for affy data)
            development=new WebColor("B3DCD2"),
            biotic=new WebColor("EFEEC3"),
            abiotic=new WebColor("D5E9F8")
            ;
    
            
}
 