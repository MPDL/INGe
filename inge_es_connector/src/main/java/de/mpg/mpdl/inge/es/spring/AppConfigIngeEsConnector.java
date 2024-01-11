package de.mpg.mpdl.inge.es.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientProvider;
import jakarta.annotation.PreDestroy;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.es")
public class AppConfigIngeEsConnector {
  private static final Logger logger = Logger.getLogger(AppConfigIngeEsConnector.class);

  @Bean
  public ElasticSearchClientProvider elasticSearchClientProvider() {
    logger.info("Initializing Spring Bean ElasticSearchTransportClientProvider");
    return new ElasticSearchTransportClientProvider();
  }


  @PreDestroy
  public void closeClient() {

    //elasticSearchClientProvider().getClient().close();
  }
}
