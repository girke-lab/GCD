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
import servlets.dataViews.AffyKey;
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
    
    public static Map getDataByAcc(DbConnection dbc, Collection ids)
    {
        //wrap accession_ids in AffyKey objects for the affy records
        List affyKeys=new LinkedList();
        for(Iterator i=ids.iterator();i.hasNext();)
            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
            
        return getData(dbc,affyKeys,true,null,"asc");
    }
    public static Map getData(DbConnection dbc, Collection ids)
    {
        return getData(dbc,ids,null,"ASC");
    }
    public static Map getData(DbConnection dbc, Collection affyKeys, String sortCol, String sortDir)
    {
        return getData(dbc,affyKeys,false,sortCol,sortDir);
    }
    public static Map getData(DbConnection dbc, Collection affyKeys, boolean allGroups, String sortCol, String sortDir)
    {
        if(affyKeys==null || affyKeys.size()==0)
            return new HashMap();
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(
                        affyKeys,allGroups, sortCol,sortDir);
                
        List data=null;                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordSource rb=new RecordSource(){
            public Record buildRecord(List l){
                return new AffyDetailRecord(l);
            }
        };                
        //log.debug("affy data, data="+data);
        
        return RecordGroup.buildRecordMap(rb,data,new int[]{1,2,3},1,9);             
    }
    public static Map getRootData(DbConnection dbc, Collection ids)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        List affyKeys=new LinkedList();
        for(Iterator i=ids.iterator();i.hasNext();)
            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyDetailRecordQuery(
                        affyKeys,true,null,"asc");
                
        List data=null;                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordSource rb=new RecordSource(){
            public Record buildRecord(List l){
                return new AffyDetailRecord(l);
            }
        };                
        //log.debug("affy data, data="+data);
        
        return RecordGroup.buildRecordMap(rb,data,1,9);             
    }
}
