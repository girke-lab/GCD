/*  
 * AffyDataVew.java
 *
 * Created on August 4, 2005, 9:53 AM
 * 
 */

package servlets.dataViews;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.KeyTypeUser.KeyType;
import servlets.beans.HeaderBean;
import servlets.dataViews.dataSource.display.html.AffyCompFormat;
import servlets.dataViews.dataSource.records.AffyCompRecord;
import servlets.dataViews.dataSource.records.AffyDetailRecord;
import servlets.dataViews.dataSource.records.AffyExpSetRecord;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.display.DebugRecordVisitor;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.display.PatternedRecordPrinter;
import servlets.dataViews.dataSource.display.RecordVisitor;
import servlets.dataViews.dataSource.display.html.HtmlPatternFactory;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.structure.Record;
import servlets.dataViews.dataSource.structure.RecordFactory;
import servlets.dataViews.dataSource.records.UnknownRecord;
import servlets.dataViews.queryWideViews.DefaultQueryWideView;
import servlets.search.Search;

/**
 *
 * @author khoran
 */

public class AffyDataView implements DataView
{
    private static Logger log=Logger.getLogger(AffyDataView.class);    
    private static final int MAS5=0, RMA=1;
    private static final String[] dataTypes=new String[]{"mas5","rma"};
    private static final String[] dataTypeTitles=new String[]{"MAS 5","RMA"};
    
    private KeyType keyType;
    private int hid;
    private String sortDir, sortCol,action,compView;
    private int[] dbNums;        
    private Set<AffyKey> nodeSet=null;
    private List[] newIds;
    private List accIds;
    private int dataType;
    private Map storage;
    private String userName; 
    
    
    private DbConnection dbc=null;  
    private HeaderBean header;
    
    //indexes into idLists list 
    public static final int ACC=0, PSK=1, ES=2,GROUP=3;
    
    /** Creates a new instance of AffyDataVew */
    public AffyDataView()
    {
        sortCol=null;
        sortDir="ASC";        
        
        newIds=new List[4]; //temp storage.
        for(int i=0;i<4;i++)
            newIds[i]=new LinkedList();
        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to khoran");
                
        header=new HeaderBean();        
    }

    public KeyType getKeyType()
    {
        return keyType;
    }
    public void setUserName(String userName)
    {
        this.userName=userName;
        header.setLoggedOn(userName!=null);
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
            public void printGeneral(PrintWriter out, Search search, String position,Map storage)
            {
                out.println(DescriptionManager.wrapText("download","Download data:")+" &nbsp");
                Common.printUnknownDownloadLinks(out, hid, search.getResults().size(),dataTypes[dataType]);                               
            }            
            public void printGeneral(PrintWriter out, Search search, String position)
            {
                if(position.equals("after_stats"))
                {
                    int nextDataType=(dataType==MAS5 ? RMA : MAS5);
                    //out.println("&nbsp&nbsp Display &nbsp ");
                    out.println("&nbsp&nbsp "+DescriptionManager.wrapText("display","Display")  +" &nbsp ");
                    
                    if(dataType==MAS5)
                        out.println(dataTypeTitles[dataType]);
                    else
                        out.println("<a href='QueryPageServlet?hid="+hid+"&data_type="+
                            dataTypes[nextDataType]+"'>"+dataTypeTitles[nextDataType]+"</a>");
                    out.println("&nbsp");
                    if(dataType==RMA)
                        out.println(dataTypeTitles[dataType]);
                    else
                        out.println("<a href='QueryPageServlet?hid="+hid+"&data_type="+
                            dataTypes[nextDataType]+"'>"+dataTypeTitles[nextDataType]+"</a>");
                    
                    //out.println("&nbsp&nbsp <a href")

                }
            }
         };        
    }

    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.MODEL};
    }
   
    public void setData(String sortCol, int[] dbList, int hid)
    {
        log.debug("hid in setData="+hid);
        this.hid=hid;        
        this.dbNums=dbList;     
        
        if(sortCol!=null && sortCol.length() > 0)
            this.sortCol=sortCol;
    }

    public void setIds(java.util.List ids)
    {
        log.debug("setting ids to "+ids);
        accIds=ids;        
        updateNodeSet();
    }

    public void setKeyType(KeyType keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        if(!Common.checkType(this, keyType))
            throw new servlets.exceptions.UnsupportedKeyTypeException(this.getSupportedKeyTypes(),keyType);
        this.keyType=keyType;
    }

    public void setSortDirection(String dir)
    {
         if(dir!=null && (dir.equalsIgnoreCase("asc") || dir.equalsIgnoreCase("desc")))
            sortDir=dir;   
    }
    
    public void setParameters(Map parameters)
    {
        processRequest(parameters);
    }

    public void setStorage(Map storage)
    {
        this.storage=storage;
    }
    
    public void printHeader(java.io.PrintWriter out)
    {   
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.PED);
        header.printStdHeader(out,"", userName!=null);
        
