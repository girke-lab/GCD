/*
 * DescriptionSearch.java
 *
 * Created on March 3, 2004, 12:49 PM
 */

/**
 *
 * @author  khoran
 */
import java.util.*;
public class DescriptionSearch implements Search 
{

    List input;
    int limit;
    int[] db;
    
    /** Creates a new instance of DescriptionSearch */
    public DescriptionSearch() 
    {
    }
    public void init(List data, int limit, int[] dbID)
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
                conditions.append(" ( Sequences.Description ILIKE '%"+temp+"%') ");
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
    private String buildIdStatement(String conditions, int limit,int[] DBs)
    {
        String id="SELECT DISTINCT Sequences.Seq_id from Sequences "+
                  "WHERE (";
        
        for(int i=0;i<DBs.length;i++)
        {
            id+=" Genome='"+Common.dbRealNames[DBs[i]]+"' ";
            if(i < DBs.length-1)//not last iteration of loop
                id+=" or ";
        }
        
        id+=") and ("+conditions+")";
        id+=" limit "+limit;
        System.out.println("Description query: "+id);   
        return id;
    }
    
    public List notFound() 
    {
        return new ArrayList(); //return an empty list since this operation does not make sense.
    }    
    
}
