package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
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
   * @exception TechnicalException
   * @exception DepositingException
   * @return {@link String}
 * @throws SecurityException 
   */
  public String createItem(PubItemVO item, String itemId) throws IngeServiceException, TechnicalException, SecurityException;

  /**
   * 
   * @param itemId
   * @throws TechnicalException
   * @throws PubItemNotFoundException
   * @throws SecurityException
   * @return {@link PubItemVO}
   */
  public PubItemVO readItem(String itemId) throws IngeServiceException;

  /**
   * 
   * @param itemId
   * @param item
   * @throws TechnicalException
   * @throws DepositingException
   * @throws ItemNotFoundException
   * @return {@link String}
   */ 
  public String updateItem(PubItemVO item, String itemId, boolean createNewVersion) throws IngeServiceException;


  /**
   * 
   * @param itemId
   * @exception TechnicalException
   * @exception DepositingException
   * @exception ItemNotFoundException
   * @return {@link String}
   */
  public String deleteItem(String itemId) throws IngeServiceException;
}
