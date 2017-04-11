package de.mpg.mpdl.inge.pubman.web.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@Configuration
@ComponentScan("de.mpg.mpdl.inge.pubman.web")
@Import({de.mpg.mpdl.inge.es.spring.AppConfig.class, AppConfigPubmanLogic.class})
public class AppConfigPubmanPresentation {

}
