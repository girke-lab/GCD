/*
 * ProbeSetDataView.java
 *
 * Created on April 10, 2006, 2:49 PM
 *
 */

package servlets.dataViews;

import java.io.*;
import java.util.*;
import servlets.dataViews.dataSource.records.ComparisonRecord;
import servlets.dataViews.dataSource.records.ProbeSetKeyRecord;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.structure.RecordFactory;
import servlets.dataViews.queryWideViews.QueryWideView;
import servlets.exceptions.UnsupportedKeyTypeException;

import org.apache.log4j.Logger;
import servlets.*;
import servlets.KeyTypeUser.KeyType;
import servlets.beans.HeaderBean;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.display.PatternedRecordPrinter;
import servlets.dataViews.dataSource.display.html.HtmlPatternFactory;
import servlets.dataViews.dataSource.display.html.ProbeSetInfoFormat;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.queryWideViews.DefaultQueryWideView;
import servlets.search.Search;

/**
 *
 * @author khoran
 */
public class ProbeSetDataView implements DataView
{
    private static Logger log=Logger.getLogger(ProbeSetDataView.class);   
    private static final int MAS5=0, RMA=1;
    private static final String[] dataTypes=new String[]{"mas5","rma"};
    private static final String[] dataTypeTitles=new String[]{"MAS 5","RMA"};
    
    private int hid;
    private String sortDir, sortCol,action;
    private int[] dbNums;       
    private KeyType keyType;
    //private List pskIds,comparisonIds;
    private List ids;
    private int dataType;
    private Map storage;
    private String userName; 
    
    
    private DbConnection dbc=null;  
    private HeaderBean header;
    
    
    /** Creates a new instance of ProbeSetDataView */
    public ProbeSetDataView()
    {
        sortCol=null;
        sortDir="ASC";
        
        ids=new LinkedList(); // empty default value
        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection to 'khoran'");
        
        header=new HeaderBean();
    }

    public void setData(String sortCol, int[] dbList, int hid)
    {
        this.hid=hid;
        this.dbNums=dbList;
        
        if(sortCol!=null && sortCol.length() > 0)
            this.sortCol=sortCol;
    }

    public void setIds(List ids)
    {
        this.ids=ids;
    }

    private String getParam(Map params,String key)
    {
        Object obj=params.get(key);
        if(obj!=null && obj instanceof String[] && ((String[])obj).length!=0)
            return ((String[])obj)[0];
        return null;
    }
    public void setParameters(Map parameters)
    {
        for(Iterator i=parameters.keySet().iterator();i.hasNext();)
            log.debug(" got key: "+i.next());
        
        
//        String[] ids=(String[])parameters.get("comparisonIds");
//        log.debug("ids="+ids);
//        
//        if(ids!=null && ids.length > 0)
//        {
//            comparisonIds=Arrays.asList(ids);
//            storage.put("comparisonIds",comparisonIds);
//        }
//        else if(storage.containsKey("comparisonIds"))
//            comparisonIds=(List)storage.get("comparisonIds");
//        else
//            comparisonIds=new LinkedList();
        
        
        String dataTypeStr=getParam(parameters,"data_type");               
                                
        if(dataTypeStr==null)
            dataTypeStr=(String)storage.get("data_type");
        else
            storage.put("data_type", dataTypeStr);
        
        if(dataTypeStr==null || !dataTypeStr.equals("rma"))
            dataType=MAS5;
        else
            dataType=RMA;
        
            
        log.debug("dataType="+dataType);        
    }

    public void setStorage(Map storage)
    {
        this.storage=storage;
    }

    public void setUserName(String userName)
    {
        this.userName=userName;
        header.setLoggedOn(userName!=null);
    }

    public void printData(PrintWriter out)
    {
        PageColors.printColorKey(out);
        out.println("<P>");
        printData(out,getRecords());   
    }

    public void printHeader(PrintWriter out)
    {
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.PED);
        header.printStdHeader(out,"", userName!=null);
        
