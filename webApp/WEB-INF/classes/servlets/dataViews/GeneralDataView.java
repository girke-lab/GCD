/*
 * GeneralDataView.java
 *
 * Created on August 11, 2004, 9:03 AM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;

public class GeneralDataView implements DataView
{

    
    List seq_ids;
    String sortCol;
    int limit,hid;
    int[] dbNums;
    
    /** Creates a new instance of GeneralDataView */
    public GeneralDataView() {    
    }
    public void printHeader(java.io.PrintWriter out) {
        Common.printForm(out,hid); 
    }
    
    public void printData(java.io.PrintWriter out) {
        List data=getData(seq_ids, sortCol,limit,dbNums);
        printCounts(out,data);
        printSummary(out, data, findGoNumbers(seq_ids), findClusterNumbers(seq_ids), dbNums, hid);
    }
    
    public void setData(java.util.List ids, String sortCol, int limit, int[] dbList, int hid) 
    {
        this.seq_ids=ids;
        this.sortCol=sortCol;
        this.limit=limit;        
        this.dbNums=dbList;        
        this.hid=hid;
    }
    
    private void printSummary(PrintWriter out,List data,HashMap goNumbers,HashMap clusterNumbers,int[] DBs,int hid)
    {
        ListIterator li=data.listIterator();
        List row;
        String key,lastKey="";
        String clusterNum="",clusterSize="";
        StringBuffer keyList=new StringBuffer();
        int count=0;
        String[] colors=new String[2];
        colors[0]=new String("AAAAAA"); //00aa00 //green  //"29F599"
        colors[1]=new String("D3D3D3"); //00cc00 //cyan  //"26F5CC"

        printForms(out,data,goNumbers);

        out.println("<TABLE width='100%' align='center' border='1' cellspacing='0'>");
        int lastDB=-1,currentDB;
        while(li.hasNext())
        {
            row=(ArrayList)li.next();

            currentDB=Common.getDBid((String)row.get(4));
            if(currentDB!=lastDB)//we have changed databases, so print the title of the new db
                out.println("<TR><TH colspan='7'><H2 align='center'>"+Common.dbPrintNames[currentDB]+" search results</H2></TH></TR>");
            lastDB=currentDB; //update lastDB


            key=(String)row.get(0);
            out.println("<TR><TD colspan='7'>&nbsp</TD></TR>");
            
            //print key and description
            out.println("<TR bgcolor='"+colors[0]+"'><TH>Key</TH><TH colspan='6'>Description</TH></TR>");            
            out.println("<TR bgcolor='"+colors[1]+"'>");
            out.println("<TD><A href='http://bioinfo.ucr.edu/cgi-bin/seqview.pl?database=all&accession="+row.get(0)+"'>"+row.get(0)+"</A></TD>");
            out.println("<TD colspan='6'>"+row.get(1)+"</TD>");
            out.println("</TR>");
            
            //print links
            out.println("<TR bgcolor='"+colors[1]+"'><TH align='left'>Links</TH>");
            out.println("\t\t<TD colspan='6'>");
            printLinks(out,key,clusterNum,hid,currentDB,clusterSize,(String)row.get(2),goNumbers);
            out.println("</TD>");
            out.println("</TR>");

            //print clusters table
            printClusters(out,(ArrayList)clusterNumbers.get(row.get(2)),hid,count++,colors);
        }
        out.println("</TABLE>");
    }
    
    private void printClusters(PrintWriter out, ArrayList set,int hid,int count,String[] colors)
    {
        if(set==null)
            return;
//        out.println("<TABLE border='0' width='100%'>");
        out.println("\t<TR bgcolor='"+colors[0]+"'><TH>Clustering</TH><TH>Name</TH><TH>ID</TH><TH>Size</TH><TH>Members</TH><TH>Alignment</TH><TH>Tree</TH></TR>");
        String clusterType,blast="BLASTCLUST", hmm="Domain Composition";
        for(Iterator i=set.iterator();i.hasNext();)
        {//one row per set
             out.println("\t<TR bgcolor='"+colors[1]+"'>");
             ClusterSet cs=(ClusterSet)i.next();
             clusterType=cs.clusterNum.matches("PF.*") ? hmm : blast;                          
             out.println("\t\t<TD nowrap>"+clusterType+"</TD>");                                      
             out.println("\t\t<TD>"+cs.name+"</TD>");
             out.println("\t\t<TD>");
             if(clusterType.equals(hmm))
             {
                 StringTokenizer tok=new StringTokenizer(cs.clusterNum,"_");
                 while(tok.hasMoreTokens())
                 {
                     String n=tok.nextToken();                     
                     out.println("<a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?"+n.substring(0,n.indexOf('.'))+"'>"+n+"</a>");
                     if(tok.hasMoreTokens())
                         out.println("_");
                 }                 
             }else
                 out.println(cs.clusterNum);
             out.println("\t\t</TD>");
             out.println("\t\t<TD>"+cs.size+"</TD>");
             //out.println("<a href='/databaseWeb/ClusterServlet?hid="+hid+"&clusterID="+cs.clusterNum+"'>Query Ids</a>&nbsp&nbsp");
             out.println("\t\t<TD><a href='/databaseWeb/index.jsp?fieldName=Cluster Id&input="+cs.clusterNum+"'>Retrieve</a></TD>");
             if(!cs.size.equals("1"))
             {
                 String base="http://bioinfo.ucr.edu/projects/ClusterDB/clusters.d/";
                 if(clusterType.equals(hmm))
                     base+="hmmClusters/";
                 else
                     base+="blastClusters/";
                 out.println("\t\t<TD nowrap>");
                 out.println("\t\t\t<a href='"+base+cs.clusterNum+".html'>Consensus shaded</a>&nbsp&nbsp");
                 out.println("\t\t\t<a href='http://bioinfo.ucr.edu/cgi-bin/domainShader?cid="+cs.clusterNum+"'>Domain shaded</a>");
                 out.println("\t\t</TD>");
                 out.println("\t\t<TD><a href='"+base+cs.clusterNum+".jpg'>view</a></TD>");
             }             
             else
                 out.println("<TD>&nbsp</TD><TD>&nbsp</TD>");
             out.println("\t</TR>");
        }
 //       out.println("</TABLE>");
    }
    private void printLinks(PrintWriter out,String key,String clusterNum,int hid,int currentDB,String size,String Seq_id,HashMap goNumbers)
    {//size is cluster size
         String db=null;
         if(currentDB==Common.arab)
             db="ath1";
         else if(currentDB==Common.rice)
             db="osa1";
         
         if(currentDB==Common.arab)
         {
             out.println("<a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+key+"'>TAIR</a>&nbsp&nbsp");
             out.println("<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+ key+"'>MIPS</a>&nbsp&nbsp");
         }
         out.println("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+ "ORF_infopage.cgi?db="+db+"&orf="+key+"'>TIGR</a>&nbsp&nbsp");
         out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/geneview.pl?accession="+key+"'>GeneStructure*</a>&nbsp&nbsp");
         //out.println("<a href='http://www.sanger.ac.uk/cgi-bin/Pfam/getacc?PF'>PFAM</a>&nbsp&nbsp");
         //expression link goes here
         if(currentDB==Common.arab)
            out.println("<a href='http://signal.salk.edu/cgi-bin/tdnaexpress?GENE="+key+"&FUNCTION=&JOB=HITT&DNA=&INTERVAL=10'>KO</a>&nbsp&nbsp");

         //here we want an array of go numbers
         StringBuffer querys=new StringBuffer();
         ArrayList gos=(ArrayList)goNumbers.get(key);
         if(gos!=null) //gos may be null if this Seq_id does not have any GO numbers
           for(Iterator i=gos.iterator();i.hasNext();)
                 querys.append("query="+((String)i.next()).replaceFirst(":","%3A")+"&"); //the : must be encoded
         if(querys.length()!=0)//we have at least one go number
            out.println("<a href='http://www.godatabase.org/cgi-bin/go.cgi?depth=0&advanced_query=&search_constraint=terms&"+querys+"action=replace_tree'>GO</a>&nbsp&nbsp");

         //does this link work for rice? no
         out.println("<a href='http://www.genome.ad.jp/dbget-bin/www_bget?ath:"+key+"'>KEGG</a>&nbsp&nbsp");         
    }

    private void printForms(PrintWriter out, List data,Map goNumbers)
    {
        out.println("<TABLE border='0' ><TR><TD >");
        //gene structure form
        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/multigene.pl'>\n");
        if(data.size() > 0) //don't print a button if thier is no data
            out.println("<INPUT type='submit' value='All Gene Structures'>\n");
        for(Iterator i=data.iterator();i.hasNext();)
            out.println("<INPUT type=hidden name='accession' value='"+((ArrayList)i.next()).get(0)+"'/>\n");
        out.println("</FORM></TD><TD>");
        
        //chr form
        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/chrplot.pl'>\n");
        if(data.size() > 0)
            out.println("<INPUT type='submit' value='Chr Map'><BR>\n");
        out.println("<INPUT type=hidden name='database' value='all'/>");
        for(Iterator i=data.iterator();i.hasNext();)
            out.println("<INPUT type=hidden name='accession' value='"+((ArrayList)i.next()).get(0)+"'/>\n");
        out.println("</FORM></TD><TD>");
        
        //go slim form
        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/goSlimCounts'>\n");
        if(goNumbers.size() > 0)
            out.println("<INPUT type='submit' value='Go Slim Counts'><BR>\n");
        
        for(Iterator i=goNumbers.entrySet().iterator();i.hasNext();)
        {//print out key_value pairs for the goSlimCounts script
            Map.Entry entry=(Map.Entry)i.next();
            for(Iterator j=((ArrayList)entry.getValue()).iterator();j.hasNext();)            
                out.println("<INPUT type=hidden name='go_numbers' value='"+entry.getKey()+"_"+j.next()+"'/>\n");
        }
        for(Iterator i=data.iterator();i.hasNext();)
        {
            String key=(String)((List)i.next()).get(0);
            boolean match=false;
            for(Iterator j=goNumbers.keySet().iterator();j.hasNext();)
            {
                String goKey=(String)j.next();
                if(key.equals(goKey))
                    match=true;                    
            }            
            if(!match)
                out.println("<INPUT type=hidden name='missing_keys' value='"+key+"'/>\n");
        }        
        out.println("<INPUT type=hidden name='total_seq_count' value='"+data.size()+"'/>\n");
        out.println("</FORM></TD></TR></TABLE>\n");

   
    }
    public void printCounts(PrintWriter out, List data)
    {
        int models=0,keys=0;
        String lastKey="";
        ArrayList row;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            row=(ArrayList)i.next();
            if(!lastKey.equals(row.get(0)))
            {
                keys++;
                models+=Integer.parseInt((String) row.get(3));
            }
            lastKey=(String)row.get(0);
        }
        out.println("Keys found: "+keys+"<br> Models found: "+models+" <BR>");
    }
    
    private HashMap findGoNumbers(List data)
    {//query the go numbers from the GO table and organize them in a hashMap.
        StringBuffer conditions=new StringBuffer();
        System.out.println("data="+data);
        
        conditions.append("go.seq_id in (");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            conditions.append(i.next());
            if(i.hasNext())
                conditions.append(",");
        }
        conditions.append(")");
                

        System.out.println("sending goNumbers query: "+buildGoStatement(conditions.toString()));
        List results=Common.sendQuery(buildGoStatement(conditions.toString()),2); 
        if(results.size()==0)
            System.out.println("no results");
        
        //create one entry in a hashMap for each set of Seq_id's
        HashMap gos=new HashMap();
        
        for(Iterator rit=results.iterator();rit.hasNext();)
        {
            ArrayList row=(ArrayList)rit.next();
            
            ArrayList goSet=(ArrayList)gos.get(row.get(0)); //look up the Seq_id's array
            if(goSet==null)//no array found
            {//create a new array and add it to the hash
                goSet=new ArrayList();
                goSet.add(row.get(1)); //add one go number to array
                gos.put(row.get(0), goSet); 
            } 
            else //an array was found for this seq_id, so add this go number to it
                goSet.add(row.get(1));//add go number to array            
        }
        if(gos==null || gos.size()==0)
            System.out.println("nothing in gos");
        else
            System.out.println(gos.size()+" elements in go");
        return gos;
    }
    private HashMap findClusterNumbers(List data)
    {
        StringBuffer conditions=new StringBuffer();
        conditions.append("Clusters.Seq_id in (");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            conditions.append(i.next());
            if(i.hasNext())
                conditions.append(",");
        }
        conditions.append(")");
        System.out.println("sending clusterNumbers query: "+buildClusterStatement(conditions.toString()));
        List results=Common.sendQuery(buildClusterStatement(conditions.toString()));
        if(results==null || results.size()==0){
            System.out.println("no cluster numbers results");
            return null;
        }

        HashMap cids=new HashMap();
        for(Iterator i=results.iterator();i.hasNext();)
        {
            ArrayList row=(ArrayList)i.next();
            ArrayList set=(ArrayList)cids.get(row.get(0));//look up cluster set by seq_id
            if(set==null)//no array found
            {
                set=new ArrayList();
                set.add(new ClusterSet((String)row.get(1),(String)row.get(2),(String)row.get(3)));//add a cluster number
                cids.put(row.get(0), set);
            }
            else
                set.add(new ClusterSet((String)row.get(1),(String)row.get(2),(String)row.get(3))); //add the new cluster number
        }
        return cids;
    }
    
    private List getData(List input, String order, int limit, int[] db)
    {
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        conditions.append("sequences.seq_id in (");
        for(Iterator it=input.iterator();it.hasNext() && count++ < limit;)
        {
            conditions.append((String)it.next());
            if(it.hasNext() && count < limit)
                conditions.append(",");
        }
        conditions.append(")");
        rs=Common.sendQuery(buildKeyStatement(conditions.toString(),order,limit,db));
        return rs;
    }
    
    
    private String buildKeyStatement(String conditions,String order,int limit, int[] DBs)
    {
        StringBuffer general=new StringBuffer();

        general.append("SELECT DISTINCT primary_key, description,sequences.seq_id,count(models.model_id),genome "+
                       "FROM sequences LEFT JOIN models USING(seq_id),clusters, cluster_info  "+
                       "WHERE cluster_info.cluster_id=clusters.cluster_id and clusters.seq_id=sequences.seq_id and  ( ");

        for(int i=0;i<DBs.length;i++)
        {
            general.append(" genome='"+Common.dbRealNames[DBs[i]]+"' ");
            if(i < DBs.length-1)//not last iteration of loop
                general.append(" or ");
        }

        general.append(") and ( "+conditions+" ) GROUP BY sequences.seq_id,primary_Key, description,Genome ");
        //general.append("ORDER BY Genome,Primary_Key");
        general.append("ORDER BY "+order);
        //general.append(" limit "+limit);
        System.out.println("general Query: "+general);
        return general.toString();
    }
    private String buildGoStatement(String conditions)
    {
        //return "SELECT Seq_id, Go from Go WHERE "+conditions;
        return "SELECT primary_key, Go FROM sequences, go "+
               "WHERE sequences.seq_id=go.seq_id AND ("+conditions+")";
    }
    private String buildClusterStatement(String conditions)
    {
        return "SELECT DISTINCT Seq_id, Cluster_Info.filename,size,name FROM Clusters,cluster_info WHERE "+
            "Clusters.cluster_id=Cluster_Info.Cluster_id AND "+conditions;
    }
    
    
    class ClusterSet {
        public String clusterNum, size,name;
        ClusterSet(String cn,String s,String n)
        {
            clusterNum=cn;
            size=s;
            name=n;
        }
    }
}