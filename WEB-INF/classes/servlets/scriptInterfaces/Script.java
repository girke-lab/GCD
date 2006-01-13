/*
 * Script.java
 *
 * Created on August 26, 2004, 4:18 PM
 */

package servlets.scriptInterfaces;

/**
 *
 * @author  khoran
 */
public interface Script 
{
    /**
     * returns the content type of this script.
     */
    public String getContentType();
  
    /**
     * takes a list of seq_id's or cluster_id's, does a query
        to get desired data, and sends it to the script via post.
     */    
    public void run(java.io.OutputStream out, java.util.List ids);
    
}
