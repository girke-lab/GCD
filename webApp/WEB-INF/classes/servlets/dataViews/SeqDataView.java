/*
 * SeqDataView.java
 *
 * Created on August 11, 2004, 8:52 AM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import servlets.Common;

public class SeqDataView implements DataView 
{
    List seq_ids;
    int limit,hid;
    String sortCol;
    int[] dbNums;
    
    private final int GENOME_COL=0, P_KEY_COL=1,
                      DESC_COL=2,   MODEL_COL=3,
                      GO_COL=4,     FILENAME_COL=5,
                      SIZE_COL=6,   NAME_COL=7;
    String dataColor="D3D3D3",titleColor="AAAAAA";
    
    /** Creates a new instance of SeqDataView */
    public SeqDataView() {
    }
    
    public void printData(java.io.PrintWriter out) 
    {
        List data=getData(seq_ids, sortCol, limit, dbNums);
        printCounts(out,data);
        printSummary(out,data,dbNums,hid);
    }
    
    public void setData(java.util.List ids, String sortCol, int limit, int[] dbList, int hid) 
    {
        this.seq_ids=ids;
        this.limit=limit;
        this.sortCol=sortCol;
        this.hid=hid;
        this.dbNums=dbList;
    }
 
    private void printCounts(PrintWriter out,List data)
    { //print number of keys and models
        int modelCount=0, keyCount=0;
        String lastKey=null,lastModel=null;
        
        for(Iterator i=data.iterator();i.hasNext();)
        {
            List row=(List)i.next();
            if(lastKey==null || !lastKey.equals(row.get(P_KEY_COL)))
            {
                keyCount++;
                lastKey=(String)row.get(P_KEY_COL);
            }
            if(lastModel==null || !lastModel.equals(row.get(MODEL_COL)))
            {
                modelCount++;
                lastModel=(String)row.get(MODEL_COL);
            }
        }
        out.println("Keys found: "+keyCount+", models found: "+modelCount+"<BR>");
    }
    private void printSummary(PrintWriter out, List data, int[] dbNums, int hid)
    {
        
        String genome=null,primary_key=null, desc=null,modelId=null;
        int modelCount=0;
        Collection goNumbers=new LinkedHashSet();
        Collection clusterNumbers=new LinkedHashSet();
        
        out.println("<TABLE bgcolor='"+dataColor+"' width='100%' align='center' border='1' cellspacing='0'>");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            List row=(List)i.next();
            if(!row.get(GENOME_COL).equals(genome))//genome changed
            {
                out.println("<TR><TD colspan='7' align='center' bgcolor='FFFFFF'><H2 align='center'>"+
                    Common.dbPrintNames[Common.getDBid((String)row.get(GENOME_COL))]+" Search Results</H2></TD></TR>"); 
                genome=(String)row.get(GENOME_COL); 
            }
            if(!row.get(P_KEY_COL).equals(primary_key))
            {//reset stuff for the next sequence
                if(primary_key!=null)
                    printRecord(out,primary_key,desc,goNumbers,clusterNumbers,modelCount,Common.getDBid(genome));
                primary_key=(String)row.get(P_KEY_COL);
                desc=(String)row.get(DESC_COL);
                goNumbers.clear();
                clusterNumbers.clear();
                modelCount=0;   
                modelId=null;
            }
            if(!row.get(MODEL_COL).equals(modelId))
            {
                modelCount++;
                modelId=(String)row.get(MODEL_COL);
            }
            if(row.get(GO_COL)!=null)
                goNumbers.add(row.get(GO_COL));            
            clusterNumbers.add(new ClusterSet((String)row.get(FILENAME_COL),(String)row.get(SIZE_COL),(String)row.get(NAME_COL)));            
        }
        //print last set
        printRecord(out,primary_key,desc,goNumbers,clusterNumbers, modelCount, Common.getDBid(genome));
        out.println("</TABLE>");
    }
    private void printRecord(PrintWriter out,String key,String desc,Collection gos,Collection clusters,int modelCount,int genome)
    {
        System.out.println("Key="+key);
        System.out.println("gos: "+gos);
        System.out.println("clusters: "+clusters);
        System.out.println("--------------------------");
        
        out.println("<TR bgcolor='"+titleColor+"'><TH>Key</TH><TH colspan='6' align='center'>Description</TH></TR>");
        out.println("<TR><TD><A href='http://bioinfo.ucr.edu/cgi-bin/seqview.pl?database=all&accession="+key+"'>"+key+"</A></TD>");
        out.println("<TD colspan='6'>"+desc+"</TD></TR>");        
        
        out.println("<TR><TH>Links</TH><TD colspan='6' nowrap> ");
        printLinks(out,key,genome,gos);
        out.println("</TD></TR>");
                
        printClusters(out, clusters);
        
        out.println("<TR><TD bgcolor='FFFFFF' colspan='7'>&nbsp</TD></TR>");
    }
    private void printClusters(PrintWriter out, Collection set)
    {
        if(set==null || set.size()==0)
            return;
        out.println("\t<TR bgcolor='"+titleColor+"'><TH>Clustering</TH><TH>Name</TH><TH>ID</TH><TH>Size</TH><TH>Members</TH><TH>Alignment</TH><TH>Tree</TH></TR>");
        String clusterType,blast="BLASTCLUST", hmm="Domain Composition";
        for(Iterator i=set.iterator();i.hasNext();)
        {//one row per set
             out.println("\t<TR>");
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
    }
    private void printLinks(PrintWriter out,String key,int genome,Collection goNumbers)    
    {//size is cluster size
         String db=null;
         if(genome==Common.arab)
             db="ath1";
         else if(genome==Common.rice)
             db="osa1";
         
         if(genome==Common.arab)
         {
             out.println("<a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+key+"'>TAIR</a>&nbsp&nbsp");
             out.println("<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+ key+"'>MIPS</a>&nbsp&nbsp");
         }
         out.println("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+ "ORF_infopage.cgi?db="+db+"&orf="+key+"'>TIGR</a>&nbsp&nbsp");
         out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/geneview.pl?accession="+key+"'>GeneStructure*</a>&nbsp&nbsp");
         //expression link goes here
         if(genome==Common.arab)
            out.println("<a href='http://signal.salk.edu/cgi-bin/tdnaexpress?GENE="+key+"&FUNCTION=&JOB=HITT&DNA=&INTERVAL=10'>KO</a>&nbsp&nbsp");

         //here we want an array of go numbers
         StringBuffer querys=new StringBuffer();
         if(goNumbers!=null) //gos may be null if this Seq_id does not have any GO numbers
           for(Iterator i=goNumbers.iterator();i.hasNext();)
                 querys.append("query="+((String)i.next()).replaceFirst(":","%3A")+"&"); //the : must be encoded
         if(querys.length()!=0)//we have at least one go number
            out.println("<a href='http://www.godatabase.org/cgi-bin/go.cgi?depth=0&advanced_query=&search_constraint=terms&"+querys+"action=replace_tree'>GO</a>&nbsp&nbsp");

         //does this link work for rice? no
         out.println("<a href='http://www.genome.ad.jp/dbget-bin/www_bget?ath:"+key+"'>KEGG</a>&nbsp&nbsp");         
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
        rs=Common.sendQuery(buildSeqViewStatement(conditions.toString(),order,limit,db));
        return rs;
    }
    private String buildSeqViewStatement(String conditions,String order,int limit, int[] DBs)
    {
        StringBuffer query=new StringBuffer();
        
        query.append("SELECT sequences.genome, sequences.primary_key,sequences.description,clusters.model_id," +
                    " go.go, cluster_info.filename,cluster_info.size,cluster_info.name "+
                "FROM clusters, cluster_info, sequences LEFT JOIN go USING (seq_id) "+
                "WHERE cluster_info.cluster_id=clusters.cluster_id AND clusters.seq_id=sequences.seq_id ");
        
                
        query.append(" AND (");
        for(int i=0;i<DBs.length;i++)
        {
            query.append("sequences.genome='"+Common.dbRealNames[DBs[i]]+"' ");
            if(i+1 < DBs.length)
                query.append(" or ");
        }
        
        query.append(") AND ( "+conditions+" ) ");
        query.append("ORDER BY sequences.genome,");
        if(order!=null && order != "")
            query.append(order+", ");
        query.append(" sequences.primary_key,clusters.model_id, go.go,cluster_info.filename ");
        //query.append("LIMIT "+limit);
        System.out.println("cluster view query: "+query);
        return query.toString();
    }
    
    class ClusterSet {
        public String clusterNum, size,name;
        ClusterSet(String cn,String s,String n)
        {
            clusterNum=cn;
            size=s;
            name=n;
        }
        public boolean equals(Object o)
        {
            System.out.println("comparing "+clusterNum);
            if(!(o instanceof ClusterSet))
                return false;
            ClusterSet cs=(ClusterSet)o;
            return cs.clusterNum.equals(clusterNum);            
        }
        public String toString()
        {
            return "("+clusterNum+","+size+","+name+", hash="+this.hashCode()+")";
        }
        public int hashCode()
        {
            return clusterNum.hashCode();
        }
    }
}
