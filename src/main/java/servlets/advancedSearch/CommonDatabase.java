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
import servlets.advancedSearch.fields.*;
import servlets.advancedSearch.fields.Field;
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
    
    void defineOptions()
    {   
        rootTableName="sequences";
        primaryKey="seq_id";
        defaultColumn="primary_key";
                
        
        fields=new Field[]{ 
            new StringField("Loci Id", "sequences.primary_key",true).setSortable(true), 
            new StringField("Loci Description","sequences.description").setSortable(true),
            new StringField("Cluster Id","clusters_and_info.filename",true),
            new StringField("Cluster Name","clusters_and_info.name"),                            
            new IntField("Cluster Size","clusters_and_info.size"),
            new ListField("Clustering Method","clusters_and_info.method",
                new String[]{"BLASTCLUST_35","BLASTCLUST_50","BLASTCLUST_70","Domain Composition"}),
            new IntField("# arab keys in cluster","clusters_and_info.arab_count"),
            new IntField("# rice keys in cluster","clusters_and_info.rice_count"),
            new ListField("Database","sequences.Genome",new String[]{"arab","rice"}),
            new StringField("GO Number","go.go",true)
        };
        
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
