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

 public class Field
 {
        public String displayName,dbName;
        public Class type;
        private Object[] list;
        public Field(String name, String dbn)
        {
            displayName=name;
            dbName=dbn;
            type=String.class;
            list=null;
        }
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
            type=List.class;
            list=l;
        }
        
        public String render(String currentValue)
        {//draws corect input statement for this type
            if(type.isAssignableFrom(List.class))
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
            else //use a text field
                return "<INPUT type=text name='values' value='"+currentValue+"'>";
        }
    }