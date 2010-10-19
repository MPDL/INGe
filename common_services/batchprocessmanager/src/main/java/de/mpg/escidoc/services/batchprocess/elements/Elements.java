package de.mpg.escidoc.services.batchprocess.elements;

import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;

public abstract class Elements<ListElementType>
{
    private List<ListElementType> list;

    public static Elements<?> getBatchProcessList(String name)
    {
        if ("LingLitAll".equals(name))
        {
            return new LingLitAllElements();
        }
        else
        {
            throw new RuntimeException(name + " is not a valid Element name");
        }
    }

    public abstract List<ListElementType> getList();

    public void setList(List<ListElementType> list)
    {
        this.list = list;
    }

    public abstract CoreServiceObjectType getObjectType();
}
