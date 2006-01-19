/*
 * DiffTrackingDataView.java
 *
 * Created on July 13, 2005, 11:24 AM
 * 
 */

package servlets.dataViews;

/**
 *
 * @author khoran
 */

import java.io.PrintWriter;
import java.util.*;
import servlets.*;
import org.apache.log4j.Logger;
import servlets.dataViews.queryWideViews.DefaultQueryWideView;
import servlets.querySets.QuerySetProvider;
import servlets.search.Search;

public class DiffTrackingDataView implements DataView
{
    private static Logger log=Logger.getLogger(DiffTrackingDataView.class);    
    
    private static final int VERSION=0,     QUERIES_ID=1,   NAME=2,
                             PURPOSE=3,     DESCRIPTION=4,  LINK=5,
                             COUNT=6,       VERSION_A=7,    UPDATED_ON=8,
                             ADDED=9,       REMOVED=10,     UNCHANGED=11,
                             COMP_ID=12,    GENOME=13,      GENOME_ID=14;
    private int keyType;
    
    /** Creates a new instance of DiffTrackingDataView */
    public DiffTrackingDataView()
    {
    }


    public void printData(PrintWriter out)
    {
        
        printStatsData(out);
        
        List data=Common.sendQuery(QuerySetProvider.getDataViewQuerySet().getDiffTrackingDataViewQuery());                 
        
        
        if(data==null || data.size()==0)
        {
            out.println("No results found");
            return;
        }
            
        
        List row;                        
        Map genomes=new HashMap();
        Genome genome=null;
        Version version=null;
        Query query=null;
        Stat stat=null;
        for(Iterator i=data.iterator();i.hasNext();)
        { 
            row=(List)i.next();
            
            genome=(Genome)genomes.get(row.get(GENOME));
            if(genome==null)
            {
                genome=new Genome((String)row.get(GENOME), new Integer((String)row.get(GENOME_ID)));
                genomes.put(row.get(GENOME), genome);
            }
            
            version=(Version)genome.versions.get(row.get(VERSION));
            if(version==null)
            {
                version=new Version((String)row.get(UPDATED_ON));
                genome.versions.put(row.get(VERSION),version);
            }
            
            query=(Query)version.queries.get(row.get(QUERIES_ID));
            if(query==null)
            {
                query=new Query((String)row.get(NAME),(String)row.get(PURPOSE),
                    (String)row.get(DESCRIPTION),(String)row.get(LINK),
                    (String)row.get(COUNT),(String)row.get(COMP_ID));
                version.queries.put(row.get(QUERIES_ID),query);
            }
            stat=new Stat((String)row.get(ADDED),(String)row.get(REMOVED),
                    (String)row.get(UNCHANGED));
            query.stats.put(row.get(VERSION_A),stat);
        }
        
        out.println("<h2>Version Tracking</h2>");
        out.println("<table border='0' width='100%' >");
        for(Iterator i=genomes.entrySet().iterator();i.hasNext();)
        {
            Map.Entry set=(Map.Entry)i.next();
            out.println(((Genome)set.getValue()).toHtml((String)set.getKey()));
        }
        out.println("</table>");               
                        
        
        
        Common.printUnknownFooter(out);
    }

    private void printStatsData(PrintWriter out)
    {
        List statsData=Common.sendQuery(QuerySetProvider.getDataViewQuerySet().getDiffStatsQuery());
        String url="QueryPageServlet?searchType=Query_Stats&displayType=unknowns2View&rpp=25&";                  
                                                
        out.println("<h2>Unknown Stats and Batch Retrieval</h2>");
        out.println("<table border='1' cellspacing='0' width='100%' bgcolor='"+PageColors.data+"'>");
        out.println("<tr bgcolor='"+PageColors.title+"'>");
        out.println("<th>Query</th><th>Arabidopsis</th><th>Rice</th></tr>");
        
        List row;
        String lastName=null;
        
        for(Iterator i=statsData.iterator();i.hasNext();)
        {
            row=(List)i.next();
            
            
            if(lastName==null || !lastName.equals(row.get(0)))
            {//print new row
                if(lastName!=null)
                    out.println("</tr>");
                out.println("<tr><td>"+row.get(0)+"</td>");
                lastName=(String)row.get(0);
            }            
                        
            out.println("<td>");
            if("0".equals(row.get(2)))
                out.println("0");
            else            
                out.println("<a href='"+url+"inputKey="+row.get(3)+" $ "+row.get(1)+"'>"+
                    row.get(2)+"</a> &nbsp&nbsp");                                        
            
            if(i.hasNext()){
                row=(List)i.next();
                if("0".equals(row.get(2)))
                    out.println("0");
                else
                    out.println("<a href='"+url+"inputKey="+row.get(3)+" $ "+row.get(1)+"'>"+
                        "Orothologs ("+row.get(2)+")</a>");    
            }
            out.println("</td>");
        }
        
        out.println("</table>");
    }
    
    
///////////////////////////////////////////////////////////    
    public void printHeader(java.io.PrintWriter out)
    {
        out.println(
                "<style type='text/css'>" +
                "   .test a {color: #006699}" +
                "   .test a:hover {background-color: #AAAAAA}" +
                "</style>" );       
        Common.printUnknownHeader(out);
        out.println("<h1 align='center'>Unknown Sets</h1>   " +
                    "<center>");
        Common.printUnknownsSearchLinks(out); 
        out.println("</center> <div class='test'>");

    }

    public void printStats(java.io.PrintWriter out)
    {
    }

    public void setData(String sortCol, int[] dbList, int hid)
    {
    }

    public void setIds(java.util.List ids)
    {
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        this.keyType=keyType;
    }

