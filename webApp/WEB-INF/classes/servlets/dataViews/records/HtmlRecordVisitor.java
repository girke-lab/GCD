/*
 * HtmlRecordVisitor.java
 *
 * Created on October 19, 2004, 1:54 PM
 */

package servlets.dataViews.records;

/**
 *
 * @author  khoran
 */

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import servlets.*;
import org.apache.log4j.Logger;
import servlets.querySets.DataViewQuerySet;
import servlets.querySets.QuerySetProvider;
import servlets.dataViews.records.*;
/**
 * This class implements RecordVisitor so that it can print Records in 
 * html format.  Each record gets its own table, which can be nested inside
 * each other.
 */
public class HtmlRecordVisitor implements RecordVisitor
{
    
    private static Logger log=Logger.getLogger(HtmlRecordVisitor.class);
    int hid;
    String sortDir,sortCol,compView="";
    String currentAccession,expType;
    DecimalFormat df=new DecimalFormat("0.00");
    DecimalFormat percent=new DecimalFormat("0%");
    DecimalFormat ldf=new DecimalFormat("0.##E0");
    
    /** Creates a new instance of HtmlRecordVisitor */
    public HtmlRecordVisitor()
    {
    }
    public void setHid(int hid)
    {
        this.hid=hid;
    }
    public int getHid()
    {
        return hid;
    }
    public void setSortInfo(String col,String dir)
    {
        sortCol=col;
        sortDir=dir;
    }
    public String getSortCol()
    {
        return sortCol;
    }
    public String getSortDir()
    {
        return sortDir;
    }
    public void setCompView(String s)
    {
        compView=s;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" misc records ">
    ////////////////////////////////////////////////////////////////////////////
    //            Unknown
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        log.debug("unknown header");
        out.write("<tr bgcolor='"+PageColors.title+"'><th>Key</th><th>Description</th></tr>\n");
    }
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        log.debug("unknown record");
        currentAccession=ur.key;
        
        out.write("<tr><td><a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+
            ur.key.subSequence(0,ur.key.lastIndexOf('.'))+"'>"+ur.key+"</a></td><td>"+ur.description+"</td></tr>\n");
        String[] names=new String[]{"mfu","ccu","bpu"};
        out.write("<tr><td colspan='2'>\n");
        for(int i=0;i<ur.go_unknowns.length;i++)
            out.write("<b>"+names[i]+"</b>: "+ur.go_unknowns[i]+" &nbsp&nbsp&nbsp \n");
        String url="QueryPageServlet?searchType=Id&displayType=correlationView&rpp=200&inputKey="+ur.key;
        out.write("&nbsp&nbsp&nbsp <a href='"+url+"'>Correlations</a>");
        out.write("</td></tr>\n");               
                                
