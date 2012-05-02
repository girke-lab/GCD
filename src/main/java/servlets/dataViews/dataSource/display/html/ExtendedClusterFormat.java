/*
 * ExtendedClusterFormat.java
 *
 * Created on September 27, 2007, 12:15 PM
 *
 */

package servlets.dataViews.dataSource.display.html;

import java.io.IOException;
import org.apache.log4j.Logger;
import servlets.PageColors;
import servlets.dataViews.dataSource.display.AbstractPatternFormat;
import servlets.dataViews.dataSource.display.RecordPattern;
import servlets.dataViews.dataSource.records.ClusterRecord;

/**
 *
 * @author khoran
 */
public class ExtendedClusterFormat extends AbstractPatternFormat<ClusterRecord>
{
    
    private static final Logger log=Logger.getLogger(ExtendedClusterFormat.class);
    
    /** Creates a new instance of ExtendedClusterFormat */
    public ExtendedClusterFormat()
    {
    }

    public RecordPattern getPattern()
    {
        return new RecordPattern(ClusterRecord.class);
    }

    public void printHeader(ClusterRecord r) throws IOException
    {
        Utilities.startTable(out);
        out.write("<tr bgcolor='"+PageColors.title+"'>");
        
        String[] titles=new String[]{"Clustering","Cluster Name","ID","Size","Members","Alignment","Tree"};
        for(String title : titles)
            out.write("<th>"+title+"</th>");
        out.write("</tr>");
    }

    public void printRecord(ClusterRecord r) throws IOException
    {
        out.write("<td nowrap>"+r.method+"</td><td>"+r.name+"&nbsp;</td>");
        
        out.write("<td>");
        if("Domain Composition".equals(r.method))
            out.write("<a href='pfamOptions.jsp?accession="+r.key+"'>"+
                                    r.key+"</a>");
        else
            out.write(r.key);
        out.write("</td>");
        out.write("<td>"+r.size+"</td>");

        out.write("<td nowrap><a href='pfamSearches.jsp?input="+r.key+"'>" +
                            r.arabCount+" Ath &nbsp&nbsp "+r.riceCount+" Osa</a></td>");
         if(r.size > 1 && !r.method.endsWith("_50") && !r.method.endsWith("_70"))
         {
            String webBase="http://"+servlets.Common.hostname+"/scripts/getClusterFiles.pl?cid="+r.key+ 
                 "&cluster_type="+r.method+"&file_type=";

            out.write("<td nowrap>");
            out.write("<a href='"+webBase+"html'>Consensus shaded</a>&nbsp&nbsp");
            out.write("<a href='http://"+servlets.Common.hostname+"/scripts/domainShader?cid="+r.key+"'>Domain shaded</a>");
            out.write("</td>");

            if(r.size > 2 ){
                String treeViewLink="DispatchServlet?hid="+this.getParameters().getHid()+
                    "&script=treeViewer.pl&range=0&clusterId="+r.key;
                out.write("<td><a href='"+treeViewLink+"' target='_blank'>view</a></td>");
            }else{
                out.write("<td>&nbsp;</td>");
            }
         }                     
         else
             out.write("<td>&nbsp</td><td>&nbsp</td>");

        out.write("</tr>");
    }

    public void printFooter(ClusterRecord r) throws IOException
    {
        if(Utilities.hasChildren(r))
            Utilities.closeChildCel(out);
        Utilities.endTable(out);
    }
    
}
