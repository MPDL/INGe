package de.mpg.mpdl.inge.rest.web.controller;

import java.util.ArrayList;
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
import de.mpg.mpdl.inge.model.valueobjects.AffiliationVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.service.exceptions.AaException;
import de.mpg.mpdl.inge.service.pubman.PubItemService;

@RestController
@RequestMapping("/items")
public class ItemRestController {

  private final String AUTHZ_HEADER = "Authorization";
  private final String ITEM_ID_PATH = "/{itemId}";
  private final String ITEM_ID_VAR = "itemId";
  private PubItemService pis;

  @Autowired
  public ItemRestController(PubItemService pis) {
    this.pis = pis;
  }

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<List<PubItemVO>> getAll(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token,
		  @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
		  @RequestParam(value = "offset", required = true, defaultValue = "0") int offset) throws AaException,
      IngeServiceException {
	  QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
	  SearchRetrieveRequestVO<QueryBuilder> srRequest = new SearchRetrieveRequestVO<QueryBuilder>(matchAllQuery, limit, offset);
    SearchRetrieveResponseVO<PubItemVO> srResponse = pis.search(srRequest, token);
    List<PubItemVO> response = new ArrayList<PubItemVO>();;
    srResponse.getRecords().forEach(record -> response.add(record.getData()));
    return new ResponseEntity<List<PubItemVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<List<PubItemVO>> getFiltered(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token,
		  @RequestParam(value = "q") String query,
		  @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
		  @RequestParam(value = "offset", required = true, defaultValue = "0") int offset) throws AaException,
      IngeServiceException {
	  QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
	  SearchRetrieveRequestVO<QueryBuilder> srRequest = new SearchRetrieveRequestVO<QueryBuilder>(matchQueryParam, limit, offset);
    SearchRetrieveResponseVO<PubItemVO> srResponse = pis.search(srRequest, token);
    List<PubItemVO> response = new ArrayList<PubItemVO>();;
    srResponse.getRecords().forEach(record -> response.add(record.getData()));
    return new ResponseEntity<List<PubItemVO>>(response, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<PubItemVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token, @RequestBody String query,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AaException, IngeServiceException {
    QueryBuilder matchQueryParam = QueryBuilders.wrapperQuery(query);
    SearchRetrieveRequestVO<QueryBuilder> srRequest =
        new SearchRetrieveRequestVO<QueryBuilder>(matchQueryParam, limit, offset);
    SearchRetrieveResponseVO<PubItemVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<PubItemVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<PubItemVO> get(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @PathVariable(
          value = ITEM_ID_VAR) String itemId) throws AaException, IngeServiceException {
    PubItemVO item = null;
    if (token != null && !token.isEmpty()) {
      item = pis.get(itemId, token);
    } else {
      item = pis.get(itemId, null);
    }
    return new ResponseEntity<PubItemVO>(item, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/history", method = RequestMethod.GET)
  public ResponseEntity<List<VersionHistoryEntryVO>> getVersionHistory(@RequestHeader(
      value = AUTHZ_HEADER) String token, @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws AaException, IngeServiceException {
    List<VersionHistoryEntryVO> list = null;
    list = pis.getVersionHistory(itemId, token);
    return new ResponseEntity<List<VersionHistoryEntryVO>>(list, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<PubItemVO> create(@RequestHeader(value = AUTHZ_HEADER) String token,
      @RequestBody PubItemVO item) throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO created = null;
    created = pis.create(item, token);
    return new ResponseEntity<PubItemVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/release", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> release(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO item2BeReleased = pis.get(itemId, token);
    PubItemVO released = null;
    released = pis.releasePubItem(itemId, item2BeReleased.getModificationDate(), message, token);
    return new ResponseEntity<PubItemVO>(released, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/revise", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> revise(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO item2BeRevised = pis.get(itemId, token);
    PubItemVO revised = null;
    revised = pis.revisePubItem(itemId, item2BeRevised.getModificationDate(), message, token);
    return new ResponseEntity<PubItemVO>(revised, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/submit", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> submit(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO item2BeSubmitted = pis.get(itemId, token);
    PubItemVO submitted = null;
    submitted = pis.submitPubItem(itemId, item2BeSubmitted.getModificationDate(), message, token);
    return new ResponseEntity<PubItemVO>(submitted, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/withdraw", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> withdraw(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO item2BeWithdrawn = pis.get(itemId, token);
    PubItemVO withdrawn = null;
    withdrawn = pis.withdrawPubItem(itemId, item2BeWithdrawn.getModificationDate(), message, token);
    return new ResponseEntity<PubItemVO>(withdrawn, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody PubItemVO item)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO updated = null;
    updated = pis.update(item, token);
    return new ResponseEntity<PubItemVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId) throws AaException, IngeServiceException {
    PubItemVO item2BeDeleted = pis.get(itemId, token);
    pis.delete(itemId, item2BeDeleted.getModificationDate(), token);
    return new ResponseEntity<>(HttpStatus.GONE);
  }

}