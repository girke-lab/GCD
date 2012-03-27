/*
 * AffyKey.java
 *
 * Created on August 16, 2005, 2:53 PM
 *
 */

package servlets.dataViews;

import java.io.Serializable;
import java.util.*;
import org.apache.log4j.Logger;

/**
 *
 * @author khoran
 */
public class AffyKey implements Serializable
{
    private Integer acc, //accession_id
                    psk, //probe_set_key_id
                    es,  //experiment_set_id
                    group; //group_no
    private static Logger log=Logger.getLogger(AffyKey.class);

    public AffyKey(Integer acc,Integer psk, Integer es, Integer group)
    {
        this.acc=acc;
        this.psk=psk;
        this.es=es;
        this.group=group;
    }
    public AffyKey(Integer acc,Integer psk, Integer es)
    {
        this.acc=acc;
        this.psk=psk;
        this.es=es;
        this.group=null;
    }
    public boolean equals(Object o)
    {
        if(this==o)
            return true;
        if(!(o instanceof AffyKey))
            return false;
        //log.debug("comparing "+this+" with "+o);
        AffyKey n=(AffyKey)o;    

        //each pair is either both null or same reference, or, if the n
        // value is not null, they are equal.
        return (n.acc==acc || (n.acc!=null && n.acc.equals(acc))) &&
               (n.psk==psk || (n.psk!=null && n.psk.equals(psk))) && 
               (n.es==es   || (n.es!=null  && n.es.equals(es)))   &&
               (n.group==group || (n.group!=null && n.group.equals(group)));
    }
    public int hashCode()
    {            
        if(psk==null || es==null)
            return acc;
        return acc+psk+es;
    }
    public String toString()
    {
        return acc+"_"+psk+"_"+es+"_"+group;
    }

    public Integer getAcc()
    {
        return acc;
    }
    public Integer getPsk()
    {
        return psk;
    }
    public Integer getEs()
    {
        return es;
    }
    public Integer getGroup()
    {
        return group;
    }
    
    public static String buildIdSetCondition(Collection<AffyKey> affyKeys,boolean includeGroup)
    {
        StringBuffer condition=new StringBuffer();
        //AffyKey af=null;
        boolean isFirst=true;
        
        log.debug("includeGroup="+includeGroup);
        log.debug("affykeys="+affyKeys);
                
        
        
        for(AffyKey af : affyKeys)
        {            
            //if we want a group, skip sets with no group,
            // if we don't want a group, skip sets with a group
            if( (includeGroup && af.getGroup()==null) ||
                (!includeGroup && af.getGroup()!=null) )                    
                continue;
            
            if(!isFirst)            
                condition.append(" OR ");                
            else
                isFirst=false;
            
            condition.append("(");
            
            //use either psk or accession, but prefer psk.
            if(af.getPsk()!=null)
                condition.append("probe_set_key_id="+af.getPsk());
            else if(af.getAcc()!=null)
                condition.append("accession_id="+af.getAcc());
            else 
            { //fail if we don't have either
                log.error("no probe_set_key_id or accession_id given");
                return "FALSE"; //prevent an improperly constrained join
            }
            if(af.getEs()!=null) //use expression_set_id if we have it
                condition.append(" AND experiment_set_id="+af.getEs());                        

            if(includeGroup) //use group if caller asked for it
                condition.append(" AND group_no="+af.getGroup()+")");
            else
                condition.append(")");
        }
        if(condition.length()==0) //no valid keys given
            condition.append("FALSE");
        
        return condition.toString();
    }
}
