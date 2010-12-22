package de.mpg.escidoc.services.batchprocess.elements;

import java.util.List;

import de.escidoc.www.services.om.ItemHandler;
import de.mpg.escidoc.services.batchprocess.BatchProcessReport.ReportEntryStatusType;
import de.mpg.escidoc.services.batchprocess.helper.CoreServiceHelper;
import de.mpg.escidoc.services.common.valueobjects.ItemVO;
import de.mpg.escidoc.services.common.valueobjects.ItemVO.State;
import de.mpg.escidoc.services.framework.AdminHelper;
import de.mpg.escidoc.services.framework.PropertyReader;
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
            setUserHandle(AdminHelper.loginUser(PropertyReader.getProperty("escidoc.user.name"), PropertyReader.getProperty("escidoc.user.password")));
        }
        catch (Exception e)
        {
            throw new RuntimeException("Login error. Please make sure the user credentials (escidoc.user.name, escidoc.user.password) are provided in your settings.xml file." + e);
        }
    }

    private static final String LOCAL_TAG = "test before live 4 2010-12-21 10:23";

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
            System.out.println(elements.size() + " items found");
            for (int i = elements.size() - 1; i >= 0; i--)
            {
            	if (elements.get(i).getVersion().getVersionNumber() == 1)
            	{
            		System.out.println(elements.get(i).getVersion().getObjectId() + " was not edited");
            	}
            	else if(elements.get(i).getVersion().getVersionNumber() != 1 && !elements.get(i).getVersion().getState().equals(State.RELEASED))
            	{
            		this.getTransformed().add(elements.get(i).getVersion().getObjectId());
            		System.out.println(elements.get(i).getVersion().getObjectId() + " was edited, but not released.");
            	}
            	else
            	{
            	    System.out.println(elements.get(i).getVersion().getObjectId() + " was edited, removed from the list");
            	    elements.remove(i);
            	}
			}
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
