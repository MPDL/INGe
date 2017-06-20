package de.mpg.mpdl.inge.rest.web.controller;

import java.util.ArrayList;
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

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
import de.mpg.mpdl.inge.model.valueobjects.AccountUserVO;
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.GrantVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.OrganizationService;
import de.mpg.mpdl.inge.service.pubman.UserAccountService;

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
  public ResponseEntity<List<AccountUserVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token) throws AaException,
      IngeServiceException {
	  QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
	  SearchRetrieveRequestVO<QueryBuilder> srRequest = new SearchRetrieveRequestVO<QueryBuilder>(matchAllQuery);
    SearchRetrieveResponseVO<AccountUserVO> srResponse = userSvc.search(srRequest, token);
    List<AccountUserVO> response = new ArrayList<AccountUserVO>();;
    srResponse.getRecords().forEach(record -> response.add(record.getData()));
    return new ResponseEntity<List<AccountUserVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<List<AccountUserVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query) throws AaException,
      IngeServiceException {
	  QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
	  SearchRetrieveRequestVO<QueryBuilder> srRequest = new SearchRetrieveRequestVO<QueryBuilder>(matchQueryParam);
    SearchRetrieveResponseVO<AccountUserVO> srResponse = userSvc.search(srRequest, token);
    List<AccountUserVO> response = new ArrayList<AccountUserVO>();;
    srResponse.getRecords().forEach(record -> response.add(record.getData()));
    return new ResponseEntity<List<AccountUserVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<AccountUserVO> get(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @PathVariable(
          value = USER_ID_VAR) String userId) throws AaException, IngeServiceException {
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
      @RequestBody AccountUserVO user) throws AaException, IngeServiceException,
      ItemInvalidException {
    AccountUserVO created = null;
    created = userSvc.create(user, token);
    return new ResponseEntity<AccountUserVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody AccountUserVO user)
      throws AaException, IngeServiceException, ItemInvalidException {
    AccountUserVO updated = null;
    updated = userSvc.update(user, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/add", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> addGrant(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody GrantVO[] grants)
      throws AaException, IngeServiceException, ItemInvalidException {
    AccountUserVO user2AddGrants2 = userSvc.get(userId, token);
    AccountUserVO updated = null;
    updated = userSvc.addGrants(userId, user2AddGrants2.getLastModificationDate(), grants, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH + "/remove", method = RequestMethod.PUT)
  public ResponseEntity<AccountUserVO> removeGrant(
      @RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = USER_ID_VAR) String userId, @RequestBody GrantVO[] grants)
      throws AaException, IngeServiceException, ItemInvalidException {
    AccountUserVO user2RemoveGrantsFrom = userSvc.get(userId, token);
    AccountUserVO updated = null;
    updated =
        userSvc
            .removeGrants(userId, user2RemoveGrantsFrom.getLastModificationDate(), grants, token);
    return new ResponseEntity<AccountUserVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = USER_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(
      value = USER_ID_VAR) String userId) throws AaException, IngeServiceException {
    userSvc.delete(userId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
