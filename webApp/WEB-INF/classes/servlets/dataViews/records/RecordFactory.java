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
 * This is a singleton
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
    
    public static RecordFactory getInstance()
    {
        if(factory==null)
            factory=new RecordFactory();
        return factory;
    }
    public void setDbConnection(DbConnection dbc)
    {
        this.dbc=dbc;
    }
    
    public Collection<CompositeRecord> getRecords(RecordInfo ri, QueryParameters qp)
    {
        return getMap(ri,qp, Common.KEY_TYPE_DEFAULT).values();
    }       
    
    public Collection<CompositeRecord> addSubType(Collection<? extends Record> records, RecordInfo ri, QueryParameters qp)
    {  // find a key supported by both records and ri, then create a Map from ri and qp
       //run through each record in records, find it in subRecords, and add it the record.
        
        if(records==null || records.size()==0)
            return null;
                
        int childKeyType=records.iterator().next().getChildKeyType();
                
        if(!Common.checkKeyType(ri.getSupportedKeyTypes(),childKeyType)){
            log.error("key "+childKeyType+" not supported by given child: "+ri.getRecord(new LinkedList()).getClass());
            return null;
        }                
        
        Map<Object,CompositeRecord> subRecords=getMap(ri,qp, childKeyType);

        //log.debug("sub record keys are: "+subRecords.keySet());
        
        Object primaryKey;
        Record sr,r2;
        for(Record r : records)
        {
            //log.debug("r is a "+r.getClass());
            if(r instanceof CompositeRecord) //decend into composites
                for(Iterator i=r.iterator();i.hasNext();)
                {
                    r2=(Record)i.next();
//                    log.debug("r2 is a "+r2.getClass());
                    primaryKey=r2.getPrimaryKey();
//                    log.debug("primary key is a "+primaryKey.getClass());
//                    log.debug("looking for primary key "+primaryKey);
                    sr=subRecords.get(primaryKey);
                    if(sr==null)
                        log.info("no sub record found with primary key "+primaryKey);
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
    
    public Map<Object,CompositeRecord> getMap(RecordInfo ri, QueryParameters qp,int keyType)
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
        String keyStr;
        Record r;
        Map<Object,CompositeRecord> output=new HashMap<Object,CompositeRecord>(); 
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            keyStr=buildKey(row,ri.getKeyIndecies(keyType));
            //try to find an existing Record for this key
            cr=output.get(keyStr);
            if(cr==null)
            { //build and add a new RecordGroup                 
                cr=new CompositeRecord(keyStr, ri.getCompositeFormat());
                output.put(keyStr,cr);
            }
            //create a new record from data, and add to existing 
            // RecordGroup
            try{                
                r=ri.getRecord(row.subList(ri.getStart(),ri.getEnd()));
                r.setKeyType(keyType);
                cr.addSubRecord(r);            
            }catch(UnsupportedKeyTypeException e){ 
                log.error("invalid key: "+e);
            }   
        }
        return output;                                
    }
    private String buildKey(List data,int[] indecies)
    {
        if(indecies==null || indecies.length==0)
            return "";
        else if(indecies.length==1)
            return (String)data.get(indecies[0]);
        
        
        StringBuffer key=new StringBuffer();
        for(int i=0;i<indecies.length;i++)
        {
            key.append(data.get(indecies[i]));
            if(i+1 < indecies.length) //we have at least one more iteration
                key.append("_");
        }        
        return key.toString();
    }
}
