package de.mpg.mpdl.inge.service.spring;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mpg.mpdl.inge.db.spring.JPATestConfiguration;
import de.mpg.mpdl.inge.es.connector.spring.AppConfigTest;
import de.mpg.mpdl.inge.filestorage.spring.AppConfigFileStorage;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfigPidServiceTest.class, AppConfigTest.class, JPATestConfiguration.class,
    AppConfigFileStorage.class})
@EnableTransactionManagement
@PropertySource("classpath:es_connector.properties")
public class AppConfigPubmanLogicTest {
  private final static Logger logger = LogManager.getLogger(AppConfigPubmanLogicTest.class);

  @Bean
  public PasswordEncoder passwordEncoder() {
    logger.info("Initializing Spring Bean PasswordEncoder");
    return new BCryptPasswordEncoder();
  }

}
