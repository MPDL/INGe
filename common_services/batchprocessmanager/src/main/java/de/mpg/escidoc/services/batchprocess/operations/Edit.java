package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntry;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;
import de.mpg.escidoc.services.batchprocess.transformers.Transformer;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Edit extends Operation
{
    private Transformer<?> transformer;
    private static final Logger logger = Logger.getLogger(Edit.class);

    public void execute(String[] args)
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
                new Submit().execute(args);
            }
            if (CoreServiceObjectStatus.RELEASED.equals(CommandHelper.getStatusEnumValue(CommandHelper.getArgument(
                    "-s", args, false))))
            {
                new Release().execute(args);
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
            // TODO
        }
    }

    private void updateItems(Elements list) throws Exception
    {
        ItemHandler ih = ServiceLocator.getItemHandler();
        if (list.getElements() != null)
        {
            for (ItemVO ivo : new ArrayList<ItemVO>(list.getElements()))
            {
                this.report.addEntry("Update" + ivo.getVersion().getObjectId(), "Edit " + ivo.getVersion().getObjectId(),
                        ReportEntryStatusType.FINE);
            }
        }
    }
}
