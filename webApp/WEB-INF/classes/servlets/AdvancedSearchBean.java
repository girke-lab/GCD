/*
 * AdvancedSearchServlet.java
 *
 * Created on August 5, 2004, 2:56 PM
 */

package servlets;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.servlet.jsp.JspWriter;

/**
 *
 * @author  khoran
 * @version
 */
public class AdvancedSearchBean {
 
    
    
    public Field[] fields;
    public String[] operators;
    public String[] booleans;    
    
    //user values
    public List selectedFields;
    public List selectedOps;
    public List selectedBools;
    public List values;
    public List startParinths;
    public List endParinths;
    public String limit;
    public int sortField;
    
    boolean lastWasRemove=false;
    ServletContext servletContext=null;
    ServletRequest request=null;
    ServletResponse response=null;
    
    public AdvancedSearchBean()
    {
        defineOptions(); //load all the global arrays
    }
    public void loadValues(HttpServletRequest request)
    {
        selectedFields=getIntList(request.getParameterValues("fields"));
        selectedOps=getIntList(request.getParameterValues("ops"));
        selectedBools=getIntList(request.getParameterValues("bools"));
        values=getList(request.getParameterValues("values"));
        
        startParinths=getIntList(request.getParameterValues("startPars"));
        endParinths=getIntList(request.getParameterValues("endPars"));
        
        String sortTemp=request.getParameter("sortField");
        limit=request.getParameter("limit");
        
        
        if(selectedFields==null)        
            selectedFields=new ArrayList();
        if(startParinths==null)
            startParinths=new ArrayList();
        if(endParinths==null)
            endParinths=new ArrayList();
        if(sortTemp==null)
            sortField=2; //default to sort on cluster id
        else
            sortField=Integer.parseInt(sortTemp);
        if(limit==null)
            limit="50";
        
        processCommands(request);
    }
    public String selected(List values,int i, int j)
    {
        return values != null && i >= 0 && i<values.size() && ((Integer)values.get(i)).intValue()==j ? 
            "selected" : "";
    }
    public String getValue(int i)
    {
        if(values != null && i >= 0 && i<values.size())
            return (String)values.get(i); 
        return "";
    }
    public Object get(List l,int i)
    {
        if(l!=null && i>=0 && i<l.size())
            return l.get(i);
        return null;
    }
    public int printParinth(JspWriter out,int j, int sp,String position,int space) 
    {
        boolean start=position.equals("start");
        List parinths= start? startParinths : endParinths;
        if(sp < parinths.size() && ((Integer)parinths.get(sp)).intValue()==j)            
        {
            try{
                if(start)
                    out.println(printSpace(space)+"(");                
                else
                    out.println(printSpace(space-1)+")");
                        
                String name= start? "startPars" : "endPars";
                out.println("<input type=hidden name='"+name+"' value='"+j+"'>");
                return sp+1;
            }catch(IOException e){}
        }
        return sp;
    }
    public boolean printEndSubButton(int sp,int ep)
    {
        return startParinths.size() > endParinths.size();
    }
    public String printSpace(int level)
    {//used for indenting sub expressions
        String space="";
        for(int i=0;i<level;i++)
            space+="&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp";
        return space;        
    }
    public int getLoopCount()
    {
        System.out.println("lastWasRemove="+lastWasRemove);
        if(lastWasRemove){
            lastWasRemove=false;
            return selectedFields.size();
        }
        return selectedFields.size()+1;
    }
    public void setContext(ServletContext sc,HttpServletRequest rq,HttpServletResponse rs)
    {
        servletContext=sc;
        request=rq;
        response=rs;
    }
    
//////////////////// PRIVATE METHODS  ////////////////////////////////////    
    private void processCommands(HttpServletRequest request)
    {
        if(request.getParameter("remove") != null){
            String row=request.getParameter("row");    
            if(row==null)
                return;            
            removeExpression(Integer.decode(row));
        }else if(request.getParameter("search") != null){
            doQuery();
        }else if(request.getParameter("add_sub_exp") != null){
            //add parith at this location
            addSubExp(); //add current index to startPars
        }else if(request.getParameter("end_sub_exp") != null){
            endSubExp(); //add current index to endPars
        }else if(request.getParameter("add_exp") != null){
            //nothing to do
        }else if(request.getParameter("action") !=null)
            lastWasRemove=true;
        
    }
    private void removeExpression(Integer row)
    { //remove entry row from fields, ops, values, and bools
        selectedFields.remove(row.intValue());
        selectedOps.remove(row.intValue());
        values.remove(row.intValue());
        selectedBools.remove(row.intValue());
        
        System.out.println("removing "+row);
        System.out.println("start: "+startParinths+", end: "+endParinths);
        for(int i=0;i<startParinths.size();i++)
            if(((Integer)startParinths.get(i)).intValue() > row.intValue())
                startParinths.set(i, new Integer(((Integer)startParinths.get(i)).intValue()-1));
        for(int i=0;i<endParinths.size();i++)
            if(((Integer)endParinths.get(i)).intValue() >= row.intValue())
                endParinths.set(i, new Integer(((Integer)endParinths.get(i)).intValue()-1));
           
//        startParinths.remove(row);
//        endParinths.remove(row);
        System.out.println("2start: "+startParinths+", end: "+endParinths);
        
        lastWasRemove=true;
    }
    private void doQuery()
    { //put all the conditions together to build a query
        if(servletContext==null)
        {
            System.out.println("could not get servlet context");
            return;
        }
        StringBuffer query=new StringBuffer();
        if(fields[sortField].dbName.startsWith("cluster_info"))
            query.append("SELECT DISTINCT cluster_info.cluster_id ");
        else
            query.append("SELECT DISTINCT sequences.seq_id ");
        query.append("FROM sequences , cluster_info, clusters , go ");
        query.append("WHERE sequences.seq_id=clusters.seq_id " +
                     "AND clusters.cluster_id=cluster_info.cluster_id "+
                     "AND sequences.seq_id=go.seq_id ");
        query.append("AND (");
        
        int sp=0,ep=0;
        int fid,oid;
        for(int i=0;i<selectedFields.size();i++)
        {
            if(sp < startParinths.size() && ((Integer)startParinths.get(sp)).intValue()==i){
                sp++;
                query.append("(");
            }
            fid=((Integer)selectedFields.get(i)).intValue();
            oid=((Integer)selectedOps.get(i)).intValue();
            
            
            if(fields[fid].displayName.equals("Cluster Type")){
                if(values.get(i).equals("blast"))
                    query.append(fields[fid].dbName+" NOT LIKE 'PF%' ");
                else if(values.get(i).equals("hmm"))
                    query.append(fields[fid].dbName+" LIKE 'PF%'");
            }
            else{
                query.append(fields[fid].dbName+" "+operators[oid]+" ");

                if(fields[fid].type.equals(String.class) || fields[fid].type.equals(List.class))
                    query.append("'"+values.get(i)+"'");            
                else
                    query.append(values.get(i));    
            }
            
            query.append(" ");
            
            if(ep < endParinths.size() && ((Integer)endParinths.get(ep)).intValue()==i){
                ep++;
                query.append(")");
            }

            if(i+1 < selectedFields.size())
                query.append(booleans[((Integer)selectedBools.get(i)).intValue()]+" ");
            
            
        }
        
        
        query.append(") LIMIT "+limit);
        System.out.println("query is: "+query);
        List results=Common.sendQuery(query.toString());
        //then figure out how to pass this info to QueryPageServlet via post.
        //set the parameters needed by QueryPageServlet
        
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", limit);
        mRequest.getParameterMap().put("sortCol",fields[sortField].dbName);        
                
        if(fields[sortField].dbName.startsWith("cluster_info"))
            mRequest.getParameterMap().put("displayType","clusterView");
        else
            mRequest.getParameterMap().put("displayType","seqView");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        try{
            servletContext.getRequestDispatcher("/QueryPageServlet").forward(mRequest, response);    
        }catch(Exception e){
            System.out.println("could not forward to QueryPageServlet: "+e.getMessage());
            e.printStackTrace();
        }
    }
    private void addSubExp()
    { //add the length of fields at the end of startPars
        System.out.println("adding sub exp");
        startParinths.add(new Integer(selectedFields.size()));
        System.out.println("startParinths="+startParinths);
    }
    private void endSubExp()
    { //add the length of fields at end of endPars
        endParinths.add(new Integer(selectedFields.size()));        
    }
        
    
    
