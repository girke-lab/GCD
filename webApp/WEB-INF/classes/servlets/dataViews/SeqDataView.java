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
    int hid;
    String sortCol;
    int[] dbNums;
    List records=null;
    
    private final int GENOME_COL=0, P_KEY_COL=1,
                      DESC_COL=2,   MODEL_COL=3,
                      GO_COL=4,     FILENAME_COL=5,
                      SIZE_COL=6,   NAME_COL=7;
    String dataColor="D3D3D3",titleColor="AAAAAA";
    
    /** Creates a new instance of SeqDataView */
    public SeqDataView() {
    }
    
    public void setData(java.util.List ids, String sortCol, int[] dbList, int hid) 
    {
        this.seq_ids=ids;        
        this.sortCol=sortCol;
        this.hid=hid;
        this.dbNums=dbList;
    }
    public void printHeader(java.io.PrintWriter out) {
        Common.printForm(out,hid);    
    }   
    public void printData(java.io.PrintWriter out) 
    {        
        if(records==null)
            loadData();
        printSummary(out,records);
    }
    public void printStats(java.io.PrintWriter out) {
        if(records==null)
            loadData();
        printCounts(out,records);
    }    
    
    
///////////////////////////////////////////////////////////////////////
    private void loadData()
    {
        records=parseData(getData(seq_ids,sortCol,dbNums));
    }
    private void printCounts(PrintWriter out,List records)
    { //print number of keys and models
        int modelCount=0;        
        Collection temp=new HashSet();
        SeqRecord sr=null;
        for(Iterator i=records.iterator();i.hasNext();)
        {
            sr=(SeqRecord)i.next();
            modelCount+=sr.getModelCount();  
            temp.addAll(sr.getClusters());
        }

        Common.printPageStats(out, records.size(), modelCount, temp.size());
//        out.println("Unique clusters found: "+temp.size()+"<BR>");
//        out.println("Keys found: "+records.size()+",&nbsp&nbsp Models found: "+modelCount+"<BR>");
    }
    private List parseData(List data)
    {
        String genome=null,primary_key=null, desc=null,modelId=null;        
        Collection goNumbers=new LinkedHashSet();
        Collection clusterNumbers=new LinkedHashSet();
        List records=new LinkedList();
        SeqRecord sr=null;
        
        
        for(Iterator i=data.iterator();i.hasNext();)
        {
            List row=(List)i.next();
            if(!row.get(P_KEY_COL).equals(primary_key))
            {//reset stuff for the next sequence
                if(primary_key!=null) //first is null
                    records.add(sr);                
                primary_key=(String)row.get(P_KEY_COL);
                sr=new SeqRecord(primary_key,(String)row.get(DESC_COL),(String)row.get(GENOME_COL));                
            }
            sr.addModel((String)row.get(MODEL_COL));                
            if(row.get(GO_COL)!=null)
                sr.addGoNumber((String)row.get(GO_COL));
            sr.addCluster(new ClusterSet((String)row.get(FILENAME_COL),(String)row.get(SIZE_COL),(String)row.get(NAME_COL)));                
        }
        //store last set
        records.add(sr);
        return records;
    }
    private void printSummary(PrintWriter out,List data)
    {
        String genome=null;
        SeqRecord sr=null;
        printForms(out,data);
        
        out.println("<TABLE bgcolor='"+dataColor+"' width='100%' align='center' border='1' cellspacing='0'>");
        for(Iterator i=data.iterator();i.hasNext();)
        {
            sr=(SeqRecord)i.next();            
            if(!sr.getGenome().equals(genome)) //genome has changed
            {
                out.println("<TR><TD colspan='7' align='center' bgcolor='FFFFFF'><H2 align='center'>"+
                    Common.dbPrintNames[Common.getDBid(sr.getGenome())]+" Search Results</H2></TD></TR>"); 
                genome=sr.getGenome(); 
            } 
            sr.printRecord(out);
        }
        out.println("</TABLE>");
         
    }

    private void printForms(PrintWriter out, List data)
    {
        if(data==null || data.size() == 0)
            return;

        out.println("<FORM METHOD='POST' ACTION='http://bioinfo.ucr.edu/cgi-bin/multigene.pl'>\n");
        out.println("<TABLE border='0' ><TR>");        
        out.println("<TD><INPUT type='submit' value='All Gene Structures' "+
                    " onClick=\"javascript: action='http://bioinfo.ucr.edu/cgi-bin/multigene.pl'; submit();\"></TD>");
        out.println("<TD><INPUT type='submit' value='Chr Map' "+       
                    " onClick=\"javascript: action='http://bioinfo.ucr.edu/cgi-bin/chrplot.pl'; submit();\"></TD>");
        out.println("<TD><INPUT type='submit' value='Go Slim Counts' "+
                    " onClick=\"javascript: action='http://bioinfo.ucr.edu/cgi-bin/goSlimCounts'; submit();\"></TD>");
        out.println("<TD><INPUT type='submit' value='Key List' "+
                " onClick=\"javascript: action='http://bioinfo.ucr.edu/cgi-bin/displayKeys.pl'; submit();\"></TD>");

        out.println("</TR></TABLE>");

        out.println("<INPUT type=hidden name='database' value='all'/>");
        out.println("<INPUT type=hidden name='total_seq_count' value='"+data.size()+"'/>\n");

        SeqRecord sr;
        for(Iterator i=data.iterator();i.hasNext();)
        {
            sr=(SeqRecord)i.next();
            for(Iterator j=sr.getGoNumbers().iterator();j.hasNext();)
                out.println("<INPUT type=hidden name='go_numbers' value='"+sr.getKey()+"_"+j.next()+"' />");
            out.println("<INPUT type=hidden name='accession' value='"+sr.getKey()+"'/>\n");
            
            if(sr.getGoNumbers().size()==0) //no go numbers
                out.println("<INPUT type=hidden name='missing_keys' value='"+sr.getKey()+"' />\n");
        }
        out.println("</FORM></TD></TR></TABLE>\n");
    }
    private List getData(List input, String order, int[] db)
    {
        StringBuffer conditions=new StringBuffer();
        List rs=null;
        int count=0;

        conditions.append("sequences.seq_id in (");
        for(Iterator it=input.iterator();it.hasNext();)
        {
            conditions.append((String)it.next());
            if(it.hasNext())
                conditions.append(",");
        }
        conditions.append(")");
        rs=Common.sendQuery(buildSeqViewStatement(conditions.toString(),order,db));
        return rs;
    }
    private String buildSeqViewStatement(String conditions,String order, int[] DBs)
    {
        StringBuffer query=new StringBuffer();
        
        query.append("SELECT sequences.genome, sequences.primary_key,sequences.description,models.model_id," +
                    " go.go, cluster_info.filename,cluster_info.size,cluster_info.name "+
                "FROM sequences LEFT JOIN models USING (seq_id) LEFT JOIN clusters USING (seq_id) LEFT JOIN cluster_info USING (cluster_id) LEFT JOIN go ON (sequences.seq_id=go.seq_id)"+
                //clusters, cluster_info, sequences LEFT JOIN go USING (seq_id) "+
                "WHERE  ");
        
                
        query.append("  (");
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
        System.out.println("sequence view query: "+query);
        return query.toString();
    }
