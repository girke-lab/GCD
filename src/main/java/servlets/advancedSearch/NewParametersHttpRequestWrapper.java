/*
 * NewParametersHttpRequestWrapper.java
 *
 * Created on September 7, 2004, 11:42 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author  khoran
 */

import javax.servlet.http.*;
import java.util.*;

public class NewParametersHttpRequestWrapper extends HttpServletRequestWrapper
{
    private Map parameterMap;
    private String method;

    public NewParametersHttpRequestWrapper(HttpServletRequest request)
    {
        super(request);
    }

    public NewParametersHttpRequestWrapper(HttpServletRequest request,
                                            Map parameterMap,
                                            boolean keepExistingParameters,
                                            String method)
    {
        this(request);
        this.parameterMap = parameterMap;
        if(keepExistingParameters)
        {
            Enumeration existingParameterNames = request.getParameterNames();
            while(existingParameterNames.hasMoreElements())
            {
                String existingParameterName = (String)existingParameterNames.nextElement();
                String existingParameterValue = request.getParameter(existingParameterName);
                this.parameterMap.put(existingParameterName, existingParameterValue);
            }
        }
        if(method.equalsIgnoreCase("GET"))
            this.method = "GET";
        else if(method.equalsIgnoreCase("POST"))
            this.method = "POST";
        else
            throw new IllegalArgumentException(" is not a valid HHTP method type. Must be GET or POST.");
    }

    public Map getParameterMap()
    {
        return parameterMap;
    }

    public java.util.Enumeration getParameterNames()
    {
        class Enumeration implements java.util.Enumeration
        {
            private final List list;
            private int index = 0;

            public Enumeration(List list)
            {
                this.list = list;
            }

            public Object nextElement()
            {
                if(index >= list.size())
                    throw new NoSuchElementException();
                else
                {
                    Object object = list.get(index);
                    index++;
                    return object;
                }
            }

            public boolean hasMoreElements()
            {
                if (index < list.size())
                    return true;
                else
                    return false;
            }
        }

        Set parameterNamesSet = parameterMap.keySet();
        List parameterNamesList = new ArrayList(parameterNamesSet);
        Enumeration paramterNames = new Enumeration(parameterNamesList);
        return paramterNames;
    }

    public String[] getParameterValues(String name)
    {

        Object value = parameterMap.get(name);
        if (value == null)
            return null;
        else if(value instanceof List)
        {
            String[] values=new String[((List)value).size()];
            int j=0;
            for(Iterator i=((List)value).iterator();i.hasNext();)
                values[j++]=(String)i.next();
            return values;
        }                
        else
        {
            String[] values = new String[1];
            values[0] = (String)value;
            return values;
        }
    }

    public String getParameter(String name)
    {
        Object value = parameterMap.get(name);
        if (value == null)
            return null;
        return (String)value;
    }

    public String getMethod()
    {
        return method;
    }
}
