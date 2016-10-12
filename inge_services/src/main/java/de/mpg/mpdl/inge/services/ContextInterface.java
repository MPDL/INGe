package de.mpg.mpdl.inge.services;

import de.mpg.mpdl.inge.model.valueobjects.ContextVO;


/**
 * Interface for persisting and retrieving Contexts
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public interface ContextInterface {
  /**
   * 
   * @param context
   * @param contextId
   * @throws IngeServiceException
   * @return {@link String}
   */
  public String createContext(ContextVO context, String contextId) throws IngeServiceException;


  /**
   * 
   * @param contextId
   * @throws IngeServiceException
   * @return {@link ContextVO}
   */
  public ContextVO readContext(String contextId) throws IngeServiceException;

  /**
   * 
   * @param context
   * @param contextId
   * @throws IngeServiceException
   * @return {@link String}
   */
  public String updateContext(ContextVO context, String contextId) throws IngeServiceException;

  /**
   * 
   * @param contextId
   * @throws IngeServiceException
   * @return {@link String}
   */
  public String deleteContext(String contextId) throws IngeServiceException;
}
