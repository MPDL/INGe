package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.util.PropertyReader;

@RestController
@RequestMapping("/ous")
public class OrganizationRestController {

  private final String AUTHZ_HEADER = "Authorization";
  private final String OU_ID_PATH = "/{ouId}";
  private final String OU_ID_VAR = "ouId";
  private OrganizationService organizationSvc;
  private UtilServiceBean utils;

  @Autowired
  public OrganizationRestController(OrganizationService ouSvc, UtilServiceBean utils) {
    this.organizationSvc = ouSvc;
    this.utils = utils;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>> getAll(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty("inge.index.organization.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = organizationSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>> filter(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty("inge.index.organization.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = organizationSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>> query(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = organizationSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/toplevel", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationDbVO>> topLevel()
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    List<AffiliationDbVO> response = organizationSvc.searchTopLevelOrganizations();
    return new ResponseEntity<List<AffiliationDbVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/children", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationDbVO>> childOrganizations(@PathVariable(value = OU_ID_VAR) String parentAffiliationId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    List<AffiliationDbVO> response = organizationSvc.searchChildOrganizations(parentAffiliationId);
    response.sort((ou1, ou2) -> ou1.getMetadata().getName().compareTo(ou2.getMetadata().getName()));
    return new ResponseEntity<List<AffiliationDbVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<AffiliationDbVO> get(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = OU_ID_VAR) String ouId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AffiliationDbVO ou = null;
    if (token != null && !token.isEmpty()) {
      ou = organizationSvc.get(ouId, token);
    } else {
      ou = organizationSvc.get(ouId, null);
    }
    return new ResponseEntity<AffiliationDbVO>(ou, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AffiliationDbVO> create(@RequestHeader(value = AUTHZ_HEADER) String token, @RequestBody AffiliationDbVO ou)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AffiliationDbVO created = null;
    created = organizationSvc.create(ou, token);
    return new ResponseEntity<AffiliationDbVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = OU_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> open(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AffiliationDbVO opened = null;
    Date lmd = utils.string2Date(modificationDate);
    opened = organizationSvc.open(ouId, lmd, token);
    return new ResponseEntity<AffiliationDbVO>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> close(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AffiliationDbVO closed = null;
    Date lmd = utils.string2Date(modificationDate);
    closed = organizationSvc.close(ouId, lmd, token);
    return new ResponseEntity<AffiliationDbVO>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody AffiliationDbVO ou)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AffiliationDbVO updated = null;
    updated = organizationSvc.update(ou, token);
    return new ResponseEntity<AffiliationDbVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(value = OU_ID_VAR) String ouId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    organizationSvc.delete(ouId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
