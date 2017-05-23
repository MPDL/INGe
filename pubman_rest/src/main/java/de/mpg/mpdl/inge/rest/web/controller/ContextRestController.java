package de.mpg.mpdl.inge.rest.web.controller;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.ContextService;

@RestController
@RequestMapping("/contexts")
public class ContextRestController {

  private final String AUTHZ_HEADER = "Authorization";
  private final String CTX_ID_PATH = "/{ctxId}";
  private final String CTX_ID_VAR = "ctxId";
  private ContextService ctxSvc;

  @Autowired
  public ContextRestController(ContextService ctxSvc) {
    this.ctxSvc = ctxSvc;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token,
      @RequestBody SearchRetrieveRequestVO<QueryBuilder> srr) throws AaException,
      IngeServiceException {
    SearchRetrieveResponseVO<ContextVO> response = ctxSvc.search(srr, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ContextVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<ContextVO> get(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @PathVariable(
          value = CTX_ID_VAR) String ctxId) throws AaException, IngeServiceException {
    ContextVO ctx = null;
    if (token != null && !token.isEmpty()) {
      ctx = ctxSvc.get(ctxId, token);
    } else {
      ctx = ctxSvc.get(ctxId, null);
    }
    return new ResponseEntity<ContextVO>(ctx, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ContextVO> create(@RequestHeader(value = AUTHZ_HEADER) String token,
      @RequestBody ContextVO ctx) throws AaException, IngeServiceException, ItemInvalidException {
    ContextVO created = null;
    created = ctxSvc.create(ctx, token);
    return new ResponseEntity<ContextVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = CTX_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<ContextVO> open(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId) throws AaException, IngeServiceException {
    ContextVO opened = null;
    opened = ctxSvc.open(ctxId, token);
    return new ResponseEntity<ContextVO>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<ContextVO> close(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId) throws AaException, IngeServiceException {
    ContextVO closed = null;
    closed = ctxSvc.close(ctxId, token);
    return new ResponseEntity<ContextVO>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ContextVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId, @RequestBody ContextVO ctx)
      throws AaException, IngeServiceException, ItemInvalidException {
    ContextVO updated = null;
    updated = ctxSvc.update(ctx, token);
    return new ResponseEntity<ContextVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(
      value = CTX_ID_VAR) String ctxId) throws AaException, IngeServiceException {
    ctxSvc.delete(ctxId, token);
    return new ResponseEntity<>(HttpStatus.GONE);
  }

}
