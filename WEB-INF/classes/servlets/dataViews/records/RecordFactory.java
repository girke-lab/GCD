/*
 * RecordFactory.java
 *
 * Created on August 30, 2005, 3:25 PM
 *
 */

package servlets.dataViews.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.exceptions.UnsupportedKeyTypeException;

/**
 * Used to put together Record objects.
 * This is a singleton.
 * 
 * Some examples:
 * <PRE>
 *        Collection unknowns ;
 *        RecordFactory f=RecordFactory.getInstance();
 *        QueryParameters qp=new QueryParameters(ids);
 *        qp.setUserName(userName);
 * 
 *        unknowns=f.getRecords(UnknownRecord.getRecordInfo(), qp);
 *        f.addSubType(unknowns,GoRecord.getRecordInfo(),qp); 
 *        f.addSubType(unknowns,BlastRecord.getRecordInfo(),qp);    
 * </PRE>
 * This will create a root set of UnknownRecords, and then add
 * a set of GoRecords as children, and a set of BlastRecords as children.
 * 
 * You can also add sub-sub-records:
 * <PRE>
 *        unknowns=f.getRecords(UnknownRecord.getRecordInfo(),new QueryParameters(accIds));
 *        f.addSubType(
 *            f.addSubType(
 *                f.addSubType(
 *                    unknowns,  
 *                    AffyExpSetRecord.getRecordInfo(), qp
 *                ),
 *                AffyCompRecord.getRecordInfo(),qp
 *            ), 
 *            AffyDetailRecord.getRecordInfo(), qp
 *        );
 * </PRE>
 * This will start with unknowns at the root, then add AffyExpSetRecords as children.
 * Then a set of AffyCompRecords will be added under the AffyExpSetRecords, and then
 * a set of AffyDetailRecords under the AffyCompRecords, creating a 4 level tree.
 * @author khoran
 */
public class RecordFactory
{
    private static RecordFactory factory=null;
    private static Logger log=Logger.getLogger(RecordFactory.class);    
        
    
    private DbConnection dbc=null;
    
    private RecordFactory()
    {
        // default database connection
        dbc=DbConnectionManager.getConnection("khoran");
    }
    
    /**
     * Returns the instance of this class
     * @return the instance of this class
     */
    public static RecordFactory getInstance()
    {
        if(factory==null)
            factory=new RecordFactory();
        return factory;
    }
    /**
     * Sets a new database connection to use for retrieving
     * record data. This should not normally be used. Also note that since
     * this is a static class, this change will affect all records across all sessions.
     * @param dbc a new database connection
     */
    public void setDbConnection(DbConnection dbc)
    {
        this.dbc=dbc;
    }
    
    /**
     * retrieves a list of record objects of the given type using the
     * given query parameters. This should be used first for the root record type.
     * @param ri The {@link RecordInfo} object of the desired {@link Record} object.
     * @param qp query parameters to use to get the data
     * @return a collection of records ( possibly {@link CompositeRecord}s)
     */           //CompositeRecord
    public Collection<CompositeRecord> getRecords(RecordInfo ri, QueryParameters qp)
    {
        return getMap(ri,qp, null).values();
    }       
    
    /**
     * This can be used to add sub records to an existing collection of records.
     * @param records a collection of existing records
     * @param ri the RecordInfo of the type of record to add as a sub-record
     * @param qp the query parameters to use when querying the sub-records
     * @return a collection of child records. This can be used to add additional children.
     */
     public Collection<CompositeRecord> addSubType(Collection<? extends Record> records, RecordInfo ri, QueryParameters qp)
    {  // find a key supported by both records and ri, then create a Map from ri and qp
       //run through each record in records, find it in subRecords, and add it the record.
        
        if(records==null || records.size()==0)
            return null;
                
        KeyTypeUser.KeyType childKeyType=records.iterator().next().getChildKeyType();
                
        if(!Common.checkKeyType(ri.getSupportedKeyTypes(),childKeyType)){
            log.error("key "+childKeyType+" not supported by given child: "+ri.getRecord(new LinkedList()).getClass());
            return null;
        }                
        
        Map<Object,CompositeRecord> subRecords=getMap(ri,qp, childKeyType);

        log.debug("sub record keys are: "+subRecords.keySet());
        
        Object primaryKey;
        Record sr,r2;
        for(Record r : records)
        {
            log.debug("r is a "+r.getClass());
            if(r instanceof CompositeRecord) //decend into composites
                for(Iterator i=r.iterator();i.hasNext();)
                {
                    r2=(Record)i.next();
                    log.debug("r2 is a "+r2.getClass());
                    primaryKey=r2.getPrimaryKey();
                    log.debug("primary key is a "+primaryKey.getClass());
                    log.debug("looking for primary key "+primaryKey);
                    sr=subRecords.get(primaryKey);
                    if(sr==null)
                        log.debug("no sub record found with primary key "+primaryKey);
                    else
                        r2.addSubRecord(sr);

                }
            else
            {
                primaryKey=r.getPrimaryKey();
                sr=subRecords.get(primaryKey);
                if(sr==null)
                    log.debug("no sub record found with primary key "+primaryKey);
                else
                    r.addSubRecord(sr);
            }
                
            
        }
        return subRecords.values();
    }
    
    
    
    
   
    
    /////////////////////// Factory methods  /////////////////////////////////
    
