package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

/**
 * Interface defining the batch functions for PubItems
 * 
 * @author walter
 *
 */
public interface PubItemBatchService {

  /**
   * add local tags for multiple Items within a Map <pubItemId, modificationDate> and return a Map
   * with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param selectedContextOld
   * @param selectedContextNew
   * @param string
   * @param authenticationToken
   * @throws IngeApplicationException
   * @throws AuthorizationException
   * @throws AuthenticationException
   * @throws IngeTechnicalException
   */
  public Map<String, Exception> addLocalTags(Map<String, Date> pubItemsMap, List<String> localTagsToAdd, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * change the context of multiple Items within a Map <pubItemId, modificationDate> from contextOld
   * to contextNew return a Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param contextOld
   * @param contextNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public Map<String, Exception> changeContext(Map<String, Date> pubItemsMap, String contextOld, String contextNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * change genre for multiple items within a Map <pubItemId, modificationDate> and return a Map
   * with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param genreOld
   * @param genreNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeGenre(Map<String, Date> pubItemsMap, Genre genreOld, Genre genreNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * delete multiple items within a Map <pubItemId, modificationDate> and return a Map with <itemId,
   * exception>
   * 
   * @param pubItemsMap
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public Map<String, Exception> deletePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * Release multiple Items within a Map <pubItemId, modificationDate> and return a Map with
   * <itemId, exception>
   * 
   * @param pubItemsMap
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public Map<String, Exception> releasePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * revise multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId,
   * exception>
   * 
   * @param pubItemsMap
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public Map<String, Exception> revisePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * Submit multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId,
   * exception>
   * 
   * @param pubItemsMap
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public Map<String, Exception> submitPubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * withdraw multiple Items within a Map <pubItemId, modificationDate> and return a Map with
   * <itemId, exception>
   * 
   * @param pubItemsMap
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  public Map<String, Exception> withdrawPubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * replacing local tags for a Map <pubItemId, modificationDate> of items and return a Map with
   * <itemId, exception>
   * 
   * @param pubItemsMap
   * @param contextOld
   * @param contextNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> replaceLocalTags(Map<String, Date> pubItemsMap, String localTagsOld, String localTagsNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * replacing the visibility for the files of a Map <pubItemId, modificationDate> of items and
   * return a Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param visibilityOld
   * @param visibilityOld2
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeFileVisibility(Map<String, Date> pubItemsMap, Visibility visibilityOld, Visibility visibilityOld2,
      String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;



  // public boolean checkAccess(AccessType at, Principal userAccount, ItemVersionVO item)
  // throws IngeApplicationException, IngeTechnicalException;

  // public void reindex(String id, boolean includeFulltext, String authenticationToken)
  // throws IngeTechnicalException, AuthenticationException, AuthorizationException,
  // IngeApplicationException;
}
