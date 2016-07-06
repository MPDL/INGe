package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.tech.exceptions.NotFoundException;

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
   */
  public String createItem(ItemVO item, String itemId) throws TechnicalException, SecurityException;


  /**
   * 
   * @param itemId
   * @throws TechnicalException
   * @throws PubItemNotFoundException
   * @throws SecurityException
   * @return {@link PubItemVO}
   */
  public PubItemVO readItem(String itemId) throws TechnicalException, NotFoundException,
      SecurityException;

  /**
   * 
   * @param itemId
   * @param item
   * @throws TechnicalException
   * @throws DepositingException
   * @throws ItemNotFoundException
   * @return {@link String}
   */
  public String updateItem(ItemVO item, String itemId, boolean createNewVersion)
      throws TechnicalException, SecurityException, NotFoundException;


  /**
   * 
   * @param itemId
   * @exception TechnicalException
   * @exception DepositingException
   * @exception ItemNotFoundException
   * @return {@link String}
   */
  public String deleteItem(String itemId) throws TechnicalException, SecurityException,
      NotFoundException;
}
