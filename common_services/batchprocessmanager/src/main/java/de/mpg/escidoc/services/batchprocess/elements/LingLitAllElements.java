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
    public LingLitAllElements(String[] args)
    {
        super(args);
    }

    private static final String LOCAL_TAG = "LingLit Import 2010-04-01 10:10";

    @Override
    public void retrieveElements()
    {
        try
        {
            System.out.println("RETRIEVING ELEMENTS FROM CORESERVICE...");
            ItemHandler ih = ServiceLocator.getItemHandler(AdminHelper.loginUser("bibliothek_mpi_eva", "bibliothek"));
            String seachResultXml = ih.retrieveItems(CoreServiceHelper.createBasicFilter(
                    "\"/properties/content-model-specific/local-tags/local-tag\"=\"" + LOCAL_TAG + "\"",
                    maximumNumberOfElements));
            elements.addAll(CoreServiceHelper.transformSearchResultXmlToListOfItemVO(seachResultXml));
            System.out.println("RETRIEVING ELEMENTS FROM CORESERVICE DONE!");
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
