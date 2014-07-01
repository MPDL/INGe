package de.mpg.escidoc.util;

public class Statistic
{
    protected int objectsTotal = 0;
    
    public void incrementTotal()
    {
        this.objectsTotal++;
    }
    
    public void setObjectsTotal(int total)
    {
    	this.objectsTotal = total;
    }
    
    public int getObjectsTotal()
    {
    	return this.objectsTotal;
    }
}
