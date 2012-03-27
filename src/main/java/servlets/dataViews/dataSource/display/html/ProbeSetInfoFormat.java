/*
 * ProbeSetInfoFormat.java
 *
 * Created on January 10, 2007, 1:23 PM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import org.apache.log4j.Logger;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.ProbeSetKeyRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.querySets.DataViewQuerySet;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class ProbeSetInfoFormat extends AbstractPatternFormat<ProbeSetKeyRecord>
{
    
    private static final RecordPattern pattern=buildPattern();
    private static final Logger log=Logger.getLogger(ProbeSetInfoFormat.class);
    
    /** Creates a new instance of ProbeSetInfoFormat */
    public ProbeSetInfoFormat()
    {
    }

    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(ProbeSetKeyRecord.class);
        p.addChild(ProbeClusterRecord.class);
        p.addChild(SequenceRecord.class);
        return p;
    }
    public RecordPattern getPattern()
    {
        return pattern;
    }

    public void printHeader(ProbeSetKeyRecord r) throws IOException
    {
        Utilities.startTable(out);
        out.write("<tr bgcolor='"+PageColors.title+"'>");
        String[] titles=new String[]{"Control Mean",
                            "Treat Mean","Control PMA","Treat PMA","ratio (log2)",
                            "Contrast","p-value","Adjusted p-value","PFP up",
                            "PFP down"};
            //,"Clusters","Accession Descriptions"};

        String[] dbNames=QuerySetProvider.getDataViewQuerySet().getSortableTreatmentColoumns()[DataViewQuerySet.TREAT_PSK];
        
       PrintWriter wout=new PrintWriter(out);
       Utilities.printTableTitles(wout,getParameters(),new String[]{"AffyID"},new String[]{dbNames[0]},"psk","");
       wout.println("<th nowrap> Accessions</th>");
       
       dbNames=Arrays.asList(dbNames).subList(1,dbNames.length).toArray(new String[]{});
       Utilities.printTableTitles(new PrintWriter(out),getParameters(),titles,dbNames,"psk","");
       wout.println("<th nowrap> Clusters</th><th nowrap> Accession Descriptions</th>");
       

        out.write("</tr>");                                  
    }

    public void printRecord(ProbeSetKeyRecord r) throws IOException
    {
        
        out.write("<tr>");

        String pskLink="QueryPageServlet?displayType=affyView&searchType=Probe_Set&inputKey="+r.probeSetKey;
        String clusterLink="QueryPageServlet?displayType=probeSetView&searchType=Psk_Cluster" +
                "&inputKey=";
        String accLink="QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";

        String pskPopup;                

        String clusters="";
        ProbeClusterRecord pcr;
        for(Record temp : r.childGroup(ProbeClusterRecord.class))
        {
            pcr=(ProbeClusterRecord)temp;
            pskPopup="onmouseover=\"return escape('"+pcr.name+"<br>"+pcr.method+"')\"";
            clusters+="<a href='"+clusterLink+pcr.probeClusterId+" "+r.comparisonId+"' "+pskPopup+">"+
                    pcr.name+"("+pcr.size+")</a> &nbsp&nbsp ";
        }
                         
        StringBuilder accSb=new StringBuilder();
        StringBuilder descSb=new StringBuilder();
        SequenceRecord sr;
        for(Record temp : r.childGroup(SequenceRecord.class))
        {
            sr=(SequenceRecord)temp;
            accSb.append("<a href='"+accLink+sr.key+"'>"+sr.key+"</a> &nbsp ");
            descSb.append(sr.description+" &nbsp&nbsp&nbsp ");
        }
        
        Object[] values=new Object[]{
            "<a href='"+pskLink+"' >"+r.probeSetKey+"</a>",accSb.toString(),
            r.controlMean,r.treatmentMean, 
            Utilities.cn(r.controlPMA), Utilities.cn(r.treatmentPMA), r.ratio,
            r.contrast, r.pValue, r.adjPValue, 
            r.pfpUp, r.pfpDown,clusters,descSb.toString()
        };
        for(Object v : values)
            out.write("<td nowrap >"+v+"</td>");
        out.write("</tr>");        
        
        //TODO: open child cel here 
    }

    public void printFooter(ProbeSetKeyRecord r) throws IOException
    {
        Utilities.endTable(out);
    }
    
}
