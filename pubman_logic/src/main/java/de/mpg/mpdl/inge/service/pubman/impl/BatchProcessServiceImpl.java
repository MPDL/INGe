package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessLogHeaderRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessUserLockRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessService;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessAsyncService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;
import de.mpg.mpdl.inge.service.util.GrantUtil;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class BatchProcessServiceImpl implements BatchProcessService {

  private static final Logger logger = LogManager.getLogger(BatchProcessServiceImpl.class);

  private final AuthorizationService authorizationService;
  private final BatchProcessAsyncService batchProcessAsyncService;
  private final BatchProcessCommonService batchProcessCommonService;
  private final BatchProcessLogDetailRepository batchProcessLogDetailRepository;
  private final BatchProcessLogHeaderRepository batchProcessLogHeaderRepository;
  private final BatchProcessUserLockRepository batchProcessUserLockRepository;
  private final ContextService contextService;

  public BatchProcessServiceImpl(AuthorizationService authorizationService, BatchProcessAsyncService batchProcessAsyncService,
      BatchProcessCommonService batchProcessCommonService, BatchProcessLogDetailRepository batchProcessLogDetailRepository,
      BatchProcessLogHeaderRepository batchProcessLogHeaderRepository, BatchProcessUserLockRepository batchProcessUserLockRepository,
      ContextService contextService) {
    this.authorizationService = authorizationService;
    this.batchProcessAsyncService = batchProcessAsyncService;
    this.batchProcessCommonService = batchProcessCommonService;
    this.batchProcessLogDetailRepository = batchProcessLogDetailRepository;
    this.batchProcessLogHeaderRepository = batchProcessLogHeaderRepository;
    this.batchProcessUserLockRepository = batchProcessUserLockRepository;
    this.contextService = contextService;
  }

  @Override
  public BatchProcessLogHeaderDbVO addKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeApplicationException {

    return doKeywords(BatchProcessLogHeaderDbVO.Method.ADD_KEYWORDS, itemIds, keywords, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO addLocalTags(List<String> itemIds, List<String> localTags, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkList(localTags, "localTags");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.ADD_LOCALTAGS;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setLocalTags(localTags);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("NACH ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO addSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifier, String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkInt(sourceNumber, "sourceNumber");
    checkEnum(sourceIdentifierType, "sourceIdentifierType");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.ADD_SOURCE_IDENTIFIER;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setSourceNumber(sourceNumber);
    batchOperationsImpl.setSourceIdentifierType(sourceIdentifierType);
    batchOperationsImpl.setSourceIdentifier(sourceIdentifier);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeContext(List<String> itemIds, String contextFrom, String contextTo, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkString(contextFrom, "contextFrom");
    checkString(contextTo, "contextTo");
    checkEquals(contextFrom, contextTo, "contextFrom", "contextTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_CONTEXT;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setContextFrom(contextFrom);
    batchOperationsImpl.setContextTo(contextTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeExternalReferenceContentCategory(List<String> itemIds, String externalReferenceContentCategoryFrom,
      String externalReferenceContentCategoryTo, String token) throws AuthenticationException, IngeApplicationException {

    return doChangeContentCategory(BatchProcessLogHeaderDbVO.Method.CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY, itemIds,
        externalReferenceContentCategoryFrom, externalReferenceContentCategoryTo, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO changeFileContentCategory(List<String> itemIds, String fileContentCategoryFrom,
      String fileContentCategoryTo, String token) throws AuthenticationException, IngeApplicationException {

    return doChangeContentCategory(BatchProcessLogHeaderDbVO.Method.CHANGE_FILE_CONTENT_CATEGORY, itemIds, fileContentCategoryFrom,
        fileContentCategoryTo, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO changeFileVisibility(List<String> itemIds, FileDbVO.Visibility fileVisibilityFrom,
      FileDbVO.Visibility fileVisibilityTo, String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkVisibility(fileVisibilityFrom, "fileVisibilityFrom");
    checkVisibility(fileVisibilityTo, "fileVisibilityTo");
    checkEquals(fileVisibilityFrom, fileVisibilityTo, "fileVisibilityFrom", "fileVisibilityTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_FILE_VISIBILITY;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setVisibilityFrom(fileVisibilityFrom);
    batchOperationsImpl.setVisibilityTo(fileVisibilityTo);

    String userIpListId = this.authorizationService.getUserIpListIdFromToken(token);
    batchOperationsImpl.setUserIpListId(userIpListId);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeGenre(List<String> itemIds, MdsPublicationVO.Genre genreFrom, MdsPublicationVO.Genre genreTo,
      MdsPublicationVO.DegreeType degreeType, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkGenre(genreFrom, "genreFrom");
    checkGenre(genreTo, "genreTo");
    checkEquals(genreFrom, genreTo, "genreFrom", "genreTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_GENRE;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setGenreFrom(genreFrom);
    batchOperationsImpl.setGenreTo(genreTo);
    batchOperationsImpl.setDegreeType(degreeType);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeKeywords(List<String> itemIds, String keywordsFrom, String keywordsTo, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkString(keywordsFrom, "keywordsFrom");
    checkString(keywordsTo, "keywordsTo");
    checkEquals(keywordsFrom, keywordsTo, "keywordsFrom", "keywordsTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_KEYWORDS;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setKeywordsFrom(keywordsFrom);
    batchOperationsImpl.setKeywordsTo(keywordsTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeLocalTag(List<String> itemIds, String localTagFrom, String localTagTo, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkString(localTagFrom, "localTagFrom");
    checkString(localTagTo, "localTagTo");
    checkEquals(localTagFrom, localTagTo, "localTagFrom", "localTagTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_LOCALTAG;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setLocalTagFrom(localTagFrom);
    batchOperationsImpl.setLocalTagTo(localTagTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeReviewMethod(List<String> itemIds, MdsPublicationVO.ReviewMethod reviewMethodFrom,
      MdsPublicationVO.ReviewMethod reviewMethodTo, String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    // noCheck: null values allowed
    checkEquals(reviewMethodFrom, reviewMethodTo, "reviewMethodFrom", "reviewMethodTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_REVIEW_METHOD;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setReviewMethodFrom(reviewMethodFrom);
    batchOperationsImpl.setReviewMethodTo(reviewMethodTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeSourceGenre(List<String> itemIds, SourceVO.Genre sourceGenreFrom, SourceVO.Genre sourceGenreTo,
      String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkSourceGenre(sourceGenreFrom, "sourceGenreFrom");
    checkSourceGenre(sourceGenreTo, "sourceGenreTo");
    checkEquals(sourceGenreFrom, sourceGenreTo, "sourceGenreFrom", "sourceGenreTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_SOURCE_GENRE;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setSourceGenreFrom(sourceGenreFrom);
    batchOperationsImpl.setSourceGenreTo(sourceGenreTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifierFrom, String sourceIdentifierTo, String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkInt(sourceNumber, "sourceNumber");
    checkEnum(sourceIdentifierType, "sourceIdentifierType");
    // noCheck: null values allowed
    checkEquals(sourceIdentifierFrom, sourceIdentifierTo, "sourceIdentifierFrom", "sourceIdentifierTo");


    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_SOURCE_IDENTIFIER;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setSourceNumber(sourceNumber);
    batchOperationsImpl.setSourceIdentifierType(sourceIdentifierType);
    batchOperationsImpl.setSourceIdentifierFrom(sourceIdentifierFrom);
    batchOperationsImpl.setSourceIdentifierTo(sourceIdentifierTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public void deleteBatchProcessUserLock(String accountUserObjectId, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException, NoSuchElementException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    if (!GrantUtil.hasRole(accountUserDbVO, GrantVO.PredefinedRoles.SYSADMIN)) {
      throw new AuthorizationException("User must be SYSADMIN");
    }

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = this.batchProcessUserLockRepository.findById(accountUserObjectId).orElseThrow();
    this.batchProcessUserLockRepository.delete(batchProcessUserLockDbVO);
  }

  @Override
  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.DELETE_PUBITEMS, itemIds, token);
  }

  @Override
  public List<BatchProcessLogHeaderDbVO> getAllBatchProcessLogHeaders(String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    List<BatchProcessLogHeaderDbVO> batchProcessLogHeaderDbVOs =
        this.batchProcessLogHeaderRepository.findAllByUserAccountObjectId(accountUserDbVO.getObjectId());

    for (BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO : batchProcessLogHeaderDbVOs) {
      if (null != batchProcessLogHeaderDbVO) {
        setPercentageOfProcessedItems(batchProcessLogHeaderDbVO);
      }
    }

    return batchProcessLogHeaderDbVOs;
  }

  @Override
  public List<BatchProcessLogDetailDbVO> getBatchProcessLogDetails(String batchProcessLogHeaderId, String token)
      throws AuthenticationException, IngeApplicationException {

    List<BatchProcessLogDetailDbVO> batchProcessLogDetailDbVOs = null;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = getBatchProcessLogHeader(batchProcessLogHeaderId, token);

    if (null != batchProcessLogHeaderDbVO) {
      batchProcessLogDetailDbVOs = this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVO(batchProcessLogHeaderDbVO);
    }

    return batchProcessLogDetailDbVOs;
  }

  @Override
  public BatchProcessLogHeaderDbVO getBatchProcessLogHeader(String batchProcessLogHeaderId, String token)
      throws AuthenticationException, IngeApplicationException {

    this.authorizationService.getUserAccountFromToken(token);

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessLogHeaderRepository.findById(batchProcessLogHeaderId).orElse(null);

    if (null != batchProcessLogHeaderDbVO) {
      setPercentageOfProcessedItems(batchProcessLogHeaderDbVO);
    }

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessUserLockDbVO getBatchProcessUserLock(String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    return batchProcessUserLockDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO releasePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.RELEASE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceFileAudience(List<String> itemIds, List<String> allowedAudienceIds, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkList(allowedAudienceIds, "allowedAudienceIds");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.REPLACE_FILE_AUDIENCE;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setAllowedAudienceIds(allowedAudienceIds);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeApplicationException, AuthorizationException {

    return doKeywords(BatchProcessLogHeaderDbVO.Method.REPLACE_KEYWORDS, itemIds, keywords, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceOrcid(List<String> itemIds, String creatorId, String orcid, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkString(creatorId, "creatorId");
    checkString(orcid, "orcid");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.REPLACE_ORCID;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setCreatorId(creatorId);
    batchOperationsImpl.setOrcid(orcid);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceSourceEdition(List<String> itemIds, int sourceNumber, String edition, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkInt(sourceNumber, "sourceNumber");
    checkString(edition, "edition");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.REPLACE_SOURCE_EDITION;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setSourceNumber(sourceNumber);
    batchOperationsImpl.setEdition(edition);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO revisePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.REVISE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO submitPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.SUBMIT_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO withdrawPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.WITHDRAW_PUBITEMS, itemIds, token);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private AccountUserDbVO checkCommon(AccountUserDbVO accountUserDbVO, List<String> itemIds) throws IngeApplicationException {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    if (null != batchProcessUserLockDbVO) {
      throw new IngeApplicationException(
          "User " + accountUserDbVO.getObjectId() + " already locked since " + batchProcessUserLockDbVO.getLockDate());
    }

    checkList(itemIds, "itemIds");

    return accountUserDbVO;
  }

  private void checkEnum(IdentifierVO.IdType identifierType, String name) throws IngeApplicationException {
    if (null == identifierType) {
      throw new IngeApplicationException("The identifierType " + name + " must not be empty");
    }
  }

  private void checkEquals(Object object1, Object object2, String name1, String name2) throws IngeApplicationException {
    if (object1.equals(object2)) {
      throw new IngeApplicationException("The object " + name1 + " must not be equal to " + name2);
    }
  }

  private void checkGenre(MdsPublicationVO.Genre genre, String name) throws IngeApplicationException {
    if (null == genre) {
      throw new IngeApplicationException("The genre " + name + " must not be empty");
    }
  }

  private void checkInt(int number, String name) throws IngeApplicationException {
    if (0 > number) {
      throw new IngeApplicationException("The number " + name + " must not be negative");
    }
  }

  private void checkList(List<String> list, String name) throws IngeApplicationException {
    if (null == list || list.isEmpty()) {
      throw new IngeApplicationException("The list " + name + " must not be empty");
    }
  }

  private void checkSourceGenre(SourceVO.Genre sourceGenre, String name) throws IngeApplicationException {
    if (null == sourceGenre) {
      throw new IngeApplicationException("The sourceGenre " + name + " must not be empty");
    }
  }

  private void checkString(String string, String name) throws IngeApplicationException {
    if (null == string || string.trim().isEmpty()) {
      throw new IngeApplicationException("The string " + name + " must not be empty");
    }
  }

  private void checkVisibility(FileDbVO.Visibility visibility, String name) throws IngeApplicationException {
    if (null == visibility) {
      throw new IngeApplicationException("The visibility " + name + " must not be empty");
    }
  }

  private BatchProcessLogHeaderDbVO doChangeContentCategory(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds,
      String contentCategoryFrom, String contentCategoryTo, String token) throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkString(contentCategoryFrom, "contentCategoryFrom");
    checkString(contentCategoryTo, "contentCategoryTo");
    checkEquals(contentCategoryFrom, contentCategoryTo, "contentCategoryFrom", "contentCategoryTo");

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setContentCategoryFrom(contentCategoryFrom);
    batchOperationsImpl.setCategoryTo(contentCategoryTo);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  private BatchProcessLogHeaderDbVO doKeywords(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);
    checkString(keywords, "keywords");

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl(this.batchProcessCommonService, this.contextService);
    batchOperationsImpl.setKeywords(keywords);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  private BatchProcessLogHeaderDbVO doPubItems(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds, String token)
      throws AuthenticationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);
    checkCommon(accountUserDbVO, itemIds);

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessCommonService.initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    logger.info("Vor ASYNC Call " + method + ": " + itemIds.size());
    this.batchProcessAsyncService.doPubItemsAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token);
    logger.info("Nach ASYNC Call " + method + ": " + itemIds.size());

    return batchProcessLogHeaderDbVO;
  }

  private void setPercentageOfProcessedItems(BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO) {
    switch (batchProcessLogHeaderDbVO.getState()) {
      case INITIALIZED -> batchProcessLogHeaderDbVO.setPercentageOfProcessedItems(0);
      case FINISHED -> batchProcessLogHeaderDbVO.setPercentageOfProcessedItems(100);
      case FINISHED_WITH_ERROR -> batchProcessLogHeaderDbVO.setPercentageOfProcessedItems(100);
      case RUNNING -> batchProcessLogHeaderDbVO.setPercentageOfProcessedItems(this.batchProcessLogDetailRepository.countProcessedItems(
          batchProcessLogHeaderDbVO.getBatchLogHeaderId()) * 100 / batchProcessLogHeaderDbVO.getNumberOfItems());
    }
  }
}
