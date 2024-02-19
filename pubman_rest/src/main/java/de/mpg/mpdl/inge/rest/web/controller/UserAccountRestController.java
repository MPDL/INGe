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
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.MediaType;

@RestController
@RequestMapping("/users")
@Tag(name = "User Accounts")
public class UserAccountRestController {

  private static final String USER_ID_PATH = "/{userId}";
  private static final String USER_ID_VAR = "userId";

  private final UserAccountService userSvc;
  private final UtilServiceBean utils;

  @Autowired
  public UserAccountRestController(UserAccountService userSvc, UtilServiceBean utils) {
    this.userSvc = userSvc;
    this.utils = utils;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AccountUserDbVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_USER_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<AccountUserDbVO> srResponse = this.userSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<AccountUserDbVO>> filter(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Query matchQueryParam = QueryStringQuery.of(q -> q.query(query))._toQuery();
    //QueryBuilder matchQueryParam = QueryBuilders.queryStringQuery(query);
    //QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_USER_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<AccountUserDbVO> srResponse = this.userSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<AccountUserDbVO>> query(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = this.utils.query2VO(query);
    SearchRetrieveResponseVO<AccountUserDbVO> srResponse = this.userSvc.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<AccountUserDbVO> get(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = USER_ID_VAR) String userId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    AccountUserDbVO user = this.userSvc.get(userId, token);
    if (null != user) {
      return new ResponseEntity<>(user, HttpStatus.OK);
    } else {
      throw new NotFoundException();
    }
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<AccountUserDbVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody AccountUserDbVO user)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO created = this.userSvc.create(user, token);

    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<AccountUserDbVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody AccountUserDbVO user)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO updated = this.userSvc.update(user, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/add", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserDbVO> addGrant(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody GrantVO[] grants)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO user2AddGrants2 = this.userSvc.get(userId, token);
    AccountUserDbVO updated = this.userSvc.addGrants(userId, user2AddGrants2.getLastModificationDate(), grants, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/remove", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserDbVO> removeGrant(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody GrantVO[] grants)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO user2RemoveGrantsFrom = this.userSvc.get(userId, token);
    AccountUserDbVO updated = this.userSvc.removeGrants(userId, user2RemoveGrantsFrom.getLastModificationDate(), grants, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/activate", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserDbVO> activate(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = this.utils.string2Date(modificationDate);
    AccountUserDbVO updated = this.userSvc.activate(userId, lmd, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/deactivate", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserDbVO> deactivate(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody String modificationDate)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Date lmd = this.utils.string2Date(modificationDate);
    AccountUserDbVO updated = this.userSvc.deactivate(userId, lmd, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/password", method = RequestMethod.PUT)
  @Hidden
  public ResponseEntity<AccountUserDbVO> changePassword(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody String changedPassword)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    AccountUserDbVO user = this.userSvc.get(userId, token);
    Date lmd = user.getLastModificationDate();
    AccountUserDbVO updated = this.userSvc.changePassword(userId, lmd, changedPassword, false, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    this.userSvc.delete(userId, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/generateRandomPassword", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN)
  public ResponseEntity<String> generateRandomPassword(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token) {
    String pw = this.userSvc.generateRandomPassword();

    return new ResponseEntity<>(pw, HttpStatus.OK);
  }
}
