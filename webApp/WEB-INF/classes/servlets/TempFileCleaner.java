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

public class TempFileCleaner extends Thread
{
    private static TempFileCleaner singleCleaner=null;
    private Collection fileList=null;
    private static Logger log=Logger.getLogger(TempFileCleaner.class);
    //private static CleanerThread cleaner=new CleanerThread();
    
//    public int timeToExpire=30000;   //3600000;
//    public int timeToSleep=10000;    //600000;

    public int timeToExpire=3600000;
    public int timeToSleep=4000000;
    
    private boolean stop=false;
    
    public static TempFileCleaner getInstance()
    {
        if(singleCleaner==null)
            singleCleaner=new TempFileCleaner();
        return singleCleaner;
    }
    
    private TempFileCleaner() 
    {
        super("cleaner");
    }
    
    public void addFile(File f)
    {
        if(fileList==null)
        {            
            fileList=new LinkedList();
            startCleaner();
        }
        else if(fileList.size()==0)
            startCleaner();

        synchronized (fileList)
        {
            log.debug("added file "+f.getName()+" to fileList");
            fileList.add(f);
        }        
    }
    public void stopCleaner()
    {
        stop=true;
    }
    public void startCleaner()
    {
        stop=false;
        log.debug("values of isInterupted, isAlive are: "+this.isInterrupted()+","+this.isAlive());
        if(!this.isAlive()){
            log.debug("restarting cleaner");
            start(); //restart thread if it has died
        }
    }
    
    public void run()
    { //only this method will execute in seperate thread.
        log.debug("cleaner starts");
        while(!stop)
        {            
            log.debug("waiting for fileList lock...");
            synchronized (fileList)
            {
                log.debug("got lock, checking for old files");
                for(Iterator i=fileList.iterator();i.hasNext();)
                {
                        File f=(File)i.next();
                        //log.debug("age of "+f.getName()+" is "+(System.currentTimeMillis()-f.lastModified()));
                        if(System.currentTimeMillis()-f.lastModified() > timeToExpire) //file is 1 hour old
                        { //remove file.
                            i.remove();
                            f.delete();
                            log.debug("found and removed "+f.getName());
                        }
                }                
//                if(fileList.size()==0)
//                    stop=true;
            }            
            log.debug("going back to bed");
            try{
                this.sleep(timeToSleep);
            }catch(InterruptedException e){
                log.warn("someone woke up the cleaner: "+e.getMessage());
            }
        }
    }
    
}

