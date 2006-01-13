/*
 * CommonDatabaseV2.java
 *
 * Created on March 30, 2005, 9:12 AM
 */

package servlets.advancedSearch;

/**
 *
 * @author khoran
 */

import java.util.*;
import servlets.Common;
import javax.servlet.http.*;
import javax.servlet.*;
import servlets.*;
import servlets.advancedSearch.queryTree.*;

public class CommonDatabaseV2 extends DefaultSearchableDatabase
{
    
    
    private static SearchTreeManager stm=new SearchTreeManager("CommonDatabaseV2.properties");
    
    /** Creates a new instance of CommonDatabase 
        passes a db connection and a {@link SearchTreeManager} to 
     *  {@link DefaultSearchableDatabase}.
     */
    public CommonDatabaseV2() 
    {
        super(DbConnectionManager.getConnection("khoran"),stm);
       
    }
    
    void defineOptions()
    {   
        rootTableName="general.accessions";
        primaryKey="accession_id";
        defaultColumn="accession";
        
        fields=new Field[]{ new Field("Loci Id", "general.accessions.accession",List.class), 
                            new Field("Loci Description","general.accessions.description"),
                            new Field("Cluster Id","common.sequence_clusters.key",List.class),
                            new Field("Cluster Name","common.sequence_clusters.name"),                            
                            new Field("Cluster Size","common.sequence_clusters.size",Integer.class),
                            new Field("Clustering Method","common.sequence_clusters.method",
                                new String[]{"BLASTCLUST_35","BLASTCLUST_50","BLASTCLUST_70","Domain Composition"}),
                            new Field("# arab keys in cluster","common.sequence_clusters.arab_count",Integer.class),
                            new Field("# rice keys in cluster","common.sequence_clusters.rice_count",Integer.class),
                            new Field("Database","general.genome_databases_view.db_name",new String[]{"arab","rice"}),
                            new Field("GO Number","general.go_view.go_number",List.class)
        };
        int[] sortableFields=new int[]{0,1};
        for(int i=0;i<sortableFields.length;i++)
            fields[sortableFields[i]].setSortable(true);
        operators=new String[]{"=","!=","<",">","<=",">=",Common.ILIKE,"NOT "+Common.ILIKE};
        booleans=new String[]{"and","or"};        
    }
  
    
    public Query buildQueryTree(SearchState state)
    {
        Query q=super.buildQueryTree(state);
//        if(true) 
//            return q;
        //modify select list and order by.
        List fields=new LinkedList();
        Order order=q.getOrder();
                
        if(getField(state.getSortField()).dbName.startsWith("general.clusters_and_info"))
        {
            fields.add("general.clusters_and_info.cluster_id");                                                            
        }
        else{
            fields.add("general.accessions.accession_id");
            fields.add("common.sequence_models.model_accession_id"); 
            fields.add("general.genome_databases_view.db_name");
            
            order=new Order(new DbField("genome_databases_view.db_name, "+getField(state.getSortField()).dbName,String.class),"ASC");
            
//            fieldList=" sequences.seq_id, models.model_id, "+fields[state.getSortField()].dbName+",sequences.genome ";
//            order=" sequences.genome, "+fields[state.getSortField()].dbName; 
        }
        fields.add(getField(state.getSortField()).dbName);
        q.setFields(fields);
        q.setOrder(order);
        return q;
        
    }
    
    protected List additionalJoins()
    {
        List j=new ArrayList(1);
        j.add("common.sequence_models");
        j.add("general.genome_databases_view");
        return j;
    }
    
    protected ServletRequest getNewRequest(SearchState state,HttpServletRequest request,List results)
    { //this can be overridden by sub classes to send different parameters
        NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_model");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName.replaceAll("sequences","sequence_view"));         
        mRequest.getParameterMap().put("origin_page","advancedSearch.jsp");
                
        if(getFields()[state.getSortField()].dbName.startsWith("cluster_info"))
            mRequest.getParameterMap().put("displayType","clusterView");
        else
            mRequest.getParameterMap().put("displayType","seqView");
        
        StringBuffer inputStr=new StringBuffer();      
        List row;
        for(Iterator i=results.iterator();i.hasNext();)
        {
            row=(List)i.next();
            inputStr.append(row.get(0)+" "+row.get(1)+" ");
            //inputStr.append(row.get(0)+" ");
        }            

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        
        return mRequest;
    }
    
}
