/*
 * ClusterNameSearch.java
 *
 * Created on March 3, 2004, 12:51 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.search.Search;
import servlets.Common;

public class ClusterNameSearch extends AbstractSearch
{   
    
    /** Creates a new instance of ClusterNameSearch */
    public ClusterNameSearch() 
    {
    }
    
    void loadData()
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
                conditions.append(" ( Cluster_Info.Name ILIKE '%"+temp+"%') ");

                wasOp=0;
            }
            else //must be a keyword or a parinth
            {
                conditions.append(" "+temp+" ");    
                wasOp=1;
            }    
        }
        rs=Common.sendQuery(buildIdStatement(conditions.toString(),limit,db));
        
        ArrayList al=new ArrayList();
        String lastDb="";
        List row;
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)        
        {
            row=(List)i.next();
            if(!lastDb.equals(row.get(1))){
                lastDb=(String)row.get(1);
                dbStartPositions[Common.getDBid(lastDb)]=c;
            }            
            al.add(row.get(0));
        }                
        data=al;
        stats=Arrays.asList(new String[]{null,Integer.toString(data.size())});
    }
   
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id,sequences.genome from Cluster_Info, Clusters, Sequences "+
                  "WHERE Cluster_Info.cluster_id=Clusters.cluster_id AND "+
                  " Clusters.seq_id=Sequences.seq_id AND (";

        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }

        id+=") AND "+conditions;
        id+=" order by genome";
        id+=" limit "+limit;
        System.out.println("ClusterNameSearch query: "+id);
        return id;
    }
}
