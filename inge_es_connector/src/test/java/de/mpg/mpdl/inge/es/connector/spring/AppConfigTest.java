package de.mpg.mpdl.inge.es.connector.spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.mpg.mpdl.inge.es.connector.ElasticSearchLocalClientProvider;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.es")
public class AppConfigTest {
  private final static Logger logger = LogManager.getLogger(AppConfigTest.class);

  @Bean
  @Primary
  public ElasticSearchLocalClientProvider elasticSearchClientProvider() {
    logger.info("Initializing Spring Bean ElasticSearchLocalClientProvider");
    return new ElasticSearchLocalClientProvider();
  }
}
