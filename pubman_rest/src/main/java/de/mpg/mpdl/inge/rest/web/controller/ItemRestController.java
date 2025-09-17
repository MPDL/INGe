package de.mpg.mpdl.inge.rest.web.controller;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.TaskParamVO;
import de.mpg.mpdl.inge.rest.web.exceptions.NotFoundException;
import de.mpg.mpdl.inge.rest.web.spring.AuthCookieToHeaderFilter;
import de.mpg.mpdl.inge.rest.web.util.MultipartFileSender;
import de.mpg.mpdl.inge.rest.web.util.UtilServiceBean;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.FileServiceExternal;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.service.pubman.impl.FileVOWrapper;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.service.util.ThumbnailCreationService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
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

@RestController
@RequestMapping("/items")
@Tag(name = "Items / Publications")
public class ItemRestController {

  public static final long DEFAULT_SCROLL_TIME = 90000;
  private static final Logger logger = LogManager.getLogger(ItemRestController.class);
  private static final String ITEM_ID_PATH = "/{itemId}";
  private static final String ITEM_ID_VAR = "itemId";

  @Autowired
  private PubItemService pubItemService;

  @Autowired
  private UtilServiceBean utilServiceBean;

  @Autowired
  private FileServiceExternal fileService;

  @Autowired
  private SearchAndExportService saes;

  private ObjectMapper objectMapper = new ObjectMapper();

