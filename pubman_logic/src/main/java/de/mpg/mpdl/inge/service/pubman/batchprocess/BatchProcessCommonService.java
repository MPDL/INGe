package de.mpg.mpdl.inge.service.pubman.batchprocess;

import java.util.Date;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO.Message;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO.State;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO.Method;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessCommonService {

  public BatchProcessLogDetailDbVO doPubItem(Method method, String token, String itemId, Date modificationDate,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO doUpdatePubItem(Method method, String token, ItemVersionVO itemVersionVO,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO);

  public BatchProcessLogDetailDbVO updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, State state,
      Message message);

  public BatchProcessLogHeaderDbVO updateBatchProcessLogHeader(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO.State state);

}
