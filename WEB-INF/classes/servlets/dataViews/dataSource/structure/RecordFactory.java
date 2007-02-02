/*
 * RecordFactory.java
 *
 * Created on August 30, 2005, 3:25 PM
 *
 */

package servlets.dataViews.dataSource.structure;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.KeyTypeUser.KeyType;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.exceptions.InvalidPatternException;
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
    public Collection<Record> getRecords(RecordInfo ri, QueryParameters qp)
    {
        return collapse(getMap(ri,qp,KeyType.ANY).values());        
    }       
    
    /**
     * This can be used to add sub records to an existing collection of records.
     * @param records a collection of existing records
     * @param ri the RecordInfo of the type of record to add as a sub-record
     * @param qp the query parameters to use when querying the sub-records
     * @return a collection of child records. This can be used to add additional children.
     */
    public Collection<Record> addSubType(Collection<? extends Record> records, RecordInfo ri, QueryParameters qp)
    {  // find a key supported by both records and ri, then create a Map from ri and qp
       //run through each record in records, find it in subRecords, and add it the record.
        
        log.debug("adding sub type "+ri.getRecordType());
         
        if(records==null || records.size()==0)
            return null;
                
        KeyTypeUser.KeyType childKeyType=records.iterator().next().getPrimaryKeyType();
                
        if(!Common.checkKeyType(ri.getSupportedKeyTypes(),childKeyType)){
            log.error("key "+childKeyType+" not supported by given child: "+ri.createRecord(new LinkedList()).getClass());
            return null;
        }                
        
        Map<Object,Collection<Record>> subRecords=getMap(ri,qp, childKeyType);
        Collection<Record> list;
        RecordPattern parentPattern=null, childPattern=null;
        boolean parentUpdated=false;
        log.debug("sub record keys are: "+subRecords.keySet());                        
        
        for(Record r : records)
        {
            log.debug("r is a "+r.getClass());
            if(parentPattern == null)
                parentPattern=r.getPattern();
                        
                        
            list=subRecords.get(r.getPrimaryKey());
            if(list==null)            
                log.info("no sub record found with primary key "+r.getPrimaryKey());                            
            else
                for(Record sr : list)
                {
                    //need to update parent pattern before we insert any children
                    if(!parentUpdated && parentPattern!=null){
                        parentPattern.addChild(sr.getPattern());
                        parentUpdated=true;
                    }
                    r.addChildRecord(sr);                        
                }
        }       
        
        if(!parentUpdated && parentPattern!=null) // no child records
            parentPattern.addChild(new RecordPattern(ri.getRecordType()));
        
//        if(childPattern==null) //no child records, so just create our own pattern
//            childPattern=new RecordPattern(ri.getRecordType());
//        if(parentPattern!=null) // shouldn't happen, but just to be sure.
//            parentPattern.addChild(childPattern);
        
        return collapse(subRecords.values());
    }
    
    
    
    
   
    
    /////////////////////// Factory methods  /////////////////////////////////
    
    /**
     * This will return a map of record keys to record objects.
     * @param ri 
     * @param qp 
     * @param keyType 
     * @return 
     */
    private Map<Object,Collection<Record>> getMap(RecordInfo ri, QueryParameters qp,KeyTypeUser.KeyType keyType)
    { // query information and store it in a Map of CompositeRecords        
                    
        List data=null;
        try{
            data=dbc.sendQuery(ri.getQuery(qp, keyType));
        }catch(java.sql.SQLException e){
            log.error("could not send Record query: "+e.getMessage());
            return new HashMap<Object,Collection<Record>>();
        }
        
        List row;
        Collection<Record> list;
        Object key;
        Record r;
        Map<Object,Collection<Record>> output=new LinkedHashMap<Object,Collection<Record>>(); 
        RecordPattern pattern=new RecordPattern(ri.getRecordType());
        
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            key=ri.buildKey(row,keyType);
            //try to find an existing Record for this key
            list=output.get(key);
            if(list==null)
            { //build and add a new RecordGroup                 
                list=new LinkedList<Record>();
                output.put(key,list);
            }
            //create a new record from data, and add to list            
            try{                
                r=ri.createRecord(row.subList(ri.getStart(),ri.getEnd()));
                if(keyType!=null) //if we dont have a parent, don't set the key type.
                    r.setKeyType(keyType);
                
                // all records refer to the same pattern object                
                r.setPattern(pattern);                
                list.add(r);                
            }catch(UnsupportedKeyTypeException e){ 
                log.error("invalid key: "+e);
            }catch(InvalidPatternException e){
                log.error("invalid pattern: "+e);
            }   
        }
        
        log.debug("created new map, keys: "+output.keySet());
        return output;                                
    }
     
    private Collection<Record> collapse(Collection<Collection<Record>> ccr)
    {
        Collection<Record> allRecords=new LinkedList<Record>();
        
        for(Collection<Record> c : ccr)
            allRecords.addAll(c);
        
        return allRecords;     
    }      
}