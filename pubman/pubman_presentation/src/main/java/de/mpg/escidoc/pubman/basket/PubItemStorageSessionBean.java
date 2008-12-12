package de.mpg.escidoc.pubman.basket;

import java.util.HashMap;
import java.util.Map;

import de.mpg.escidoc.pubman.appbase.FacesBean;
import de.mpg.escidoc.services.common.referenceobjects.ItemRO;

/**
 * Session Bean that stores the referneces of PubItems in a Map for the Basket session basket functionality.
 * TODO Description
 *
 * @author Markus Haarlaender (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class PubItemStorageSessionBean extends FacesBean
{
    public static String BEAN_NAME = "PubItemStorageSessionBean";
    
    /**
     * A map with the current reference objects of the basket's items. The key is the object id with version.
     */
    private Map<String, ItemRO> storedPubItems;
    
    public PubItemStorageSessionBean()
    {
        storedPubItems = new HashMap<String, ItemRO>();
    }


    public int getStoredPubItemsSize()
    {
        return storedPubItems.size();
    }
    
    
    /**
     * Sets the map with the current reference objects of the basket's items. The key is the object id with version.
    */
    public void setStoredPubItems(Map<String, ItemRO> storedPubItems)
    {
        this.storedPubItems = storedPubItems;
    }


    /**
     * Returns the map with the current reference objects of the basket's items. The key is the object id with version.
     */
    public Map<String, ItemRO> getStoredPubItems()
    {
        return storedPubItems;
    }

}
