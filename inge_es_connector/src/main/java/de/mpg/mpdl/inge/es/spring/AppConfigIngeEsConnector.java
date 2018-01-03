package de.mpg.mpdl.inge.es.spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.mpg.mpdl.inge.es.connector.ElasticSearchClientProvider;
import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientProvider;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.es")
public class AppConfigIngeEsConnector {
  private final static Logger logger = LogManager.getLogger(AppConfigIngeEsConnector.class);

  @Bean
  public ElasticSearchClientProvider elasticSearchClientProvider() {
    logger.info("Initializing Spring Bean ElasticSearchTransportClientProvider");
    return new ElasticSearchTransportClientProvider();
  }
}
