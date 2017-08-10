package de.mpg.mpdl.inge.service.es.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeValidationException;


public class ESBase {

  private static final Logger logger = Logger.getLogger(ESBase.class);

  private static Node theNode;

  private static final String TEMP_FOLDER = "./target/es/";
  private static final String CLUSTER_NAME = "myLocalCluster";
  private static final String[] indexNames = {/* "pure", */"db_users" /*
                                                                       * , "user_accounts",
                                                                       * "pure_contexts",
                                                                       * "organizational_units"
                                                                       */};



  public ESBase() {
    try {
      theNode = getNode(TEMP_FOLDER, CLUSTER_NAME);
    } catch (NodeValidationException e) {
      logger.warn("Could not initialize test node", e);
    }

    for (String indexName : indexNames) {
      try {
        createIndex(indexName, theNode.client());

        String mappingString =
            new String(FileUtils.readFileToByteArray(new File(this.getClass().getClassLoader()
                .getResource("./es_scripts/mapping_db_users.txt").toURI())));
        putMapping(indexName, theNode.client(), "users", mappingString);

      } catch (NodeValidationException | InterruptedException | ExecutionException | IOException e) {
        logger.warn("Could not create index <" + indexName + ">", e);
      } catch (URISyntaxException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public synchronized Node getNode(String tempFolder, String clusterName)
      throws NodeValidationException {
    if (theNode != null)
      return theNode;
    Settings settings =
        Settings.builder().put("path.home", tempFolder).put("cluster.name", clusterName)
            .put("transport.type", "local").put("http.enabled", false).build();

    theNode = new Node(settings).start();

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
      InterruptedException, ExecutionException, IOException {

    XContentBuilder indexSettings = XContentFactory.jsonBuilder();;

    indexSettings.startObject().startObject("index").startObject("analysis").startObject("filter")
        .startObject("autocomplete_filter").field("type", "edge_ngram").field("min_gram", "1")
        .field("max_gram", "20").endObject().endObject().startObject("analyzer")
        .startObject("autocomplete").field("type", "custom").field("tokenizer", "standard")
        .field("filter", new String[] {"autocomplete_filter", "lowercase"}).endObject().endObject()
        .endObject().endObject().endObject();
    /**
     * indexSettings .startObject() .startObject("index") .startObject("analysis")
     * .startObject("filter") .startObject("autocomplete_filter") .field("type", "edge_ngram")
     * .field("min_gram", "1") .field("max_gram", "20") .endObject() .endObject()
     * .startObject("analyzer") .startObject("autocomplete") .field("type", "custom")
     * .field("tokenizer", "standard") .field("filter", new String[] { "autocomplete_filter",
     * "lowercase" }) .endObject() .endObject() .endObject() .endObject() .endObject();
     */

    logger.info(indexSettings.string());

    ActionFuture<IndicesExistsResponse> indicesExistsResponseAction =
        client.admin().indices().exists(new IndicesExistsRequest(index));

    IndicesExistsResponse indicesExistsResponse = indicesExistsResponseAction.actionGet();
    if (!indicesExistsResponse.isExists()) {

      CreateIndexResponse createIndexResponse =
          client.admin().indices().prepareCreate(index).setSettings(indexSettings).get();

      /*
       * ActionFuture<CreateIndexResponse> createIndexResponseAction =
       * client.admin().indices().create(new CreateIndexRequest(index)); CreateIndexResponse
       * createIndexResponse = createIndexResponseAction.get();
       */
      if (!createIndexResponse.isAcknowledged()) {
        throw new IllegalStateException("Failed to create index " + index);
      }
    }
  }

  private void putMapping(String index, Client client, String type, String mappingSource)
      throws NodeValidationException {

    PutMappingResponse mappingResponse =
        client.admin().indices().preparePutMapping(index).setType(type).setSource(mappingSource)
            .get();
    if (!mappingResponse.isAcknowledged()) {
      throw new IllegalStateException("Failed to create type " + index);
    }
  }
}
