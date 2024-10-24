package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.service.pubman.impl.FileVOWrapper;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/items")
@Api(tags = "Items / Publications")
public class ItemRestController {

  private static final Logger logger = Logger.getLogger(ItemRestController.class);

  private final String ITEM_ID_PATH = "/{itemId}";
  private final String ITEM_ID_VAR = "itemId";

  public final static long DEFAULT_SCROLL_TIME = 90000;

  public static final String EXPORT_FORMAT_ALLOWABLE_VALUES = TransformerFactory.JSON + "," + TransformerFactory.ESCIDOC_ITEMLIST_XML + ","
      + TransformerFactory.BIBTEX + "," + TransformerFactory.ENDNOTE + "," + TransformerFactory.MARC_XML + "," + TransformerFactory.PDF
      + "," + TransformerFactory.DOCX + "," + TransformerFactory.HTML_PLAIN + "," + TransformerFactory.HTML_LINKED + ","
      + TransformerFactory.JSON_CITATION + "," + TransformerFactory.ESCIDOC_SNIPPET;

  public static final String EXPORT_CITATION_ALLOWABLE_VALUES = "APA, APA(CJK), AJP, JUS, CSL";

  @Autowired
  private PubItemService pis;

  @Autowired
  private UtilServiceBean utils;

  @Autowired
  private FileServiceExternal fileService;

  @Autowired
  private SearchAndExportService saes;

