package de.mpg.mpdl.inge.es.spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.mpg.mpdl.inge.es.connector.ElasticSearchTransportClientProvider;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.es")
public class AppConfig {
  private final static Logger logger = LogManager.getLogger(AppConfig.class);

  @Bean
  @Primary
  public ElasticSearchTransportClientProvider elasticSearchClientProvider() {
    logger.info("Initializing Spring Bean ElasticSearchTransportClientProvider");
    return new ElasticSearchTransportClientProvider();
  }
}
