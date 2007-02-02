/*
 * AffyCompRecord.java
 *
 * Created on August 3, 2005, 2:35 PM
 * 
 */

package servlets.dataViews.dataSource.records;

import java.util.*;
import org.apache.log4j.Logger;
import servlets.KeyTypeUser.KeyType;
import servlets.PageColors;
import servlets.dataViews.AffyKey;
import servlets.dataViews.dataSource.*;
import servlets.dataViews.dataSource.display.CompositeFormat;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordInfo;
import servlets.dataViews.dataSource.structure.UniChildRecord;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class AffyCompRecord extends UniChildRecord
{
    
    public String probeSetKey,expSetKey,controlPMA,treatmentPMA;
    public Integer comparison;
    public Float controlMean, treatmentMean,ratio;
    public Float contrast, pValue, adjPValue, pfpUp,pfpDown;
    public Integer accId, probeSetId, expSetId;
    
    public String controlDesc,treatDesc;
    
    private static Logger log=Logger.getLogger(AffyCompRecord.class);    
    private static int reqSize=19;    
    
    
    /** Creates a new instance of AffyCompRecord */
    public AffyCompRecord(List values)
    {                
        
        if(!checkList(this.getRecordInfo().getRecordType().getName(),reqSize,values))
            return;        
        
        accId=new Integer((String)values.get(0));
        probeSetId=new Integer((String)values.get(1));
        probeSetKey=(String)values.get(2);
        expSetId=new Integer((String)values.get(3));
        expSetKey=(String)values.get(4);        
        comparison=new Integer((String)values.get(5));
        controlMean=new Float((String)values.get(6));
        treatmentMean=new Float((String)values.get(7));
        controlPMA=(values.get(8)==null? "" : (String)values.get(8));        
        treatmentPMA=(values.get(9)==null? "" : (String)values.get(9));        
        ratio=new Float((String)values.get(10));
        
        contrast=new Float((String)values.get(12));
        pValue=new Float((String)values.get(13));
        adjPValue=new Float((String)values.get(14));
        pfpUp=new Float((String)values.get(15));
        pfpDown=new Float((String)values.get(16));
        
        controlDesc=(String)values.get(17);
        treatDesc=(String)values.get(18);
        
    }
    public Object getPrimaryKey()
    {
        return probeSetId+"_"+expSetId+"_"+comparison;
    }
    public KeyType getPrimaryKeyType()
    {
        return KeyType.PSK_EXP_COMP;
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
               
    
    public KeyType[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(0,reqSize){
            public Record createRecord(List l)
            { 
                return new AffyCompRecord(l);
            }
            public Class getRecordType()
            {
                return AffyCompRecord.class;
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
            {                
                switch(keyType){
                    case ACC:
                        Collection<AffyKey> affyKeys=new LinkedList<AffyKey>();
                        for(Iterator i=qp.getIds().iterator();i.hasNext();)
                            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
                        return QuerySetProvider.getRecordQuerySet().getAffyCompRecordQuery(
                                    affyKeys,qp.getDataType(), qp.getSortCol(), qp.getSortDir(),qp.getUserName());        
                    case PSK_EXP:
                        return QuerySetProvider.getRecordQuerySet().getAffyCompRecordQuery(
                                    qp.getAffyKeys(),qp.getDataType(), qp.getSortCol(), qp.getSortDir(),qp.getUserName());
                    default:
                        return null;
                }                
            }
            public KeyType[] getSupportedKeyTypes()
            {
                return new KeyType[]{KeyType.ACC,KeyType.PSK_EXP};
            }
            public int[] getKeyIndecies(KeyType keyType)
            {
                switch(keyType)
                {
                    case ACC:
                        return new int[]{0};
                    case PSK_EXP:
                        return new int[]{1,3};
                    default:
                        log.error("invalid key type given: "+keyType);
                        return null;
                }
                
            }
//            public CompositeFormat getCompositeFormat()
//            {
//                return new ComparisonFormat();                
//            }
        };
    }    
 
    static class ComparisonFormat extends CompositeFormat
    {
        public void printRecords(java.io.Writer out, RecordVisitor visitor, Iterable ib) throws java.io.IOException
        { //not used
            AffyCompRecord rec;
            
            out.write("<tr bgcolor='"+PageColors.title+"'><th>comparision</th><th>Experiment type</th><th>mean</th><th>pma</th></tr>");
            for(Iterator i=ib.iterator();i.hasNext();)
            { 
                rec=(AffyCompRecord)i.next();
                String controlPopup="onmouseover=\"return escape('"+rec.controlDesc+"')\"";
                String treatPopup="onmouseover=\"return escape('"+rec.treatDesc+"')\"";
                
                out.write("<tr>");
                out.write("<td "+controlPopup+">"+rec.comparison+"</td><td>Control</td>");
                out.write("<td>"+rec.controlMean+"</td><td>"+rec.controlPMA+"</td>");
                out.write("</tr><tr>");
                out.write("<td "+treatPopup+">"+rec.comparison+"</td><td>Treatment</td>");
                out.write("<td>"+rec.treatmentMean+"</td><td>"+rec.treatmentPMA+"</td>");
                out.write("</tr>");
            }
            out.write("</table><TablE bgcolor='"+PageColors.data+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='1'>");
            
            boolean isFirst=true;
            for(Iterator i=ib.iterator();i.hasNext();)
            {
                rec=(AffyCompRecord)i.next();
                if(isFirst)
                    rec.printHeader(out, visitor);
                rec.printRecord(out, visitor);
                if(!i.hasNext())
                    rec.printFooter(out, visitor);
                isFirst=false;                
            }            
        }
        
    }
}
