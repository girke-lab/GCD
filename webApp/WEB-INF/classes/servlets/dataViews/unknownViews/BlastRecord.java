/*
 * BlastRecord.java
 *
 * Created on October 12, 2004, 1:56 PM
 */

package servlets.dataViews.unknownViews;

/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.Common;
import org.apache.log4j.Logger;
import servlets.DbConnection;

public class BlastRecord implements Record
{
    String target,targetDesc,score,ident,positives,gaps,dbname,link,method;
    int length;
    double evalue;
    
    private static Logger log=Logger.getLogger(BlastRecord.class);
    
    /** Creates a new instance of BlastRecord */
    public BlastRecord(String t,String d,String s,String id,String pos,String gaps,String db,String l,String m,int len,double e)
    {
        target=t;
        targetDesc=d;
        score=s;
        ident=id;
        positives=pos;
        this.gaps=gaps;
        dbname=db;
        link=buildLink(l,target);
        length=len;
        evalue=e;       
        method=m;
    }
    public BlastRecord(List values)
    {
        if(values==null || values.size()!=11)
        {
            log.error("invalid values list in BlastRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 10");
            return;
        }
        if(values.get(0)==null) //this is a no hit
        {
            target="no hit";
        }
        else{
            target=(String)values.get(0);
            targetDesc=(String)values.get(1);
            evalue=Double.parseDouble((String)values.get(2));
            score=(String)values.get(3);
            ident=(String)values.get(4);
            length=Integer.parseInt((String)values.get(5));
            positives=(String)values.get(6);
            gaps=(String)values.get(7);
            dbname=(String)values.get(8);
            link=buildLink((String)values.get(9),target); 
            method=(String)values.get(10);
        }
    }
    private String buildLink(String link,String key)
    {                
        if(dbname.equals("pfam"))  //must make sure that this name always matches the value in the db_name field of unknowns.blast_databases
        { //ugly hack to chop off end of pfam keys since the pfam search page does not accept them.
            int i=key.lastIndexOf('.');
            if(i > 0)
                key=key.substring(0,i);
        }                            
        return link.replaceAll("\\$V\\{key\\}",key );
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof BlastRecord))
            return false;
        BlastRecord br=(BlastRecord)o;
        //everything must match
        return br.target.equals(target) &&
               br.dbname.equals(dbname) &&
               br.evalue==evalue &&
               br.score.equals(score) &&
               br.ident.equals(ident) &&
               br.positives.equals(positives) &&
               br.gaps.equals(gaps) &&
               br.length==length;
    }
    public int hashCode()
    {
        return target.hashCode()+dbname.hashCode()+length;
    }
    public String toString()
    {
        return "target="+target+",evalue="+evalue;
    }
    
    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);       
    }
    
    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);      
    }
  
    public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
    }
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,"db_name","ASC");
    }    
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        String query="SELECT * " +
        "   FROM unknowns.blast_summary_mv " +
        "   WHERE "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
                
        List data=null;
        try{
            data=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("could not send BlastRecord query: "+e.getMessage());
            return new HashMap();
        }
        List row,l;
        Map output=new HashMap(); 
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            l=(List)output.get(row.get(0));
            if(l==null)
            {
                l=new LinkedList();
                output.put(row.get(0),l);
            }            
            //log.debug("adding row "+row.subList(2,13));
            l.add(new BlastRecord(row.subList(2,13)));
        }
        return output;
    }       
    
}
