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

  void addLocalTags(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void addSourceIdentifier(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeContentCategory(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeContext(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeFileVisibility(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeGenre(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeKeywords(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeLocalTag(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeReviewMethod(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeSourceGenre(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeSourceIdentifier(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void doKeywords(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void replaceFileAudience(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void replaceOrcid(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void replaceSourceEdition(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
