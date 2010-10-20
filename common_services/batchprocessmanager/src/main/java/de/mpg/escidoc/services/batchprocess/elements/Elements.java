package de.mpg.escidoc.services.batchprocess.elements;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;

public abstract class Elements<ListElementType>
{
    protected List<ListElementType> elements = new ArrayList<ListElementType>();
    
    public Elements()
    {
        retrieveElements();
    }

    public static Elements<?> getBatchProcessList(String name)
    {
        try
        {
            return (Elements<?>)Class.forName(name).newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException(name + " is not a valid Element name", e);
        }
    }

    public List<ListElementType> getElements()
    {
        return elements;
    }

    public void setElements(List<ListElementType> elements)
    {
        this.elements = elements;
    }

    public abstract void retrieveElements();

    public abstract CoreServiceObjectType getObjectType();
}
