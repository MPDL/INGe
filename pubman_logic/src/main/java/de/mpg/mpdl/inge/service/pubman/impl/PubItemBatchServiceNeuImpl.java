package de.mpg.mpdl.inge.service.pubman.impl;

import java.time.LocalDateTime;
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
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemBatchServiceNeu;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

@Service
@Primary
public class PubItemBatchServiceNeuImpl implements PubItemBatchServiceNeu {

  private static final Logger logger = Logger.getLogger(PubItemBatchServiceNeuImpl.class);

  @Autowired
  private AuthorizationService authorizationService;

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private BatchProcessUserLockRepository batchProcessUserLockRepository;

  @Autowired
  private BatchProcessLogHeaderRepository batchProcessLogHeaderRepository;

  @Autowired
  private BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  public PubItemBatchServiceNeuImpl() {}

  @Override
  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    Principal principal = authorizationService.checkLoginRequired(token);
    AccountUserDbVO accountUserDbVO = principal.getUserAccount();

    if (null == itemIds || itemIds.isEmpty()) {
      throw new IngeApplicationException("The list of items must not be empty");
    }

    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    if (batchProcessUserLockDbVO != null) {
      throw new IngeApplicationException(
          "User " + accountUserDbVO.getObjectId() + " already locked since " + batchProcessUserLockDbVO.getLockDate());
    }

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        initializeBatchProcessLog(accountUserDbVO, BatchProcessLogHeaderDbVO.Method.DELETE_PUBITEMS, itemIds, token);

    deletePubItemsAsync(batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token);

    return batchProcessLogHeaderDbVO;
  }

  @Async
  public void deletePubItemsAsync(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO,
      List<String> itemIds, String token) {

    batchProcessLogHeaderDbVO = updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.RUNNING);

    for (String itemId : itemIds) {
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO =
          this.batchProcessLogDetailRepository.findByLogHeaderAndItem(batchProcessLogHeaderDbVO, itemId);

      if (BatchProcessLogDetailDbVO.State.INITIALIZED.equals(batchProcessLogDetailDbVO.getState())) {
        batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.RUNNING);

        ItemVersionVO itemVersionVO = null;
        try {
          itemVersionVO = this.pubItemService.get(itemId, token);
          if (null == itemVersionVO) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND);
          } else if (ItemVersionRO.State.WITHDRAWN.equals(itemVersionVO.getObject().getPublicState())
              || ItemVersionRO.State.RELEASED.equals(itemVersionVO.getObject().getPublicState())) {
            batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.STATE_WRONG);
          } else {
            batchProcessLogDetailDbVO = deletePubItem(token, itemId, batchProcessLogDetailDbVO);
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
  }

  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogDetailDbVO deletePubItem(String token, String itemId, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    this.pubItemService.delete(itemId, token);
    batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);

    return batchProcessLogDetailDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogHeaderDbVO initializeBatchProcessLog(AccountUserDbVO accountUserDbVO, BatchProcessLogHeaderDbVO.Method method,
      List<String> itemIds, String token) {

    createBatchProcessUserLock(accountUserDbVO);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = createBatchProcessLogHeader(accountUserDbVO, method, itemIds.size());
    createBatchProcessLogDetails(accountUserDbVO, itemIds, batchProcessLogHeaderDbVO, token);

    return batchProcessLogHeaderDbVO;
  }

  private void createBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = new BatchProcessUserLockDbVO(accountUserDbVO, LocalDateTime.now());
    this.batchProcessUserLockRepository.saveAndFlush(batchProcessUserLockDbVO);
  }

  private BatchProcessLogHeaderDbVO createBatchProcessLogHeader(AccountUserDbVO accountUserDbVO, BatchProcessLogHeaderDbVO.Method method,
      int numberOfItems) {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = new BatchProcessLogHeaderDbVO(accountUserDbVO,
        BatchProcessLogHeaderDbVO.State.INITIALIZED, method, numberOfItems, LocalDateTime.now());
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
}
