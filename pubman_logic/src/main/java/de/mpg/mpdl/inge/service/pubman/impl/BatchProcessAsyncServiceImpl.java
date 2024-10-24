package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportItemVO;
import de.mpg.mpdl.inge.inge_validation.data.ValidationReportVO;
import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.inge_validation.util.ErrorMessages;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessAsyncService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessOperations;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import java.util.List;
import java.util.concurrent.Executor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.stereotype.Service;

@Service
@Primary
public class BatchProcessAsyncServiceImpl implements BatchProcessAsyncService, AsyncConfigurer {

  private static final Logger logger = LogManager.getLogger(BatchProcessAsyncServiceImpl.class);

  private final Executor asyncExecutor;

  private final BatchProcessCommonService batchProcessCommonService;

  private final BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  private final ContextService contextService;

  private final PubItemService pubItemService;

  public BatchProcessAsyncServiceImpl(Executor asyncExecutor, BatchProcessCommonService batchProcessCommonService,
      BatchProcessLogDetailRepository batchProcessLogDetailRepository, ContextService contextService, PubItemService pubItemService) {
    this.asyncExecutor = asyncExecutor;
    this.batchProcessCommonService = batchProcessCommonService;
    this.batchProcessLogDetailRepository = batchProcessLogDetailRepository;
    this.contextService = contextService;
    this.pubItemService = pubItemService;
  }

