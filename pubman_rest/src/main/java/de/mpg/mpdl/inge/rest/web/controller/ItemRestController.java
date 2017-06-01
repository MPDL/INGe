package de.mpg.mpdl.inge.rest.web.controller;

import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.mpg.mpdl.inge.inge_validation.exception.ItemInvalidException;
import de.mpg.mpdl.inge.model.exception.IngeServiceException;
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
  public ResponseEntity<SearchRetrieveResponseVO<PubItemVO>> search(@RequestHeader(
      value = AUTHZ_HEADER, required = false) String token,
      @RequestBody SearchRetrieveRequestVO<QueryBuilder> srr) throws AaException,
      IngeServiceException {
    SearchRetrieveResponseVO<PubItemVO> response = pis.search(srr, token);
    return new ResponseEntity<SearchRetrieveResponseVO<PubItemVO>>(response, HttpStatus.OK);
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
    PubItemVO released = null;
    released = pis.releasePubItem(itemId, message, token);
    return new ResponseEntity<PubItemVO>(released, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/revise", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> revise(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO revised = null;
    revised = pis.revisePubItem(itemId, message, token);
    return new ResponseEntity<PubItemVO>(revised, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/submit", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> submit(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO submitted = null;
    submitted = pis.submitPubItem(itemId, message, token);
    return new ResponseEntity<PubItemVO>(submitted, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/withdraw", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> withdraw(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody String message)
      throws AaException, IngeServiceException, ItemInvalidException {
    PubItemVO withdrawn = null;
    withdrawn = pis.withdrawPubItem(itemId, message, token);
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
    pis.delete(itemId, token);
    return new ResponseEntity<>(HttpStatus.GONE);
  }

}
