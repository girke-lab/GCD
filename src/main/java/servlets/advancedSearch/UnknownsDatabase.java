/*
 * UnknownsDatabase.java
 *
 * Created on September 7, 2004, 3:58 PM
 */

package servlets.advancedSearch;
//TODO make this go away
/**
 *
 * @author  khoran
 */

import java.util.*;
import servlets.Common;
import servlets.DbConnection;
import servlets.DbConnectionManager;
import org.apache.log4j.Logger;
import javax.servlet.http.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import java.io.*;
import servlets.advancedSearch.fields.*;
import servlets.advancedSearch.fields.Field;
import servlets.dataViews.*;
import servlets.querySets.*;

public class UnknownsDatabase extends DefaultSearchableDatabase
{

    private static SearchTreeManager stm=new SearchTreeManager("UnknownDatabase.properties");
    
    /** Creates a new instance of UnknownsDatabase */
    public UnknownsDatabase() 
    {
        super(DbConnectionManager.getConnection("khoran"),stm);
    }
    void defineOptions()
    {                
        rootTableName="old_unknowns.unknowns";
        primaryKey="unknown_id";
        defaultColumn="At_key";
                
        String[] printNames=new String[]{
            "At Key" ,
            "Description" ,
            "Unknown Method TIGR" ,
            "Unknown Method SWP_BLAST" ,
            "Unknown Method GO: MFU OR CCU OR BPU" ,
            "Unknown Method GO: MFU" ,
            "Unknown Method InterPro" ,
            "Unknown Method Pfam" ,
            "Citosky Small List" ,
            "SALK tDNA-Insertion" ,
            "EST avail" ,
            "avail" ,
            "flcDNA TIGR (XML) avail" ,
            "Nottingham Chips: 3x >90" ,
            "Rice Orth E-value" ,
            "Gene Family Size 35%_50%_70% ident" ,
            "Pet Gene from" ,
            "Targeting Ipsort" ,
            "Targeting Predotar" ,
            "Targeting Targetp" ,
            "Membr dom Hmmtop" ,
            "Membr dom Thumbup" ,
            "Membr dom TMHMM" ,
            "Focus list of grant" ,
            "Selected by" ,
            "Multiple selects" ,
            "Occurrence in treaments",        
            "HumanRatMouse Orth E-value" ,
            "S. cerevisiae E-value" ,                    
            "Treatments"
        };
        String[] dbNames=new String[]{
            rootTableName+".At_Key",
            rootTableName+".Description",
            rootTableName+".Unknown_Method_TIGR",
            rootTableName+".Unknown_Method_SWP_BLAST",
            rootTableName+".Unknown_Method_GO_MFU_OR_CCU_OR_BPU",
            rootTableName+".Unknown_Method_GO_MFU",
            rootTableName+".Unknown_Method_InterPro",
            rootTableName+".Unknown_Method_Pfam",
            rootTableName+".Citosky_Small_List",
            rootTableName+".SALK_tDNA_Insertion",
            rootTableName+".EST_avail",
            rootTableName+".avail",
            rootTableName+".flcDNA_TIGR_XML_avail",
            rootTableName+".Nottingham_Chips_3x_90",
            rootTableName+".Rice_Orth_Evalue",            
            rootTableName+".Gene_Family_Size_35_50_70_perc_ident",
            rootTableName+".Pet_Gene_from",
            rootTableName+".Targeting_Ipsort",
            rootTableName+".Targeting_Predotar",
            rootTableName+".Targeting_Targetp",
            rootTableName+".Membr_dom_Hmmtop",
            rootTableName+".Membr_dom_Thumbup",
            rootTableName+".Membr_dom_TMHMM",
            rootTableName+".Focus_list_of_grant",
            rootTableName+".Selected_by",
            rootTableName+".Multiple_selects",
            rootTableName+".Occurrence_in_treaments",
            rootTableName+".HumanRatMouse_Orth_Evalue",
            rootTableName+".S_cerevisiae_Evalue",
            "old_unknowns.treats.treat"
        };
        fields=new Field[dbNames.length];
        for(int i=0;i<fields.length;i++)
        {
            fields[i]=new StringField(printNames[i],dbNames[i]);
            if(i!=fields.length-1) //last field is not sortable
                fields[i].setSortable(true);
        }
        
        booleans=new String[]{"and","or"};        
    }
     protected ServletRequest getNewRequest(SearchState state,HttpServletRequest request,List results)
     {
         NewParametersHttpRequestWrapper mRequest=new NewParametersHttpRequestWrapper(
                    (HttpServletRequest)request,new HashMap(),false,"POST");
        
        mRequest.getParameterMap().put("searchType","seq_id");
        mRequest.getParameterMap().put("limit", state.getLimit());
        mRequest.getParameterMap().put("sortCol",getFields()[state.getSortField()].dbName);         
                
        mRequest.getParameterMap().put("displayType","unknownsView");
        
        StringBuffer inputStr=new StringBuffer();      
        for(Iterator i=results.iterator();i.hasNext();)
            inputStr.append(((List)i.next()).get(0)+" ");       

        mRequest.getParameterMap().put("inputKey",inputStr.toString());
        return mRequest;
     }
}
