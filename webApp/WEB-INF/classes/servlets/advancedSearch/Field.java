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
        public static final int POSTGRESQL=0;
        public static final int MYSQL=1;
     
        public String displayName,dbName;       
        public Class type;
        
        private int dbType=POSTGRESQL; //should be one of POSTGRESQL or MYSQL
        private Object[] list;
        private String[] titles;
        private static Map validOps;  //maps types to operations
        private static Logger log=Logger.getLogger(Field.class);
        private boolean sortable=false; //fields are only sortable if they are in a 1-1 relation with the primary key.
        private boolean hidden=false; //hidden fields are not displayed to user
        
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
        public Field(String name, String dbn,String[] titles, Object[] l)
        {
            displayName=name;
            dbName=dbn;
            type=String.class;
            list=l;
            this.titles=titles;
        }
        public void setDbType(int t)
        {
            dbType=t;
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
                    if(currentValue.equalsIgnoreCase(str))
                        output+="selected ";
                    if(titles!=null && i<titles.length) 
                        output+=" value='"+str+"' >"+titles[i]+"</OPTION>";
                    else
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
            
            String like="ILIKE";
            if(dbType==MYSQL)
                like="LIKE";
            
            validOps.put(String.class, new String[]{"=","!=",like,"NOT "+like});
            validOps.put(Integer.class, new String[]{"<",">","<=",">=","=","!="});
            validOps.put(Float.class, new String[]{"<",">","<=",">=","=","!="});
            validOps.put(List.class, new String[]{"IN","NOT IN",like,"NOT "+like});
            validOps.put(Boolean.class, new String[]{"=","!="});
            
        }

     public boolean isSortable()
     {
         return sortable;
     }

     public void setSortable(boolean sortable)
     {
         this.sortable = sortable;
     }

     public boolean isHidden()
     {
         return hidden;
     }

     public void setHidden(boolean hidden)
     {
         this.hidden = hidden;
     }
     public String toString()
     {
        
        return displayName+", "+dbName+", type:"+type.getName();
     }
}