package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbRO;
import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRecordVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/ous")
@Tag(name = "Organizations")
public class OrganizationRestController {

  private static final String OU_ID_PATH = "/{ouId}";
  private static final String OU_ID_VAR = "ouId";

  private final OrganizationService organizationSvc;
  private final UtilServiceBean utils;

  @Autowired
  public OrganizationRestController(OrganizationService ouSvc, UtilServiceBean utils) {
    this.organizationSvc = ouSvc;
    this.utils = utils;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    //QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ORGANIZATION_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = this.organizationSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "/xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML + ";charset=UTF-8")
  public ResponseEntity<String> getAllAsXml(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    //QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ORGANIZATION_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = this.organizationSvc.search(srRequest, token);
    StringBuilder xml = new StringBuilder();
    xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    xml.append("<root>");
    xml.append("<records>");
    for (SearchRetrieveRecordVO<AffiliationDbVO> record : srResponse.getRecords()) {
      xml.append("<record>");
      AffiliationDbVO affiliation = record.getData();

      String name = affiliation.getName() //
          .replaceAll("&", "&amp;") //
          .replaceAll("'", "&apos;") //
          .replaceAll("<", "&lt;") //
          .replaceAll(">", "&gt;") //
          .replaceAll("\"", "&quot;");
      xml.append("<name>");
      xml.append(name);
      xml.append("</name>");

      String objectId = affiliation.getObjectId();
      xml.append("<objectId>");
      xml.append(objectId);
      xml.append("</objectId>");

      AffiliationDbRO parentAffiliation = affiliation.getParentAffiliation();
      String parent = "";
      if (null != parentAffiliation && null != parentAffiliation.getName()) {
        parent = parentAffiliation.getName() //
            .replaceAll("&", "&amp;") //
            .replaceAll("'", "&apos;") //
            .replaceAll("<", "&lt;") //
            .replaceAll(">", "&gt;") //
            .replaceAll("\"", "&quot;");
      }
      xml.append("<parent>");
      xml.append(parent);
      xml.append("</parent>");

      String parentId = "";
      if (null != parentAffiliation && null != parentAffiliation.getObjectId()) {
        parentId = parentAffiliation.getObjectId();
      }
      xml.append("<parentId>");
      xml.append(parentId);
      xml.append("</parentId>");

      xml.append("</record>");
    }
    xml.append("</records>");
    xml.append("</root>");

    return new ResponseEntity<>(xml.toString(), HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>> filter(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    Query matchQueryParam = QueryStringQuery.of(q -> q.query(query))._toQuery();
    //QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ORGANIZATION_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = this.organizationSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<AffiliationDbVO>> query(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    SearchRetrieveRequestVO srRequest = this.utils.query2VO(query);
    SearchRetrieveResponseVO<AffiliationDbVO> srResponse = this.organizationSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/toplevel", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationDbVO>> topLevel()
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<AffiliationDbVO> response = this.organizationSvc.searchTopLevelOrganizations();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/firstlevel", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationDbVO>> firstLevel()
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<AffiliationDbVO> response = this.organizationSvc.searchFirstLevelOrganizations();

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/children", method = RequestMethod.GET)
  public ResponseEntity<List<AffiliationDbVO>> childOrganizations(@PathVariable(value = OU_ID_VAR) String parentAffiliationId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<AffiliationDbVO> response = this.organizationSvc.searchChildOrganizations(parentAffiliationId);
    response.sort(Comparator.comparing(ou -> ou.getMetadata().getName()));

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/allchildren/" + OU_ID_PATH, method = RequestMethod.POST)
  public ResponseEntity<List<AffiliationDbVO>> allChildOrganizations(@PathVariable(value = OU_ID_VAR) String ignoreOuId,
      @RequestBody String[] parentAffiliationIds)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    List<AffiliationDbVO> response = this.organizationSvc.searchAllChildOrganizations(parentAffiliationIds, ignoreOuId);
    response.sort(Comparator.comparing(ou -> ou.getMetadata().getName()));

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<AffiliationDbVO> get(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = OU_ID_VAR) String ouId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    AffiliationDbVO ou = null;
    if (null != token && !token.isEmpty()) {
      ou = this.organizationSvc.get(ouId, token);
    } else {
      ou = this.organizationSvc.get(ouId, null);
    }
    if (null == ou) {
      throw new NotFoundException();
    }

    return new ResponseEntity<>(ou, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AffiliationDbVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody AffiliationDbVO ou)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    AffiliationDbVO created = null;
    created = this.organizationSvc.create(ou, token);

    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = OU_ID_PATH + "/open", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> open(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    AffiliationDbVO opened = null;
    Date lmd = this.utils.string2Date(modificationDate);
    opened = this.organizationSvc.open(ouId, lmd, token);

    return new ResponseEntity<>(opened, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/close", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> close(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    AffiliationDbVO closed = null;
    Date lmd = this.utils.string2Date(modificationDate);
    closed = this.organizationSvc.close(ouId, lmd, token);

    return new ResponseEntity<>(closed, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @RequestBody AffiliationDbVO ou)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    AffiliationDbVO updated = null;
    updated = this.organizationSvc.update(ou, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    this.organizationSvc.delete(ouId, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/idPath", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN)
  public ResponseEntity<String> idPath(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = OU_ID_VAR) String ouId) throws IngeApplicationException {

    List<String> idPath = this.organizationSvc.getIdPath(ouId);
    StringBuilder ouIdPath = new StringBuilder();
    int i = 0;
    for (String ou : idPath) {
      i++;
      ouIdPath.append(ou);
      if (i < idPath.size()) {
        ouIdPath.append(",");
      }
    }

    return new ResponseEntity<>(ouIdPath.toString(), HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/ouPath", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN)
  public ResponseEntity<String> ouPath(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = OU_ID_VAR) String ouId) throws IngeApplicationException {

    String ouPath = this.organizationSvc.getOuPath(ouId);

    return new ResponseEntity<>(ouPath, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/remove/{predecessorId}", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> removePredecessor(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @PathVariable String predecessorId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    Date lmd = this.utils.string2Date(modificationDate);
    AffiliationDbVO updated = this.organizationSvc.removePredecessor(ouId, lmd, predecessorId, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = OU_ID_PATH + "/add/{predecessorId}", method = RequestMethod.PUT)
  public ResponseEntity<AffiliationDbVO> addPredecessor(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = OU_ID_VAR) String ouId, @PathVariable String predecessorId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    Date lmd = this.utils.string2Date(modificationDate);
    AffiliationDbVO updated = this.organizationSvc.addPredecessor(ouId, lmd, predecessorId, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "/elasticsearch", method = RequestMethod.POST,
      consumes = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<String> searchDetailed(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode searchSource, @RequestParam(name = "scroll", required = false) String scrollTimeValue,
      HttpServletResponse httpResponse)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    return UtilServiceBean.searchDetailed(this.organizationSvc, searchSource, scrollTimeValue, token, httpResponse);
  }
}
