/*
 * MostCommonSelector.java
 *
 * Created on February 25, 2004, 12:29 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import java.util.*;
import khoran.debugPrint.Debug;

public class MostCommonSelector implements GoSelector {
    
    Debug d;
    /** Creates a new instance of MostCommonSelector */
    public MostCommonSelector() 
    {
        d=new Debug(2);        
    }
    
    public int getGoNumber(int[] goNumbers)
    {//takes a list of numbers, and return the most common one
        
        if(goNumbers.length==0)
            return 0;
        
        HashMap counts=new HashMap();

        d.print("-------------------------");
        for(int i=0;i<goNumbers.length;i++)
        {
            d.print(goNumbers[i]+"");
            Integer c=null;
            Integer goNum=new Integer(goNumbers[i]);
            c=(Integer)counts.get(goNum);
            if(c==null) //this go num is not yet in the hash
                counts.put(goNum, new Integer(1)); //add goNum with count of 1
            else //c was found, so incrment its count
                counts.put(goNum,new Integer(c.intValue()+1));
        }
        
        int max=0;
        int bestGoNum=goNumbers[0];
        
        for(Iterator i=counts.entrySet().iterator();i.hasNext();)
        {
            Map.Entry set=(Map.Entry)i.next();
            Integer count=(Integer)set.getValue();
            if(count.intValue() > max)
            {
                max=count.intValue();
                bestGoNum=((Integer)set.getKey()).intValue();
            }
        }
        
        d.print("selected "+bestGoNum+", which occured "+max+" times");
        
        return bestGoNum;
    }
    
}
