/*
 * GoRecord.java
 *
 * Created on October 12, 2004, 3:16 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.DbConnection;

/**
 * see docs for <CODE>BlastRecord</CODE>, everything is very similar.
 */
public class GoRecord implements Record
{
    String go_number,text,function;
    
    private static Logger log=Logger.getLogger(GoRecord.class);
    
    /** Creates a new instance of GoRecord */
    public GoRecord(String go_number,String text,String function)
    {
        this.go_number=go_number;
        this.text=text;
        this.function=function;
    }
    public GoRecord(List values)
    {
        if(values==null || values.size()!=3)
        {
            log.error("invalid values list in GoRecord constructor");
            return;
        }
        go_number=(String)values.get(0);
        function=(String)values.get(1);
        text=(String)values.get(2);
        
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof GoRecord))
            return false;
        return ((GoRecord)o).go_number.equals(go_number);
    }
    public int hashCode()
    {
        return go_number.hashCode();
    }
    public String toString()
    {
        return go_number+" "+text;
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
    }
    
    public static Map getData(DbConnection dbc, List ids)
    {
        return getData(dbc,ids,"go_number","ASC");
    }
    
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        String query="SELECT  key_id, go_number,function,text"+                 
        "   FROM unknowns.unknown_keys as uk, go.go_numbers as gn, go.seq_gos as sg " +        
        "   WHERE substring(uk.key from 1 for 9)=sg.accession AND sg.go_id=gn.go_id \n" +
        "      AND "+Common.buildIdListCondition("key_id",ids)+
        "   ORDER BY "+sortCol+" "+sortDir;
        
        List data=null;
        try{
            data=dbc.sendQuery(query);
        }catch(java.sql.SQLException e){
            log.error("could not send GoRecord query: "+e.getMessage());
            return new HashMap();
        }
        
        RecordBuilder rb=new RecordBuilder(){
            public Record buildRecord(List l){
                return new GoRecord(l);
            }
        };                
        return RecordGroup.buildRecordMap(rb,data,1,4);     
        
//        List row,l;
//        Map output=new HashMap(); //need to maintain order here
//        for(Iterator i=data.iterator();i.hasNext();)
//        {
//            row=(List)i.next();
//            l=(List)output.get(row.get(0));
//            if(l==null)
//            {
//                l=new LinkedList();
//                output.put(row.get(0),l);
//            }
//            l.add(new GoRecord(row.subList(1,4)));            
//        }
//        return output;
    }      
    
}
