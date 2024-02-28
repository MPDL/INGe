package de.mpg.mpdl.inge.rest.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
import de.mpg.mpdl.inge.model.db.valueobjects.FileDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.metadata.IdentifierVO;
import de.mpg.mpdl.inge.model.valueobjects.metadata.SourceVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.MdsPublicationVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.BatchProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/batchProcess")
@Tag(name = "Batch Process")
public class BatchProcessController {

  private static final String AccountUserObjectId_VAR = "accountUserObjectId";
  private static final String BatchProcessUserLockDelete_PATH = "deleteBatchProcessUserLock/{accountUserObjectId}";

  private static final String BatchProcessLogHeader_ID_PATH = "/{batchProcessLogHeaderId}";
  private static final String BatchProcessLogDetails_ID_PATH = "/batchProcessLogDetails/{batchProcessLogHeaderId}";
  private static final String BatchProcessLogHeader_VAR = "batchProcessLogHeaderId";

  private static final String CONTEXT_FROM = "contextFrom";
  private static final String CONTEXT_TO = "contextTo";
  private static final String CREATOR_ID = "creatorId";
  private static final String DEGREE_TYPE = "degreeType";
  private static final String EDITION = "edition";
  private static final String EXTERNAL_REFERENCE_CONTENT_CATEGORY_FROM = "externalReferenceContentCategoryFrom";
  private static final String EXTERNAL_REFERENCE_CONTENT_CATEGORY_TO = "externalReferenceContentCategoryTo";
  private static final String FILE_CONTENT_CATEGORY_FROM = "fileContentCategoryFrom";
  private static final String FILE_CONTENT_CATEGORY_TO = "fileContentCategoryTo";
  private static final String FILE_VISIBILITY_FROM = "fileVisibilityFrom";
  private static final String FILE_VISIBILITY_TO = "fileVisibilityTo";
  private static final String GENRE_FROM = "genreFrom";
  private static final String GENRE_TO = "genreTo";
  private static final String KEYWORDS = "keywords";
  private static final String KEYWORDS_FROM = "keywordsFrom";
  private static final String KEYWORDS_TO = "keywordsTo";
  private static final String LOCALTAG_FROM = "localTagFrom";
  private static final String LOCALTAG_TO = "localTagTo";
  private static final String ORCID = "orcid";
  private static final String REVIEW_METHOD_FROM = "reviewMethodFrom";
  private static final String REVIEW_METHOD_TO = "reviewMethodTo";
  private static final String SOURCE_GENRE_FROM = "sourceGenreFrom";
  private static final String SOURCE_GENRE_TO = "sourceGenreTo";
  private static final String SOURCE_IDENTIFIER = "sourceIdentifer";
  private static final String SOURCE_IDENTIFIER_FROM = "sourceIdentiferFrom";
  private static final String SOURCE_IDENTIFIER_TO = "sourceIdentiferTo";
  private static final String SOURCE_IDENTIFIER_TYPE = "sourceIdentiferType";
  private static final String SOURCE_NUMBER = "sourceNumber";
  
  private static final String PARAM_ITEM_IDS = "itemIds";
  private static final String PARAM_LOCALTAGS = "localTags";
  private static final String PARAM_ALLOWED_AUDIENCE_IDS = "allowedAudienceIds";

  private static final String EXAMPLE_ITEM_IDS = "\"itemIds\": [\"item_xxx\", \"item_xxx\", ...]";
  private static final String EXAMPLE_LOCALTAGS = "\"localTags\": [\"tag\", \"tag\", ...]";
  private static final String EXAMPLE_ALLOWED_AUDIENCE_IDS = "\"allowedAudienceIds\": [\"id\", \"id\", ...]";

  private final BatchProcessService batchProcessService;

  public BatchProcessController(BatchProcessService batchProcessService) {
    this.batchProcessService = batchProcessService;
  }

  /*
   * Beispiel für parameters:
   *   {
   *     "itemIds": ["item_xxx", "item_xxx", ...],
   *     "localTags": ["tag", "tag", ...],
   *     "allowedAudienceIds": ["id", "id", ...]
   *   }
   */

