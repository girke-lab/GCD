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
import servlets.Common;
import org.apache.log4j.Logger;
import servlets.querySets.DataViewQuerySet;
import servlets.querySets.QuerySetProvider;
/**
 * This class implements RecordVisitor so that it can print Records in 
 * html format.  Each record gets its own table, which can be nested inside
 * each other.
 */
public class HtmlRecordVisitor implements RecordVisitor
{
    
    private static Logger log=Logger.getLogger(HtmlRecordVisitor.class);
    int hid;
    String sortDir,sortCol;
    
    /** Creates a new instance of HtmlRecordVisitor */
    public HtmlRecordVisitor()
    {
    }
    public void setHid(int hid)
    {
        this.hid=hid;
    }
    public void setSortInfo(String col,String dir)
    {
        sortCol=col;
        sortDir=dir;
    }
    public void printHeader(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
         out.write("<tr bgcolor='"+Common.titleColor+"'><th>Go Number</th><th>Description</th><th>Function</th></tr>\n");
    }
    
    public void printHeader(java.io.Writer out, BlastRecord br) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+Common.titleColor+"'>" +
                "<th>Target Key</th><th>E-value</th>" +
                "<th>Score</th><th>DB/Method</th></tr>\n");
    }
    // <editor-fold defaultstate="collapsed" desc=" misc records ">
    public void printHeader(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+Common.titleColor+"'><th>Key</th><th>Description</th></tr>\n");
    }
    
    public void printRecord(java.io.Writer out, GoRecord gr) throws java.io.IOException
    {
         String link="http://www.godatabase.org/cgi-bin/go.cgi?" +
            "depth=0&advanced_query=&search_constraint=terms&query="+gr.go_number+"&action=replace_tree";
         out.write("<tr><td><a href='"+link+"'>"+gr.go_number+"</a></td><td>"+gr.text+"</td><td>"+gr.function+"</td></tr>\n");
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
    public void printRecord(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {
        out.write("<tr><td><a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+
            ur.key.subSequence(0,ur.key.lastIndexOf('.'))+"'>"+ur.key+"</a></td><td>"+ur.description+"</td></tr>\n");
        String[] names=new String[]{"mfu","ccu","bpu"};
        out.write("<tr><td colspan='2'>\n");
        for(int i=0;i<ur.go_unknowns.length;i++)
            out.write("<b>"+names[i]+"</b>: "+ur.go_unknowns[i]+" &nbsp&nbsp&nbsp \n");
        out.write("</td></tr>\n");               
                                
        printSubRecords(out, ur.subRecords.values(),5,0);        
        out.write("<tr><td bgcolor='FFFFFF' colspan='5'>&nbsp</td></tr>\n");                
    }
   
    public void printFooter(java.io.Writer out, UnknownRecord ur) throws java.io.IOException
    {        
        
    }
    
    public void printHeader(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        String prob="Probability";
        if(pr.prob_is_neg)
            prob="Improbability";
        out.write("<tr bgcolor='"+Common.titleColor+"'><th>MW</th><th>IP</th><th>Charge</th><th>"+prob+"</th></tr>\n");
    }      
    public void printRecord(java.io.Writer out, ProteomicsRecord pr) throws java.io.IOException
    {
        out.write("<tr><td>"+pr.mol_weight+"</td><td>"+pr.ip+"</td><td>"+pr.charge+"</td><td>"+pr.prob+"</td></tr>\n");
    }
    
    public void printHeader(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write("<tr bgcolor='"+Common.titleColor+"'><th>Cluster Size(Method)</th></tr>\n");
        out.write("<tr><td>"); //all records go in one row
    }    
    public void printRecord(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        //a relative url won't work here becuase we are under /unknowns/... , 
        // but we need to link to pages under /databaseWeb/... .
        String page="http://bioweb.ucr.edu/databaseWeb/QueryPageServlet?searchType=Cluster Id&displayType=seqView&inputKey="+cr.key;
        
        out.write("<a href='"+page+"'>"+cr.size+"("+cr.method+")</a> &nbsp&nbsp&nbsp ");
        if(cr.showClusterCentricView)
        { //print the list of keys that are in this cluster
            int colNum=3;
            int length=cr.keys.size();
            int keysPerCol=(int)(length/colNum);
            
            out.write("</td></tr>"); //end the cluster(cutoff) row
            out.write("<tr><td colspan='5'><table bgcolor='"+Common.dataColor+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='0'>\n");
            out.write("<tr  bgcolor='"+Common.titleColor+"'><th colspan='"+colNum+"'>Cluster Members</th></tr>");
            for(int i=0;i<length;i++)
            {
                out.write("<tr>");
                for(int c=0;i<colNum;c++)
                    if(i+c*keysPerCol < length)
                        out.write("<td>"+cr.keys.get(i+c*keysPerCol)+"</td>");
                    else
                        out.write("<td>&nbsp</td>");
                out.write("</tr>");
            }
            out.write("</table></td></tr><tr><td>"); //set up another row for future cluster(cutoff) entries
        }
    }    
    public void printFooter(java.io.Writer out, ClusterRecord cr) throws java.io.IOException
    {
        out.write("</td></tr>"); //end the last cluster(cutoff) row
    }
    
    public void printHeader(java.io.Writer out, ExternalUnknownRecord eur) throws java.io.IOException
    {
        out.write("<tr><th bgcolor='"+Common.titleColor+"'>External Sources</th></tr>\n");
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
    //</editor-fold>
    
    // <editor-fold desc=" Affy record stuff ">
    ///////////////////////// Affy stuff  ////////////////////////////////////
    public void printHeader(Writer out, AffyExpSetRecord ar) throws IOException
    {

        String[] titles=new String[]{"AffyID","Exp","up 2x","down 2x","up 4x","down 4x","on","off"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.EXPSET]; 
        
        out.write("<tr bgcolor='"+Common.titleColor+"'><td><a name='"+ar.probeSetId+"'>&nbsp</a></td>");        
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
        out.write("<tr>");
        
        printTreeControls(out,link,key,ar.subRecords);    
        out.write("<td>"+ar.probeSetKey+"</td><td><a href='"+expSetKeyLink+"'>"+
                ar.expSetKey+"</a></td>");
        out.write("<td>"+ar.up2+"</td><td>"+ar.down2+"</td><td>"+ar.up4+"</td>");
        out.write("<td>"+ar.down4+"</td><td>"+ar.on+"</td><td>"+ar.off+"</td>");
        out.write("</tr>");
        
        //print sub records
        printSubRecords(out, ar.subRecords,8,1);
    }
    public void printFooter(Writer out, AffyExpSetRecord ar) throws IOException
    {
    }

    public void printHeader(Writer out, AffyCompRecord ar) throws IOException
    {        
        String[] titles=new String[]{"Comparison","Control mean","Treat mean",
                                "control pma","treat pma","ratio (log2)"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.COMP]; 

        out.write("<tr bgcolor='"+Common.titleColor+"'><td><a name='"+ar.probeSetId+"'>&nbsp</a></td>");
        printTableTitles(new PrintWriter(out), titles, feilds,"comp",ar.probeSetId.toString());        
        out.write("</tr>");        
    }
    public void printRecord(Writer out, AffyCompRecord ar) throws IOException
    {        
        String link="QueryPageServlet?hid="+hid+"&displayType=affyView&es_ids="+ar.expSetId+
                "&psk_ids="+ar.probeSetId+"&groups="+ar.comparison;
        String key=ar.expSetId+"_"+ar.probeSetId+"_"+ar.comparison;
        
        DecimalFormat df=new DecimalFormat("0.00");        

        out.write("<tr>");       
        printTreeControls(out,link,key,ar.subRecords);                
        out.write("<td>"+ar.comparison+"</td><td>"+df.format(ar.controlMean)+"</td>");
        out.write("<td>"+df.format(ar.treatmentMean)+"</td><td>"+ar.controlPMA+"</td>");
        out.write("<td>"+ar.treatmentPMA+"</td><td>"+df.format(ar.ratio)+"</td>");
        out.write("</tr>");
        
        printSubRecords(out,ar.subRecords,6, 1);
    }
   
    public void printFooter(Writer out, AffyCompRecord ar) throws IOException
    {
    }
    
    public void printHeader(Writer out, AffyDetailRecord ar) throws IOException
    {
        String[] titles=new String[]{"Type","Cel File","Intensity","PMA"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.DETAIL]; 
        
        out.write("<tr bgcolor='"+Common.titleColor+"'><a name='"+ar.probeSetId+"'>&nbsp</a>");
        printTableTitles(new PrintWriter(out), titles, feilds,"detail",ar.probeSetId.toString());        
        out.write("</tr>\n");        
    }
    public void printRecord(Writer out, AffyDetailRecord ar) throws IOException
    {
        DecimalFormat df=new DecimalFormat("0.00");   
        out.write("<tr>");                
        //String desc=(ar.description==null || ar.description.equals(""))?"&nbsp":ar.description;
        
        out.write("<td>"+ar.type+"</td><td>"+ar.celFile+"</td>"); //"<td>"+desc+"</td>
        out.write("<td>"+df.format(ar.intensity)+"</td>");
        out.write("<td>"+ar.pma+"</td>");
        out.write("</tr>");
    }
    public void printFooter(Writer out, AffyDetailRecord ar) throws IOException
    {
    }
    //</editor-fold>
    
    ////////////////////////////////////////////////////////////////////////////
    
    private void printSubRecords(java.io.Writer out, Collection recordGroups,int span, int shift) throws java.io.IOException
    {
        if(recordGroups==null || recordGroups.size()==0)
            return;
        String spaces="";
        for(int i=0;i<shift;i++)
            spaces+="<td>&nbsp</td>";
        RecordGroup rg;
        for(Iterator i=recordGroups.iterator();i.hasNext();)
        {
            rg=(RecordGroup)i.next();
            if(rg.records==null || rg.records.size()==0)
                continue;
            out.write("<tr>"+spaces);            
            out.write("<td colspan='"+span+"'><TablE bgcolor='"+Common.dataColor+"' width='100%'" +
                " border='1' cellspacing='0' cellpadding='0'>\n");
            rg.printRecords(out,this);
            out.write("</TablE></td></tr>\n");
        }                    
    }
    private void printTreeControls(Writer out, String link, String key,List records)  throws IOException
    {
        String imageOptions=" border='0' height='10' width='15' ";
        Iterator i=null;
        if(records!=null && records.size()!=0)
            i=((RecordGroup)records.get(0)).iterator();                
     
        out.write("<td nowrap>"+"<a name='"+key+"'></a>"); 
        if(i==null || !i.hasNext()) //no children
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
