package de.mpg.mpdl.inge.rest.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogDetailDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessLogHeaderDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.BatchProcessUserLockDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessService;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/batchProcess")
@Tag(name = "Batch Process")
public class BatchProcessController {

  private static final String AUTHZ_HEADER = "Authorization";

  private static final String AccountUserObjectId_VAR = "accountUserObjectId";
  private static final String BatchProcessUserLockDelete_PATH = "deleteBatchProcessUserLock/{accountUserObjectId}";

  private static final String BatchProcessLogHeader_ID_PATH = "/{batchProcessLogHeaderId}";
  private static final String BatchProcessLogDetails_ID_PATH = "/batchProcessLogDetails/{batchProcessLogHeaderId}";
  private static final String BatchProcessLogHeader_VAR = "batchProcessLogHeaderId";

  private static final String AUDIENCES = "audiences";
  private static final String CONTEXT_FROM = "contextFrom";
  private static final String CONTEXT_TO = "contextTo";
  private static final String DEGREE_TYPE = "degreeType";
  private static final String EXTERNAL_REFERENCE_CONTENT_CATEGORY_FROM = "externalReferenceContentCategoryFrom";
  private static final String EXTERNAL_REFERENCE_CONTENT_CATEGORY_TO = "externalReferenceContentCategoryTo";
  private static final String GENRE_FROM = "genreFrom";
  private static final String GENRE_TO = "genreTo";
  private static final String EDITION = "edition";
  private static final String FILE_CONTENT_CATEGORY_FROM = "fileContentCategoryFrom";
  private static final String FILE_CONTENT_CATEGORY_TO = "fileContentCategoryTo";
  private static final String ITEM_IDS = "itemIds";
  private static final String KEYWORDS = "keywords";
  private static final String LOCALTAGS = "localTags";
  private static final String LOCALTAG_FROM = "localTagFrom";
  private static final String LOCALTAG_TO = "localTagTo";
  private static final String SOURCE_GENRE_FROM = "sourceGenreFrom";
  private static final String SOURCE_GENRE_TO = "sourceGenreTo";
  private static final String SOURCE_IDENTIFIER = "sourceIdentifer";
  private static final String SOURCE_IDENTIFIER_TYPE = "sourceIdentiferType";
  private static final String SOURCE_NUMBER = "sourceNumber";

  private BatchProcessService batchProcessService;

  @Autowired
  public BatchProcessController(BatchProcessService batchProcessService) {
    this.batchProcessService = batchProcessService;
  }

