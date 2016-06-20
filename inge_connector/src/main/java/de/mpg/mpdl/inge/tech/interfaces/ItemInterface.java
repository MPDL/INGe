package de.mpg.mpdl.inge.tech.interfaces;

import de.escidoc.core.client.exceptions.application.notfound.ItemNotFoundException;
import de.mpg.mpdl.inge.model.exceptions.TechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ItemVO;
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
   * Create a new item. A stupid creation of the item. Referenced collection needs to be checked in
   * advance.
   * 
   * @param item
   * @exception TechnicalException
   * @exception DepositingException
   */
  public void createItem(ItemVO item) throws TechnicalException, SecurityException;


  /**
   * Reads a requested item
   * 
   * @param itemId The ID of the pubItem
   * @throws TechnicalException
   * @throws PubItemNotFoundException
   * @throws SecurityException
   * @return the pubItem with the id equal to pubItemId
   */
  public ItemVO readItem(String itemId) throws TechnicalException, NotFoundException,
      SecurityException;

  /**
   * Update an item. A stupid update of an item. If the item is existing no further checks will be
   * made.
   * 
   * @param id
   * @param createNewVersion
   * @exception TechnicalException
   * @exception DepositingException
   * @exception ItemNotFoundException
   */
  public void updateItem(ItemVO item, boolean createNewVersion) throws TechnicalException,
      SecurityException, NotFoundException;


  /**
   * Delete an item.
   * 
   * @param id
   * @exception TechnicalException
   * @exception DepositingException
   * @exception ItemNotFoundException
   */
  public void deleteItem(String id) throws TechnicalException, SecurityException, NotFoundException;


  /**
   * Returns a list of pubItems satisfying the requirements of the searchQuery
   * 
   * @param searchQuery The search query
   * @throws TechnicalException
   * @return a list of items satisfying the requirements of the searchQuery
   */
  public java.util.List<ItemVO> searchItems(String searchQuery) throws TechnicalException;

}
