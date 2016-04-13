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

/**
 * TODO Withdraw Elements
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Withdraw extends Operation
{
    private static String TASKPARAM = "<param last-modification-date=\"XXX_DATE_XXX\"><comment>Batch withdraw</comment></param>";
    private static Logger logger = Logger.getLogger(Release.class);

    @Override
    public void execute(String[] args)
    {
        try
        {
            withdraw(elements);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public void withdraw(Elements elements) throws Exception
    {
        if (CoreServiceObjectType.ITEM.equals(elements.getObjectType()))
        {
            withdrawItems(elements);
        }
    }

    private void withdrawItems(Elements elements) throws Exception
    {
        ItemHandler itemHandler = ServiceLocator.getItemHandler(elements.getUserHandle());
        XmlTransformingBean xmlTransforming = new XmlTransformingBean();
        if (elements.getElements() != null)
        {
            for (ItemVO item : new ArrayList<ItemVO>(elements.getElements()))
            {
                String modificationDate = JiBXHelper.serializeDate(item.getModificationDate());
                // Withdraw
                itemHandler.withdraw(item.getVersion().getObjectId(), TASKPARAM.replace("XXX_DATE_XXX", modificationDate));
                
                logger.info("Withdrawing " + item.getVersion().getObjectId());
                
                // Write Report
                this.report.addEntry("Withdraw" + item.getVersion().getObjectId(), "Withdraw "
                        + item.getVersion().getObjectId(), ReportEntryStatusType.FINE);
            }
        }
    }
}