//////////////////////////////////////////////////////////////////////
    //  Classes 
//////////////////////////////////////////////////////////////////////    
   
    
    class ClusterSet {
        public String clusterNum, size,name;
        ClusterSet(String cn,String s,String n)
        {
            clusterNum= cn==null? "":cn;
            size= s==null? "":s;
            name= n==null? "":n;
        }
        public boolean equals(Object o)
        {
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
    class SeqRecord
    {
        Collection goNumbers,clusters,models;
        String key,desc,genome;        
        
        public SeqRecord()
        {
            goNumbers=new HashSet();
            clusters=new LinkedHashSet(); //keeps order
            models=new HashSet();
        }
        public SeqRecord(String key,String desc,String genome)
        {
            this.key=key;
            this.desc=desc;
            this.genome=genome;
            goNumbers=new HashSet();
            clusters=new LinkedHashSet(); //keeps order
            models=new HashSet();
        }
        
        public void addGoNumber(String go){
            goNumbers.add(go);
        }
        public void addCluster(ClusterSet cs){
            clusters.add(cs);
        }
        public void addModel(String m){
            models.add(m);
        }
        public void setKey(String k){
            key=k;
        }
        public void setDesc(String desc){
            this.desc=desc;
        }
        public void setGenome(String g){
            genome=g;
        }
        public String getKey(){
            return key;
        }
        public String getDesc(){
            return desc;
        }
        public String getGenome(){
            return genome;
        }
        public int getModelCount(){
            return models.size();
        }
        public Collection getGoNumbers(){
            return goNumbers;
        }
        public Collection getClusters(){
            return clusters;
        }
        public String toString()
        {
            StringBuffer out=new StringBuffer();
            out.append("key="+key+", desc="+desc+", genome="+genome+"\n");
            out.append("go numbers: \n");
            for(Iterator i=goNumbers.iterator();i.hasNext();)
                out.append(i.next()+"\n");
            out.append("clusters are: \n");
            for(Iterator i=clusters.iterator();i.hasNext();)
                out.append(i.next()+"\n");
            return out.toString();
        }
                
        public void printRecord(PrintWriter out)
        {
            out.println("<TR bgcolor='"+titleColor+"'><TH>Key</TH><TH colspan='6' align='center'>Description</TH></TR>");
            out.println("<TR><TD><A href='http://bioinfo.ucr.edu/cgi-bin/seqview.pl?database=all&accession="+key+"'>"+key+"</A></TD>");
            out.println("<TD colspan='6'>"+desc+"</TD></TR>");        

            out.println("<TR><TH>Links</TH><TD colspan='6' nowrap> ");
            printLinks(out);
            out.println("</TD></TR>");

            printClusters(out);

            out.println("<TR><TD bgcolor='FFFFFF' colspan='7'>&nbsp</TD></TR>");
        }
        ///////////////////////// PRIVATE METHODS ////////////////////

        private void printClusters(PrintWriter out)
        {
            if(clusters.size()==0)
                return;
            boolean hasCluster=false;
            for(Iterator i=clusters.iterator();i.hasNext();)
            {
                if(((ClusterSet)i.next()).clusterNum!=""){
                    hasCluster=true;
                    break;
                }
            }
            if(!hasCluster)
                return;
            out.println("\t<TR bgcolor='"+titleColor+"'><TH>Clustering</TH><TH>Name</TH><TH>ID</TH><TH>Size</TH><TH>Members</TH><TH>Alignment</TH><TH>Tree</TH></TR>");
            String clusterType,blast="BLASTCLUST", hmm="Domain Composition";
            for(Iterator i=clusters.iterator();i.hasNext();)
            {//one row per set

                 ClusterSet cs=(ClusterSet)i.next();
                 if(cs.clusterNum=="")
                     continue;
                 clusterType=cs.clusterNum.matches("PF.*") ? hmm : blast;                          
                 out.println("\t<TR>");
                 out.println("\t\t<TD nowrap>"+clusterType+"</TD>");                                      
                 if(cs.name=="")
                     out.println("\t\t<TD>&nbsp</TD>");
                 else
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
                 out.println("\t\t<TD><a href='/databaseWeb/index.jsp?fieldName=Cluster Id&limit=0&input="+cs.clusterNum+"'>Retrieve</a></TD>");
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
        private void printLinks(PrintWriter out)    
        {//size is cluster size
             String db=null;
             int g=Common.getDBid(genome);
             if(g==Common.arab)
                 db="ath1";
             else if(g==Common.rice)
                 db="osa1";

             if(g==Common.arab)
             {
                 out.println("<a href='http://www.arabidopsis.org/servlets/TairObject?type=locus&name="+key+"'>TAIR</a>&nbsp&nbsp");
                 out.println("<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+ key+"'>MIPS</a>&nbsp&nbsp");
             }
             out.println("<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+ "ORF_infopage.cgi?db="+db+"&orf="+key+"'>TIGR</a>&nbsp&nbsp");
             out.println("<a href='http://bioinfo.ucr.edu/cgi-bin/geneview.pl?accession="+key+"'>GeneStructure*</a>&nbsp&nbsp");
             //expression link goes here
             if(g==Common.arab)
                out.println("<a href='http://signal.salk.edu/cgi-bin/tdnaexpress?GENE="+key+"&FUNCTION=&JOB=HITT&DNA=&INTERVAL=10'>KO</a>&nbsp&nbsp");

             //here we want an array of go numbers
             StringBuffer querys=new StringBuffer();
             for(Iterator i=goNumbers.iterator();i.hasNext();)
                 querys.append("query="+((String)i.next()).replaceFirst(":","%3A")+"&"); //the : must be encoded
             if(querys.length()!=0)//we have at least one go number
                out.println("<a href='http://www.godatabase.org/cgi-bin/go.cgi?depth=0&advanced_query=&search_constraint=terms&"+querys+"action=replace_tree'>GO</a>&nbsp&nbsp");

             //does this link work for rice? no
             out.println("<a href='http://www.genome.ad.jp/dbget-bin/www_bget?ath:"+key+"'>KEGG</a>&nbsp&nbsp");         
        }        
    }
}
