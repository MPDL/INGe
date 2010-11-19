package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Submit extends Operation
{
    private static String TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch submit</comment></param>";

    @Override
    public void execute(String[] args)
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
        ItemHandler ih = ServiceLocator.getItemHandler(elements.getUserHandle());
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        List<ItemVO> submitted = new ArrayList<ItemVO>();
        if (elements.getElements() != null)
        {
            for (ItemVO ivo : new ArrayList<ItemVO>(elements.getElements()))
            {
                if (State.PENDING.equals(ivo.getVersion().getState()))
                {
                    String xml = xmlTransforming.transformToItem(ivo);
                    ih.submit(ivo.getVersion().getObjectId(), TASKPARAM.replace("XXX_DATE_XXX", JiBXHelper
                            .serializeDate(ivo.getModificationDate())));
                    xml = ih.retrieve(ivo.getVersion().getObjectId());
                    submitted.add(xmlTransforming.transformToItem(xml));
                    this.report.addEntry("Submit" + ivo.getVersion().getObjectId(), "Submit "
                            + ivo.getVersion().getObjectId(), ReportEntryStatusType.FINE);
                }
                else
                {
                    submitted.add(ivo);
                    this.report.addEntry("Submit" + ivo.getVersion().getObjectId(), "Item is not pending",
                            ReportEntryStatusType.PROBLEM);
                }
            }
        }
        elements.setElements(submitted);
    }
}
