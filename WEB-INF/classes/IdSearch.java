/*
 * IdSearch.java
 *
 * Created on March 3, 2004, 12:49 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;

public class IdSearch implements Search {
    
    List input;
    int limit;
    int db;
    
    /** Creates a new instance of IdSearch */
    public IdSearch() 
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
            conditions.append("Id_Associations.Accession LIKE '"+in.next()+"' OR ");
        conditions.append(" 0=1 ");                 
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),1);

        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)        
            al.add(((ArrayList)i.next()).get(0));
        System.out.println("al="+al);
        return al;

    }
    private String buildIdStatement(String conditions, int limit,int currentDB)
    {
        String id="SELECT DISTINCT Sequences.Seq_id from Sequences LEFT JOIN Id_Associations USING(Seq_id) "+
                  "WHERE ";
        if(currentDB==Common.arab)
            id+=" Genome='arab' and ";
        else if(currentDB==Common.rice)
            id+=" Genome='rice' and ";
        id+="("+conditions+")";
        id+=" limit "+limit;
        System.out.println("IdSearch query: "+id);   
        return id;
    }
}
