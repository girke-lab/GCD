/*
 * CorrelationSetFormat.java
 *
 * Created on January 9, 2007, 9:57 AM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.CorrelationRecord;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class CorrelationSetFormat extends AbstractPatternFormat<CorrelationRecord>
{
    private static final RecordPattern pattern=buildPattern();
    private static final Logger log=Logger.getLogger(CorrelationSetFormat.class);
    private static final String plotScript="http://bioweb.ucr.edu/scripts/plotAffyCluster.pl";
    
    private static DecimalFormat percent=new DecimalFormat("0%");
    
    //private static final Collection<String[]> clusterMethods=getClusterMethods();
    private Collection<String[]> clusterMethods;
        
    
    /** Creates a new instance of CorrelationSetFormat */
    public CorrelationSetFormat()
    {
        clusterMethods=getClusterMethods();
    }

    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(CorrelationRecord.class);
        p.addChild(ProbeClusterRecord.class);
        p.addChild(SequenceRecord.class);
        return p;
    }
    public RecordPattern getPattern()
    {
        return pattern;
    }

//    public void preProcess(Iterable<CorrelationRecord> records)
//    {
//        clusterMethods=new TreeSet<String>();
//        
//        //still kind of a hack.
//        // we need to make sure these columns are displayed, even if they
//        // will be empty.
//        clusterMethods.add("cl3-l1 split");
//        clusterMethods.add("kmeans-1 split");
//        
//        //TODO: fix this mess. need complete list, and an order.
//        
//        for(CorrelationRecord cr : records)
//            for(Record r : cr.childGroup(ProbeClusterRecord.class))
//                clusterMethods.add(((ProbeClusterRecord)r).method);
//        log.debug("cluster methods: "+clusterMethods);
//    }
    
    public void printHeader(CorrelationRecord r) throws IOException
    {                              
        String newDir;
        String prefix="corr";
        String sortCol=getParameters().getSortCol();
        String sortDir=getParameters().getSortDir();
        int hid=getParameters().getHid();
        String [] titles=new String[]{"Pearson","Spearman"};
        String[] colNames=QuerySetProvider.getDataViewQuerySet().getSortableCorrelationColumns();        
        
        out.write("&nbsp&nbsp&nbsp");
        printFormTop();            
        
        Utilities.startTable(out);       
        out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</nbsp><th>Affy ID</th>");
//        for(String s : catagories)
//            out.write("<th colspan='2'>"+s+"</th>");            
        out.write("<th colspan='2'>All</th>");            
        out.write("<th colspan='"+clusterMethods.size()+"'>Clusters ( key confidence (size) )</th>");
        out.write("<th>Accessions</th></tr>");
                        
        newDir="asc";
        if(sortCol!=null && sortCol.equals(prefix+"_"+colNames[0]))
            newDir=(sortDir.equals("asc"))? "desc" : "asc"; //flip direction
        out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</td><th><a "+
                "href='QueryPageServlet?hid="+hid+"&sortCol="+
                prefix+"_"+colNames[0]+"&sortDirection="+newDir+"'>"+
                r.psk1_key+"</a></th>\n");

        String[] catagories=new String[]{"All"};
        for(String catagory : catagories)                            
            for(int j=0;j<titles.length;j++)
            {
                newDir="asc";
                if(sortCol!=null && sortCol.equals(prefix+"_"+colNames[j+1]))
                    newDir=(sortDir.equals("asc"))? "desc" : "asc"; //flip direction
                out.write("<th nowrap ><a href='QueryPageServlet?hid="+hid+"&sortCol="+prefix+"_"+colNames[j+1]+
                        "&sortDirection="+newDir+"&catagory="+catagory+"'>"+titles[j]+"</a></th>\n");
            }                            

        String popup;
        for(String[] m : clusterMethods)
        {
            if(m[1].trim().length() > 0)
                popup="onmouseover=\"return escape('"+m[1]+"')\"";
            else popup="";
            out.write("<th nowrap "+popup+"  >"+m[0]+"</th>");
        }

        out.write("<th>&nbsp</th></tr>");
    }

    public void printRecord(CorrelationRecord r) throws IOException
    {
        String url="QueryPageServlet?displayType=affyView&searchType=Probe_Set&inputKey=";
        

        out.write("<tr>");
        out.write("<td><INPUT type=checkbox name='probe_set_key' value='"+r.psk2_key+"'></td>");
        out.write("<td><a href='"+url+r.psk2_key+"'>"+r.psk2_key+"</a></td>");    
        
        // for each catagory
        out.write("<td>"+r.pearson+"</td><td>"+r.spearman+"</td>");
                
        printClusters( r.childGroup(ProbeClusterRecord.class),r.psk1_id);  
        printSequences(r.childGroup(SequenceRecord.class));
        
        
        //TODO: need to find out if any records will print after this pattern        
        //Utilities.openChildCel(out,3+clusterMethods.size()+2);
    }

    public void printFooter(CorrelationRecord r) throws IOException
    {         
        //if(Utilities.hasChildren(r))
         //   Utilities.closeChildCel(out);
        Utilities.endTable(out);
        out.write("</FORM>");
    }
    
    ///////////////////////////////////////////////////////////////////////
           
    private void printClusters(Iterable<Record> records,Integer psk1_id) throws IOException
    {
        String clusterLink="QueryPageServlet?displayType=correlationView&searchType=Cluster_Corr" +
            "&inputKey=";
        String clusterPicLink=plotScript+"?script=plot&cluster_id=";            
        
        ProbeClusterRecord pcr;
        Iterator<Record> ri=records.iterator();
        
        pcr=(ProbeClusterRecord)ri.next();
        for(String[] method : clusterMethods)
        {            
            if(pcr==null || !pcr.method.equals(method[0]))
                out.write("<td> &nbsp </td>");
            else
            {            
                out.write("<td nowrap><a href='"+clusterLink+pcr.probeClusterId+" "+psk1_id+"'>");
                out.write(pcr.name+" "+ (pcr.confidence==null?"":percent.format(pcr.confidence))   +" ("+pcr.size+")</a>&nbsp");
                out.write("<a href='"+clusterPicLink+pcr.probeClusterId+"'>");
                out.write("<img border=0 src='images/ts_icon.png' height=14/>  </td>");    

                if(ri.hasNext())
                    pcr=(ProbeClusterRecord)ri.next();                
                else
                    pcr=null;
            }
        }                                
    }
    private void printFormTop() throws IOException
    {            
        out.write("<FORM method=GET action="+plotScript+">");
        out.write("<INPUT type=hidden name='script' value='junk'>");
        out.write("<INPUT type=submit value='RBC Tools' onClick=\"script.value='';submit()\">");
        out.write("<INPUT type=submit value='Plot Selected' onClick=\"script.value='plot';submit()\">");
    }

    private void printSequences(Iterable<Record> records) throws IOException
    {
        String accUrl="QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";
        
        out.write("<td nowrap> &nbsp");
        
        SequenceRecord sr;
        for(Record r : records) {
            sr=(SequenceRecord)r;
            out.write("<a href='"+accUrl+sr.key+"'>"+sr.key+"</a> &nbsp ");
        }
        out.write(" &nbsp&nbsp ");
        
        for(Record r : records) {
            sr=(SequenceRecord)r;
            out.write(sr.description+" &nbsp&nbsp&nbsp ");
        }
        out.write("</td>"); 
    }

    private static Collection<String[]> getClusterMethods()
    {
        String query=QuerySetProvider.getDataViewQuerySet().getClusterMethodsQuery();
        DbConnection dbc=DbConnectionManager.getConnection("khoran");
        try{
            List data=dbc.sendQuery(query);
            
            List<String[]> methods=new LinkedList<String[]>();
            for(Object row : data)        
                methods.add(new String[]{ (String)((List)row).get(0), (String)((List)row).get(1)});

            return Collections.unmodifiableCollection(methods);

        }catch(SQLException e){
            return new LinkedList<String[]>();
        }
    }
   

  
}
