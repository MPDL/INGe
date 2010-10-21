package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Release extends BatchProcess
{
    @Override
    public void run(String[] args)
    {
        try
        {
            release(elements);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void release(Elements elements) throws Exception
    {
        if (CoreServiceObjectType.ITEM.equals(elements.getObjectType()))
        {
            releaseItems(elements);
        }
        else if (CoreServiceObjectType.CONTAINER.equals(elements.getObjectType()))
        {
            // TODO
        }
    }

    private void releaseItems(Elements elements) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler();
        if (elements.getElements() != null)
        {
            for (ItemVO ivo : new ArrayList<ItemVO>(elements.getElements()))
            {
                // ih.update(arg0, arg1);
                System.out.println("Releasing: " + ivo.getVersion().getObjectId());
            }
        }
    }
}
