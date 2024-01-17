package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessLogHeaderRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessUserLockRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessAsyncService;
import de.mpg.mpdl.inge.service.pubman.BatchProcessService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.util.GrantUtil;

@Service
@Primary
public class BatchProcessServiceImpl implements BatchProcessService {

  private static final Logger logger = Logger.getLogger(BatchProcessServiceImpl.class);

  @Autowired
  private AuthorizationService authorizationService;

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private BatchProcessAsyncService batchProcessAsyncService;

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
    this.batchProcessAsyncService.batchPubItemsAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token);
    logger.info("Nach ASYNC");

    return batchProcessLogHeaderDbVO;
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

    if (!GrantUtil.hasRole(accountUserDbVO, PredefinedRoles.MODERATOR)) {
      throw new AuthorizationException("User must be MODERATOR");
    }

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

  private void createBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = new BatchProcessUserLockDbVO(accountUserDbVO, new Date());
    this.batchProcessUserLockRepository.saveAndFlush(batchProcessUserLockDbVO);
  }

  private BatchProcessLogHeaderDbVO createBatchProcessLogHeader(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      Integer numberOfItems) {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        new BatchProcessLogHeaderDbVO(method, accountUserDbVO, BatchProcessLogHeaderDbVO.State.INITIALIZED, numberOfItems, new Date());
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
              BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND, new Date());
        } else {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId, itemVersionVO.getVersionNumber(),
              BatchProcessLogDetailDbVO.State.INITIALIZED, new Date());
        }
      } catch (IngeTechnicalException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, new Date());
      } catch (AuthenticationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR, new Date());
      } catch (AuthorizationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR, new Date());
      } catch (IngeApplicationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, new Date());
      } catch (RuntimeException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, new Date());
      }

      this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);
    }
  }
}
