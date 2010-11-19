package de.mpg.escidoc.services.batchprocess.elements;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;

public abstract class Elements<ListElementType> extends BatchProcess
{
    protected List<ListElementType> elements = new ArrayList<ListElementType>();
    protected int maximumNumberOfElements = 50;
    private String userHandle;

    public Elements(String[] args)
    {
        init(args);
        String max = CommandHelper.getArgument("-n", args, false);
        if (max != null)
        {
            this.maximumNumberOfElements = Integer.parseInt(max);
        }
        retrieveElements();
    }

    public static Elements<?> factory(String[] args)
    {
        try
        {
            Constructor c = Class.forName(CommandHelper.getArgument("-e", args, true)).getConstructor(
                    new Class[] { String[].class });
            return (Elements<?>)c.newInstance(new Object[] { args });
        }
        catch (Exception e)
        {
            throw new RuntimeException(CommandHelper.getArgument("-e", args, true) + " is not a valid Element name", e);
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

    public String getUserHandle()
    {
        return userHandle;
    }

    public void setUserHandle(String userHandle)
    {
        this.userHandle = userHandle;
    }

    public abstract void init(String[] args);

    public abstract void retrieveElements();

    public abstract CoreServiceObjectType getObjectType();
}
