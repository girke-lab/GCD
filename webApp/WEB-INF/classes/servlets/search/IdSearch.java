/*
 * IdSearch.java
 *
 * Created on March 3, 2004, 12:49 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.Common;

public class IdSearch extends AbstractSearch
{
    
    /** Creates a new instance of IdSearch */
    public IdSearch() 
    {
    }
    void loadData()
    {
        ListIterator in=input.listIterator();
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        conditions.append("a.accession in (");
        while(in.hasNext() && count++ < limit)
        {
            conditions.append("'"+in.next()+"'");
            if(in.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");


        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db));
        ArrayList al=new ArrayList();
        String lastDb="";
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)
        {
            ArrayList t=(ArrayList)i.next();
            if(!lastDb.equals(t.get(2))){
                lastDb=(String)t.get(2);
                dbStartPositions[Common.getDBid(lastDb)]=c;
            }
            al.add(t.get(0));
            keysFound.add(t.get(1));
        }
        System.out.println("al="+al);
        data=al;
        if(data.size() > Common.MAX_QUERY_KEYS) 
            stats=(List)Common.sendQuery(buildStatsStatement(conditions.toString(),db)).get(0);        
    }
    private String buildCondition()
    {
        StringBuffer condition=new StringBuffer();
        condition.append(" s.seq_id in (");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            condition.append(i.next());
            if(i.hasNext())
                condition.append(",");
        }
        condition.append(")");
        return condition.toString();
    }
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT a.Seq_id, a.Accession,s.genome FROM Sequences as s, Id_Associations as a "+
                  "WHERE s.seq_id=a.seq_id AND ("; 

        for(int i=0;i<DBs.length;i++)
        {
            id+=" s.Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+conditions+")";
        id+=" order by genome";
        id+=" limit "+limit;
        System.out.println("IdSearch query: "+id);   
        return id;
    }
    private String buildStatsStatement(String conditions,int[] dbs)
    {
        conditions="( "+conditions+") AND (";
        for(int i=0;i<dbs.length;i++)
        {
            conditions+=" Genome='"+Common.dbRealNames[dbs[i]]+"' ";
            if(i < dbs.length-1)//not last iteration of loop
                conditions+=" or ";
        }
        conditions+=" )";
        String query="SELECT t1.count as model_count, t2.count as cluster_count" +
        " FROM" +
        "        (select count( distinct m.model_id) from sequences as s, models as m, id_associations as a" +
        "        where s.seq_id=m.seq_id and s.seq_id=a.seq_id and "+conditions+" ) as t1," +
        "        (select count(distinct c.cluster_id) from sequences as s, clusters as c, id_associations as a" +
        "        where s.seq_id=c.seq_id and s.seq_id=a.seq_id and "+conditions+" ) as t2";       
                
        System.out.println("IdSearch stats query: "+query);
        return query;
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
