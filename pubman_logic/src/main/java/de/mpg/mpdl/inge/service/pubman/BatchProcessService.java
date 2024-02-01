package de.mpg.mpdl.inge.service.pubman;

import java.util.List;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.aa.IpListProvider;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

public interface BatchProcessService {

  public BatchProcessLogHeaderDbVO addKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO addLocalTags(List<String> itemIds, List<String> localTags, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO addSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifier, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeContext(List<String> itemIds, String contextFrom, String contextTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeExternalReferenceContentCategory(List<String> itemIds, String externalReferenceContentCategoryFrom,
      String externalReferenceContentCategoryTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeFileContentCategory(List<String> itemIds, String fileContentCategoryFrom,
      String fileContentCategoryTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeFileVisibility(List<String> itemIds, FileDbVO.Visibility fileVisibilityFrom,
      FileDbVO.Visibility fileVisibilityTo, IpListProvider.IpRange userAccountIpRange, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeGenre(List<String> itemIds, MdsPublicationVO.Genre genreFrom, MdsPublicationVO.Genre genreTo,
      MdsPublicationVO.DegreeType degreeType, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeKeywords(List<String> itemIds, String keywordsFrom, String keywordsTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeLocalTag(List<String> itemIds, String localTagFrom, String localTagTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeReviewMethod(List<String> itemIds, MdsPublicationVO.ReviewMethod reviewMethodFrom,
      MdsPublicationVO.ReviewMethod reviewMethodTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeSourceGenre(List<String> itemIds, SourceVO.Genre sourceGenreFrom, SourceVO.Genre sourceGenreTo,
      String token) throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO changeSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifierFrom, String sourceIdentifierTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public void deleteBatchProcessUserLock(String accountUserObjectId, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public List<BatchProcessLogHeaderDbVO> getAllBatchProcessLogHeaders(String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public List<BatchProcessLogDetailDbVO> getBatchProcessLogDetails(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessLogHeaderDbVO getBatchProcessLogHeader(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException;

  public BatchProcessUserLockDbVO getBatchProcessUserLock(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO releasePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO replaceSourceEdition(List<String> itemIds, int sourceNumber, String edition, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO replaceFileAudience(List<String> itemIds, List<String> audiences, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO replaceKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO replaceOrcid(List<String> itemIds, String creatorId, String orcid, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO revisePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO submitPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;

  public BatchProcessLogHeaderDbVO withdrawPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException;
}
