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
    //this should take in the list of go numbers and immediatly pick one
    public void GoSelector(int[] goNumbers);
    
    //this should return the selected go number
    public int getGoNumber();
}
