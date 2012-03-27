/*
 * IntField.java
 *
 * Created on April 6, 2006, 10:36 AM
 *
 */

package servlets.advancedSearch.fields;

/**
 *
 * @author khoran
 */
public class IntField extends Field
{
    
    private static final String[] validOps=new String[]{"<",">","<=",">=","=","!="};
    
    /** Creates a new instance of IntField */
    public IntField(String displayName,String dbName)
    {
        super(displayName,dbName);
    }
    
       
    public String[] getValidOps()
    {
        return validOps;
    }
    
    public Class getType()
    {
        return Integer.class;
    }
    
}
