package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Submit extends BatchProcess
{
    @Override
    public void run(String[] args)
    {
        try
        {
            submitItems(elements);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void submitItems(Elements elements) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler();
        if (elements.getElements() != null)
        {
            for (ItemVO ivo : new ArrayList<ItemVO>(elements.getElements()))
            {
                // ih.update(arg0, arg1);
                System.out.println("Submitting: " + ivo.getVersion().getObjectId());
            }
        }
    }
}
