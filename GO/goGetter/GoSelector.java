/*
 * GoSelector.java
 *
 * Created on January 28, 2004, 2:33 PM
 */

package GO.goGetter;

/**
 *
 * @author  khoran
 */
public interface GoSelector 
{    
    //this should return the selected go number
    public int getGoNumber(int[] goNumbers);
}