  @RequestMapping(value = ITEM_ID_PATH + "/rollbackToVersion", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> rollbackToVersion( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws IngeTechnicalException, AuthenticationException, IngeApplicationException, AuthorizationException {

    ItemVersionVO itemVersionVO = this.pubItemService.rollbackToVersion(itemId, token);

    return new ResponseEntity<>(itemVersionVO, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/addNewDoi", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> addNewDoi( //
      @RequestHeader(AuthCookieToHeaderFilter.AUTHZ_HEADER) String token, //
      @PathVariable(value = ITEM_ID_VAR) String itemId)
      throws IngeTechnicalException, AuthenticationException, IngeApplicationException, AuthorizationException {

    ItemVersionVO itemVersionVO = this.pubItemService.addNewDoi(itemId, token);

    return new ResponseEntity<>(itemVersionVO, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/authorization", method = RequestMethod.GET)
  public ResponseEntity<JsonNode> authInfo(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId,
      @RequestParam(value = "afterSave", defaultValue = "false", required = false) boolean afterSave)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    Map<AuthorizationService.AccessType, Boolean> map = this.pubItemService.getAuthorizationInfo(itemId, token, afterSave);
    if (map == null)
      throw new NotFoundException();

    ObjectNode returnNode = objectMapper.createObjectNode();
    returnNode.set("actions", objectMapper.valueToTree(map));

    return new ResponseEntity<>(returnNode, HttpStatus.OK);
  }

  @RequestMapping(value = "/authorization", method = RequestMethod.GET)
  public ResponseEntity<JsonNode> authInfoForCreation(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestParam(value = "contextId", required = true) String contextId,
      @RequestParam(value = "afterSave", defaultValue = "false", required = false) boolean afterSave)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    Map<AuthorizationService.AccessType, Boolean> map = this.pubItemService.getAuthorizationInfoForCreation(contextId, token, afterSave);
    if (map == null)
      throw new NotFoundException();

    ObjectNode returnNode = objectMapper.createObjectNode();
    returnNode.set("actions", objectMapper.valueToTree(map));

    return new ResponseEntity<>(returnNode, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<ItemVersionVO> create(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @RequestBody ItemVersionVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    ItemVersionVO created = null;
    created = this.pubItemService.create(item, token);

    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    this.pubItemService.delete(itemId, token);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/export", method = RequestMethod.GET)
  public ResponseEntity export(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId,
      @RequestParam(value = "format", required = false,
          defaultValue = "json") @Parameter(schema = @Schema(allowableValues = {TransformerFactory.JSON,
              TransformerFactory.ESCIDOC_ITEMLIST_XML, TransformerFactory.BIBTEX, TransformerFactory.ENDNOTE, TransformerFactory.MARC_XML,
              TransformerFactory.PDF, TransformerFactory.DOCX, TransformerFactory.HTML_PLAIN, TransformerFactory.HTML_LINKED,
              TransformerFactory.JSON_CITATION, TransformerFactory.ESCIDOC_SNIPPET})) String format, //
      @RequestParam(value = "citation", required = false,
          defaultValue = "APA") @Parameter(schema = @Schema(allowableValues = {"APA, APA(CJK), AJP, JUS, CSL"})) String citation, //
      @RequestParam(value = "cslConeId", required = false) String cslConeId, HttpServletResponse response) throws AuthenticationException,
      AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException, IOException {

    List<ItemVersionVO> itemList = new ArrayList<>();
    itemList.add(getItem(itemId, token));

    return this.utilServiceBean.searchOrExport(format, citation, cslConeId, itemList, response, token);
  }

  @Hidden
  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> filter(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, @RequestParam(value = "q") String query,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    //QueryBuilder matchQueryParam = QueryBuilders.queryStringQuery(query);
    Query matchQueryParam = QueryStringQuery.of(q -> q.query(query))._toQuery();
    //QueryBuilder matchQueryParam = QueryBuilders.boolQuery().filter(QueryBuilders.termQuery(query.split(":")[0], query.split(":")[1]));
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset, sorting);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = this.pubItemService.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
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

    return new ResponseEntity<>(getItem(itemId, token), HttpStatus.OK);
  }

  @Hidden
  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> getAll(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "size", defaultValue = "10") int limit, @RequestParam(value = "from", defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
    //QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchSortCriteria sorting =
        new SearchSortCriteria(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_SORT), SearchSortCriteria.SortOrder.ASC);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, sorting);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse = this.pubItemService.search(srRequest, token);

    return new ResponseEntity<>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/authorization", method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JsonNode> getAuthInfoForFile(
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @PathVariable(value = "componentId") String componentId)
      throws IngeTechnicalException, IngeApplicationException, NotFoundException {

    JsonNode node = this.pubItemService.getAuthorizationInfoForFile(itemId, componentId, token);

    if (node == null)
      throw new NotFoundException();

    return new ResponseEntity<>(node, HttpStatus.OK);
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
      @RequestParam(value = "download", required = false, defaultValue = "false") boolean forceDownload, HttpServletResponse response,
      HttpServletRequest request)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    try {
      FileVOWrapper fileVOWrapper = this.fileService.readFile(itemId, componentId, token);
      if (null == fileVOWrapper) {
        throw new NotFoundException();
      }
      String contentDispositionType = "inline";
      if (forceDownload) {
        contentDispositionType = "attachment";
      }

      MultipartFileSender.fromFileVOWrapper(fileVOWrapper).with(contentDispositionType).with(request).with(response).serveResource();


      //response.setContentType(fileVOWrapper.getFileVO().getMimeType());


      /*
      //Add filename and RFC 5987 encoded filename as content disposition headers
      response.setHeader("Content-Disposition", contentDispositionType + "; "
      //Leave only utf-8 encoded filename, as normal filename could lead to encoding problems in Apache
      //+ "filename=\"" + fileVOWrapper.getFileVO().getName() + "\"; "
          + "filename*=UTF-8''" + URLEncoder.encode(fileVOWrapper.getFileVO().getName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
      //      OutputStream output = response.getOutputStream();
      //      fileVOWrapper.readFile(output);
      //      output.flush();
      //      output.close();
      try (OutputStream output = response.getOutputStream()) {
        fileVOWrapper.readFile(output);
      }
      
       */
    } catch (IOException e) {
      logger.error("could not read file [" + componentId + "]");
      throw new IngeTechnicalException("Error while opening input stream", e);
    }
  }


  @RequestMapping(path = ITEM_ID_PATH + "/component/{componentId}/thumbnail", method = RequestMethod.GET)
  public void getComponentThumbnail(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @PathVariable String itemId, @PathVariable String componentId,
      @RequestParam(value = "download", required = false, defaultValue = "false") boolean forceDownload, HttpServletResponse response,
      HttpServletRequest request)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {

    try {
      FileVOWrapper fileVOWrapper = this.fileService.readFile(itemId, componentId, token);
      if (null == fileVOWrapper) {
        throw new NotFoundException();
      }

      if (fileVOWrapper.getThumbnailFileId() != null) {
        String contentDispositionType = "inline";
        String fileName = ThumbnailCreationService.createThumbnailFileIdentifier(fileVOWrapper.getFileVO().getObjectId());
        response.setHeader("Content-Disposition",
            "inline;filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));
        response.setHeader("Content-Type", "image/jpeg");
        try (OutputStream output = response.getOutputStream()) {
          fileVOWrapper.readThumbnail(output);
        }
      } else {
        throw new NotFoundException();
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

    return this.fileService.getFileMetadata(itemId, componentId, token);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/history", method = RequestMethod.GET)
  public ResponseEntity<List<AuditDbVO>> getVersionHistory(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId) {

    List<AuditDbVO> list = null;
    list = this.pubItemService.getVersionHistory(itemId, token);

    return new ResponseEntity<>(list, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/release", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> release(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    ItemVersionVO released = null;
    released = this.pubItemService.releasePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);

    return new ResponseEntity<>(released, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/revise", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> revise(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    ItemVersionVO revised = null;
    revised = this.pubItemService.revisePubItem(itemId, params.getLastModificationDate(), params.getComment(), token);

    return new ResponseEntity<>(revised, HttpStatus.OK);
  }

  @RequestMapping(value = "/elasticsearch/scroll", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @Hidden
  public ResponseEntity<String> scroll(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode scrollJson, HttpServletResponse httpResponse) throws IngeTechnicalException {

    return UtilServiceBean.scroll(this.pubItemService, scrollJson, token, httpResponse);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> search( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, //
      @RequestParam(value = "format", required = false,
          defaultValue = "json") @Parameter(schema = @Schema(allowableValues = {TransformerFactory.JSON,
              TransformerFactory.ESCIDOC_ITEMLIST_XML, TransformerFactory.BIBTEX, TransformerFactory.ENDNOTE, TransformerFactory.MARC_XML,
              TransformerFactory.PDF, TransformerFactory.DOCX, TransformerFactory.HTML_PLAIN, TransformerFactory.HTML_LINKED,
              TransformerFactory.JSON_CITATION, TransformerFactory.ESCIDOC_SNIPPET})) String format, //
      @RequestParam(value = "citation", required = false,
          defaultValue = "APA") @Parameter(schema = @Schema(allowableValues = {"APA, APA(CJK), AJP, JUS, CSL"})) String citation, //
      @RequestParam(value = "cslConeId", required = false) String cslConeId, //
      @RequestParam(value = "scroll", required = false) boolean scroll, //
      @RequestBody JsonNode query, //
      HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    SearchRetrieveRequestVO srRequest = this.utilServiceBean.query2VO(query);

    return this.utilServiceBean.searchOrExport(format, citation, cslConeId, scroll, srRequest, response, token);
  }

  //  @ApiImplicitParams({@ApiImplicitParam(name = "searchSource", dataType = "[Ljava.lang.String;", examples = @Example(
  //      value = @ExampleProperty(mediaType = "application/json", value = "{\n\t\"query\" : {\n\t\t\"match_all\": {}\n\t}\n}")))})
  @Hidden
  @RequestMapping(value = "/elasticsearch", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<String> searchDetailed(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode searchSource, @RequestParam(name = "scroll", required = false) String scrollTimeValue,
      HttpServletResponse httpResponse)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    return UtilServiceBean.searchDetailed(this.pubItemService, searchSource, scrollTimeValue, token, httpResponse);
  }

  @RequestMapping(value = "/search/scroll", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> searchScroll( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, //
      @RequestParam(value = "format", required = false) String format, //
      @RequestParam(value = "citation", required = false) String citation, //
      @RequestParam(value = "cslConeId", required = false) String cslConeId, //
      @RequestParam(value = "scrollId") String scrollId, //
      HttpServletResponse response) throws IngeTechnicalException, IOException {

    ResponseBody searchResp = this.pubItemService.scrollOn(scrollId, DEFAULT_SCROLL_TIME);
    SearchRetrieveResponseVO<ItemVersionVO> srResponse =
        SearchUtils.getSearchRetrieveResponseFromElasticSearchResponse(searchResp, ItemVersionVO.class);

    if (null == format || format.equals(TransformerFactory.JSON)) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-total-number-of-results", "" + srResponse.getNumberOfRecords());
      headers.add("scrollId", srResponse.getScrollId());
      return new ResponseEntity<>(srResponse, headers, HttpStatus.OK);
    }

    ExportFormatVO exportFormat = new ExportFormatVO(format, citation, cslConeId);
    //SearchAndExportRetrieveRequestVO saerrVO = new SearchAndExportRetrieveRequestVO(srResponse, exportFormat);
    SearchAndExportResultVO saerVO =
        this.saes.exportItems(exportFormat, SearchUtils.getRecordListFromSearchRetrieveResponse(srResponse, ItemVersionVO.class), token);

    response.setContentType(saerVO.getTargetMimetype());
    response.setHeader("Content-disposition", "attachment; filename=" + saerVO.getFileName());
    response.setIntHeader("x-total-number-of-results", srResponse.getNumberOfRecords());
    response.setHeader("scrollId", srResponse.getScrollId());

    OutputStream output = response.getOutputStream();
    output.write(saerVO.getResult());

    return null;
  }

  @RequestMapping(value = ITEM_ID_PATH + "/submit", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> submit(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    ItemVersionVO submitted = null;
    submitted = this.pubItemService.submitPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);

    return new ResponseEntity<>(submitted, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> update(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody ItemVersionVO item)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    //TODO Write itemId into item
    ItemVersionVO updated = null;
    updated = this.pubItemService.update(item, token);

    return new ResponseEntity<>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = ITEM_ID_PATH + "/withdraw", method = RequestMethod.PUT)
  public ResponseEntity<ItemVersionVO> withdraw(@RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER) String token,
      @PathVariable(value = ITEM_ID_VAR) String itemId, @RequestBody TaskParamVO params)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {

    ItemVersionVO withdrawn = null;
    withdrawn = this.pubItemService.withdrawPubItem(itemId, params.getLastModificationDate(), params.getComment(), token);

    return new ResponseEntity<>(withdrawn, HttpStatus.OK);
  }

  private ItemVersionVO getItem(String itemId, String token)
      throws AuthenticationException, AuthorizationException, IngeApplicationException, IngeTechnicalException, NotFoundException {

    ItemVersionVO item = null;

    if (null != token && !token.isEmpty()) {
      item = this.pubItemService.get(itemId, token);
    } else {
      item = this.pubItemService.get(itemId, null);
    }

    if (null == item) {
      throw new NotFoundException();
    }

    return item;
  }

  @RequestMapping(value = "/jusreport", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> search( //
      @RequestHeader(value = AuthCookieToHeaderFilter.AUTHZ_HEADER, required = false) String token, //
      @RequestParam(value = "format", required = false, defaultValue = TransformerFactory.JUS_HTML_XML) @Parameter(
          schema = @Schema(allowableValues = {TransformerFactory.JUS_HTML_XML, TransformerFactory.JUS_INDESIGN_XML})) String format, //
      @RequestParam(value = "orgId", required = true) String orgId, @RequestParam(value = "year", required = true) String year, //
      HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    ExportFormatVO exportFormat = new ExportFormatVO(format);
    SearchAndExportResultVO saerVO = this.saes.exportJusReport(exportFormat, orgId, year, token);
    this.utilServiceBean.setResponseEntityHeader(exportFormat, false, saerVO, response);
    return null;
  }
}
