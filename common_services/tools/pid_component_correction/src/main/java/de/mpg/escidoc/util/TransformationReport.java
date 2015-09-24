package de.mpg.escidoc.util;

import java.util.ArrayList;
import java.util.Collection;

public class TransformationReport
{
    private int filesTotal = 0;
    private int filesNotItem = 0;
    private int filesNotReleased = 0;
    private int filesErrorOccured = 0;
    private int filesUpdateDone = 0;
    private int totalNumberOfPidsUpdated = 0;
    private long start = System.currentTimeMillis();
    private Collection<String> errorList = new  ArrayList<String>();
    
    public int getFilesTotal()
    {
        return filesTotal;
    }
    public void incrementFilesTotal()
    {
        this.filesTotal++;
    }
    public int getFilesNotItem()
    {
        return filesNotItem;
    }
    public void incrementFilesNotItem()
    {
        this.filesNotItem++;
    }
    public int getFilesNotReleased()
    {
        return filesNotReleased;
    }
    public void incrementFilesNotReleased()
    {
        this.filesNotReleased++;
    }
    public int getFilesErrorOccured()
    {
        return this.filesErrorOccured;
    }   
    public void incrementFilesErrorOccured()
    {
        this.filesErrorOccured++;
    }
    public int getFilesMigrationDone()
    {
        return this.filesUpdateDone;
    }   
    public void incrementFilesMigrationDone()
    {
        this.filesUpdateDone++;
    }
    public void setPidsUpdated(int n)
    {
        this.totalNumberOfPidsUpdated = n;        
    }
    public int  getTotalNumberOfPidsUpdated()
    {
        return this.totalNumberOfPidsUpdated;        
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
        this.filesTotal = 0;
        this.filesNotItem = 0;
        this.filesNotReleased = 0;
        this.filesErrorOccured = 0;
        this.filesUpdateDone = 0;
        this.totalNumberOfPidsUpdated = 0;
        this.start = System.currentTimeMillis();
        this.errorList.clear();      
    }
     
}
