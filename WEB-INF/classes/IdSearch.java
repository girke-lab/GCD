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
    ArrayList keysFound; //list of the keys found, of the same type of the query key.
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
        keysFound=new ArrayList();
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
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),2);

        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)        
        {
            ArrayList t=(ArrayList)i.next();
            al.add(t.get(0));
            keysFound.add(t.get(1));
        }
        System.out.println("al="+al);
        return al;

    }
    private String buildIdStatement(String conditions, int limit,int currentDB)
    {
        String id="SELECT DISTINCT Sequences.Seq_id, Accession from Sequences LEFT JOIN Id_Associations USING(Seq_id) "+
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
