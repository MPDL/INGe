package de.mpg.escidoc.util;


public class HandleUpdateStatistic extends Statistic
{
    private int handlesCreated = 0;
    private int handlesUpdated = 0;
    private int handlesNotFound = 0;
    private int handlesUpdateError = 0;

    public int getHandlesCreated()
    {
        return handlesCreated;
    }
    
    public void incrementHandlesCreated()
    {
        this.handlesCreated++;   
    }

    public int getHandlesUpdated()
    {
        return handlesUpdated;
    }

    public void incrementHandlesUpdated()
    {
        this.handlesUpdated++;
    }

    public int getHandlesNotFound()
    {
        return handlesNotFound;
    }

    public void incrementHandlesNotFound()
    {
        this.handlesNotFound++;
    }

    public int getHandlesUpdateError()
    {
        return handlesUpdateError;
    }

    public void incrementHandlesUpdateError()
    {
        this.handlesUpdateError++;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        
        b.append("handlesTotal " + new Integer(super.objectsTotal) + System.getProperty("line.separator"));
        b.append("handlesCreated " + new Integer(getHandlesCreated()) + System.getProperty("line.separator"));
        b.append("handlesUpdated " + new Integer(getHandlesUpdated()) + System.getProperty("line.separator"));
        b.append("handlesNotFound " + new Integer(getHandlesNotFound()) + System.getProperty("line.separator"));
        b.append("handlesUpdateError " + new Integer(getHandlesUpdateError()) + System.getProperty("line.separator"));
        
        return b.toString();  
    }

    public void clear()
    {
        this.objectsTotal = 0;
        this.handlesUpdated = 0;
        this.handlesNotFound = 0;
        this.handlesUpdateError = 0;
        
    }

    
}
