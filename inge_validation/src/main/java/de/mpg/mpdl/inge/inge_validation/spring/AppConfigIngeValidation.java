package de.mpg.mpdl.inge.inge_validation.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import de.mpg.mpdl.inge.inge_validation.ItemValidatingService;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.inge_validation")
public class AppConfigIngeValidation {

  @Bean
  public ItemValidatingService itemValidatingService() {
    return new ItemValidatingService();
  }
}
