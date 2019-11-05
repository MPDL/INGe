package de.mpg.mpdl.inge.pubman.web.batch;

import java.util.HashMap;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.pubman.web.util.FacesBean;

/**
 * SessionBean for batch operations on PubItems
 * 
 * @author walter
 *
 */
@ManagedBean(name = "PubItemBatchSessionBean")
@SessionScoped
@SuppressWarnings("serial")
public class PubItemBatchSessionBean extends FacesBean {
  private Map<String, ItemVersionRO> storedPubItems;

  /**
   * The number that represents the difference between the real number of items in the batch
   * environment and the number that is displayed. These might differ due to the problem that items
   * can change their state and are then not retrieved by the filter any more. In this case, this
   * number is adapted to the number of items retrieved via the filter query.
   */
  private int diffDisplayNumber = 0;

  public PubItemBatchSessionBean() {
    this.storedPubItems = new HashMap<String, ItemVersionRO>();
  }

  public int getBatchPubItemsSize() {
    return this.storedPubItems.size();
  }

  /**
   * Sets the map with the current reference objects of the basket's items. The key is the object id
   * with version.
   */
  public void setStoredPubItems(Map<String, ItemVersionRO> storedPubItems) {
    this.storedPubItems = storedPubItems;
  }

  /**
   * Returns the map with the current reference objects of the basket's items. The key is the object
   * id with version.
   */
  public Map<String, ItemVersionRO> getStoredPubItems() {
    return this.storedPubItems;
  }

  public void setDiffDisplayNumber(int diffDisplayNumber) {
    this.diffDisplayNumber = diffDisplayNumber;
  }

  public int getDiffDisplayNumber() {
    return this.diffDisplayNumber;
  }

  public int getDisplayNumber() {
    return this.getBatchPubItemsSize() - this.diffDisplayNumber;
  }
}
