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

import java.util.ArrayList;

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
    *
    *We take in a list of At numbers, each of which has a list of GO numbers associated with it.  
    *For each At number, we use the GoSelector to select on of these GO numbers.
         */
        if(args.length!=1)
        {
            System.out.println("USAGE: GoGetter xmlfile");
            return;
        }
        //khoran.debugPrint.Debug.setPrintStatus(false);  //turn debug printing on or off
        GoDag dag=new GoDag(args[0]);
        
        System.out.println("Dag:\n"+dag);
        
        /*
        GoSelector selector=new Selector1(dag); //assign an instance of a GoSelector
        GoGetter work=new GoGetter();
        
        //input structure should be a list of AtNumbers.
        ArrayList inputData=work.getInput();        
        for(Iterator i=inputData.iterator();i.hasNext();)
        {
            AtNumber at=((AtNumber)i.next());
            at.selectedGO=selector.getGoNumber(at.goNums);
        }
        
        work.storeOutput(inputData); //write data back to db or file or whatever
         */
    }
    public ArrayList getInput()
    {
        
        return null;
    }
    public void storeOutput(ArrayList data)
    {
        
    }
    
}

class AtNumber
{
    public int atId;
    public int[] goNums;
    public int selectedGO;
    
    public AtNumber(int id,int[] g)
    {
        atId=id;
        goNums=g;
    }
}