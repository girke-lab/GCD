/*
 * SelectGo.java
 *
 * Created on January 28, 2004, 2:32 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */
public class GoGetter 
{
    
    /** Creates a new instance of SelectGo */
    public GoGetter() 
    {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
   /* input: xmlfile to build the DAG with, and a list of keys to select Go numbers for.
    *OR, we could just connect directly to the db and read the key and go numbers directly
    *
    *output: a list of keys associated with just one GO number. OR, store the go number in another
    *table.
         */
        if(args.length!=1)
        {
            System.out.println("USAGE: GoGetter xmlfile");
            return;
        }
        
        
    }
    
}
