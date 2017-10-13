package de.mpg.mpdl.inge.es.connector;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ElasticSearchLocalClientProvider implements ElasticSearchClientProvider {

  private static final Logger logger = Logger.getLogger(ElasticSearchLocalClientProvider.class);

  private static Node theNode;

  private static final String TEMP_FOLDER = "./target/es/";
  private static final String CLUSTER_NAME = "myLocalCluster";
  private static final String[] indexNames = {/* "pure", */"db_users", "db_contexts", "db_ous"};

  public Client getClient() {
    init();
    return theNode.client();
  }

  private void init() {

    try {
      theNode = getNode(TEMP_FOLDER, CLUSTER_NAME);
    } catch (NodeValidationException e) {
      logger.warn("Could not initialize test node", e);
    }

    for (String indexName : indexNames) {
      try {
        createIndex(indexName, theNode.client());

      } catch (NodeValidationException | InterruptedException | ExecutionException | IOException
          | URISyntaxException e) {
        logger.warn("Could not create index <" + indexName + ">", e);
      }

      doBulkImport(indexName, theNode.client());
    }
  }

  private Node getNode(String tempFolder, String clusterName) throws NodeValidationException {
    logger.info("theNode <" + theNode + ">");

    if (theNode != null)
      return theNode;
    Settings settings =
        Settings.builder().put("path.home", tempFolder).put("cluster.name", clusterName)
            .put("transport.type", "local").put("http.enabled", false).build();

    theNode = new Node(settings).start();

    logger.info("Created successfully theNode <" + theNode + ">");

    return theNode;
  }

  public void clear() {
    try {
      theNode.close();
    } catch (IOException e) {
      logger.warn("Could not close test node", e);
    } finally {
      try {
        FileUtils.deleteDirectory(new File(TEMP_FOLDER));
      } catch (IOException e) {
        logger.warn("Could not delete test folder <" + TEMP_FOLDER + ">", e);
      }
    }
  }

  private void createIndex(String index, Client client) throws NodeValidationException,
      InterruptedException, ExecutionException, IOException, URISyntaxException {

    XContentBuilder indexSettings = XContentFactory.jsonBuilder();;

    indexSettings.startObject().startObject("index").startObject("analysis").startObject("filter")
        .startObject("autocomplete_filter").field("type", "edge_ngram").field("min_gram", "1")
        .field("max_gram", "20").endObject().endObject().startObject("analyzer")
        .startObject("autocomplete").field("type", "custom").field("tokenizer", "standard")
        .field("filter", new String[] {"autocomplete_filter", "lowercase"}).endObject().endObject()
        .endObject().endObject().endObject();

    logger.info(indexSettings.string());

    ActionFuture<IndicesExistsResponse> indicesExistsResponseAction =
        client.admin().indices().exists(new IndicesExistsRequest(index));

    IndicesExistsResponse indicesExistsResponse = indicesExistsResponseAction.actionGet();

    if (!indicesExistsResponse.isExists()) {

      CreateIndexRequestBuilder createIndexRequestBuilder =
          client.admin().indices().prepareCreate(index).setSettings(indexSettings);

      StringBuffer mappingNameStringBuffer = new StringBuffer("./es_scripts/mapping_");
      mappingNameStringBuffer.append(index).append(".txt");
      String mappingString =
          new String(FileUtils.readFileToByteArray(new File(this.getClass().getClassLoader()
              .getResource(mappingNameStringBuffer.toString()).toURI())));



      CreateIndexResponse createIndexResponse =
          createIndexRequestBuilder.addMapping(getType(index), mappingString).execute().actionGet();

      if (!createIndexResponse.isAcknowledged()) {
        throw new IllegalStateException("Failed to create index " + index);
      }
      logger.info("Index <" + index + "> created successfully");
    }
  }

  private void doBulkImport(String indexName, Client client) {

    BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
    List<String> lines = null;
    JSONParser parser = new JSONParser();

    try {
      StringBuffer importFileStringBuffer =
          new StringBuffer().append("./es_scripts/import_").append(indexName).append(".txt");
      lines =
          FileUtils.readLines(new File(this.getClass().getClassLoader()
              .getResource(importFileStringBuffer.toString()).toURI()));
    } catch (IOException | URISyntaxException e) {
      logger.warn("Error occured when reading bulk import file", e);
    }

    String _id = null;
    for (String line : lines) {

      if (line.startsWith("{\"_id")) {
        try {
          JSONObject jobj = (JSONObject) parser.parse(line);
          _id = String.valueOf(jobj.get("_id"));
          if (_id == null)
            continue;
        } catch (ParseException e) {
          logger.warn("Error when parsing line <" + line + ">", e);
          continue;
        }
      } else {
        bulkRequestBuilder.add(client.prepareIndex(indexName, getType(indexName), _id).setSource(
            line));
      }
    }
    BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();

    if (bulkResponse.hasFailures()) {
      logger.warn(bulkResponse.buildFailureMessage());
    } else {
      logger.info("Imported successfully <" + bulkResponse.getItems().length + "> objects");
      for (int i = 0; i < bulkResponse.getItems().length; i++) {
        logger.info("Imported item id <" + bulkResponse.getItems()[i].getItemId() + ">");
        logger.info("Imported id <" + bulkResponse.getItems()[i].getId() + ">");
      }


      SearchResponse response =
          client.prepareSearch(indexName).setTypes(getType(indexName)).setSize(100).execute()
              .actionGet();

      SearchHit[] results = response.getHits().getHits();

      for (SearchHit hit : results) {

        Map<String, Object> map = hit.getSource();

        for (String key : map.keySet()) {
          logger.info("key <" + key + "> value <" + map.get(key).toString() + ">");

        }
        logger.info("*****************************");
      }
    }
  }

  private String getType(String index) {
    switch (index) {
      case "db_users":
        return "user";
      case "db_contexts":
        return "context";
      case "db_ous":
        return "organization";
      default:
        return "";
    }
  }
}
