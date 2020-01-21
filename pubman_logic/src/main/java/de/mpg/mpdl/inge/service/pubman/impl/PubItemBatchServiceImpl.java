package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.es.util.ElasticSearchIndexField;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.PubItemBatchService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;


/**
 * Implementation of the PubItemBatchService interface
 * 
 * @author walter
 *
 */
@Service
@Primary
public class PubItemBatchServiceImpl implements PubItemBatchService {

  private static final Logger logger = LogManager.getLogger(PubItemBatchServiceImpl.class);

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ContextService contextService;

  public PubItemBatchServiceImpl() {

  }

  /**
   * @param pubItemsMap
   * @param LocalTags
   * @param message
   * @param authenticationToken
   * @return
   * @throws IngeApplicationException
   * @throws AuthorizationException
   * @throws AuthenticationException
   * @throws IngeTechnicalException
   */
  @Override
  public Map<String, Exception> addLocalTags(Map<String, Date> pubItemsMap, List<String> localTagsToAdd, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (itemId != null) {
          List<String> localTags = pubItemVO.getObject().getLocalTags();
          localTags.addAll(localTagsToAdd);
          pubItemVO.getObject().setLocalTags(localTags);;
          ItemVersionVO pubItemVOnew = this.pubItemService.update(pubItemVO, authenticationToken);
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not update local Tags for item " + itemId + " due to a technical error");
        messageMap.put(itemId, new Exception("Local Tags have not been updated due to a technical error"));
        throw e;
      } catch (AuthenticationException e) {
        logger.error("Could not update local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been updated due to a authentication error"));
        throw e;
      } catch (AuthorizationException e) {
        logger.error("Could not update local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been updated due to a authentication error"));
        throw e;
      } catch (IngeApplicationException e) {
        logger.error("Could not update local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been updated due to a authentication error"));
        throw e;
      }

    }
    return messageMap;

  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeContext(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  public Map<String, Exception> changeContext(Map<String, Date> pubItemsMap, String contextOld, String contextNew, String message,
      String authenticationToken) {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    ContextDbVO contextVO = null;
    try {
      contextVO = this.contextService.get(contextNew, authenticationToken);
    } catch (IngeTechnicalException | AuthenticationException | AuthorizationException | IngeApplicationException e) {
      logger.error("Batch changing of context failed. Error retrieving destination context", e);
    }
    for (String itemId : pubItemsMap.keySet()) {
      try {
        ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (itemId != null && contextOld.equals(pubItemVO.getObject().getContext().getObjectId())) {
          pubItemVO.getObject().setContext(contextVO);
          ItemVersionVO pubItemVOnew = this.pubItemService.update(pubItemVO, authenticationToken);
          if (pubItemVOnew != null && pubItemVOnew.getObject().getContext().equals(pubItemVO.getObject().getContext())) {
            messageMap.put(itemId, null);
          }
        }
        // this.pubItemService.update(object, authenticationToken)(itemId, pubItemsMap.get(itemId),
        // message, authenticationToken);
        logger.error("Could not update context of " + itemId + " because the from context is not the same as in the item");
        messageMap.put(itemId,
            new Exception("Context was not updated. Either Item was null, or the old context did not match the context of the item"));
      } catch (Exception e) {
        logger.error("Could not change context of item " + itemId, e);
        messageMap.put(itemId, e);
      }
    }
    return messageMap;
  }



  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#submitPubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> submitPubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        this.pubItemService.submitPubItem(itemId, pubItemsMap.get(itemId), message, authenticationToken);
        messageMap.put(itemId, null);
      } catch (Exception e) {
        logger.error("Could not batch submit item " + itemId, e);
        messageMap.put(itemId, e);
      }
    }
    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#releasePubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> releasePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        this.pubItemService.releasePubItem(itemId, pubItemsMap.get(itemId), message, authenticationToken);
        messageMap.put(itemId, null);
      } catch (Exception e) {
        logger.error("Could not batch release item " + itemId, e);
        messageMap.put(itemId, e);
      }
    }
    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#withdrawPubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> withdrawPubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        this.pubItemService.withdrawPubItem(itemId, pubItemsMap.get(itemId), message, authenticationToken);
        messageMap.put(itemId, null);
      } catch (Exception e) {
        logger.error("Could not batch withdraw item " + itemId, e);
        messageMap.put(itemId, e);
      }
    }
    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#revisePubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> revisePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        this.pubItemService.revisePubItem(itemId, pubItemsMap.get(itemId), message, authenticationToken);
        messageMap.put(itemId, null);
      } catch (Exception e) {
        logger.error("Could not batch revise item " + itemId, e);
        messageMap.put(itemId, e);
      }
    }
    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#deletePubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> deletePubItems(Map<String, Date> pubItemsMap, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        this.pubItemService.delete(itemId, authenticationToken);
        messageMap.put(itemId, null);
      } catch (Exception e) {
        logger.error("Could not batch delete item " + itemId, e);
        messageMap.put(itemId, e);
      }
    }
    return messageMap;
  }

}
