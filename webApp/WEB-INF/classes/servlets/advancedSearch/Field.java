/**
 * field name dipslyed to user
 */
/**
 * databases field name
 */
/**
 * databases field name
 */
/**
 * Field  name displayed to user.
 */
/*
 * Field.java
 *
 * Created on September 7, 2004, 11:41 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;
import org.apache.log4j.Logger;

 /**
  * This class is used to store information about a searchable field and
  * and can also render the search field in html.
  */
 public class Field
 {
        public String displayName,dbName;
        /**
         * The type of this field
         */
        public Class type;
        private Object[] list;
        private static Map validOps;  //maps types to operations
        private static Logger log=Logger.getLogger(Field.class);
        
        /**
         * 
         */
        public Field(String name, String dbn)
        {
            displayName=name;
            dbName=dbn;
            type=String.class;
            list=null;
        }
        /**
         * 
         */
        public Field(String name, String dbn,Class t)
        {
            displayName=name;
            dbName=dbn;
            type=t;
            list=null;
        }
        public Field(String name, String dbn,Object[] l)
        {
            displayName=name;
            dbName=dbn;
            type=String.class;
            list=l;
        }
        public Field(String name, String dbn,Class t,Object[] l)
        {
            displayName=name;
            dbName=dbn;
            type=t;
            list=l;
        }
        
        /**
         * Returns html view of this field with the current value.
         * @param currentValue value of field, as entered by user
         * @return html required to draw this field
         */
        public String render(String currentValue)
        {//draws corect input statement for this type
            if(list!=null && list.length != 0)
            {//render dropdown box
                String output="<SELECT name='values'>\n";                
                for(int i=0;i<list.length;i++)
                {
                    String str=list[i].toString();
                    output+="<OPTION ";
                    if(currentValue.equals(str))
                        output+="selected ";
                    output+=">"+str+"</OPTION>";
                }
                output+="</SELECT>";
                return output;
            }
            else if(type.equals(List.class))
            {
                return "<TEXTAREA name='values' rows='1'>"+currentValue+"</TEXTAREA>";                
            }
            else //use a text field
                return "<INPUT type=text name='values' value='"+currentValue+"'>";
        }
        public String[] getValidOps()
        {
            if(validOps==null)
                defineValidOps();
            String[] ops=(String[])validOps.get(type);
            if(ops!=null)
                return ops;
            log.error("no ops known for type "+type);
            return new String[]{""};
            
        }
        private void defineValidOps()
        {
            validOps=new HashMap();
            
            validOps.put(String.class, new String[]{"=","!=",Common.ILIKE,"NOT "+Common.ILIKE});
            validOps.put(Integer.class, new String[]{"<",">","<=",">=","=","!="});
            validOps.put(Float.class, new String[]{"<",">","<=",">=","=","!="});
            validOps.put(List.class, new String[]{"IN","NOT IN",Common.ILIKE,"NOT "+Common.ILIKE});
            validOps.put(Boolean.class, new String[]{"=","!="});
            
        }
    }