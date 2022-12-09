package de.mpg.mpdl.inge.rest.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import de.mpg.mpdl.inge.service.spring.AppConfigPubmanLogic;

@Configuration
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.rest"})
public class PubmanRestConfiguration {
}
