package de.mpg.mpdl.inge.es.connector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import de.mpg.mpdl.inge.util.PropertyReader;

public class ElasticSearchTransportClientProvider implements ElasticSearchClientProvider {

  private TransportClient client;

  public ElasticSearchTransportClientProvider() {
    this.client =
        new PreBuiltTransportClient(Settings.builder()
            .put("cluster.name", PropertyReader.getProperty("inge.es.cluster.name"))
            .put("client.transport.sniff", true).build());
    String transportIps = PropertyReader.getProperty("inge.es.transport.ips");
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
  }

  public Client getClient() {
    return client;
  }
}
