package de.mpg.mpdl.inge.es.connector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Repository;

import de.mpg.mpdl.inge.util.PropertyReader;

@Repository
public class ElasticSearchTransportClientProvider {

  private TransportClient client;

  public ElasticSearchTransportClientProvider() {
    this.client =
        new PreBuiltTransportClient(Settings.builder()
            .put("cluster.name", PropertyReader.getProperty("es_cluster_name"))
            .put("client.transport.sniff", true).build());
    String transportIps = PropertyReader.getProperty("es_transport_ips");
    for (String ip : transportIps.split(" ")) {
      String addr = ip.split(":")[0];
      int port = Integer.valueOf(ip.split(":")[1]);
      try {
        this.client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(addr),
            port));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }

    // super(settings);
  }

  public TransportClient getClient() {
    return client;
  }

  public void setClient(TransportClient client) {
    this.client = client;
  }



}
