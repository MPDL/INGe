package de.mpg.mpdl.inge.service.pubman.batchprocess;

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;

public interface BatchProcessAsyncService {

  public void doAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token, BatchProcessOperations batchOperations);

  public void doPubItemsAsync(BatchProcessLogHeaderDbVO.Method method, BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO,
      AccountUserDbVO accountUserDbVO, List<String> itemIds, String token);
}
