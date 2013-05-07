package de.mpg.escidoc.util;

import java.util.ArrayList;
import java.util.List;

public class MigrationStatistic
{
    private int filesMigratedTotal = 0;
    private int filesMigratedNotItemOrComponent = 0;
    private int filesMigratedNotReleased = 0;
    private int filesMigratedNotUpdated = 0;
    private int filesErrorOccured = 0;
    private int filesMigrationDone = 0;
    private int totalNumberOfPidsRequested = 0;
    private List<String> errorList = new ArrayList<String>();
    
    public int getFilesMigratedTotal()
    {
        return filesMigratedTotal;
    }
    public void incrementFilesMigratedTotal()
    {
        this.filesMigratedTotal++;
    }
    public int getFilesMigratedNotItemOrComponent()
    {
        return filesMigratedNotItemOrComponent;
    }
    public void incrementFilesMigratedNotItemOrComponent()
    {
        this.filesMigratedNotItemOrComponent++;
    }
    public int getFilesMigratedNotReleased()
    {
        return filesMigratedNotReleased;
    }
    public void incrementFilesMigratedNotReleased()
    {
        this.filesMigratedNotReleased++;
    }
    public int getFilesMigratedNotUpdated()
    {
        return filesMigratedNotUpdated;
    }
    public void incrementFilesMigratedNotUpdated()
    {
        this.filesMigratedNotUpdated++;
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
        return this.filesMigrationDone;
    }   
    public void incrementFilesMigrationDone()
    {
        this.filesMigrationDone++;
    }
    public void setPidsRequested(int n)
    {
        this.totalNumberOfPidsRequested = n;        
    }
    public int  getTotalNumberOfPidsRequested()
    {
        return this.totalNumberOfPidsRequested;        
    }
    public void addToErrorList(String escidocId)
    {
        this.errorList.add(escidocId);        
    }
    public List<String> getErrorList()
    {
        return this.errorList;  
    }
    public void clear()
    {
        this.filesMigratedTotal = 0;
        this.filesMigratedNotItemOrComponent = 0;
        this.filesMigratedNotReleased = 0;
        this.filesMigratedNotUpdated = 0;
        this.filesErrorOccured = 0;
        this.filesMigrationDone = 0;
        this.totalNumberOfPidsRequested = 0;
        this.errorList.clear();      
    }
     
}
