package de.mpg.mpdl.inge.rest.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

  private final String AUTHZ_HEADER = "Authorization";
  private final String BatchProcessLogHeader_ID_PATH = "/{batchProcessLogHeaderId}";
  private final String BatchProcessLogDetails_ID_PATH = "/{batchProcessLogHeaderId}/batchProcessLogDetails";
  private final String BatchProcessLogHeader_VAR = "batchProcessLogHeaderId";

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

    List<BatchProcessLogDetailDbVO> adaptedBatchProcessLogDetailDbVOs = new ArrayList<BatchProcessLogDetailDbVO>();
    for (BatchProcessLogDetailDbVO batchProcessLogDetailDbVO : batchProcessLogDetailDbVOs) {
      BatchProcessLogHeaderDbVO adaptedBatchProcessLogHeaderDbVO = new BatchProcessLogHeaderDbVO();
      adaptedBatchProcessLogHeaderDbVO
          .setBatchProcessLogHeaderId(batchProcessLogDetailDbVO.getBatchProcessLogHeaderDbVO().getBatchLogHeaderId());
      batchProcessLogDetailDbVO.setBatchProcessLogHeaderDbVO(adaptedBatchProcessLogHeaderDbVO);
      adaptedBatchProcessLogDetailDbVOs.add(batchProcessLogDetailDbVO);
    }

    return new ResponseEntity<List<BatchProcessLogDetailDbVO>>(adaptedBatchProcessLogDetailDbVOs, HttpStatus.OK);
  }

  @RequestMapping(value = "/deletePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> deletePubItems(@RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @RequestBody List<String> itemIds)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.deletePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/releasePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> releasePubItems(@RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @RequestBody List<String> itemIds)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.releasePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/revisePubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> revisePubItems(@RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @RequestBody List<String> itemIds)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.revisePubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/submitPubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> submitPubItems(@RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @RequestBody List<String> itemIds)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.submitPubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }

  @RequestMapping(value = "/withdrawPubItems", method = RequestMethod.PUT)
  public ResponseEntity<BatchProcessLogHeaderDbVO> withdrawPubItems(@RequestHeader(value = AUTHZ_HEADER, required = true) String token,
      @RequestBody List<String> itemIds)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    BatchProcessLogHeaderDbVO batchProcessLogHeaderDbVO = this.batchProcessService.withdrawPubItems(itemIds, token);

    return new ResponseEntity<BatchProcessLogHeaderDbVO>(batchProcessLogHeaderDbVO, HttpStatus.OK);
  }
}
