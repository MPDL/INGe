package de.mpg.escidoc.util;

public class LocatorCheckStatistic extends Statistic
{
    private int locatorsResolved = 0;
    private int locatorsNotResolved = 0;

    public int getLocatorsTotal()
    {
        return objectsTotal;
    }
    
    public void setLocatorsTotal(int size)
    {
        this.objectsTotal = size;
    }

    public int getLocatorsResolved()
    {
        return locatorsResolved;
    }

    public void incrementLocatorsResolved()
    {
        this.locatorsResolved++;
    }

    public int getLocatorsNotResolved()
    {
        return locatorsNotResolved;
    }

    public void incrementLocatorsNotResolved()
    {
        this.locatorsNotResolved++;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        
        b.append("locatorsTotal " + new Integer(getLocatorsTotal()) + System.getProperty("line.separator"));
        b.append("locatorsResolved " + new Integer(getLocatorsResolved()) + System.getProperty("line.separator"));
        b.append("locatorsNotResolved " + new Integer(getLocatorsNotResolved()) + System.getProperty("line.separator"));
        
        return b.toString();  
    }

    public void clear()
    {
        this.objectsTotal = 0;
        this.locatorsResolved = 0;
        this.locatorsNotResolved = 0;      
    }

   
    
}
