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
import servlets.DbConnection;
import servlets.PageColors;
import servlets.querySets.*;

/**
 * Stores blast information from the unknowns database.
 */
public class BlastRecord extends AbstractRecord
{
    String target,targetDesc,score,ident,positives,gaps,dbname,link,method,purpose;
    int length;
    double evalue;
    
    private static Logger log=Logger.getLogger(BlastRecord.class);
    
    /**
     * Creates a new instance of BlastRecord
     */
    public BlastRecord(String t,String d,String s,String id,String pos,
            String gaps,String db,String l,String m,int len,double e,String p)
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
        purpose=p;
    }
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
        if(values==null || values.size()!=12)
        {
            log.error("invalid values list in BlastRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 12");
            return;
        }
        
        if(values.get(0)==null || values.get(0).equals("no hit")) //this is a no hit
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
            purpose=(String)values.get(11);
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
    
    /**
     * This method is used to allow BlastRecords to load themselves. The list should
     * be a list of id numbers to get information for.
     * @param dbc a database connection to the proper database
     * @param ids list of id values
     * @return a map of ids to BlastRecords.
     */
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,null,"ASC");
    }    
    /**
     * Allows one to also specify a sort field and a direction
     * @param dbc a connection to the proper db. 
     * @param ids list of ids
     * @param sortCol column name to sort by
     * @param sortDir sort direction, should be either "ASC", or "DESC".
     * @return a map of ids to BlastRecords
     */
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        
                
        String query=QuerySetProvider.getRecordQuerySet().getBlastRecordQuery(ids, sortCol, sortDir);
        List data=null;
        try{
            data=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("could not send BlastRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordSource rb=new RecordSource(){
            public Record buildRecord(List l){
                return new BlastRecord(l);
            }
            public RecordGroup buildRecordGroup(){
                return new BlastRecordGroup();
            }
        };                
        return RecordGroup.buildRecordMap(rb,data,2,14);                
    }           

    public Object getPrimaryKey()
    {
        return null;
    }

    public int[] getSupportedKeyTypes()
    {
        return getInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getInfo()
    {
        return new RecordInfo(new int[]{0}, 2,14){
            public Record getRecord(List l)
            {
                return new BlastRecord(l);
            }
            public String getQuery(QueryParameters qp)
            {
                return QuerySetProvider.getRecordQuerySet().getBlastRecordQuery(qp.getIds(),qp.getSortCol(), qp.getSortDir());
            }
            public int[] getSupportedKeyTypes()
            {
                return new int[]{Common.KEY_TYPE_ACC};
            }
        };
    }
}

class BlastRecordGroup extends RecordGroup
{
     public void printRecords(Writer out, RecordVisitor visitor)  
        throws IOException
     {
        BlastRecord rec;
        boolean firstRecord=true;
        String lastPurpose=null;
        
        Map titles=new HashMap(); 
        titles.put("UD","Unknown Searches");
        titles.put("orthologs","Ortholog Searches");
        
        
        for(Iterator i=records.iterator();i.hasNext();)
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
