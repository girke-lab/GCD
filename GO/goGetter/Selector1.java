/*
 * Selector1.java
 *
 * Created on February 4, 2004, 12:40 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */

import khoran.debugPrint.Debug;

public class Selector1 implements GoSelector 
{
    private GoDag dag;
    private Debug d;
    
    /** Creates a new instance of Selector1 */
    public Selector1(GoDag dag)
    {
        this.dag=dag;
        d=new Debug();
//        d.setPrintLevel(2);
    }
    
    public int getGoNumber(int[] goNumbers) 
    {/* RULES:
      *     1) choose most specific go term.  
      *         a. choose the go number with the highest depth
      *         b. if several share the same depth, ? just take the first one
      */
     
        if(goNumbers.length==0)
            return 0;
        
        int maxDepth=-1; //all depths must be greater than -1
        int currentBest=goNumbers[0];
        GoNode gn;
        
        d.print(2,"--------------------");
        for(int i=0;i<goNumbers.length;i++)
        {
            gn=dag.find(goNumbers[i]);
            if(gn==null)
            {
                d.print(-1,"node "+goNumbers[i]+" was not found");//this should only occur on sample tests                
                continue;
            }
            d.print(2,goNumbers[i]+", depth="+gn.getMaxDepth()+", text="+gn.getText());
            if(gn.getMaxDepth() > maxDepth)
            {
                currentBest=goNumbers[i];
                maxDepth=gn.getMaxDepth();
            }
        }
                
        d.print("selected "+currentBest+", with depth "+maxDepth);
        return currentBest;        
    }
    
}
