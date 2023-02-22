package de.mpg.mpdl.inge.service.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.service.pubman.impl.PidServiceMock;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
public class AppConfigPidServiceTest {
  private static final Logger logger = Logger.getLogger(AppConfigPidServiceTest.class);

  @Bean(name = "pidServiceMock")
  @Primary
  public PidService pidService() {
    logger.info("Initializing PidServiceMock");
    return new PidServiceMock();
  }

}
