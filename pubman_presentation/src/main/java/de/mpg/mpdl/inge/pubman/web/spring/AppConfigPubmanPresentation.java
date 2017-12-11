package de.mpg.mpdl.inge.pubman.web.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("de.mpg.mpdl.inge.pubman.web")
public class AppConfigPubmanPresentation {
}
