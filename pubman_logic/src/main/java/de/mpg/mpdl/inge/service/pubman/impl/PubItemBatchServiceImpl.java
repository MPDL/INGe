package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.db.repository.BatchLogRepository;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessItemVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
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

  @Autowired
  private BatchLogRepository batchRepository;

  public PubItemBatchServiceImpl() {}

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#addKeywords(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO addKeywords(List<String> pubItemObjectIdList, String keywordsNew, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);

    if (null != keywordsNew && !keywordsNew.trim().isEmpty()) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            String currentKeywords = null;
            if (null != (currentKeywords = pubItemVO.getMetadata().getFreeKeywords())) {
              if (currentKeywords.contains(",")) {
                pubItemVO.getMetadata().setFreeKeywords(currentKeywords + ", " + keywordsNew);
              } else if (currentKeywords.contains(";")) {
                pubItemVO.getMetadata().setFreeKeywords(currentKeywords + "; " + keywordsNew);
              } else if (currentKeywords.contains(" ")) {
                pubItemVO.getMetadata().setFreeKeywords(currentKeywords + " " + keywordsNew);
              } else {
                pubItemVO.getMetadata().setFreeKeywords(currentKeywords + ", " + keywordsNew);
              }
            } else {
              pubItemVO.getMetadata().setFreeKeywords(keywordsNew);
            }
            if (null != pubItemVO.getObject().getLocalTags()) {
              pubItemVO.getObject().getLocalTags().add(message);
            } else {
              pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
            }
            resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not add keywords for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not add keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not add keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not add keywords for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not add keywords for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#addLocalTags(java.util.Map,
   * java.util.List, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO addLocalTags(List<String> pubItemObjectIdList, List<String> localTagsToAdd, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != localTagsToAdd && !localTagsToAdd.isEmpty()) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            if (null != itemId && null != localTagsToAdd && !localTagsToAdd.isEmpty()) {
              List<String> localTags = pubItemVO.getObject().getLocalTags();
              localTags.addAll(localTagsToAdd);
              localTags.add(message);
              pubItemVO.getObject().setLocalTags(localTags);
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_NEW_VALUE_SET,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not add local tags for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not add local tags for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not add local tags for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not add local tags for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not add local tags for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeContext(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeContext(List<String> pubItemObjectIdList, String contextOld, String contextNew, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ContextDbVO contextVO = null;
    if (null != contextNew) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          contextVO = this.contextService.get(contextNew, authenticationToken);
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            if (null != itemId && contextOld.equals(pubItemVO.getObject().getContext().getObjectId())) {
              if (null != pubItemVO.getMetadata() && null != pubItemVO.getMetadata().getGenre() && null != contextVO.getAllowedGenres()
                  && !contextVO.getAllowedGenres().isEmpty() && contextVO.getAllowedGenres().contains(pubItemVO.getMetadata().getGenre())) {
                pubItemVO.getObject().setContext(contextVO);
                if (!((ItemVersionRO.State.SUBMITTED.equals(pubItemVO.getObject().getPublicState())
                    || ItemVersionRO.State.IN_REVISION.equals(pubItemVO.getVersionState()))
                    && ContextDbVO.Workflow.SIMPLE.equals(contextVO.getWorkflow()))) {
                  if (null != pubItemVO.getObject().getLocalTags()) {
                    pubItemVO.getObject().getLocalTags().add(message);
                  } else {
                    pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                  }
                  resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                      BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
                } else {
                  resultList
                      .add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_ALLOWED,
                          BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
                }
              } else {
                resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_ALLOWED,
                    BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
              }
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not add keywords for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not add keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not add keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not add keywords for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not add keywords for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeExternalReferenceContentCategory(
   * java.util.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeExternalReferenceContentCategory(List<String> pubItemObjectIdList, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != contentCategoryOld && null != contentCategoryNew && !contentCategoryOld.equals(contentCategoryNew)) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            boolean anyFilesChanged = false;
            for (FileDbVO file : pubItemVO.getFiles()) {
              if (FileDbVO.Storage.EXTERNAL_URL.equals(file.getStorage())
                  && file.getMetadata().getContentCategory().equals(contentCategoryOld)) {
                file.getMetadata().setContentCategory(contentCategoryNew);
                anyFilesChanged = true;
              }
            }
            if (true == anyFilesChanged) {
              if (null != pubItemVO.getObject().getLocalTags()) {
                pubItemVO.getObject().getLocalTags().add(message);
              } else {
                pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
              }
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.FILES_METADATA_OLD_VALUE_NOT_EQUAL,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change external reference content category for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change external reference content category for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change external reference content category for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change external reference content category for item " + itemId + " due to an internal application error",
              e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change external reference content category for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see
   * de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeOrcid(
   * java.util.Map, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeOrcid(List<String> pubItemObjectIdList, String creatorId, String orcidNew, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
          boolean anyOrcidChanged = false;
          for (CreatorVO creator : pubItemVO.getMetadata().getCreators()) {
            PersonVO person = creator.getPerson();
            if (null != person && null != person.getIdentifier()) {
              if (creatorId.equals(person.getIdentifier().getId())) {
                if (!orcidNew.equals(person.getOrcid())) {
                  person.setOrcid(orcidNew);
                  anyOrcidChanged = true;
                }
              }
            }
          }
          List<SourceVO> sources = pubItemVO.getMetadata().getSources();
          for (SourceVO source : sources) {
            for (CreatorVO creator : source.getCreators()) {
              PersonVO person = creator.getPerson();
              if (null != person && null != person.getIdentifier()) {
                if (creatorId.equals(person.getIdentifier().getId())) {
                  if (!orcidNew.equals(person.getOrcid())) {
                    person.setOrcid(orcidNew);
                    anyOrcidChanged = true;
                  }
                }
              }
            }
          }
          if (true == anyOrcidChanged) {
            if (null != pubItemVO.getObject().getLocalTags()) {
              pubItemVO.getObject().getLocalTags().add(message);
            } else {
              pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
            }
            resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_ORCID_NO_PERSON,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not change orcid for item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not change orcid for item " + itemId + " due authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could not change orcid for item " + itemId + " due authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could not change orcid for item " + itemId + " due to an internal application error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not change orcid for item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeFileAudience(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeFileAudience(List<String> pubItemObjectIdList, List<String> audienceListNew, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != audienceListNew && !audienceListNew.isEmpty()) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            boolean anyFilesChanged = false;
            for (FileDbVO file : pubItemVO.getFiles()) {
              List<String> audienceList = null != file.getAllowedAudienceIds() ? file.getAllowedAudienceIds() : new ArrayList<>();
              if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                  && FileDbVO.Visibility.AUDIENCE.equals(file.getVisibility())) {
                audienceList.clear();
                audienceList.addAll(audienceListNew);
                file.setAllowedAudienceIds(audienceList);
                anyFilesChanged = true;
              }
            }
            if (true == anyFilesChanged) {
              if (null != pubItemVO.getObject().getLocalTags()) {
                pubItemVO.getObject().getLocalTags().add(message);
              } else {
                pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
              }
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.FILES_METADATA_OLD_VALUE_NOT_EQUAL,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change file audience for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change file audience for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change file audiencefor item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change file audience for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change file audience for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }


  /*
   * (non-Javadoc)
   *
   * @see
   * de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeFileContentCategory(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeFileContentCategory(List<String> pubItemObjectIdList, String contentCategoryOld,
      String contentCategoryNew, String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != contentCategoryOld && null != contentCategoryNew && !contentCategoryOld.equals(contentCategoryNew)) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            boolean anyFilesChanged = false;
            for (FileDbVO file : pubItemVO.getFiles()) {
              if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                  && file.getMetadata().getContentCategory().equals(contentCategoryOld)) {
                file.getMetadata().setContentCategory(contentCategoryNew);
                anyFilesChanged = true;
              }
            }
            if (true == anyFilesChanged) {
              if (null != pubItemVO.getObject().getLocalTags()) {
                pubItemVO.getObject().getLocalTags().add(message);
              } else {
                pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
              }
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change file content category for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change file content category for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change file content category for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change file content category for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change file content category for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeFileVisibility(java.util.Map,
   * de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility,
   * de.mpg.mpdl.inge.model.valueobjects.FileVO.Visibility, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeFileVisibility(List<String> pubItemObjectIdList, FileVO.Visibility visibilityOld,
      FileVO.Visibility visibilityNew, IpListProvider.IpRange userAccountIpRange, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != visibilityOld && null != visibilityNew && !visibilityOld.equals(visibilityNew)) {
      ItemVersionVO pubItemVO = null;
      String ipRangeToSet = null;
      if (null != userAccountIpRange && null != userAccountIpRange.getId()) {
        ipRangeToSet = userAccountIpRange.getId();
      }
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            boolean anyFilesChanged = false;
            for (FileDbVO file : pubItemVO.getFiles()) {
              if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                  && file.getVisibility().toString().equals(visibilityOld.toString())) {
                file.setVisibility(FileDbVO.Visibility.valueOf(visibilityNew.toString()));
                if (FileVO.Visibility.AUDIENCE.equals(visibilityNew)) {
                  if (null != file.getAllowedAudienceIds() && null != ipRangeToSet) {
                    file.getAllowedAudienceIds().add(ipRangeToSet);
                  } else if (null == file.getAllowedAudienceIds()) {
                    file.setAllowedAudienceIds(new ArrayList<>());
                    if (null != ipRangeToSet) {
                      file.getAllowedAudienceIds().add(ipRangeToSet);
                    }
                  }
                }
                if (FileVO.Visibility.PUBLIC.equals(visibilityNew) && null != file.getMetadata().getEmbargoUntil()) {
                  file.getMetadata().setEmbargoUntil(null);
                }
                anyFilesChanged = true;
              }
            }
            if (true == anyFilesChanged) {
              if (null != pubItemVO.getObject().getLocalTags()) {
                pubItemVO.getObject().getLocalTags().add(message);
              } else {
                pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
              }
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.FILES_METADATA_OLD_VALUE_NOT_EQUAL,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change file visibility for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change file visibility for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change file visibility for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change file visibility for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change file visibility for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
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
  public BatchProcessLogDbVO changeGenre(List<String> pubItemObjectIdList, MdsPublicationVO.Genre genreOld, MdsPublicationVO.Genre genreNew,
      MdsPublicationVO.DegreeType degree, String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != genreOld && null != genreNew) {
      ItemVersionVO pubItemVO = null;
      ContextDbVO contextVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        contextVO = null; // reset contextVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          contextVO = this.contextService.get(pubItemVO.getObject().getContext().getObjectId(), authenticationToken);
          MdsPublicationVO.Genre currentPubItemGenre = pubItemVO.getMetadata().getGenre();
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            if (null != contextVO.getAllowedGenres() && !contextVO.getAllowedGenres().isEmpty()
                && contextVO.getAllowedGenres().contains(pubItemVO.getMetadata().getGenre())) {
              if (currentPubItemGenre.equals(genreOld)) {
                if (!genreOld.equals(genreNew)) {
                  if (!MdsPublicationVO.Genre.THESIS.equals(genreNew)) {
                    pubItemVO.getMetadata().setGenre(genreNew);
                    if (null != pubItemVO.getObject().getLocalTags()) {
                      pubItemVO.getObject().getLocalTags().add(message);
                    } else {
                      pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                    }
                    resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                        BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
                  } else if (MdsPublicationVO.Genre.THESIS.equals(genreNew) && null != degree) {
                    pubItemVO.getMetadata().setGenre(genreNew);
                    pubItemVO.getMetadata().setDegree(degree);
                    if (null != pubItemVO.getObject().getLocalTags()) {
                      pubItemVO.getObject().getLocalTags().add(message);
                    } else {
                      pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                    }
                    resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                        BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
                  } else {
                    resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_NEW_VALUE_SET,
                        BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
                  }

                } else {
                  resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_CHANGE_VALUE,
                      BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
                }
              } else {
                resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                    BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
              }
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_ALLOWED,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change genre for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change genre for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change genre for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change genre for item " + itemId + " due to an internal application error", e);
          BatchProcessItemVO.BatchProcessMessages batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR;
          if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
            ValidationException validationException = (ValidationException) e.getCause();
            ValidationReportVO validationReport = validationException.getReport();

            if (validationReport.hasItems()) {
              for (ValidationReportItemVO validationItem : validationReport.getItems()) {
                if (ErrorMessages.SOURCE_NOT_PROVIDED.equals(validationItem.getContent())) {
                  batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.VALIDATION_NO_SOURCE;
                  break;
                } else {
                  batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.VALIDATION_GLOBAL;
                  // no break: anther report Item could set a finer message
                }
              }
            }
          }
          resultList.add(new BatchProcessItemVO(pubItemVO, batchProcessMessage, BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change genre for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeKeywords(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeKeywords(List<String> pubItemObjectIdList, String keywordsOld, String keywordsNew, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != keywordsNew) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            boolean keywordsChanged = false;
            char splittingChar = ',';
            String currentKeywords = null;
            String[] keywordArray = new String[1];
            if (null != keywordsOld && !keywordsOld.trim().isEmpty()
                && null != (currentKeywords = pubItemVO.getMetadata().getFreeKeywords())) {

              if (currentKeywords.contains(",")) {
                keywordArray = currentKeywords.split(",");
              } else if (currentKeywords.contains(";")) {
                keywordArray = currentKeywords.split(";");
                splittingChar = ';';
              } else if (currentKeywords.contains(" ")) {
                keywordArray = currentKeywords.split(" ");
                splittingChar = ' ';
              } else {
                keywordArray[0] = currentKeywords;
              }
              StringBuilder keywordString = new StringBuilder();
              for (int i = 0; i < keywordArray.length; i++) {
                String keyword = keywordArray[i].trim();
                if (0 != i) {
                  keywordString.append(splittingChar);
                }
                if (!keyword.isEmpty() && keywordsOld.equals(keyword)) {
                  keywordString.append(keywordsNew);
                  keywordsChanged = true;
                } else {
                  keywordString.append(keyword);
                }
              }
              if (keywordsChanged) {
                pubItemVO.getMetadata().setFreeKeywords(keywordString.toString());
                if (null != pubItemVO.getObject().getLocalTags()) {
                  pubItemVO.getObject().getLocalTags().add(message);
                } else {
                  pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                }
                resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                    BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
              } else {
                resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                    BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
              }
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_CHANGE_VALUE,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change keywords for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change keywords for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change keywords for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeReviewMethod(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeReviewMethod(List<String> pubItemObjectIdList, String reviewMethodOld, String reviewMethodNew,
      String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    // if (reviewMethodOld != null && reviewMethodNew != null) {
    ItemVersionVO pubItemVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
          MdsPublicationVO.ReviewMethod currentReviewMethod = pubItemVO.getMetadata().getReviewMethod();
          if (null == reviewMethodOld || !reviewMethodOld.equals(reviewMethodNew)) {
            if ((null == currentReviewMethod && null == reviewMethodOld && null != reviewMethodNew) || (null != currentReviewMethod
                && null != reviewMethodOld && currentReviewMethod.equals(MdsPublicationVO.ReviewMethod.valueOf(reviewMethodOld)))) {
              if (null != reviewMethodNew) {
                pubItemVO.getMetadata().setReviewMethod(MdsPublicationVO.ReviewMethod.valueOf(reviewMethodNew));
              } else {
                pubItemVO.getMetadata().setReviewMethod(null);
              }
              if (null != pubItemVO.getObject().getLocalTags()) {
                pubItemVO.getObject().getLocalTags().add(message);
              } else {
                pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
              }
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_CHANGE_VALUE,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not change review method for item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not change review method for item " + itemId + " due authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could not change review method for item " + itemId + " due authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could not change review method for item " + itemId + " due to an internal application error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not change review method for item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    // }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
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
  public BatchProcessLogDbVO changeSourceGenre(List<String> pubItemObjectIdList, SourceVO.Genre genreOld, SourceVO.Genre genreNew,
      String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != genreOld && null != genreNew) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        boolean sourceChanged = false;
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            List<SourceVO> currentSourceList = pubItemVO.getMetadata().getSources();
            if (!genreOld.equals(genreNew)) {
              for (SourceVO currentSource : currentSourceList) {
                SourceVO.Genre currentSourceGenre = currentSource.getGenre();
                if (currentSourceGenre.equals(genreOld)) {
                  currentSource.setGenre(genreNew);
                  sourceChanged = true;

                }
              }
              if (true == sourceChanged) {
                if (null != pubItemVO.getObject().getLocalTags()) {
                  pubItemVO.getObject().getLocalTags().add(message);
                } else {
                  pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                }
                resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                    BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
              } else {
                resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                    BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
              }
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_CHANGE_VALUE,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change source genre for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change source genre for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change source genre for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change source genre for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change source genre for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#addSourceId(java.util.Map,
   * java.lang.String, de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType,
   * java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO addSourceId(List<String> pubItemObjectIdList, String sourceNumber, IdentifierVO.IdType sourceIdType,
      String idNew, String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != idNew) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            if (null != itemId) {
              List<SourceVO> currentSourceList = pubItemVO.getMetadata().getSources();
              int sourceNumberInt = Integer.parseInt(sourceNumber);
              if (null != currentSourceList && currentSourceList.size() >= sourceNumberInt
                  && null != currentSourceList.get(sourceNumberInt - 1)) {
                if (null != currentSourceList.get(sourceNumberInt - 1).getIdentifiers()) {
                  currentSourceList.get(sourceNumberInt - 1).getIdentifiers().add(new IdentifierVO(sourceIdType, idNew));
                  if (null != pubItemVO.getObject().getLocalTags()) {
                    pubItemVO.getObject().getLocalTags().add(message);
                  } else {
                    pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                  }
                  resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                      BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
                }
              } else {
                resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_SOURCE_FOUND,
                    BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
              }
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not add source id for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not add source id for item " + itemId + " due to an authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not add source id for item " + itemId + " due to an authorization error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not add source id for item " + itemId + " due to an application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not add source id for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeSourceIdReplace(java.util.Map,
   * java.lang.String, de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO.IdType,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeSourceIdReplace(List<String> pubItemObjectIdList, String sourceNumber, IdentifierVO.IdType sourceIdType,
      String idOld, String idNew, String message, String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != sourceNumber && null != sourceIdType && null != idOld && !idOld.trim().isEmpty()) {
      ItemVersionVO pubItemVO = null;
      boolean sourceChanged = false;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        sourceChanged = false; // reset sourceChanged
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            List<SourceVO> currentSourceList = pubItemVO.getMetadata().getSources();
            int sourceNumberInt = Integer.parseInt(sourceNumber);
            if (null != currentSourceList && currentSourceList.size() >= sourceNumberInt
                && null != currentSourceList.get(sourceNumberInt - 1)
                && null != currentSourceList.get(sourceNumberInt - 1).getIdentifiers()) {
              for (int i = 0; i < currentSourceList.get(sourceNumberInt - 1).getIdentifiers().size(); i++) {
                IdentifierVO identifier = currentSourceList.get(sourceNumberInt - 1).getIdentifiers().get(i);
                if (sourceIdType.equals(identifier.getType()) && idOld.equals(identifier.getId())) {
                  if (null != idNew && !idNew.trim().isEmpty()) {
                    identifier.setId(idNew);
                    currentSourceList.get(sourceNumberInt - 1).getIdentifiers().set(i, identifier);
                  } else {
                    currentSourceList.get(sourceNumberInt - 1).getIdentifiers().remove(i);
                  }
                  sourceChanged = true;
                }
              }
              if (true == sourceChanged) {
                if (null != pubItemVO.getObject().getLocalTags()) {
                  pubItemVO.getObject().getLocalTags().add(message);
                } else {
                  pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                }
                resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                    BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
              } else {
                resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_CHANGE_VALUE_NOT_EQUAL,
                    BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
              }
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_SOURCE_FOUND,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change source id for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change source id for item " + itemId + " due to an authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change source id for item " + itemId + " due to an authorization error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change source id for item " + itemId + " due to an application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not change source id for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#changeSourceEdition(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO changeSourceEdition(List<String> pubItemObjectIdList, String sourceNumber, String edition, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != sourceNumber) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            List<SourceVO> currentSourceList = pubItemVO.getMetadata().getSources();
            int sourceNumberInt = Integer.parseInt(sourceNumber);
            if (null != currentSourceList && currentSourceList.size() >= sourceNumberInt
                && null != currentSourceList.get(sourceNumberInt - 1)) {
              if (null != currentSourceList.get(sourceNumberInt - 1).getPublishingInfo()) {
                currentSourceList.get(sourceNumberInt - 1).getPublishingInfo().setEdition(edition);
                if (null != pubItemVO.getObject().getLocalTags()) {
                  pubItemVO.getObject().getLocalTags().add(message);
                } else {
                  pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                }
                resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                    BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
              } else {
                currentSourceList.get(sourceNumberInt - 1).setPublishingInfo(new PublishingInfoVO());
                currentSourceList.get(sourceNumberInt - 1).getPublishingInfo().setEdition(edition);
                if (null != pubItemVO.getObject().getLocalTags()) {
                  pubItemVO.getObject().getLocalTags().add(message);
                } else {
                  pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
                }
                resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                    BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
              }
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_SOURCE_FOUND,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }
        } catch (IngeTechnicalException e) {
          logger.error("Could not change source edition for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not change source edition for item " + itemId + " due to an authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not change source edition for item " + itemId + " due to an authorization error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not change source edition for item " + itemId + " due to an application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not cahnge source edition for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }


  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#replaceLocalTags(java.util.Map,
   * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO replaceLocalTags(List<String> pubItemObjectIdList, String localTagOld, String localTagNew, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    if (null != localTagOld && null != localTagNew && !localTagOld.trim().isEmpty()) {
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
            if (null != pubItemVO.getObject().getLocalTags() && pubItemVO.getObject().getLocalTags().contains(localTagOld)) {
              List<String> localTagList = pubItemVO.getObject().getLocalTags();
              localTagList.remove(localTagOld);
              localTagList.add(localTagNew);
              localTagList.add(message);
              pubItemVO.getObject().setLocalTags(localTagList);
              resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
                  BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
            } else {
              resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.METADATA_NO_CHANGE_VALUE,
                  BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
            }
          } else {
            resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
                BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
          }

        } catch (IngeTechnicalException e) {
          logger.error("Could not replace local tags for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not replace local tags for item " + itemId + " due to an authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not replace local tags for item " + itemId + " due to an authorization error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not replace local tags for item " + itemId + " due to an application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not replace local tags for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }

    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }



  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#submitPubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO submitPubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        ContextDbVO contextDbVO = this.contextService.get(pubItemVO.getObject().getContext().getObjectId(), authenticationToken);
        if ((ItemVersionRO.State.IN_REVISION.equals(pubItemVO.getVersionState())
            || ItemVersionRO.State.PENDING.equals(pubItemVO.getVersionState()))
            && ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow())
            && !ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
          resultList.add(new BatchProcessItemVO(
              this.pubItemService.submitPubItem(itemId, pubItemVO.getModificationDate(), message, authenticationToken),
              BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not submit item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not submit item " + itemId + " due to an authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could not submit item " + itemId + " due to an authorization error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could not submit item " + itemId + " due to an internal application error", e);
        BatchProcessItemVO.BatchProcessMessages batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR;
        if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
          ValidationException validationException = (ValidationException) e.getCause();
          ValidationReportVO validationReport = validationException.getReport();

          if (validationReport.hasItems()) {
            for (ValidationReportItemVO validationItem : validationReport.getItems()) {
              if (ErrorMessages.SOURCE_NOT_PROVIDED.equals(validationItem.getContent())) {
                batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.VALIDATION_NO_SOURCE;
                break;
              } else {
                batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.VALIDATION_GLOBAL;
                // no break: anther report Item could set a finer message
              }
            }
          }
        }
        resultList.add(new BatchProcessItemVO(pubItemVO, batchProcessMessage, BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not submit item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;


  }

  @Override
  public BatchProcessLogDbVO replaceAllKeywords(List<String> pubItemObjectIdList, String keywordsNew, String message,
      String authenticationToken, AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    if (null != keywordsNew) {
      ItemVersionVO pubItemVO = null;
      for (String itemId : pubItemObjectIdList) {
        pubItemVO = null; // reset pubItemVO
        try {
          pubItemVO = this.pubItemService.get(itemId, authenticationToken);
          pubItemVO.getMetadata().setFreeKeywords(keywordsNew);
          if (null != pubItemVO.getObject().getLocalTags()) {
            pubItemVO.getObject().getLocalTags().add(message);
          } else {
            pubItemVO.getObject().setLocalTags(new ArrayList<>(Collections.singletonList(message)));
          }
          resultList.add(new BatchProcessItemVO(this.pubItemService.update(pubItemVO, authenticationToken),
              BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } catch (IngeTechnicalException e) {
          logger.error("Could not replace keywords for item " + itemId + " due to a technical error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthenticationException e) {
          logger.error("Could not replace keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (AuthorizationException e) {
          logger.error("Could not replace keywords for item " + itemId + " due authentication error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (IngeApplicationException e) {
          logger.error("Could not replace keywords for item " + itemId + " due to an internal application error", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        } catch (NullPointerException e) {
          logger.error("Could not replace keywords for item " + itemId + " due to an nullpointer exception", e);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  @Override
  public BatchProcessLogDbVO getBatchProcessLogForCurrentUser(AccountUserDbVO accountUser) {
    BatchProcessLogDbVO resultBatchProcessLog = null;
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      resultBatchProcessLog = this.batchRepository.findById(accountUser.getObjectId()).orElse(null);
    }
    return resultBatchProcessLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#releasePubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO releasePubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    ContextDbVO contextVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      contextVO = null; //reset contextVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        contextVO = this.contextService.get(pubItemVO.getObject().getContext().getObjectId(), authenticationToken);
        if (ItemVersionRO.State.SUBMITTED.equals(pubItemVO.getVersionState())) {
          resultList.add(new BatchProcessItemVO(
              this.pubItemService.releasePubItem(itemId, pubItemVO.getModificationDate(), message, authenticationToken),
              BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } else if (ItemVersionRO.State.PENDING.equals(pubItemVO.getVersionState())
            && ContextDbVO.Workflow.SIMPLE.equals(contextVO.getWorkflow())) {
          resultList.add(new BatchProcessItemVO(
              this.pubItemService.releasePubItem(itemId, pubItemVO.getModificationDate(), message, authenticationToken),
              BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not release item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not release item " + itemId + " due to an authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could release item " + itemId + " due to an authorization error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could release item " + itemId + " due to an internal application error", e);
        BatchProcessItemVO.BatchProcessMessages batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR;
        if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
          ValidationException validationException = (ValidationException) e.getCause();
          ValidationReportVO validationReport = validationException.getReport();

          if (validationReport.hasItems()) {
            for (ValidationReportItemVO validationItem : validationReport.getItems()) {
              if (ErrorMessages.SOURCE_NOT_PROVIDED.equals(validationItem.getContent())) {
                batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.VALIDATION_NO_SOURCE;
                break;
              } else {
                batchProcessMessage = BatchProcessItemVO.BatchProcessMessages.VALIDATION_GLOBAL;
                // no break: anther report Item could set a finer message
              }
            }
          }
        }
        resultList.add(new BatchProcessItemVO(pubItemVO, batchProcessMessage, BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not release item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#withdrawPubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO withdrawPubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (ItemVersionRO.State.RELEASED.equals(pubItemVO.getVersionState())
            && !ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
          resultList.add(new BatchProcessItemVO(
              this.pubItemService.withdrawPubItem(itemId, pubItemVO.getModificationDate(), message, authenticationToken),
              BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not withdraw item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not withdraw item " + itemId + " due to an authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could withdraw item " + itemId + " due to an authorization error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could withdraw item " + itemId + " due to an application error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not withdraw item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#revisePubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO revisePubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        ContextDbVO contextDbVO = this.contextService.get(pubItemVO.getObject().getContext().getObjectId(), authenticationToken);
        if (ItemVersionRO.State.SUBMITTED.equals(pubItemVO.getVersionState())
            && ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow())
            && !ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())) {
          resultList.add(new BatchProcessItemVO(
              this.pubItemService.revisePubItem(itemId, pubItemVO.getModificationDate(), message, authenticationToken),
              BatchProcessItemVO.BatchProcessMessages.SUCCESS, BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not revise item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not revise item " + itemId + " due to an authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could not revise item " + itemId + " due to an authorization error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could not revise item " + itemId + " due to an application error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not revise item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }

  /*
   * (non-Javadoc)
   *
   * @see de.mpg.mpdl.inge.service.pubman.PubItemBatchService#deletePubItems(java.util.Map,
   * java.lang.String, java.lang.String)
   */
  @Override
  public BatchProcessLogDbVO deletePubItems(List<String> pubItemObjectIdList, String message, String authenticationToken,
      AccountUserDbVO accountUser) {
    List<BatchProcessItemVO> resultList = new ArrayList<>();
    BatchProcessLogDbVO resultLog = new BatchProcessLogDbVO(accountUser);
    ItemVersionVO pubItemVO = null;
    for (String itemId : pubItemObjectIdList) {
      pubItemVO = null; // reset pubItemVO
      try {
        pubItemVO = this.pubItemService.get(itemId, authenticationToken);
        if (!ItemVersionRO.State.WITHDRAWN.equals(pubItemVO.getObject().getPublicState())
            && !ItemVersionRO.State.RELEASED.equals(pubItemVO.getObject().getPublicState())) {
          this.pubItemService.delete(itemId, authenticationToken);
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.SUCCESS,
              BatchProcessItemVO.BatchProcessMessagesTypes.SUCCESS));
        } else {
          resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.STATE_WRONG,
              BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
        }
      } catch (IngeTechnicalException e) {
        logger.error("Could not delete item " + itemId + " due to a technical error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthenticationException e) {
        logger.error("Could not delete item " + itemId + " due to an authentication error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHENTICATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (AuthorizationException e) {
        logger.error("Could not delete item " + itemId + " due to an authorization error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.AUTHORIZATION_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (IngeApplicationException e) {
        logger.error("Could not delete item " + itemId + " due to an application error", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.INTERNAL_ERROR,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      } catch (NullPointerException e) {
        logger.error("Could not delete item " + itemId + " due to an nullpointer exception", e);
        resultList.add(new BatchProcessItemVO(pubItemVO, BatchProcessItemVO.BatchProcessMessages.ITEM_NOT_FOUND,
            BatchProcessItemVO.BatchProcessMessagesTypes.ERROR));
      }
    }
    resultLog.setBatchProcessLogItemList(resultList);
    if (this.batchRepository.existsById(accountUser.getObjectId())) {
      this.batchRepository.deleteById(accountUser.getObjectId());
    }
    this.batchRepository.save(resultLog);
    this.batchRepository.flush();
    return resultLog;
  }
}
