package de.mpg.mpdl.inge.service.pubman.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

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
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
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
  public BatchProcessLogDetailDbVO doPubItem(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, Date modificationDate,
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

    batchProcessLogDetailDbVO = updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.SUCCESS,
        BatchProcessLogDetailDbVO.Message.SUCCESS);

    return batchProcessLogDetailDbVO;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public BatchProcessLogDetailDbVO doUpdatePubItem(BatchProcessLogHeaderDbVO.Method method, String token, ItemVersionVO itemVersionVO,
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

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO) {

    updateBatchProcessLogHeader(batchProcessLogHeaderDbVO, BatchProcessLogHeaderDbVO.State.FINISHED);
    removeBatchProcessUserLock(accountUserDbVO);
  }

  @Override
  public BatchProcessLogDetailDbVO updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      BatchProcessLogDetailDbVO.State state, BatchProcessLogDetailDbVO.Message message) {

    batchProcessLogDetailDbVO.setState(state);
    if (message != null) {
      batchProcessLogDetailDbVO.setMessage(message);
    }
    batchProcessLogDetailDbVO.setEndDate(new Date());
    batchProcessLogDetailDbVO = this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO updateBatchProcessLogHeader(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      BatchProcessLogHeaderDbVO.State state) {

    batchProcessLogHeaderDbVO.setState(state);
    batchProcessLogHeaderDbVO.setEndDate(new Date());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
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
