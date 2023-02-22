package de.mpg.mpdl.inge.service.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.mpg.mpdl.inge.db.spring.JPAConfiguration;
import de.mpg.mpdl.inge.filestorage.spring.AppConfigFileStorage;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
@Import({AppConfigPidServiceTest.class, JPAConfiguration.class, AppConfigFileStorage.class})
@EnableTransactionManagement
public class AppConfigPubmanLogicTest {
  private static final Logger logger = Logger.getLogger(AppConfigPubmanLogicTest.class);

  @Bean
  public PasswordEncoder passwordEncoder() {
    logger.info("Initializing Spring Bean PasswordEncoder");
    return new BCryptPasswordEncoder();
  }

}
