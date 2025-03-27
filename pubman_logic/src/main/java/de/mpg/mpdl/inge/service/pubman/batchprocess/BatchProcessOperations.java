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

  boolean addLocalTags(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean addSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeContentCategory(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeContext(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO, AccountUserDbVO accountUserDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeFileVisibility(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeGenre(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeKeywords(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeLocalTag(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeReviewMethod(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeSourceGenre(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean changeSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean doKeywords(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean replaceFileAudience(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean replaceOrcid(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  boolean replaceSourceEdition(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO) throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;
}
