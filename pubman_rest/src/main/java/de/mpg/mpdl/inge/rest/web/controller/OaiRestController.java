package de.mpg.mpdl.inge.rest.web.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.model.xmltransforming.exceptions.TechnicalException;
import de.mpg.mpdl.inge.service.exceptions.AuthenticationException;
import de.mpg.mpdl.inge.service.exceptions.AuthorizationException;
import de.mpg.mpdl.inge.service.exceptions.IngeApplicationException;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.OaiFileTools;
import de.mpg.mpdl.inge.util.PropertyReader;

@RestController
@RequestMapping("/oai")
public class OaiRestController {

  private static Logger logger = Logger.getLogger(OaiRestController.class);

  @Autowired
  ElasticSearchClientProvider client;

  @RequestMapping(value = "init", method = RequestMethod.POST)
  public ResponseEntity<String> init(
      @RequestParam(value = "maxIntervals", required = false) Integer max)
      throws AuthenticationException, AuthorizationException, IngeTechnicalException,
      IngeApplicationException {

    int count = 0;
    int countInterval = 0;
    int maxIntervals = max != null ? max : 500; // TODO: maxIntervals wieder löschen -> max 500.000
                                                // Datensätze

    QueryBuilder qb = QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "RELEASED");

    SearchResponse scrollResp =
        this.client.getClient().prepareSearch(PropertyReader.getProperty("item_index_name"))
            .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC) //
            .setScroll(new TimeValue(60000)) // 1 Minute for keeping search context alive
            .setQuery(qb) //
            .setSize(1000) // max of 1000 hits will be returned for each scroll
            .get();
    // Scroll until no hits are returned

    ObjectMapper mapper = JsonObjectMapperFactory.getObjectMapper();

    do {
      for (SearchHit hit : scrollResp.getHits().getHits()) {
        count++;
        PubItemVO pubItemVO = null;
        try {
          pubItemVO = mapper.readValue(hit.getSourceAsString(), PubItemVO.class);
          logger.info(count + ":" + pubItemVO.getVersion().getObjectIdAndVersion());
        } catch (IOException e) {
          logger.error(e);
          logger.error(pubItemVO);
          throw new IngeTechnicalException(e);
        }
        String s;
        try {
          s = XmlTransformingService.transformToItem(pubItemVO);
        } catch (TechnicalException e) {
          logger.error(e);
          throw new IngeTechnicalException(e);
        }
        OaiFileTools.createFile(new ByteArrayInputStream(s.getBytes()), pubItemVO.getVersion()
            .getObjectIdAndVersion() + ".xml");
      }

      countInterval++;

      scrollResp = this.client.getClient().prepareSearchScroll(scrollResp.getScrollId()) //
          .setScroll(new TimeValue(60000)) //
          .execute() //
          .actionGet();
    } while (scrollResp.getHits().getHits().length != 0 && countInterval < maxIntervals);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.TEXT_PLAIN);
    String srResponse = "Everything is fine: " + count + " files generated";

    return new ResponseEntity<String>(srResponse, headers, HttpStatus.OK);
  }

}
