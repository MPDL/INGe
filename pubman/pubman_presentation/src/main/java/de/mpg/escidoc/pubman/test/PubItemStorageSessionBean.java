package de.mpg.escidoc.pubman.test;

import java.util.ArrayList;
import java.util.List;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;

public class PubItemStorageSessionBean extends FacesBean
{
    public static String BEAN_NAME = "PubItemStorageSessionBean";
    
    private List<PubItemVOPresentation> storedPubItems;
    
    public PubItemStorageSessionBean()
    {
        storedPubItems = new ArrayList<PubItemVOPresentation>();
    }

    public void setStoredPubItems(List<PubItemVOPresentation> storedPubItems)
    {
        this.storedPubItems = storedPubItems;
    }

    public List<PubItemVOPresentation> getStoredPubItems()
    {
        return storedPubItems;
    }
    
    public int getStoredPubItemsSize()
    {
        return storedPubItems.size();
    }
    
    public List<PubItemVOPresentation> getSelectedPubItems()
    {
        List<PubItemVOPresentation> returnList = new ArrayList<PubItemVOPresentation>();
        for (PubItemVOPresentation pubItem : getStoredPubItems())
        {
            if(pubItem.getSelected())
            {
                returnList.add(pubItem);
            }
        }
        return returnList;
    }
    
    
}