  @RequestMapping(value = "/addKeywords", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> addKeywords( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = KEYWORDS) String keywords, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.addKeywords(itemIds, keywords, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/addLocalTags", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "," + EXAMPLE_LOCALTAGS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> addLocalTags( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    List<String> localTags = convertJsonNode2List(parameters, PARAM_LOCALTAGS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.addLocalTags(itemIds, localTags, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/addSourceIdentifer", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> addSourceIdentifer( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = SOURCE_NUMBER) int sourceNumber, //
      @RequestParam(value = SOURCE_IDENTIFIER_TYPE) IdentifierVO.IdType sourceIdentifierType, //
      @RequestParam(value = SOURCE_IDENTIFIER) String sourceIdentifier, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.addSourceIdentifier(itemIds, sourceNumber, sourceIdentifierType, sourceIdentifier, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeContext", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeContext( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = CONTEXT_FROM) String contextFrom, //
      @RequestParam(value = CONTEXT_TO) String contextTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeContext(itemIds, contextFrom, contextTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeExternalReferenceContentCategory", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeExternalReferenceContentCategory( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = EXTERNAL_REFERENCE_CONTENT_CATEGORY_FROM) String externalReferenceContentCategoryFrom, //
      @RequestParam(value = EXTERNAL_REFERENCE_CONTENT_CATEGORY_TO) String externalReferenceContentCategoryTo, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeExternalReferenceContentCategory(itemIds,
        externalReferenceContentCategoryFrom, externalReferenceContentCategoryTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeFileContentCategory", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeFileContentCategory( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = FILE_CONTENT_CATEGORY_FROM) String fileContentCategoryFrom, //
      @RequestParam(value = FILE_CONTENT_CATEGORY_TO) String fileContentCategoryTo, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeFileContentCategory(itemIds, fileContentCategoryFrom, fileContentCategoryTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeFileVisibility", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeFileVisibility( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = FILE_VISIBILITY_FROM) FileDbVO.Visibility fileVisibilityFrom, //
      @RequestParam(value = FILE_VISIBILITY_TO) FileDbVO.Visibility fileVisibilityTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeFileVisibility(itemIds, fileVisibilityFrom, fileVisibilityTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeGenre", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeGenre( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = GENRE_FROM) MdsPublicationVO.Genre genreFrom, //
      @RequestParam(value = GENRE_TO) MdsPublicationVO.Genre genreTo, //
      @RequestParam(value = DEGREE_TYPE, required = false) MdsPublicationVO.DegreeType degreeType, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeGenre(itemIds, genreFrom, genreTo, degreeType, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeKeywords", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeKeywords( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = KEYWORDS_FROM) String keywordsFrom, //
      @RequestParam(value = KEYWORDS_TO) String keywordsTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeKeywords(itemIds, keywordsFrom, keywordsTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeLocalTag", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeLocalTag( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = LOCALTAG_FROM) String localTagFrom, //
      @RequestParam(value = LOCALTAG_TO) String localTagTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeLocalTag(itemIds, localTagFrom, localTagTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeReviewMethod", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeReviewMethod( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = REVIEW_METHOD_FROM, required = false) MdsPublicationVO.ReviewMethod reviewMethodFrom, //
      @RequestParam(value = REVIEW_METHOD_TO, required = false) MdsPublicationVO.ReviewMethod reviewMethodTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeReviewMethod(itemIds, reviewMethodFrom, reviewMethodTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeSourceGenre", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeSourceGenre( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = SOURCE_GENRE_FROM) SourceVO.Genre sourceGenreFrom, //
      @RequestParam(value = SOURCE_GENRE_TO) SourceVO.Genre sourceGenreTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.changeSourceGenre(itemIds, sourceGenreFrom, sourceGenreTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/changeSourceIdentifer", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> changeSourceIdentifer( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = SOURCE_NUMBER) int sourceNumber, //
      @RequestParam(value = SOURCE_IDENTIFIER_TYPE) IdentifierVO.IdType sourceIdentifierType, //
      @RequestParam(value = SOURCE_IDENTIFIER_FROM, required = false) String sourceIdentifierFrom, //
      @RequestParam(value = SOURCE_IDENTIFIER_TO, required = false) String sourceIdentifierTo, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.changeSourceIdentifier(itemIds, sourceNumber,
        sourceIdentifierType, sourceIdentifierFrom, sourceIdentifierTo, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = BatchProcessUserLockDelete_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteBatchProcessUserLock( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @PathVariable(value = AccountUserObjectId_VAR) String accountUserObjectId)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, NotFoundException {

    try {
      this.batchProcessService.deleteBatchProcessUserLock(accountUserObjectId, token);
    } catch (NoSuchElementException e) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/deletePubItems", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> deletePubItems( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.deletePubItems(itemIds, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/getAllBatchProcessLogHeaders", method = RequestMethod.GET)
  public ResponseEntity<List<BatchProcessLogHeaderDbVO>> getAllBatchProcessLogHeaders(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token)
      throws AuthenticationException, IngeApplicationException, NotFoundException {

    List<BatchProcessLogHeaderDbVO> batchProcessLogHeaderDbVOs = this.batchProcessService.getAllBatchProcessLogHeaders(token);

    if (null == batchProcessLogHeaderDbVOs || batchProcessLogHeaderDbVOs.isEmpty()) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(batchProcessLogHeaderDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = BatchProcessLogDetails_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<List<BatchProcessLogDetailDbVO>> getBatchProcessLogDetails(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = BatchProcessLogHeader_VAR) String batchProcessLogHeaderId)
      throws AuthenticationException, IngeApplicationException, NotFoundException {

    List<BatchProcessLogDetailDbVO> batchProcessLogDetailDbVOs =
        this.batchProcessService.getBatchProcessLogDetails(batchProcessLogHeaderId, token);

    if (null == batchProcessLogDetailDbVOs || batchProcessLogDetailDbVOs.isEmpty()) {
      throw new NotFoundException();
    }

    // remove BatchProcessLogHeaderDbVO
    List<BatchProcessLogDetailDbVO> adaptedBatchProcessLogDetailDbVOs = new ArrayList<>();
    for (BatchProcessLogDetailDbVO batchProcessLogDetailDbVO : batchProcessLogDetailDbVOs) {
      batchProcessLogDetailDbVO.setBatchProcessLogHeaderDbVO(null);
      adaptedBatchProcessLogDetailDbVOs.add(batchProcessLogDetailDbVO);
    }

    return new ResponseEntity<>(adaptedBatchProcessLogDetailDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = BatchProcessLogHeader_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<BatchProcessLogHeaderDbVO> getBatchProcessLogHeader(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = BatchProcessLogHeader_VAR) String batchProcessLogHeaderId)
      throws AuthenticationException, IngeApplicationException, NotFoundException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.getBatchProcessLogHeader(batchProcessLogHeaderId, token);

    if (null == batchProcessLogHeaderDbVO) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/getBatchProcessUserLock", method = RequestMethod.GET)
  public ResponseEntity<BatchProcessUserLockDbVO> getBatchProcessUserLock(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token)
      throws AuthenticationException, IngeApplicationException, NotFoundException {

    BatchProcessUserLockDbVO batchProcessUserLockDbVO = this.batchProcessService.getBatchProcessUserLock(token);

    if (null == batchProcessUserLockDbVO) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(batchProcessUserLockDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/releasePubItems", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> releasePubItems( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.releasePubItems(itemIds, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceSourceEdition", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceEdition( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = SOURCE_NUMBER) int sourceNumber, //
      @RequestParam(value = EDITION) String edition, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO =
        this.batchProcessService.replaceSourceEdition(itemIds, sourceNumber, edition, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceFileAudience", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "," + EXAMPLE_ALLOWED_AUDIENCE_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceFileAudience( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    List<String> allowedAudienceIds = convertJsonNode2List(parameters, PARAM_ALLOWED_AUDIENCE_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.replaceFileAudience(itemIds, allowedAudienceIds, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceKeywords", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceKeywords( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = KEYWORDS) String keywords, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.replaceKeywords(itemIds, keywords, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/replaceOrcid", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> replaceOrcid( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestParam(value = CREATOR_ID) String creatorId, //
      @RequestParam(value = ORCID) String orcid, //
      @RequestBody JsonNode parameters) throws AuthenticationException, AuthorizationException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.replaceOrcid(itemIds, creatorId, orcid, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/revisePubItems", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> revisePubItems( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.revisePubItems(itemIds, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/submitPubItems", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> submitPubItems( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.submitPubItems(itemIds, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/withdrawPubItems", method = RequestMethod.PUT)
  @Operation(requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( //
      content = @Content(examples = @ExampleObject(value = "{" + EXAMPLE_ITEM_IDS + "}"))))
  public ResponseEntity<BatchProcessLogHeaderDbVO> withdrawPubItems( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, PARAM_ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.withdrawPubItems(itemIds, token);

    return new ResponseEntity<>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  private List<String> convertJsonNode2List(JsonNode parameters, String parameterName) throws IngeApplicationException {

    List<String> convertedList = new ArrayList<>();

    JsonNode jsonNode = parameters.get(parameterName);
    if (null != jsonNode) {
      jsonNode.forEach(element -> convertedList.add(element.asText()));
    }

    if (convertedList.isEmpty()) {
      throw new IngeApplicationException("The request body doesn't contain valid " + parameterName);
    }

    return convertedList;
  }
}
