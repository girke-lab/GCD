/*
 * DescriptionManager.java
 *
 * Created on September 19, 2007, 10:09 AM
 *
 */

package servlets;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * manage descriptions used in webapp, mostly for
 * titles of stuff.
 * @author khoran
 */
public class DescriptionManager
{
    
    private static final Logger log=Logger.getLogger(DescriptionManager.class);
    private static final String filename="descriptions.properties";
    private final URL propertiesUrl;
    
    private static DescriptionManager manager=null;
    
    
    private Properties descriptions;
    
    /** Creates a new instance of DescriptionManager */
    private DescriptionManager()
    {
        propertiesUrl=this.getClass().getResource(filename);
        descriptions=new Properties();
        
        try{
            descriptions.load(new FileInputStream(propertiesUrl.getPath()));
        }catch(IOException e){
            log.error("failed to load descriptions file: "+e);
        }
    }
    
    public static DescriptionManager getInstance()
    {
        if(manager==null)
            manager=new DescriptionManager();
        return manager;
    }
    
    
    /* fetch the description associated with this
     * key, and return some html which will display it
     * in a popup box
     */
    public static String renderPopup(String key)
    {
        String desc=getInstance().getDescription(key);
        if(desc==null || desc.equals(""))
            return "";
       return "onmouseover=\" this.T_CLICKCLOSE=true; this.T_STICKY=true; return escape('"+desc+"')\"";
    }
    public static String wrapText(String key,String text)
    {
       return "<span "+renderPopup(key)+">"+text+"</span>"; 
    }
    public static String wrapLink(String key,String text,String url)
    {
       return "<A href='"+url+"' "+renderPopup(key)+" >"+text+"</A>"; 
    }
    
    public String getDescription(String key)
    {
        return descriptions.getProperty(key,"");
    }
    public void setDescription(String key, String value)
    {
        descriptions.setProperty(key,value);
        try{
            descriptions.store(new FileOutputStream(propertiesUrl.getPath()),"descriptions");
        }catch(IOException e){
            log.error("could not save description changes: "+e);
        }
    }
    
}
