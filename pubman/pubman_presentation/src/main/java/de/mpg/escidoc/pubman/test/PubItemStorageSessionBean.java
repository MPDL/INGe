package de.mpg.escidoc.pubman.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.pubman.util.PubItemVOPresentation;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;

public class PubItemStorageSessionBean extends FacesBean
{
    public static String BEAN_NAME = "PubItemStorageSessionBean";
    
    private Map<String, ItemRO> storedPubItems;
    
    public PubItemStorageSessionBean()
    {
        storedPubItems = new HashMap<String, ItemRO>();
    }


    public int getStoredPubItemsSize()
    {
        return storedPubItems.size();
    }
    
    


    public void setStoredPubItems(Map<String, ItemRO> storedPubItems)
    {
        this.storedPubItems = storedPubItems;
    }



    public Map<String, ItemRO> getStoredPubItems()
    {
        return storedPubItems;
    }

}
