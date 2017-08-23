package de.mpg.mpdl.inge.rest.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
// @PropertySource(value = "file:${catalina.home.dir}/conf/auth.properties", ignoreResourceNotFound
// = true)
@PropertySource(value = "file:${catalina.home}/conf/pubman.properties",
    ignoreResourceNotFound = true)

})
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.rest"})
@Import(value = {WebConfiguration.class, RestWebConfiguration.class, SwaggerConfiguration.class})
public class PubmanRestConfiguration {

}
