package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.FileVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.model.valueobjects.VersionHistoryEntryVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.util.PropertyReader;

@RestController
@RequestMapping("/items")
public class ItemRestController {

  private static final Logger logger = Logger.getLogger(ItemRestController.class);

  private final String ITEM_ID_PATH = "/{itemId}";
  private final String ITEM_ID_VAR = "itemId";

  @Autowired
  private PubItemService pis;

  @Autowired
  private UtilServiceBean utils;

  @Autowired
  private FileServiceExternal fileService;



  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<PubItemVO>> getAll(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(
      value = "limit", required = true, defaultValue = "10") int limit, @RequestParam(
      value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty("item_index_sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest =
        new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<PubItemVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<PubItemVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<PubItemVO>> filter(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(
      value = "q") String query, @RequestParam(value = "limit", required = true,
      defaultValue = "10") int limit, @RequestParam(value = "offset", required = true,
      defaultValue = "0") int offset) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchQueryParam =
        QueryBuilders.boolQuery().filter(
            QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty("item_index_sort"), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest =
        new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<PubItemVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<PubItemVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<PubItemVO>> query(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode query) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);
    SearchRetrieveResponseVO<PubItemVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<PubItemVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<PubItemVO> get(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    PubItemVO item = null;
    if (token != null && !token.isEmpty()) {
      item = pis.get(itemId, token);
    } else {
      item = pis.get(itemId, null);
    }
    return new ResponseEntity<PubItemVO>(item, HttpStatus.OK);
  }


  /**
   * Retrieve a file with a given ID
   * 
   * @param componentId
   * @param response
   */
  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/content",
      method = RequestMethod.GET)
  public void getComponentContent(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER,
      required = false) String token, @PathVariable String itemId,
      @PathVariable String componentId, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    try {
      OutputStream output = response.getOutputStream();
      FileVO fileVO = fileService.readFile(itemId, componentId, output, token);
      response.setContentType(fileVO.getMimeType());
      response.setHeader("Content-disposition", "attachment; filename=" + fileVO.getName());

      output.flush();
      output.close();
    } catch (IOException e) {
      logger.error("could not read file [" + componentId + "]");
      throw new IngeTechnicalException("Error while opening input stream", e);
    }
  }

  /**
   * Retrive the technical Metadata of a file
   * 
   * @param componentId
   * @return
   * @throws IOException
   * @throws SAXException
   * @throws TikaException
   */
  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/metadata",
      method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
  public String getTechnicalMetadataByTika(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String itemId, @PathVariable String componentId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    return fileService.getFileMetadata(itemId, componentId, token);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/history", method = RequestMethod.GET)
  public ResponseEntity<List<VersionHistoryEntryVO>> getVersionHistory(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId) throws AuthenticationException, AuthorizationException,
      IngeTechnicalException, IngeApplicationException {
    List<VersionHistoryEntryVO> list = null;
    list = pis.getVersionHistory(itemId, token);
    return new ResponseEntity<List<VersionHistoryEntryVO>>(list, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<PubItemVO> create(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @RequestBody PubItemVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    PubItemVO created = null;
    created = pis.create(item, token);
    return new ResponseEntity<PubItemVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/release", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> release(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    PubItemVO released = null;
    released =
        pis.releasePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<PubItemVO>(released, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/revise", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> revise(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    PubItemVO revised = null;
    revised =
        pis.revisePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<PubItemVO>(revised, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/submit", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> submit(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    PubItemVO submitted = null;
    submitted =
        pis.submitPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<PubItemVO>(submitted, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/withdraw", method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> withdraw(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    PubItemVO withdrawn = null;
    withdrawn =
        pis.withdrawPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<PubItemVO>(withdrawn, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<PubItemVO> update(@RequestHeader(
      value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
      value = ITEM_ID_VAR) String itemId, @RequestBody PubItemVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    PubItemVO updated = null;
    updated = pis.update(item, token);
    return new ResponseEntity<PubItemVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, @PathVariable(
          value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {
    pis.delete(itemId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
