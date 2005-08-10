/*
 * AffyRecord.java
 *
 * Created on July 27, 2005, 11:13 AM
 * 
 */

package servlets.dataViews.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.dataViews.AffyDataView;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */



public class AffyDetailRecord implements Record 
{
//    String dataType;
//    String esKey, esDesc, esLink;
//    String expType,expNotes;
//    String repDesc,celFilename;
//    String probeSetKey, pma;
//    Integer groupNo,repNo;

    
    
    Integer probeSetId, expSetId,comparison;
    String celFile,type,description,pma;
    Float intensity;        
    
    private static Logger log=Logger.getLogger(AffyDetailRecord.class);
    
    /** Creates a new instance of AffyRecord */
    public AffyDetailRecord(List values)
    {
        int reqSize=8;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyDetailRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        
        probeSetId=new Integer((String)values.get(0));
        expSetId=new Integer((String)values.get(1));
        comparison=new Integer((String)values.get(2));
        type=(String)values.get(3);
        celFile=(String)values.get(4);
        description=(String)values.get(5);
        intensity=new Float((String)values.get(6));
        pma=(String)values.get(7);
        
                
//        esKey=(String)values.get(0);
//        esDesc=(String)values.get(1);
//        esLink=(String)values.get(2);
//        groupNo=Integer.parseInt((String)values.get(3));
//        expType=(String)values.get(4);
//        expNotes=(String)values.get(5);
//        repDesc=(String)values.get(6);
//        repNo=Integer.parseInt((String)values.get(7));
//        celFilename=(String)values.get(8);
//        probeSetKey=(String)values.get(9);
//        intensity=Float.parseFloat((String)values.get(10));
//        pma=(String)values.get(11);
//        dataType=(String)values.get(12);
        
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
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(
                        (Collection)ids.get(AffyDataView.PSK),(Collection)ids.get(AffyDataView.ES),
                        (Collection)ids.get(AffyDataView.GROUP),  sortCol,sortDir);
                
        List data=null;
                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new AffyDetailRecord(l);
            }
        };                
        log.debug("affy data, data="+data);
        
        return RecordGroup.buildRecordMap(rb,data,new int[]{0,1,2},0,8);             
    }
}
