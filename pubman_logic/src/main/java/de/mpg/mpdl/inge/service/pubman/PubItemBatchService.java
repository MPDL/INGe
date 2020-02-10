package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
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
   * add keywords for multiple Items within a Map <pubItemId, modificationDate> and return a Map
   * with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> addKeywords(Map<String, Date> pubItemsMap, String keywordsNew, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

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
   * adding a source id for multiple items within a Map <pubItemId, modificationDate> and return a
   * Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param sourceNumber
   * @param sourceIdType
   * @param idNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> addSourceId(Map<String, Date> pubItemsMap, String sourceNumber, IdType sourceIdType, String idNew, String message,
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
   * replacing the content category for the external references of a Map <pubItemId,
   * modificationDate> of items and return a Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param contentCategoryOld
   * @param contentCategoryNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeExternalRefereneceContentCategory(Map<String, Date> pubItemsMap, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;


  /**
   * replacing the audience for the files of a Map <pubItemId, modificationDate> of items and return
   * a Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param audienceOld
   * @param audienceNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeFileAudience(Map<String, Date> pubItemsMap, String audienceOld, String audienceNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * replacing the content category for the files of a Map <pubItemId, modificationDate> of items
   * and return a Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param contentCategoryOld
   * @param contentCategoryNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeFileContentCategory(Map<String, Date> pubItemsMap, String contentCategoryOld, String contentCategoryNew,
      String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

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
   * replace one specific keyword for multiple items within a Map <pubItemId, modificationDate> and
   * return a Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param keywordsOld
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeKeywords(Map<String, Date> pubItemsMap, String keywordsOld, String keywordsNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * replace all keywords for multiple items within a Map <pubItemId, modificationDate> and return a
   * Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeKeywords(Map<String, Date> pubItemsMap, String keywordsNew, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * change review method for multiple items within a Map <pubItemId, modificationDate> and return a
   * Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param reviewMethodOld
   * @param reviewMethodNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeReviewMethod(Map<String, Date> pubItemsMap, String reviewMethodOld, String reviewMethodNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;


  /**
   * change source genre for multiple items within a Map <pubItemId, modificationDate> and return a
   * Map with <itemId, exception>
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
  Map<String, Exception> changeSourceGenre(Map<String, Date> pubItemsMap, SourceVO.Genre genreOld, SourceVO.Genre genreNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * replace source id for multiple items within a Map <pubItemId, modificationDate> and return a
   * Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param sourceNumber
   * @param sourceIdType
   * @param idOld
   * @param idNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeSourceIdReplace(Map<String, Date> pubItemsMap, String sourceNumber, IdType sourceIdType, String idOld,
      String idNew, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  /**
   * change source issue for multiple items within a Map <pubItemId, modificationDate> and return a
   * Map with <itemId, exception>
   * 
   * @param pubItemsMap
   * @param sourceNumber
   * @param issue
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> changeSourceIssue(Map<String, Date> pubItemsMap, String sourceNumber, String issue, String message,
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
   * replacing all keywords for a Map <pubItemId, modificationDate> of items and return a Map with
   * <itemId, exception>
   * 
   * @param pubItemsMap
   * @param keywordsNew
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeTechnicalException
   * @throws AuthenticationException
   * @throws AuthorizationException
   * @throws IngeApplicationException
   */
  Map<String, Exception> replaceAllKeywords(Map<String, Date> pubItemsMap, String keywordsNew, String message, String authenticationToken)
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


  // public boolean checkAccess(AccessType at, Principal userAccount, ItemVersionVO item)
  // throws IngeApplicationException, IngeTechnicalException;

  // public void reindex(String id, boolean includeFulltext, String authenticationToken)
  // throws IngeTechnicalException, AuthenticationException, AuthorizationException,
  // IngeApplicationException;
}