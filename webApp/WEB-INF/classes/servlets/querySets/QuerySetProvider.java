/*
 * QuerySetProvider.java
 *
 * Created on March 25, 2005, 3:13 PM
 */

package servlets.querySets;

/**
 *
 * @author khoran
 */

/**
 * This is a singlton  class that provides QuerySet objects.
 */

import org.apache.log4j.Logger;


public class QuerySetProvider
{
    private static QuerySetProvider singleProvider=null;
    private static Logger log=Logger.getLogger(QuerySetProvider.class);
    
    private SearchQuerySet searchSet=null;
    private DataViewQuerySet dataViewSet=null;
    private RecordQuerySet recordSet=null;
    private DatabaseQuerySet dbSet=null;
    
    public static void setDataViewQuerySet(DataViewQuerySet dvqs)
    {        
        getProvider().dataViewSet=dvqs;
    }
    public static DataViewQuerySet getDataViewQuerySet()
    {
        return getProvider().dataViewSet;
    }
    
    public static void setSearchQuerySet(SearchQuerySet sqs)
    {
        getProvider().searchSet=sqs;
    }
    public static SearchQuerySet getSearchQuerySet()
    {
        return getProvider().searchSet;
    }
    
    public static void setRecordQuerySet(RecordQuerySet rqs)
    {
        getProvider().recordSet=rqs;
    }
    public static RecordQuerySet getRecordQuerySet()
    {
        return getProvider().recordSet;
    }
    
    public static void setDatabaseQuerySet(DatabaseQuerySet dqs)
    {
        getProvider().dbSet=dqs;
    }
    public static DatabaseQuerySet getDatabaseQuerySet()
    {
        return getProvider().dbSet;
    }
    /** Creates a new instance of QuerySetProvider */
    private QuerySetProvider()
    {
    }

    private static QuerySetProvider getProvider()
    {
        if(singleProvider==null)
            singleProvider=new QuerySetProvider();
        return singleProvider;
    }
    
    
}
