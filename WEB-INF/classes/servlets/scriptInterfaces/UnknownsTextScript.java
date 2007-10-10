/*
 * UnknownsTextScript.java
 *
 * Created on November 10, 2004, 8:17 AM
 */

package servlets.scriptInterfaces;
import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;
import servlets.*;
import servlets.dataViews.AffyKey;
import servlets.dataViews.dataSource.display.PatternedRecordPrinter;
import servlets.dataViews.dataSource.records.*;
import servlets.dataViews.dataSource.QueryParameters;
import servlets.dataViews.dataSource.display.DisplayParameters;
import servlets.dataViews.dataSource.structure.RecordFactory;
//import servlets.dataViews.dataSource.display.RecordVisitor;
////import servlets.dataViews.dataSource.display.TextRecordVisitorFactory;
import servlets.dataViews.dataSource.display.text.*;
import servlets.dataViews.dataSource.records.ProbeClusterRecord;
import servlets.dataViews.dataSource.records.SequenceRecord;
import servlets.dataViews.dataSource.records.UnknownRecord;


public class UnknownsTextScript implements Script
{
    
    private static Logger log=Logger.getLogger(UnknownsTextScript.class);
    private DbConnection dbc=null;
    
    private static  int batchSize=1000;
    
    private String dataType;
    private String intensityType;
    private String userName;
    private boolean printDescription;
    
    /** Creates a new instance of UnknownsTextScript */
    public UnknownsTextScript(Map parameters,String userName)
    {        
        dbc=DbConnectionManager.getConnection("khoran");
        if(dbc==null)
            log.error("could not get db connection for text dump");
        
        if(parameters!=null)
        {
            if(parameters.containsKey("dataType") && ((String[])parameters.get("dataType")).length!=0 )
                dataType=((String[])parameters.get("dataType"))[0];
            if(parameters.containsKey("intensityType") && ((String[])parameters.get("intensityType")).length!=0)
                intensityType=((String[])parameters.get("intensityType"))[0];
        }        
        this.userName=userName;
        if(this.userName==null)
            this.userName="public";
    }    

    public void run(java.io.OutputStream os, java.util.List ids)
    {        
        PrintWriter out=new PrintWriter(os);
        writeData(out,ids);
        out.close();
    }
    
    private void writeData(PrintWriter out,List ids)
    {
        Collection data;
        DisplayParameters dp=new DisplayParameters(out);        
        PatternedRecordPrinter prp=new PatternedRecordPrinter(dp);
        
        addCustomFormats(prp);
        prp.addFormat(TextPatternFactory.getAllPatterns());
        
        try{
            int end;
                        
            //Writer out2=new BufferedWriter(new FileWriter("/home/khoran/debug_text.out"));            
            log.debug("start of writeData");
            for(int j=0; j < ids.size(); j+=batchSize)
            {
                log.debug("j="+j);
                end= j+batchSize > ids.size()? ids.size() : j+batchSize;
                data=getRecords(ids.subList(j,end));
                if(data!=null)
                    log.debug("data size="+data.size());
                else
                    log.debug("data is null");
                prp.printTabular(data,j==0); //print header only when j==0
                
//                if(log.isDebugEnabled())
//                {
//                    
//                     dp=new DisplayParameters(out2);                     
//
//                     prp=new PatternedRecordPrinter(dp);            
//                     prp.addFormat(DebugPatternFactory.getAllPatterns());            
//                     prp.printRecord(data);                                                
//                    out2.close();
//                }
 
            }
        }catch(IOException e){
               log.error("io error: "+e);
        }
    }        

    private void addCustomFormats(PatternedRecordPrinter prp)
    {
        if(dataType.equals("Correlation"))
            prp.addFormat(new CorrelationExtFormat());
        if(dataType.equals("Comparison"))
            prp.addFormat(new ComparisonExtFormat());
        else if(dataType.equals("UnknownGenes"))
        {
            prp.addFormat(new PufFormat());
            batchSize=350;
        }
    }
    
