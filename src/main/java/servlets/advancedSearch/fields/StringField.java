/*
 * StringField.java
 *
 * Created on April 6, 2006, 10:37 AM
 *
 */

package servlets.advancedSearch.fields;

/**
 *
 * @author khoran
 */
public class StringField extends Field
{    
    private boolean multiple=false;
            
    /** Creates a new instance of StringField */
    public StringField(String displayName,String dbName)
    {
        super(displayName,dbName);
        
    }
    public StringField(String displayName,String dbName,boolean multiple)
    {
        super(displayName,dbName);
        
        this.multiple=multiple;
    }
    
    public String[] getValidOps()
    {        
        if(multiple)
            return new String[]{"IN","NOT IN",getDbType().like(),"NOT "+getDbType().like()};                        
        else
            return new String[]{"=","!=",getDbType().like(),"NOT "+getDbType().like()};
    }   
    public StringField setMultiple(boolean b)
    {
        multiple=b;
        return this;
    }
    
    public String render(String currentValue)
    {
        if(!multiple)
            return super.render(currentValue);
        
        return "<TEXTAREA name='values' rows='1'>"+currentValue+"</TEXTAREA>";    
    }

    public Class getType()
    {
        if(multiple)
            return java.util.List.class;
        return String.class;
    }
}
