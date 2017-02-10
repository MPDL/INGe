/**
 * 
 */
package de.mpg.mpdl.inge.es;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author frank
 * 
 */
@Configuration
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.es.connector", "de.mpg.mpdl.inge.es.service"})
@PropertySources({@PropertySource("classpath:es_connector.properties"),
    @PropertySource(value = "${pg.conf.file}", ignoreResourceNotFound = true)})
public class IngeESConnectorConfiguration {

  @Value("${es_cluster_name}")
  String clusterName;

  @Value("${es_transport_ips}")
  String transportIps;

  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfig() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public Client client() {

    Settings settings =
        Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
            .build();
    TransportClient client = new PreBuiltTransportClient(settings);
    for (String ip : transportIps.split(" ")) {
      String addr = ip.split(":")[0];
      int port = Integer.valueOf(ip.split(":")[1]);
      try {
        client
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(addr), port));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
    return client;
  }

  @Bean
  public ObjectMapper mapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return mapper;
  }

}
