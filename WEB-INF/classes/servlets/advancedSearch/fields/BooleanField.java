/*
 * BooleanField.java
 *
 * Created on April 6, 2006, 10:37 AM
 *
 */

package servlets.advancedSearch.fields;

/**
 *
 * @author khoran
 */
public class BooleanField extends Field
{
    private static final String[] validOps=new String[]{"=","!="};
    
    
    /** Creates a new instance of BooleanField */
    public BooleanField(String displayName,String dbName)
    {
        super(displayName,dbName);
        
    }
   

    public String[] getValidOps()    
    {        
        return validOps;        
    }
      
    public Class getType()
    {
        return Boolean.class;
    }
    
    
    public String render(String currentValue)
    { //render a drop down list                
        
        StringBuilder output=new StringBuilder("<SELECT name='values'>\n");
        String trueSelected="",falseSelected="";
        if("TRUE".equalsIgnoreCase(currentValue))
            trueSelected="selected";
        else if("FALSE".equalsIgnoreCase(currentValue))
            falseSelected="selected";
        else
            log.warn("bad currentValue for BooleanField: "+currentValue);
        
        output.append("<OPTION "+trueSelected+" >TRUE</OPTION");
        output.append("<OPTION "+falseSelected+" >FALSE</OPTION");

        output.append("</SELECT>");
        return output.toString();
    }
}
