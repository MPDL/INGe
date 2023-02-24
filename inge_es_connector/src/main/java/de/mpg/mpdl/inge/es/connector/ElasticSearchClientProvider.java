package de.mpg.mpdl.inge.es.connector;

import co.elastic.clients.elasticsearch.ElasticsearchClient;


public interface ElasticSearchClientProvider {

  ElasticsearchClient getClient();

}
