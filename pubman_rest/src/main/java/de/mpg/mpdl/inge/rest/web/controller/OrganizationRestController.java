package de.mpg.mpdl.inge.rest.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import de.mpg.mpdl.inge.inge_validation.exception.ValidationException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.ContextService;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;

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
  public ResponseEntity<List<AffiliationVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token) throws AaException,
      IngeServiceException {
	  QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
	  SearchSortCriteria sorting = new SearchSortCriteria("defaultMetadata.name.sorted", SortOrder.ASC);
	  SearchRetrieveRequestVO<QueryBuilder> srRequest = new SearchRetrieveRequestVO<QueryBuilder>(matchAllQuery, sorting);
    SearchRetrieveResponseVO<AffiliationVO> srResponse = organizationSvc.search(srRequest, token);
    List<AffiliationVO> response = new ArrayList<AffiliationVO>();;
    srResponse.getRecords().forEach(record -> response.add(record.getData()));
    return new ResponseEntity<List<AffiliationVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query) throws AaException,
      IngeServiceException {
	  QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
	  SearchSortCriteria sorting = new SearchSortCriteria("defaultMetadata.name.sorted", SortOrder.ASC);
	  SearchRetrieveRequestVO<QueryBuilder> srRequest = new SearchRetrieveRequestVO<QueryBuilder>(matchQueryParam, sorting);
    SearchRetrieveResponseVO<AffiliationVO> srResponse = organizationSvc.search(srRequest, token);
    List<AffiliationVO> response = new ArrayList<AffiliationVO>();;
    srResponse.getRecords().forEach(record -> response.add(record.getData()));
    return new ResponseEntity<List<AffiliationVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/toplevel", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationVO>> searchTopLevel() throws AaException,
      IngeServiceException {
    List<AffiliationVO> response = organizationSvc.searchTopLevelOrganizations();
    return new ResponseEntity<List<AffiliationVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/children", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationVO>> searchChildOrganizations(@PathVariable(
      value = OU_ID_VAR) String parentAffiliationId) throws AaException, IngeServiceException {
    List<AffiliationVO> response = organizationSvc.searchChildOrganizations(parentAffiliationId);
    response.sort((ou1, ou2) -> ou1.getDefaultMetadata().getName().compareTo(ou2.getDefaultMetadata().getName()));
    return new ResponseEntity<List<AffiliationVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<AffiliationVO> get(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @PathVariable(
          value = OU_ID_VAR) String ouId) throws AaException, IngeServiceException {
    AffiliationVO ou = null;
    if (token != null && !token.isEmpty()) {
      ou = organizationSvc.get(ouId, token);
    } else {
      ou = organizationSvc.get(ouId, null);
    }
    return new ResponseEntity<AffiliationVO>(ou, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AffiliationVO> create(@RequestHeader(value = AUTHZ_HEADER) String token,
      @RequestBody AffiliationVO ou) throws AaException, IngeServiceException, ValidationException {
    AffiliationVO created = null;
    created = organizationSvc.create(ou, token);
    return new ResponseEntity<AffiliationVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = OU_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationVO> open(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody String modificationDate)
      throws AaException, IngeServiceException {
    AffiliationVO opened = null;
    Date lmd = utils.string2Date(modificationDate);
    opened = organizationSvc.open(ouId, lmd, token);
    return new ResponseEntity<AffiliationVO>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationVO> close(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody String modificationDate)
      throws AaException, IngeServiceException {
    AffiliationVO closed = null;
    Date lmd = utils.string2Date(modificationDate);
    closed = organizationSvc.close(ouId, lmd, token);
    return new ResponseEntity<AffiliationVO>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<AffiliationVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody AffiliationVO ou)
      throws AaException, IngeServiceException, ValidationException {
    AffiliationVO updated = null;
    updated = organizationSvc.update(ou, token);
    return new ResponseEntity<AffiliationVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(
      value = OU_ID_VAR) String ouId, @RequestBody String modificationDate) throws AaException,
      IngeServiceException {
    Date lmd = utils.string2Date(modificationDate);
    organizationSvc.delete(ouId, lmd, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
