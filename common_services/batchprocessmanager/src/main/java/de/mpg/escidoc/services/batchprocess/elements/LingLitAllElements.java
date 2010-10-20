package de.mpg.escidoc.services.batchprocess.elements;

import java.util.List;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcess.CoreServiceObjectType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.ServiceLocator;

public class LingLitAllElements extends Elements<ItemVO>
{
    @Override
    public void retrieveElements()
    {
        try
        {
            System.out.println(ServiceLocator.getFrameworkUrl());
            ItemHandler ih = ServiceLocator.getItemHandler(AdminHelper.loginUser("bibliothek_mpi_eva", "bibliothek"));
            String seachResultXml = ih.retrieveItems(CoreServiceHelper.createBasicFilter("", 50));
            elements.addAll(CoreServiceHelper.transformSearchResultXmlToListOfItemVO(seachResultXml));
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
