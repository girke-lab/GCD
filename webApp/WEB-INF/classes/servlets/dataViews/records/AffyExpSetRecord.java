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
import servlets.*;
import servlets.querySets.QuerySetProvider;
import servlets.dataViews.AffyKey;
/**
 *
 * @author khoran
 */
public class AffyExpSetRecord implements   Record
{
    
    String probeSetKey,expSetKey,link;
    String catagory,name,description;
    Integer up4,down4, up2, down2,on, off;
    Integer accId,probeSetId, expSetId;
    List subRecords;
    WebColor rowColor;
    
    private static Logger log=Logger.getLogger(AffyExpSetRecord.class);
    
    
    /** Creates a new instance of AffyExpSetRecord */
    public AffyExpSetRecord(List values)
    {
        int reqSize=15;
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
        catagory=(String)values.get(5);
        name=(String)values.get(6);
        description=(String)values.get(7);
        link=(String)values.get(8);
        up4=Integer.parseInt((String)values.get(9));
        down4=Integer.parseInt((String)values.get(10));
        up2=Integer.parseInt((String)values.get(11));
        down2=Integer.parseInt((String)values.get(12));
        on=Integer.parseInt((String)values.get(13));
        off=Integer.parseInt((String)values.get(14));
                
        if(catagory.toLowerCase().startsWith("biotic"))
            rowColor=PageColors.biotic;
        else if(catagory.toLowerCase().startsWith("abiotic"))
            rowColor=PageColors.abiotic;
        else if(catagory.toLowerCase().startsWith("development"))
            rowColor=PageColors.development;
        else
            rowColor=PageColors.data;
        
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
    
    public static Map getData(DbConnection dbc, Collection ids,String sortCol, String sortDir)
    {
        return getData(dbc,ids,new LinkedList(),sortCol, sortDir);
    }
//    public static Map getData(DbConnection dbc, Collection ids, Collection affyKeys)
//    {
//        return getData(dbc,ids,affyKeys,null, "ASC");
//    }
    public static Map getData(DbConnection dbc, Collection ids)
    {
        //wrap accession_ids in AffyKey objects for the affy records
        List affyKeys=new LinkedList();
        for(Iterator i=ids.iterator();i.hasNext();)
            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
            
        return getData(dbc,ids,affyKeys,true,null,"asc");
    }
    public static Map getData(DbConnection dbc, Collection ids, Collection affyKeys, String sortCol, String sortDir)
    {
        return getData(dbc,ids,affyKeys,false,sortCol,sortDir);
    }
    public static Map getData(DbConnection dbc, Collection ids, Collection affyKeys, boolean allGroups, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyExpSetRecordQuery(ids,sortCol,sortDir);
                
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
            AffyCompRecord.getData(dbc,affyKeys, allGroups, sortCol, sortDir)
        };
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new AffyExpSetRecord(l);
            }
        };                
        
        
        
        expSetRecordsMap=RecordGroup.buildRecordMap(rb,data,0,15);           
        
        AffyExpSetRecord affyRec;
        RecordGroup rg;
        //log.debug("expSetRecordsMap=\n"+expSetRecordsMap);
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
//                    if(rg==null)
//                    {
//                        log.debug("no record found for "+affyRec.probeSetId+"_"+affyRec.expSetId);
//                        log.debug("key list: "+subRecordMap.keySet());
//                    }
                    
                    if(rg==null)
                        rg=new RecordGroup();
                    affyRec.addSubRecord(rg);
                }
            }            
        }

        log.debug("done combining");
        
        return expSetRecordsMap; //map keyed on accession_id
    }
    public static Map getRootData(DbConnection dbc, Collection ids)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyExpSetRecordQuery(ids,null, "asc");
                
        List data=null;
        Map expSetRecordsMap;
        RecordGroup expSetGroup;
                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }              
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new AffyExpSetRecord(l);
            }
        };                
        
        expSetRecordsMap=RecordGroup.buildRecordMap(rb,data,0,15);   
        
        return expSetRecordsMap;
    }
}