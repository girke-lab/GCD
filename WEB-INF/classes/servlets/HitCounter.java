/*
 * HitCounter.java
 *
 * Created on June 14, 2007, 2:14 PM
 *
 */

package servlets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 *  Keeps a count of how many pages have been 
 * accessed.  Methods in this class may be called from 
 * several different threads simultaniously.
 * @author khoran
 */
public class HitCounter
{
    
    private static final Logger log=Logger.getLogger(HitCounter.class);
    
    private static final String countFile="/home/khoran/databaseWeb/data/hit_counter";
    
    private volatile static int count=loadCount();
    private static Object countLock=new Object();
    
    private static long lastStore=0;
    private static final Long storeTimeInterval=60000L; // 1 minunte
    
    /** Creates a new instance of HitCounter */
    private HitCounter()
    { }

    /** try to store count before we exit
     */
    protected void finalize() 
    {
        try{
            storeCount();
        }catch(IOException e){
            log.error("could not store hit count to file "+countFile+": "+e);
        }
    }

    
    public static void increment()
    {
        synchronized(countLock){
            count++;
            if(System.currentTimeMillis() - lastStore > storeTimeInterval)
            {
                try{
                    storeCount();
                }catch(IOException e){
                    log.error("could not store hit count to file "+countFile+": "+e);
                }
                // reset time even if an error occured to prevent
                // us from trying to write on every increment.
                lastStore=System.currentTimeMillis();
            }
        }
    }
    public static int getHitCount()
    {
        return count;
    }
    
    
    private static void  storeCount() throws IOException
    {
        BufferedWriter out=new BufferedWriter(new FileWriter(countFile));
        
        out.append(""+getHitCount());
        
        out.close();
    }
    
    private static int loadCount() 
    {
        try{
            BufferedReader in =new BufferedReader(new FileReader(countFile));
            int c=Integer.parseInt(in.readLine());
            return c;
        }catch(IOException e){
            log.error("could not read hit count from file "+countFile,e);
            return 0;
        }
    }
}
