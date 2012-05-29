package de.mpg.escidoc.services.batchprocess.operations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.elements.Elements;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.PidTaskParamVO;
import de.mpg.escidoc.services.common.valueobjects.ResultVO;
import de.mpg.escidoc.services.common.xmltransforming.JiBXHelper;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class Release extends Operation
{
    private static String TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch release</comment></param>";
    private static Logger logger = Logger.getLogger(Release.class);

    @Override
    public void execute(String[] args)
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
        ItemHandler ih = ServiceLocator.getItemHandler(elements.getUserHandle());
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        if (elements.getElements() != null)
        {
            for (ItemVO ivo : new ArrayList<ItemVO>(elements.getElements()))
            {
                String modificationDate = JiBXHelper.serializeDate(ivo.getModificationDate());
                PidTaskParamVO paramAssignation = new PidTaskParamVO(ivo.getVersion().getModificationDate(),
                        ServiceLocator.getFrameworkUrl() + "/ir/item/" + ivo.getVersion().getObjectId());
                String paramXml = xmlTransforming.transformToPidTaskParam(paramAssignation);
                if (ivo.getVersion().getState() == ItemVO.State.SUBMITTED)
                {
                    try
                    {
                        // Assign objectPID
                        String result = ih.assignObjectPid(ivo.getVersion().getObjectId(), paramXml);
                        modificationDate = JiBXHelper.serializeDate(xmlTransforming.transformToResult(result).getLastModificationDate());
                        paramAssignation = new PidTaskParamVO(xmlTransforming.transformToResult(result).getLastModificationDate(),
                                ServiceLocator.getFrameworkUrl() + "/ir/item/" + ivo.getVersion().getObjectId());
                        paramXml = xmlTransforming.transformToPidTaskParam(paramAssignation);
                    }
                    catch (Exception e)
                    {
                        logger.warn(ivo.getVersion().getObjectId() + " has already an object PID");
                    }
                    // Assign Version PID
                    String resp = ih.assignVersionPid(ivo.getVersion().getObjectId(), paramXml);
                    ResultVO rVO = xmlTransforming.transformToResult(resp);
                    modificationDate = JiBXHelper.serializeDate(rVO.getLastModificationDate());
                    // Release
                    ih.release(ivo.getVersion().getObjectId(), TASKPARAM.replace("XXX_DATE_XXX", modificationDate));
                    
                    logger.info("Releasing " + ivo.getVersion().getObjectId());
                    
                    // Write Report
                    this.report.addEntry("Release" + ivo.getVersion().getObjectId(), "Release "
                            + ivo.getVersion().getObjectId(), ReportEntryStatusType.FINE);
                }
                else
                {
                    logger.warn("Item " + ivo.getVersion().getObjectId() + " is " + ivo.getVersion().getState());
                }
            }
        }
    }
}
