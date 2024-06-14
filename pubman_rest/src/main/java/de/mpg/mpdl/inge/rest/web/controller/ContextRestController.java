package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;
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
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
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

  private static final String CTX_ID_PATH = "/{ctxId}";
  private static final String CTX_ID_VAR = "ctxId";

  private final ContextService ctxSvc;
  private final UtilServiceBean utils;

  @Autowired
  public ContextRestController(ContextService ctxSvc, UtilServiceBean utils) {
    this.ctxSvc = ctxSvc;
    this.utils = utils;
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>> query(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = this.utils.query2VO(query);
    SearchRetrieveResponseVO<ContextDbVO> srResponse = this.ctxSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "/elasticsearch", method = RequestMethod.POST,
      consumes = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<String> searchDetailed(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode searchSource, @RequestParam(name = "scroll", required = false) String scrollTimeValue,
      HttpServletResponse httpResponse)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    return UtilServiceBean.searchDetailed(this.ctxSvc, searchSource, scrollTimeValue, token, httpResponse);
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_CONTEXT_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<ContextDbVO> srResponse = this.ctxSvc.search(srRequest, token);
    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ContextDbVO>> filter(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query, //
      @RequestParam(value = "size", defaultValue = "10") int limit, //
      @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //QueryBuilder matchQueryParam = QueryBuilders.queryStringQuery(query);
    Query matchQueryParam = QueryStringQuery.of(q -> q.query(query))._toQuery();
    //QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_CONTEXT_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<ContextDbVO> srResponse = this.ctxSvc.search(srRequest, token);
    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<?> get(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    ContextDbVO ctx = null;
    if (null != token && !token.isEmpty()) {
      ctx = this.ctxSvc.get(ctxId, token);
    } else {
      ctx = this.ctxSvc.get(ctxId, null);
    }
    if (null != ctx) {
      return new ResponseEntity<>(ctx, HttpStatus.OK);
    } else {
      throw new NotFoundException();
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ContextDbVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody ContextDbVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextDbVO created = null;
    created = this.ctxSvc.create(ctx, token);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = CTX_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<ContextDbVO> open(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = this.utils.string2Date(modificationDate);
    ContextDbVO opened = null;
    opened = this.ctxSvc.open(ctxId, lmd, token);
    return new ResponseEntity<>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<ContextDbVO> close(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = this.utils.string2Date(modificationDate);
    ContextDbVO closed = null;
    closed = this.ctxSvc.close(ctxId, lmd, token);
    return new ResponseEntity<>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ContextDbVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId, @RequestBody ContextDbVO ctx)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ContextDbVO updated = null;
    updated = this.ctxSvc.update(ctx, token);
    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = CTX_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = CTX_ID_VAR) String ctxId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    this.ctxSvc.delete(ctxId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
