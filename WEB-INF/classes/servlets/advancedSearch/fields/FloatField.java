/*
 * FloatField.java
 *
 * Created on April 6, 2006, 10:37 AM
 *
 */

package servlets.advancedSearch.fields;

/**
 *
 * @author khoran
 */
public class FloatField extends Field
{
    private static final String[] validOps=new String[]{"<",">","<=",">=","=","!="};
    
    /** Creates a new instance of FloatField */
    public FloatField(String displayName,String dbName)
    {
        super(displayName,dbName);
    }
    public String[] getValidOps()
    {
        return validOps;
    }

    public Class getType()
    {
        return Float.class;
    }
}
