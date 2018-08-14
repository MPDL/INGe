package de.mpg.mpdl.inge.rest.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tika.exception.TikaException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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

import de.mpg.mpdl.inge.model.db.valueobjects.AffiliationDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.AuditDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.db.valueobjects.YearbookDbVO;
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
import de.mpg.mpdl.inge.service.pubman.YearbookService;
import de.mpg.mpdl.inge.service.pubman.impl.FileVOWrapper;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.util.PropertyReader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/yearbooks")
@Api(tags = "Yearbooks")
public class YearbookRestController {

  private static final Logger logger = Logger.getLogger(YearbookRestController.class);

  private final String AUTHZ_HEADER = "Authorization";
  private final String YEARBOOK_ID_PATH = "/{yearbookId}";
  private final String YEARBOOK_ID_VAR = "yearbookId";

  public final static long DEFAULT_SCROLL_TIME = 90000;

  @Autowired
  private PubItemService pis;

  @Autowired
  private UtilServiceBean utils;


  @Autowired
  private SearchAndExportService saes;

  @Autowired
  private YearbookService yearbookService;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<YearbookDbVO>> getAll(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchAllQuery, limit, offset, null);
    SearchRetrieveResponseVO<YearbookDbVO> srResponse = yearbookService.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<YearbookDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "", params = "q", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<YearbookDbVO>> filter(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestParam(value = "q") String query, @RequestParam(value = "limit", required = true, defaultValue = "10") int limit,
      @RequestParam(value = "offset", required = true, defaultValue = "0") int offset)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    QueryBuilder matchQueryParam = QueryBuilders.queryStringQuery(query);
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(matchQueryParam, limit, offset);
    SearchRetrieveResponseVO<YearbookDbVO> srResponse = yearbookService.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<YearbookDbVO>>(srResponse, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<SearchRetrieveResponseVO<YearbookDbVO>> query(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @RequestBody JsonNode query)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    SearchRetrieveRequestVO srRequest = utils.query2VO(query);
    SearchRetrieveResponseVO<YearbookDbVO> srResponse = yearbookService.search(srRequest, token);
    return new ResponseEntity<SearchRetrieveResponseVO<YearbookDbVO>>(srResponse, HttpStatus.OK);
  }


  @RequestMapping(value = YEARBOOK_ID_PATH, method = RequestMethod.GET)
  public ResponseEntity<YearbookDbVO> get(@RequestHeader(value = AUTHZ_HEADER, required = false) String token,
      @PathVariable(value = YEARBOOK_ID_VAR) String yearbookId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, NotFoundException {
    YearbookDbVO yearbook = null;
    if (token != null && !token.isEmpty()) {
      yearbook = yearbookService.get(yearbookId, token);
    } else {
      yearbook = yearbookService.get(yearbookId, token);
    }
    if (yearbook == null) {
      throw new NotFoundException();
    }
    return new ResponseEntity<YearbookDbVO>(yearbook, HttpStatus.OK);
  }


  @RequestMapping(value = YEARBOOK_ID_PATH + "/items", method = RequestMethod.GET)
  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> getItems(
      @RequestHeader(value = AUTHZ_HEADER, required = false) String token, @PathVariable(value = YEARBOOK_ID_VAR) String yearbookId,
      @RequestParam(value = "format", required = false,
          defaultValue = "json") @ApiParam(allowableValues = ItemRestController.EXPORT_FORMAT_ALLOWABLE_VALUES) String format, //
      @RequestParam(value = "citation", required = false,
          defaultValue = "APA") @ApiParam(allowableValues = ItemRestController.EXPORT_CITATION_ALLOWABLE_VALUES) String citation, //
      @RequestParam(value = "cslConeId", required = false) String cslConeId, //
      HttpServletResponse response) throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException, NotFoundException, IOException {
    YearbookDbVO yearbook = yearbookService.get(yearbookId, token);
    if (yearbook == null) {
      throw new NotFoundException();
    }

    QueryBuilder qb = QueryBuilders.termsQuery(PubItemServiceDbImpl.INDEX_VERSION_OBJECT_ID, yearbook.getItemIds());
    SearchRetrieveRequestVO srRequest = new SearchRetrieveRequestVO(qb, null);
    srRequest.setLimit(10000);

    return utils.searchOrExport(format, citation, cslConeId, false, srRequest, response, null);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<YearbookDbVO> create(@RequestHeader(value = AUTHZ_HEADER) String token, @RequestBody YearbookDbVO yearbook)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    YearbookDbVO created = null;
    created = yearbookService.create(yearbook, token);
    return new ResponseEntity<YearbookDbVO>(created, HttpStatus.CREATED);
  }


  @RequestMapping(value = YEARBOOK_ID_PATH, method = RequestMethod.PUT)
  public ResponseEntity<YearbookDbVO> update(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = YEARBOOK_ID_VAR) String yearbookId, @RequestBody YearbookDbVO yearbook)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    YearbookDbVO updated = null;
    updated = yearbookService.update(yearbook, token);
    return new ResponseEntity<YearbookDbVO>(updated, HttpStatus.OK);
  }

  @RequestMapping(value = YEARBOOK_ID_PATH, method = RequestMethod.DELETE)
  public ResponseEntity<?> delete(@RequestHeader(value = AUTHZ_HEADER) String token,
      @PathVariable(value = YEARBOOK_ID_VAR) String yearbookId)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException {
    yearbookService.delete(yearbookId, token);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
