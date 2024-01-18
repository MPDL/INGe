package de.mpg.mpdl.inge.service.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.mpg.mpdl.inge.service.pubman.PidService;
import de.mpg.mpdl.inge.service.pubman.impl.PidServiceImpl;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.service")
public class AppConfigPidService {

  private static final Logger logger = Logger.getLogger(AppConfigPidService.class);

  @Bean(name = "pidServiceImpl")
  public PidService pidService() {
    logger.info("Initializing PidServiceImpl");
    return new PidServiceImpl();
  }
}
