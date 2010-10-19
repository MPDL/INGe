package de.mpg.escidoc.services.batchprocess.elements;

import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;

public abstract class Elements<ListElementType>
{
    private List<ListElementType> list;

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

    public abstract List<ListElementType> getList();

    public void setList(List<ListElementType> list)
    {
        this.list = list;
    }

    public abstract CoreServiceObjectType getObjectType();
}
