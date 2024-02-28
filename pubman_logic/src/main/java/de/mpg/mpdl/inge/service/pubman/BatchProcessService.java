package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessService {

  BatchProcessLogHeaderDbVO addKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO addLocalTags(List<String> itemIds, List<String> localTags, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO addSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifier, String token) throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeContext(List<String> itemIds, String contextFrom, String contextTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeExternalReferenceContentCategory(List<String> itemIds, String externalReferenceContentCategoryFrom,
      String externalReferenceContentCategoryTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeFileContentCategory(List<String> itemIds, String fileContentCategoryFrom, String fileContentCategoryTo,
      String token) throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeFileVisibility(List<String> itemIds, FileDbVO.Visibility fileVisibilityFrom,
      FileDbVO.Visibility fileVisibilityTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeGenre(List<String> itemIds, MdsPublicationVO.Genre genreFrom, MdsPublicationVO.Genre genreTo,
      MdsPublicationVO.DegreeType degreeType, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeKeywords(List<String> itemIds, String keywordsFrom, String keywordsTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeLocalTag(List<String> itemIds, String localTagFrom, String localTagTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeReviewMethod(List<String> itemIds, MdsPublicationVO.ReviewMethod reviewMethodFrom,
      MdsPublicationVO.ReviewMethod reviewMethodTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeSourceGenre(List<String> itemIds, SourceVO.Genre sourceGenreFrom, SourceVO.Genre sourceGenreTo,
      String token) throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO changeSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifierFrom, String sourceIdentifierTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  void deleteBatchProcessUserLock(String accountUserObjectId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  List<BatchProcessLogHeaderDbVO> getAllBatchProcessLogHeaders(String token) throws AuthenticationException, IngeApplicationException;

  List<BatchProcessLogDetailDbVO> getBatchProcessLogDetails(String batchProcessLogHeaderId, String token)
      throws AuthenticationException, IngeApplicationException;

  BatchProcessLogHeaderDbVO getBatchProcessLogHeader(String batchProcessLogHeaderId, String token)
      throws AuthenticationException, IngeApplicationException;

  BatchProcessUserLockDbVO getBatchProcessUserLock(String token) throws AuthenticationException, IngeApplicationException;

  BatchProcessLogHeaderDbVO releasePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO replaceFileAudience(List<String> itemIds, List<String> allowedAudienceIds, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO replaceKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO replaceOrcid(List<String> itemIds, String creatorId, String orcid, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO replaceSourceEdition(List<String> itemIds, int sourceNumber, String edition, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO revisePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO submitPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;

  BatchProcessLogHeaderDbVO withdrawPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException;
}
