package de.mpg.mpdl.inge.es.connector;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import de.mpg.mpdl.inge.util.PropertyReader;

public class ElasticSearchTransportClientProvider implements ElasticSearchClientProvider {

  private TransportClient client;

  private final static Logger logger = LogManager.getLogger(ElasticSearchTransportClientProvider.class);

  public ElasticSearchTransportClientProvider() {
    this.client = new PreBuiltTransportClient(Settings.builder()
        .put("cluster.name", PropertyReader.getProperty("inge.es.cluster.name"))
        .put("client.transport.sniff", true)
        .build());

    logger.info("Building TransportClient for <" + PropertyReader.getProperty("inge.es.cluster.name") + ">" + " and <"
        + PropertyReader.getProperty("inge.es.transport.ips") + "> ");
    String transportIps = PropertyReader.getProperty("inge.es.transport.ips");

    for (String ip : transportIps.split(" ")) {
      String addr = ip.split(":")[0];
      int port = Integer.valueOf(ip.split(":")[1]);
      try {
        this.client.addTransportAddress(new TransportAddress(InetAddress.getByName(addr), port));

        String nodeName = this.client.nodeName();
        logger.info("Nodename <" + nodeName + ">");
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
  }

  public Client getClient() {
    return client;
  }
}
