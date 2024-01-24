package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.mpg.mpdl.inge.db.repository.BatchProcessLogDetailRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessLogHeaderRepository;
import de.mpg.mpdl.inge.db.repository.BatchProcessUserLockRepository;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO.PredefinedRoles;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.aa.Principal;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessAsyncService;
import de.mpg.mpdl.inge.service.util.GrantUtil;

@Service
@Primary
public class BatchProcessServiceImpl implements BatchProcessService {

  private static final Logger logger = Logger.getLogger(BatchProcessServiceImpl.class);

  @Autowired
  private AuthorizationService authorizationService;

  @Autowired
  private BatchProcessAsyncService batchProcessAsyncService;

  @Autowired
  private BatchProcessLogDetailRepository batchProcessLogDetailRepository;

  @Autowired
  private BatchProcessLogHeaderRepository batchProcessLogHeaderRepository;

  @Autowired
  private BatchProcessUserLockRepository batchProcessUserLockRepository;

  @Autowired
  private PubItemService pubItemService;

  public BatchProcessServiceImpl() {}

  @Override
  public BatchProcessLogHeaderDbVO addKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doKeywords(BatchProcessLogHeaderDbVO.Method.ADD_KEYWORDS, itemIds, keywords, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO addLocalTags(List<String> itemIds, List<String> localTags, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkList(localTags, "localTags");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.ADD_LOCALTAGS;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setLocalTags(localTags);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO addSourceIdentifier(List<String> itemIds, int sourceNumber, IdentifierVO.IdType sourceIdentifierType,
      String sourceIdentifier, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkInt(sourceNumber, "sourceNumber");
    checkEnum(sourceIdentifierType, "sourceIdentifierType");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.ADD_SOURCE_IDENTIFIER;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setSourceNumber(sourceNumber);
    batchOperationsImpl.setSourceIdentifierType(sourceIdentifierType);
    batchOperationsImpl.setSourceIdentifer(sourceIdentifier);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeContext(List<String> itemIds, String contextFrom, String contextTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkString(contextFrom, "contextFrom");
    checkString(contextTo, "contextTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_CONTEXT;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setContextFrom(contextFrom);
    batchOperationsImpl.setContextTo(contextTo);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeExternalReferenceContentCategory(List<String> itemIds, String externalReferenceContentCategoryFrom,
      String externalReferenceContentCategoryTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doChangeContentCategory(BatchProcessLogHeaderDbVO.Method.CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY, itemIds,
        externalReferenceContentCategoryFrom, externalReferenceContentCategoryTo, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO changeFileContentCategory(List<String> itemIds, String fileContentCategoryFrom,
      String fileContentCategoryTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doChangeContentCategory(BatchProcessLogHeaderDbVO.Method.CHANGE_FILE_CONTENT_CATEGORY, itemIds, fileContentCategoryFrom,
        fileContentCategoryTo, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO changeGenre(List<String> itemIds, MdsPublicationVO.Genre genreFrom, MdsPublicationVO.Genre genreTo,
      MdsPublicationVO.DegreeType degreeType, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkGenre(genreFrom, "genreFrom");
    checkGenre(genreTo, "genreTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_GENRE;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setGenreFrom(genreFrom);
    batchOperationsImpl.setGenreTo(genreTo);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeLocalTag(List<String> itemIds, String localTagFrom, String localTagTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkString(localTagFrom, "localTagFrom");
    checkString(localTagTo, "localTagTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_LOCALTAG;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setLocalTagFrom(localTagFrom);
    batchOperationsImpl.setLocalTagTo(localTagTo);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO changeSourceGenre(List<String> itemIds, SourceVO.Genre sourceGenreFrom, SourceVO.Genre sourceGenreTo,
      String token) throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkSourceGenre(sourceGenreFrom, "sourceGenreFrom");
    checkSourceGenre(sourceGenreTo, "sourceGenreTo");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.CHANGE_SOURCE_GENRE;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setSourceGenreFrom(sourceGenreFrom);
    batchOperationsImpl.setSourceGenreTo(sourceGenreTo);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public void deleteBatchProcessUserLock(String accountUserObjectId, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException, NoSuchElementException {

    AccountUserDbVO accountUserDbVO = checkUser(token);

    if (!GrantUtil.hasRole(accountUserDbVO, PredefinedRoles.SYSADMIN)) {
      throw new AuthorizationException("User must be SYSADMIN");
    }

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = this.batchProcessUserLockRepository.findById(accountUserObjectId).orElseThrow();
    this.batchProcessUserLockRepository.delete(batchProcessUserLockDbVO);
  }

  @Override
  public BatchProcessLogHeaderDbVO deletePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.DELETE_PUBITEMS, itemIds, token);
  }

  @Override
  public List<BatchProcessLogHeaderDbVO> getAllBatchProcessLogHeaders(String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);

    List<BatchProcessLogHeaderDbVO> batchProcessLogHeaderDbVOs =
        this.batchProcessLogHeaderRepository.findAllByUserAccountObjectId(accountUserDbVO.getObjectId());

    return batchProcessLogHeaderDbVOs;
  }

  @Override
  public List<BatchProcessLogDetailDbVO> getBatchProcessLogDetails(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<BatchProcessLogDetailDbVO> batchProcessLogDetailDbVOs = null;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = getBatchProcessLogHeader(batchProcessLogHeaderId, token);

    if (null != batchProcessLogHeaderDbVO) {
      batchProcessLogDetailDbVOs = this.batchProcessLogDetailRepository.findByBatchProcessLogHeaderDbVO(batchProcessLogHeaderDbVO);
    }

    return batchProcessLogDetailDbVOs;
  }

  @Override
  public BatchProcessLogHeaderDbVO getBatchProcessLogHeader(String batchProcessLogHeaderId, String token)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository
        .findOneByBatchProcessLogHeaderIdAndUserAccountObjectId(Long.parseLong(batchProcessLogHeaderId), accountUserDbVO.getObjectId());

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessUserLockDbVO getBatchProcessUserLock(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);
    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    return batchProcessUserLockDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO releasePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.RELEASE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceEdition(List<String> itemIds, int sourceNumber, String edition, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkInt(sourceNumber, "sourceNumber");
    checkString(edition, "edition");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.REPLACE_EDITION;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setSourceNumber(sourceNumber);
    batchOperationsImpl.setEdition(edition);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceFileAudience(List<String> itemIds, List<String> audiences, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkList(audiences, "audiences");

    BatchProcessLogHeaderDbVO.Method method = BatchProcessLogHeaderDbVO.Method.REPLACE_FILE_AUDIENCE;
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setAudiences(audiences);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Override
  public BatchProcessLogHeaderDbVO replaceKeywords(List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doKeywords(BatchProcessLogHeaderDbVO.Method.REPLACE_KEYWORDS, itemIds, keywords, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO revisePubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.REVISE_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO submitPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.SUBMIT_PUBITEMS, itemIds, token);
  }

  @Override
  public BatchProcessLogHeaderDbVO withdrawPubItems(List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    return doPubItems(BatchProcessLogHeaderDbVO.Method.WITHDRAW_PUBITEMS, itemIds, token);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private AccountUserDbVO checkCommon(String token, List<String> itemIds)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkUser(token);

    if (!GrantUtil.hasRole(accountUserDbVO, PredefinedRoles.MODERATOR)) {
      throw new AuthorizationException("User must be MODERATOR");
    }

    BatchProcessUserLockDbVO batchProcessUserLockDbVO =
        this.batchProcessUserLockRepository.findById(accountUserDbVO.getObjectId()).orElse(null);

    if (batchProcessUserLockDbVO != null) {
      throw new IngeApplicationException(
          "User " + accountUserDbVO.getObjectId() + " already locked since " + batchProcessUserLockDbVO.getLockDate());
    }

    checkList(itemIds, "itemIds");

    return accountUserDbVO;
  }

  private void checkEnum(IdentifierVO.IdType identifierType, String message) throws IngeApplicationException {
    if (null == identifierType) {
      throw new IngeApplicationException("The identiferType " + message + " must not be empty");
    }
  }

  private void checkGenre(MdsPublicationVO.Genre genre, String message) throws IngeApplicationException {
    if (null == genre) {
      throw new IngeApplicationException("The genre " + message + " must not be empty");
    }
  }

  private void checkInt(int number, String message) throws IngeApplicationException {
    if (number < 0) {
      throw new IngeApplicationException("The number " + message + " must not be negative");
    }
  }

  private void checkList(List<String> list, String message) throws IngeApplicationException {
    if (null == list || list.isEmpty()) {
      throw new IngeApplicationException("The list " + message + " must not be empty");
    }
  }

  private void checkSourceGenre(SourceVO.Genre genre, String message) throws IngeApplicationException {
    if (null == genre) {
      throw new IngeApplicationException("The sourceGenre " + message + " must not be empty");
    }
  }

  private void checkString(String string, String message) throws IngeApplicationException {
    if (null == string || string.trim().isEmpty()) {
      throw new IngeApplicationException("The string " + message + " must not be empty");
    }
  }

  private AccountUserDbVO checkUser(String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {
    Principal principal = authorizationService.checkLoginRequired(token);

    AccountUserDbVO accountUserDbVO = principal.getUserAccount();

    if (null == accountUserDbVO) {
      throw new IngeApplicationException("Invalid user");
    }

    return accountUserDbVO;
  }

  private void createBatchProcessLogDetails(AccountUserDbVO accountUserDbVO, List<String> itemIds,
      BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO, String token) {

    for (String itemId : itemIds) {
      ItemVersionVO itemVersionVO = null;
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO = null;

      try {
        itemVersionVO = this.pubItemService.get(itemId, token);
        if (null == itemVersionVO) {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
              itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.ITEM_NOT_FOUND, new Date());
        } else {
          batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId, itemVersionVO.getVersionNumber(),
              BatchProcessLogDetailDbVO.State.INITIALIZED, new Date());
        }
      } catch (IngeTechnicalException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, new Date());
      } catch (AuthenticationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.AUTHENTICATION_ERROR, new Date());
      } catch (AuthorizationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.AUTHORIZATION_ERROR, new Date());
      } catch (IngeApplicationException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, new Date());
      } catch (RuntimeException e) {
        batchProcessLogDetailDbVO = new BatchProcessLogDetailDbVO(batchProcessLogHeaderDbVO, itemId,
            itemVersionVO != null ? itemVersionVO.getVersionNumber() : null, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.INTERNAL_ERROR, new Date());
      }

      this.batchProcessLogDetailRepository.saveAndFlush(batchProcessLogDetailDbVO);
    }
  }

  private BatchProcessLogHeaderDbVO createBatchProcessLogHeader(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      Integer numberOfItems) {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        new BatchProcessLogHeaderDbVO(method, accountUserDbVO, BatchProcessLogHeaderDbVO.State.INITIALIZED, numberOfItems, new Date());
    batchProcessLogHeaderDbVO = this.batchProcessLogHeaderRepository.saveAndFlush(batchProcessLogHeaderDbVO);

    return batchProcessLogHeaderDbVO;
  }

  private void createBatchProcessUserLock(AccountUserDbVO accountUserDbVO) {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = new BatchProcessUserLockDbVO(accountUserDbVO, new Date());
    this.batchProcessUserLockRepository.saveAndFlush(batchProcessUserLockDbVO);
  }

  private BatchProcessLogHeaderDbVO doChangeContentCategory(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds,
      String contentCategoryFrom, String contentCategoryTo, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkString(contentCategoryFrom, "contentCategoryFrom");
    checkString(contentCategoryTo, "contentCategoryTo");

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setContentCategoryFrom(contentCategoryFrom);
    batchOperationsImpl.setCategoryTo(contentCategoryTo);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  private BatchProcessLogHeaderDbVO doKeywords(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds, String keywords, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {
    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);
    checkString(keywords, "keywords");

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    BatchProcessOperationsImpl batchOperationsImpl = new BatchProcessOperationsImpl();
    batchOperationsImpl.setKeywords(keywords);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token, batchOperationsImpl);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  private BatchProcessLogHeaderDbVO doPubItems(BatchProcessLogHeaderDbVO.Method method, List<String> itemIds, String token)
      throws AuthenticationException, IngeTechnicalException, IngeApplicationException, AuthorizationException {

    AccountUserDbVO accountUserDbVO = checkCommon(token, itemIds);

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = initializeBatchProcessLog(method, accountUserDbVO, itemIds, token);

    logger.info("Vor ASYNC Call");
    this.batchProcessAsyncService.doPubItemsAsync(method, batchProcessLogHeaderDbVO, accountUserDbVO, itemIds, token);
    logger.info("Nach ASYNC Call");

    return batchProcessLogHeaderDbVO;
  }

  @Transactional(rollbackFor = Throwable.class)
  private BatchProcessLogHeaderDbVO initializeBatchProcessLog(BatchProcessLogHeaderDbVO.Method method, AccountUserDbVO accountUserDbVO,
      List<String> itemIds, String token) {

    createBatchProcessUserLock(accountUserDbVO);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = createBatchProcessLogHeader(method, accountUserDbVO, itemIds.size());
    createBatchProcessLogDetails(accountUserDbVO, itemIds, batchProcessLogHeaderDbVO, token);

    return batchProcessLogHeaderDbVO;
  }
}


