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
    int[] db;
    
    /** Creates a new instance of IdSearch */
    public IdSearch() 
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

        conditions.append("Id_Associations.Accession in (");
        while(in.hasNext() && count++ < limit)
        {
            conditions.append("'"+in.next()+"'");
            if(in.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");


        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db));
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

    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id, Accession from Sequences LEFT JOIN Id_Associations USING(Seq_id) "+
                  "WHERE (";

        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+conditions+")";
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
