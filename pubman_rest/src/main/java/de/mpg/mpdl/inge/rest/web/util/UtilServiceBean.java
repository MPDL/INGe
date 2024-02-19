package de.mpg.mpdl.inge.rest.web.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import de.mpg.mpdl.inge.es.dao.impl.ElasticSearchGenericDAOImpl;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.ExportFormatVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportResultVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchAndExportRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveResponseVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.rest.web.controller.ItemRestController;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.GenericService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.SearchAndExportService;
import de.mpg.mpdl.inge.transformation.TransformerFactory;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UtilServiceBean {

  @Autowired
  private PubItemService pis;

  @Autowired
  private SearchAndExportService saes;

  public Date string2Date(String dateString) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);
    Date convertedDate = Date.from(zdt.toInstant());
    return convertedDate;
  }



  /*
  public static SearchSourceBuilder parseJsonToSearchSourceBuilder(String json) throws IOException {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
    try (XContentParser parser =
        XContentFactory.xContent(XContentType.JSON).createParser(new NamedXContentRegistry(searchModule.getNamedXContents()), json)) {
      searchSourceBuilder.parseXContent(parser);
    }
    return searchSourceBuilder;
  
  }
  */



  public static <T> ResponseEntity<String> searchDetailed(GenericService<T, ?> service, JsonNode searchSource, String scrollTimeValue,
      String token, HttpServletResponse httpResp)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    String searchSourceText = MapperFactory.getObjectMapper().writeValueAsString(searchSource);
    long scrollTime = -1;

    if (scrollTimeValue != null) {

      scrollTime = Long.parseLong(scrollTimeValue); //TimeValue.parseTimeValue(scrollTimeValue, "test").millis();
    }

    SearchRequest srequ = SearchRequest.of(sr -> sr.withJson(new StringReader(searchSourceText)));
    //SearchSourceBuilder ssb = UtilServiceBean.parseJsonToSearchSourceBuilder(searchSourceText);
    ResponseBody resp = service.searchDetailed(srequ, scrollTime, token);
    httpResp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
    /*
    XContentBuilder builder = XContentFactory.jsonBuilder();
    resp.toXContent(builder, ToXContent.EMPTY_PARAMS);
    
    */

    return new ResponseEntity<>(ElasticSearchGenericDAOImpl.toJson(resp), HttpStatus.OK);
  }


  public static <T> ResponseEntity<String> scroll(GenericService<T, ?> service, JsonNode scrollJson, String token,
      HttpServletResponse httpResp) throws IngeTechnicalException {
    String scrollTimeValue = scrollJson.get("scroll").asText();
    String scrollId = scrollJson.get("scroll_id").asText();
    //long scrollTime = TimeValue.parseTimeValue(scrollTimeValue, "test").getMillis();
    long scrollTime = Long.parseLong(scrollTimeValue);

    ResponseBody resp = service.scrollOn(scrollId, scrollTime);

    httpResp.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
    //XContentBuilder builder = XContentFactory.jsonBuilder(httpResp.getOutputStream());
    //resp.toXContent(builder, ToXContent.EMPTY_PARAMS);

    return new ResponseEntity<>(ElasticSearchGenericDAOImpl.toJson(resp), HttpStatus.OK);
  }


  public SearchRetrieveRequestVO query2VO(JsonNode query) throws JsonProcessingException, IngeApplicationException {
    //    SearchRetrieveRequestVO request = new SearchRetrieveRequestVO();
    ArrayList<SearchSortCriteria> sortCriterias = new ArrayList<>();
    Query queryBuilder = null;
    int limit = 10;
    int offset = 0;

    JsonNode queryNode = query.get("query");
    if (queryNode != null) {
      ObjectMapper mapper = new ObjectMapper();
      Object queryObject = mapper.treeToValue(queryNode, Object.class);
      String queryString = mapper.writeValueAsString(queryObject);
      queryBuilder = Query.of(q -> q.withJson(new StringReader(queryString)));
    } else {
      throw new IngeApplicationException("The request body doesn't contain a query string.");
    }

    JsonNode sorting = query.get("sort");
    if (sorting != null) {
      if (sorting.isArray()) {
        sorting.forEach(node -> node.fieldNames().forEachRemaining(field -> sortCriterias
            .add(new SearchSortCriteria(field, SortOrder.valueOf(node.get(field).get("order").textValue().toUpperCase())))));
      } else {
        String key = sorting.fieldNames().next();
        String value = sorting.get(key).get("order").textValue().toUpperCase();
        sortCriterias.add(new SearchSortCriteria(key, SortOrder.valueOf(value)));
      }
    }

    if (query.get("size") != null) {
      limit = query.get("size").asInt();
    }

    if (query.get("from") != null) {
      offset = query.get("from").asInt();
    }

    SearchRetrieveRequestVO request =
        new SearchRetrieveRequestVO(queryBuilder, limit, offset, sortCriterias.toArray(new SearchSortCriteria[0]));
    //    request.setQueryBuilder(queryBuilder);
    //    request.setSortKeys(sortCriterias.toArray(new SearchSortCriteria[sortCriterias.size()]));
    //    request.setLimit(limit);
    //    request.setOffset(offset);;

    return request;
  }

  //  public SearchAndExportRetrieveRequestVO query2SaEVO(JsonNode query) throws IngeApplicationException, IngeTechnicalException {
  //    String exportFormat = null;
  //    String outputFormat = null;
  //    String cslConeId = null;
  //    QueryBuilder queryBuilder = null;
  //    ArrayList<SearchSortCriteria> sortCriterias = new ArrayList<>();
  //    int limit = 10;
  //    int offset = 0;
  //
  //    JsonNode queryNode = query.get("query");
  //    if (queryNode != null) {
  //      ObjectMapper mapper = new ObjectMapper();
  //      Object queryObject;
  //      String queryString;
  //      try {
  //        queryObject = mapper.treeToValue(queryNode, Object.class);
  //        queryString = mapper.writeValueAsString(queryObject);
  //      } catch (JsonProcessingException e) {
  //        throw new IngeTechnicalException(e);
  //      }
  //      queryBuilder = QueryBuilders.wrapperQuery(queryString);
  //    } else {
  //      throw new IngeApplicationException("The request body doesn't contain a query string.");
  //    }
  //
  //    JsonNode sorting = query.get("sort");
  //    if (sorting != null) {
  //      if (sorting.isArray()) {
  //        sorting.forEach(node -> {
  //          node.fieldNames().forEachRemaining(field -> {
  //            sortCriterias.add(new SearchSortCriteria(field, SortOrder.valueOf(node.get(field).get("order").textValue().toUpperCase())));
  //          });
  //        });
  //      } else {
  //        String key = sorting.fieldNames().next();
  //        String value = sorting.get(key).get("order").textValue().toUpperCase();
  //        sortCriterias.add(new SearchSortCriteria(key, SortOrder.valueOf(value)));
  //      }
  //    }
  //
  //    if (query.get("size") != null) {
  //      limit = query.get("size").asInt();
  //    }
  //
  //    if (query.get("from") != null) {
  //      offset = query.get("from").asInt();
  //    }
  //
  //    if (query.get("exportFormat") != null) {
  //      exportFormat = query.get("exportFormat").asText();
  //    }
  //
  //    if (query.get("outputFormat") != null) {
  //      outputFormat = query.get("outputFormat").asText();
  //    }
  //
  //    if (query.get("cslConeId") != null) {
  //      cslConeId = query.get("cslConeId").asText();
  //    }
  //
  //    return new SearchAndExportRetrieveRequestVO(exportFormat, outputFormat, cslConeId, queryBuilder, limit, offset,
  //        sortCriterias.toArray(new SearchSortCriteria[sortCriterias.size()]));
  //  }


  public ResponseEntity<SearchRetrieveResponseVO<ItemVersionVO>> searchOrExport(String format, String citation, String cslConeId,
      boolean scroll, SearchRetrieveRequestVO srRequest, HttpServletResponse response, String token)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException, IngeApplicationException, IOException {
    if (scroll) {
      srRequest.setScrollTime(ItemRestController.DEFAULT_SCROLL_TIME);
    }

    if (format == null || format.equals(TransformerFactory.JSON)) {
      SearchRetrieveResponseVO<ItemVersionVO> srResponse = pis.search(srRequest, token);
      HttpHeaders headers = new HttpHeaders();
      headers.add("x-total-number-of-results", "" + srResponse.getNumberOfRecords());
      if (scroll) {
        headers.add("scrollId", srResponse.getScrollId());
      }
      return new ResponseEntity<>(srResponse, headers, HttpStatus.OK);
    }

    ExportFormatVO exportFormat = new ExportFormatVO(format, citation, cslConeId);
    SearchAndExportRetrieveRequestVO saerrVO = new SearchAndExportRetrieveRequestVO(srRequest, exportFormat);
    SearchAndExportResultVO saerVO = this.saes.searchAndExportItems(saerrVO, token);

    response.setContentType(saerVO.getTargetMimetype());
    response.setHeader("Content-disposition", "attachment; filename=" + saerVO.getFileName());
    response.setIntHeader("x-total-number-of-results", saerVO.getTotalNumberOfRecords());
    if (scroll) {
      response.setHeader("scrollId", saerrVO.getSearchRetrieveReponseVO().getScrollId());
    }

    OutputStream output = response.getOutputStream();
    output.write(saerVO.getResult());

    return null;
  }

}

