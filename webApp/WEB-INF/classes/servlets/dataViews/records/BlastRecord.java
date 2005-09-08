/*
 * BlastRecord.java
 *
 * Created on October 12, 2004, 1:56 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.PageColors;
import servlets.dataViews.records.formats.BlastCompositeFormat;
import servlets.dataViews.records.formats.CompositeFormat;
import servlets.querySets.*;

/**
 * Stores blast information from the unknowns database.
 */
public class BlastRecord extends AbstractRecord
{
    public String target,targetDesc,score,ident,positives,gaps,dbname,link,method,purpose;
    public int length;
    public double evalue;
    public Integer accId;
    
    private static Logger log=Logger.getLogger(BlastRecord.class);
    
   
    /**
     * Takes a list of values corresponding to the data values that can be stored.
     * The values must be in the same order as they are given in the first
     * constructor, which is the same order as their respective database feilds 
     * appear in the db tables.  This makes it simple to just pass a subset 
     * of each row of a result set to this constructor.
     * @param values list of values in proper order and of string type.
     */
    public BlastRecord(List values)
    {
        if(values==null || values.size()!=13)
        {
            log.error("invalid values list in BlastRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 13");
            return;
        }
        
        accId=new Integer((String)values.get(0));
        if(values.get(1)==null || values.get(1).equals("no hit")) //this is a no hit
        {
            target="no hit";
        }
        else
        {            
            target=(String)values.get(1);
            targetDesc=(String)values.get(2);
            evalue=Double.parseDouble((String)values.get(3));
            score=(String)values.get(4);
            ident=(String)values.get(5);
            length=Integer.parseInt((String)values.get(6));
            positives=(String)values.get(7);
            gaps=(String)values.get(8);
            dbname=(String)values.get(9);
            link=buildLink((String)values.get(10),target); 
            method=(String)values.get(11);
            purpose=(String)values.get(12);
        }
    }
    private String buildLink(String link,String key)
    {                
        //should no longer be neccasary
        if(dbname.equals("pfam"))  //must make sure that this name always matches the value in the db_name field of unknowns.blast_databases
        { //ugly hack to chop off end of pfam keys since the pfam search page does not accept them.
            int i=key.lastIndexOf('.');
            if(i > 0)
                key=key.substring(0,i);
        }                            
        return link.replaceAll("\\$V\\{key\\}",key );
    }
    
    public Object getPrimaryKey()
    {
        return accId;
    }
    /**
     * 
     * @param o 
     * @return 
     */
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
    /**
     * 
     * @return 
     */
    public String toString()
    {
        return "target="+target+",evalue="+evalue+",dbname="+dbname+",purpose="+purpose;
    }
    
    /**
     * Should use the given RecordVisitor to print the header
     * @param out 
     * @param visitor 
     * @throws java.io.IOException 
     */
    public void printHeader(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);       
    }
    
    /**
     * Should use the given RecordVisitor to print the data
     * @param out 
     * @param visitor 
     * @throws java.io.IOException 
     */
    public void printRecord(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);      
    }
  
    /**
     * Should use the given RecordVisitor to print the footer
     * @param out 
     * @param visitor 
     * @throws java.io.IOException 
     */
    public void printFooter(java.io.Writer out, RecordVisitor visitor) throws java.io.IOException
    {
    }
     
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{1}, 1,14){
            public Record getRecord(List l)
            {
                return new BlastRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getBlastRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
            public CompositeFormat getCompositeFormat()
            {
                return new BlastCompositeFormat();
            }
        };
    }
}

