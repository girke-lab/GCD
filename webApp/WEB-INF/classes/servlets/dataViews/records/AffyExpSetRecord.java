/*
 * AffyExpSetRecord.java
 *
 * Created on August 3, 2005, 2:34 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
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
public class AffyExpSetRecord implements   Record
{
    
    String probeSetKey,expSetKey,link;
    Integer up4,down4, up2, down2,on, off;
    Integer accId,probeSetId, expSetId;
    List subRecords;
    
    private static Logger log=Logger.getLogger(AffyExpSetRecord.class);
    
    
    /** Creates a new instance of AffyExpSetRecord */
    public AffyExpSetRecord(List values)
    {
        int reqSize=12;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyExpSetRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }        
        
        accId=Integer.parseInt((String)values.get(0));
        probeSetId=Integer.parseInt((String)values.get(1));
        probeSetKey=(String)values.get(2);
        expSetId=Integer.parseInt((String)values.get(3));
        expSetKey=(String)values.get(4);
        link=(String)values.get(5);
        up4=Integer.parseInt((String)values.get(6));
        down4=Integer.parseInt((String)values.get(7));
        up2=Integer.parseInt((String)values.get(8));
        down2=Integer.parseInt((String)values.get(9));
        on=Integer.parseInt((String)values.get(10));
        off=Integer.parseInt((String)values.get(11));
                
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
    
    public void addSubRecord(RecordGroup rg)
    {
        if(subRecords==null)
            subRecords=new LinkedList();
        subRecords.add(rg);
    }
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof AffyExpSetRecord))
            return false;
        return ((AffyExpSetRecord)o).probeSetKey.equals(probeSetKey) &&
               ((AffyExpSetRecord)o).expSetKey.equals(expSetKey);
    }
    public int hashCode()
    {
        return probeSetKey.hashCode()+expSetKey.hashCode();
    }
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,null,"ASC");
    }
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyExpSetRecordQuery(
                        (Collection)ids.get(AffyDataView.ACC), sortCol,sortDir);
                
        List data=null;
        Map expSetRecordsMap;
        RecordGroup expSetGroup;
                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        Map[] subRecordMaps=new Map[]{
            AffyCompRecord.getData(dbc,ids)
        };
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new AffyExpSetRecord(l);
            }
        };                
        log.debug("affy data, data="+data);
        
        
        expSetRecordsMap=RecordGroup.buildRecordMap(rb,data,0,12);           
        
        AffyExpSetRecord affyRec;
        RecordGroup rg;
        log.debug("expSetRecordsMap=\n"+expSetRecordsMap);
        log.debug("combining affy exp set sub records");
        
        for(Iterator j=expSetRecordsMap.values().iterator();j.hasNext();) 
        { //each map entry corresponds to one accession.
            for(Iterator i=((RecordGroup)j.next()).iterator();i.hasNext();)
            { //each recordGroup of each accession needs to have
                //it subrecords added
                affyRec=(AffyExpSetRecord)i.next();        
                for(Map subRecordMap : subRecordMaps)
                {//go through the sub record map add find any associated with 
                    //this RecordGroup
                    rg=(RecordGroup)subRecordMap.get(affyRec.probeSetId+"_"+affyRec.expSetId);
                    if(rg==null)
                    {
                        log.debug("no record found for "+affyRec.probeSetId+"_"+affyRec.expSetId);
                        log.debug("key list: "+subRecordMap.keySet());
                    }
                    
                    if(rg==null)
                        rg=new RecordGroup();
                    affyRec.addSubRecord(rg);
                }
            }            
        }

        log.debug("done combining");
        
        return expSetRecordsMap; //map keyed on accession_id
    }
}