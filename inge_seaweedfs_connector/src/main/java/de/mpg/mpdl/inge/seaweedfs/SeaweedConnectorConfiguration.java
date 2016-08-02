package de.mpg.mpdl.inge.seaweedfs;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring configuration for the seaweed service module
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
@Configuration
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.seaweedfs"})
@PropertySource("classpath:seaweed.properties")
public class SeaweedConnectorConfiguration {

  private final CloseableHttpClient httpClient = HttpClients.createDefault();
  
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertyConfig() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public CloseableHttpClient httpClient() {
    return httpClient;
  }

}
