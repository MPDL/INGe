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
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService.AccessType;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
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

  public PubItemBatchServiceImpl() {

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
