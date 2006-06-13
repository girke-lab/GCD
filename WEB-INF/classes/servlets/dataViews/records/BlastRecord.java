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
import servlets.dataViews.records.CompositeFormat;
import servlets.querySets.*;

/**
 * Stores blast information from the unknowns database.
 */
public class BlastRecord extends AbstractRecord
{
    String target,targetDesc,score,ident,positives,gaps,dbname,link,method,purpose;
    int length;
    double evalue;
    Integer accId,blastId;
    
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
        if(values==null || values.size()!=14)
        {
            log.error("invalid values list in BlastRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 14");
            return;
        }
        
        
        accId=new Integer((String)values.get(1));
        if(values.get(2)==null || values.get(2).equals("no hit")) //this is a no hit
        {
            blastId=-1; //special case, if we ever need to deal with it, -1 should cause a problem somewhere so we notice.
            target="no hit";
        }
        else
        {            
            blastId=new Integer((String)values.get(0));
            target=(String)values.get(2);
            targetDesc=(String)values.get(3);
            evalue=Double.parseDouble((String)values.get(4));
            score=(String)values.get(5);
            ident=(String)values.get(6);
            length=Integer.parseInt((String)values.get(7));
            positives=(String)values.get(8);
            gaps=(String)values.get(9);
            dbname=(String)values.get(10);
            link=buildLink((String)values.get(11),target); 
            method=(String)values.get(12);
            purpose=(String)values.get(13);
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
        return blastId;
    }
    public KeyType getChildKeyType()
    {
        return KeyType.BLAST;
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

        return br.blastId.intValue()==blastId.intValue();
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
     
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{1}, 0,14){
            public Record getRecord(List l)
            {
                return new BlastRecord(l);
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {
                return QuerySetProvider.getRecordQuerySet().getBlastRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC};
            }
            public CompositeFormat getCompositeFormat()
            {
                return new BlastCompositeFormat();
            }
        };
    }
    
    static class BlastCompositeFormat extends CompositeFormat
    {

        /** Creates a new instance of BlastCompositeFormat */
        public BlastCompositeFormat() 
        {
        }

        public void printRecords(Writer out, RecordVisitor visitor,Iterable ib)
            throws IOException
        {
            BlastRecord rec;
            boolean firstRecord=true;
            String lastPurpose=null;

            Map<String,String> titles=new HashMap<String,String>(); 
            titles.put("UD","Unknown Searches");
            titles.put("orthologs","Ortholog Searches");

            Iterator i=ib.iterator();
            while(i.hasNext())
            {
                rec=(BlastRecord)i.next();
                if(rec.target.equals("no hit")) 
                    continue; //skip no hits
                if(firstRecord)
                {
                    rec.printHeader(out, visitor);
                    firstRecord=false;
                }            
                if(lastPurpose==null || !lastPurpose.equals(rec.purpose))
                {
                    out.write("<tr><th align='left' colspan='4' bgcolor='"+PageColors.title+"'>"+
                            titles.get(rec.purpose)+"</th></tr>");
                    lastPurpose=rec.purpose;
                }
                rec.printRecord(out, visitor);
                if(!i.hasNext()) //last record
                    rec.printFooter(out, visitor);
            }
        }

    }
    
}

