package de.mpg.mpdl.inge.rest.spring;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import de.mpg.mpdl.inge.model.util.MapperFactory;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"de.mpg.mpdl.inge.rest"})
public class WebConfiguration implements WebMvcConfigurer {

  @Override
  public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    // First place: A Json converter using our default Jackson object mapper
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(MapperFactory.getObjectMapper());
    converters.add(converter);

    // Second Place: For SpringDoc (otherwise Malformed (base64 encoded) api-docs)
    converters.add(new ByteArrayHttpMessageConverter());

    // Third place: A String converter which allows to return strings as json
    StringHttpMessageConverter smc = new StringHttpMessageConverter();
    smc.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
    converters.add(smc);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").exposedHeaders("Token").allowedMethods("OPTIONS", "HEAD", "GET", "POST", "PUT", "DELETE");
  }
}
