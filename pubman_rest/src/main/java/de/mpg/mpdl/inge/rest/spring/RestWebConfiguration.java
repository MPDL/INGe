package de.mpg.mpdl.inge.rest.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
class RestWebConfiguration extends RepositoryRestConfigurerAdapter {

  @Override
  public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new JavaTimeModule());
    super.configureJacksonObjectMapper(objectMapper);
  }

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
    config.setDefaultMediaType(MediaType.APPLICATION_JSON_UTF8);
    config.setBasePath("/rest");
    config.getProjectionConfiguration().setParameterName("render");
    super.configureRepositoryRestConfiguration(config);
  }

  @Bean
  public SpelAwareProxyProjectionFactory projectionFactory() {
    return new SpelAwareProxyProjectionFactory();
  }

}
