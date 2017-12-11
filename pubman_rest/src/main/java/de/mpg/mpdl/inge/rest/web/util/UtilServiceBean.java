package de.mpg.mpdl.inge.rest.web.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.model.valueobjects.SearchRetrieveRequestVO;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria;
import de.mpg.mpdl.inge.model.valueobjects.SearchSortCriteria.SortOrder;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;

@Service
public class UtilServiceBean {

  private ObjectMapper mapper;

  public Date string2Date(String dateString) {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSxxxx");
    ZonedDateTime zdt = ZonedDateTime.parse(dateString, formatter);
    Date convertedDate = Date.from(zdt.toInstant());
    return convertedDate;
  }

  public SearchRetrieveRequestVO query2VO(JsonNode query) throws JsonProcessingException, IngeApplicationException {

    SearchRetrieveRequestVO request = new SearchRetrieveRequestVO();
    ArrayList<SearchSortCriteria> sortCriterias = new ArrayList<>();
    QueryBuilder queryBuilder = null;;
    int limit = 10, offset = 0;
    mapper = new ObjectMapper();

    JsonNode queryNode = query.get("query");
    if (queryNode != null) {
      Object queryObject = mapper.treeToValue(queryNode, Object.class);
      String queryString = mapper.writeValueAsString(queryObject);
      queryBuilder = QueryBuilders.wrapperQuery(queryString);
    } else {
      throw new IngeApplicationException("The request body doesn't contain a query string.");
    }

    JsonNode sorting = query.get("sort");
    if (sorting != null) {
      if (sorting.isArray()) {
        sorting.forEach(node -> {
          node.fieldNames().forEachRemaining(field -> {
            sortCriterias.add(new SearchSortCriteria(field, SortOrder.valueOf(node.get(field).get("order").textValue().toUpperCase())));
          });
        });
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

    request.setQueryBuilder(queryBuilder);
    request.setSortKeys(sortCriterias.toArray(new SearchSortCriteria[sortCriterias.size()]));
    request.setLimit(limit);
    request.setOffset(offset);;

    return request;

  }
}
