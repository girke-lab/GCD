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
    //Float correlation,p_value;
    Float pearson,spearman;
    Object acc;
    int[] cluster_ids,sizes;
    double[] confidences;
    String[] clusterNames,parentNames, methods,accessions,descriptions;
    
    /** Creates a new instance of CorrelationRecord */
    public CorrelationRecord(List values)
    {
        int reqSize=16;
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
        
        pearson=Float.parseFloat((String)values.get(6));
        spearman=Float.parseFloat((String)values.get(7));
        
        accessions=Common.getStringArray((java.sql.Array)values.get(8));
        descriptions=Common.getStringArray((java.sql.Array)values.get(9));
        
        cluster_ids=Common.getIntArray((java.sql.Array)values.get(10));
        clusterNames=Common.getStringArray((java.sql.Array)values.get(11));
        methods=Common.getStringArray((java.sql.Array)values.get(12));
        sizes=Common.getIntArray((java.sql.Array)values.get(13));
        
        parentNames=Common.getStringArray((java.sql.Array)values.get(14));
        confidences=Common.getDoubleArray((java.sql.Array)values.get(15));
    }

    
    public Object getPrimaryKey()
    {        
        return corrId;
    }
    public KeyType getChildKeyType()
    {
        return KeyType.CORR;
    }
    public KeyType[] getSupportedKeyTypes()
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
        return new RecordInfo(new int[]{1},0,16){
            public Record getRecord(List l)
            {
                return new CorrelationRecord(l);
            }
            public String getQuery(QueryParameters qp,KeyType keyType)
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
        
        private static final String plotScript="http://bioweb.ucr.edu/scripts/plotAffyCluster.pl";
        //private static final String plotScript="http://bioinfo.ucr.edu/cgi-bin/plotAffyCluster.pl";
        
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
            String[] methods=null;
            
            //This mess is to deal with multiple catagories, which arrive
            // serially, but must be printed out in parallel.
            // Its kind of a waste though since we only have one catagory
            // with correlation data, and it does not seem like this
            // will change any time soon. Oh well, its done now.             
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
                
                if(methods==null || rec.methods.length > methods.length)
                    methods=rec.methods;
            }
           
            //now print the table
            boolean isFirst;
            printHeader(out,catagories, psk1_key,methods);
            
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
                        out.write("<td><INPUT type=checkbox name='probe_set_key' value='"+rec.psk2_key+"'></td>");
                        out.write("<td><a href='"+url+rec.psk2_key+"'>"+rec.psk2_key+"</a></td>");    
                        isFirst=false;
                    }
                    if(rec==null)
                        out.write("<td>&nbsp</td><td>&nbsp</td>");
                    else
                        out.write("<td>"+rec.pearson+"</td><td>"+rec.spearman+"</td>");
                    if(!i.hasNext())
                    {// last element
                                                          
                        printClusters(out,methods,rec);
                        
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
       
        private void printClusters(Writer out, String[] methods, CorrelationRecord rec) throws IOException
        {
            String clusterLink="QueryPageServlet?displayType=correlationView&searchType=Cluster_Corr" +
                "&inputKey=";
            String clusterPicLink=plotScript+"?script=plot&cluster_id=";            
            
            //temp hack
            methods= new String[]{"PCC 0.7","PCC 0.8","PCC 0.9","PCC 0.95","cl3-l1","cl3-l1","kmeans-1","kmeans-1"};            

            int methodIdx=0;
            for(int j=0; j < methods.length; j++)
            {
                if(methodIdx >= rec.methods.length || !methods[j].equals(rec.methods[methodIdx]))
                    out.write("<td> &nbsp </td>");
                else
                {
                    out.write("<td nowrap ><a href='"+clusterLink+rec.cluster_ids[methodIdx]+" "+rec.psk1_id+"' >"+
                        rec.clusterNames[methodIdx]+" "+
                            (rec.confidences[methodIdx]==-1?"":rec.confidences[methodIdx])  +
                            " ("+rec.sizes[methodIdx]+")</a>&nbsp" +
                        "<a href='"+clusterPicLink +rec.cluster_ids[methodIdx]+"'><img border=0 src='images/ts_icon.png' height=14/></a>"+
                        " </td> ");                  
                    methodIdx++;
                }
            }                                   
        }
        
        /** this is not the same printHeader as in the super class
         */
        private void printHeader(Writer out, Set<String> catagories,String psk1,String[] methods)
            throws IOException
        {
            String newDir;
            String prefix="corr";
            String sortCol=visitor.getSortCol();
            String sortDir=visitor.getSortDir();
            String [] titles=new String[]{"Pearson","Spearman"};
            String[] colNames=QuerySetProvider.getDataViewQuerySet().getSortableCorrelationColumns();
            
            // temporary hack
            methods= new String[]{"PCC 0.7","PCC 0.8","PCC 0.9","PCC 0.95","cl3-l1","cl3-l1 split","kmeans-1","kmeans-1 split"};
            
            out.write("&nbsp&nbsp&nbsp");
            printFormTop(out);            
            
            
            out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</nbsp><th>Affy ID</th>");
            for(String s : catagories)
                out.write("<th colspan='2'>"+s+"</th>");            
            out.write("<th colspan='"+methods.length+"'>Clusters ( key confidence (size) )</th>");
            out.write("<th>Accessions</th></tr>");
            
            newDir="asc";
            if(sortCol!=null && sortCol.equals(prefix+"_"+colNames[0]))
                newDir=(sortDir.equals("asc"))? "desc" : "asc"; //flip direction
            out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</td><th><a "+
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
                
            for(String m : methods)
                out.write("<th nowrap >"+m+"</th>");
            
            out.write("<th>&nbsp</th></tr>");
        }
        public void printFooter(Writer out, RecordVisitor visitor, Iterable ib) throws IOException
        {
            out.write("</FORM>");
        }
        
        private void printFormTop(Writer out) throws IOException
        {            
            out.write("<FORM method=GET action="+plotScript+">");
            out.write("<INPUT type=hidden name='script' value='junk'>");
            out.write("<INPUT type=submit value='RBC Tools' onClick=\"script.value='';submit()\">");
            out.write("<INPUT type=submit value='Plot Selected' onClick=\"script.value='plot';submit()\">");
        }
    }
    
    
}
