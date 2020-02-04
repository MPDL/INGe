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
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Storage;
import de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.ReviewMethod;
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

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#addLocalTags(java.util.Map,
   * java.util.List, java.lang.String, java.lang.String)
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
        logger.error("Could not add local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been added due to a authentication error"));
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
   * @see
   * de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeExternalRefereneceContentCategory(
   * java.util.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> changeExternalRefereneceContentCategory(Map<String, Date> pubItemsMap, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (contentCategoryOld != null && contentCategoryNew != null && !contentCategoryOld.equals(contentCategoryNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          boolean anyFilesChanged = false;
          for (FileDbVO file : pubItemVO.getFiles()) {
            if (FileDbVO.Storage.EXTERNAL_URL.equals(file.getStorage())
                && file.getMetadata().getContentCategory().equals(contentCategoryOld)) {
              file.getMetadata().setContentCategory(contentCategoryNew);
              anyFilesChanged = true;
            }
          }
          if (anyFilesChanged == true) {
            this.pubItemService.update(pubItemVO, authenticationToken);
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace external reference content category for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("External reference content category has not been replaced due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not replace external reference content category for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("External reference content category has not been replaced due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not replace external reference content category for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("External reference content category has not been replaced due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not replace external reference content category for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("External reference content category has not been replaced due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeFileAudience(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> changeFileAudience(Map<String, Date> pubItemsMap, String audienceOld, String audienceNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (audienceOld != null && audienceNew != null && !audienceOld.equals(audienceNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          boolean anyFilesChanged = false;
          for (FileDbVO file : pubItemVO.getFiles()) {
            List<String> audienceList = file.getAllowedAudienceIds();
            if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage()) && audienceList.contains(audienceOld)) {
              audienceList.remove(audienceList.indexOf(audienceOld));
              audienceList.add(audienceNew);
              anyFilesChanged = true;
            }
          }
          if (anyFilesChanged == true) {
            this.pubItemService.update(pubItemVO, authenticationToken);
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace file audience for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("File audience has not been replaced due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not replace file audience for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File audience has not been replaced due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not replace file audience for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File audiencehas not been replaced due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not replace file audience for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File audience has not been replaced due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }


  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeFileContentCategory(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> changeFileContentCategory(Map<String, Date> pubItemsMap, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (contentCategoryOld != null && contentCategoryNew != null && !contentCategoryOld.equals(contentCategoryNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          boolean anyFilesChanged = false;
          for (FileDbVO file : pubItemVO.getFiles()) {
            if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                && file.getMetadata().getContentCategory().equals(contentCategoryOld)) {
              file.getMetadata().setContentCategory(contentCategoryNew);
              anyFilesChanged = true;
            }
          }
          if (anyFilesChanged == true) {
            this.pubItemService.update(pubItemVO, authenticationToken);
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace file content category for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("File content category has not been replaced due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not replace file content category for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File content category has not been replaced due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not replace file content category for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File content category has not been replaced due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not replace file content category for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File content category has not been replaced due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeFileVisibility(java.util.Map,
   * de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility,
   * de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility, java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> changeFileVisibility(Map<String, Date> pubItemsMap, Visibility visibilityOld, Visibility visibilityNew,
      String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (visibilityOld != null && visibilityNew != null && !visibilityOld.equals(visibilityNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          boolean anyFilesChanged = false;
          for (FileDbVO file : pubItemVO.getFiles()) {
            if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                && file.getVisibility().toString().equals(visibilityOld.toString())) {
              file.setVisibility(FileDbVO.Visibility.valueOf(visibilityNew.toString()));
              anyFilesChanged = true;
            }
          }
          if (anyFilesChanged == true) {
            this.pubItemService.update(pubItemVO, authenticationToken);
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace file visibility for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("File visibility has not been replaced due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not replace file visibility for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File visibility has not been replaced due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not replace file visibility for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File visibility has not been replaced due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not replace file visibility for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("File visibility has not been replaced due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeGenre(java.util.Map,
   * de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre,
   * de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre, java.lang.String,
   * java.lang.String)
   */
  @Override
  public Map<String, Exception> changeGenre(Map<String, Date> pubItemsMap, Genre genreOld, Genre genreNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (genreOld != null && genreNew != null && !genreOld.equals(genreNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          Genre currentPubItemGenre = pubItemVO.getMetadata().getGenre();
          if (currentPubItemGenre.equals(genreOld)) {
            pubItemVO.getMetadata().setGenre(genreNew);
            this.pubItemService.update(pubItemVO, authenticationToken);
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace local Tags for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not replace local Tags for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not replace local Tags for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not replace local Tags for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeReviewMethod(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> changeReviewMethod(Map<String, Date> pubItemsMap, String reviewMethodOld, String reviewMethodNew,
      String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (reviewMethodOld != null && reviewMethodNew != null && !reviewMethodOld.equals(reviewMethodNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          ReviewMethod currentReviewMethod = pubItemVO.getMetadata().getReviewMethod();
          if (currentReviewMethod.equals(ReviewMethod.valueOf(reviewMethodOld))) {
            pubItemVO.getMetadata().setReviewMethod(ReviewMethod.valueOf(reviewMethodNew));
            this.pubItemService.update(pubItemVO, authenticationToken);
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace review method for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("Review method have not been replaced due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not replace review method for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Review method have not been replaced due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not replace review method for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Review method have not been replaced due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not replace review method for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Review method have not been replaced due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeSourceGenre(java.util.Map,
   * de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre,
   * de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre, java.lang.String,
   * java.lang.String)
   */
  @Override
  public Map<String, Exception> changeSourceGenre(Map<String, Date> pubItemsMap, SourceVO.Genre genreOld, SourceVO.Genre genreNew,
      String message, String authenticationToken)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (genreOld != null && genreNew != null && !genreOld.equals(genreNew)) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          List<SourceVO> currentSourceList = pubItemVO.getMetadata().getSources();
          for (SourceVO currentSource : currentSourceList) {
            SourceVO.Genre currentSourceGenre = currentSource.getGenre();
            if (currentSourceGenre.equals(genreOld)) {
              currentSource.setGenre(genreNew);
              this.pubItemService.update(pubItemVO, authenticationToken);
            }
          }

        } catch (IngeTechnicalException e) {
          logger.error("Could not change source genre for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("Source genre has not been changed due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not change source genre for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Source genre has not been changed due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not change source genre for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Source genre has not been changed due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not change source genre for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Source genre has not been changed due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }

  @Override
  public Map<String, Exception> changeSourceIssue(Map<String, Date> pubItemsMap, String sourceNumber, String issue, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    if (sourceNumber != null && issue != null) {
      for (String itemId : pubItemsMap.keySet()) {
        try {
          ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          List<SourceVO> currentSourceList = pubItemVO.getMetadata().getSources();
          int sourceNumberInt = Integer.parseInt(sourceNumber);
          if (currentSourceList != null && currentSourceList.size() >= sourceNumberInt) {
            if (currentSourceList.get(sourceNumberInt - 1) != null) {
              currentSourceList.get(sourceNumberInt - 1).setIssue(issue);
              this.pubItemService.update(pubItemVO, authenticationToken);
            }
          }

        } catch (IngeTechnicalException e) {
          logger.error("Could not change source issue for item " + itemId + " due to a technical error");
          messageMap.put(itemId, new Exception("Source issue has not been changed due to a technical error"));
          throw e;
        } catch (AuthenticationException e) {
          logger.error("Could not change source issue for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Source issue has not been changed due to a authentication error"));
          throw e;
        } catch (AuthorizationException e) {
          logger.error("Could not change source issue for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Source issue has not been changed due to a authentication error"));
          throw e;
        } catch (IngeApplicationException e) {
          logger.error("Could not change source issue for item " + itemId + " due authentication error");
          messageMap.put(itemId, new Exception("Source issue has not been changed due to a authentication error"));
          throw e;
        }
      }
    }

    return messageMap;
  }


  /*
   * (non-Javadoc)
   * 
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#replaceLocalTags(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public Map<String, Exception> replaceLocalTags(Map<String, Date> pubItemsMap, String localTagOld, String localTagNew, String message,
      String authenticationToken) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {
    Map<String, Exception> messageMap = new HashMap<String, Exception>();
    for (String itemId : pubItemsMap.keySet()) {
      try {
        ItemVersionVO pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (localTagOld != null && localTagNew != null && !"".equals(localTagOld.trim()) && pubItemVO.getObject().getLocalTags() != null
            && pubItemVO.getObject().getLocalTags().contains(localTagOld)) {
          List<String> localTagList = pubItemVO.getObject().getLocalTags();
          localTagList.remove(localTagOld);
          localTagList.add(localTagNew);
          pubItemVO.getObject().setLocalTags(localTagList);
          this.pubItemService.update(pubItemVO, authenticationToken);
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not replace local Tags for item " + itemId + " due to a technical error");
        messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a technical error"));
        throw e;
      } catch (AuthenticationException e) {
        logger.error("Could not replace local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a authentication error"));
        throw e;
      } catch (AuthorizationException e) {
        logger.error("Could not replace local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a authentication error"));
        throw e;
      } catch (IngeApplicationException e) {
        logger.error("Could not replace local Tags for item " + itemId + " due authentication error");
        messageMap.put(itemId, new Exception("Local Tags have not been replaced due to a authentication error"));
        throw e;
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
