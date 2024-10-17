package de.mpg.mpdl.inge.dataacquisition.spring;

import de.mpg.mpdl.inge.dataacquisition.DataHandlerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.dataacquisition")
public class AppConfigDataacquisition {

  @Bean
  public DataHandlerService dataHandlerService() {
    return new DataHandlerService();
  }
}
