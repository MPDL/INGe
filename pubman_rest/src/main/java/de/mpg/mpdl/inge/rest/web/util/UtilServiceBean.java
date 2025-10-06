package de.mpg.mpdl.inge.rest.web.util;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.AccountUserDbVO;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.rest.web.controller.ItemRestController;
import de.mpg.mpdl.inge.service.aa.AuthorizationService;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.GenericService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.service.pubman.impl.SearchAndExportServiceImpl;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import de.mpg.mpdl.inge.transformation.results.TransformerStreamResult;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UtilServiceBean {

  @Autowired
  private AuthorizationService authorizationService;

  @Autowired
  private PubItemService pis;

  @Autowired
  private SearchAndExportService saes;

  public static <T> ResponseEntity<String> searchDetailed(GenericService<T, ?> service, JsonNode searchSource, String scrollTimeValue,
      String token, HttpServletResponse httpResp)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    String searchSourceText = MapperFactory.getObjectMapper().writeValueAsString(searchSource);
    long scrollTime = -1;

    if (null != scrollTimeValue) {
      scrollTime = Long.parseLong(scrollTimeValue);
    }

    SearchRequest srequ = SearchRequest.of(sr -> sr.withJson(new StringReader(searchSourceText)));
    ResponseBody resp = service.searchDetailed(srequ, scrollTime, token);
    httpResp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

    return new ResponseEntity<>(ElasticSearchGenericDAOImpl.toJson(resp), HttpStatus.OK);
  }

  public static <T> ResponseEntity<String> scroll(GenericService<T, ?> service, JsonNode scrollJson, String token,
      HttpServletResponse httpResp) throws IngeTechnicalException {
    String scrollTimeValue = scrollJson.get("scroll").asText();
    String scrollId = scrollJson.get("scroll_id").asText();
    long scrollTime = Long.parseLong(scrollTimeValue);

    ResponseBody resp = service.scrollOn(scrollId, scrollTime);

    httpResp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

    return new ResponseEntity<>(ElasticSearchGenericDAOImpl.toJson(resp), HttpStatus.OK);
  }

  public SearchRetrieveRequestVO query2VO(JsonNode query) throws JsonProcessingException, IngeApplicationException {
    ArrayList<SearchSortCriteria> sortCriterias = new ArrayList<>();
    Query queryBuilder = null;
    int limit = 10;
    int offset = 0;

    JsonNode queryNode = query.get("query");
    if (null != queryNode) {
      ObjectMapper mapper = new ObjectMapper();
      Object queryObject = mapper.treeToValue(queryNode, Object.class);
      String queryString = mapper.writeValueAsString(queryObject);
      queryBuilder = Query.of(q -> q.withJson(new StringReader(queryString)));
    } else {
      throw new IngeApplicationException("The request body doesn't contain a query string.");
    }

    JsonNode sorting = query.get("sort");
    if (null != sorting) {
      if (sorting.isArray()) {
        sorting.forEach(node -> node.fieldNames().forEachRemaining(field -> sortCriterias.add(
            new SearchSortCriteria(field, SearchSortCriteria.SortOrder.valueOf(node.get(field).get("order").textValue().toUpperCase())))));
      } else {
        String key = sorting.fieldNames().next();
        String value = sorting.get(key).get("order").textValue().toUpperCase();
        sortCriterias.add(new SearchSortCriteria(key, SearchSortCriteria.SortOrder.valueOf(value)));
      }
    }

    if (null != query.get("size")) {
      limit = query.get("size").asInt();
    }

    if (null != query.get("from")) {
      offset = query.get("from").asInt();
    }

    SearchRetrieveRequestVO request =
        new SearchRetrieveRequestVO(queryBuilder, limit, offset, sortCriterias.toArray(new SearchSortCriteria[0]));

    return request;
  }

  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> searchOrExport(String format, String citation, String cslConeId,
      List<ItemVersionVO> itemList, HttpServletResponse response, String token)
      throws IngeTechnicalException, IOException, AuthenticationException, AuthorizationException, IngeApplicationException {
    ExportFormatVO exportFormat = new ExportFormatVO(format, citation, cslConeId);
    SearchAndExportResultVO saerVO = this.saes.exportItemsWrapped(exportFormat, itemList, token);
    setResponseEntityHeader(exportFormat, false, saerVO, response);

    return null;
  }

  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> searchOrExport(String format, String citation, String cslConeId,
      boolean scroll, SearchRetrieveRequestVO srRequest, HttpServletResponse response, String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    if (scroll) {
      srRequest.setScrollTime(ItemRestController.DEFAULT_SCROLL_TIME);
    }

    if (null == format || format.equals(TransformerFactory.JSON)) {
      SearchRetrieveResponseVO<ItemVersionVO> srResponse = this.pis.search(srRequest, token);
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-total-number-of-results", "" + srResponse.getNumberOfRecords());
      if (scroll) {
        headers.add("scrollId", srResponse.getScrollId());
      }
      return new ResponseEntity<>(srResponse, headers, HttpStatus.OK);

    }
    ExportFormatVO exportFormat = new ExportFormatVO(format, citation, cslConeId);
    SearchAndExportRetrieveRequestVO saerrVO = new SearchAndExportRetrieveRequestVO(srRequest, exportFormat);
    SearchAndExportResultVO saerVO = this.saes.searchAndExportItemsWrapped(saerrVO, token);
    setResponseEntityHeader(exportFormat, scroll, saerVO, response);

    return null;
  }



  public Date string2Date(String dateString) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);
    Date convertedDate = Date.from(zdt.toInstant());

    return convertedDate;
  }

  public void setResponseEntityHeader(ExportFormatVO exportFormat, boolean scroll, SearchAndExportResultVO saerVO,
      HttpServletResponse response)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {

    response.setContentType(saerVO.getTargetMimetype());
    response.setHeader("Content-disposition", "inline; filename=" + saerVO.getFileName());
    response.setIntHeader("x-total-number-of-results", saerVO.getTotalNumberOfRecords());
    if (scroll) {
      response.setHeader("scrollId", saerVO.getSearchRetrieveResponseVO().getScrollId());
    }
    try {
      ((SearchAndExportServiceImpl.ExtendedSearchAndExportResultVO) saerVO).getTransformerWrapper()
          .executeTransformation(new TransformerStreamResult(response.getOutputStream()));
    } catch (Exception e) {
      try {
        //Reset response to enable correct error messages in JSON format via PubmanRestExceptionHandler
        response.reset();
      } catch (Exception e1) {

      }
      throw e;
    }
  }

  public AccountUserDbVO checkUser(String token) throws AuthenticationException, IngeApplicationException {
    AccountUserDbVO accountUserDbVO = this.authorizationService.getUserAccountFromToken(token);

    return accountUserDbVO;
  }

  public static void checkData(Object obj) throws IngeApplicationException {
    if (obj == null) {
      throw new IngeApplicationException("The data must not be empty");
    }
    if (obj instanceof String) {
      String str = (String) obj;
      if (str.trim().isEmpty()) {
        throw new IngeApplicationException("The data must not be empty");
      }
    }
  }
}

