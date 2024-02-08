package de.mpg.mpdl.inge.filestorage.spring;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Spring configuration
 *
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@Configuration
@ComponentScan(basePackages = { //
    "de.mpg.mpdl.inge.filestorage", //
    "de.mpg.mpdl.inge.filestorage.filesystem", //
    "de.mpg.mpdl.inge.filestorage.seaweedfs"
})
public class FileStorageConnectorConfiguration {

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
