package de.mpg.mpdl.inge.rest.development.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({@PropertySource(value = "file:${catalina.home}/conf/pubman.properties", ignoreResourceNotFound = true)})
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.rest.development"})
public class PubmanRestConfiguration {
}
