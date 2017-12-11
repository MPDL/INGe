package de.mpg.mpdl.inge.rest.web.controller;

import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ContextVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.util.PropertyReader;

@RestController
@RequestMapping("/contexts")
public class ContextRestController {

  private final String AUTHZ_HEADER = "Authorization";
  private final String CTX_ID_PATH = "/{ctxId}";
  private final String CTX_ID_VAR = "ctxId";

  private ContextService ctxSvc;
  private UtilServiceBean utils;

  @Autowired
  public ContextRestController(ContextService ctxSvc, UtilServiceBean utils) {
    this.ctxSvc = ctxSvc;
    this.utils = utils;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextVO>> getAll(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty("inge.index.context.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<ContextVO> srResponse = ctxSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ContextVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextVO>> filter(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "q") String query, @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty("inge.index.context.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<ContextVO> srResponse = ctxSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ContextVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<?> get(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextVO ctx = null;
    if (token != null && !token.isEmpty()) {
      ctx = ctxSvc.get(ctxId, token);
    } else {
      ctx = ctxSvc.get(ctxId, null);
    }
    if (ctx != null) {
      return new ResponseEntity<ContextVO>(ctx, HttpStatus.OK);
    } else {
      VndError error = new VndError("404 - NOT FOUND", "could not get context with id: " + ctxId);
      return new ResponseEntity<VndError>(error, HttpStatus.NOT_FOUND);
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ContextVO> create(@RequestHeader(value = AUTHZ_HEADER) String token, @RequestBody ContextVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextVO created = null;
    created = ctxSvc.create(ctx, token);
    return new ResponseEntity<ContextVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = CTX_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<ContextVO> open(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = CTX_ID_VAR) String ctxId,
      @RequestBody String modificatioDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = utils.string2Date(modificatioDate);
    ContextVO opened = null;
    opened = ctxSvc.open(ctxId, lmd, token);
    return new ResponseEntity<ContextVO>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<ContextVO> close(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = CTX_ID_VAR) String ctxId,
      @RequestBody String modificatioDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = utils.string2Date(modificatioDate);
    ContextVO closed = null;
    closed = ctxSvc.close(ctxId, lmd, token);
    return new ResponseEntity<ContextVO>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ContextVO> update(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = CTX_ID_VAR) String ctxId,
      @RequestBody ContextVO ctx) throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextVO updated = null;
    updated = ctxSvc.update(ctx, token);
    return new ResponseEntity<ContextVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = CTX_ID_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ctxSvc.delete(ctxId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
