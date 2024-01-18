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

  public BatchProcessUserLockDbVO getBatchProcessUserLock(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public void deleteBatchProcessUserLock(String accountUserObjectId, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO addKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO addLocalTags(List<String> itemIds, List<String> localTags, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO releasePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO revisePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO submitPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO withdrawPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;
}
