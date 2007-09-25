/*
 * HtmlPatternFactory.java
 *
 * Created on January 3, 2007, 1:23 PM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.DescriptionManager;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.PatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.*;
import servlets.dataViews.dataSource.structure.Record;
import servlets.querySets.DataViewQuerySet;
import servlets.querySets.QuerySetProvider;

/**
 * Creates a set of default formats for printing records in HTML format.
 * 
 * @author khoran
 */
public class HtmlPatternFactory
{
    private static final Logger log=Logger.getLogger(HtmlPatternFactory.class);
    private static HtmlPatternFactory singleton=null;
    private static Utilities utils=new Utilities();
    
    private List<PatternFormat<? extends Record>> patterns=null;
    
    
    private static DecimalFormat df=new DecimalFormat("0.00");
    private static DecimalFormat percent=new DecimalFormat("0%");
    private static DecimalFormat ldf=new DecimalFormat("0.##E0");
    private static final DescriptionManager dm=DescriptionManager.getInstance();
    
    String expDefURL="http://bioweb.ucr.edu/databaseWeb/data/exp_definitions";
    
    /** Creates a new instance of HtmlPatternFactory */
    private HtmlPatternFactory()
    {
        patterns=new LinkedList<PatternFormat<? extends Record>>(); 
        createPatterns();
    }     
    private static HtmlPatternFactory getInstance()
    {
        if(singleton == null)
            singleton=new HtmlPatternFactory();
        return singleton;
    }
           
