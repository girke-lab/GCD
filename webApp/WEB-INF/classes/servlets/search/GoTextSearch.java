/*
 * GoTextSearch.java
 *
 * Created on March 22, 2005, 8:41 AM
 */

package servlets.search;

/**
 *
 * @author jcui
 */
import java.util.*;
import servlets.search.Search;
import servlets.Common;
        
public class GoTextSearch extends AbstractSearch {
    
    /** Creates a new instance of GoTextSearch */
    public GoTextSearch() {
    }
    
    void loadData(){
        ListIterator in = input.listIterator();
        StringBuffer conditions = new StringBuffer();
        List rs = null;
        int count = 0;
        
        while (in.hasNext()){
            String temp = (String)in.next();
            if (count != 0){
                conditions.append(" and ");
            }            
            conditions.append(" ( g.text " + Common.ILIKE + " '%" + temp + "%') ");
            count++;
        }
        
        seqId_query = buildIdStatement(conditions.toString(), limit);
        rs = Common.sendQuery(seqId_query);
        
        List al = new ArrayList();
        String lastDb = "";
        List row;
        int index = 0;
        
        for (Iterator i=rs.iterator();i.hasNext();index++){
            row = (List)i.next();
            if (!lastDb.equals(row.get(1))){
                lastDb=(String)row.get(1);
                dbStartPositions[Common.getDBid(lastDb)] = index;
            }
            al.add(row.get(0));
        }
        data = al;
    }
    
    private String buildIdStatement(String conditions, int limit){
        String query = "SELECT DISTINCT s.Seq_id, s.genome from go AS g, sequences AS s " + 
                "where g.seq_id = s.seq_id AND ";
        
        query += "(" + conditions + ")";
        query += " limit " + limit;
        log.info("GoTextSearch Query: " + query);
        return query;
    }
}
