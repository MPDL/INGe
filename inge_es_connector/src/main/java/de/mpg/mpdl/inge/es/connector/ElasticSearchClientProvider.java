package de.mpg.mpdl.inge.es.connector;

import org.elasticsearch.client.Client;


public interface ElasticSearchClientProvider {

  Client getClient();

}
