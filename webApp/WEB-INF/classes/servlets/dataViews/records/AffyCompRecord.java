/*
 * AffyCompRecord.java
 *
 * Created on August 3, 2005, 2:35 PM
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
public class AffyCompRecord implements Record
{
    
    String probeSetKey,expSetKey,description,controlPMA,treatmentPMA;
    Integer comparison;
    Float controlMean, treatmentMean,ratio;
    Integer probeSetId, expSetId;
    List subRecords;
    
    private static Logger log=Logger.getLogger(AffyCompRecord.class);        
    
    
    /** Creates a new instance of AffyCompRecord */
    public AffyCompRecord(List values)
    {
        int reqSize=11;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in AffyCompRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        probeSetId=new Integer((String)values.get(0));
        probeSetKey=(String)values.get(1);
        expSetId=new Integer((String)values.get(2));
        expSetKey=(String)values.get(3);
        description=(String)values.get(4);
        comparison=new Integer((String)values.get(5));
        controlMean=new Float((String)values.get(6));
        treatmentMean=new Float((String)values.get(7));
        controlPMA=(String)values.get(8);
        treatmentPMA=(String)values.get(9);
        ratio=new Float((String)values.get(10));
        
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
        if(!(o instanceof AffyCompRecord))
            return false;
        return ((AffyCompRecord)o).probeSetId.equals(probeSetId) &&
               ((AffyCompRecord)o).expSetId.equals(expSetId) &&
               ((AffyCompRecord)o).comparison.equals(comparison);
    }
    public int hashCode()
    {
        return probeSetKey.hashCode()+expSetKey.hashCode()+
                comparison.hashCode();
    }
    
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,null,"ASC");
    }
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        
        String query=QuerySetProvider.getRecordQuerySet().getAffyCompRecordQuery(
                        (Collection)ids.get(AffyDataView.PSK),(Collection)ids.get(AffyDataView.ES), sortCol,sortDir);
                
        List data=null;
                
        try{        
            data=dbc.sendQuery(query);        
        }catch(java.sql.SQLException e){
            log.error("could not send AffyRecord query: "+e.getMessage());
            return new HashMap();
        }
        Map[] subRecordMaps=new Map[]{
            AffyDetailRecord.getData(dbc,ids)
        };
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new AffyCompRecord(l);
            }
        };                
        
        log.debug("affy data, data="+data);        
        Map rgMap= RecordGroup.buildRecordMap(rb,data,new int[]{1,3},1,12);             
        
        log.debug("rgMap=\n"+rgMap);
        RecordGroup rg;
        AffyCompRecord compRecord;
        log.debug("matching up affyComp records");
        for(Iterator j=rgMap.values().iterator();j.hasNext();)
        { //for each RecordGroup in this map
            for(Iterator i=((RecordGroup)j.next()).iterator();i.hasNext();)
            { //for each record in this RecordGroup (should be AffyDetailRecords)
                compRecord=(AffyCompRecord)i.next();
                for(Map subRecordMap : subRecordMaps)
                {//go through the sub record map add find any associated with 
                    //this RecordGroup
                    rg=(RecordGroup)subRecordMap.get(compRecord.probeSetId+"_"+compRecord.expSetId+
                            "_"+compRecord.comparison);     
                    //if(rg==null)
                      //  log.debug("no record found for "+compRecord.probeSetId+"_"+compRecord.expSetId+
                        //    "_"+compRecord.comparison);

                    if(rg==null)
                        rg=new RecordGroup();
                    compRecord.addSubRecord(rg);
                }
            }
        }
        log.debug("done with comp records");
        
        
        return rgMap; //map keyed on probeSetId_expSetId
    }
}
