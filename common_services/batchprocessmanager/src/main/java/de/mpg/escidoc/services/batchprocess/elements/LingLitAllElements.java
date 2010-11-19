package de.mpg.escidoc.services.batchprocess.elements;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.rpc.ServiceException;

import org.apache.commons.httpclient.HttpException;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class LingLitAllElements extends Elements<ItemVO>
{
    public LingLitAllElements(String[] args)
    {
        super(args);
    }

    @Override
    public void init(String[] args)
    {
        try
        {
            setUserHandle(AdminHelper.loginUser("bibliothek_mpi_eva", "bibliothek"));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error login:" + e);
        }
    }

    private static final String LOCAL_TAG = "LingLit Import 2010-04-01 10:10";

    @Override
    public void retrieveElements()
    {
        try
        {
            ItemHandler ih = ServiceLocator.getItemHandler(this.getUserHandle());
            String seachResultXml = ih.retrieveItems(CoreServiceHelper.createBasicFilter(
                    "\"/properties/content-model-specific/local-tags/local-tag\"=\"" + LOCAL_TAG + "\"",
                    maximumNumberOfElements));
            elements.addAll(CoreServiceHelper.transformSearchResultXmlToListOfItemVO(seachResultXml));
            report.addEntry("retrieveElements", "Get Data", ReportEntryStatusType.FINE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error initializing LingLitAllElements.java: ", e);
        }
    }

    public List<ItemVO> getElements()
    {
        return elements;
    }

    @Override
    public CoreServiceObjectType getObjectType()
    {
        return CoreServiceObjectType.ITEM;
    }
}
