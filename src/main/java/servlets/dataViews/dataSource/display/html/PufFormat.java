/*
 * PufFormat.java
 *
 * Created on September 26, 2007, 1:09 PM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import org.apache.log4j.Logger;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.ExternalUnknownRecord;
import servlets.dataViews.dataSource.records.UnknownRecord;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
public class PufFormat extends AbstractPatternFormat<UnknownRecord>
{
    
    private static final Logger log=Logger.getLogger(PufFormat.class);
    
    /** Creates a new instance of PufFormat */
    public PufFormat()
    {
    }

    public RecordPattern getPattern()
    {
        RecordPattern p=new RecordPattern(UnknownRecord.class);
        p.addChild(ExternalUnknownRecord.class);
        return p;
    }

    public void printHeader(UnknownRecord r) throws IOException
    {
        Utilities.startTable(out);
        String[] titles=new String[]{"Key","GOMF","Pfam","SWP","Description"};
        
        out.write("<tr bgcolor='"+PageColors.title+"'>");
        for(int i=0; i < titles.length; i++)
            out.write("<th>"+titles[i]+"</th>");
        out.write("</tr>");
    }

    public void printRecord(UnknownRecord r) throws IOException
    {
        out.write("<tr>");
        out.write("<td>"+r.key+"</td>");

        printExternalUnknowns(r.childGroup(ExternalUnknownRecord.class));

        out.write("<td>"+r.description+"</td>");

        out.write("</tr>");
    }

    public void printFooter(UnknownRecord r) throws IOException
    {
        Utilities.endTable(out);
    }
    
    private void printExternalUnknowns(Iterable<Record> records) throws IOException
    {

        String gomf,pfam,swp;
        ExternalUnknownRecord eur;

        gomf=pfam=swp="&nbsp";

        for(Record rec : records)
        {
            eur=(ExternalUnknownRecord)rec;
            
            if("gomf".equals(eur.source))
                gomf=Utilities.asUnknown(eur.isUnknown);
            else if("swp".equals(eur.source))
                swp=Utilities.asUnknown(eur.isUnknown);
            else if("pfam".equals(eur.source))
                pfam=Utilities.asUnknown(eur.isUnknown);
        }
        out.write("<td>"+gomf+"</td><td>"+pfam+"</td><td>"+swp+"</td>");
    }
}
