/*
 * StatisticsDataView.java
 *
 * Created on March 22, 2005, 1:29 PM
 */

package servlets.dataViews;

/**
 *
 * @author jcui
 */

import servlets.*;
import servlets.dataViews.DataView;
import servlets.dataViews.queryWideViews.*;
import java.util.*;
import javax.servlet.http.*;
import java.io.*;
import org.apache.log4j.Logger;

public class StatisticsDataView implements DataView {
    List data = null;
    List seq_ids;
    int [] dbs;
    int hid;
    int keyType;
    String sortCol;
    String[] displayFields;
    
    private static Logger log = Logger.getLogger(StatisticsDataView.class);
    
    /** Creates a new instance of StatisticsDataView */
    public StatisticsDataView() {
        defineFields();
    }

    public QueryWideView getQueryWideView() {
        return new DefaultQueryWideView();
    }

    public void printData(java.io.PrintWriter out) {
        if (data == null){
            loadData();
        }
        printResult(out,data);        
    }

    public void printHeader(java.io.PrintWriter out) {
        Common.printHeader(out);
        out.println("<p>");
        Common.printForm(out,hid);
    }

    public void printStats(java.io.PrintWriter out) {
        if (data == null)
            loadData();
        if (seq_ids == null){
            log.debug("Seq_id is null");
        }
        if (data == null){
            log.debug("data is null");
        }
        Common.printStatsTable(out, "On This Page", new String[]{"Loci", "Model"},
                new Object[]{new Integer(seq_ids.size()), new Integer(data.size())});
    }

    public void setData(String sortCol, int[] dbList, int hid) {
        this.sortCol = sortCol;
        this.dbs = dbList;
        this.hid = hid;        
    }

    public void setIds(java.util.List ids) {
        this.seq_ids = ids;
        loadData();
    }

    public void setSortDirection(String dir) {
    }
    
    private void loadData(){
        if (seq_ids.size() == 0)
            data = new ArrayList();
        else
            data = searchData(seq_ids);
    }
    
    private void printResult(java.io.PrintWriter out, List rs){
        StringBuffer output = new StringBuffer();
        int lastDB = -1;
        int currentDB;
        String key, desc, value;
        
        if (rs == null || rs.size() == 0)
            return;
        
        int fieldsLen = ((ArrayList)rs.get(0)).size() - 1; //last column is genome, we print it as subtitle
        
        output.append("<table align='center' border='1' cellspacing='0' cellpadding='5'>");
        for (ListIterator l = rs.listIterator();l.hasNext();){
            List row = (ArrayList)l.next();
            key = (String)row.get(0); //sequence name
            desc = (String)row.get(1); //sequence description 
            currentDB = Common.getDBid((String)row.get(row.size() - 1));
            if (lastDB != currentDB) { //db has now changed to a new db
                output.append("<TR><TH colspan='10'><H2>" + Common.dbPrintNames[currentDB] + " search results:</H2></TH></TR>");
            }
            lastDB = currentDB;
                
            output.append("<TR bgcolor='" + PageColors.title + "'><TH align='left'>Key</TH><TH align='left' colspan='9'>Description</TH></TR>");
            output.append("<TR bgcolor='" + PageColors.data + "'><TD>" + key + "</TD><TD colspan='9'>" + desc + "</TD></TR>");
            output.append("<TR bgcolor='" + PageColors.title + "'><TH align='left'>Model ID</TH><TH align='left'>Model Length</TH>" + 
                    "<TH align='left'>Left Intron Count</TH><TH align='left'>Right Intron Count</TH><TH align='left'>Left Intron Length</TH>" +
                    "<TH align='left'>Right Intron Length</TH><TH align='left'>Left UTR Length</TH><TH align='left'>Right UTR Length</TH>" + 
                    "<TH align='left'>Left Deltag</TH><TH align='left'>Right Deltag</TH></TR>");
            output.append("<TR bgcolor='" + PageColors.data + "'>");
            for (int i=2;i<fieldsLen;i++){
                value = (String)row.get(i);
                if (value == null || value.compareTo("") == 0)
                    output.append("<TD>&nbsp;</TD>");
                else
                    output.append("<TD>" + value + "</TD>");
            }
            output.append("</TR>");
            output.append("<TR><TD colspan='10'>&nbsp;</TD></TR>");
        }
        output.append("</table>");
        out.println(output);
    }
    
    private List searchData(List keys){
        StringBuffer conditions = new StringBuffer();
        StringBuffer fields = new StringBuffer();
        List rs = null;
        String query;
        
        for (int i=0;i<displayFields.length;i++){
            if (i != 0)
                fields.append(", ");
            fields.append(displayFields[i]);
        }
        
        ListIterator in = keys.listIterator();
        conditions.append("st.seq_id in (");
        while (in.hasNext()){
            conditions.append("'" + in.next() + "'");
            if (in.hasNext()){
                conditions.append(",");
            }
        }
        conditions.append(")");
        
        query = buildStatStatement(fields.toString(), conditions.toString());
        rs = Common.sendQuery(query);
        
        return rs;       
    }
    
    private String buildStatStatement(String fields, String conditions){
        StringBuffer query = new StringBuffer();
        query.append("SELECT " + fields + " FROM statistics AS st, sequences AS se WHERE " + conditions + " AND se.seq_Id = st.seq_id" +
                " ORDER BY se.genome, st.seq_id, st.model_id");
        log.info("Statistics Query: " + query);
        return query.toString();
    }
    
    private void defineFields(){
        displayFields = new String[]{//se --> sequences table; st--> statistics table
            "se.primary_key", "se.description", 
                    "st.model_id",  "st.model_length",
                    "st.left_intron_count",    "st.right_intron_count",
                    "st.left_intron_lengths",  "st.right_intron_lengths",
                    "st.left_utr_length",      "st.right_utr_length",
                    "st.left_deltag",          "st.right_deltag",
                    "se.genome"};
    }   

    public int[] getSupportedKeyTypes()
    {
         return new int[]{Common.KEY_TYPE_SEQ};
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
}
