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
    List input;
    int limit;
    int db;
    
    /** Creates a new instance of GoSearch */
    public GoSearch() 
    {
    }
    
    public void init(List data, int limit, int dbID)
    {
        this.input=data;
        this.limit=limit;
        this.db=dbID;
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
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),1);

        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)        
            al.add(((ArrayList)i.next()).get(0));
        return al;
    }
    
    private String buildIdStatement(String conditions, int limit,int currentDB)
    {
        String id="SELECT DISTINCT Seq_id from Go "+
                  "WHERE ";
        id+="("+conditions+")";
        id+=" limit "+limit;
        System.out.println("IdSearch query: "+id);   
        return id;
    }
    
}