    public static Collection<PatternFormat<? extends Record>> getAllPatterns()
    {
        return getInstance().patterns;  
    }
    public static PatternFormat getPattern(Class c)
    {
        try{
            return (PatternFormat)c.getDeclaredConstructor(getInstance().getClass()).newInstance(getInstance());
        }catch(Exception e){
            log.error("could not create class "+c.getName(),e);
            return null;
        }
    }    
    private void createPatterns()
    { //load the patterns list                        
        try{
            for(Class c : this.getClass().getDeclaredClasses())                
                patterns.add((PatternFormat)c.getDeclaredConstructor(this.getClass()).newInstance(this));
        }catch(Exception e){
            log.error("could not create all patterns",e);            
        }        
    }    
    
    
    class UnknownRecordFormat extends  AbstractPatternFormat<UnknownRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(UnknownRecord.class);
        }

        public void printHeader( UnknownRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><th>Key</th><th>Description</th></tr>\n");
        }
        public void printRecord( UnknownRecord r) throws IOException
        {
            out.write("<tr><td>");         
            String accUrl="QueryPageServlet?searchType=Id&displayType=seqView&inputKey=";
            
                out.write("<a href='"+accUrl+
                    r.key.subSequence(0,r.key.lastIndexOf('.'))+"'>"+r.key+"</a>");
                
            out.write("</td><td>"+r.description+"</td></tr>\n");
            
            out.write("<tr><td colspan='2'><b>PUF Profile</b>  GOMF: "+Utilities.cn(r.go_unknowns[0]));
            out.write(" &nbsp&nbsp Pfam: "+Utilities.cn(r.pfam_is_unknown)+" &nbsp&nbsp SWP: "+Utilities.cn(r.swp_is_unknown));
            out.write("</td></tr>");
            /*
            String[] names=new String[]{"mfu","ccu","bpu"};
            out.write("<tr><td colspan='2'>\n");
            for(int i=0;i<r.go_unknowns.length;i++)
                out.write("<b>"+names[i]+"</b>: "+r.go_unknowns[i]+" &nbsp&nbsp&nbsp \n");
             */
            
            //String url="QueryPageServlet?searchType=Id&displayType=correlationView&rpp=200&inputKey="+r.key;
            //if(r.getGroupList().contains(AffyExpSetRecord.class))
                //out.write("&nbsp&nbsp&nbsp <a href='"+url+"'><font color='red'>Correlation Data</font></a>&nbsp&nbsp");

            String pskPopup;                
            String clusterLink="QueryPageServlet?displayType=correlationView&searchType=Cluster_Corr" +
                    "&inputKey=";

            out.write("</td></tr>\n");                                       

            if(utils.hasChildren(r))
                utils.openChildCel(out,2);
        }
        public void printFooter( UnknownRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            out.write("<tr><td bgcolor='FFFFFF' colspan='5'>&nbsp</td></tr>\n");
            utils.endTable(out);
        }
    }

    class ProbeClusterFormat extends AbstractPatternFormat<ProbeClusterRecord>
    {
        String lastPskKey=null;
        boolean multipleKeys=true;
        
        public RecordPattern getPattern()
        {
            return new RecordPattern(ProbeClusterRecord.class);
        }

        public void preProcess(Iterable<ProbeClusterRecord> records)
        {
            String key=null;
            multipleKeys=false;
            for(ProbeClusterRecord r : records)
            {
                if(key==null)
                    key=r.pskKey;
                else if(!key.equals(r.pskKey))
                {
                    multipleKeys=true;
                    break;
                }
            }
        }
        public void printHeader(ProbeClusterRecord r) throws IOException
        {
            utils.startTable(out);     
            if(multipleKeys)
                out.write("<tr bgcolor='"+PageColors.title+"'><th>AffyID</th><th>Clusters  name(size)</th></tr>\n");
            else
            {
                out.write("<tr bgcolor='"+PageColors.title+"'><th>Clusters  name(size)</th></tr>\n");
                out.write("<tr><td>"); //all records go in one row
            }
            
            lastPskKey=null;
        }

        public void printRecord(ProbeClusterRecord r) throws IOException
        {          
            String page="QueryPageServlet?" +
                    "displayType=correlationView&searchType=Cluster_Corr&inputKey="+
                    r.probeClusterId+" "+r.pskId;             
            String pskPopup="onmouseover=\"return escape('"+r.method+"<br>"+r.methodDesc+"')\"";
            String corrUrl="QueryPageServlet?searchType=Probe_Set_Key&displayType=correlationView&rpp=200&inputKey="+r.pskId;
            
            if( lastPskKey==null || !lastPskKey.equals(r.pskKey)  )
            {
                if(multipleKeys)
                {
                    if(lastPskKey!=null) // not the first record
                        out.write("</td></tr>"); //close the previous row
                    out.write("<tr><td>"+r.pskKey+"</td><td>");
                }
                out.write("<a href='"+corrUrl+"'><font color='red'>Correlation Data</font></a>&nbsp&nbsp&nbsp ");
                lastPskKey=r.pskKey;
            }
            
            out.write("<a href='"+page+"' "+pskPopup+" >"+r.name+"("+r.size+")</a> &nbsp&nbsp&nbsp ");
            
            if(utils.hasChildren(r))
                utils.openChildCel(out,1);
        }

        public void printFooter(ProbeClusterRecord r) throws IOException
        {
            out.write("</td></tr>"); //end the last cluster(cutoff) row
            if(utils.hasChildren(r))
                utils.closeChildCel(out);            
            utils.endTable(out);
        }

       
        
    }
    class GoRecordFormat extends AbstractPatternFormat<GoRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(GoRecord.class);
        }

        public void printHeader( GoRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><th>Go Number</th><th>Description</th><th>Function</th></tr>\n");
        }

        public void printRecord( GoRecord r) throws IOException
        {
            String link="http://www.godatabase.org/cgi-bin/go.cgi?" +
                "depth=0&advanced_query=&search_constraint=terms&query="+r.go_number+"&action=replace_tree";
            out.write("<tr><td><a href='"+link+"'>"+r.go_number+"</a></td><td>"+r.text+"</td><td>"+r.function+"</td></tr>\n");    
            if(utils.hasChildren(r))
                utils.openChildCel(out,3);
        }

        public void printFooter( GoRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
           utils.endTable(out);
        }            
    }    
    
    class ProbeSetSummaryRecordFormat extends AbstractPatternFormat<ProbeSetSummaryRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ProbeSetSummaryRecord.class);
        }

        public void printHeader( ProbeSetSummaryRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'>" +
                "<th>Affy Probe Set</th>" +
                "<th> Average Intensity</th>" +
                "<th>Standard Deviation</th>" +
                "<th>MFC</th></tr>\n");
        }

        public void printRecord( ProbeSetSummaryRecord r) throws IOException
        {
            
            String link="QueryPageServlet?displayType=affyView&" +
                    "searchType=Probe_Set_Key&limit=1&dbs=0&inputKey="+r.probeSetId; 
            String corrLink="QueryPageServlet?rpp=200&displayType=correlationView&" +
                    "searchType=Probe_Set_Key&inputKey="+r.probeSetId;

            out.write("<tr>");
            out.write("<td><a href='"+link+"'>"+r.probeSetKey+"</a> &nbsp&nbsp " +
                    "<a href='"+corrLink+"'><font color='red'>correlations</font></a></td>");                 
            out.write("<td>"+df.format(r.average)+"</td><td>"+df.format(r.stddev)+"</td>");
            out.write("<td>"+df.format(r.mfc)+"</td>");
            out.write("</td>");
            
            if(utils.hasChildren(r))
                utils.openChildCel(out,5);
        }

        public void printFooter( ProbeSetSummaryRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    
    class BlastRecordFormat extends AbstractPatternFormat<BlastRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(BlastRecord.class);
        }

        public void printHeader(BlastRecord r) throws IOException
        {
            utils.startTable(out);            
            out.write("<tr bgcolor='"+PageColors.title+"'>" +
                "<th>Target Key</th><th>E-value</th>" +
                "<th>Score</th><th>DB/Method</th></tr>\n");          
        }

        public void printRecord(BlastRecord r) throws IOException
        {            
            String target=r.target;        
            if(target.equals(""))
                target="no hit";
            if(!target.equals("no hit") && r.link!=null)
                target="<a href='"+r.link+"'>"+r.target+"</a>";
             out.write("<tr><td>"+target+"</td><td>"+r.evalue+"</td><td>"+
                        (r.score==null || r.score.equals("")?"&nbsp":r.score)+"</td>" +
                        "<td>"+r.dbname+"/"+r.method+"</td></tr>\n");            
             if(utils.hasChildren(r))
                utils.openChildCel(out,4);
        }

        public void printFooter(BlastRecord r) throws IOException
        {         
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    class ProteomicsRecordFormat extends AbstractPatternFormat<ProteomicsRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ProteomicsRecord.class);
        }

        public void printHeader(ProteomicsRecord r) throws IOException
        {
            utils.startTable(out);
            String prob="Probability";
            if(r.prob_is_neg)
                prob="Improbability";
            out.write("<tr bgcolor='"+PageColors.title+"'><th>MW</th><th>IP</th><th>Charge</th><th>"+prob+"</th></tr>\n");   
        }
        public void printRecord(ProteomicsRecord r) throws IOException
        {
            out.write("<tr><td>"+r.mol_weight+"</td><td>"+r.ip+"</td><td>"+r.charge+"</td><td>"+r.prob+"</td></tr>\n");
            if(utils.hasChildren(r))
                utils.openChildCel(out,4);
        }
        public void printFooter(ProteomicsRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }        
    }
    class ClusterRecordFormat extends AbstractPatternFormat<ClusterRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ClusterRecord.class);
        }

        public void printHeader(ClusterRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><th>Clusters size(method)</th></tr>\n");
            out.write("<tr><td>"); //all records go in one row
        }
        public void printRecord(ClusterRecord r) throws IOException
        {
            
            String page="QueryPageServlet?searchType=Cluster Id&displayType=seqView&inputKey="+r.key;

            out.write("<a href='"+page+"'>"+r.size+"("+r.method+")</a> &nbsp&nbsp&nbsp ");
            //out.write("</table></td></tr><tr><td>"); //set up another row for future cluster(cutoff) entries
            if(utils.hasChildren(r))
                utils.openChildCel(out,1);
        }
        public void printFooter(ClusterRecord r) throws IOException
        {
            out.write("</td></tr>"); //end the last cluster(cutoff) row
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }        
    }
    class ExternalUnknownRecordFormat extends AbstractPatternFormat<ExternalUnknownRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ExternalUnknownRecord.class);
        }

        public void printHeader(ExternalUnknownRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr><th bgcolor='"+PageColors.title+"'>External Sources</th></tr>\n");
            out.write("<tr><td>");
        }
        public void printRecord(ExternalUnknownRecord r) throws IOException
        {
            out.write("<b>"+r.source+":</b> "+(r.isUnknown? "unknown" : "known")+" &nbsp&nbsp&nbsp ");
            if(utils.hasChildren(r))
                utils.openChildCel(out,1);
        }
        public void printFooter(ExternalUnknownRecord r) throws IOException
        {
            out.write("</td></tr>");
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }       
    }
    class AffyExpSetRecordFormat extends  AbstractPatternFormat<AffyExpSetRecord>
    {
        Integer lastProbeKeyId=null;
        public RecordPattern getPattern()
        {
            return new RecordPattern(AffyExpSetRecord.class);
        }

        public void printHeader( AffyExpSetRecord r) throws IOException
        {
            String[] titles=new String[]{"AffyID","Exp","Name","up 2x","down 2x",
                                 "up 4x","down 4x","on","off",
                                 "Average","Stddev","MFC"};
            String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.EXPSET]; 

            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><td><a name='"+r.probeSetId+"'></a>"+
                    dm.wrapText("ratio","Ratio")+"</td><td>"+dm.wrapText("int","Int")+"</td>");        
            utils.printTableTitles(new PrintWriter(out),getParameters(), titles, feilds, "expset",r.probeSetId.toString());        
            out.write("</tr>");
            
            lastProbeKeyId=null;
        }

        public void printRecord( AffyExpSetRecord r) throws IOException
        {
             String link="QueryPageServlet?hid="+getParameters().getHid()+
                     "&displayType=affyView&es_ids="+r.expSetId+"&psk_ids="+r.probeSetId;

            String key=r.expSetId+"_"+r.probeSetId;        

            String expSetKeyLink=r.info_link.replaceAll("\\$\\{key\\}",r.expSetKey);   

            String popup="onmouseover=\"return escape('";
            if(r.long_name!=null && !r.long_name.equals(""))
                popup+="Data Source: "+r.long_name+"<p>";
            popup+=r.description+"')\"";          

            if(lastProbeKeyId==null)
                lastProbeKeyId=r.probeSetId;
            else if(!lastProbeKeyId.equals(r.probeSetId))
            {
                log.error("writing separator");
                out.write("<tr bgcolor='FFFFFF'><td colspan='15'> &nbsp </td></tr>");
                lastProbeKeyId=r.probeSetId;
            }
            out.write("<tr bgcolor='"+PageColors.catagoryColors.get(r.catagory)+"'>");
            
            String compView=getParameters().getCompView();
            if(compView!=null && compView.equals("all"))
            {
                utils.printTreeControls(out,link+"&comp_view=all",key,r.iterator());    
                utils.printTreeControls(out,link+"&comp_view=comp",key,true);                
            }
            else if(compView!=null && compView.equals("comp"))
            {
                utils.printTreeControls(out,link+"&comp_view=all",key,true);    
                utils.printTreeControls(out,link+"&comp_view=comp",key,r.iterator());    
            }
            else
            {
                utils.printTreeControls(out,link+"&comp_view=all",key,r.iterator());    
                utils.printTreeControls(out,link+"&comp_view=comp",key,r.iterator());    
            }

            out.write("<td>"+r.probeSetKey+"</td><td>");
            if(expSetKeyLink!=null && !expSetKeyLink.equals(""))              
                out.write("<a href='"+expSetKeyLink+"' "+popup+">"+r.expSetKey+"</a>");                    
            else
                out.write("<span "+popup+">"+r.expSetKey+"</span>");
            out.write("</td>");
            out.write("<td>"+(r.name.equals("")?"&nbsp":r.name)+"</td>"); //  <td>"+r.description+"</td>");
            out.write("<td>"+r.up2+"</td><td>"+r.down2+"</td><td>"+r.up4+"</td>");
            out.write("<td>"+r.down4+"</td><td>"+(r.on==null?"&nbsp":r.on)+"</td><td>"+(r.off==null?"&nbsp":r.off)+"</td>");
            out.write("<td>"+(r.average==null ? "&nbsp":df.format(r.average))+
                    "</td><td>"+(r.stddev==null ? "&nbsp":df.format(r.stddev))+"</td>");
            out.write("<td>"+(r.mfc==null ? "&nbsp":df.format(r.mfc))) ;
            out.write("</tr>");

            String expDefLink="<a href='"+expDefURL+"/Ex"+
                r.expSetKey+"'>E<br>X<br>P <p> D<br>E<br>F</a>";

            if(utils.hasChildren(r))
                utils.openChildCel(out,14,1,expDefLink);

        }

        public void printFooter( AffyExpSetRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
    }
    class AffyCompRecordFormat extends AbstractPatternFormat<AffyCompRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(AffyCompRecord.class);
        }

        public void printHeader( AffyCompRecord r) throws IOException
        {
            String[] titles=new String[]{"Comparison","Control mean","Treat mean",
                                "control pma","treat pma","ratio (log2)","contrast",
                                "P-value","adj P-value","pfp up","pfp down"};
            String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.COMP]; 

            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><td><a name='"+r.probeSetId+"'>&nbsp</a></td>");
            utils.printTableTitles(new PrintWriter(out),getParameters(), titles, feilds,"comp",r.probeSetId.toString());        
            out.write("</tr>");        
        }
        public void printRecord( AffyCompRecord r) throws IOException
        {
            String link="QueryPageServlet?hid="+getParameters().getHid()+"&displayType=affyView&es_ids="+r.expSetId+
                "&psk_ids="+r.probeSetId+"&groups="+r.comparison;
            String key=r.expSetId+"_"+r.probeSetId+"_"+r.comparison;       
            String alignment="align=left valign=top ";
            String popupData="<table><tr "+alignment+"><th >Control</th><td>"+r.controlDesc+"</td></tr>"+
                    "<tr "+alignment+"><th>Treatment</th><td>"+r.treatDesc+"</td></tr></table>";
            String popup="onmouseover=\"return escape('"+popupData+"')\"";  

            out.write("<tr >");       
           utils. printTreeControls(out,link,key,r.iterator());                
            out.write("<td "+popup+" >"+r.comparison+"</td><td>"+df.format(r.controlMean)+"</td>");
            out.write("<td>"+df.format(r.treatmentMean)+"</td><td>"+r.controlPMA+"&nbsp</td>");
            out.write("<td>"+r.treatmentPMA+"&nbsp</td><td>"+df.format(r.ratio)+"</td>");
            out.write("<td>"+df.format(r.contrast)+"</td><td>"+ldf.format(r.pValue)+"</td><td>"+ldf.format(r.adjPValue)+"</td>");
            out.write("<td>"+df.format(r.pfpUp)+"</td><td>"+df.format(r.pfpDown)+"</td>");
            out.write("</tr>");
         
            if(utils.hasChildren(r))
                utils.openChildCel(out,11,1);            
        }
        public void printFooter( AffyCompRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }               
    }
    class AffyDetailRecordFormat extends AbstractPatternFormat<AffyDetailRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(AffyDetailRecord.class);
        }

        public void printHeader( AffyDetailRecord r) throws IOException
        {
            String[] titles=new String[]{"Type","Cel File","Intensity","PMA"};
            String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.DETAIL]; 

            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><a name='"+r.probeSetId+"'></a>");
            utils.printTableTitles(new PrintWriter(out),getParameters(), titles, feilds,"detail",r.probeSetId.toString());        
            out.write("</tr>\n");        
        }

        public void printRecord( AffyDetailRecord r) throws IOException
        {
            out.write("<tr>");                
            out.write("<td>"+r.type+"</td><td>"+r.celFile+"</td>"); //"<td>"+desc+"</td>
            out.write("<td>"+df.format(r.intensity)+"</td>");
            out.write("<td>"+r.pma+"&nbsp</td>");
            out.write("</tr>");
            if(utils.hasChildren(r))
                utils.openChildCel(out,4,1);
        }

        public void printFooter( AffyDetailRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    
    class CorrelationRecordFormat extends AbstractPatternFormat<CorrelationRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(CorrelationRecord.class);
        }

        public void printHeader(CorrelationRecord r) throws IOException
        {
            utils.startTable(out);
            String[] titles=new String[]{"Catagory","Pearson","Spearman"};
            String[] keys=new String[]{"psk2_key","pearson","spearman"};
            
            out.write("<tr bgcolor='"+PageColors.title+"'>");
            for(int i=0; i < titles.length; i++)
                out.write("<th>"+dm.wrapText(keys[i],titles[i])+"</th>");
            out.write("</tr>");
        }

        public void printRecord(CorrelationRecord r) throws IOException
        {            
            //QueryPageServlet?displayType=affyView&searchType=id&dbs=0&inputKey=exact%20At1g01070.1
            String url="QueryPageServlet?displayType=affyView&searchType=Probe_Set&inputKey="+r.psk2_key;
            out.write("<tr>");
            out.write("<td><a href='"+url+"'>"+r.psk2_key+"</a></td><td>"+r.pearson+"</td>");
            out.write("<td>"+r.spearman+"</td></tr>");
            if(utils.hasChildren(r))
                utils.openChildCel(out,3);
        }

        public void printFooter(CorrelationRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    
    class ComparisonPskRecordFormat extends AbstractPatternFormat<ComparisonPskRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ComparisonPskRecord.class);
        }

        public void printHeader(ComparisonPskRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'>");
            String[] titles=new String[]{"AffyID","Exp","Clusters","Comparison","Control Mean",
                                "Treat Mean","Control PMA","Treat PMA","ratio (log2)",
                                "Contrast","p-value","Adjusted p-value","PFP up",
                                "PFP down","Control Description","Treatment Description"};

        //        String[] dbNames=QuerySetProvider.getDataViewQuerySet().getSortableTreatmentColoumns();
        //        printTableTitles(new PrintWriter(out),titles,dbNames,"treatment","");
        //        
            out.write("</tr>");  
        }

        public void printRecord(ComparisonPskRecord r) throws IOException
        {
            out.write("<tr>");

            String expSetKeyLink=r.info_link.replaceAll("\\$\\{key\\}",r.expSetKey);   
            String pskLink="";
            String clusterLink="QueryPageServlet?displayType=probeSetView&searchType=Psk_Cluster" +
                    "&comparisonIds="+r.comparison_id+"&inputKey=";

            String pskPopup;                
            String expSetPopup="onmouseover=\"return escape('";

            if(r.sourceName!=null && !r.sourceName.equals(""))
                expSetPopup+="Data Source: "+r.sourceName+"<p>";
            expSetPopup+=r.expDesc+"')\""; 

            String clusters="";
            for(int i=0;i<r.cluster_ids.length;i++)
            {
                pskPopup="onmouseover=\"return escape('"+r.clusterNames[i]+"<br>"+r.methods[i]+"')\"";
                clusters+="<a href='"+clusterLink+r.cluster_ids[i]+"' "+pskPopup+">"+
                        r.clusterNames[i]+"("+r.sizes[i]+")</a> &nbsp&nbsp ";
            }


            Object[] values=new Object[]{
                "<a href='"+pskLink+"' >"+r.probeSetKey+"</a>",
                "<a href='"+expSetKeyLink+"' "+expSetPopup+" >"+r.expSetKey+"</a>",
                clusters, r.group_no, r.controlMean,
                r.treatmentMean, r.controlPMA, r.treatmentPMA, r.ratio,
                r.contrast, r.pValue, r.adjPValue, r.pfpUp, r.pfpDown,
                r.controlDesc, r.treatDesc
            };
            for(Object v : values)
                out.write("<td nowrap >"+v+"</td>");

            out.write("</tr>");
            if(utils.hasChildren(r))
                utils.openChildCel(out,values.length);
        }

        public void printFooter(ComparisonPskRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    
    class ComparisonRecordFormat extends AbstractPatternFormat<ComparisonRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ComparisonRecord.class);
        }

        public void printHeader(ComparisonRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'>");
            String[] titles=new String[]{"Experiment Set","Comparison",
                                         "Control Description","Treatment Description"};

            String[] dbNames=QuerySetProvider.getDataViewQuerySet().getSortableTreatmentColoumns()[DataViewQuerySet.TREAT_COMP];
            utils.printTableTitles(new PrintWriter(out),getParameters(),titles,dbNames,"comparison","","width='10%'");

            out.write("<td>&nbsp</td>");
            out.write("</tr>");  
        }

        public void printRecord(ComparisonRecord r) throws IOException
        {
            
            out.write("<tr bgcolor='"+PageColors.catagoryColors.get(r.catagory)+"'>");

            String expSetKeyLink=r.infoLink.replaceAll("\\$\\{key\\}",r.expSetKey);   

            String expSetPopup="onmouseover=\"return escape('";

            if(r.sourceName!=null && !r.sourceName.equals(""))
                expSetPopup+="Data Source: "+r.sourceName+"<p>";
            expSetPopup+=r.expDesc+"')\""; 

            Object[] values=new Object[]{            
                "<a href='"+expSetKeyLink+"' "+expSetPopup+" >"+r.expSetKey+"</a>",
                r.comparison,
                r.controlDesc, r.treatmentDesc,"&nbsp"
            };
    //        String thOptions="align='left' bgcolor='"+PageColors.title+"' width='10%'";
    //        String tdOptions="bgcolor='"+PageColors.catagoryColors.get(cr.catagory)+"'";
            for(int i=0;i<values.length;i++)
                out.write("<td nowrap >"+values[i]+"</td>");
            out.write("</tr>");
             
            if(utils.hasChildren(r))
                utils.openChildCel(out,values.length);
        }

        public void printFooter(ComparisonRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    
    class ProbeSetKeyRecordFormat extends AbstractPatternFormat<ProbeSetKeyRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(ProbeSetKeyRecord.class);
        }

        public void printHeader(ProbeSetKeyRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'>");
            String[] titles=new String[]{"AffyID","Accessions","Control Mean",
                                "Treat Mean","Control PMA","Treat PMA","ratio (log2)",
                                "Contrast","p-value","Adjusted p-value","PFP up",
                                "PFP down","Clusters","Accession Descriptions"};

            String[] dbNames=QuerySetProvider.getDataViewQuerySet().getSortableTreatmentColoumns()[DataViewQuerySet.TREAT_PSK];
            utils.printTableTitles(new PrintWriter(out),getParameters(),titles,dbNames,"psk","");

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
            for(int i=0;i<r.cluster_ids.length;i++)
            {
                pskPopup="onmouseover=\"return escape('"+r.clusterNames[i]+"<br>"+r.methods[i]+"')\"";
                clusters+="<a href='"+clusterLink+r.cluster_ids[i]+" "+r.comparisonId+"' "+pskPopup+">"+
                        r.clusterNames[i]+"("+r.sizes[i]+")</a> &nbsp&nbsp ";
            }       
            StringBuilder accSb=new StringBuilder();
            StringBuilder descSb=new StringBuilder();

            for(String s : r.accessions)
                accSb.append("<a href='"+accLink+s+"'>"+s+"</a> &nbsp ");
            for(String s : r.accDescriptions)
                descSb.append(s+" &nbsp&nbsp&nbsp ");

            Object[] values=new Object[]{
                "<a href='"+pskLink+"' >"+r.probeSetKey+"</a>",accSb.toString(),
                r.controlMean,r.treatmentMean, 
                utils.cn(r.controlPMA), utils.cn(r.treatmentPMA), r.ratio,
                r.contrast, r.pValue, r.adjPValue, 
                r.pfpUp, r.pfpDown,clusters,descSb.toString()
            };
            for(Object v : values)
                out.write("<td nowrap >"+v+"</td>");


            out.write("</tr>");
            if(utils.hasChildren(r))
                utils.openChildCel(out,values.length);
        }

        public void printFooter(ProbeSetKeyRecord r) throws IOException
        {
            if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }
        
    }
    
    class SequenceRecordFormat extends AbstractPatternFormat<SequenceRecord>
    {
        public RecordPattern getPattern()
        {
            return new RecordPattern(SequenceRecord.class);
        }
        public void printHeader(SequenceRecord r) throws IOException
        {
            utils.startTable(out);
            out.write("<tr bgcolor='"+PageColors.title+"'><th>Key</th><th>Description</th>");
        }
        public void printRecord(SequenceRecord r) throws IOException
        {
            out.write("<tr><td>"+r.key+"</td><td>"+r.description+"</td></tr>");
            
             if(utils.hasChildren(r))
                utils.openChildCel(out,2);
        }
        public void printFooter(SequenceRecord r) throws IOException
        {
             if(utils.hasChildren(r))
                utils.closeChildCel(out);
            utils.endTable(out);
        }        
    }
   
    
    
   
}


