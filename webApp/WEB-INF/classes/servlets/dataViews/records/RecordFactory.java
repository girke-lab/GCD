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
import servlets.querySets.QuerySetProvider;

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
    
    public Map<Object,CompositeRecord> getMap(RecordInfo ri, QueryParameters qp)
    { // query information and store it in a Map of CompositeRecords                 
        return buildMap(ri.getQuery(qp), ri, ri.getKey(), ri.getStart(),ri.getEnd()); 
    }
    public void addSubType(Collection<Record> records, RecordInfo ri, QueryParameters qp)   //Map<Object,CompositeRecord> subRecords)
    {  // find a key supported by both records and ri, then create a Map from ri and qp
       //run through each record in records, find it in subRecords, and add it the the record.
       
        
        Map<Object,CompositeRecord> subRecords=buildMap( ... );
        
        Object primaryKey;
        Record sr;
        for(Record r : records)
        {
            primaryKey=r.getPrimaryKey();
            sr=subRecords.get(primaryKey);
            if(sr==null)
                log.debug("no sub record found with primary key "+primaryKey);
            else
                r.addSubRecord(sr);
        }
    }
    
    public CompositeRecord getUnkownRecords(Collection ids)
    {
        
        return null;
    }
    public CompositeRecord getUnkownRecords(QueryParameters qp)
    {
        return null;
    }
    
    
    /////////////////////// Factory methods  /////////////////////////////////
    
    private Map<Object,CompositeRecord> buildMap(String query,RecordInfo ri,int[] key, int start, int end)
    {
        List data=null;
        try{
            data=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("could not send Record query: "+e.getMessage());
            return new HashMap<Object,CompositeRecord>();
        }
        
        List row;
        CompositeRecord rc;
        String keyStr;
        Map<Object,CompositeRecord> output=new HashMap<Object,CompositeRecord>(); 
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            keyStr=buildKey(row,key);
            //try to find an existing Record for this key
            rc=output.get(keyStr);
            if(rc==null)
            { //build and add a new RecordGroup                 
                rc=new CompositeRecord(keyStr);
                output.put(keyStr,rc);
            }
            //create a new record from data, and add to existing 
            // RecordGroup
            rc.addSubRecord(ri.getRecord(row.subList(start,end)));            
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
