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
    
    /**
     * The number that represents the difference between the real number of items in the basket and the number that is displayed. 
     * These might differ due to the problem that items can change their state and are then not retrieved by the filter any more.
     * In this case, this number is adapted to the number of items retrieved via the filter query.
     */
    private int diffDisplayNumber = 0;
    
    
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


    public void setDiffDisplayNumber(int diffDisplayNumber)
    {
        this.diffDisplayNumber = diffDisplayNumber;
    }


    public int getDiffDisplayNumber()
    {
        return diffDisplayNumber;
    }
    
    public int getDisplayNumber()
    {
        return getStoredPubItemsSize() - diffDisplayNumber;
    }


   

}
