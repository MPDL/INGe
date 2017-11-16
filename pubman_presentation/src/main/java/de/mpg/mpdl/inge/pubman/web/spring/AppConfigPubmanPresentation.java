package de.mpg.mpdl.inge.pubman.web.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.mpg.mpdl.inge.inge_validation.spring.AppConfigIngeValidation;

@Configuration
@EnableScheduling
@ComponentScan("de.mpg.mpdl.inge.pubman.web")
@Import({AppConfigIngeValidation.class})
public class AppConfigPubmanPresentation {
}
