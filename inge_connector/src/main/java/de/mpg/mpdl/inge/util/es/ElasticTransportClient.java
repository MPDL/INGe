package de.mpg.mpdl.inge.util.es;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * ElasticTransportClient enables a elasticsearch accessibility
 * 
 * @author wfrank (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class ElasticTransportClient {

  private final static String INDEX_NAME = "pure";
  private final static String ITEM_TYPE = "pubitem";

  private final static String MAPPING_JSON_PATH =
      "/home/frank/data/git/pandora_core/src/main/resources/json/";

  public static void main(String[] args) {

    // ElasticClusterInfoService.listIndices();
    /*
     * createIndex("publications_20151209"); //addAlias("publications_20151208", "pure");
     * addMapping("publications_20151209", "component", "component_mapping.json");
     * addMapping("publications_20151209", "publication", "publication_mapping.json");
     */

  }

  /**
   * Create a new index
   * 
   * @param indexName
   */
  public static void createIndex(String indexName) {
    Client c = start();

    IndicesExistsResponse res = c.admin().indices().prepareExists(indexName).execute().actionGet();
    if (res.isExists()) {
      DeleteIndexRequestBuilder delIdx = c.admin().indices().prepareDelete(indexName);
      delIdx.execute().actionGet();
    }

    CreateIndexRequestBuilder indexReq = c.admin().indices().prepareCreate(indexName);
    CreateIndexResponse indexResp = indexReq.execute().actionGet();

    System.out.println("created index " + indexName + ": " + indexResp.isAcknowledged());
    c.close();

  }

  /**
   * delete an existing index
   * 
   * @param index
   */
  public static void deleteIndex(String index) {
    Client c = start();
    DeleteIndexResponse delResponse =
        c.admin().indices().prepareDelete(index).execute().actionGet();
    if (delResponse.isAcknowledged()) {
      System.out.println("Deleted index: " + index);
    }
    c.close();
  }

  /**
   * Add a json mapping to an existing index
   * 
   * @param index
   * @param type
   * @param jsonFile
   */
  public static void addMapping(String index, String type, String jsonFile) {
    java.nio.file.Path path = Paths.get(MAPPING_JSON_PATH + jsonFile);
    byte[] mapping = null;
    try {
      mapping = Files.readAllBytes(path);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Client c = start();
    PutMappingRequestBuilder mappingReq = c.admin().indices().preparePutMapping(index);
    mappingReq.setType(type);
    mappingReq.setSource(new String(mapping, StandardCharsets.UTF_8));

    PutMappingResponse mappingResp = mappingReq.execute().actionGet();
    System.out.println("added mapping to index" + index + ": " + mappingResp.isAcknowledged());
    c.close();

  }

  /**
   * create an alias for an existing index
   * 
   * @param index
   * @param alias
   */
  public static void addAlias(String index, String alias) {
    Client c = start();
    IndicesAliasesResponse resp =
        c.admin().indices().prepareAliases().addAlias(index, alias).execute().actionGet();
    if (resp.isAcknowledged()) {
      for (String head : resp.getHeaders()) {
        System.out.println(head);
      }
    }
    c.close();
  }

  public static TransportClient start() {
    Settings settings =
        Settings.settingsBuilder().put("cluster.name", "elastic_inge")
            .put("client.transport.sniff", true).build();
    TransportClient client = new TransportClient.Builder().settings(settings).build();

    try {
      client.addTransportAddress(new InetSocketTransportAddress(
          InetAddress.getByName("10.20.2.11"), 9300));
      // client.addTransportAddress(new
      // InetSocketTransportAddress(InetAddress.getByName("10.20.2.60"),
      // 9300));
    } catch (UnknownHostException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return client;
  }

  /**
   * matches all objects in an existing index
   * 
   * @param index
   * @param type
   * @return all items of the chosen index
   */
  public static ArrayList<String[]> matchall(String index, String type) {
    Client c = start();
    ArrayList<String[]> idList = new ArrayList<String[]>();
    SearchResponse scrollResponse =
        c.prepareSearch(index).setTypes(type).setSearchType(SearchType.SCAN)
            .setScroll(new TimeValue(60000))
            .setQuery(QueryBuilders.matchQuery("metadata.alternativeTitles.language", "fr"))
            .setSize(100).execute().actionGet();

    System.out.println("scroll status. " + scrollResponse.status().name());

    while (true) {
      for (SearchHit hit : scrollResponse.getHits()) {
        if (hit.field("_parent") != null) {
          System.out.println(hit.getId() + "   " + hit.getType() + "   "
              + hit.field("_parent").getValue());
        } else {
          System.out.println(hit.getId() + "   " + hit.getSource().get("creator"));
          String ouid = (String) hit.getSource().get("creator");
          String[] ids = new String[] {hit.getId(), ouid};
          idList.add(ids);
        }
      }
      scrollResponse =
          c.prepareSearchScroll(scrollResponse.getScrollId()).setScroll(new TimeValue(600000))
              .execute().actionGet();
      if (scrollResponse.getHits().getHits().length == 0) {
        break;
      }
    }
    c.close();
    return idList;
  }

  /**
   * perform a nested query
   */
  public static void nestedQuery() {
    SearchRequestBuilder srb = start().prepareSearch("pure");

    NestedQueryBuilder qb =
        QueryBuilders.nestedQuery(
            "metadata.creators",
            QueryBuilders.boolQuery().must(
                QueryBuilders.matchQuery("metadata.creators.person.givenName", "Ralf")));
    // srb.setTypes("pubitem");
    /*
     * HasChildQueryBuilder hcqb = QueryBuilders.hasChildQuery("metadata",
     * QueryBuilders.nestedQuery( "author", QueryBuilders .boolQuery()
     * .must(QueryBuilders.matchQuery("author.first", "sid"))
     * .must(QueryBuilders.matchQuery("author.last", "vicious"))));
     */
    /*
     * BoolQueryBuilder tfb = QueryBuilders.boolQuery()
     * .must(QueryBuilders.nestedQuery("metadata.creators", QueryBuilders
     * .matchQuery("person.givenName", "Peter")));
     */
    // .filter(QueryBuilders.nestedQuery("author.identifier", QueryBuilders
    // .matchQuery("author.identifier.authority", "CONE")));
    SearchResponse sr = srb.setQuery(qb).execute().actionGet();
    System.out.println(sr.getHits().totalHits());
    System.out.println(sr.toString());
  }
}
