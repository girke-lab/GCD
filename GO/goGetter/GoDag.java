/*
 * GoDag.java
 *
 * Created on January 28, 2004, 2:30 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import java.util.*;

public class GoDag 
{
    GoNode root; //first node in GO DAG.
    HashMap index; 
    
    /** Creates a new instance of GoDag */
    public GoDag(String xmlFilename) 
    {//open and parse the xml file
        
    }
    
    public GoNode find(int goNumber)
    {        
        return (GoNode)index.get(new Integer(goNumber));
    }
}
