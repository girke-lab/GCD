/*
 * ClusterNameSearch.java
 *
 * Created on March 3, 2004, 12:51 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;
public class ClusterNameSearch implements Search {
    
    List input;
    int limit;
    int db;
    
    /** Creates a new instance of ClusterNameSearch */
    public ClusterNameSearch() 
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
        Iterator in=input.iterator();
        StringBuffer conditions=new StringBuffer();
        List rs;
        int wasOp=1;
       
        while(in.hasNext())
        { //create conditions string
            String temp=(String)in.next();//use temp becuase sucsesive calls to nextToken
                                                    //advace the pointer
            if(temp.compareToIgnoreCase("and")!=0 && temp.compareToIgnoreCase("or")!=0 
                    && temp.compareToIgnoreCase("not")!=0 && temp.compareTo("(")!=0
                    && temp.compareTo(")")!=0)
            //no keywords or parinths
            {
                if(wasOp==0)//last token was not an operator, but we must have an operator between every word
                    conditions.append(" and ");
                conditions.append(" ( Cluster_Counts.Name REGEXP \""+temp+"\") ");
                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db),1);
        ArrayList al=new ArrayList();
        for(Iterator i=rs.iterator();i.hasNext();)        
            al.add(((ArrayList)i.next()).get(0));
        return al;
    }
     private String buildIdStatement(String conditions, int limit,int currentDB)
    {
        String id="SELECT DISTINCT Seq_id from Cluster_Counts LEFT JOIN Clusters USING(Cluster_id) "+
                  "WHERE ";       
        id+="("+conditions+")";
        id+=" limit "+limit;
        System.out.println("ClusterNameSearch query: "+id);   
        return id;
    }
   
}
