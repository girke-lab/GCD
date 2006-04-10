/*
 * ModelDataView.java
 *
 * Created on August 20, 2004, 1:40 PM
 */

package servlets.dataViews;

/**
 *
 * @author  khoran
 */

import servlets.*;
import servlets.dataViews.queryWideViews.*; 
import java.util.*;
import javax.servlet.http.*;
import java.io.*;
import org.apache.log4j.Logger;
import servlets.beans.HeaderBean;
import servlets.querySets.*;

public class ModelDataView implements DataView
{
    final int fieldCount=10;        
    final int STANDARD=0, FASTA=1,ALL_FASTA=2;    
    final int LINE_SIZE=1000; //number of base pairs to print on a line
                             //per database query
      
    List seq_ids;
    String sortCol;
    int[] dbs;
    int hid;
    int keyType;
    HttpSession session;
    HttpServletRequest request;
    List data=null;
    private String userName; 
    private HeaderBean header=new HeaderBean();

    Map storage;
    
    ModelQueryInfo mqi;
    
    String[] fullNames;//names to use in querys
    String[] printNames;//names to print on screen    
    private static Logger log=Logger.getLogger(ModelDataView.class);
    
    /** Creates a new instance of ModelDataView */
    public ModelDataView(HttpServletRequest request)
    {           
        this.request=request;
        session=request.getSession(false);                
    }
            
    public void printData(java.io.PrintWriter out) 
    {                
        if(data==null)
            loadData();        
        printFasta(out,data,mqi.fieldNums,mqi.length,mqi.format);      
        
    }    
    public void printHeader(java.io.PrintWriter out) 
    {        
        header.setHeaderType(servlets.beans.HeaderBean.HeaderType.GCD);
        header.printStdHeader(out,"", userName!=null);
        

        out.println("<p>");
        Common.printForm(out,hid);
    }    
    public void printFooter(java.io.PrintWriter out)
    {
        header.printFooter();
    }
    public void printStats(java.io.PrintWriter out) 
    {
        if(data==null)
            loadData();
        Common.printStatsTable(out,"On This Page", new String[]{"Loci","Models"},
            new Object[]{new Integer(seq_ids.size()),new Integer(data.size())});        
    }    
    public void setData(String sortCol, int[] dbList, int hid) 
    {        
        this.sortCol=sortCol;
        this.dbs=dbList;
        this.hid=hid;
        
        //store the data we got from request in the session object
        QueryInfo qi=(QueryInfo)((List)session.getAttribute("history")).get(hid);
        ModelQueryInfo mqi_temp=(ModelQueryInfo)qi.getObject("ModelQueryInfo");
        
        //update the stored mqi with any new options
        mqi=getOptions(mqi_temp);        
        qi.setObject("ModelQueryInfo",mqi);
        defineNames();
    }    
    public void setIds(java.util.List ids) 
    {
        this.seq_ids=ids;   
        loadData();
    }
    public void setParameters(Map parameters)
    {
    }
    public void setStorage(Map storage)
    {
    }
    public void setUserName(String userName)
    {
        this.userName=userName;;
    }
    
