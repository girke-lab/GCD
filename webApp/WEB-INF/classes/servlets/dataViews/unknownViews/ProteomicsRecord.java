/*
 * ProteomicsRecord.java
 *
 * Created on October 26, 2004, 3:14 PM
 */

package servlets.dataViews.unknownViews;

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
public class ProteomicsRecord implements Record
{
    float mol_weight,ip,charge,prob;
    boolean prob_is_neg;
    private static Logger log=Logger.getLogger(ProteomicsRecord.class);
        
    /** Creates a new instance of ProteomicsRecord */
    public ProteomicsRecord(float mol_weight,float ip,float charge, float prob,boolean isNeg)
    {
        this.mol_weight=mol_weight;
        this.ip=ip;
        this.charge=charge;
        this.prob=prob;
        this.prob_is_neg=isNeg;
    }
    public ProteomicsRecord(List values)
    {
         if(values==null || values.size()!=5)
        {
            log.error("invalid list in ProteomicsRecord constructor");
            return;
        }
         mol_weight=Float.parseFloat((String)values.get(0));
         ip=Float.parseFloat((String)values.get(1));
         charge=Float.parseFloat((String)values.get(2));
         prob=Float.parseFloat((String)values.get(3));
         prob_is_neg=getBoolean((String)values.get(4));
    }
    private boolean getBoolean(String str)
    {
        return str.compareToIgnoreCase("true")==0 || str.compareToIgnoreCase("yes")==0 ||
                str.compareToIgnoreCase("t")==0|| str.equals("1");
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof ProteomicsRecord))
            return false;
        ProteomicsRecord rec=(ProteomicsRecord)o;
        return mol_weight==rec.mol_weight && ip==rec.ip &&
               charge==rec.charge && prob==rec.prob &&
               prob_is_neg==rec.prob_is_neg;                
    }
    public int hashCode()
    {
        return new Integer((int)(mol_weight+charge+prob+ip)).hashCode();
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
        return getData(dbc,ids,"","");
    }     
    public static Map getData(DbConnection dbc, List ids, String sortCol, String sortDir)
    {
        if(ids==null || ids.size()==0)
            return new HashMap();
        String query="SELECT * "+                 
        "   FROM unknowns.proteomics_stats " +        
        "   WHERE "+Common.buildIdListCondition("key_id",ids);
        
        List data=null;
        try{            
            data=dbc.sendQuery(query);            
        }catch(java.sql.SQLException e){
            log.error("could not send ProteomicsRecord query: "+e.getMessage());
            return new HashMap();
        }
        List row,l;
        Map output=new HashMap(); //need to maintain order here
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(List)i.next();
            l=(List)output.get(row.get(1));
            if(l==null)
            {
                l=new LinkedList();
                output.put(row.get(1),l);
            }
            l.add(new ProteomicsRecord(row.subList(2,7)));            
        }
        return output;
    } 
    
}
