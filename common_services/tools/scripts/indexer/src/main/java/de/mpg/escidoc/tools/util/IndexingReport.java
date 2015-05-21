package de.mpg.escidoc.tools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexingReport
{
    private AtomicInteger filesErrorOccured  = new AtomicInteger(0);
    private AtomicInteger filesIndexingDone  = new AtomicInteger(0);
    private AtomicInteger filesSkipped  = new AtomicInteger(0);
  
    private long start = System.currentTimeMillis();
    private Collection<String> errorList = Collections.synchronizedList(new ArrayList<String>());
    

    public int getFilesErrorOccured()
    {
        return this.filesErrorOccured.get();
    } 
    
    public void incrementFilesErrorOccured()
    {
        this.filesErrorOccured.incrementAndGet();
    }
    
    public int getFilesIndexingDone()
    {
        return this.filesIndexingDone.get();
    } 
    
    public void incrementFilesIndexingDone()
    {
        this.filesIndexingDone.incrementAndGet();
    }
    
    public int getFilesSkipped()
    {
        return this.filesSkipped.get();
    } 
    
    public void incrementFilesSkipped()
    {
        this.filesSkipped.incrementAndGet();
    }

    public long getTimeUsed()
    {
        return (System.currentTimeMillis() - this.start)/1000;
    }
    
    public void addToErrorList(String escidocId)
    {
        this.errorList.add(escidocId);        
    }
    
    public Collection<String> getErrorList()
    {
        return this.errorList;  
    }
    
    public void clear()
    {      
        this.filesErrorOccured.set(0);
        this.filesIndexingDone.set(0);;
        this.filesSkipped.set(0);;

        this.start = System.currentTimeMillis();
        this.errorList.clear();      
    }
    
    public String toString()
    {
    	long s = (System.currentTimeMillis() - start)/1000;
    	return 

    			"\nfilesErrorOccured\t<" + filesErrorOccured.get() + "> \n"
    			+ "filesSkipped\t\t<" + filesSkipped.get() + "> \n"
    			+ "filesIndexingDone\t<" + filesIndexingDone.get() + "> \n"
    			+ "time used\t\t<"	+  String.format("%d:%02d:%02d", s/3600, (s%3600)/60, (s%60)) + "> \n"
    			+ "errorList\t\t<" + Arrays.toString(errorList.toArray())  + "> \n";
    		
    }
     

}
