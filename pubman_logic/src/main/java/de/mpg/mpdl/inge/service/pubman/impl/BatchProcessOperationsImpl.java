package de.mpg.mpdl.inge.service.pubman.impl;

import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.CreatorVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PersonVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessOperations;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class BatchProcessOperationsImpl implements BatchProcessOperations {

  private final BatchProcessCommonService batchProcessCommonService;

  private final ContextService contextService;

  private List<String> allowedAudienceIds;
  private String categoryTo;
  private String contentCategoryFrom;
  private String contextFrom;
  private String contextTo;
  private String creatorId;
  private String userIpListId;
  private MdsPublicationVO.DegreeType degreeType;
  private String edition;
  private MdsPublicationVO.Genre genreFrom;
  private MdsPublicationVO.Genre genreTo;
  private String keywords;
  private String keywordsFrom;
  private String keywordsTo;
  private String localTagFrom;
  private List<String> localTags;
  private String localTagTo;
  private String orcid;
  private MdsPublicationVO.ReviewMethod reviewMethodFrom;
  private MdsPublicationVO.ReviewMethod reviewMethodTo;
  private SourceVO.Genre sourceGenreFrom;
  private SourceVO.Genre sourceGenreTo;
  private String sourceIdentifier;
  private String sourceIdentifierFrom;
  private String sourceIdentifierTo;
  private IdentifierVO.IdType sourceIdentifierType;
  private int sourceNumber;
  private FileDbVO.Visibility visibilityFrom;
  private FileDbVO.Visibility visibilityTo;

  public BatchProcessOperationsImpl(BatchProcessCommonService batchProcessCommonService, ContextService contextService) {
    this.batchProcessCommonService = batchProcessCommonService;
    this.contextService = contextService;
  }

  @Override
  public void addLocalTags(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> currentLocalTags = itemVersionVO.getObject().getLocalTags();
    currentLocalTags.addAll(this.localTags);
    itemVersionVO.getObject().setLocalTags(currentLocalTags);
    this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
  }

  @Override
  public void addSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
    if (null != currentSourceList && currentSourceList.size() >= this.sourceNumber
        && null != currentSourceList.get(this.sourceNumber - 1)) {
      if (null != currentSourceList.get(this.sourceNumber - 1).getIdentifiers()) {
        currentSourceList.get(this.sourceNumber - 1).getIdentifiers()
            .add(new IdentifierVO(this.sourceIdentifierType, this.sourceIdentifier));
        this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
      }
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_SOURCE_FOUND);
    }
  }

  @Override
  public void changeContentCategory(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean anyFilesChanged = false;
    if (BatchProcessLogHeaderDbVO.Method.CHANGE_FILE_CONTENT_CATEGORY.equals(method)) {
      for (FileDbVO file : itemVersionVO.getFiles()) {
        if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage())
            && file.getMetadata().getContentCategory().equals(this.contentCategoryFrom)) {
          file.getMetadata().setContentCategory(this.categoryTo);
          anyFilesChanged = true;
        }
      }
    } else if (BatchProcessLogHeaderDbVO.Method.CHANGE_EXTERNAL_REFERENCE_CONTENT_CATEGORY.equals(method)) {
      for (FileDbVO file : itemVersionVO.getFiles()) {
        if (FileDbVO.Storage.EXTERNAL_URL.equals(file.getStorage())
            && file.getMetadata().getContentCategory().equals(this.contentCategoryFrom)) {
          file.getMetadata().setContentCategory(this.contentCategoryFrom);
          anyFilesChanged = true;
        }
      }
    }
    if (anyFilesChanged) {
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
    }
  }

  @Override
  public void changeContext(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO, AccountUserDbVO accountUserDbVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    ContextDbVO contextDbVOFrom = getContext(this.contextFrom, accountUserDbVO, token, batchProcessLogDetailDbVO);
    if (null != contextDbVOFrom) {
      ContextDbVO contextDbVOTo = getContext(this.contextTo, accountUserDbVO, token, batchProcessLogDetailDbVO);
      if (null != contextDbVOTo) {
        if (contextDbVOFrom.getObjectId().equals(itemVersionVO.getObject().getContext().getObjectId())) {
          if (null != itemVersionVO.getMetadata() && null != itemVersionVO.getMetadata().getGenre()
              && null != contextDbVOTo.getAllowedGenres() && !contextDbVOTo.getAllowedGenres().isEmpty()
              && contextDbVOTo.getAllowedGenres().contains(itemVersionVO.getMetadata().getGenre())) {
            itemVersionVO.getObject().setContext(contextDbVOTo);
            if (!((ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getObject().getPublicState())
                || ItemVersionRO.State.IN_REVISION.equals(itemVersionVO.getVersionState()))
                && ContextDbVO.Workflow.SIMPLE.equals(contextDbVOTo.getWorkflow()))) {
              this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
            } else {
              this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                  BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_ALLOWED);
            }
          } else {
            this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_ALLOWED);
          }
        } else {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
        }
      }
    }
  }

  @Override
  public void changeFileVisibility(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean anyFilesChanged = false;
    for (FileDbVO file : itemVersionVO.getFiles()) {
      if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage()) && file.getVisibility().equals(this.visibilityFrom)) {
        file.setVisibility(this.visibilityTo);
        if (FileDbVO.Visibility.AUDIENCE.equals(this.visibilityTo)) {
          if (null != file.getAllowedAudienceIds() && null != this.userIpListId) {
            file.getAllowedAudienceIds().add(this.userIpListId);
          } else if (null == file.getAllowedAudienceIds()) {
            file.setAllowedAudienceIds(new ArrayList<>());
            if (null != this.userIpListId) {
              file.getAllowedAudienceIds().add(this.userIpListId);
            }
          }
        }
        if (FileDbVO.Visibility.PUBLIC.equals(this.visibilityTo) && null != file.getMetadata().getEmbargoUntil()) {
          file.getMetadata().setEmbargoUntil(null);
        }
        anyFilesChanged = true;
      }
    }
    if (anyFilesChanged) {
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_FILES_METADATA_OLD_VALUE_NOT_EQUAL);
    }
  }

  @Override
  public void changeGenre(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    ContextDbVO contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
    if (null != contextDbVO.getAllowedGenres() && !contextDbVO.getAllowedGenres().isEmpty()
        && contextDbVO.getAllowedGenres().contains(this.genreTo)) {
      MdsPublicationVO.Genre currentPubItemGenre = itemVersionVO.getMetadata().getGenre();
      if (currentPubItemGenre.equals(this.genreFrom)) {
        if (!MdsPublicationVO.Genre.THESIS.equals(this.genreTo)) {
          itemVersionVO.getMetadata().setGenre(this.genreTo);

          this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
        } else if (MdsPublicationVO.Genre.THESIS.equals(this.genreTo) && null != this.degreeType) {
          itemVersionVO.getMetadata().setGenre(this.genreTo);
          itemVersionVO.getMetadata().setDegree(this.degreeType);

          this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
        } else {
          this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
              BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_NEW_VALUE_SET);
        }
      } else {
        this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
      }
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_ALLOWED);
    }
  }

  @Override
  public void changeKeywords(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean keywordsChanged = false;
    char splittingChar = ',';
    String currentKeywords = itemVersionVO.getMetadata().getFreeKeywords();
    String[] keywordArray = new String[1];
    if (null != currentKeywords) {
      if (currentKeywords.contains(",")) {
        keywordArray = currentKeywords.split(",");
      } else if (currentKeywords.contains(";")) {
        keywordArray = currentKeywords.split(";");
        splittingChar = ';';
      } else if (currentKeywords.contains(" ")) {
        keywordArray = currentKeywords.split(" ");
        splittingChar = ' ';
      } else {
        keywordArray[0] = currentKeywords;
      }
      StringBuilder keywordString = new StringBuilder();
      for (int i = 0; i < keywordArray.length; i++) {
        String keyword = keywordArray[i].trim();
        if (0 != i) {
          keywordString.append(splittingChar);
        }
        if (!keyword.isEmpty() && this.keywordsFrom.equals(keyword)) {
          keywordString.append(this.keywordsTo);
          keywordsChanged = true;
        } else {
          keywordString.append(keyword);
        }
      }
      if (keywordsChanged) {
        itemVersionVO.getMetadata().setFreeKeywords(keywordString.toString());
        this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
      } else {
        this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
      }
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_CHANGE_VALUE);
    }
  }

  @Override
  public void changeLocalTag(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (null != itemVersionVO.getObject().getLocalTags() && itemVersionVO.getObject().getLocalTags().contains(this.localTagFrom)) {
      List<String> localTagList = itemVersionVO.getObject().getLocalTags();
      localTagList.remove(this.localTagFrom);
      localTagList.add(this.localTagTo);
      itemVersionVO.getObject().setLocalTags(localTagList);
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_CHANGE_VALUE);
    }
  }

  @Override
  public void changeReviewMethod(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    MdsPublicationVO.ReviewMethod currentReviewMethod = itemVersionVO.getMetadata().getReviewMethod();
    if ((null == currentReviewMethod && null == this.reviewMethodFrom && null != this.reviewMethodTo)
        || (null != currentReviewMethod && currentReviewMethod.equals(this.reviewMethodFrom))) {
      itemVersionVO.getMetadata().setReviewMethod(this.reviewMethodTo);
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
    }
  }

  @Override
  public void changeSourceGenre(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    ContextDbVO contextDbVO = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
    if (null != contextDbVO.getAllowedGenres() && !contextDbVO.getAllowedGenres().isEmpty()
        && contextDbVO.getAllowedGenres().contains(this.genreTo)) {
      if (!this.sourceGenreFrom.equals(this.sourceGenreTo)) {
        List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
        boolean sourceChanged = false;
        for (SourceVO currentSource : currentSourceList) {
          SourceVO.Genre currentSourceGenre = currentSource.getGenre();
          if (currentSourceGenre.equals(this.sourceGenreFrom)) {
            currentSource.setGenre(this.sourceGenreTo);
            sourceChanged = true;
          }
          if (sourceChanged) {
            this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
          } else {
            this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
                BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
          }
        }
      } else {
        this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_CHANGE_VALUE);
      }
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_ALLOWED);
    }
  }

  @Override
  public void changeSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean sourceChanged = false;
    List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
    if (null != currentSourceList && currentSourceList.size() >= this.sourceNumber && null != currentSourceList.get(this.sourceNumber - 1)
        && null != currentSourceList.get(this.sourceNumber - 1).getIdentifiers()) {
      for (int i = 0; i < currentSourceList.get(this.sourceNumber - 1).getIdentifiers().size(); i++) {
        IdentifierVO identifier = currentSourceList.get(this.sourceNumber - 1).getIdentifiers().get(i);
        if (this.sourceIdentifierType.equals(identifier.getType()) && this.sourceIdentifierFrom.equals(identifier.getId())) {
          if (null != this.sourceIdentifierTo && !this.sourceIdentifierTo.trim().isEmpty()) {
            identifier.setId(this.sourceIdentifierTo);
            currentSourceList.get(this.sourceNumber - 1).getIdentifiers().set(i, identifier);
          } else {
            currentSourceList.get(this.sourceNumber - 1).getIdentifiers().remove(i);
          }
          sourceChanged = true;
        }
      }
      if (sourceChanged) {
        this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
      } else {
        this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
            BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_NOT_EQUAL);
      }
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_SOURCE_FOUND);
    }
  }

  @Override
  public void doKeywords(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String currentKeywords = null;
    if (BatchProcessLogHeaderDbVO.Method.ADD_KEYWORDS.equals(method)
        && null != (currentKeywords = itemVersionVO.getMetadata().getFreeKeywords())) {
      if (currentKeywords.contains(",")) {
        itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + ", " + this.keywords);
      } else if (currentKeywords.contains(";")) {
        itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + "; " + this.keywords);
      } else if (currentKeywords.contains(" ")) {
        itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + " " + this.keywords);
      } else {
        itemVersionVO.getMetadata().setFreeKeywords(currentKeywords + ", " + this.keywords);
      }
    } else {
      itemVersionVO.getMetadata().setFreeKeywords(this.keywords);
    }
    this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
  }

  @Override
  public void replaceFileAudience(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean anyFilesChanged = false;
    for (FileDbVO file : itemVersionVO.getFiles()) {
      List<String> audienceList = null != file.getAllowedAudienceIds() ? file.getAllowedAudienceIds() : new ArrayList<>();
      if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage()) && FileDbVO.Visibility.AUDIENCE.equals(file.getVisibility())) {
        audienceList.clear();
        audienceList.addAll(this.allowedAudienceIds);
        file.setAllowedAudienceIds(audienceList);
        anyFilesChanged = true;
      }
    }
    if (anyFilesChanged) {
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_FILES_METADATA_OLD_VALUE_NOT_EQUAL);
    }
  }

  @Override
  public void replaceOrcid(BatchProcessLogHeaderDbVO.Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean anyOrcidChanged = false;
    for (CreatorVO creator : itemVersionVO.getMetadata().getCreators()) {
      PersonVO person = creator.getPerson();
      if (null != person && null != person.getIdentifier()) {
        if (this.creatorId.equals(person.getIdentifier().getId())) {
          if (!this.orcid.equals(person.getOrcid())) {
            person.setOrcid(this.orcid);
            anyOrcidChanged = true;
          }
        }
      }
    }
    List<SourceVO> sources = itemVersionVO.getMetadata().getSources();
    for (SourceVO source : sources) {
      for (CreatorVO creator : source.getCreators()) {
        PersonVO person = creator.getPerson();
        if (null != person && null != person.getIdentifier()) {
          if (this.creatorId.equals(person.getIdentifier().getId())) {
            if (!this.orcid.equals(person.getOrcid())) {
              person.setOrcid(this.orcid);
              anyOrcidChanged = true;
            }
          }
        }
      }
    }
    if (anyOrcidChanged) {
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_CHANGE_VALUE_ORCID_NO_PERSON);
    }
  }

  @Override
  public void replaceSourceEdition(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
    if (null != currentSourceList && currentSourceList.size() >= this.sourceNumber
        && null != currentSourceList.get(this.sourceNumber - 1)) {
      if (null == currentSourceList.get(this.sourceNumber - 1).getPublishingInfo()) {
        currentSourceList.get(this.sourceNumber - 1).setPublishingInfo(new PublishingInfoVO());
      }
      currentSourceList.get(this.sourceNumber - 1).getPublishingInfo().setEdition(this.edition);
      this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_METADATA_NO_SOURCE_FOUND);
    }
  }

  public void setAllowedAudienceIds(List<String> allowedAudienceIds) {
    this.allowedAudienceIds = allowedAudienceIds;
  }

  public void setCategoryTo(String categoryTo) {
    this.categoryTo = categoryTo;
  }

  public void setContentCategoryFrom(String contentCategoryFrom) {
    this.contentCategoryFrom = contentCategoryFrom;
  }

  public void setContextFrom(String contextFrom) {
    this.contextFrom = contextFrom;
  }

  public void setContextTo(String contextTo) {
    this.contextTo = contextTo;
  }

  public void setCreatorId(String creatorId) {
    this.creatorId = creatorId;
  }

  public void setUserIpListId(String userIpListId) {
    this.userIpListId = userIpListId;
  }

  public void setDegreeType(MdsPublicationVO.DegreeType degreeType) {
    this.degreeType = degreeType;
  }

  public void setEdition(String edition) {
    this.edition = edition;
  }

  public void setGenreFrom(MdsPublicationVO.Genre genreFrom) {
    this.genreFrom = genreFrom;
  }

  public void setGenreTo(MdsPublicationVO.Genre genreTo) {
    this.genreTo = genreTo;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public void setKeywordsFrom(String keywordsFrom) {
    this.keywordsFrom = keywordsFrom;
  }

  public void setKeywordsTo(String keywordsTo) {
    this.keywordsTo = keywordsTo;
  }

  public void setLocalTagFrom(String localTagFrom) {
    this.localTagFrom = localTagFrom;
  }

  public void setLocalTags(List<String> localTags) {
    this.localTags = localTags;
  }

  public void setLocalTagTo(String localTagTo) {
    this.localTagTo = localTagTo;
  }

  public void setOrcid(String orcid) {
    this.orcid = orcid;
  }

  public void setReviewMethodFrom(MdsPublicationVO.ReviewMethod reviewMethodFrom) {
    this.reviewMethodFrom = reviewMethodFrom;
  }

  public void setReviewMethodTo(MdsPublicationVO.ReviewMethod reviewMethodTo) {
    this.reviewMethodTo = reviewMethodTo;
  }

  public void setSourceGenreFrom(SourceVO.Genre sourceGenreFrom) {
    this.sourceGenreFrom = sourceGenreFrom;
  }

  public void setSourceGenreTo(SourceVO.Genre sourceGenreTo) {
    this.sourceGenreTo = sourceGenreTo;
  }

  public void setSourceIdentifier(String sourceIdentifier) {
    this.sourceIdentifier = sourceIdentifier;
  }

  public void setSourceIdentifierFrom(String sourceIdentifierFrom) {
    this.sourceIdentifierFrom = sourceIdentifierFrom;
  }

  public void setSourceIdentifierTo(String sourceIdentifierTo) {
    this.sourceIdentifierTo = sourceIdentifierTo;
  }

  public void setSourceIdentifierType(IdentifierVO.IdType sourceIdentifierType) {
    this.sourceIdentifierType = sourceIdentifierType;
  }

  public void setSourceNumber(int sourceNumber) {
    this.sourceNumber = sourceNumber;
  }

  public void setVisibilityFrom(FileDbVO.Visibility visibilityFrom) {
    this.visibilityFrom = visibilityFrom;
  }

  public void setVisibilityTo(FileDbVO.Visibility visibilityTo) {
    this.visibilityTo = visibilityTo;
  }

  private ContextDbVO getContext(String context, AccountUserDbVO accountUserDbVO, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException {
    ContextDbVO contextDbVO = this.contextService.get(context, token);

    if (null == contextDbVO) {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_CONTEXT_NOT_FOUND);

      return null;
    } else if (!contextDbVO.getState().equals(ContextDbVO.State.OPENED)) {
      this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
          BatchProcessLogDetailDbVO.Message.BATCH_CONTEXT_NOT_OPEN);

      return null;
    }

    List<GrantVO> grantVOs = accountUserDbVO.getGrantList();
    for (GrantVO grantVO : grantVOs) {
      if (contextDbVO.getObjectId().equals(grantVO.getObjectRef())) {
        return contextDbVO;
      }
    }

    this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO, BatchProcessLogDetailDbVO.State.ERROR,
        BatchProcessLogDetailDbVO.Message.BATCH_CONTEXT_AUTHORIZATION_ERROR);

    return null;
  }
}
