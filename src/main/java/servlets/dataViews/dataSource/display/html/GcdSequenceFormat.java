/*
 * GcdSequenceFormat.java
 *
 * Created on September 27, 2007, 11:56 AM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.ExternalUnknownRecord;
import servlets.dataViews.dataSource.records.GoRecord;
import servlets.dataViews.dataSource.records.ModelRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.dataSource.structure.Record;

/**
 *
 * @author khoran
 */
public class GcdSequenceFormat extends AbstractPatternFormat<SequenceRecord>
{
    
    private static final Logger log=Logger.getLogger(GcdSequenceFormat.class);
    private static final RecordPattern pattern=buildPattern();
    
    /** Creates a new instance of GcdSequenceFormat */
    public GcdSequenceFormat()
    {
    }

    private static RecordPattern buildPattern()
    {
        RecordPattern p=new RecordPattern(SequenceRecord.class);
        p.addChild(GoRecord.class);
        //p.addChild(ClusterRecord.class);
        p.addChild(ModelRecord.class);
        p.addChild(ExternalUnknownRecord.class);
        return p;
    }
    public RecordPattern getPattern()
    {
        return pattern;
    }

    public void printHeader(SequenceRecord r) throws IOException
    {
        Utilities.startTable(out);
        out.write("<tr bgcolor='"+PageColors.title+"' >");
        out.write("<th align='left'>Key</th><th align='left'>Description");
        out.write("</th>");

        out.write("</tr>");
    }

    public void printRecord(SequenceRecord r) throws IOException
    {
        String keyLink="http://bioweb.ucr.edu/scripts/seqview.pl?database=all&accession=";
        out.write("<tr>");
        out.write("<td><a href='"+keyLink+r.key+"' >"+r.key+"</a></td>");
        out.write("<td>"+r.description+"</td></tr>");

        out.write("<tr><td>PED / POND</td><td>");
        printPedLinks(r.key,r.genome);
        out.write(" &nbsp&nbsp&nbsp ");
        printPUF(r.childGroup(ExternalUnknownRecord.class));
        out.write("</td></tr>");

        out.write("<tr><td>Links</td><td>");
        printLinks(r.key,r.genome,
                                r.childGroup(GoRecord.class),
                                r.childGroup(ModelRecord.class));
        out.write("&nbsp</td>");

        if(Utilities.hasChildren(r))
                Utilities.openChildCel(out,2);
    }

    public void printFooter(SequenceRecord r) throws IOException
    {
        if(Utilities.hasChildren(r))
            Utilities.closeChildCel(out);
        out.write("<tr><td bgcolor='FFFFFF' colspan='5'>&nbsp</td></tr>\n");
        Utilities.endTable(out);
    }
    
    private void printLinks(String key,String genome,Iterable<Record> goRecords,Iterable<Record> modelRecords)    
        throws IOException
    {//size is cluster size
         String db=null;
         int g=Common.getDBid(genome);
         if(g==Common.arab)
             db="ath1";
         else if(g==Common.rice)
             db="osa1";

         if(g==Common.arab)
         {
             out.write("<a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+key+"'>TAIR</a>&nbsp&nbsp");
             out.write("<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+ key+"'>MIPS</a>&nbsp&nbsp");
         }
         out.write("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+ "ORF_infopage.cgi?db="+db+"&orf="+key+"'>TIGR</a>&nbsp&nbsp");
         out.write("<a href='http://bioweb.ucr.edu/scripts/geneview.pl?accession="+key+"'>GeneStructure*</a>&nbsp&nbsp");
         //expression link goes here
         if(g==Common.arab)
            out.write("<a href='http://signal.salk.edu/cgi-bin/tdnaexpress?GENE="+key+"&FUNCTION=&JOB=HITT&DNA=&INTERVAL=10'>KO</a>&nbsp&nbsp");

         //here we want an array of go numbers
         StringBuffer querys=new StringBuffer();

         for(Record rec : goRecords)
             querys.append("query="+ ((GoRecord)rec).go_number.replaceFirst(":","%3A")+"&"); //the : must be encoded
         if(querys.length()!=0)//we have at least one go number
            out.write("<a href='http://www.godatabase.org/cgi-bin/go.cgi?depth=0&advanced_query=&search_constraint=terms&"+querys+"action=replace_tree'>GO</a>&nbsp&nbsp");

         //does this link work for rice? no
         out.write("<a href='http://www.genome.ad.jp/dbget-bin/www_bget?ath:"+key+"'>KEGG</a>&nbsp&nbsp");         

         if(g==Common.arab)
            out.write("<a href='http://www.arabidopsis.org:1555/ARA/NEW-IMAGE?type=GENE&object="+key+"'>AraCyc</a>");

         //link to uniprot blast results
         StringBuffer modelList=new StringBuffer();
         for(Record rec : modelRecords)
             modelList.append( ((ModelRecord)rec).key+" ");
         out.write("&nbsp&nbsp&nbsp<a href='QueryPageServlet?searchType=blast&displayType=blastView&inputKey=dbs: swp trembl keys: "+modelList+"'>" +
                     "<font color='red' >Cross-Species Profile</font></a>");
         
         //printPedLinks(out,key,genome);
    }        
    private void printPedLinks(String key,String genome) throws IOException
    {
         if(Common.getDBid(genome)==Common.arab)// link to affy page
         {
             String affyUrl="QueryPageServlet?searchType=Id&displayType=affyView&inputKey="+key;
             out.write(" PED: <a href='"+affyUrl+"'><font color='dark green'>Expression</font></a>");

             affyUrl="QueryPageServlet?searchType=Id&displayType=correlationView&inputKey="+key;
             out.write("&nbsp <a href='"+affyUrl+"'><font color='dark green'>Co-Expression</font></a>");
         }
    }
    private void printPUF(Iterable<Record> externallUnknowns) throws IOException
    {
        ExternalUnknownRecord eur;
        boolean haveTitle=false;

        // this list must be sorted
        String[] sources=new String[]{"gomf","pfam","swp"};
        String[] titles = new String[]{"GOMF","Pfam","SWP"};
        int i;

        for(Record rec : externallUnknowns)
        {
            eur=(ExternalUnknownRecord)rec;
            i=Arrays.binarySearch(sources,eur.source);
            if( i >= 0)
            {
                if(!haveTitle) { // only print a title if we have at least one source to display
                    out.write("<b>PUF Profile </b> &nbsp ");
                    haveTitle=true;
                }
                out.write(titles[i]+": "+ Utilities.asUnknown(eur.isUnknown)+" &nbsp ");
            }
        }
    }
}
