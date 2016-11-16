package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;


/**
 * Interface for persisting and retrieving publication items
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface ItemInterface {
  /**
   * 
   * @param item
   * @param itemId
   * @exception IngeServiceException
   * @return {@link String}
   */
  public String createItem(PubItemVO item, String itemId) throws IngeServiceException;

  /**
   * 
   * @param itemId
   * @throws IngeServiceException
   * @return {@link PubItemVO}
   */
  public PubItemVO readItem(String itemId) throws IngeServiceException;

  /**
   * 
   * @param itemId
   * @param item
   * @throws IngeServiceException
   * @return {@link String}
   */
  public String updateItem(PubItemVO item, String itemId)
      throws IngeServiceException;


  /**
   * 
   * @param itemId
   * @exception IngeServiceException
   * @return {@link String}
   */
  public String deleteItem(String itemId) throws IngeServiceException;
}