  @SuppressWarnings("incomplete-switch")
  @Async
  public void doAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token, BatchProcessOperations batchOperations) {

    logger.info("Start ASYNC " + method + ": " + itemIds.size());
    long startTime = System.currentTimeMillis();

    batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING,
            null);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.BATCH_ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            ContextDbVO contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
            if (GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
              switch (method) {
                case ADD_LOCALTAGS:
                  batchOperations.addLocalTags(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case ADD_KEYWORDS, REPLACE_KEYWORDS:
                  batchOperations.doKeywords(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case ADD_SOURCE_IDENTIFIER:
                  batchOperations.addSourceIdentifier(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_CONTEXT:
                  batchOperations.changeContext(method, token, batchProcessLogDetailDbVO, itemVersionVO, accountUserDbVO);
                  break;
                case CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY, CHANGE_FILE_CONTENT_CATEGORY:
                  batchOperations.changeContentCategory(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_FILE_VISIBILITY:
                  batchOperations.changeFileVisibility(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_GENRE:
                  batchOperations.changeGenre(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_KEYWORDS:
                  batchOperations.changeKeywords(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_LOCALTAG:
                  batchOperations.changeLocalTag(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_REVIEW_METHOD:
                  batchOperations.changeReviewMethod(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_SOURCE_GENRE:
                  batchOperations.changeSourceGenre(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case CHANGE_SOURCE_IDENTIFIER:
                  batchOperations.changeSourceIdentifier(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case REPLACE_SOURCE_EDITION:
                  batchOperations.replaceSourceEdition(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case REPLACE_FILE_AUDIENCE:
                  batchOperations.replaceFileAudience(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
                case REPLACE_ORCID:
                  batchOperations.replaceOrcid(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                  break;
              }
            } else {
              this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.BATCH_CONTEXT_AUTHORIZATION_ERROR);
            }
          } else {
            this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.BATCH_STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          BatchProcessLogDetailDbVO.Message message = BatchProcessLogDetailDbVO.Message.BATCH_INTERNAL_ERROR;

          switch (method) {
            case CHANGE_FILE_VISIBILITY:
              if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
                ValidationException validationException = (ValidationException) e.getCause();
                ValidationReportVO validationReport = validationException.getReport();

                if (validationReport.hasItems()) {
                  for (ValidationReportItemVO validationItem : validationReport.getItems()) {
                    if (ErrorMessages.COMPONENT_IP_RANGE_NOT_PROVIDED.equals(validationItem.getContent())) {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_IP_RANGE_NOT_PROVIDED;
                      break;
                    } else {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_GLOBAL;
                      // no break: another report Item could set a finer message
                    }
                  }
                }
              }
              break;
            case CHANGE_GENRE:
              if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
                ValidationException validationException = (ValidationException) e.getCause();
                ValidationReportVO validationReport = validationException.getReport();

                if (validationReport.hasItems()) {
                  for (ValidationReportItemVO validationItem : validationReport.getItems()) {
                    if (ErrorMessages.SOURCE_NOT_PROVIDED.equals(validationItem.getContent())) {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_NO_SOURCE;
                      break;
                    } else {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_GLOBAL;
                      // no break: another report Item could set a finer message
                    }
                  }
                }
              }
              break;
            case REPLACE_ORCID:
              if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
                ValidationException validationException = (ValidationException) e.getCause();
                ValidationReportVO validationReport = validationException.getReport();

                if (validationReport.hasItems()) {
                  for (ValidationReportItemVO validationItem : validationReport.getItems()) {
                    if (ErrorMessages.CREATOR_ORCID_INVALID.equals(validationItem.getContent())) {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_INVALID_ORCID;
                      break;
                    } else {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_GLOBAL;
                      // no break: another report Item could set a finer message
                    }
                  }
                }
              }
              break;
          }

          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              message);
        }
      }

      // TODO remove
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    this.batchProcessCommonService.finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    long endTime = System.currentTimeMillis();
    long durationInSeconds = (endTime - startTime) / 1000;
    logger.info("End ASYNC " + method + "-> " + durationInSeconds + "s");
  }

  @SuppressWarnings("incomplete-switch")
  @Async
  public void doPubItemsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token) {

    logger.info("Start ASYNC " + method + ": " + itemIds.size());
    long startTime = System.currentTimeMillis();

    batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING,
            null);

        ItemVersionVO itemVersionVO = null;
        ContextDbVO contextDbVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.BATCH_ITEM_NOT_FOUND);
          } else {
            contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
            if (GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.MODERATOR, contextDbVO.getObjectId())) {
              switch (method) {
                case DELETE_PUBITEMS:
                  if (!ItemVersionRO.State.WITHDRAWN.equals(
                      itemVersionVO.getObject().getPublicState()) && !ItemVersionRO.State.RELEASED.equals(
                      itemVersionVO.getObject().getPublicState())) {
                    this.batchProcessCommonService.doPubItem(method, token, itemId, null, batchProcessLogDetailDbVO);
                  } else {
                    this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                        BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.BATCH_STATE_WRONG);
                  }
                  break;
                case RELEASE_PUBITEMS:
                  if (ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getVersionState())) {
                    this.batchProcessCommonService.doPubItem(method, token, itemId, itemVersionVO.getModificationDate(),
                        batchProcessLogDetailDbVO);
                  } else if (ItemVersionRO.State.PENDING.equals(itemVersionVO.getVersionState()) && ContextDbVO.Workflow.SIMPLE.equals(
                      contextDbVO.getWorkflow())) {
                    this.batchProcessCommonService.doPubItem(method, token, itemId, itemVersionVO.getModificationDate(),
                        batchProcessLogDetailDbVO);
                  } else {
                    this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                        BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.BATCH_STATE_WRONG);
                  }
                  break;
                case REVISE_PUBITEMS:
                  if (ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getVersionState()) && ContextDbVO.Workflow.STANDARD.equals(
                      contextDbVO.getWorkflow()) && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                    this.batchProcessCommonService.doPubItem(method, token, itemId, itemVersionVO.getModificationDate(),
                        batchProcessLogDetailDbVO);
                  } else {
                    this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                        BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.BATCH_STATE_WRONG);
                  }
                  break;
                case SUBMIT_PUBITEMS:
                  if ((ItemVersionRO.State.IN_REVISION.equals(itemVersionVO.getVersionState()) || ItemVersionRO.State.PENDING.equals(
                      itemVersionVO.getVersionState())) && ContextDbVO.Workflow.STANDARD.equals(
                      contextDbVO.getWorkflow()) && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                    this.batchProcessCommonService.doPubItem(method, token, itemId, itemVersionVO.getModificationDate(),
                        batchProcessLogDetailDbVO);
                  } else {
                    this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                        BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.BATCH_STATE_WRONG);
                  }
                  break;
                case WITHDRAW_PUBITEMS:
                  if (ItemVersionRO.State.RELEASED.equals(itemVersionVO.getVersionState()) && !ItemVersionRO.State.WITHDRAWN.equals(
                      itemVersionVO.getObject().getPublicState())) {
                    this.batchProcessCommonService.doPubItem(method, token, itemId, itemVersionVO.getModificationDate(),
                        batchProcessLogDetailDbVO);
                  } else {
                    this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                        BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.BATCH_STATE_WRONG);
                  }
                  break;
              }
            } else {
              this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.BATCH_CONTEXT_AUTHORIZATION_ERROR);
            }
          }
        } catch (IngeTechnicalException e) {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          BatchProcessLogDetailDbVO.Message message = BatchProcessLogDetailDbVO.Message.BATCH_INTERNAL_ERROR;

          switch (method) {
            case RELEASE_PUBITEMS, SUBMIT_PUBITEMS:
              if (null != e.getCause() && ValidationException.class.equals(e.getCause().getClass())) {
                ValidationException validationException = (ValidationException) e.getCause();
                ValidationReportVO validationReport = validationException.getReport();
                if (validationReport.hasItems()) {
                  for (ValidationReportItemVO validationItem : validationReport.getItems()) {
                    if (ErrorMessages.SOURCE_NOT_PROVIDED.equals(validationItem.getContent())) {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_NO_SOURCE;
                      break;
                    } else {
                      message = BatchProcessLogDetailDbVO.Message.BATCH_VALIDATION_GLOBAL;
                      // no break: another report Item could set a finer message
                    }
                  }
                }
              }
              break;
          }

          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              message);
        }
      }

      // TODO remove
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    this.batchProcessCommonService.finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    long endTime = System.currentTimeMillis();
    long durationInSeconds = (endTime - startTime) / 1000;
    logger.info("End ASYNC " + method + "-> " + durationInSeconds + "s");
  }

  @Override
  public Executor getAsyncExecutor() {
    return this.asyncExecutor;
  }
}
