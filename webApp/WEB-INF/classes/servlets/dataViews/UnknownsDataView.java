/*
 * UnknownsDataView.java
 *
 * Created on September 8, 2004, 1:04 PM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.PrintWriter;
import servlets.*;
import org.apache.log4j.Logger;

public class UnknownsDataView implements DataView
{
    List seq_ids;
    int hid;
    String sortCol;
    int[] dbNums;
    DbConnection dbc=null;
    static Logger log=Logger.getLogger(UnknownsDataView.class);
    
    /** Creates a new instance of UnknownsDataView */
    public UnknownsDataView() 
    {
        dbc=DbConnectionManager.getConnection("unknowns");
        if(dbc==null)
            log.error("could not get db connection to unknowns");
    }
    
    public void printData(java.io.PrintWriter out) 
    {
        List data=getData();
        printData(out,data);
    }
    
    public void printHeader(java.io.PrintWriter out)
    {
        out.println("<h1 align='center' >Unknowns</h1>");
    }
    
    public void printStats(java.io.PrintWriter out) 
    {
        out.println(seq_ids.size()+" records found");
    }
    
    public void setData(java.util.List ids, String sortCol, int[] dbList, int hid) 
    {
        this.seq_ids=ids;
        this.hid=hid;
        this.sortCol=sortCol;
        this.dbNums=dbList;
    }
    
    ////////////////////////////////////////////////////////////////
    
    private void printData(PrintWriter out,List data)
    {
         String titleColor="AAAAAA", dataColor="D3D3D3";
         String lastId="";
         if(data==null)
             return;
         
         out.println("<TABLE border='1' cellspacing='0' cellpadding='0' bgcolor='"+dataColor+"'>");
         out.println("<TR bgcolor='"+titleColor+"'>");
         out.print("<th>Unknown_id</th>");
         for(int i=0;i<printNames.length;i++) //print titles
             out.print("<th>"+printNames[i]+"</th>");
         //out.println("<th colspan='5'>"+printNames[printNames.length-1]+"</th>");
         out.println("</TR><tr>");
         for(Iterator i=data.iterator();i.hasNext();)
         {
            List row=(List)i.next();
            if(lastId.equals(row.get(0))) //additional treatment
            {               
                //then just print the last element
                out.println(" &nbsp&nbsp&nbsp "+row.get(row.size()-1));
            }
            else{ //new record
                lastId=(String)row.get(0);
                out.println("</td></tr><tr>");
                for(Iterator j=row.iterator();j.hasNext();)
                {
                    String t=(String)j.next();
                    if(t==null || t.equals(""))
                        out.print("<td>&nbsp");                          
                    else
                        out.print("<td>"+t);             
                    if(j.hasNext()) //don't print last td, so we can put more treatments in cell
                        out.println("</td>");
                }
            }            
         }
         out.println("</td></tr></TABLE>");
    }
    
    private List getData()
    {
        StringBuffer conditions=new StringBuffer();
        for(Iterator i=seq_ids.iterator();i.hasNext();)
        {
            conditions.append(i.next());
            if(i.hasNext())
                conditions.append(",");
        }
        try{
            return dbc.sendQuery(buildQuery(conditions.toString()));
        }catch(Exception e){
            log.error("could not send query: "+e.getMessage());
        }
        return null;
    }
    private String buildQuery(String conditions)
    {
        String query="SELECT unknowns.*,treats.treat " +
            " FROM unknowns LEFT JOIN treats USING(unknown_id) " +
            " WHERE unknowns.unknown_id in ("+conditions+") " +
            " ORDER BY "+sortCol+",unknowns.unknown_id";
        
        log.info("query is: "+query);
        return query;
    }
    
    String[] printNames=new String[]{
            "At Key" ,
            "Description" ,
            "Unknown Method TIGR" ,
            "Unknown Method SWP_BLAST" ,
            "Unknown Method GO: MFU OR CCU OR BPU" ,
            "Unknown Method GO: MFU" ,
            "Unknown Method InterPro" ,
            "Unknown Method Pfam" ,
            "Citosky Small List" ,
            "SALK tDNA-Insertion" ,
            "EST avail" ,
            "avail" ,
            "flcDNA TIGR (XML) avail" ,
            "Nottingham Chips: 3x >90" ,
            "Rice Orth E-value" ,
            "HumanRatMouse Orth E-value" ,
            "S. cerevisiae E-value" ,
            "Gene Family Size 35%_50%_70% ident" ,
            "Pet Gene from" ,
            "Targeting Ipsort" ,
            "Targeting Predotar" ,
            "Targeting Targetp" ,
            "Membr dom Hmmtop" ,
            "Membr dom Thumbup" ,
            "Membr dom TMHMM" ,
            "Focus list of grant" ,
            "Selected by" ,
            "Multiple selects" ,
            "Occurrence in treaments",        
            "Treatments"
        };
}