    public QueryWideView getQueryWideView() 
    {
        return new DefaultQueryWideView(){
            public boolean printAllData(){
                return mqi.format==ALL_FASTA;
            }
        };
    }     
     
/////////////////////////////////////////////////////////////////////////////
//                              Private  Methods                 
/////////////////////////////////////////////////////////////////////////////
    private void loadData()
    {
        if(seq_ids.size()==0)
            data=new ArrayList();
        else
            data=searchByKey(seq_ids,mqi.fieldNums,mqi.fieldsLength);
    }
    private ModelQueryInfo getOptions(ModelQueryInfo mqit)
    {
            int[] fieldNums=new int[fieldCount];       
            int fieldsLength=0;
            int length,format;
            boolean[] isNew=new boolean[3];
            

            //get the list of feilds from the web page
            String[] temp1=request.getParameterValues("fields");//all fields                
            isNew[0]=(temp1!=null);
            try{//test feildNums, feildLength    
                int i;
                for(i=0;i<temp1.length;i++)
                    fieldNums[i]=Integer.parseInt(temp1[i]);           
                fieldsLength=i;
            }catch(Exception e){}            
            try{//test length
                length=Integer.parseInt(request.getParameter("length"));
                isNew[1]=true;
            }catch(NumberFormatException nfe){
                isNew[1]=false;
                length=0;// really means everyting
            }
            try{ //test format, if not defined, set to STANDARD
                format=Integer.parseInt(request.getParameter("format"));
                isNew[2]=true;
            }catch(Exception e){
                isNew[2]=false;
                format=STANDARD;
            }
            if(mqit==null || temp1!=null)
                return new ModelQueryInfo(fieldNums, fieldsLength, length,format);
            else 
                return mqit;
    }
    private List searchByKey(List keys,int[] fields,int fieldsLength)
    {   //takes a List of keys to search for, and the fields to return
        //returns a string of actual keys returned from database
       
        StringBuffer conditions=new StringBuffer();
        StringBuffer feildCombo=new StringBuffer();            
        List rs=null;
        int fieldCount=3; //we add accession, model, and description
        int count=0; //used to limit number of keys actually sent to database

        feildCombo.append(fullNames[0]);  //accession
        feildCombo.append(", "+fullNames[1]); //model number
        feildCombo.append(", "+fullNames[2]); //description
        for(int i=0;i<fieldsLength;i++)
            if(fullNames[fields[i]].length()!=0)
            {
                feildCombo.append(", "+fullNames[fields[i]]);
                fieldCount++;
            }

        feildCombo.append(", "+fullNames[10]); //always query genome so we know where to put titles
        fieldCount++;

        rs=Common.sendQuery(QuerySetProvider.getDataViewQuerySet().getModelDataViewQuery(keys,feildCombo.toString(), -1));                
           
        return rs;   
    }       
    private void printFasta(PrintWriter out,List rs,int[] currentFeildNums,int length,int format)
    {
        
        StringBuffer fastaOutput=new StringBuffer();//gets send to blast script
        StringBuffer record=new StringBuffer(); //these get sent to screen
        StringBuffer standard=new StringBuffer();
        StringBuffer temp=new StringBuffer();
        String key,key2,desc,data, tigrDb="ath1";
        int start,currentDB;
        int lastDB=-1;
        if(rs==null || rs.size()==0)
            return;
        
        int fieldsLength=((ArrayList)rs.get(0)).size()-1; //last entry is genome data, don't print it.
        try{            
            standard.append("<TABLE align='center' border='1' cellspacing='0'>");
            
            for(ListIterator l=rs.listIterator();l.hasNext();)
            {
                List row=(ArrayList)l.next();
                key=(String)row.get(0); //accession number
                key2=(String)row.get(1); //model accession number
                desc=(String)row.get(2); //description
                start=3;
                
                currentDB=Common.getDBid((String)row.get(row.size()-1)); //genome is always last entry
                if(lastDB!=currentDB)//db has now changed to a new db
                {
                    tigrDb="ath1";
                    if(currentDB==Common.rice) 
                        tigrDb="osa1";
                    standard.append("<TR><TH colspan='2'><H2 align='left'>"+Common.dbPrintNames[currentDB]+" search results:</H2></TH></TR>");
                }
                lastDB=currentDB;
  
                int index=0;                
                for(int f=start;f<fieldsLength;f++)
                {   
                    data=(String)row.get(f);                          
                    if(data==null || data.compareTo("")==0 )//|| currentFeildNums[index]==3)
                    {
                        index++;
                        continue;                                        
                    }
                    if(key2.startsWith(key))
                        record.append(">"+key2+" "+desc+": "+printNames[currentFeildNums[index]]+"\n");
                    else
                        record.append(">"+key2+" "+key+" "+desc+": "+printNames[currentFeildNums[index]]+"\n");
                    
                    if(currentFeildNums[index]==4){ //deal with the promoter
                        if(data.length() > 3000) //only trim if it is greater than 3000
                            data=data.substring(0,3000).toUpperCase(); //trim the intergenic to 3000 
                    }
                    
                    if(currentFeildNums[index]==3) //dont uppercase the TU
                        ; //dont change data at all                    
                    else if(currentFeildNums[index]==9)//dont trim the protein  
                        data=data.toUpperCase();
                    else if(length > 0 && length < data.length())  //trim output feilds to length
                        data=data.substring(0,length).toUpperCase();
                    else if(length < 0 && (-1*length) < data.length())
                        //length is negative, so adding it to data.lenth moves back from the end
                        data=data.substring(data.length()+length,data.length()).toUpperCase();
                    else //length==0
                        data=data.toUpperCase();
                    
                    //insert some spaces into data, so that the text is wrapped
                    if(format==STANDARD)
                    {
                        StringBuffer temp2=new StringBuffer(data);
                        for(int j=LINE_SIZE;j<temp2.length();j+=LINE_SIZE)
                            temp2.insert(j,' ');
                        data=temp2.toString();
                    }
                  
                    temp.append("\t<TR bgcolor='"+PageColors.data+"'>"+
                        "<TH align='left'>"+printNames[currentFeildNums[index]]+"</TH>"+
                        "<TD>"+data+"</TD></TR>\n");
                    record.append(data+"\n");
                    index++;
                }
                fastaOutput.append(record);                   
                standard.append("<FORM method=post action='http://138.23.191.152/blast/blastSearch.cgi'>"+
                    "<INPUT type=hidden name='input' value=\""+record+"\">\n");
                standard.append("\t<TR bgcolor='"+PageColors.title+"'><TH>Links</TH>"+
                    "<TD>" +
                        ((currentDB!=Common.rice)?"<a href='http://mips.gsf.de/cgi-bin/proj/thal/search_gene?code="+key+"'>MIPS</a>&nbsp&nbsp":"")+
                        "<a href='http://www.tigr.org/tigr-scripts/euk_manatee/shared/"+
                            "ORF_infopage.cgi?db="+tigrDb+"&orf="+key+"'>TIGR</a>" +
                    "</TD></TR>");      
                standard.append("\t<TR bgcolor='"+PageColors.title+"'><TH align='left'>Accession</TH><TD>"+
                    "<A href='http://bioweb.ucr.edu/scripts/seqview.pl?db=all&accession="+key+"'>"+key+"</A>"+
                    "&nbsp&nbsp<INPUT type=submit value='Blast it'></TD></TR>"+
                    "\t<TR bgcolor='"+PageColors.title+"'><TH align='left'>Model Accession</TH><TD>"+key2+"</TD></TR>"+
                    "\t<TR bgcolor='"+PageColors.title+"'><TH align='left'>Description</TH><TD>"+desc+"</TD></TR>\n");
                standard.append(temp);
                standard.append("<TR><TD colspan='2'>&nbsp</TD></TR></FORM>\n");
                record.setLength(0); //erase string
                temp.setLength(0);
            }
            standard.append("</TABLE>\n");
            if(format==STANDARD)
                out.println(standard);
            else if(format==FASTA || format==ALL_FASTA)
                out.println("<PRE>"+fastaOutput.toString()+"</PRE>");
        }catch(NullPointerException npe){
            log.error("null pointer in fasta: "+npe.getMessage());
            npe.printStackTrace();
        }
    }
///////////////////////////  Query stuff  ///////////////////////////////////////////////////////    
   
    
    private void defineNames()
    {
        //assign names for later lookup
        fullNames=QuerySetProvider.getDataViewQuerySet().getModelColumns();
                        
        //names to be printed on the screen
        printNames=new String[fieldCount];
        printNames[0]="Id 1";printNames[1]="Id 2";printNames[2]="Description";printNames[3]="Transcription Model";
        printNames[4]="Promoter 3000";printNames[5]="3`UTR";printNames[6]="Intergenic";printNames[7]="CDS";
        printNames[8]="5`UTR";printNames[9]="Protein";        
    }
     
