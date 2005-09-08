/*
 * BlastCompositeFormat.java
 *
 * Created on September 6, 2005, 12:05 PM
 *
 */

package servlets.dataViews.records.formats;

import java.io.*;
import java.util.*;
import servlets.PageColors;
import servlets.dataViews.records.*;

/**
 *
 * @author khoran
 */
public class BlastCompositeFormat extends CompositeFormat
{
    
    /** Creates a new instance of BlastCompositeFormat */
    public BlastCompositeFormat() 
    {
    }
 
    public void printRecords(Writer out, RecordVisitor visitor,Iterator i)
        throws IOException
    {
        BlastRecord rec;
        boolean firstRecord=true;
        String lastPurpose=null;
        
        Map<String,String> titles=new HashMap<String,String>(); 
        titles.put("UD","Unknown Searches");
        titles.put("orthologs","Ortholog Searches");
                        
        while(i.hasNext())
        {
            rec=(BlastRecord)i.next();
            if(rec.target.equals("no hit")) 
                continue; //skip no hits
            if(firstRecord)
            {
                rec.printHeader(out, visitor);
                firstRecord=false;
            }            
            if(lastPurpose==null || !lastPurpose.equals(rec.purpose))
            {
                out.write("<tr><th align='left' colspan='4' bgcolor='"+PageColors.title+"'>"+
                        titles.get(rec.purpose)+"</th></tr>");
                lastPurpose=rec.purpose;
            }
            rec.printRecord(out, visitor);
            if(!i.hasNext()) //last record
                rec.printFooter(out, visitor);
        }
    }
    
}
