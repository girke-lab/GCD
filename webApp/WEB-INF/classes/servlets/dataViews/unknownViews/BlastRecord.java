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
    String target,targetDesc,score,ident,positives,gaps,dbname,link;
    int length;
    double evalue;
    
    private static Logger log=Logger.getLogger(BlastRecord.class);
    
    /** Creates a new instance of BlastRecord */
    public BlastRecord(String t,String d,String s,String id,String pos,String gaps,String db,String l,int len,double e)
    {
        target=t;
        targetDesc=d;
        score=s;
        ident=id;
        positives=pos;
        this.gaps=gaps;
        dbname=db;
        link=l;
        length=len;
        evalue=e;
        
        link=link.replaceAll("\\$V{key}", target);
    }
    public BlastRecord(List values)
    {
        if(values==null || values.size()!=10)
        {
            log.error("invalid values list in BlastRecord constructor");
            return;
        }
        target=(String)values.get(0);
        targetDesc=(String)values.get(1);
        evalue=Double.parseDouble((String)values.get(2));
        score=(String)values.get(3);
        ident=(String)values.get(4);
        length=Integer.parseInt((String)values.get(5));
        positives=(String)values.get(6);
        gaps=(String)values.get(7);
        dbname=(String)values.get(8);
        link=(String)values.get(9);
        
        String key=target;
        if(dbname.equals("hmmPfam"))
        { //ugly hack to chop off end of pfam keys since the pfam search page does not accept them.
            int i=target.lastIndexOf('.');
            if(i > 0)
                key=target.substring(0,i);
        }
            
        
        link=link.replaceAll("\\$V\\{key\\}",key );
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
    
}
