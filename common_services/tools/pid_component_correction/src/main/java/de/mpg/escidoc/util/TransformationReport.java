package de.mpg.escidoc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class TransformationReport
{
    private AtomicInteger filesTotal = new AtomicInteger(0);
    private AtomicInteger filesNotItem = new AtomicInteger(0);
    private AtomicInteger filesNotReleased = new AtomicInteger(0);
    private AtomicInteger filesErrorOccured = new AtomicInteger(0);
    private AtomicInteger filesUpdateDone = new AtomicInteger(0);
    private AtomicInteger totalNumberOfPidsUpdated = new AtomicInteger(0);
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
    public int getFilesMigrationDone()
    {
        return filesUpdateDone.get();
    }   
    public void incrementFilesMigrationDone()
    {
        this.filesUpdateDone.getAndIncrement();
    }
    public void setPidsUpdated(int n)
    {
        totalNumberOfPidsUpdated.getAndSet(n);        
    }
    public int getTotalNumberOfPidsUpdated()
    {
        return totalNumberOfPidsUpdated.get();        
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
        filesUpdateDone.getAndSet(0);
        totalNumberOfPidsUpdated.getAndSet(0);
        start = System.currentTimeMillis();
        errorList.clear();      
    }
     
}