//        out.println(
//                "<style type='text/css'>" +
//                    ".test a {color: #006699}\n" +
//                    ".test a:hover {background-color: #AAAAAA}\n" +
//                "</style>");
//        
//        out.println("<div class='test'>");
        
        Common.printUnknownsSearchLinks(out);
        
        
    }
    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
    }

    public void printStats(java.io.PrintWriter out)
    {        
         Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(accIds.size())});            
    }
    public void printData(java.io.PrintWriter out)
    {        
        PageColors.printColorKey(out);
        out.println("<P>");
        printData(out,getRecords());        
        out.println("<script language='JavaScript' type='text/javascript' src='wz_tooltip.js'></script>");        
    }
    
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    
    private String getParam(Map params,String key)
    {
        Object obj=params.get(key);
        if(obj!=null && obj instanceof String[] && ((String[])obj).length!=0)
            return ((String[])obj)[0];
        return null;
    }
    private void processRequest(Map params)
    {
        action=getParam(params,"action");
                
        String dataTypeStr=getParam(params,"data_type");                
        log.debug("dataTypeStr="+dataTypeStr);
        
        compView=getParam(params, "comp_view");
        
        String[] psk_ids=(String[])params.get("psk_ids");
        String[] es_ids=(String[])params.get("es_ids");
        String[] groups=(String[])params.get("groups");
        
        if(log.isDebugEnabled())
        {
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
        }
        if(dataTypeStr==null)
            dataTypeStr=(String)storage.get("data_type");
        else
            storage.put("data_type", dataTypeStr);
        
        if(dataTypeStr==null || !dataTypeStr.equals("rma"))
            dataType=MAS5;
        else
            dataType=RMA;
        
        if(compView==null || compView.equals(""))
            compView=(String)storage.get("comp_view");
        else
            storage.put("comp_view",compView);
            
        log.debug("dataType="+dataType);
        
        
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
    private void updateNodeSet()
    {
        if(accIds.size()==0)
        { //make sure setIds has been called first
            log.error("no accession ids, has setIds() been called yet?");
            nodeSet=null;
            return;
        }

        nodeSet=(Set)storage.get(accIds); //use the accession_id list as a per-page key
        if(nodeSet==null) //first page of query
        {
            nodeSet=new HashSet<AffyKey>();
            storage.put(accIds,nodeSet);                    
        }                                
        //now that we have a new/existing set of keys, add the ids for this request
        Iterator accItr,pskItr,esItr,groupItr;                    
        pskItr=newIds[PSK].iterator();
        esItr=newIds[ES].iterator();
        groupItr=newIds[GROUP].iterator();

        boolean add=action!=null && action.equals("expand");

        log.debug("nodeSet before: "+nodeSet);
        while(pskItr.hasNext() && esItr.hasNext())
        {
            Integer group=null;

            if(groupItr.hasNext())
                group=(Integer)groupItr.next();
            AffyKey n=new AffyKey(-1, (Integer)pskItr.next(), (Integer)esItr.next(),group);                        

            if(add)
                nodeSet.add(n);
            else
                nodeSet.remove(n);

        }
        log.debug("nodeSet="+nodeSet);
    }
    private Collection getRecords()
    {                       
        Collection unknowns;
        RecordFactory f=RecordFactory.getInstance();
        log.debug("sortCol="+sortCol);
        QueryParameters qp=new QueryParameters(accIds,sortCol,sortDir);
        qp.setUserName(userName);
        qp.setAffyKeys(nodeSet);
        qp.setDataType(dataTypes[dataType]);
        
        QueryParameters accQp=new QueryParameters(accIds);
                        
        unknowns=f.getRecords(UnknownRecord.getRecordInfo(),accQp);
        f.addSubType(unknowns,ProbeClusterRecord.getRecordInfo(),accQp);
        f.addSubType(
            f.addSubType(
                f.addSubType(
                    unknowns,  
                    AffyExpSetRecord.getRecordInfo(), qp
                ),
                AffyCompRecord.getRecordInfo(),qp
            ), 
            AffyDetailRecord.getRecordInfo(), qp
        );

        return unknowns;                                                
    }        
    
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        //log.debug("printing "+data.size()+" records");
        
        try{
            
            DisplayParameters dp=new DisplayParameters(out);
            dp.setHid(hid);
            dp.setSortCol(sortCol);
            dp.setSortDir(sortDir);
            dp.setCompView(compView);
            
            PatternedRecordPrinter prp=new PatternedRecordPrinter(dp);
            //prp.addFormat(DebugPatternFactory.getAllPatterns());
            prp.addFormat(HtmlPatternFactory.getAllPatterns());
            if("comp".equals(compView))
                prp.addFormat(new AffyCompFormat());
            
            prp.printRecord(data);
            
        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        out.println("</div>");
    }        
    

   

   
}
