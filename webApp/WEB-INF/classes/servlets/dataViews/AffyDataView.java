/*  
 * AffyDataVew.java
 *
 * Created on August 4, 2005, 9:53 AM
 * 
 */

package servlets.dataViews;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.dataViews.queryWideViews.DefaultQueryWideView;
import servlets.dataViews.records.*;
import servlets.search.Search;

/**
 *
 * @author khoran
 */

public class AffyDataView implements DataView
{
    private static Logger log=Logger.getLogger(AffyDataView.class);
    
    private int keyType, hid;
    private String sortDir, sortCol,action;
    private int[] dbNums;        
    private List idLists=null;
    private List[] newIds;
    DbConnection dbc=null;  
    
    //indexes into idLists list 
    public static final int ACC=0, PSK=1, ES=2,GROUP=3;
    
    /** Creates a new instance of AffyDataVew */
    public AffyDataView(HttpServletRequest request)
    {
        sortDir="ASC";        
        newIds=new List[4]; //temp storage.
        for(int i=0;i<4;i++)
            newIds[i]=new LinkedList();
        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
        
        processRequest(request);
    }

    public int getKeyType()
    {
        return keyType;
    }

    public servlets.dataViews.queryWideViews.QueryWideView getQueryWideView()
    {
        return new DefaultQueryWideView(){
            public void printStats(PrintWriter out,Search search)
            {
                Common.printStatsTable(out, "Total Query",
                    new String[]{"Keys found"},new Integer[]{new Integer(search.getResults().size())});
            }
            public void printButtons(PrintWriter out, int hid, int pos, int c, int d)
            {                
            }          
            public void printGeneral(PrintWriter out, Search search, String position, Map storage)
            {
                if(newIds[ACC].size()==0)
                { //make sure setIds has been called first
                    log.error("no accession ids, has setIds() been called yet?");
                    idLists=null;
                    return;
                }
                idLists=(List)storage.get(newIds[ACC]); //use the accession_id list as a per-page key
                if(idLists==null) //first page of query
                {
                    idLists=new ArrayList(4);
                    idLists.add(newIds[ACC]);
                    for(int i=1;i<4;i++)
                      idLists.add(new HashSet()); //use a set to avoid duplicates
                    storage.put(newIds[ACC],idLists);                    
                }                                
                //now that we have a new/existing set of keys, add the ids for this request
                if(action==null)
                    log.error("no action given");                
                else if(action.equals("expand"))
                    for(int i=PSK;i<=GROUP;i++)
                        ((Collection)idLists.get(i)).addAll(newIds[i]);
                else if(action.equals("collapse"))
                    for(int i=PSK;i<=GROUP;i++)
                        ((Collection)idLists.get(i)).removeAll(newIds[i]);
                else
                    log.error("invalid action: "+action);
            }
         };
        
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_MODEL};
    }
   
    public void setData(String sortCol, int[] dbList, int hid)
    {
        this.hid=hid;
        this.sortCol=sortCol;
        this.dbNums=dbList;     
    }

    public void setIds(java.util.List ids)
    {
        newIds[ACC]=ids;        
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyType
    {
        this.keyType=keyType;
    }

    public void setSortDirection(String dir)
    {
         if(dir!=null && (dir.equalsIgnoreCase("asc") || dir.equalsIgnoreCase("desc")))
            sortDir=dir;   
    }
    
    
    public void printHeader(java.io.PrintWriter out)
    {        
        Common.printUnknownHeader(out);
        Common.printUnknownsSearchLinks(out);
        
        out.println(
                "<style type='text/css'>" +
                    ".test a {color: #006699}\n" +
                    ".test a:hover {background-color: #AAAAAA}\n" +
                "</style>");
        out.println("<div class='test'>");
//        out.println("<form method='get'><input type=hidden name='hid' value='"+hid+"'>");
//        out.println("<input type=hidden name='psk_ids' value='-1'><input type=hidden name='es_ids' value='-1'");
    }

    public void printStats(java.io.PrintWriter out)
    {
         Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(newIds[ACC].size())});
    }
    public void printData(java.io.PrintWriter out)
    {
        printData(out,getRecords());
        out.println("</td></table>"); //close page level table
    }
////////////////////////////////////////////////////////////////////////////////
    private void processRequest(HttpServletRequest request)
    {
        action=request.getParameter("action");
        
        String[] psk_ids=request.getParameterValues("psk_ids");
        String[] es_ids=request.getParameterValues("es_ids");
        String[] groups=request.getParameterValues("groups");
        
        log.debug("action="+action);
        log.debug("psk_ids: "+Common.printArray(psk_ids)); 
        if(psk_ids!=null)
            log.debug(psk_ids.length+" psk ids");
        log.debug("es_ids: "+Common.printArray(es_ids));
        if(es_ids!=null)
            log.debug(es_ids.length+" es ids");
        log.debug("groups: "+Common.printArray(groups));
        if(groups!=null)
            log.debug(groups.length+" groups");               
                
        
        
        // the index of each array in this array must 
        // match the values of PSK,ES, GROUP, respectivly.
        String[][] idSets=new String[][]{psk_ids,es_ids,groups};
        List l;
        for(int i=PSK-1;i<=GROUP-1;i++)
        { //loop over PSK, ES,GROUP            
            l=(List)newIds[i+1];
            for(int j=0;idSets[i]!=null && j<idSets[i].length;j++)
                l.add(new Integer(idSets[i][j]));    
        }
        log.debug("newIds="+Common.printArray(newIds));
    }
    private Collection getRecords()
    {        
        Map[] subRecordMaps=new Map[]{
            AffyExpSetRecord.getData(dbc,idLists)            
        };
        Map records=UnknownRecord.getData(dbc,(List)idLists.get(ACC),sortCol,sortDir,subRecordMaps);
        return records.values();                                 
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        //log.debug("printing "+data.size()+" records");
        out.println("<TABLE bgcolor='"+Common.dataColor+"' width='100%'" +
            " align='center' border='1' cellspacing='0' cellpadding='0'>");
        RecordGroup rec;
        RecordVisitor visitor=new HtmlRecordVisitor();
        ((HtmlRecordVisitor)visitor).setHid(hid);
        try{
            for(Iterator i=data.iterator();i.hasNext();)
                ((RecordGroup)i.next()).printRecords(out,visitor);  
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</TABLE></div>");
    }
    
    
    
}
