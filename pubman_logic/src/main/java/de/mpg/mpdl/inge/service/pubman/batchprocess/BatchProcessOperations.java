package de.mpg.mpdl.inge.service.pubman.batchprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO.Method;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessOperations {

  public BatchProcessLogDetailDbVO addLocalTags(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO addSourceIdentifier(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeContentCategory(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeContext(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeFileVisibility(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeGenre(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeKeywords(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeLocalTag(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeReviewMethod(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeSourceGenre(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO changeSourceIdentifier(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO doKeywords(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO replaceEdition(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO replaceFileAudience(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogDetailDbVO replaceOrcid(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
