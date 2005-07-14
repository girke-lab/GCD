/*
 * UnknownRecord.java
 *
 * Created on October 12, 2004, 1:54 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.querySets.*;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class UnknownRecord implements Record
{
    String key,description;
    int estCount,key_id;
    boolean[] go_unknowns;

    Map subRecords=new LinkedHashMap();
    private static Logger log=Logger.getLogger(UnknownRecord.class);

    public UnknownRecord(int key_id,String key, String desc, int estCount, String[] go_unknowns)
    {
        this.key_id=key_id;
        this.key=key;
        this.description=desc;
        this.estCount=estCount;
        this.go_unknowns=new boolean[3];
        this.go_unknowns[0]=getBoolean(go_unknowns[0]);
        this.go_unknowns[1]=getBoolean(go_unknowns[1]);
        this.go_unknowns[2]=getBoolean(go_unknowns[2]);        
    }
    public UnknownRecord(List values)
    {
        if(values==null || values.size()!=7)
        {
            log.error("invalid list in UnknownRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of 6");
            return;
        }
        key_id=Integer.parseInt((String)values.get(0));
        key=(String)values.get(1);
        description=(String)values.get(2);
        estCount=Integer.parseInt((String)values.get(3));
        go_unknowns=new boolean[3];
        go_unknowns[0]=getBoolean((String)values.get(4));
        go_unknowns[1]=getBoolean((String)values.get(5));
        go_unknowns[2]=getBoolean((String)values.get(6));
    }
    private boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
   
    public void setSubRecord(String name,Object o)
    {
        //log.debug("adding sub record: "+o);
        subRecords.put(name, o);
    }
    
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof UnknownRecord))
            return false;
        return ((UnknownRecord)o).key.equals(key);
    }
    public int hashCode()
    {
        return key.hashCode();
    }
  
    public String toString()
    {
        StringBuffer out=new StringBuffer();
        out.append("<PRE>\n"+key+"\n");
        for (Iterator i=subRecords.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();
            out.append(e.getKey()+"\n");
            out.append(e.getValue()+"\n");
        }
        out.append("</PRE>");
        return out.toString();
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
        log.debug("getting data for unknownRecords");
        Map unknownRecords,t;
        RecordGroup unknownRG;
        if(ids==null || ids.size()==0)
            return new HashMap();

        Map[] subRecordMaps=new Map[]{
            GoRecord.getData(dbc,ids),
            BlastRecord.getData(dbc,ids),            
            ProteomicsRecord.getData(dbc,ids),
            ClusterRecord.getData(dbc,ids),
            ExternalUnknownRecord.getData(dbc,ids)
        };//array of maps of ids to RecordGroups
        
        log.debug("got data for all sub records");
        
        //load the unknown records
        String query=QuerySetProvider.getRecordQuerySet().getUnknownRecordQuery(ids, sortCol, sortDir);
        
        List data=null;
        try{
            log.debug("sending query for data");
            data=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("could not send unknownRecord query: "+e.getMessage());
            return new HashMap();
        }
        log.debug("parsing data and building a RecordGroup");
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new UnknownRecord(l);
            }
        };                
        //this will return a map with one mapping to a RecordGroup (the root)
        unknownRecords=RecordGroup.buildRecordMap(rb,data,1,8);  
        
        if(unknownRecords==null)
            log.debug("unknownRecords is null");
        else
            log.debug("unknownRecords="+unknownRecords);
        unknownRG=(RecordGroup)unknownRecords.get("1");
        if(unknownRG==null)
            log.debug("unknownRG is null");
        else 
            log.debug("unknownRG="+unknownRG);
        
        //these names must appear in the same order as the subRecordMaps array
        String[] names=new String[]{"go_numbers","blast_results","proteomics","clusters","externals"};
            
        log.debug("matching up child records with parent records");
        UnknownRecord ur;
        Object o;
        for(Iterator i=unknownRG.iterator();i.hasNext();) 
        {//match up the child records to the parent records
            ur=(UnknownRecord)i.next();
            for(int j=0;j<subRecordMaps.length;j++)
            {
                o=subRecordMaps[j].get(""+ur.key_id);
//                if(o==null)
//                {
//                    log.debug("could not find key "+ur.key_id+" for dataset "+names[j]);
//                    log.debug(" in list "+subRecordMaps[j].keySet());
//                }

                if(o==null)
                    o=new RecordGroup();                    
                
                ur.setSubRecord(names[j],o);               
            }
        }
        log.debug("all done with UnknownRecords");
        return unknownRecords;                
    }          
}
