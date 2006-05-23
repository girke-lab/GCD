/*
 * CorrelationRecord.java
 *
 * Created on November 17, 2005, 11:37 AM
 *
 */

package servlets.dataViews.records;

import java.io.IOException;
import java.io.Writer;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.PageColors;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class CorrelationRecord extends AbstractRecord
{
    private static Logger log=Logger.getLogger(CorrelationRecord.class);        
    
    Long corrId;
    Integer psk1_id,psk2_id;
    String catagory,psk1_key,psk2_key;
    Float correlation,p_value;
    Object acc;
    String[] accessions,descriptions;
    /** Creates a new instance of CorrelationRecord */
    public CorrelationRecord(List values)
    {
        int reqSize=10;
        if(values==null || values.size()!=reqSize)
        {
            log.error("invalid list in CorrelationRecord constructor");
            if(values!=null)
                log.error("recieved list of size "+values.size()+", but expected size of "+reqSize);
            return;
        }
        
        corrId=Long.parseLong((String)values.get(0));
        psk1_id=Integer.parseInt((String)values.get(1));
        psk2_id=Integer.parseInt((String)values.get(2));
        catagory=(String)values.get(3);
        psk1_key=(String)values.get(4);
        psk2_key=(String)values.get(5);
        
        correlation=Float.parseFloat((String)values.get(6));
        p_value=Float.parseFloat((String)values.get(7));
        
        accessions=getArray((java.sql.Array)values.get(8));
        descriptions=getArray((java.sql.Array)values.get(9));
    }

    private String[] getArray(java.sql.Array a)
    {
        String[] strings;
        try{
            if(a==null)
                strings=new String[]{};
            else
                strings=(String[])(a.getArray());            
        }catch(java.sql.SQLException e){
            log.warn("exception while grabbing array: "+e);
            strings=new String[]{};
        }        
        return strings;
    }
    public Object getPrimaryKey()
    {        
        return corrId;
    }
    public int getChildKeyType()
    {
        return Common.KEY_TYPE_CORR;
    }
    public int[] getSupportedKeyTypes()
    {
        return this.getRecordInfo().getSupportedKeyTypes();
    }


    public void printHeader(Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printHeader(out,this);
    }
    public void printRecord(Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printRecord(out,this);
    }
    public void printFooter(Writer out, RecordVisitor visitor) throws java.io.IOException
    {
        visitor.printFooter(out,this);
    }
    
    
    public static RecordInfo getRecordInfo()
    {
        return new RecordInfo(new int[]{1},0,10){
            public Record getRecord(List l)
            {
                return new CorrelationRecord(l);
            }
            public String getQuery(QueryParameters qp,int keyType)
            {
                return QuerySetProvider.getRecordQuerySet().
                        getCorrelationRecordQuery(qp.getIds(),qp.getSortCol(),qp.getSortDir(), qp.getCatagory());
            }
            
            public CompositeFormat getCompositeFormat()
            {
                return new CorrelationFormat();
            }
        };
    }
    
    
    static class CorrelationFormat extends CompositeFormat
    {

        HtmlRecordVisitor visitor;
        
        /** Creates a new instance of CorrelationFormat */
        public CorrelationFormat()
        {
        }

        public void printRecords(Writer out, RecordVisitor visitor, Iterable ib)
            throws IOException
        {
            if(visitor instanceof HtmlRecordVisitor)
                this.visitor=(HtmlRecordVisitor)visitor;
            else
                throw new IOException("this formater requires an HtmlRecordVisitor, " +
                        "but got an "+visitor.getClass().getName());
            
            CorrelationRecord rec;
            
            Map<Integer,Map<String,CorrelationRecord>> records=
                    new LinkedHashMap<Integer,Map<String,CorrelationRecord>>();
            Map<String,CorrelationRecord> catagoryMap;
            
            Set<String> catagories=new TreeSet<String>();
            String psk1_key="";
            String url="QueryPageServlet?displayType=affyView&searchType=Probe_Set&inputKey=";
            
            for(Object o : ib)
            { //load the hash
                rec=(CorrelationRecord)o;
                catagories.add(rec.catagory);
                catagoryMap=records.get(rec.psk2_id);
                if(catagoryMap==null)
                { //if don't already have a catagory map, add one
                    catagoryMap=new HashMap<String,CorrelationRecord>();
                    records.put(rec.psk2_id,catagoryMap);
                }
                catagoryMap.put(rec.catagory,rec);                
                //this will be the same key for every record.
                psk1_key=rec.psk1_key;
            }
           
            //now print the table
            boolean isFirst;
            printHeader(out,catagories, psk1_key);
            for(Map<String,CorrelationRecord> cm : records.values())
            {                
                isFirst=true;                
                out.write("<tr>");
                
                String catagory;    
                for(Iterator i=catagories.iterator();i.hasNext();)
                {
                    catagory=(String)i.next();
                    rec=cm.get(catagory);
                    if(isFirst)
                    {
                        if(rec==null)
                            continue;
                        out.write("<td><a href='"+url+rec.psk2_key+"'>"+rec.psk2_key+"</a></td>");    
                        isFirst=false;
                    }
                    if(rec==null)
                        out.write("<td>&nbsp</td><td>&nbsp</td>");
                    else
                        out.write("<td>"+rec.correlation+"</td><td>"+rec.p_value+"</td>");
                    if(!i.hasNext())
                    {// last element
                        //print accessions
                        String accUrl="QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";
                        out.write("<td nowrap > &nbsp ");
                        for(int j=0;j<rec.accessions.length;j++)
                            out.write("<a href='"+accUrl+rec.accessions[j]+"'>"+
                                    rec.accessions[j]+"</a> &nbsp ");
                        out.write(" &nbsp&nbsp ");
                        for(int j=0;j<rec.descriptions.length;j++)
                            out.write(rec.descriptions[j]+" &nbsp&nbsp&nbsp ");
                        out.write("</td>");
                    }
                }
                out.write("</tr>");
            }
            
        }
         
         
        
        public void printRecords2(Writer out, RecordVisitor visitor, Iterable ib)
            throws IOException
        { //not used
            CorrelationRecord rec;
            Integer lastPsk=null;            
            Set catagories;
            
            int c=0,catNum=0;
            
            log.debug("using correlation format");
            
            
            //we assume that the records are sorted by psk1,psk2, and catagory.
            
            for(Iterator i=ib.iterator(); i.hasNext();c++)
            {
                rec=(CorrelationRecord)i.next();
                
                if(c == 0)
                {
                    catagories=printHeader(out,ib,rec.psk1_key);
                    catNum=catagories.size();
                    log.debug("catNum="+catNum);

//                    out.write("<tr><th align='left' colspan='"+(catNum*2+1)+
//                            "' bgcolor='"+PageColors.title+"'>"+rec.psk1_key+"</th></tr>");
                    out.write("<tr>");                    
                }
                
                if(!rec.psk2_id.equals(lastPsk))
                {   //we have moved to the next key, but have not finsihed
                    // all the catagories, so print blanks for the
                    // remaining catagories.
                    while(c % catNum !=0)
                    {
                        c++;
                        out.write("<td>&nbsp</td><td>&nbsp</td>");
                    }
                }
                lastPsk=rec.psk2_id;
                if(c % catNum == 0)
                {
                    out.write("</tr><tr>");
                    out.write("<td>"+rec.psk2_key+"</td>");    
                }
                
                
                out.write("<td>"+rec.correlation+"</td><td>"+rec.p_value+"</td>");
                
                //rec.printRecord(out, visitor);
            }
            out.write("</td>");
        }

        private Set printHeader(Writer out, Iterable ib,String psk1)
            throws IOException
        { //not used
            Set<String> catagories=new TreeSet<String>();
            Iterator i=ib.iterator();
            while(i.hasNext())
                catagories.add(((CorrelationRecord)i.next()).catagory);

            out.write("<tr bgcolor='"+PageColors.title+"'><th>Affy ID</th>");
            for(String s : catagories)
                out.write("<th colspan='2'>"+s+"</th>");
            out.write("</tr>");
            out.write("<tr bgcolor='"+PageColors.title+"'><th>"+psk1+"</th>");
            for(String s : catagories)
                out.write("<th>Corr</th><th>P values</th>");
            out.write("</tr>");
            
            return catagories;
        }
        private void printHeader(Writer out, Set<String> catagories,String psk1)
            throws IOException
        {
            String newDir;
            String prefix="corr";
            String sortCol=visitor.getSortCol();
            String sortDir=visitor.getSortDir();
            String [] titles=new String[]{"Correlations","P value"};
            String[] colNames=QuerySetProvider.getDataViewQuerySet().getSortableCorrelationColumns();
            
            out.write("<tr bgcolor='"+PageColors.title+"'><th>Affy ID</th>");
            for(String s : catagories)
                out.write("<th colspan='2'>"+s+"</th>");
            out.write("<th>Accessions</th></tr>");
            
            newDir="asc";
            if(sortCol!=null && sortCol.equals(prefix+"_"+colNames[0]))
                newDir=(sortDir.equals("asc"))? "desc" : "asc"; //flip direction
            out.write("<tr bgcolor='"+PageColors.title+"'><th><a "+
                    "href='QueryPageServlet?hid="+visitor.getHid()+"&sortCol="+
                    prefix+"_"+colNames[0]+"&sortDirection="+newDir+"'>"+
                    psk1+"</a></th>\n");
            
            for(String catagory : catagories)                            
                for(int j=0;j<titles.length;j++)
                {
                    newDir="asc";
                    if(sortCol!=null && sortCol.equals(prefix+"_"+colNames[j+1]))
                        newDir=(sortDir.equals("asc"))? "desc" : "asc"; //flip direction
                    out.write("<th nowrap ><a href='QueryPageServlet?hid="+visitor.getHid()+"&sortCol="+prefix+"_"+colNames[j+1]+
                            "&sortDirection="+newDir+"&catagory="+catagory+"'>"+titles[j]+"</a></th>\n");
                }                            
                
            out.write("<th>&nbsp</th></tr>");
        }
//        static class CorrelationKey
//        {
//            public Integer psk2_id;
//            public String catagory;
//            public CorrelationKey(Integer psk2_id, String catagory)
//            {                
//                this.psk2_id=psk2_id;
//                this.catagory=catagory;
//            }
//        }
    }
    
    
}