    public void setSortDirection(String dir)
    {
    }
    public int getKeyType()
    {
        return keyType;
    }

    public servlets.dataViews.queryWideViews.QueryWideView getQueryWideView()
    {
        return new DefaultQueryWideView(){
            public void printStats(java.io.PrintWriter out,Search search){}
            public void printButtons(java.io.PrintWriter out, int hid,int pos,int size,int rpp){}    
            public boolean printAllData(){return true;}
        };
    }

    public int[] getSupportedKeyTypes()
    {
        return new int[]{Common.KEY_TYPE_SEQ,Common.KEY_TYPE_ACC,Common.KEY_TYPE_BLAST,
                        Common.KEY_TYPE_CLUSTER,Common.KEY_TYPE_MODEL,Common.KEY_TYPE_QUERY};
    }

    public void setParameters(Map parameters)
    {
    }

    public void setStorage(Map storage)
    {
    }
///////////////////////////////////  CLASSES /////////////////////
    
    class Genome
    {
        public String name;
        public Integer genome_id;
        public Map versions;
        private Map realNames;        
        
        public Genome(String name,Integer genome_id)
        {
            this.name=name;
            this.genome_id=genome_id;
            versions=new  LinkedHashMap();
            realNames=new HashMap();
            realNames.put("arab", "Arabidopsis");
            realNames.put("rice","Rice");
        }
        public String toHtml(String genome)
        {
            StringBuffer out=new StringBuffer();
            out.append("<tr><th align='center'><h2>"+realNames.get(genome)+"</h2></th><tr>");
            out.append("<tr><td><table border='1' cellspacing='0' width='100%' bgcolor='"+PageColors.data+"'>");            
            
            for(Iterator i=versions.entrySet().iterator();i.hasNext();)
            {
                Map.Entry set=(Map.Entry)i.next();
                out.append(((Version)set.getValue()).toHtml((String)set.getKey(), genome_id));
            }
            out.append("</table></td></tr>");
            return out.toString();
        }
    }
    class Version
    {
        public String date;        
        public Map queries; //use queries_id as key to query objects
        
        public Version(String date)
        {
            this.date=date;            
            queries=new HashMap();
        }
        public String toHtml(String version,Integer genome_id)
        {
            StringBuffer out=new StringBuffer();
            out.append("<tr bgcolor='"+PageColors.title+"' align='left'>" +
                    "<th colspan='7'>"+date+" Version "+version+"</th></td>\n");
            out.append("<tr bgcolor='"+PageColors.title+"'><th>Query</th><th>Purpose</th><th>Description"+
                "</th><th>Size</th><th>Overlaps</th><th>New</th><th>"+
                "Removed</th></tr>");
            for(Iterator i=queries.entrySet().iterator();i.hasNext();)                
            {
                Map.Entry e=(Map.Entry)i.next();
                out.append(((Query)e.getValue()).toHtml((String)e.getKey(),version,genome_id));
            }
            
//            for(Iterator i=queries.values().iterator();i.hasNext();)                                        
//                out.append(((Query)i.next()).toHtml(version,genome_id));
                            
            return out.toString();
        }
    }
    class Query
    {
        public String name,purpose,description,link,count,comp_id;
        public Map stats; //use version_a as key to stat objects
        
        public Query(String name,String purpose,String desc,String link,String count,String comp_id)
        {
            this.name=name;
            this.purpose=purpose;
            this.description=desc;
            this.link=link;
            this.count=count;
            this.comp_id=comp_id;
            stats=new HashMap();
        }
        public String toHtml(String query_id,String version,Integer genome_id)
        {            
            String linkedDesc=description;
            String added="&nbsp",removed="&nbsp",unchanged="&nbsp";
            String url="QueryPageServlet?searchType=query_comp&displayType=unknowns2View&rpp=25&";
            String countUrl="QueryPageServlet?searchType=Query_Test&displayType=unknowns2View&rpp=25" +
                    "&inputKey="+query_id+" "+version+" "+genome_id;
            if(link!=null && link.length() > 0)
                linkedDesc="<a href='"+link+"'>"+description+"</a>";
            
            for(Iterator i=stats.entrySet().iterator();i.hasNext();)
            {
                Map.Entry set=(Map.Entry)i.next();
                if(((Stat)set.getValue()).added!=null)
                    added="V"+version+"/"+set.getKey()+
                        ": <a href='"+url+"inputKey="+comp_id+" added'>"+
                        ((Stat)set.getValue()).added+"</a>\n";
                if(((Stat)set.getValue()).removed!=null)
                    removed="V"+version+"/"+set.getKey()+
                        ": <a href='"+url+"inputKey="+comp_id+" removed'>"+
                        ((Stat)set.getValue()).removed+"</a>\n";
                if(((Stat)set.getValue()).unchanged!=null)
                    unchanged="V"+version+"/"+set.getKey()+
                        ": <a href='"+url+"inputKey="+comp_id+" unchanged'>"+
                        ((Stat)set.getValue()).unchanged+"</a>\n";                
            }
            
            return "<tr><td>"+name+"</td><td>"+purpose+"</td><td>"+linkedDesc+
                "</td><td><a href='"+countUrl+"'>"+count+"</a></td><td nowrap>"+unchanged+"</td><td nowrap>"+added+"</td><td nowrap>"+
                removed+"</tr>";            
        }
    }
    class Stat
    {
        public String added,removed,unchanged;
        
        public Stat(String added,String removed,String unchanged)
        {            
            this.added=added;
            this.removed=removed;
            this.unchanged=unchanged;
        }
    }
    
    
    
}