  @RequestMapping(value = "/addKeywords", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> addKeywords( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = KEYWORDS, required = true) String keywords, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.addKeywords(itemIds, keywords, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/addLocalTags", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> addLocalTags( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    List<String> localTags = convertJsonNode2List(listParameters, LOCALTAGS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.addLocalTags(itemIds, localTags, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/addSourceIdentifer", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> addSourceIdentifer( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = SOURCE_NUMBER, required = true) int sourceNumber, //
      @RequestParam(value = SOURCE_IDENTIFIER_TYPE, required = true) IdentifierVO.IdType sourceIdentifierType, //
      @RequestParam(value = SOURCE_IDENTIFIER, required = true) String sourceIdentifier, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.addSourceIdentifier(itemIds, sourceNumber, sourceIdentifierType, sourceIdentifier, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeExternalReferenceContentCategory", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeExternalReferenceContentCategory( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = EXTERNAL_REFERENCE_CONTENT_CATEGORY_FROM, required = true) String externalReferenceContentCategoryFrom, //
      @RequestParam(value = EXTERNAL_REFERENCE_CONTENT_CATEGORY_TO, required = true) String externalReferenceContentCategoryTo, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeExternalReferenceContentCategory(itemIds,
        externalReferenceContentCategoryFrom, externalReferenceContentCategoryTo, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeContext", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeContext( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = CONTEXT_FROM, required = true) String contextFrom, //
      @RequestParam(value = CONTEXT_TO, required = true) String contextTo, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeContext(itemIds, contextFrom, contextTo, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeFileContentCategory", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeFileContentCategory( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = FILE_CONTENT_CATEGORY_FROM, required = true) String fileContentCategoryFrom, //
      @RequestParam(value = FILE_CONTENT_CATEGORY_TO, required = true) String fileContentCategoryTo, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeFileContentCategory(itemIds, fileContentCategoryFrom, fileContentCategoryTo, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeGenre", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeGenre( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = GENRE_FROM, required = true) MdsPublicationVO.Genre genreFrom, //
      @RequestParam(value = GENRE_TO, required = true) MdsPublicationVO.Genre genreTo, //
      @RequestParam(value = DEGREE_TYPE, required = false) MdsPublicationVO.DegreeType degreeType, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeGenre(itemIds, genreFrom, genreTo, degreeType, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeLocalTag", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeLocalTag( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = LOCALTAG_FROM, required = true) String localTagFrom, //
      @RequestParam(value = LOCALTAG_TO, required = true) String localTagTo, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeContext(itemIds, localTagFrom, localTagTo, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeSourceGenre", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeSourceGenre( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = SOURCE_GENRE_FROM, required = true) SourceVO.Genre sourceGenreFrom, //
      @RequestParam(value = SOURCE_GENRE_TO, required = true) SourceVO.Genre sourceGenreTo, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeSourceGenre(itemIds, sourceGenreFrom, sourceGenreTo, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = BatchProcessUserLockDelete_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteBatchProcessUserLock( //
      @RequestHeader(value = AUTHZ_HEADER) String token, //
      @PathVariable(value = AccountUserObjectId_VAR) String accountUserObjectId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    try {
      this.batchProcessService.deleteBatchProcessUserLock(accountUserObjectId, token);
    } catch (NoSuchElementException e) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/deletePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> deletePubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.deletePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/getAllBatchProcessLogHeaders", method = RequestMethod.GET)
  public ResponseEntity<List<BatchProcessLogHeaderDbVO>> getAllBatchProcessLogHeaders(
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    List<BatchProcessLogHeaderDbVO> batchProcessLogHeaderDbVOs = this.batchProcessService.getAllBatchProcessLogHeaders(token);

    if (null == batchProcessLogHeaderDbVOs || batchProcessLogHeaderDbVOs.isEmpty()) {
      throw new NotFoundException();
    }

    return new ResponseEntity<List<BatchProcessLogHeaderDbVO>>(batchProcessLogHeaderDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = BatchProcessLogDetails_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<List<BatchProcessLogDetailDbVO>> getBatchProcessLogDetails(
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @PathVariable(value = BatchProcessLogHeader_VAR) String batchProcessLogHeaderId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    List<BatchProcessLogDetailDbVO> batchProcessLogDetailDbVOs =
        this.batchProcessService.getBatchProcessLogDetails(batchProcessLogHeaderId, token);

    if (null == batchProcessLogDetailDbVOs || batchProcessLogDetailDbVOs.isEmpty()) {
      throw new NotFoundException();
    }

    // remove BatchProcessLogHeaderDbVO
    List<BatchProcessLogDetailDbVO> adaptedBatchProcessLogDetailDbVOs = new ArrayList<BatchProcessLogDetailDbVO>();
    for (BatchProcessLogDetailDbVO batchProcessLogDetailDbVO : batchProcessLogDetailDbVOs) {
      batchProcessLogDetailDbVO.setBatchProcessLogHeaderDbVO(null);
      adaptedBatchProcessLogDetailDbVOs.add(batchProcessLogDetailDbVO);
    }

    return new ResponseEntity<List<BatchProcessLogDetailDbVO>>(adaptedBatchProcessLogDetailDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = BatchProcessLogHeader_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<BatchProcessLogHeaderDbVO> getBatchProcessLogHeader(
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @PathVariable(value = BatchProcessLogHeader_VAR) String batchProcessLogHeaderId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.getBatchProcessLogHeader(batchProcessLogHeaderId, token);

    if (null == batchProcessLogHeaderDbVO) {
      throw new NotFoundException();
    }

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/getBatchProcessUserLock", method = RequestMethod.GET)
  public ResponseEntity<BatchProcessUserLockDbVO> getBatchProcessUserLock(
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = this.batchProcessService.getBatchProcessUserLock(token);

    if (null == batchProcessUserLockDbVO) {
      throw new NotFoundException();
    }

    return new ResponseEntity<BatchProcessUserLockDbVO>(batchProcessUserLockDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/releasePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> releasePubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.releasePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceEdition", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceEdition( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = SOURCE_NUMBER, required = true) int sourceNumber, //
      @RequestParam(value = EDITION, required = true) String edition, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.replaceEdition(itemIds, sourceNumber, edition, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceFileAudience", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceFileAudience( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    List<String> audiences = convertJsonNode2List(listParameters, AUDIENCES);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.replaceFileAudience(itemIds, audiences, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceKeywords", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceKeywords( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = KEYWORDS, required = true) String keywords, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.replaceKeywords(itemIds, keywords, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/revisePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> revisePubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.revisePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/submitPubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> submitPubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.submitPubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/withdrawPubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> withdrawPubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode listParameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(listParameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.withdrawPubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private List<String> convertJsonNode2List(JsonNode listParameters, String node) throws IngeApplicationException {

    List<String> list = new ArrayList<String>();
    JsonNode jsonNode = listParameters.get(node);
    if (null != jsonNode) {
      jsonNode.forEach(element -> list.add(element.asText()));
    }

    if (list.isEmpty()) {
      throw new IngeApplicationException("The request body doesn't contain valid " + node);
    }

    return list;
  }
}
