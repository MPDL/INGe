package de.mpg.escidoc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformationReport
{
	// total number of files met during recursiv run
    private AtomicInteger filesTotal = new AtomicInteger(0);
    
    // number of files met of type other than ITEM (may be e.g. organizational unit...)
    private AtomicInteger filesNotItem = new AtomicInteger(0);
    
    // number of files met of version status other than RELEASED (may be e.g. PENDING...)
    private AtomicInteger filesNotReleased = new AtomicInteger(0);
    
    private AtomicInteger filesErrorOccured = new AtomicInteger(0);
    
    // number of component pids where an update has done successfully
    private AtomicInteger componentsUpdateDone = new AtomicInteger(0);
    
    private long start = System.currentTimeMillis();
    private Collection<String> errorList = new  ArrayList<String>();
    
    public int getFilesTotal()
    {
        return filesTotal.get();
    }
    public void incrementFilesTotal()
    {
        filesTotal.getAndIncrement();
    }
    public int getFilesNotItem()
    {
        return filesNotItem.get();
    }
    public void incrementFilesNotItem()
    {
        filesNotItem.getAndIncrement();
    }
    public int getFilesNotReleased()
    {
        return filesNotReleased.get();
    }
    public void incrementFilesNotReleased()
    {
        filesNotReleased.getAndIncrement();
    }
    public int getFilesErrorOccured()
    {
        return filesErrorOccured.get();
    }   
    public void incrementFilesErrorOccured()
    {
        this.filesErrorOccured.getAndIncrement();
    }
    public int getComponentsUpdateDone()
    {
        return componentsUpdateDone.get();
    }   
    public void incrementComponentsUpdateDone()
    {
        this.componentsUpdateDone.getAndIncrement();
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
        filesTotal.getAndSet(0);
        filesNotItem.getAndSet(0);
        filesNotReleased.getAndSet(0);
        filesErrorOccured.getAndSet(0);
        componentsUpdateDone.getAndSet(0);
        start = System.currentTimeMillis();
        errorList.clear();      
    }
     
}
