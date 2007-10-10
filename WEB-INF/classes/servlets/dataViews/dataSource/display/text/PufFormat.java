/*
 * PufFormat.java
 *
 * Created on September 27, 2007, 9:59 AM
 *
 */

package servlets.dataViews.dataSource.display.text;

import java.io.IOException;
import org.apache.log4j.Logger;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.ExternalUnknownRecord;
import servlets.dataViews.dataSource.records.UnknownRecord;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
public class PufFormat extends  AbstractPatternFormat<UnknownRecord>
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
        out.write("key\tgomf\tpfam\tswp\tdescription\n");
    }

    public void printRecord(UnknownRecord r) throws IOException
    {
        out.write(r.key+"\t");
        printExternalUnknowns(r.childGroup(ExternalUnknownRecord.class));
        out.write(r.description+"\n");
    }

    public void printFooter(UnknownRecord r) throws IOException
    {
    }
   
     private void printExternalUnknowns(Iterable<Record> records) throws IOException
    {
        Boolean gomf,pfam,swp;
        ExternalUnknownRecord eur;

        gomf=pfam=swp=null;

        for(Record rec : records)
        {
            eur=(ExternalUnknownRecord)rec;
            
            if("gomf".equals(eur.source))
                gomf=eur.isUnknown;
            else if("swp".equals(eur.source))
                swp=eur.isUnknown;
            else if("pfam".equals(eur.source))
                pfam=eur.isUnknown;
        }
        out.write(gomf+"\t"+pfam+"\t"+swp+"\t");
    }
}
