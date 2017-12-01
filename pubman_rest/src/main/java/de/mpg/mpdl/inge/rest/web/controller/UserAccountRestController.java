package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.util.Date;

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

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.PropertyReader;

@RestController
@RequestMapping("/users")
public class UserAccountRestController {

  private final String AUTHZ_HEADER = "Authorization";
  private final String USER_ID_PATH = "/{userId}";
  private final String USER_ID_VAR = "userId";
  private UserAccountService userSvc;
  private UtilServiceBean utils;

  @Autowired
  public UserAccountRestController(UserAccountService userSvc, UtilServiceBean utils) {
    this.userSvc = userSvc;
    this.utils = utils;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AccountUserVO>> getAll(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token, @RequestParam(value = "limit",
      required = true, defaultValue = "10") int limit, @RequestParam(value = "offset",
      required = true, defaultValue = "0") int offset) throws AuthenticationException,
      AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty("inge.index.user.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest =
        new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<AccountUserVO> srResponse = userSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<AccountUserVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AccountUserVO>> filter(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "q") String query, @RequestParam(value = "limit", required = true,
          defaultValue = "10") int limit, @RequestParam(value = "offset", required = true,
          defaultValue = "0") int offset) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchQueryParam =
        QueryBuilders.boolQuery().filter(
            QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty("inge.index.user.sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest =
        new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<AccountUserVO> srResponse = userSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<AccountUserVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<AccountUserVO>> query(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token, @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);
    SearchRetrieveResponseVO<AccountUserVO> srResponse = userSvc.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<AccountUserVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<AccountUserVO> get(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @PathVariable(
          value = USER_ID_VAR) String userId) throws AuthenticationException,
      AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserVO user = null;
    if (token != null && !token.isEmpty()) {
      user = userSvc.get(userId, token);
    } else {
      user = userSvc.get(userId, null);
    }
    return new ResponseEntity<AccountUserVO>(user, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AccountUserVO> create(@RequestHeader(value = AUTHZ_HEADER) String token,
      @RequestBody AccountUserVO user) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    AccountUserVO created = null;
    created = userSvc.create(user, token);
    return new ResponseEntity<AccountUserVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody AccountUserVO user)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    AccountUserVO updated = null;
    updated = userSvc.update(user, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/add", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> addGrant(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody GrantVO[] grants)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    AccountUserVO user2AddGrants2 = userSvc.get(userId, token);
    AccountUserVO updated = null;
    updated = userSvc.addGrants(userId, user2AddGrants2.getLastModificationDate(), grants, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/remove", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> removeGrant(
      @RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody GrantVO[] grants)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    AccountUserVO user2RemoveGrantsFrom = userSvc.get(userId, token);
    AccountUserVO updated = null;
    updated =
        userSvc
            .removeGrants(userId, user2RemoveGrantsFrom.getLastModificationDate(), grants, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/activate", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> activate(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    Date lmd = utils.string2Date(modificationDate);
    AccountUserVO updated = null;
    updated = userSvc.activate(userId, lmd, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/deactivate", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> deactivate(
      @RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    Date lmd = utils.string2Date(modificationDate);
    AccountUserVO updated = null;
    updated = userSvc.deactivate(userId, lmd, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(
      value = USER_ID_VAR) String userId) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    userSvc.delete(userId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