        Common.printUnknownsSearchLinks(out);
        
    }

    public void printFooter(PrintWriter out)
    {
        out.println("<script language='JavaScript' type='text/javascript' src='wz_tooltip.js'></script>");        
        header.printFooter();
    }

    public void printStats(PrintWriter out)
    {
        Common.printStatsTable(out, "On This Page", new String[]{"Records found"},
            new Object[]{new Integer(ids.size())});        
    }

    public QueryWideView getQueryWideView()
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
                String link="DispatchServlet?hid="+hid+"&script=unknownsText&range=0-"+search.getResults().size()+
                                "&intensityType="+dataTypes[dataType];
                out.println(DescriptionManager.wrapText("download","Download data:")+" &nbsp");
                out.println("&nbsp<a href='"+link+"&dataType=Comparison'>Comparisons</a>");                           
            }            
            
            public void printGeneral(PrintWriter out, Search search, String position)
            {
                if(position.equals("after_stats"))
                {
                    int nextDataType=(dataType==MAS5 ? RMA : MAS5);
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

    public void setSortDirection(String dir)
    {
        if(dir!=null && (dir.equalsIgnoreCase("asc") || dir.equalsIgnoreCase("desc")))
            sortDir=dir;   
    }

    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.PSK_COMP};
    }

    public void setKeyType(KeyType keyType) throws UnsupportedKeyTypeException
    {
        if(Common.checkType(this,keyType))
            this.keyType=keyType;
        else
            throw new UnsupportedKeyTypeException(this.getSupportedKeyTypes(),keyType);
    }

    public KeyType getKeyType()
    {
        return keyType;
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////    
    
    
    private Collection getRecords()
    {
        Collection records=null,pskRecords;
        RecordFactory f=RecordFactory.getInstance();
        List pskIds=new LinkedList(),comparisonIds=new LinkedList();
        StringTokenizer tok;
        String id;
        
        for(Iterator i=ids.iterator();i.hasNext();)
        {         
            id=(String)i.next();
            tok=new StringTokenizer(id,"_");
            if(tok.hasMoreTokens())
                pskIds.add(tok.nextToken());
            else
                log.error("bad key for probe set dataview: "+id);
            
            if(tok.hasMoreTokens())
                comparisonIds.add(tok.nextToken());
            else
                log.error("bad key for probe set dataview: "+id);
        }
        
        
        
        QueryParameters compQp=new QueryParameters(comparisonIds,sortCol,sortDir);
        compQp.setUserName(userName);
        compQp.setDataType(dataTypes[dataType]);
        
        QueryParameters pskQp=new QueryParameters(pskIds,sortCol,sortDir);                
        pskQp.setComparisonIds(comparisonIds);
        pskQp.setDataType(dataTypes[dataType]);
        
        
        records=f.getRecords(ComparisonRecord.getRecordInfo(),compQp);
        pskRecords=f.addSubType(records,ProbeSetKeyRecord.getRecordInfo(),pskQp);
        
        f.addSubType(pskRecords,ProbeClusterRecord.getRecordInfo(),pskQp);
        f.addSubType(pskRecords,SequenceRecord.getRecordInfo(),pskQp);
        
        return records;
    }
        
    private void printData(PrintWriter out,Collection data)
    {    //recieves a list of RecordGroups
        
        //log.debug("printing "+data.size()+" records");
        
        try{
            
            DisplayParameters dp=new DisplayParameters(out);
            dp.setHid(hid);
            dp.setSortCol(sortCol);
            dp.setSortDir(sortDir);
                        
            PatternedRecordPrinter prp=new PatternedRecordPrinter(dp);            
            prp.addFormat(new ProbeSetInfoFormat());
            prp.addFormat(HtmlPatternFactory.getAllPatterns());
            prp.printRecord(data);            

        }catch(IOException e){
            log.error("could not print to output: "+e.getMessage());
        }
        
        
        
//        out.println("<TABLE bgcolor='"+PageColors.data+"' width='100%'" +
//            " align='center' border='1' cellspacing='0' cellpadding='0'>");
//        Record rec;
//        HtmlRecordVisitor visitor=new HtmlRecordVisitor();
//        
//        log.debug("hid in printData="+hid);
//        
//        visitor.setHid(hid);
//        visitor.setSortInfo(sortCol, sortDir);
//        boolean isFirst=true;
//        
//        try{
//            for(Iterator i=data.iterator();i.hasNext();)
//            {
//                rec=(Record)i.next();
//                
//                if(isFirst)                                        
//                    rec.printHeader(out, visitor);                
//                isFirst=false;
//                rec.printRecord(out, visitor);
//                if(i.hasNext())
//                    rec.printFooter(out, visitor);
//            }          
//        }catch(IOException e){
//            log.error("could not print to output: "+e.getMessage());
//        }
//        
        out.println("</div>");
    }              
}