  @ApiIgnore
  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "from", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    //QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_SORT), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>>(srResponse, HttpStatus.OK);
  }

  @ApiIgnore
  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> filter(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query,
      @RequestParam(value = "size", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "from", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //QueryBuilder matchQueryParam = QueryBuilders.queryStringQuery(query);
    Query matchQueryParam = QueryStringQuery.of(q -> q.query(query))._toQuery();
    //QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting = new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_SORT), SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = pis.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> search( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, // 
      @RequestParam(value = "format", required = false,
          defaultValue = "json") @ApiParam(allowableValues = EXPORT_FORMAT_ALLOWABLE_VALUES) String format, //
      @RequestParam(value = "citation", required = false,
          defaultValue = "APA") @ApiParam(allowableValues = EXPORT_CITATION_ALLOWABLE_VALUES) String citation, //
      @RequestParam(value = "cslConeId", required = false) String cslConeId, //
      @RequestParam(value = "scroll", required = false) boolean scroll, //
      @RequestBody JsonNode query, //
      HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);


    return utils.searchOrExport(format, citation, cslConeId, scroll, srRequest, response, token);


  }



  @RequestMapping(value = "/search/scroll", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> searchScroll( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, //
      @RequestParam(value = "format", required = false) String format, //
      @RequestParam(value = "citation", required = false) String citation, //
      @RequestParam(value = "cslConeId", required = false) String cslConeId, //
      @RequestParam(value = "scrollId", required = true) String scrollId, //
      HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    ResponseBody searchResp = pis.scrollOn(scrollId, DEFAULT_SCROLL_TIME);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse =
        SearchUtils.getSearchRetrieveResponseFromElasticSearchResponse(searchResp, ItemVersionVO.class);

    if (format == null || format.equals(TransformerFactory.JSON)) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-total-number-of-results", "" + srResponse.getNumberOfRecords());
      headers.add("scrollId", srResponse.getScrollId());
      return new ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>>(srResponse, headers, HttpStatus.OK);
    }

    ExportFormatVO exportFormat = new ExportFormatVO(format, citation, cslConeId);
    SearchAndExportRetrieveRequestVO saerrVO = new SearchAndExportRetrieveRequestVO(srResponse, exportFormat);
    SearchAndExportResultVO saerVO = this.saes.exportItems(saerrVO, token);

    response.setContentType(saerVO.getTargetMimetype());
    response.setHeader("Content-disposition", "attachment; filename=" + saerVO.getFileName());
    response.setIntHeader("x-total-number-of-results", saerVO.getTotalNumberOfRecords());
    response.setHeader("scrollId", srResponse.getScrollId());

    OutputStream output = response.getOutputStream();
    output.write(saerVO.getResult());

    return null;
  }



  //  @ApiImplicitParams({@ApiImplicitParam(name = "searchSource", dataType = "[Ljava.lang.String;", examples = @Example(
  //      value = @ExampleProperty(mediaType = "application/json", value = "{\n\t\"query\" : {\n\t\t\"match_all\": {}\n\t}\n}")))})
  @ApiIgnore
  @RequestMapping(value = "/elasticsearch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<String> searchDetailed(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode searchSource, @RequestParam(name = "scroll", required = false) String scrollTimeValue,
      HttpServletResponse httpResponse)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    return UtilServiceBean.searchDetailed(pis, searchSource, scrollTimeValue, token, httpResponse);
  }

  @RequestMapping(value = "/elasticsearch/scroll", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ApiIgnore
  public ResponseEntity<String> scroll(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode scrollJson, HttpServletResponse httpResponse)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    return UtilServiceBean.scroll(pis, scrollJson, token, httpResponse);
  }

  //  @RequestMapping(value = "/searchAndExport", method = RequestMethod.POST)
  //  public void searchAndExport(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, //
  //      @RequestBody JsonNode searchAndExportQuery, //
  //      HttpServletResponse response) //)
  //      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
  //    try {
  //      SearchAndExportRetrieveRequestVO saerrVO = this.utils.query2SaEVO(searchAndExportQuery);
  //      SearchAndExportResultVO saerVO = this.saes.searchAndExportItems(saerrVO, token);
  //
  //      response.setContentType(saerVO.getTargetMimetype());
  //      response.setHeader("Content-disposition", "attachment; filename=" + saerVO.getFileName());
  //
  //      OutputStream output = response.getOutputStream();
  //      output.write(saerVO.getResult());
  //    } catch (IOException e) {
  //      throw new IngeTechnicalException(e);
  //    }
  //  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<ItemVersionVO> get(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    ItemVersionVO item = null;

    if (token != null && !token.isEmpty()) {
      item = pis.get(itemId, token);
    } else {
      item = pis.get(itemId, null);
    }

    if (item == null) {
      throw new NotFoundException();
    }

    return new ResponseEntity<ItemVersionVO>(item, HttpStatus.OK);
  }


  /**
   * Retrieve a file with a given ID
   * 
   * @param componentId
   * @param response
   */
  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/content", method = RequestMethod.GET)
  public void getComponentContent(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String itemId, @PathVariable String componentId,
      @RequestParam(value = "download", required = false, defaultValue = "false") boolean forceDownload, HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    try {
      FileVOWrapper fileVOWrapper = fileService.readFile(itemId, componentId, token);
      if (fileVOWrapper == null) {
        throw new NotFoundException();
      }
      String contentDispositionType = "inline";
      if (forceDownload) {
        contentDispositionType = "attachment";
      }
      response.setContentType(fileVOWrapper.getFileVO().getMimeType());


      //Add filename and RFC 5987 encoded filename as content disposition headers
      response.setHeader("Content-Disposition", contentDispositionType + "; "
      //Leave only utf-8 encoded filename, as normal filename could lead to encoding problems in Apache
      //+ "filename=\"" + fileVOWrapper.getFileVO().getName() + "\"; "
          + "filename*=UTF-8''"
          + URLEncoder.encode(fileVOWrapper.getFileVO().getName(), StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20"));
      //      OutputStream output = response.getOutputStream();
      //      fileVOWrapper.readFile(output);
      //      output.flush();
      //      output.close();
      try (OutputStream output = response.getOutputStream()) {
        fileVOWrapper.readFile(output);
      }
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
  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/metadata", method = RequestMethod.GET,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public String getTechnicalMetadataByTika(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String itemId, @PathVariable String componentId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    return fileService.getFileMetadata(itemId, componentId, token);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/history", method = RequestMethod.GET)
  public ResponseEntity<List<AuditDbVO>> getVersionHistory(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    List<AuditDbVO> list = null;
    list = pis.getVersionHistory(itemId, token);
    return new ResponseEntity<List<AuditDbVO>>(list, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ItemVersionVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody ItemVersionVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO created = null;
    created = pis.create(item, token);
    return new ResponseEntity<ItemVersionVO>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/release", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> release(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO released = null;
    released = pis.releasePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(released, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/revise", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> revise(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO revised = null;
    revised = pis.revisePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(revised, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/submit", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> submit(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO submitted = null;
    submitted = pis.submitPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(submitted, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/withdraw", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> withdraw(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO withdrawn = null;
    withdrawn = pis.withdrawPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);
    return new ResponseEntity<ItemVersionVO>(withdrawn, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody ItemVersionVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //TODO Write itemId into item
    ItemVersionVO updated = null;
    updated = pis.update(item, token);
    return new ResponseEntity<ItemVersionVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    pis.delete(itemId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
