package de.mpg.escidoc.util;

public class MigrationStatistic
{
    private int filesMigratedTotal = 0;
    private int filesMigratedNotItemOrComponent = 0;
    private int filesMigratedNotReleased = 0;
    private int filesMigratedNotUpdated = 0;
    
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
}
