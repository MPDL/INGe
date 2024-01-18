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

  private static final String KEYWORDS = "keywords";
  private static final String LOCAL_TAGS = "localTags";
  private static final String ITEM_IDS = "itemIds";

  private static final String AUTHZ_HEADER = "Authorization";

  private static final String AccountUserObjectId_VAR = "accountUserObjectId";
  private static final String BatchProcessUserLockDelete_PATH = "deleteBatchProcessUserLock/{accountUserObjectId}";

  private static final String BatchProcessLogHeader_ID_PATH = "/{batchProcessLogHeaderId}";
  private static final String BatchProcessLogDetails_ID_PATH = "/batchProcessLogDetails/{batchProcessLogHeaderId}";
  private static final String BatchProcessLogHeader_VAR = "batchProcessLogHeaderId";

  private BatchProcessService batchProcessService;

  @Autowired
  public BatchProcessController(BatchProcessService batchProcessService) {
    this.batchProcessService = batchProcessService;
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

  @RequestMapping(value = BatchProcessUserLockDelete_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete( //
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

  @RequestMapping(value = "/addLocalTags", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> addLocalTags( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    List<String> localTags = convertJsonNode2List(parameters, LOCAL_TAGS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.addLocalTags(itemIds, localTags, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/addKeywords", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> addKeywords( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestParam(value = KEYWORDS, required = true) String keywords, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.addKeywords(itemIds, keywords, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/deletePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> deletePubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.deletePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/releasePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> releasePubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.releasePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/revisePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> revisePubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.revisePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/submitPubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> submitPubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.submitPubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/withdrawPubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> withdrawPubItems( //
      @RequestHeader(value = AUTHZ_HEADER, required = true) String token, //
      @RequestBody JsonNode parameters)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<String> itemIds = convertJsonNode2List(parameters, ITEM_IDS);
    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.withdrawPubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  private List<String> convertJsonNode2List(JsonNode parameters, String node) throws IngeApplicationException {

    List<String> list = new ArrayList<String>();
    JsonNode jsonNode = parameters.get(node);
    if (null != jsonNode) {
      jsonNode.forEach(element -> list.add(element.asText()));
    }

    if (list.isEmpty()) {
      throw new IngeApplicationException("The request body doesn't contain valid " + node);
    }

    return list;
  }
}
