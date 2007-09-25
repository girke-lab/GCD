/*
 * AffyCompFormat.java
 *
 * Created on January 4, 2007, 9:31 AM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import org.apache.log4j.Logger;
import servlets.DescriptionManager;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.AffyCompRecord;
import servlets.dataViews.dataSource.records.AffyDetailRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.querySets.DataViewQuerySet;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class AffyCompFormat extends AbstractPatternFormat<AffyCompRecord>
{
    private static final RecordPattern pattern=buildPattern();
    private static final Logger log=Logger.getLogger(AffyCompFormat.class);
    
    private static final DecimalFormat df=new DecimalFormat("0.00");
    
    /** Creates a new instance of AffyCompFormat */
    public AffyCompFormat()
    {
    }

    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(AffyCompRecord.class);
        p.addChild(AffyDetailRecord.class);
        return p;
    }
    public RecordPattern getPattern()
    {        
        return pattern;
    }

    public void printHeader(AffyCompRecord r) throws IOException
    {
        Utilities.startTable(out);
        String[] titles=new String[]{"Comparison","Exper Type","Mean","PMA","Description" };
        String[] keys=new String[]{"int-comparison","exper-type","mean","pma","exper-description" };
        
        
        out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</td>");
        for(int i=0; i < titles.length; i++)
            out.write("<th>"+DescriptionManager.wrapText(keys[i],titles[i])+"</th>");
        out.write("</tr>");
        
        //out.write("<tr bgcolor='"+PageColors.title+"'><td>&nbsp</td>" +
                            //"<th>Comparision</th><th>Exper Type</th><th>Mean</th><th>PMA</th>" +
                            //"<th>Description</th></tr>");
    }

    public void printRecord(AffyCompRecord r) throws IOException
    {
        String link="QueryPageServlet?hid="+getParameters().getHid()+"&displayType=affyView&es_ids="+r.expSetId+
                    "&psk_ids="+r.probeSetId+"&groups="+r.comparison;
        String controlPopup=""; //"onmouseover=\"return escape('"+r.controlDesc+"')\"";
        String treatPopup=""; //"onmouseover=\"return escape('"+r.treatDesc+"')\"";
        
        out.write("<tr>");
        Utilities.printTreeControls(out,link,r.getPrimaryKey().toString(),r.iterator());     
        out.write("<td "+controlPopup+">"+r.comparison+"</td><td>Control</td>");
        out.write("<td>"+df.format(r.controlMean)+"</td><td>"+r.controlPMA+"&nbsp</td>");
        out.write("<td>"+r.controlDesc+" &nbsp </td>");
        out.write("</tr>");
                
        printChild(r.childGroup(AffyDetailRecord.class),r.probeSetId,"control");
                
        out.write("<td>&nbsp</td><td "+treatPopup+">"+r.comparison+"</td><td>Treatment</td>");
        out.write("<td>"+df.format(r.treatmentMean)+"</td><td>"+r.treatmentPMA+"&nbsp</td>");
        out.write("<td>"+r.treatDesc+" &nbsp </td>");
        out.write("</tr>");                    
        
        printChild(r.childGroup(AffyDetailRecord.class),r.probeSetId,"treatment");
        
    }
    public void printFooter(AffyCompRecord r) throws IOException
    {
        //if(Utilities.hasChildren(r))
        //    Utilities.closeChildCel(out);
        Utilities.endTable(out);
    }
    
    private void printChild(Iterable<Record> records, Integer probeSetId,String expType) throws IOException
    {        
        if(!records.iterator().hasNext())  //no records
            return; 
        
        Utilities.openChildCel(out,5,1);
        Utilities.startTable(out);
        
        printDetailHeader(probeSetId);
        for(Record sr : records)
            printDetailRecord((AffyDetailRecord)sr,expType);
        
        Utilities.endTable(out);
        Utilities.closeChildCel(out);
    }
    
    
    private void printDetailHeader(Integer probeSetId) throws IOException
    {
        String[] titles=new String[]{"Type","Cel File","Intensity","PMA"};
        String[] feilds=QuerySetProvider.getDataViewQuerySet().getSortableAffyColumns()[DataViewQuerySet.DETAIL]; 
        
        out.write("<tr bgcolor='"+PageColors.title+"'><a name='"+probeSetId+"'></a>");
        Utilities.printTableTitles(new PrintWriter(out),getParameters(), titles, feilds,"detail",probeSetId.toString());        
        out.write("</tr>\n"); 
    }
    private void printDetailRecord(AffyDetailRecord r,String expType) throws IOException
    {
        if(!"both".equals(expType) && !r.type.equals(expType))
            return;
        
        out.write("<tr>");                
        out.write("<td>"+r.type+"</td><td>"+r.celFile+"</td>"); //"<td>"+desc+"</td>
        out.write("<td>"+df.format(r.intensity)+"</td>");
        out.write("<td>"+r.pma+"&nbsp</td>");
        out.write("</tr>");
    }
}
