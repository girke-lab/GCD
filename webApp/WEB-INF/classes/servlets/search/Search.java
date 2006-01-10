/*
 * Search.java
 *
 * Created on March 3, 2004, 12:42 PM
 */

/**
 *
 * @author  khoran
 */
package servlets.search;
import java.util.*;

/**
 * This is an interface for objects which search a database based
 * on a given condition, and return a list of id numbers.
 * This should usually be used with a fairly large limit, so that
 * most of the time all possible results are returned.  This is not
 * too expensive since only id numbers are transferred.
 * 
 * Most classes wishing to implement this interface should extend
 * {@link AbstractSearch }, and implement the loadData() method.
 */
public interface Search extends java.io.Serializable, servlets.KeyTypeUser
{
    
    /**
     * Initializes the search object with the input data, as well as
     * a list of databases (really genomes) that should be included in
     * the results.
     * @param data List of id, or keywords or anything else this
     * search object can use to query the database
     * @param limit limit for the query
     * @param dbID array contiain ids for genomes. Should use Common.arab
     * or Common.rice.
     */    
    void init(List data,int limit,int[] dbID);   
    
    
    /**
     * Perform the query and return the results.
     * @return returns list of found id numbers
     */    
    List getResults();   
       

    /**
     * Compares the list of input keys to the list of
     * keys found, and return a list of keys that were not found.
     * Returns an empty list if everything was found, or if this
     * operation does not make sense (e.g, for descritpions).
     * @return List of keys not found.
     */    
    List notFound();
    
    /**
     * Each search object should store the location of the first
     * element of each genome given in the list to init().
     * This method takes the genome id and returns an index
     * into the list returned by getResults().
     * @param i id number of genome.
     * @return index of first element of this genome in the results list.
     */    
    //int getDbStartPos(int i);
    
    /**
     * returns the number of genomes in this result set
     * @return number of genomes.
     */
    //int getDbCount();
    
    /**
     * Should return a Map containing the names of various
     * statistics thier values.  Currently used values are:
     * 'models' for model count
     * 'BLASTCLUST_35' or _50 or _70 for the number of this type of cluster
     * 'Domain Composition' for number of this type of cluster.
     * @return A Map containing all relevent statistics.
     */    
    Map getStats();    
    
    Collection<String> getBookmarkLabels();
    Collection<Integer> getBookmarkPositions();
    
}
