package de.mpg.mpdl.inge.service.pubman.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessLogHeaderRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessUserLockRepository;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessAsyncService;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

@Service
@Primary
public class BatchProcessAsyncServiceImpl implements BatchProcessAsyncService, AsyncConfigurer {

  private static final Logger logger = Logger.getLogger(BatchProcessAsyncServiceImpl.class);

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ContextService contextService;

  @Autowired
  private BatchProcessUserLockRepository batchProcessUserLockRepository;

  @Autowired
  private BatchProcessLogHeaderRepository batchProcessLogHeaderRepository;

  @Autowired
  private BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  @Autowired
  private Executor asyncExecutor;

  public BatchProcessAsyncServiceImpl() {}

  @Async
  public void addLocalTagsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, List<String> localTags, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            List<String> currentLocalTags = itemVersionVO.getObject().getLocalTags();
            currentLocalTags.addAll(localTags);
            itemVersionVO.getObject().setLocalTags(currentLocalTags);
            batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  public void addSourceIdentifierAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifer, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
            if (currentSourceList != null && currentSourceList.size() >= sourceNumber && currentSourceList.get(sourceNumber - 1) != null) {
              if (currentSourceList.get(sourceNumber - 1).getIdentifiers() != null) {
                currentSourceList.get(sourceNumber - 1).getIdentifiers().add(new IdentifierVO(sourceIdentifierType, sourceIdentifer));
                batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
              }
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_NO_SOURCE_FOUND);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void changeContextAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String contextFrom, String contextTo, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            if (itemId != null && contextFrom.equals(itemVersionVO.getObject().getContext().getObjectId())) {
              ContextDbVO contextDbVOTo = this.contextService.get(contextTo, token);
              if (null == contextDbVOTo) {
                batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                    BatchProcessLogDetailDbVO.Message.CONTEXT_NOT_FOUND);
              } else if (itemVersionVO.getMetadata() != null && itemVersionVO.getMetadata().getGenre() != null
                  && contextDbVOTo.getAllowedGenres() != null && !contextDbVOTo.getAllowedGenres().isEmpty()
                  && contextDbVOTo.getAllowedGenres().contains(itemVersionVO.getMetadata().getGenre())) {
                itemVersionVO.getObject().setContext(contextDbVOTo);
                if (!((ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getObject().getPublicState())
                    || ItemVersionRO.State.IN_REVISION.equals(itemVersionVO.getVersionState()))
                    && ContextDbVO.Workflow.SIMPLE.equals(contextDbVOTo.getWorkflow()))) {
                  batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_ALLOWED);
                }
              } else {
                batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                    BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_ALLOWED);
              }
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void changeContentCategoryAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String categoryFrom, String categoryTo, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            boolean anyFilesChanged = false;
            if (BatchProcessLogHeaderDbVO.Method.CHANGE_FILE_CONTENT_CATEGORY.equals(method)) {
              for (FileDbVO file : itemVersionVO.getFiles()) {
                if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                    && file.getMetadata().getContentCategory().equals(categoryFrom)) {
                  file.getMetadata().setContentCategory(categoryTo);
                  anyFilesChanged = true;
                }
              }
            } else if (BatchProcessLogHeaderDbVO.Method.CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY.equals(method)) {
              for (FileDbVO file : itemVersionVO.getFiles()) {
                if (FileDbVO.Storage.EXTERNAL_URL.equals(file.getStorage())
                    && file.getMetadata().getContentCategory().equals(categoryFrom)) {
                  file.getMetadata().setContentCategory(categoryFrom);
                  anyFilesChanged = true;
                }
              }
            }
            if (anyFilesChanged == true) {
              batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void changeGenreAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, MdsPublicationVO.Genre genreFrom, MdsPublicationVO.Genre genreTo,
      MdsPublicationVO.DegreeType degreeType, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            ContextDbVO contextDbVOTo = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
            if (contextDbVOTo.getAllowedGenres() != null && !contextDbVOTo.getAllowedGenres().isEmpty()
                && contextDbVOTo.getAllowedGenres().contains(itemVersionVO.getMetadata().getGenre())) {
              Genre currentPubItemGenre = itemVersionVO.getMetadata().getGenre();
              if (currentPubItemGenre.equals(genreFrom)) {
                if (!genreFrom.equals(genreTo)) {
                  if (!Genre.THESIS.equals(genreTo)) {
                    itemVersionVO.getMetadata().setGenre(genreTo);
                    batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
                  } else if (Genre.THESIS.equals(genreTo) && null != degreeType) {
                    itemVersionVO.getMetadata().setGenre(genreTo);
                    itemVersionVO.getMetadata().setDegree(degreeType);
                    batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
                  } else {
                    batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                        BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_NEW_VALUE_SET);
                  }
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.METADATA_NO_CHANGE_VALUE);
                }
              } else {
                batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                    BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
              }
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_ALLOWED);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void changeLocalTagAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String localTagFrom, String localTagTo, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            if (itemVersionVO.getObject().getLocalTags() != null && itemVersionVO.getObject().getLocalTags().contains(localTagFrom)) {
              List<String> localTagList = itemVersionVO.getObject().getLocalTags();
              localTagList.remove(localTagFrom);
              localTagList.add(localTagTo);
              itemVersionVO.getObject().setLocalTags(localTagList);
              batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_NO_CHANGE_VALUE);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void changeSourceGenreAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, SourceVO.Genre sourceGenreFrom, SourceVO.Genre sourceGenreTo, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            if (!sourceGenreFrom.equals(sourceGenreTo)) {
              List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
              boolean sourceChanged = false;
              for (SourceVO currentSource : currentSourceList) {
                SourceVO.Genre currentSourceGenre = currentSource.getGenre();
                if (currentSourceGenre.equals(sourceGenreFrom)) {
                  currentSource.setGenre(sourceGenreTo);
                  sourceChanged = true;
                }
                if (sourceChanged == true) {
                  batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
                }
              }
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_NO_CHANGE_VALUE);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void doKeywordsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String keywords, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            String currentKeywords = null;
            if (BatchProcessLogHeaderDbVO.Method.ADD_KEYWORDS.equals(method)
                && (currentKeywords = itemVersionVO.getMetadata().getFreeKeywords()) != null) {
              if (currentKeywords.contains(",")) {
                itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + ", " + keywords);
              } else if (currentKeywords.contains(";")) {
                itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + "; " + keywords);
              } else if (currentKeywords.contains(" ")) {
                itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + " " + keywords);
              } else {
                itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + ", " + keywords);
              }
            } else {
              itemVersionVO.getMetadata().setFreeKeywords(keywords);
            }
            batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @SuppressWarnings("incomplete-switch")
  @Async
  public void doPubItemsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        ContextDbVO contextDbVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else {
            switch (method) {
              case DELETE_PUBITEMS:
                if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())
                    && !ItemVersionRO.State.RELEASED.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO = doPubItem(method, token, itemId, (Date) null, batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
              case RELEASE_PUBITEMS:
                contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
                if (ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getVersionState())) {
                  batchProcessLogDetailDbVO =
                      doPubItem(method, token, itemId, itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else if (ItemVersionRO.State.PENDING.equals(itemVersionVO.getVersionState())
                    && ContextDbVO.Workflow.SIMPLE.equals(contextDbVO.getWorkflow())) {
                  batchProcessLogDetailDbVO =
                      doPubItem(method, token, itemId, itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
              case REVISE_PUBITEMS:
                contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
                if (ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getVersionState())
                    && ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow())
                    && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO =
                      doPubItem(method, token, itemId, itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
              case SUBMIT_PUBITEMS:
                contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
                if ((ItemVersionRO.State.IN_REVISION.equals(itemVersionVO.getVersionState())
                    || ItemVersionRO.State.PENDING.equals(itemVersionVO.getVersionState()))
                    && ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow())
                    && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO =
                      doPubItem(method, token, itemId, itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
              case WITHDRAW_PUBITEMS:
                if (ItemVersionRO.State.RELEASED.equals(itemVersionVO.getVersionState())
                    && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO =
                      doPubItem(method, token, itemId, itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                      BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
            }
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          BatchProcessLogDetailDbVO.Message message = BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR;

          switch (method) {
            case RELEASE_PUBITEMS:
            case SUBMIT_PUBITEMS:
              if (e.getCause() != null && ValidationException.class.equals(e.getCause().getClass())) {
                ValidationException validationException = (ValidationException) e.getCause();
                ValidationReportVO validationReport = validationException.getReport();
                if (validationReport.hasItems()) {
                  for (ValidationReportItemVO validationItem : validationReport.getItems()) {
                    if (ErrorMessages.SOURCE_NOT_PROVIDED.equals(validationItem.getContent())) {
                      message = BatchProcessLogDetailDbVO.Message.VALIDATION_NO_SOURCE;
                      break;
                    } else {
                      message = BatchProcessLogDetailDbVO.Message.VALIDATION_GLOBAL;
                      // no break: anther report Item could set a finer message
                    }
                  }
                }
              }
              break;
          }

          batchProcessLogDetailDbVO =
              updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR, message);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Override
  public Executor getAsyncExecutor() {
    return this.asyncExecutor;
  }

  public void replaceEditionAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, int sourceNumber, String edition, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
            if (currentSourceList != null && currentSourceList.size() >= sourceNumber && currentSourceList.get(sourceNumber - 1) != null) {
              if (currentSourceList.get(sourceNumber - 1).getPublishingInfo() != null) {
                currentSourceList.get(sourceNumber - 1).getPublishingInfo().setEdition(edition);
                batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
              } else {
                currentSourceList.get(sourceNumber - 1).setPublishingInfo(new PublishingInfoVO());
                currentSourceList.get(sourceNumber - 1).getPublishingInfo().setEdition(edition);
                batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
              }
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.METADATA_NO_SOURCE_FOUND);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Async
  public void replaceFileAudienceAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, List<String> audiences, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            boolean anyFilesChanged = false;
            for (FileDbVO file : itemVersionVO.getFiles()) {
              List<String> audienceList = file.getAllowedAudienceIds() != null ? file.getAllowedAudienceIds() : new ArrayList<String>();
              if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
                  && FileDbVO.Visibility.AUDIENCE.equals(file.getVisibility())) {
                audienceList.clear();
                audienceList.addAll(audiences);
                file.setAllowedAudienceIds(audienceList);
                anyFilesChanged = true;
              }
            }
            if (anyFilesChanged == true) {
              batchProcessLogDetailDbVO = doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
            } else {
              batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.FILES_METADATA_OLD_VALUE_NOT_EQUAL);
            }
          } else {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private String createMessage(BatchProcessLogHeaderDbVO.Method method) {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String when = formatter.format(calendar.getTime());
    String message = method.name() + when;

    return message;
  }

  @SuppressWarnings("incomplete-switch")
  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogDetailDbVO doPubItem(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, Date modificationDate,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String message;
    switch (method) {
      case DELETE_PUBITEMS:
        this.pubItemService.delete(itemId, token);
        break;
      case RELEASE_PUBITEMS:
        message = createMessage(method);
        this.pubItemService.releasePubItem(itemId, modificationDate, message, token);
        break;
      case REVISE_PUBITEMS:
        message = createMessage(method);
        this.pubItemService.revisePubItem(itemId, modificationDate, message, token);
        break;
      case SUBMIT_PUBITEMS:
        message = createMessage(method);
        this.pubItemService.submitPubItem(itemId, modificationDate, message, token);
        break;
      case WITHDRAW_PUBITEMS:
        message = createMessage(method);
        this.pubItemService.withdrawPubItem(itemId, modificationDate, message, token);
        break;
    }

    batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);

    return batchProcessLogDetailDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogDetailDbVO doUpdatePubItem(BatchProcessLogHeaderDbVO.Method method, String token, ItemVersionVO itemVersionVO,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String message = createMessage(method);
    if (itemVersionVO.getObject().getLocalTags() != null) {
      itemVersionVO.getObject().getLocalTags().add(message);
    } else {
      itemVersionVO.getObject().setLocalTags(new ArrayList<String>(Arrays.asList(message)));
    }

    this.pubItemService.update(itemVersionVO, token);

    batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);

    return batchProcessLogDetailDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  private void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO) {

    updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.FINISHED);
    removeBatchProcessUserLock(accountUserDbVO);
  }

  private void removeBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    if (this.batchProcessUserLockRepository.existsById(accountUserDbVO.getObjectId())) {
      this.batchProcessUserLockRepository.deleteById(accountUserDbVO.getObjectId());
      this.batchProcessUserLockRepository.flush();
    }
  }

  private BatchProcessLogDetailDbVO updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      BatchProcessLogDetailDbVO.State state) {

    this.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, state, null);

    return batchProcessLogDetailDbVO;
  }

  private BatchProcessLogDetailDbVO updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      BatchProcessLogDetailDbVO.State state, BatchProcessLogDetailDbVO.Message message) {

    batchProcessLogDetailDbVO.setState(state);
    if (null != message) {
      batchProcessLogDetailDbVO.setMessage(message);
    }
    batchProcessLogDetailDbVO.setEndDate(new Date());
    batchProcessLogDetailDbVO = this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);

    return batchProcessLogDetailDbVO;
  }

  private BatchProcessLogHeaderDbVO updateBatchProcessLogHeader(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      BatchProcessLogHeaderDbVO.State state) {

    batchProcessLogHeaderDbVO.setState(state);
    batchProcessLogHeaderDbVO.setEndDate(new Date());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
  }
}
