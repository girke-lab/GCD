/*
 * AffyRecord.java
 *
 * Created on July 27, 2005, 11:13 AM
 * 
 */

package servlets.dataViews.records;

/**
 *
 * @author khoran
 */
import java.util.*;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.querySets.QuerySetProvider;


public class AffyRecord implements Record
{
    String dataType;
    String esKey, esDesc, esLink;
    String expType,expNotes;
    String repDesc,celFilename;
    String probeSetKey, pma;
    Integer groupNo,repNo;
    Float intensity;
    
        
    
    private static Logger log=Logger.getLogger(AffyRecord.class);
    
    /** Creates a new instance of AffyRecord */
    public AffyRecord(List values)
    {
        int reqSize=13;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in affyRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
                
        esKey=(String)values.get(0);
        esDesc=(String)values.get(1);
        esLink=(String)values.get(2);
        groupNo=Integer.parseInt((String)values.get(3));
        expType=(String)values.get(4);
        expNotes=(String)values.get(5);
        repDesc=(String)values.get(6);
        repNo=Integer.parseInt((String)values.get(7));
        celFilename=(String)values.get(8);
        probeSetKey=(String)values.get(9);
        intensity=Float.parseFloat((String)values.get(10));
        pma=(String)values.get(11);
        dataType=(String)values.get(12);
        
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
        visitor.printFooter(out,this); 
    }
    
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,null,"ASC");
    }
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyRecordQuery(ids, sortCol,sortDir);
                
        List data=null;
                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new AffyRecord(l);
            }
        };                
        log.debug("affy data, data="+data);
        return RecordGroup.buildRecordMap(rb,data,1,14);             
    }
}
