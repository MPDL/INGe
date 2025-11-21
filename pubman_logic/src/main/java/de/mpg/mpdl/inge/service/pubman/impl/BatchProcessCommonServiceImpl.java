package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessLogHeaderRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessUserLockRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class BatchProcessCommonServiceImpl implements BatchProcessCommonService {

  private final BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  private final BatchProcessLogHeaderRepository batchProcessLogHeaderRepository;

  private final BatchProcessUserLockRepository batchProcessUserLockRepository;

  private final PubItemService pubItemService;

  public BatchProcessCommonServiceImpl(BatchProcessLogDetailRepository batchProcessLogDetailRepository,
      BatchProcessLogHeaderRepository batchProcessLogHeaderRepository, BatchProcessUserLockRepository batchProcessUserLockRepository,
      PubItemService pubItemService) {
    this.batchProcessLogDetailRepository = batchProcessLogDetailRepository;
    this.batchProcessLogHeaderRepository = batchProcessLogHeaderRepository;
    this.batchProcessUserLockRepository = batchProcessUserLockRepository;
    this.pubItemService = pubItemService;
  }

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
        BatchProcessLogDetailDbVO.Message.BATCH_SUCCESS);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void doUpdatePubItem(BatchProcessLogHeaderDbVO.Method method, String token, ItemVersionVO itemVersionVO,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    //    String message = createMessage(method);
    //    if (null != itemVersionVO.getObject().getLocalTags()) {
    //      itemVersionVO.getObject().getLocalTags().add(message);
    //    } else {
    //      itemVersionVO.getObject().setLocalTags(new ArrayList<>(List.of(message)));
    //    }

    this.pubItemService.update(itemVersionVO, token);

    updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.BATCH_SUCCESS);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void doChangeContext(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, String newContextId,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String message = createMessage(method);
    this.pubItemService.changeContext(itemId, newContextId, token, "Batch " + message);

    updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.BATCH_SUCCESS);
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void doUpdateLocalTags(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, List<String> localTags,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String message = createMessage(method);
    this.pubItemService.updateLocalTags(itemId, localTags, token, "Batch " + message);

    updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.BATCH_SUCCESS);
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO, boolean error) {

    if (error) {
      updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.FINISHED_WITH_ERROR);
    } else {
      updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.FINISHED);
    }
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
  public void updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State state,
      BatchProcessLogDetailDbVO.Message message) {

    batchProcessLogDetailDbVO.setState(state);
    if (null != message) {
      batchProcessLogDetailDbVO.setMessage(message);
    }
    batchProcessLogDetailDbVO.setEndDate(new Date());
    batchProcessLogDetailDbVO = this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);
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
        if (null == itemVersionVO) {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId, null,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.BATCH_ITEM_NOT_FOUND, new Date());
        } else {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId, itemVersionVO.getVersionNumber(),
              BatchProcessLogDetailDbVO.State.INITIALIZED, new Date());
        }
      } catch (IngeTechnicalException | RuntimeException | IngeApplicationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            null != itemVersionVO ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_INTERNAL_ERROR, new Date());
      } catch (AuthenticationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            null != itemVersionVO ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_AUTHENTICATION_ERROR, new Date());
      } catch (AuthorizationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            null != itemVersionVO ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_AUTHORIZATION_ERROR, new Date());
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
