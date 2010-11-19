package de.mpg.escidoc.services.batchprocess.elements;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CommandHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.xmltransforming.XmlTransformingBean;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class SingleItem extends Elements<ItemVO>
{
    private String escidocId = null;
    private static final Logger logger = Logger.getLogger(BatchProcess.class);

    public SingleItem(String[] args)
    {
        super(args);
    }

    @Override
    public void init(String[] args)
    {
        escidocId = CommandHelper.getArgument("-id", args, true);
        try
        {
            setUserHandle(AdminHelper.loginUser("bibliothek_mpi_eva", "bibliothek"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error login:" + e);
        }
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }

    @Override
    public void retrieveElements()
    {
        try
        {
            ItemHandler ih = ServiceLocator.getItemHandler(this.getUserHandle());
            logger.info("Retrieving item " + escidocId + " from " + ServiceLocator.getFrameworkUrl());
            String itemXml = ih.retrieve(escidocId);
            XmlTransformingBean xmlTransforming = new XmlTransformingBean();
            ItemVO item = xmlTransforming.transformToItem(itemXml);
            List<ItemVO> list = new ArrayList<ItemVO>();
            list.add(item);
            elements.addAll(list);
            report.addEntry("retrieveElements", "Get Data", ReportEntryStatusType.FINE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing SingleItem.java: ", e);
        }
    }
}
