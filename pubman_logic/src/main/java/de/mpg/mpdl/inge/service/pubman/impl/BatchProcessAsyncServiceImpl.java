package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.stereotype.Service;

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
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessAsyncService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessOperations;

@Service
@Primary
public class BatchProcessAsyncServiceImpl implements BatchProcessAsyncService, AsyncConfigurer {

  private static final Logger logger = Logger.getLogger(BatchProcessAsyncServiceImpl.class);

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private ContextService contextService;

  @Autowired
  private BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  @Autowired
  private BatchProcessCommonService batchProcessCommonService;

  @Autowired
  private Executor asyncExecutor;

  public BatchProcessAsyncServiceImpl() {}

  @SuppressWarnings("incomplete-switch")
  @Async
  public void doAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token, BatchProcessOperations batchOperations) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO =
            this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING, null);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
            switch (method) {
              case ADD_LOCALTAGS:
                batchProcessLogDetailDbVO = batchOperations.addLocalTags(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case ADD_KEYWORDS, REPLACE_KEYWORDS:
                batchProcessLogDetailDbVO = batchOperations.doKeywords(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case ADD_SOURCE_IDENTIFIER:
                batchProcessLogDetailDbVO = batchOperations.addSourceIdentifier(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case CHANGE_CONTEXT:
                batchProcessLogDetailDbVO = batchOperations.changeContext(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case CHANGE_GENRE:
                batchProcessLogDetailDbVO = batchOperations.changeGenre(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case CHANGE_LOCALTAG:
                batchProcessLogDetailDbVO = batchOperations.changeLocalTag(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY, CHANGE_FILE_CONTENT_CATEGORY:
                batchProcessLogDetailDbVO = batchOperations.changeContentCategory(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case REPLACE_EDITION:
                batchProcessLogDetailDbVO = batchOperations.replaceEdition(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
              case REPLACE_FILE_AUDIENCE:
                batchProcessLogDetailDbVO = batchOperations.replaceFileAudience(method, token, batchProcessLogDetailDbVO, itemVersionVO);
                break;
            }
          } else {
            batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
        } catch (IngeApplicationException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        }
      }
    }

    this.batchProcessCommonService.finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @SuppressWarnings("incomplete-switch")
  @Async
  public void doPubItemsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token) {

    logger.info("Start ASYNC");

    batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVOAndItemObjectId(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
            BatchProcessLogDetailDbVO.State.RUNNING, null);

        ItemVersionVO itemVersionVO = null;
        ContextDbVO contextDbVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else {
            switch (method) {
              case DELETE_PUBITEMS:
                if (!ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())
                    && !ItemVersionRO.State.RELEASED.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO =
                      this.batchProcessCommonService.doPubItem(method, token, itemId, (Date) null, batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                      BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
              case RELEASE_PUBITEMS:
                contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
                if (ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getVersionState())) {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.doPubItem(method, token, itemId,
                      itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else if (ItemVersionRO.State.PENDING.equals(itemVersionVO.getVersionState())
                    && ContextDbVO.Workflow.SIMPLE.equals(contextDbVO.getWorkflow())) {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.doPubItem(method, token, itemId,
                      itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                      BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
              case REVISE_PUBITEMS:
                contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
                if (ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getVersionState())
                    && ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow())
                    && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.doPubItem(method, token, itemId,
                      itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                      BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
              case SUBMIT_PUBITEMS:
                contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
                if ((ItemVersionRO.State.IN_REVISION.equals(itemVersionVO.getVersionState())
                    || ItemVersionRO.State.PENDING.equals(itemVersionVO.getVersionState()))
                    && ContextDbVO.Workflow.STANDARD.equals(contextDbVO.getWorkflow())
                    && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.doPubItem(method, token, itemId,
                      itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                      BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
              case WITHDRAW_PUBITEMS:
                if (ItemVersionRO.State.RELEASED.equals(itemVersionVO.getVersionState())
                    && !ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())) {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.doPubItem(method, token, itemId,
                      itemVersionVO.getModificationDate(), batchProcessLogDetailDbVO);
                } else {
                  batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                      BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.STATE_WRONG);
                }
                break;
            }
          }
        } catch (IngeTechnicalException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR);
        } catch (AuthenticationException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR);
        } catch (AuthorizationException e) {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR);
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

          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, message);
        }
      }
    }

    this.batchProcessCommonService.finishBatchProcessLog(batchProcessLogHeaderDbVO, accountUserDbVO);

    logger.info("End ASYNC");
  }

  @Override
  public Executor getAsyncExecutor() {
    return this.asyncExecutor;
  }
}
