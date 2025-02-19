package de.mpg.mpdl.inge.service.pubman.batchprocess;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessOperations {

  void addLocalTags(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void addSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeContentCategory(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeContext(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO, AccountUserDbVO accountUserDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeFileVisibility(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeGenre(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeKeywords(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeLocalTag(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeReviewMethod(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeSourceGenre(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void changeSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void doKeywords(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void replaceFileAudience(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void replaceOrcid(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  void replaceSourceEdition(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
