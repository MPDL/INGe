package de.mpg.mpdl.inge.rest.web.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.OaiFileTools;
import de.mpg.mpdl.inge.util.PropertyReader;
import springfox.documentation.annotations.ApiIgnore;

// TODO: Authorization
@RestController
@RequestMapping("/oai")
@ApiIgnore
public class OaiRestController {

  private static final Logger logger = Logger.getLogger(OaiRestController.class);

  @Autowired
  ElasticSearchClientProvider client;

  @RequestMapping(value = "init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
  public String init(@RequestParam(value = "maxIntervals", required = false) Integer max) throws IngeTechnicalException {

    int count = 0;
    int countSuccess = 0;
    int countFailure = 0;
    int countInterval = 0;
    int upperBorder = 500;
    int readSize = 1000;
    int maxIntervals = max != null ? max : upperBorder; // -> max 500.000 Datensätze

    logger.info("Es werden maximal " + (readSize * upperBorder) + " Datensätze generiert");

    BoolQueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "RELEASED"))
        .must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "RELEASED"));

    SearchResponse scrollResp = this.client.getClient().prepareSearch(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_NAME))
        .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC) //
        .setScroll(new TimeValue(60000)) // 1 Minute for keeping search context alive
        .setQuery(qb) //
        .setSize(readSize) // max of 1000 hits will be returned for each scroll
        .get();
    // Scroll until no hits are returned

    ObjectMapper mapper = MapperFactory.getObjectMapper();

    do {
      for (SearchHit hit : scrollResp.getHits().getHits()) {
        count++;
        ItemVersionVO itemVersionVO = null;
        PubItemVO pubItemVO = null;
        try {
          itemVersionVO = mapper.readValue(hit.getSourceAsString(), ItemVersionVO.class);
          pubItemVO = EntityTransformer.transformToOld(itemVersionVO);
          logger.info(count + ":" + pubItemVO.getVersion().getObjectIdAndVersion());
        } catch (IOException e) {
          logger.error(e);
          logger.error(itemVersionVO);
          throw new IngeTechnicalException(e); // Abbruch. Hier stimmt was grundsätzliches nicht...
        }
        String s;
        try {
          s = XmlTransformingService.transformToItem(pubItemVO);
          OaiFileTools.createFile(new ByteArrayInputStream(s.getBytes()), pubItemVO.getVersion().getObjectId() + ".xml");
          countSuccess++;
        } catch (Exception e) {
          countFailure++;
          logger.error(e); // Kein Abbruch. Dann kann die jeweilige Datei eben nicht generiert
                           // werden...
                           // throw new IngeTechnicalException(e);
        }
      }

      countInterval++;

      scrollResp = this.client.getClient().prepareSearchScroll(scrollResp.getScrollId()) //
          .setScroll(new TimeValue(60000)) //
          .execute() //
          .actionGet();
    } while (scrollResp.getHits().getHits().length != 0 && countInterval < maxIntervals);

    String srResponse = "Done: " + count + " / " + countSuccess + "/" + countFailure + " (Summe / OK / ERROR)";

    return srResponse;
  }

}
