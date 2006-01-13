/*
 * Debug.java
 *
 * Created on February 6, 2004, 11:56 AM
 */

package khoran.debugPrint;

/**
 *
 * @author  khoran
 */
public class Debug
{
    /* for printLevel=0, don't print anything
     */
    
    int printLevel;
    static boolean on=true;  //this allows one to turn off all printing at once
    /** Creates a new instance of Debug */
    public Debug() 
    {
        printLevel=0;
    }
    public Debug(int pl)
    {
        printLevel=pl;
    }
    public void setPrintLevel(int p)
    {
        printLevel=p;
    }
    public void print(String s)
    {//default to printLevel 1
        if(on && printLevel>=1)
            System.out.println(s);
    }
    public void print(int level,String s)
    {
        if(on && printLevel>=level)
            System.out.println(s);
    }
    public static void setPrintStatus(boolean b)
    {
        on=b;
    }
    
}
