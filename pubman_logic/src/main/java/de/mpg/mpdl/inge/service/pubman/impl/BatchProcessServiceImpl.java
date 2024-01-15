package de.mpg.mpdl.inge.service.pubman.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
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
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessItemVO.BatchProcessMessages;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessService;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

@Service
@Primary
public class BatchProcessServiceImpl implements BatchProcessService {

  private static final Logger logger = Logger.getLogger(BatchProcessServiceImpl.class);

  @Autowired
  private AuthorizationService authorizationService;

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

  public BatchProcessServiceImpl() {}

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  @Override
  public BatchProcessUserLockDbVO getBatchProcessUserLock(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);
    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    return batchProcessUserLockDbVO;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  @Override
  public BatchProcessLogHeaderDbVO getBatchProcessLogHeader(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository
        .findOneByBatchProcessLogHeaderIdAndUserAccountObjectId(Long.parseLong(batchProcessLogHeaderId), accountUserDbVO.getObjectId());

    return batchProcessLogHeaderDbVO;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  @Override
  public List<BatchProcessLogHeaderDbVO> getAllBatchProcessLogHeaders(String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);
    List<BatchProcessLogHeaderDbVO> batchProcessLogHeaderDbVOs =
        this.batchProcessLogHeaderRepository.findAllByUserAccountObjectId(accountUserDbVO.getObjectId());

    return batchProcessLogHeaderDbVOs;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  @Override
  public List<BatchProcessLogDetailDbVO> getBatchProcessLogDetails(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<BatchProcessLogDetailDbVO> batchProcessLogDetailDbVOs = null;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = getBatchProcessLogHeader(batchProcessLogHeaderId, token);

    if (null != batchProcessLogHeaderDbVO) {
      batchProcessLogDetailDbVOs = this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVO(batchProcessLogHeaderDbVO);
    }

    return batchProcessLogDetailDbVOs;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  @Override
  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return batchPubItems(BatchProcessLogHeaderDbVO.Method.DELETE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO releasePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return batchPubItems(BatchProcessLogHeaderDbVO.Method.RELEASE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO revisePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return batchPubItems(BatchProcessLogHeaderDbVO.Method.REVISE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO submitPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return batchPubItems(BatchProcessLogHeaderDbVO.Method.SUBMIT_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO withdrawPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return batchPubItems(BatchProcessLogHeaderDbVO.Method.WITHDRAW_PUBITEMS, itemIds, token);
  }

  private BatchProcessLogHeaderDbVO batchPubItems(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = commonChecks(itemIds, token);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    logger.info("Vor ASYNC");
    batchPubItemsAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token);
    logger.info("Nach ASYNC");

    return batchProcessLogHeaderDbVO;
  }

  @Async
  public void batchPubItemsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token) {

    logger.info("Start ASYNC");
    logger.info("Starte ASYNC sleep");
    try {
      Thread.currentThread();
      Thread.sleep(50000);
    } catch (InterruptedException e) {
      logger.error("ASYNC thread: " + e);
    }
    logger.info("Beende ASYNC sleep");

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
  }

  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogDetailDbVO doPubItem(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, Date modificationDate,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String when = formatter.format(calendar.getTime());
    String message;

    switch (method) {
      case DELETE_PUBITEMS:
        this.pubItemService.delete(itemId, token);
        break;
      case RELEASE_PUBITEMS:
        message = "batch release " + when;
        this.pubItemService.releasePubItem(itemId, modificationDate, message, token);
        break;
      case REVISE_PUBITEMS:
        message = "batch revise " + when;
        this.pubItemService.revisePubItem(itemId, modificationDate, message, token);
        break;
      case SUBMIT_PUBITEMS:
        message = "batch submit " + when;
        this.pubItemService.submitPubItem(itemId, modificationDate, message, token);
        break;
      case WITHDRAW_PUBITEMS:
        message = "batch withdraw " + when;
        this.pubItemService.withdrawPubItem(itemId, modificationDate, message, token);
        break;
    }

    batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);

    return batchProcessLogDetailDbVO;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private AccountUserDbVO checkUser(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {
    Principal principal = authorizationService.checkLoginRequired(token);

    AccountUserDbVO accountUserDbVO = principal.getUserAccount();

    if (null == accountUserDbVO) {
      throw new IngeApplicationException("Invalid user");
    }

    return accountUserDbVO;
  }

  private AccountUserDbVO commonChecks(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);

    if (null == itemIds || itemIds.isEmpty()) {
      throw new IngeApplicationException("The list of items must not be empty");
    }

    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    if (batchProcessUserLockDbVO != null) {
      throw new IngeApplicationException(
          "User " + accountUserDbVO.getObjectId() + " already locked since " + batchProcessUserLockDbVO.getLockDate());
    }

    return accountUserDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogHeaderDbVO initializeBatchProcessLog(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      List<String> itemIds, String token) {

    createBatchProcessUserLock(accountUserDbVO);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = createBatchProcessLogHeader(method, accountUserDbVO, itemIds.size());
    createBatchProcessLogDetails(accountUserDbVO, itemIds, batchProcessLogHeaderDbVO, token);

    return batchProcessLogHeaderDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  private void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO) {

    updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.FINISHED);
    removeBatchProcessUserLock(accountUserDbVO);
  }

  private void createBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = new BatchProcessUserLockDbVO(accountUserDbVO, LocalDateTime.now());
    this.batchProcessUserLockRepository.saveAndFlush(batchProcessUserLockDbVO);
  }

  private BatchProcessLogHeaderDbVO createBatchProcessLogHeader(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      int numberOfItems) {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = new BatchProcessLogHeaderDbVO(method, accountUserDbVO,
        BatchProcessLogHeaderDbVO.State.INITIALIZED, numberOfItems, LocalDateTime.now());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
  }

  private void createBatchProcessLogDetails(AccountUserDbVO accountUserDbVO, List<String> itemIds,
      BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String token) {

    for (String itemId : itemIds) {
      ItemVersionVO itemVersionVO = null;
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO = null;

      try {
        itemVersionVO = this.pubItemService.get(itemId, token);
        if (null == itemVersionVO) {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
              itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND, LocalDateTime.now());
        } else {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId, itemVersionVO.getVersionNumber(),
              BatchProcessLogDetailDbVO.State.INITIALIZED, LocalDateTime.now());
        }
      } catch (IngeTechnicalException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, LocalDateTime.now());
      } catch (AuthenticationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR, LocalDateTime.now());
      } catch (AuthorizationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR, LocalDateTime.now());
      } catch (IngeApplicationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, LocalDateTime.now());
      }

      this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);
    }
  }

  private BatchProcessLogHeaderDbVO updateBatchProcessLogHeader(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      BatchProcessLogHeaderDbVO.State state) {

    batchProcessLogHeaderDbVO.setState(state);
    batchProcessLogHeaderDbVO.setEndDate(LocalDateTime.now());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
  }

  private BatchProcessLogDetailDbVO updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      BatchProcessLogDetailDbVO.State state) {

    batchProcessLogDetailDbVO.setState(state);
    batchProcessLogDetailDbVO.setEndDate(LocalDateTime.now());
    batchProcessLogDetailDbVO = this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);

    return batchProcessLogDetailDbVO;
  }

  private BatchProcessLogDetailDbVO updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      BatchProcessLogDetailDbVO.State state, BatchProcessLogDetailDbVO.Message message) {

    batchProcessLogDetailDbVO.setState(state);
    batchProcessLogDetailDbVO.setMessage(message);
    batchProcessLogDetailDbVO.setEndDate(LocalDateTime.now());
    batchProcessLogDetailDbVO = this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);

    return batchProcessLogDetailDbVO;
  }

  private void removeBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    if (this.batchProcessUserLockRepository.existsById(accountUserDbVO.getObjectId())) {
      this.batchProcessUserLockRepository.deleteById(accountUserDbVO.getObjectId());
      this.batchProcessUserLockRepository.flush();
    }
  }
}
