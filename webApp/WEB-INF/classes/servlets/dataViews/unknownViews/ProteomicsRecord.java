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
    
}
