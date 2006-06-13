/*
 * IdSearch.java
 *
 * Created on March 3, 2004, 12:49 PM
 */
package servlets.search;
/**
 *
 * @author  khoran
 */
import java.util.*;
import servlets.Common;
import servlets.querySets.*; 

/**
 * Takes a list of accession numbers. The default action is to use pattern
 * matching, so comparisons are case insensitive, and wildcards % and _ can
 * be used.  Since this is slow for large numbers of accessions, one can
 * specify they keyword 'exact' as the very first accession number.  This
 * will cause it to only find exact matches, and is case sensitive.
 */
public class IdSearch extends AbstractSearch
{
    
    /** Creates a new instance of IdSearch */
    public IdSearch() 
    {
    }
    void loadData()
    {
        List rs=null;
        
        seqId_query=QuerySetProvider.getSearchQuerySet().getIdSearchQuery(input,limit, db, keyType);
        rs=Common.sendQuery(seqId_query);
        
        //Set al=new TreeSet();
        List output=new LinkedList();
        String lastDb="";
        int c=0;
        for(Iterator i=rs.iterator();i.hasNext();c++)
        {
            ArrayList t=(ArrayList)i.next();
            if(!lastDb.equals(t.get(2))){
                lastDb=(String)t.get(2);
                addBookmark(lastDb, c);
                //dbStartPositions[Common.getDBid(lastDb)]=c;
            }
            output.add(t.get(0));            
            keysFound.add(((String)t.get(1)).toLowerCase());
        }        
        data=output;     
    }
    
    public List notFound()
    {//find the intersection of inputKeys and keysFound.
        List temp=new ArrayList();
        String el;
        for(Iterator i=input.iterator();i.hasNext();)
        {
            el=(String)i.next();
            if(!el.matches(".*%.*") && !el.equals("exact")) //don't add wildcard entries or keywords
                temp.add(el.toLowerCase());
        }        
        temp.removeAll(keysFound);
        return temp;        
    }
    public KeyType[] getSupportedKeyTypes()
    {
        return new KeyType[]{KeyType.SEQ,KeyType.MODEL,
                         KeyType.CORR};
    }  
}
