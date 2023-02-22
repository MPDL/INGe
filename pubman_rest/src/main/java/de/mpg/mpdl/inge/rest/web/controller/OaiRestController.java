package de.mpg.mpdl.inge.rest.web.controller;

import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.ResponseBody;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.dao.PubItemDaoEs;
import de.mpg.mpdl.inge.model.db.valueobjects.ItemVersionVO;
import de.mpg.mpdl.inge.model.util.EntityTransformer;
import de.mpg.mpdl.inge.model.util.MapperFactory;
import de.mpg.mpdl.inge.model.valueobjects.publication.PubItemVO;
import de.mpg.mpdl.inge.model.xmltransforming.XmlTransformingService;
import de.mpg.mpdl.inge.service.pubman.PubItemService;
import de.mpg.mpdl.inge.service.pubman.impl.PubItemServiceDbImpl;
import de.mpg.mpdl.inge.service.util.OaiFileTools;
import de.mpg.mpdl.inge.service.util.SearchUtils;
import io.swagger.v3.oas.annotations.Hidden;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.util.List;

// TODO: Authorization
@RestController
@RequestMapping("/oai")
@Hidden
public class OaiRestController {

  private static final Logger logger = Logger.getLogger(OaiRestController.class);

  @Autowired
  ElasticSearchClientProvider client;

  @Autowired
  PubItemDaoEs pubItemDao;

  @Autowired
  PubItemService pubItemService;

  @RequestMapping(value = "init", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
  public String init(@RequestParam(value = "maxIntervals", required = false) Integer max) throws Exception {

    int count = 0;
    int countSuccess = 0;
    int countFailure = 0;
    int countInterval = 0;
    int upperBorder = 500;
    int readSize = 1000;
    int maxIntervals = max != null ? max : upperBorder; // -> max 500.000 Datensätze

    logger.info("Es werden maximal " + (readSize * upperBorder) + " Datensätze generiert");

    Query q = BoolQuery.of(b -> b.must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_PUBLIC_STATE).value("RELEASED"))._toQuery())
        .must(TermQuery.of(t -> t.field(PubItemServiceDbImpl.INDEX_VERSION_STATE).value("RELEASED"))._toQuery()))._toQuery();

    //BoolQueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_PUBLIC_STATE, "RELEASED"))
    //   .must(QueryBuilders.termQuery(PubItemServiceDbImpl.INDEX_VERSION_STATE, "RELEASED"));
    SearchRequest srr = SearchRequest.of(sr -> sr.size(readSize).query(q).scroll(Time.of(t -> t.time("60000ms"))));

    ResponseBody scrollResp = pubItemService.searchDetailed(srr, 60000, null);

    /*
    SearchResponse scrollResp = this.client.getClient().prepareSearch(PropertyReader.getProperty(PropertyReader.INGE_INDEX_ITEM_NAME))
        .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC) //
        .setScroll(new TimeValue(60000)) // 1 Minute for keeping search context alive
        .setQuery(qb) //
        .setSize(readSize) // max of 1000 hits will be returned for each scroll
        .get();
        */
    // Scroll until no hits are returned

    ObjectMapper mapper = MapperFactory.getObjectMapper();

    List<ItemVersionVO> results = SearchUtils.getRecordListFromElasticSearchResponse(scrollResp, ItemVersionVO.class);

    do {
      for (ItemVersionVO itemVersionVO : results) {

        count++;
        /*
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
        */
        String s;
        try {
          PubItemVO pubItemVO = EntityTransformer.transformToOld(itemVersionVO);
          s = XmlTransformingService.transformToItem(new PubItemVO(pubItemVO));
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

      scrollResp = pubItemService.scrollOn(scrollResp.scrollId(), 60000);
      results = SearchUtils.getRecordListFromElasticSearchResponse(scrollResp, ItemVersionVO.class);
      /*
      scrollResp = this.client.getClient().prepareSearchScroll(scrollResp.getScrollId()) //
          .setScroll(new TimeValue(60000)) //
          .execute() //
          .actionGet();
          */
    } while (results.size() != 0 && countInterval < maxIntervals);

    if (scrollResp != null) {
      pubItemDao.clearScroll(scrollResp.scrollId());
    }

    String srResponse = "Done: " + count + " / " + countSuccess + "/" + countFailure + " (Summe / OK / ERROR)";

    return srResponse;
  }

}