        printSubRecords(out, ur.iterator(),5,0);        
        out.write("<tr><td bgcolor='FFFFFF' colspan='5'>&nbsp</td></tr>\n");                
    }
    public void printFooter(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {                
        log.debug("unknown footer");
    }
    ////////////////////////////////////////////////////////////////////////////
    //            Go
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
         out.write("<tr bgcolor='"+PageColors.title+"'><th>Go Number</th><th>Description</th><th>Function</th></tr>\n");
    }
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
         String link="http://www.godatabase.org/cgi-bin/go.cgi?" +
            "depth=0&advanced_query=&search_constraint=terms&query="+gr.go_number+"&action=replace_tree";
         out.write("<tr><td><a href='"+link+"'>"+gr.go_number+"</a></td><td>"+gr.text+"</td><td>"+gr.function+"</td></tr>\n");
    }
    ////////////////////////////////////////////////////////////////////////////
    //            Blast
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+PageColors.title+"'>" +
                "<th>Target Key</th><th>E-value</th>" +
                "<th>Score</th><th>DB/Method</th></tr>\n");
    }
    public void printRecord(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {                
        String target=br.target;        
        if(target.equals(""))
            target="no hit";
        if(!target.equals("no hit") && br.link!=null)
            target="<a href='"+br.link+"'>"+br.target+"</a>";
         out.write("<tr><td>"+target+"</td><td>"+br.evalue+"</td><td>"+
                    (br.score==null || br.score.equals("")?"&nbsp":br.score)+"</td>" +
                    "<td>"+br.dbname+"/"+br.method+"</td></tr>\n");
    }    
    ////////////////////////////////////////////////////////////////////////////
    //            Proteomics
    public void printHeader(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        String prob="Probability";
        if(pr.prob_is_neg)
            prob="Improbability";
        out.write("<tr bgcolor='"+PageColors.title+"'><th>MW</th><th>IP</th><th>Charge</th><th>"+prob+"</th></tr>\n");
    }      
    public void printRecord(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        out.write("<tr><td>"+pr.mol_weight+"</td><td>"+pr.ip+"</td><td>"+pr.charge+"</td><td>"+pr.prob+"</td></tr>\n");
    }
    ////////////////////////////////////////////////////////////////////////////
    //                Cluster
    public void printHeader(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+PageColors.title+"'><th>Cluster Size(Method)</th></tr>\n");
        out.write("<tr><td>"); //all records go in one row
    }    
    public void printRecord(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        //a relative url won't work here becuase we are under /unknowns/... , 
        // but we need to link to pages under /databaseWeb/... .
        String page="http://bioweb.ucr.edu/databaseWeb/QueryPageServlet?searchType=Cluster Id&displayType=seqView&inputKey="+cr.key;
        
        out.write("<a href='"+page+"'>"+cr.size+"("+cr.method+")</a> &nbsp&nbsp&nbsp ");
        //out.write("</table></td></tr><tr><td>"); //set up another row for future cluster(cutoff) entries
        
    }    
    public void printFooter(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write("</td></tr>"); //end the last cluster(cutoff) row
    }
    ////////////////////////////////////////////////////////////////////////////
    //                ExternalUnknown
    public void printHeader(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write("<tr><th bgcolor='"+PageColors.title+"'>External Sources</th></tr>\n");
        out.write("<tr><td>");
    }
    public void printRecord(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write("<b>"+eur.source+":</b> "+(eur.isUnknown? "unknown" : "known")+" &nbsp&nbsp&nbsp ");
    }
    public void printFooter(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write("</td></tr>");
    }
    ////////////////////////////////////////////////////////////////////////////
    //                Composite
    public void printHeader(Writer out, CompositeRecord cr) throws IOException
    {
        log.debug("composite header");
        cr.getFormat().printHeader(out,this,cr);
    }
    public void printRecord(Writer out, CompositeRecord cr) throws IOException
    {
        log.debug("composite record");
//        out.write("<tr>");            
//        out.write("<td colspan='0'><TablE bgcolor='"+PageColors.data+"' width='100%'" +
//                " border='1' cellspacing='0' cellpadding='0'>\n");
        cr.getFormat().printRecords(out,this,cr);
//        out.write("</TablE></td></tr>\n");
    }
    public void printFooter(Writer out, CompositeRecord cr) throws IOException
    {
        log.debug("composite footer");
    }

    //</editor-fold>
    
    // <editor-fold desc=" Affy record stuff ">
    ///////////////////////// Affy stuff  ////////////////////////////////////
    
    ////////////////////////////////////////////////////////////////////////////
    //            AffyExpSet
    public void printHeader(Writer out, AffyExpSetRecord ar) throws IOException
    {
 
        
        String[] titles=new String[]{"AffyID","Exp","Name","up 2x","down 2x",
                                     "up 4x","down 4x","on","off",
                                     "ctrl avg","ctrl stddev","treat avg","treat stddev"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.EXPSET]; 
        
        out.write("<tr bgcolor='"+PageColors.title+"'><td><a name='"+ar.probeSetId+"'></a>Exp</td><td>Int</td>");        
        printTableTitles(new PrintWriter(out), titles, feilds, "expset",ar.probeSetId.toString());        
        out.write("</tr>");
    }
    public void printRecord(Writer out, AffyExpSetRecord ar) throws IOException
    {        
        
        String link="QueryPageServlet?hid="+hid+"&displayType=affyView&es_ids="+ar.expSetId+"&psk_ids="+ar.probeSetId;
        
        String key=ar.expSetId+"_"+ar.probeSetId;
        String expSetKeyLink="http://www.arabidopsis.org/servlets/Search?" +
                "type=expr&search_action=search&" +
                "name_type_1=submission_number&term_1="+ar.expSetKey+
                "&search=submit+query";
        String popup="onmouseover=\"return escape('"+ar.description+"')\"";          
        
        out.write("<tr bgcolor='"+PageColors.catagoryColors.get(ar.catagory)+"'>");
        
        if(compView!=null && compView.equals("all"))
        {
            printTreeControls(out,link+"&comp_view=all",key,ar.iterator());    
            printTreeControls(out,link+"&comp_view=comp",key,true);                
        }
        else if(compView!=null && compView.equals("comp"))
        {
            printTreeControls(out,link+"&comp_view=all",key,true);    
            printTreeControls(out,link+"&comp_view=comp",key,ar.iterator());    
        }
        else
        {
            printTreeControls(out,link+"&comp_view=all",key,ar.iterator());    
            printTreeControls(out,link+"&comp_view=comp",key,ar.iterator());    
        }
        
        out.write("<td>"+ar.probeSetKey+"</td><td><a href='"+expSetKeyLink+"' "+popup+">"+
                ar.expSetKey+"</a></td>");
        out.write("<td>"+(ar.name.equals("")?"&nbsp":ar.name)+"</td>"); //  <td>"+ar.description+"</td>");
        out.write("<td>"+ar.up2+"</td><td>"+ar.down2+"</td><td>"+ar.up4+"</td>");
        out.write("<td>"+ar.down4+"</td><td>"+(ar.on==null?"&nbsp":ar.on)+"</td><td>"+(ar.off==null?"&nbsp":ar.off)+"</td>");
        out.write("<td>"+(ar.controlAverage==null ? "&nbsp":percent.format(ar.controlAverage))+
                "</td><td>"+(ar.controlStddev==null ? "&nbsp":percent.format(ar.controlStddev))+"</td>");
        out.write("<td>"+(ar.treatAverage==null ? "&nbsp":percent.format(ar.treatAverage))+
                "</td><td>"+(ar.treatStddev==null ? "&nbsp":percent.format(ar.treatStddev))+"</td>");
        out.write("</tr>");
        
        //print sub records
        if("all".equals(compView))
            printSubRecords(out, ar.iterator(),14,1);
        else if("comp".equals(compView))
            printCompView(out,ar);
    }
    public void printFooter(Writer out, AffyExpSetRecord ar) throws IOException
    {
    }
    private void printCompView(Writer out, AffyExpSetRecord ar) throws IOException
    {
        
        AffyCompRecord rec;
        boolean isFirst=true;    
        
        for(Iterator i=ar.iterator();i.hasNext();)
        {
            out.write("<tr><td>&nbsp</td><td colspan='"+14+"'><TablE bgcolor='"+PageColors.data+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='1'>\n");
            for(Iterator j=((Record)i.next()).iterator();j.hasNext();)
            {
                rec=(AffyCompRecord)j.next();
                String link="QueryPageServlet?hid="+hid+"&displayType=affyView&es_ids="+rec.expSetId+
                        "&psk_ids="+rec.probeSetId+"&groups="+rec.comparison;
                String controlPopup=""; //"onmouseover=\"return escape('"+rec.controlDesc+"')\"";
                String treatPopup=""; //"onmouseover=\"return escape('"+rec.treatDesc+"')\"";
                
                if(isFirst)
                    out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</td>" +
                            "<th>Comparision</th><th>Exper Type</th><th>Mean</th><th>PMA</th>" +
                            "<th>Description</th></tr>");
                isFirst=false;
                out.write("<tr>");
                printTreeControls(out,link,rec.getPrimaryKey().toString(),rec.iterator());     
                out.write("<td "+controlPopup+">"+rec.comparison+"</td><td>Control</td>");
                out.write("<td>"+df.format(rec.controlMean)+"</td><td>"+rec.controlPMA+"&nbsp</td>");
                out.write("<td>"+rec.controlDesc+"</td>");
                out.write("</tr><tr>");
                expType="control";
                printSubRecords(out,rec.iterator(),11, 1);
                //printTreeControls(out,link,rec.getPrimaryKey().toString(),rec.iterator());   
                out.write("<td>&nbsp</td><td "+treatPopup+">"+rec.comparison+"</td><td>Treatment</td>");
                out.write("<td>"+df.format(rec.treatmentMean)+"</td><td>"+rec.treatmentPMA+"&nbsp</td>");
                out.write("<td>"+rec.treatDesc+"</td>");
                out.write("</tr>");
                expType="treatment";
                printSubRecords(out,rec.iterator(),11, 1);
            }
            out.write("</table></td></td>");
        }
    }
    ////////////////////////////////////////////////////////////////////////////
    //            AffyComp
    public void printHeader(Writer out, AffyCompRecord ar) throws IOException
    {        
        String[] titles=new String[]{"Comparison","Control mean","Treat mean",
                                "control pma","treat pma","ratio (log2)","contrast",
                                "P-value","adj P-value","pfp up","pfp down"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.COMP]; 

        out.write("<tr bgcolor='"+PageColors.title+"'><td><a name='"+ar.probeSetId+"'>&nbsp</a></td>");
        printTableTitles(new PrintWriter(out), titles, feilds,"comp",ar.probeSetId.toString());        
        out.write("</tr>");        
    }
    public void printRecord(Writer out, AffyCompRecord ar) throws IOException
    {        
        String link="QueryPageServlet?hid="+hid+"&displayType=affyView&es_ids="+ar.expSetId+
                "&psk_ids="+ar.probeSetId+"&groups="+ar.comparison;
        String key=ar.expSetId+"_"+ar.probeSetId+"_"+ar.comparison;                         
        String popup="onmouseover=\"return escape('"+ar.controlDesc+"<br>"+ar.treatDesc+"')\"";
        
        out.write("<tr >");       
        printTreeControls(out,link,key,ar.iterator());                
        out.write("<td "+popup+" >"+ar.comparison+"</td><td>"+df.format(ar.controlMean)+"</td>");
        out.write("<td>"+df.format(ar.treatmentMean)+"</td><td>"+ar.controlPMA+"&nbsp</td>");
        out.write("<td>"+ar.treatmentPMA+"&nbsp</td><td>"+df.format(ar.ratio)+"</td>");
        out.write("<td>"+df.format(ar.contrast)+"</td><td>"+ldf.format(ar.pValue)+"</td><td>"+ldf.format(ar.adjPValue)+"</td>");
        out.write("<td>"+df.format(ar.pfpUp)+"</td><td>"+df.format(ar.pfpDown)+"</td>");
        out.write("</tr>");
        expType="both";
        printSubRecords(out,ar.iterator(),11, 1);
    }
   
    public void printFooter(Writer out, AffyCompRecord ar) throws IOException
    {
    }
    ////////////////////////////////////////////////////////////////////////////
    //                AffyDetail
    public void printHeader(Writer out, AffyDetailRecord ar) throws IOException
    {
        String[] titles=new String[]{"Type","Cel File","Intensity","PMA"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.DETAIL]; 
        
        out.write("<tr bgcolor='"+PageColors.title+"'><a name='"+ar.probeSetId+"'></a>");
        printTableTitles(new PrintWriter(out), titles, feilds,"detail",ar.probeSetId.toString());        
        out.write("</tr>\n");        
    }
    public void printRecord(Writer out, AffyDetailRecord ar) throws IOException
    {                
        if(!"both".equals(expType) && !ar.type.equals(expType))
            return;
        
        out.write("<tr>");                
        out.write("<td>"+ar.type+"</td><td>"+ar.celFile+"</td>"); //"<td>"+desc+"</td>
        out.write("<td>"+df.format(ar.intensity)+"</td>");
        out.write("<td>"+ar.pma+"&nbsp</td>");
        out.write("</tr>");
    }
    public void printFooter(Writer out, AffyDetailRecord ar) throws IOException
    {
    }


    public void printHeader(Writer out, ProbeSetRecord psr) throws IOException
    {
        out.write("<tr bgcolor='"+PageColors.title+"'>" +
                "<th>Affy Probe Set</th>" +
                "<th>Control Average Intensity</th>" +
                "<th>Control Std Deviation</th>" +
                "<th>Treatment Average Intensity</th>" +
                "<th>Treatment Std Deviation</th></tr>\n");
        
    }
    public void printRecord(Writer out, ProbeSetRecord psr) throws IOException
    {
        String link="QueryPageServlet?displayType=affyView&" +
                "searchType=id&dbs=0&inputKey=exact "+currentAccession;
//        String corrLink="QueryPageServlet?rpp=200&displayType=correlationView&" +
//                "searchType=Probe_Set_Key&inputKey="+psr.probeSetId;
        
        out.write("<tr>");
        out.write("<td><a href='"+link+"'>"+psr.probeSetKey+"</a></td>");                 
        out.write("<td>"+percent.format(psr.controlAverage)+"</td><td>"+percent.format(psr.controlStddev)+"</td>");
        out.write("<td>"+percent.format(psr.treatAverage)+"</td><td>"+percent.format(psr.treatStddev)+"</td>");
        out.write("</td>");
    }    
    public void printFooter(Writer out, ProbeSetRecord psr) throws IOException
    {
        
    }
    //</editor-fold>
    
        
    public void printHeader(Writer out, CorrelationRecord cr) throws IOException
    {
        out.write("<tr bgcolor='"+PageColors.title+"'>"+
                "<th>Catagory</th><th>Correlation</th><th>P value</th></tr>");
    }
    public void printRecord(Writer out, CorrelationRecord cr) throws IOException
    {
        //QueryPageServlet?displayType=affyView&searchType=id&dbs=0&inputKey=exact%20At1g01070.1
        String url="QueryPageServlet?displayType=affyView&searchType=Probe_Set&inputKey="+cr.psk2_key;
        out.write("<tr>");
        out.write("<td><a href='"+url+"'>"+cr.psk2_key+"</a></td><td>"+cr.correlation+"</td>");
        out.write("<td>"+cr.p_value+"</td></tr>");
    }
    public void printFooter(Writer out, CorrelationRecord cr) throws IOException
    {
    }
    ////////////////////////////////////////////////////////////////////////////
    
   
    
    private void printSubRecords(java.io.Writer out, Iterator itr,int span, int shift) throws java.io.IOException
    {
        if(itr==null)
            return;
        String spaces="";
        for(int i=0;i<shift;i++)
            spaces+="<td>&nbsp</td>";
        Record rec;
        //log.debug("printing sub records");
        while(itr.hasNext())
        {
            log.debug("printing a sub record");
            rec=(Record)itr.next();
            if(rec==null || !rec.iterator().hasNext())
            {
                log.debug("skipping "+rec.getClass());
                continue;
            }
            out.write("<tr>"+spaces);            
            out.write("<td colspan='"+span+"'><TablE bgcolor='"+PageColors.data+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='1'>\n");
            rec.printHeader(out, this);
            rec.printRecord(out, this);
            rec.printFooter(out, this);
            out.write("</TablE></td></tr>\n");
        }                    
        //log.debug("done with sub records");
    }
    
    private void printTreeControls(Writer out, String link, String key,Iterator recordItr)  throws IOException
    {
        if(recordItr==null || !recordItr.hasNext()) //no children
            printTreeControls(out,link,key,true);
        else
            printTreeControls(out,link,key,false);
    }
    private void printTreeControls(Writer out, String link, String key,boolean expand)  throws IOException
    {
        String imageOptions=" border='0' height='10' width='15' ";
     
        out.write("<td nowrap>"+"<a name='"+key+"'></a>"); 
        if(expand) 
            out.write("<a href='"+link+"&action=expand#"+key+"'><img src='images/arrow_down.png' title='expand' "+imageOptions+" ></a>&nbsp&nbsp\n");
        else 
            out.write("<a href='"+link+"&action=collapse#"+key+"'><img src='images/arrow_up.png' title='collapse' "+imageOptions+" ></a>\n");
        out.write("</td>");
    }
    
    private void printTableTitles(PrintWriter out,String[] titles, String[] dbColNames,String prefix,String anchor)
    {
        String newDir;
        if(titles.length!=dbColNames.length)
        {
            log.error("length mismatch while printing header: titles.length="+titles.length+
                    ", feilds.length="+dbColNames.length);
            //print bare titles
            for(String title : titles)
                out.println("<th>"+title+"</th>");
            return;
        }        
        for(int i=0;i<titles.length;i++)
        {
            newDir="asc"; //default to asc
            
            if(sortCol!=null && sortCol.equals(prefix+"_"+dbColNames[i])) //reverse current sort col
                newDir=(sortDir.equals("asc"))? "desc":"asc"; //flip direction
            out.println("<th nowrap ><a href='QueryPageServlet?hid="+hid+"&sortCol="+prefix+"_"+dbColNames[i]+
                "&sortDirection="+newDir+"#"+anchor+"'>"+titles[i]+"</a></th>");             
        }
    }

   

   

   
}
