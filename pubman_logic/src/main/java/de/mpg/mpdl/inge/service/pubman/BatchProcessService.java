package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessService {

  public BatchProcessLogHeaderDbVO getBatchProcessLogHeader(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<BatchProcessLogHeaderDbVO> getAllBatchProcessLogHeaders(String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<BatchProcessLogDetailDbVO> getBatchProcessLogDetails(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessUserLockDbVO getBatchProcessUserLock(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;
}
