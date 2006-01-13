/*
 * TempFileCleaner.java
 *
 * Created on September 15, 2004, 10:10 AM
 */

package servlets;

/**
 *
 * @author  khoran
 */

import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;

/**
 * This class manages a separate thread used to delete old temp files.  It is 
 * no longer used.
 */
public class TempFileCleaner extends Thread
{
    private static TempFileCleaner singleCleaner=null;   
    private static Logger log=Logger.getLogger(TempFileCleaner.class);
    
    
//    public int timeToExpire=30000;   //3600000;
//    public int timeToSleep=10000;    //600000;

    /**
     * temp files must be this old before they can be deleted.
     */
    public int timeToExpire=3600000;
    /**
     * time thread should wait before checking temp files.
     */
    public int timeToSleep=4000000;
    
    private boolean stop=false;
    private File dirToClean;
    private FileFilter filter;
    
    /**
     * get an instance of this singleton class.
     * @return a TempFileCleaner
     */
    public static TempFileCleaner getInstance()
    {
        if(singleCleaner==null)
            singleCleaner=new TempFileCleaner();
        return singleCleaner;
    }
    
    private TempFileCleaner() 
    {
        super("cleaner "+(int)(Math.random()*100));
        filter=new OldCsvFilter(timeToExpire,"csv");
    }
    

    /**
     * sets name of directory to check for temp files in.
     * @param dirName full path to a local directory
     */
    public void setDirectory(String dirName)
    {
        dirToClean=new File(dirName);
    }
    /**
     * use a File to set temp file directory
     * @param f File correspoding to temp directory
     */
    public void setDirectory(File f)
    {
        dirToClean=f;
    }
    /**
     * supposedly stops the cleaner thread.
     */
    public void stopCleaner()
    {
        stop=true;
    }
    
    /**
     * starts cleaner thread.
     */
    public void run()
    { //only this method will execute in seperate thread.
        log.debug("cleaner starts");
        while(!stop)
        {            
            log.debug("checking for old files");
            File[] files=dirToClean.listFiles(filter);
            for(int i=0;i<files.length;i++)
            {
                //log.debug("checking file "+files[i].getName());
                if(System.currentTimeMillis()-files[i].lastModified() > timeToExpire) //file is 1 hour old
                { //remove file.                    
                    files[i].delete();
                    log.debug("found and removed "+files[i].getName());
                }
            }                        
            log.debug("going back to bed");
            try{
                this.sleep(timeToSleep);
            }catch(InterruptedException e){
                log.warn("someone woke up the cleaner: "+e.getMessage());
            }
        }
    }
    
    class OldCsvFilter implements FileFilter
    {
        int age;
        String extention;
        public OldCsvFilter(int a,String ext)
        {
            age=a;
            extention=ext;
        }
        public boolean accept(File pathname) 
        {            
            return pathname.getName().endsWith("."+extention) &&
                   System.currentTimeMillis()-pathname.lastModified() > age;
        }        
    }    
}

