package de.mpg.mpdl.inge.rest.spring;

import de.mpg.mpdl.inge.model.util.MapperFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Arrays;
import java.util.List;

@Configuration
public class PubmanRestConfiguration implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
    //config.setBasePath("/api");
    config.setRepositoryDetectionStrategy(RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED);
  }

  /*
  @Override
  public void configureHttpMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
  
    //First place: A Json converter using our default Jackson object mapper
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(MapperFactory.getObjectMapper());
    messageConverters.add(0, converter);
  
    //Second place: A String converter which allows to return strings as json
    StringHttpMessageConverter smc = new StringHttpMessageConverter();
    smc.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
    messageConverters.add(0, smc);
    //    System.out.println("Converters" + converters);
  
  }
  
   */
}
