package de.mpg.mpdl.inge.service.pubman.batchprocess;

import java.util.Date;
import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessCommonService {

  void doPubItem(BatchProcessLogHeaderDbVO.Method method, String token, String itemId, Date modificationDate,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void doUpdatePubItem(BatchProcessLogHeaderDbVO.Method method, String token, ItemVersionVO itemVersionVO,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void finishBatchProcessLog(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, AccountUserDbVO accountUserDbVO);

  BatchProcessLogHeaderDbVO initializeBatchProcessLog(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      List<String> itemIds, String token);

  void updateBatchProcessLogDetail(BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State state,
      BatchProcessLogDetailDbVO.Message message);

  BatchProcessLogHeaderDbVO updateBatchProcessLogHeader(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO.State state);
}