     public void setSortDirection(String dir)
     {
     }     

    public int[] getSupportedKeyTypes()
    {
         return new int[]{Common.KEY_TYPE_MODEL};
    }

    public void setKeyType(int keyType) throws servlets.exceptions.UnsupportedKeyTypeException
    {
        boolean isValid=false;
        int[] keys=getSupportedKeyTypes();
        for(int i=0;i<keys.length;i++)
            if(keyType == keys[i]){
                isValid=true;
                break;
            }
        if(!isValid)
            throw new servlets.exceptions.UnsupportedKeyTypeException(keys,keyType);
        this.keyType=keyType;
    }


    public int getKeyType()
    {
        return keyType;
    }


    
     
    class ModelQueryInfo implements java.io.Serializable
    {
        public int[] fieldNums=new int[fieldCount];       
        public int fieldsLength=0;
        public int length,format;
        
        public ModelQueryInfo(int[] fields,int fLength,int length,int format)
        {
            this.fieldNums=fields;
            this.fieldsLength=fLength;
            this.length=length;
            this.format=format;
        }
        public String toString()
        {
            String out="fields: [";
            for(int i=0;i<fieldsLength;i++){
                out+=fieldNums[i];
                if(i+1<fieldsLength)
                    out+=",";
            }
            out+="]\n length="+length+", format="+format;
            return out;            
        }
    }
}
