package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.util.Date;

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

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import de.mpg.mpdl.inge.model.db.valueobjects.ContextDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/contexts")
@Tag(name = "Contexts")
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

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>> query(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);
    SearchRetrieveResponseVO<ContextDbVO> srResponse = ctxSvc.search(srRequest, token);

    return new ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>> getAll(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "from", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_CONTEXT_SORT), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<ContextDbVO> srResponse = ctxSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>>(srResponse, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>> filter(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "q") String query, //
      @RequestParam(value = "size", required = true, defaultValue = "10") int limit, //
      @RequestParam(value = "from", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //QueryBuilder matchQueryParam = QueryBuilders.queryStringQuery(query);
    Query matchQueryParam = QueryStringQuery.of(q -> q.query(query))._toQuery();
    //QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_CONTEXT_SORT), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<ContextDbVO> srResponse = ctxSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<?> get(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    ContextDbVO ctx = null;
    if (token != null && !token.isEmpty()) {
      ctx = ctxSvc.get(ctxId, token);
    } else {
      ctx = ctxSvc.get(ctxId, null);
    }
    if (ctx != null) {
      return new ResponseEntity<ContextDbVO>(ctx, HttpStatus.OK);
    } else {
      throw new NotFoundException();
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ContextDbVO> create(@RequestHeader(value = AUTHZ_HEADER) String token, @RequestBody ContextDbVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextDbVO created = null;
    created = ctxSvc.create(ctx, token);
    return new ResponseEntity<ContextDbVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = CTX_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<ContextDbVO> open(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = CTX_ID_VAR) String ctxId,
      @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = utils.string2Date(modificationDate);
    ContextDbVO opened = null;
    opened = ctxSvc.open(ctxId, lmd, token);
    return new ResponseEntity<ContextDbVO>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<ContextDbVO> close(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = utils.string2Date(modificationDate);
    ContextDbVO closed = null;
    closed = ctxSvc.close(ctxId, lmd, token);
    return new ResponseEntity<ContextDbVO>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ContextDbVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId, @RequestBody ContextDbVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextDbVO updated = null;
    updated = ctxSvc.update(ctx, token);
    return new ResponseEntity<ContextDbVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = CTX_ID_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ctxSvc.delete(ctxId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