    private Collection getRecords(List ids)
    { 
        if(dataType.equals("Comparison"))
            return getComparisonRecords(ids);
        
        
        Collection unknowns=null;
        RecordFactory f=RecordFactory.getInstance();
        QueryParameters qp=new QueryParameters();
        printDescription=false;
        
        qp.setIds(ids);
        qp.setDataType(intensityType);
        qp.setUserName(userName);
        
        
        Collection<AffyKey> affyKeys=new LinkedList<AffyKey>();
        for(Iterator i=ids.iterator();i.hasNext();)
            affyKeys.add(new AffyKey(new Integer((String)i.next()),null,null));
        qp.setAffyKeys(affyKeys);
        qp.setAllGroups(true);
        
        if(dataType.equals("Correlation"))
        {
            unknowns=f.getRecords(CorrelationRecord.getRecordInfo(), qp);
            //f.addSubType(unknowns,ProbeClusterRecord.getRecordInfo(),qp);
            f.addSubType(unknowns,SequenceRecord.getRecordInfo(),qp);
            return unknowns;
        }
        else if(dataType.equals("AffyExpDef"))
        {
            unknowns=f.getRecords(AffyExpDefRecord.getRecordInfo(), qp);
            return unknowns;
        }
        
        unknowns=f.getRecords(UnknownRecord.getRecordInfo(), qp);
         
        if(dataType.equals("AffyComp"))
        {
            qp.setSortCol("comp_experiment_set_id");
            f.addSubType(unknowns, AffyCompRecord.getRecordInfo(),qp);
        }
        else if(dataType.equals("AffyDetail"))    
        {
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
        }
        else if(dataType.equals("AffyExpSet"))    
            f.addSubType(unknowns, AffyExpSetRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Blast"))    
            f.addSubType(unknowns, BlastRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Cluster")) 
            f.addSubType(unknowns, ClusterRecord.getRecordInfo(),qp);       
        else if(dataType.equals("ProbeCluster"))
            f.addSubType(unknowns,ProbeClusterRecord.getRecordInfo(),qp);
        else if(dataType.equals("ExternalUnknown") || dataType.equals("UnknownGenes"))    
            f.addSubType(unknowns, ExternalUnknownRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Go"))    
            f.addSubType(unknowns, GoRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Proteomics"))            
            f.addSubType(unknowns, ProteomicsRecord.getRecordInfo(),qp);            
        else if(dataType.equals("Unknown"))
            printDescription=true;        
        else
            log.error("invalid dataType: "+dataType);
        
        return unknowns;
    }
    
    //@Deprecated
//    private RecordVisitor getRecordVisitor()
//    {
//        RecordVisitor rv;
//        TextRecordVisitorFactory f=TextRecordVisitorFactory.getInstance();
//        
//        if(dataType.equals("AffyComp"))
//            rv=f.buildVisitor(TextRecordVisitorFactory.VisitorType.AFFY_COMP);                
//        else if(dataType.equals("AffyDetail"))//AffyDetail
//            rv=f.buildVisitor(TextRecordVisitorFactory.VisitorType.AFFY_DETAIL);        
//        else
//            rv=f.buildVisitor(TextRecordVisitorFactory.VisitorType.GENERAL);            
//        
//        return rv;
//    }
  
    private Collection getComparisonRecords(List ids)
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
                        
        QueryParameters compQp=new QueryParameters(comparisonIds);        
        compQp.setUserName(userName);
        compQp.setDataType(intensityType);
        
        QueryParameters pskQp=new QueryParameters(pskIds);                
        pskQp.setComparisonIds(comparisonIds);
        pskQp.setDataType(intensityType);
        
        
        records=f.getRecords(ComparisonRecord.getRecordInfo(),compQp);
        pskRecords=f.addSubType(records,ProbeSetKeyRecord.getRecordInfo(),pskQp);
        
        f.addSubType(pskRecords,ProbeClusterRecord.getRecordInfo(),pskQp);
        //f.addSubType(pskRecords,SequenceRecord.getRecordInfo(),pskQp);
        
        return records;
    }
    public String getContentType()
    {        
        //return "text/csv";
        //return "text/html";
        return "text/plain";
        
    }
}