    private void defineOptions()
    {
        fields=new Field[]{ new Field("Loci Id", "sequences.primary_key"),
                            new Field("Loci Description","sequences.description"),
                            new Field("Cluster Id","cluster_info.filename"),
                            new Field("Cluster Name","cluster_info.name"),
                            new Field("Cluster Type","cluster_info.filename",new String[]{"blast","hmm"}),
                            new Field("Cluster Size","cluster_info.size",Integer.class),
                            new Field("# arab keys in cluster","cluster_info.arab_count",Integer.class),
                            new Field("# rice keys in cluster","cluster_info.rice_count",Integer.class),
                            new Field("Database","sequences.Genome",new String[]{"arab","rice"}),
                            new Field("GO Number","go.go")
        };
        operators=new String[]{"=","!=","<",">","<=",">=","LIKE","NOT LIKE"};
        booleans=new String[]{"and","or"};        
    }
    private List getIntList(String[] strings)
    {
        if(strings==null)
            return null;
        List ints=new ArrayList();
        for(int i=0;i<strings.length;i++)
            ints.add(Integer.decode(strings[i]));
        return ints;
    }
    private List getList(String[] strings)
    {
        if(strings==null)
            return null;
        List strs=new ArrayList();
        for(int i=0;i<strings.length;i++)
            strs.add(strings[i]);
        return strs;
    }
    private int[] getIntArray(String[] strings)
    {
        if(strings==null)
            return null;
        int[] ints=new int[strings.length];
        for(int i=0;i<strings.length;i++)
            ints[i]=Integer.parseInt(strings[i]);
        return ints;
    }
    
    
    
    
    
    
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
}
