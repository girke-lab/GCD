/*
 * CommonDatabase.java
 *
 * Created on September 7, 2004, 12:45 PM
 */

package servlets.advancedSearch;

/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;

public class CommonDatabase implements SearchableDatabase
{
    public Field[] fields;
    public String[] operators;
    public String[] booleans;  
    
    /** Creates a new instance of CommonDatabase */
    public CommonDatabase() 
    {
        defineOptions();
    }
    
    public String[] getBooleans() 
    {
        return booleans;
    }
    
    public Field[] getFields() 
    {
        return fields;
    }
    
    public String[] getOperators() 
    {
        return operators;
    }
    
    public String buildQuery(SearchState state)
    {
        StringBuffer query=new StringBuffer();
        String fieldList, order, join;
        
        if(fields[state.getSortField()].dbName.startsWith("cluster_info")){
            fieldList=" cluster_info.cluster_id, "+fields[state.getSortField()].dbName;
            order=fields[state.getSortField()].dbName;
        }else{
            fieldList=" sequences.seq_id, "+fields[state.getSortField()].dbName+",sequences.genome ";
            order=" sequences.genome, "+fields[state.getSortField()].dbName; 
        }
        join=" sequences LEFT JOIN clusters USING (seq_id) LEFT JOIN cluster_info USING (cluster_id) LEFT JOIN go ON (sequences.seq_id=go.seq_id) ";
                
        query.append("SELECT DISTINCT "+fieldList+" FROM "+join+" WHERE (");                
        
        int sp=0,ep=0;
        int fid,oid;
        for(int i=0;i<state.getSelectedFields().size();i++)
        {
            if(sp < state.getStartParinths().size() && state.getStartParinth(sp).intValue()==i){
                sp++;
                query.append("(");
            }
            fid=state.getSelectedField(i).intValue();
            oid=state.getSelectedOp(i).intValue();
            
            
            if(fields[fid].displayName.equals("Cluster Type")){
                if(state.getValue(i).equals("blast"))
                    query.append(fields[fid].dbName+" NOT "+Common.ILIKE+" 'PF%' ");
                else if(state.getValue(i).equals("hmm"))
                    query.append(fields[fid].dbName+" "+Common.ILIKE+" 'PF%'");
            }
            else{
                query.append(fields[fid].dbName+" "+operators[oid]+" ");

                if(fields[fid].type.equals(String.class) || fields[fid].type.equals(List.class))
                    query.append("'"+state.getValue(i)+"'");            
                else
                    query.append(state.getValue(i));    
            }
            
            query.append(" ");
            
            if(ep < state.getEndParinths().size() && state.getEndParinth(ep).intValue()==i){
                ep++;
                query.append(")");
            }

            if(i+1 < state.getSelectedFields().size())
                query.append(booleans[state.getSelectedBool(i).intValue()]+" ");                        
        }
        query.append(") ");
        query.append(" ORDER BY "+order);
        query.append(" LIMIT "+state.getLimit());
        return query.toString();
    }
    private void defineOptions()
    {                
        fields=new Field[]{ new Field("Loci Id", "sequences.primary_key"), 
                            new Field("Loci Description","sequences.description"),
                            new Field("Cluster Id","cluster_info.filename"),
                            new Field("Cluster Name","cluster_info.name"),
                            new Field("Cluster Type","cluster_info.filename",new String[]{"blast","hmm"}),
                            new Field("Cluster Size","cluster_info.size",Integer.class),
                            new Field("# arab keys in cluster","cluster_info.arab_count",Integer.class),
                            new Field("# rice keys in cluster","cluster_info.rice_count",Integer.class),
                            new Field("Database","sequences.Genome",new String[]{"arab","rice"}),
                            new Field("GO Number","go.go")
        };
        operators=new String[]{"=","!=","<",">","<=",">=",Common.ILIKE,"NOT "+Common.ILIKE};
        booleans=new String[]{"and","or"};        
    }
    
    public String getDestination() {
        return "QueryPageServlet";
    }
    
    public List sendQuery(String query) {
        return Common.sendQuery(query);
    }
    
}
