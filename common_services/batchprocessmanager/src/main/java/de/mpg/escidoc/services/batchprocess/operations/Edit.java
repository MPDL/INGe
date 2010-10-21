package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;
import de.mpg.escidoc.services.batchprocess.transformers.Transformer;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Edit extends BatchProcess
{
    private Transformer<?> transformer;
    private static final Logger logger = Logger.getLogger(Edit.class);

    public void run(String[] args)
    {
        try
        {
            transformer = Transformer.getTransformer(CommandHelper.getArgument("-t", args, true));
            elements.setElements(transformer.transform(elements.getElements()));
            update(elements);
            if (CoreServiceObjectStatus.SUBMITTED.equals(CommandHelper.getStatusEnumValue(CommandHelper.getArgument(
                    "-s", args, false)))
                    || CoreServiceObjectStatus.RELEASED.equals(CommandHelper.getStatusEnumValue(CommandHelper
                            .getArgument("-s", args, false))))
            {
                new Submit().run(args);
            }
            if (CoreServiceObjectStatus.RELEASED.equals(CommandHelper.getStatusEnumValue(CommandHelper.getArgument(
                    "-s", args, false))))
            {
                new Release().run(args);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error Batch Edit: ", e);
        }
    }

    public void update(Elements list) throws Exception
    {
        if (CoreServiceObjectType.ITEM.equals(list.getObjectType()))
        {
            updateItems(list);
        }
        else if (CoreServiceObjectType.CONTAINER.equals(list.getObjectType()))
        {
            //TODO
        }
    }

    private void updateItems(Elements list) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler();
        if (list.getElements() != null)
        {
            for (ItemVO ivo : new ArrayList<ItemVO>(list.getElements()))
            {
                // ih.update(arg0, arg1);
                System.out.println("Updating: " + ivo.getVersion().getObjectId());
            }
        }
    }
}
