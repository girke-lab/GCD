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

public class BlastRecord implements Record
{
    public String target,score,ident,positives,gaps,dbname;
    public int length;
    public float evalue;
    
    private static Logger log=Logger.getLogger(BlastRecord.class);
    
    /** Creates a new instance of BlastRecord */
    public BlastRecord(String t,String s,String id,String pos,String gaps,String db,int len,float e)
    {
        target=t;
        score=s;
        ident=id;
        positives=pos;
        this.gaps=gaps;
        dbname=db;
        length=len;
        evalue=e;
    }
    public BlastRecord(List values)
    {
        if(values==null || values.size()!=8)
        {
            log.error("invalid values list in BlastRecord constructor");
            return;
        }
        target=(String)values.get(0);
        evalue=Float.parseFloat((String)values.get(1));
        score=(String)values.get(2);
        ident=(String)values.get(3);
        length=Integer.parseInt((String)values.get(4));
        positives=(String)values.get(5);
        gaps=(String)values.get(6);
        dbname=(String)values.get(7);
        
        
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
    
    public void printHeader(java.io.PrintWriter out)
    {
        out.println("<tr bgcolor='"+Common.titleColor+"'><th>Target Key</th><th>E-value</th>" +
                    "<th>Score</th><th>Database</th></tr>");
    }
    
    public void printRecord(java.io.PrintWriter out)
    {
        out.println("<tr><td>"+target+"</td><td>"+evalue+"</td><td>"+score+"</td>" +
                    "<td>"+dbname+"</td></tr>");
    }
    
}