    /**
     * This will return a map of record keys to record objects.
     * @param ri 
     * @param qp 
     * @param keyType 
     * @return 
     */
    private Map<Object,CompositeRecord> getMap(RecordInfo ri, QueryParameters qp,KeyTypeUser.KeyType keyType)
    { // query information and store it in a Map of CompositeRecords        
                    
        List data=null;
        try{
            data=dbc.sendQuery(ri.getQuery(qp, keyType));
        }catch(java.sql.SQLException e){
            log.error("could not send Record query: "+e.getMessage());
            return new HashMap<Object,CompositeRecord>();
        }
        
        List row;
        CompositeRecord cr;
        Object key;
        Record r;
        Map<Object,CompositeRecord> output=new LinkedHashMap<Object,CompositeRecord>(); 
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            key=ri.buildKey(row,keyType);
            //try to find an existing Record for this key
            cr=output.get(key);
            if(cr==null)
            { //build and add a new RecordGroup                 
                cr=new CompositeRecord(key, ri.getCompositeFormat());
                output.put(key,cr);
            }
            //create a new record from data, and add to existing 
            // RecordGroup
            try{                
                r=ri.getRecord(row.subList(ri.getStart(),ri.getEnd()));
                if(keyType!=null) //if we dont have a parent, don't set the key type.
                    r.setKeyType(keyType);
                cr.addSubRecord(r);            
            }catch(UnsupportedKeyTypeException e){ 
                log.error("invalid key: "+e);
            }   
        }
        
        log.debug("created new map, keys: "+output.keySet());
        return output;                                
    }
     
     
     
     
     
   
   
}

/*
 public Collection<Record> addSubType(Collection<? extends Record> records, RecordInfo ri, QueryParameters qp)
    {  // find a key supported by both records and ri, then create a Map from ri and qp
       //run through each record in records, find it in subRecords, and add it the record.
        
        if(records==null || records.size()==0)
            return null;
                
        KeyTypeUser.KeyType childKeyType=records.iterator().next().getChildKeyType();
                
        if(!Common.checkKeyType(ri.getSupportedKeyTypes(),childKeyType)){
            log.error("key "+childKeyType+" not supported by given child: "+ri.getRecord(new LinkedList()).getClass());
            return null;
        }                
        
        Map<Object,Record> subRecords=getMap(ri,qp, childKeyType);

        log.debug("sub record keys are: "+subRecords.keySet());
        
        Object primaryKey;
        Record sr,r2;
        for(Record r : records)
        {
            log.debug("r is a "+r.getClass());
            if(r instanceof CompositeRecord) //decend into composites
                for(Iterator i=r.iterator();i.hasNext();)
                {
                    r2=(Record)i.next();
                    log.debug("r2 is a "+r2.getClass());
                    primaryKey=r2.getPrimaryKey();
                    log.debug("primary key is a "+primaryKey.getClass());
                    log.debug("looking for primary key "+primaryKey);
                    sr=subRecords.get(primaryKey);
                    if(sr==null)
                        log.debug("no sub record found with primary key "+primaryKey);
                    else
                        r2.addSubRecord(sr);

                }
            else
            {
                primaryKey=r.getPrimaryKey();
                sr=subRecords.get(primaryKey);
                if(sr==null)
                    log.debug("no sub record found with primary key "+primaryKey);
                else
                    r.addSubRecord(sr);
            }
                
            
        }
        return subRecords.values();
    }
  
    private Map<Object,Record> getMap(RecordInfo ri, QueryParameters qp,KeyTypeUser.KeyType keyType)
    { // query information and store it in a Map of CompositeRecords        
                    
        List data=null;
        try{
            data=dbc.sendQuery(ri.getQuery(qp, keyType));
        }catch(java.sql.SQLException e){
            log.error("could not send Record query: "+e.getMessage());
            return new HashMap<Object,Record>();
        }
        
        List row;
        CompositeRecord cr;
        Object key;
        Record r,temp;
        Map<Object,Record> output=new LinkedHashMap<Object,Record>(); 
        
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            key=ri.buildKey(row,keyType);
            

            //create a new record from data, and add to existing 
            // RecordGroup
            try{                
                r=ri.getRecord(row.subList(ri.getStart(),ri.getEnd()));
                if(keyType!=null) //if we dont have a parent, don't set the key type.
                    r.setKeyType(keyType);
            }catch(UnsupportedKeyTypeException e){ 
                log.error("invalid key: "+e);
                continue;
            }               

            //try to find an existing Record for this key
            
            temp=output.get(key);
            if(temp==null) // record not already there
            {
                output.put(key,r);
            }
            else if(temp instanceof CompositeRecord)
                temp.addSubRecord(r);
            else // we have more than one, so move it into a composite
            {
                cr=new CompositeRecord(key,ri.getCompositeFormat());
                cr.addSubRecord(temp);
                cr.addSubRecord(r);
                output.put(key,cr);
            }
        }
        
        log.debug("created new map, keys: "+output.keySet());
        return output;                                
    }
 
 */


