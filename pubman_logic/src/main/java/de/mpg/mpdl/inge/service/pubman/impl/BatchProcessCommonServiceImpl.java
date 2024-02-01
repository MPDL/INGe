package de.mpg.mpdl.inge.service.pubman.impl;

import java.text.SimpleDateFormat;
import java.util.*;

import de.mpg.mpdl.inge.model.db.valueobjects.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessLogHeaderRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessUserLockRepository;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;

@Service
@Primary
public class BatchProcessCommonServiceImpl implements BatchProcessCommonService {

  @Autowired
  private BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  @Autowired
  private BatchProcessLogHeaderRepository batchProcessLogHeaderRepository;

  @Autowired
  private BatchProcessUserLockRepository batchProcessUserLockRepository;

  @Autowired
  private PubItemService pubItemService;

  @Override
  @SuppressWarnings("incomplete-switch")
  @Transactional(rollbackFor = Throwable.class)
  public void doPubItem(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, Date modificationDate,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String message;
    switch (method) {
      case DELETE_PUBITEMS:
        // no message
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

    updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doUpdatePubItem(BatchProcessLogHeaderDbVO.Method method, String token, ItemVersionVO itemVersionVO,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String message = createMessage(method);
    if (itemVersionVO.getObject().getLocalTags() != null) {
      itemVersionVO.getObject().getLocalTags().add(message);
    } else {
      itemVersionVO.getObject().setLocalTags(new ArrayList<>(Arrays.asList(message)));
    }

    this.pubItemService.update(itemVersionVO, token);

    updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO) {

    updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.FINISHED);
    removeBatchProcessUserLock(accountUserDbVO);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public BatchProcessLogHeaderDbVO initializeBatchProcessLog(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      List<String> itemIds, String token) {

    createBatchProcessUserLock(accountUserDbVO);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = createBatchProcessLogHeader(method, accountUserDbVO, itemIds.size());
    createBatchProcessLogDetails(itemIds, batchProcessLogHeaderDbVO, token);

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public void updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      BatchProcessLogDetailDbVO.State state, BatchProcessLogDetailDbVO.Message message) {

    batchProcessLogDetailDbVO.setState(state);
    if (message != null) {
      batchProcessLogDetailDbVO.setMessage(message);
    }
    batchProcessLogDetailDbVO.setEndDate(new Date());
    this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);
  }

  @Override
  public BatchProcessLogHeaderDbVO updateBatchProcessLogHeader(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      BatchProcessLogHeaderDbVO.State state) {

    batchProcessLogHeaderDbVO.setState(state);
    batchProcessLogHeaderDbVO.setEndDate(new Date());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
  }

  private void createBatchProcessLogDetails(List<String> itemIds, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String token) {

    for (String itemId : itemIds) {
      ItemVersionVO itemVersionVO = null;
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO = null;

      try {
        itemVersionVO = this.pubItemService.get(itemId, token);
        if (itemVersionVO == null) {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId, null,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND, new Date());
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

  private BatchProcessLogHeaderDbVO createBatchProcessLogHeader(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      Integer numberOfItems) {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        new BatchProcessLogHeaderDbVO(method, accountUserDbVO, BatchProcessLogHeaderDbVO.State.INITIALIZED, numberOfItems, new Date());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
  }

  private void createBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = new BatchProcessUserLockDbVO(accountUserDbVO, new Date());
    this.batchProcessUserLockRepository.saveAndFlush(batchProcessUserLockDbVO);
  }

  private String createMessage(BatchProcessLogHeaderDbVO.Method method) {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String when = formatter.format(calendar.getTime());
    String message = method.name() + when;

    return message;
  }

  private void removeBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    if (this.batchProcessUserLockRepository.existsById(accountUserDbVO.getObjectId())) {
      this.batchProcessUserLockRepository.deleteById(accountUserDbVO.getObjectId());
      this.batchProcessUserLockRepository.flush();
    }
  }
}
