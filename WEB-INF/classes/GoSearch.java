/*
 * GoSearch.java
 *
 * Created on March 3, 2004, 12:51 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;
public class GoSearch implements Search 
{
    List input,keysFound;
    int limit;
    int[] db;
    
    /** Creates a new instance of GoSearch */
    public GoSearch() 
    {
    }
    
    public void init(List data, int limit, int[] dbID)
    {
        this.input=data;
        this.limit=limit;
        this.db=dbID;
        keysFound=new ArrayList();
    }
    
    public List getResults() 
    {
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        while(in.hasNext() && count++ < limit) //build condtions
            conditions.append("Go.Go LIKE '"+in.next()+"' OR ");
        conditions.append(" 0=1 ");                 
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),2);

        ArrayList al=new ArrayList();
        ArrayList t;
        for(Iterator i=rs.iterator();i.hasNext();)        
        {
            t=(ArrayList)i.next();
            al.add(t.get(0));
            keysFound.add(t.get(1));
        }
        return al;
    }
    
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT Seq_id,Go.Go from Go "+
                  "WHERE ";
        id+="("+conditions+")";
        id+=" limit "+limit;
        System.out.println("IdSearch query: "+id);   
        return id;
    }
    
    public List notFound()
    {//find the intersection of inputKeys and keysFound.
        List temp=new ArrayList();
        String el;
        for(Iterator i=input.iterator();i.hasNext();)
        {
            el=(String)i.next();
            if(!el.matches(".*%.*")) //don't add wildcard entries
                temp.add(el);
        }
        System.out.println("temp="+temp);
        temp.removeAll(keysFound);
        return temp;        
    }
    
}
