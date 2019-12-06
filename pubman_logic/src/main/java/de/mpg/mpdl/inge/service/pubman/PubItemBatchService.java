package de.mpg.mpdl.inge.service.pubman;

import java.util.Date;
import java.util.Map;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
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

  // Submit multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId, exception>
  public Map<String, Exception> submitPubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  //Release multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId, exception>
  public Map<String, Exception> releasePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  //withdraw multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId, exception>
  public Map<String, Exception> withdrawPubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  //revise multiple Items within a Map <pubItemId, modificationDate> and return a Map with <itemId, exception>
  public Map<String, Exception> revisePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  //  public boolean checkAccess(AccessType at, Principal userAccount, ItemVersionVO item)
  //      throws IngeApplicationException, IngeTechnicalException;

  //  public void reindex(String id, boolean includeFulltext, String authenticationToken)
  //      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
