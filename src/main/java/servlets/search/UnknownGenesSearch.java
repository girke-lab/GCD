/*
 * UnknownGenesSearch.java
 *
 * Created on September 26, 2007, 10:48 AM
 *
 */

package servlets.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import servlets.Common;
import servlets.KeyTypeUser;
import servlets.querySets.QuerySetProvider;

/**
 *
 * @author khoran
 */
public class UnknownGenesSearch extends AbstractSearch
{
    
    private static final Logger log=Logger.getLogger(UnknownGenesSearch.class);
    
    /** Creates a new instance of UnknownGenesSearch */
    public UnknownGenesSearch()
    {
    }

    void loadData()
    {
        seqId_query=QuerySetProvider.getSearchQuerySet().getUnknownGenesSearchQuery(input, this.keyType);
        List rs=Common.sendQuery(seqId_query);

        List al=new ArrayList(rs.size());
        int c=0;

        for(Iterator i=rs.iterator();i.hasNext();c++)
            al.add(((ArrayList)i.next()).get(0));
        data=al; 
    }

    public KeyTypeUser.KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.ACC, KeyType.SEQ, KeyType.PSK, KeyType.MODEL};
    }
    
}
