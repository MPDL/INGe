package de.mpg.mpdl.inge.service.pubman.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO.Method;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionRO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.PublishingInfoVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO.Genre;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessCommonService;
import de.mpg.mpdl.inge.service.pubman.batchprocess.BatchProcessOperations;

@Service
@Primary
public class BatchProcessOperationsImpl implements BatchProcessOperations {

  private List<String> localTags;

  private String keywords;

  private int sourceNumber;

  private IdentifierVO.IdType sourceIdentifierType;

  private String sourceIdentifer;

  private String contextFrom;

  private String contextTo;

  private MdsPublicationVO.Genre genreFrom;

  private MdsPublicationVO.Genre genreTo;

  private MdsPublicationVO.DegreeType degreeType;

  private SourceVO.Genre sourceGenreFrom;

  private SourceVO.Genre sourceGenreTo;

  private String localTagFrom;

  private String localTagTo;

  private String contentCategoryFrom;

  private String categoryTo;

  private String edition;

  private List<String> audiences;

  @Autowired
  private ContextService contextService;

  @Autowired
  private BatchProcessCommonService batchProcessCommonService;

  @Override
  public BatchProcessLogDetailDbVO addLocalTags(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> currentLocalTags = itemVersionVO.getObject().getLocalTags();
    currentLocalTags.addAll(this.localTags);
    itemVersionVO.getObject().setLocalTags(currentLocalTags);
    batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO addSourceIdentifier(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
    if (currentSourceList != null && currentSourceList.size() >= this.sourceNumber
        && currentSourceList.get(this.sourceNumber - 1) != null) {
      if (currentSourceList.get(this.sourceNumber - 1).getIdentifiers() != null) {
        currentSourceList.get(this.sourceNumber - 1).getIdentifiers()
            .add(new IdentifierVO(this.sourceIdentifierType, this.sourceIdentifer));
        batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
      }
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_SOURCE_FOUND);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO changeContentCategory(BatchProcessLogHeaderDbVO.Method method, String token,
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
    if (anyFilesChanged == true) {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO changeContext(Method method, String token, BatchProcessLogDetailDbVO batchProcessLogDetailDbVO,
      ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (this.contextFrom.equals(itemVersionVO.getObject().getContext().getObjectId())) {
      ContextDbVO contextDbVOTo = this.contextService.get(this.contextTo, token);
      if (null == contextDbVOTo) {
        batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
            BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.CONTEXT_NOT_FOUND);
      } else if (itemVersionVO.getMetadata() != null && itemVersionVO.getMetadata().getGenre() != null
          && contextDbVOTo.getAllowedGenres() != null && !contextDbVOTo.getAllowedGenres().isEmpty()
          && contextDbVOTo.getAllowedGenres().contains(itemVersionVO.getMetadata().getGenre())) {
        itemVersionVO.getObject().setContext(contextDbVOTo);
        if (!((ItemVersionRO.State.SUBMITTED.equals(itemVersionVO.getObject().getPublicState())
            || ItemVersionRO.State.IN_REVISION.equals(itemVersionVO.getVersionState()))
            && ContextDbVO.Workflow.SIMPLE.equals(contextDbVOTo.getWorkflow()))) {
          batchProcessLogDetailDbVO =
              this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
        } else {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_ALLOWED);
        }
      } else {
        batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
            BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_ALLOWED);
      }
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO changeGenre(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    ContextDbVO contextDbVOTo = this.contextService.get(itemVersionVO.getObject().getContext().getObjectId(), token);
    if (contextDbVOTo.getAllowedGenres() != null && !contextDbVOTo.getAllowedGenres().isEmpty()
        && contextDbVOTo.getAllowedGenres().contains(itemVersionVO.getMetadata().getGenre())) {
      Genre currentPubItemGenre = itemVersionVO.getMetadata().getGenre();
      if (currentPubItemGenre.equals(this.genreFrom)) {
        if (!genreFrom.equals(this.genreTo)) {
          if (!Genre.THESIS.equals(this.genreTo)) {
            itemVersionVO.getMetadata().setGenre(this.genreTo);
            batchProcessLogDetailDbVO =
                this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
          } else if (Genre.THESIS.equals(this.genreTo) && null != this.degreeType) {
            itemVersionVO.getMetadata().setGenre(this.genreTo);
            itemVersionVO.getMetadata().setDegree(this.degreeType);
            batchProcessLogDetailDbVO =
                this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
          } else {
            batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
                BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_NEW_VALUE_SET);
          }
        } else {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_CHANGE_VALUE);
        }
      } else {
        batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
            BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
      }
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_ALLOWED);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO changeLocalTag(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (itemVersionVO.getObject().getLocalTags() != null && itemVersionVO.getObject().getLocalTags().contains(this.localTagFrom)) {
      List<String> localTagList = itemVersionVO.getObject().getLocalTags();
      localTagList.remove(this.localTagFrom);
      localTagList.add(this.localTagTo);
      itemVersionVO.getObject().setLocalTags(localTagList);
      batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_CHANGE_VALUE);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO changeSourceGenre(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    if (!this.sourceGenreFrom.equals(this.sourceGenreTo)) {
      List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
      boolean sourceChanged = false;
      for (SourceVO currentSource : currentSourceList) {
        SourceVO.Genre currentSourceGenre = currentSource.getGenre();
        if (currentSourceGenre.equals(this.sourceGenreFrom)) {
          currentSource.setGenre(this.sourceGenreTo);
          sourceChanged = true;
        }
        if (sourceChanged == true) {
          batchProcessLogDetailDbVO =
              this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
        } else {
          batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
              BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_CHANGE_VALUE_NOT_EQUAL);
        }
      }
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_CHANGE_VALUE);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO doKeywords(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    String currentKeywords = null;
    if (BatchProcessLogHeaderDbVO.Method.ADD_KEYWORDS.equals(method)
        && (currentKeywords = itemVersionVO.getMetadata().getFreeKeywords()) != null) {
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
    batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO replaceEdition(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    List<SourceVO> currentSourceList = itemVersionVO.getMetadata().getSources();
    if (currentSourceList != null && currentSourceList.size() >= this.sourceNumber
        && currentSourceList.get(this.sourceNumber - 1) != null) {
      if (currentSourceList.get(this.sourceNumber - 1).getPublishingInfo() != null) {
        currentSourceList.get(this.sourceNumber - 1).getPublishingInfo().setEdition(this.edition);
        batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
      } else {
        currentSourceList.get(this.sourceNumber - 1).setPublishingInfo(new PublishingInfoVO());
        currentSourceList.get(this.sourceNumber - 1).getPublishingInfo().setEdition(this.edition);
        batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
      }
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.METADATA_NO_SOURCE_FOUND);
    }

    return batchProcessLogDetailDbVO;
  }

  @Override
  public BatchProcessLogDetailDbVO replaceFileAudience(BatchProcessLogHeaderDbVO.Method method, String token,
      BatchProcessLogDetailDbVO batchProcessLogDetailDbVO, ItemVersionVO itemVersionVO)
      throws IngeTechnicalException, AuthenticationException, AuthorizationException, IngeApplicationException {

    boolean anyFilesChanged = false;
    for (FileDbVO file : itemVersionVO.getFiles()) {
      List<String> audienceList = file.getAllowedAudienceIds() != null ? file.getAllowedAudienceIds() : new ArrayList<String>();
      if (FileDbVO.Storage.INTERNAL_MANAGED.equals(file.getStorage()) && FileDbVO.Visibility.AUDIENCE.equals(file.getVisibility())) {
        audienceList.clear();
        audienceList.addAll(this.audiences);
        file.setAllowedAudienceIds(audienceList);
        anyFilesChanged = true;
      }
    }
    if (anyFilesChanged == true) {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.doUpdatePubItem(method, token, itemVersionVO, batchProcessLogDetailDbVO);
    } else {
      batchProcessLogDetailDbVO = this.batchProcessCommonService.updateBatchProcessLogDetail(batchProcessLogDetailDbVO,
          BatchProcessLogDetailDbVO.State.ERROR, BatchProcessLogDetailDbVO.Message.FILES_METADATA_OLD_VALUE_NOT_EQUAL);
    }

    return batchProcessLogDetailDbVO;
  }

  public void setAudiences(List<String> audiences) {
    this.audiences = audiences;
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

  public void setLocalTagFrom(String localTagFrom) {
    this.localTagFrom = localTagFrom;
  }

  public void setLocalTags(List<String> localTags) {
    this.localTags = localTags;
  }

  public void setLocalTagTo(String localTagTo) {
    this.localTagTo = localTagTo;
  }

  public void setSourceGenreFrom(SourceVO.Genre sourceGenreFrom) {
    this.sourceGenreFrom = sourceGenreFrom;
  }

  public void setSourceGenreTo(SourceVO.Genre sourceGenreTo) {
    this.sourceGenreTo = sourceGenreTo;
  }

  public void setSourceIdentifer(String sourceIdentifer) {
    this.sourceIdentifer = sourceIdentifer;
  }

  public void setSourceIdentifierType(IdentifierVO.IdType sourceIdentifierType) {
    this.sourceIdentifierType = sourceIdentifierType;
  }

  public void setSourceNumber(int sourceNumber) {
    this.sourceNumber = sourceNumber;
  }
}
