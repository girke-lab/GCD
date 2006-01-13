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
import javax.servlet.http.*;
import javax.servlet.*;
import servlets.*;
import servlets.advancedSearch.queryTree.*;

/**
 * 
 */
public class CommonDatabase extends DefaultSearchableDatabase
{

    private static SearchTreeManager stm=new SearchTreeManager("CommonDatabase.properties");
    
    /** Creates a new instance of CommonDatabase 
        passes a db connection and a {@link SearchTreeManager} to 
     *  {@link DefaultSearchableDatabase}.
     */
    public CommonDatabase() 
    {
        super(DbConnectionManager.getConnection("common"),stm);
       
    }
//    void defineOptions2()
//    {   
//        Map cn=QuerySetProvider.getSearchableDatabaseQuerySet().getCommonColumnNames();
//        
//        rootTableName=(String)cn.get("rootTableName");
//        primaryKey=(String)cn.get("primaryKey");
//        defaultColumn=(String)cn.get("defaultColumn");
//        
//        //don't set column names here
//        fields=new Field[]{ new Field("Loci Id","",List.class), 
//                            new Field("Loci Description",""),
//                            new Field("Cluster Id","",List.class),
//                            new Field("Cluster Name",""),                            
//                            new Field("Cluster Size","",Integer.class),
//                            new Field("Clustering Method","",
//                                new String[]{"BLASTCLUST_35","BLASTCLUST_50","BLASTCLUST_70","Domain Composition"}),
//                            new Field("# arab keys in cluster","",Integer.class),
//                            new Field("# rice keys in cluster","",Integer.class),
//                            new Field("Database","",new String[]{"arab","rice"}),
//                            new Field("GO Number","",List.class)
//        };
//        int[] sortableFields=new int[]{0,1};
//        for(int i=0;i<sortableFields.length;i++)        
//            fields[sortableFields[i]].setSortable(true);
//        for(int i=0;i<fields.length;i++) //set column names from map
//            fields[i].dbName=(String)cn.get(fields[i].displayName);
//        
//        operators=new String[]{"=","!=","<",">","<=",">=",Common.ILIKE,"NOT "+Common.ILIKE};
//        booleans=new String[]{"and","or"};        
//    }
    
    void defineOptions()
    {   
        rootTableName="sequences";
        primaryKey="seq_id";
        defaultColumn="primary_key";
        
        fields=new Field[]{ new Field("Loci Id", "sequences.primary_key",List.class), 
                            new Field("Loci Description","sequences.description"),
                            new Field("Cluster Id","clusters_and_info.filename",List.class),
                            new Field("Cluster Name","clusters_and_info.name"),                            
                            new Field("Cluster Size","clusters_and_info.size",Integer.class),
                            new Field("Clustering Method","clusters_and_info.method",
                                new String[]{"BLASTCLUST_35","BLASTCLUST_50","BLASTCLUST_70","Domain Composition"}),
                            new Field("# arab keys in cluster","clusters_and_info.arab_count",Integer.class),
                            new Field("# rice keys in cluster","clusters_and_info.rice_count",Integer.class),
                            new Field("Database","sequences.Genome",new String[]{"arab","rice"}),
                            new Field("GO Number","go.go",List.class)
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
                
        if(getField(state.getSortField()).dbName.startsWith("cluster_info"))
        {
            fields.add("clusters_and_info.cluster_id");                                                            
        }
        else{
            fields.add("sequences.seq_id");
            fields.add("models.model_id"); 
            fields.add("sequences.genome");
            
            order=new Order(new DbField("sequences.genome, "+getField(state.getSortField()).dbName,String.class),"ASC");
            
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
        j.add("models");
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
